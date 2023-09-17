package ru.practicum.service.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.category.service.CategoryService;
import ru.practicum.service.event.dto.*;
import ru.practicum.service.event.enums.EventSortType;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.enums.StateAdminAction;
import ru.practicum.service.event.enums.StateUserAction;
import ru.practicum.service.event.mapper.EventMapper;
import ru.practicum.service.event.mapper.LocationMapper;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.Location;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.event.repository.LocationRepository;
import ru.practicum.service.exception.ConflictException;
import ru.practicum.service.exception.DatesException;
import ru.practicum.service.exception.EntityNotFoundException;
import ru.practicum.service.stats.StatsService;
import ru.practicum.service.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EventServiceImpl implements EventService {
    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final UserService userService;
    final CategoryService categoryService;
    final LocationMapper locationMapper;
    final LocationRepository locationRepository;
    final StatsService statsService;

    @Override
    public EventFullDto getEventByIdPublic(Long id, HttpServletRequest httpServletRequest) {
        log.info("EventServiceImpl: getEventByIdPublic вызван");
        Event event = getEvent(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EntityNotFoundException("Событие не найдено");
        }
        statsService.addHit(httpServletRequest);
        Set<Event> eventSet = new HashSet<>();
        eventSet.add(event);
        Set<EventFullDto> list = getEventWithViewsAndRequests(eventSet);
        EventFullDto eventFullDto = list.iterator().next();
        log.info("EventServiceImpl: getEventByIdPublic выполнено id = {}", id);
        return eventFullDto;
    }

    @Override
    public Set<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Boolean onlyAvailable, EventSortType sort, Integer from,
                                              Integer size, HttpServletRequest httpServletRequest) {
        log.info("EventServiceImpl: getEventsPublic вызван");
        validateTime(rangeStart, rangeEnd);
        Set<Event> events = eventRepository.findEventsByPublic(text, categories, paid,
                rangeStart, rangeEnd, from, size);
        if (events.isEmpty()) {
            return Collections.emptySet();
        }
        Map<Long, Long> partLimit = new HashMap<>();
        events.forEach(event -> partLimit.put(event.getId(), event.getParticipantLimit()));
        Set<EventShortDto> eventsShortDtoList = getEventShortWithViewsAndRequests(events);
        if (onlyAvailable) {
            eventsShortDtoList = eventsShortDtoList.stream()
                    .filter(eventShortDto -> (partLimit.get(eventShortDto.getId()) == 0 ||
                            partLimit.get(eventShortDto.getId()) > eventShortDto.getConfirmedRequests()))
                    .collect(Collectors.toSet());
        }
        if (sort != null) {
            List<EventShortDto> sortedList = new ArrayList<>(eventsShortDtoList);

            if (sort.equals(EventSortType.VIEWS)) {
                sortedList.sort(Comparator.comparing(EventShortDto::getViews));
            } else if (sort.equals(EventSortType.EVENT_DATE)) {
                sortedList.sort(Comparator.comparing(EventShortDto::getEventDate));
            }
            eventsShortDtoList = new LinkedHashSet<>(sortedList);
        }
        statsService.addHit(httpServletRequest);
        log.info("EventServiceImpl: getEventsPublic выполнено");
        return eventsShortDtoList;
    }

    @Override
    public Set<EventShortDto> getEventsPrivate(Long userId, int from, int size) {
        log.info("EventServiceImpl: getEventsPrivate вызван");
        userService.findUserById(userId);
        Pageable page = PageRequest.of(from / size, size);
        Set<Event> events = eventRepository.findAllByInitiatorId(userId, page).stream().collect(Collectors.toSet());
        log.info("EventServiceImpl: getEventsPrivate выполнено userId = {}", userId);
        return getEventShortWithViewsAndRequests(events);
    }

    @Override
    @Transactional
    public EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto) {
        log.info("EventServiceImpl: addEventPrivate вызван");
        validateDate(newEventDto.getEventDate());
        Event newEvent = eventMapper.fromDtoToModel(newEventDto, userService.findUserById(userId),
                categoryService.getCategoryById(newEventDto.getCategory()), getLocation(newEventDto.getLocation()));
        EventFullDto savedEvent = eventMapper.fromModelToFullDto(
                eventRepository.save(newEvent), 0L, 0L);
        log.info("EventServiceImpl: addEventPrivate выполнено savedEvent - {}", savedEvent);
        return savedEvent;
    }

    @Override
    public EventFullDto getEventPrivate(Long userId, Long eventId) {
        log.info("EventServiceImpl: getEventPrivate вызван");
        Event event = getEventByInitiator(eventId, userId);
        Set<EventFullDto> eventFullDtoSet = getEventWithViewsAndRequests(Set.of(event));
        log.info("EventServiceImpl: getEventPrivate выполнено eventId - {}", eventId);
            return eventFullDtoSet.iterator().next();
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("EventServiceImpl: updateEventPrivate вызван");
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate()
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DatesException("Дата события должна быть не менее чем на два часа позже текущего момента.");
        }
        Event event = getEventByInitiator(eventId, userId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Состояние события не может быть изменено");
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateUserAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else {
                event.setState(EventState.PENDING);
            }
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(getLocation(updateEventUserRequest.getLocation()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        eventRepository.save(event);
        Set<EventFullDto> eventFullDtoSet = getEventWithViewsAndRequests(Set.of(event));
        log.info("EventServiceImpl: getEventPrivate выполнено {}", event);
        return eventFullDtoSet.iterator().next();
    }

    @Override
    public Set<EventFullDto> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Integer from, Integer size) {
        log.info("EventServiceImpl: getEventsAdmin вызван");
        validateTime(rangeStart, rangeEnd);
        Set<Event> events = eventRepository.findEventsByAdmin(users, states, categories,
                rangeStart, rangeEnd, from, size);
        Set<EventFullDto> fullEvents = getEventWithViewsAndRequests(events);
        log.info("EventServiceImpl: getEventsAdmin выполнено {}", fullEvents);
        return fullEvents;
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("EventServiceImpl: updateEventAdmin вызван");
        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate()
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DatesException("Дата события должна быть не менее чем на два часа позже текущего момента.");
        }
        Event event = getEvent(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Состояние события не может быть изменено");
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventAdminRequest.getCategory()));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(getLocation(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateAdminAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventAdminRequest.getStateAction().equals(StateAdminAction.REJECT_EVENT)) {
                event.setState(EventState.REJECTED);
            }
        }
        eventRepository.save(event);
        EventFullDto updatedEvent = getEventWithViewsAndRequests(Collections.singleton(getEvent(eventId)))
                .iterator().next();
        log.info("EventServiceImpl: updateEventAdmin выполнено {}", updatedEvent);
        return updatedEvent;
    }

    @Override
    public Set<EventShortDto> getEventShortWithViewsAndRequests(Set<Event> events) {
        log.info("EventServiceImpl: getEventShortWithViewsAndRequests вызван");
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);
        log.info("EventServiceImpl: getEventShortWithViewsAndRequests выполнено {}", events);
        return events.stream().map(event -> eventMapper.fromModelToShortDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Event> getEventsByIdsList(Set<Long> ids) {
        log.info("EventServiceImpl: getEventsByIdsList выполнено {}", ids);
        return eventRepository.findAllByIdIn(ids);
    }

    @Override
    public Event getEvent(Long eventId) {
        log.info("EventServiceImpl: getEvent выполнено {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> {
            throw new EntityNotFoundException("Событие не найдено");
        });
    }

    private void validateDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DatesException("Дата события должна быть не менее чем на два часа позже текущего момента.");
        }
    }

    private void validateTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new DatesException("Установлен неправильный временной интервал");
        }
    }

    private Event getEventByInitiator(Long eventId, Long userId) {
        userService.findUserById(userId);
        log.info("EventServiceImpl: getEventByInitiator выполнено eventId - {}, userId - {}", eventId, userId);
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            throw new EntityNotFoundException("Событие не найдено");
        });
    }

    private Location getLocation(LocationDto locationDto) {
        Location location = locationMapper.fromDtoToModel(locationDto);
        log.info("EventServiceImpl: getLocation выполнено Lat - {}, Lon - {}",
                location.getLat(), location.getLon());
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }

    private Set<EventFullDto> getEventWithViewsAndRequests(Set<Event> events) {
        log.info("EventServiceImpl: getEventWithViewsAndRequests вызван");
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);
        log.info("EventServiceImpl: getEventShortWithViewsAndRequests выполнено {}", events);
        return events.stream().map(event -> eventMapper.fromModelToFullDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toSet());
    }
}
