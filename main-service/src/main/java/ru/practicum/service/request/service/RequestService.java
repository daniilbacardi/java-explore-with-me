package ru.practicum.service.request.service;

import ru.practicum.service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto cancelEventRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateUserEventRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
