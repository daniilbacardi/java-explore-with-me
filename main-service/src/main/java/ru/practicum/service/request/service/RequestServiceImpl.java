package ru.practicum.service.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.exception.ConflictException;
import ru.practicum.service.exception.EntityNotFoundException;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.dto.RequestStatus;
import ru.practicum.service.request.dto.RequestStatusAction;
import ru.practicum.service.request.mapper.RequestMapper;
import ru.practicum.service.request.model.Request;
import ru.practicum.service.request.repository.RequestRepository;
import ru.practicum.service.stats.StatsService;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final UserService userService;
    final EventService eventService;
    final RequestMapper requestMapper;
    final StatsService statsService;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info("RequestServiceImpl: addRequest вызван");
        User user = userService.findUserById(userId);
        Event event = eventService.getEvent(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Невозможно добавить запрос на событие");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие еще не опубликовано");
        }
        Optional<Request> requests = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (requests.isPresent()) {
            throw new ConflictException("Запрос уже существует");
        }
        if (event.getParticipantLimit() != 0 &&
                (statsService.getConfirmedRequests(List.of(event))
                        .getOrDefault(eventId, 0L) + 1) > event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников исчерпан");
        }
        Request request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        ParticipationRequestDto savedRequest = requestMapper.fromModelToDto(requestRepository.save(request));
        log.info("RequestServiceImpl: addRequest выполнено {}", savedRequest);
        return savedRequest;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        log.info("RequestServiceImpl: getRequests вызван");
        userService.findUserById(userId);
        log.info("RequestServiceImpl: getRequests выполнено {}", userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::fromModelToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequest(Long userId, Long requestId) {
        log.info("RequestServiceImpl: cancelEventRequest вызван");
        userService.findUserById(userId);
        Request request = findRequest(requestId);
        if (!userId.equals(request.getRequester().getId())) {
            throw new ConflictException("Пользователь не делал запрос");
        }
        request.setStatus(RequestStatus.CANCELED);
        log.info("RequestServiceImpl: cancelEventRequest выполнено " +
                "requestId - {}, userId - {}", userId, requestId);
        return requestMapper.fromModelToDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        log.info("RequestServiceImpl: cancelEventRequest вызван");
        Event event = eventService.getEvent(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициировал событие");
        }
        log.info("RequestServiceImpl: getUserEventRequests выполнено " +
                "eventId - {}, userId - {}", eventId, userId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::fromModelToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateUserEventRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("RequestServiceImpl: updateUserEventRequestStatus вызван eventId = {}", eventId);
        userService.findUserById(userId);
        Event event = eventService.getEvent(eventId);
        validateUserIsOwner(userId, event);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0 ||
                eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }
        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        if (!requests.stream().map(Request::getStatus).allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("Запрос не может быть изменен");
        }
        Long limit = event.getParticipantLimit() - statsService.getConfirmedRequests(List.of(event))
                .getOrDefault(eventId, 0L);
        validateLimit(limit, event.getParticipantLimit());
        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusAction.REJECTED)) {
            rejected.addAll(changeStatusAndSave(requests, RequestStatus.REJECTED));
        } else {
            confirmedList.addAll(changeStatusAndSave(requests, RequestStatus.CONFIRMED));
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                confirmedList.stream().map(requestMapper::fromModelToDto).collect(Collectors.toList()),
                rejected.stream().map(requestMapper::fromModelToDto).collect(Collectors.toList()));
        log.info("RequestServiceImpl: updateUserEventRequestStatus выполнено eventId = {}", eventId);
        return result;
    }

    private void validateUserIsOwner(Long userId, Event event) {
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициировал событие");
        }
    }

    private void validateLimit(Long limit, Long eventParticipantLimit) {
        if (eventParticipantLimit != 0 && limit <= 0) {
            throw new ConflictException("Лимит участников исчерпан");
        }
    }

    private List<Request> changeStatusAndSave(List<Request> requests, RequestStatus status) {
        requests.forEach(request -> request.setStatus(status));
        return requestRepository.saveAll(requests);
    }

    private Request findRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
    }
}
