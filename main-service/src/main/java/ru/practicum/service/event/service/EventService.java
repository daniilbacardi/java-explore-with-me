package ru.practicum.service.event.service;

import ru.practicum.service.event.dto.*;
import ru.practicum.service.event.enums.EventSortType;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    EventFullDto getEventByIdPublic(Long id, HttpServletRequest httpServletRequest);

    Set<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                                       Integer from, Integer size, HttpServletRequest httpServletRequest);

    Set<EventShortDto> getEventsPrivate(Long userId, int from, int size);

    EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);


    Set<EventFullDto> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Set<EventShortDto> getEventShortWithViewsAndRequests(Set<Event> events);

    Set<Event> getEventsByIdsList(Set<Long> ids);

    Event getEvent(Long eventId);
}
