package ru.practicum.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.dto.*;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
@ResponseStatus(HttpStatus.OK)
@Slf4j
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventsPrivate(@PathVariable("userId") Long userId,
                                                @RequestParam(name = "from",  defaultValue = "0") int from,
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("EventPrivateController: getEventsPrivate выполнено userId {}, from {}, " +
                "size {}", userId, from, size);
        return eventService.getEventsPrivate(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEventPrivate(@PathVariable("userId") Long userId,
                                        @Valid @RequestBody NewEventDto newEventDto) {
        log.info("EventPrivateController: addEventPrivate выполнено userId {}, newEventDto {}", userId, newEventDto);
        return eventService.addEventPrivate(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventPrivate(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId) {
        log.info("EventPrivateController: getEventPrivate выполнено userId {}, eventId {}", userId, eventId);
        return eventService.getEventPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventPrivate(@PathVariable("userId") Long userId,
                                           @PathVariable("eventId") Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("EventPrivateController: getEventPrivate выполнено userId {}, " +
                "eventId {}, updateEventUserRequest {}", userId, eventId, updateEventUserRequest);
        return eventService.updateEventPrivate(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable("userId") Long userId,
                                                              @PathVariable("eventId") Long eventId) {
        log.info("EventPrivateController: getUserEventRequests выполнено userId {}, eventId {}", userId, eventId);
        return requestService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequestStatus(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("EventPrivateController: updateUserEventRequestStatus выполнено " +
                "userId {}, eventId {}, eventRequestStatusUpdateRequest {}",
                userId, eventId, eventRequestStatusUpdateRequest);
        return requestService.updateUserEventRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
