package ru.practicum.service.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Validated
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable("userId") Long userId,
                                              @RequestParam("eventId") Long eventId) {
        log.info("RequestController: addRequest выполнено userId - {}, eventId - {}", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable("userId") Long userId) {
        log.info("RequestController: getRequests выполнено {}", userId);
        return requestService.getRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelEventRequest(@PathVariable("userId") Long userId,
                                                      @PathVariable("requestId") Long requestId) {
        log.info("RequestController: cancelEventRequest выполнено " +
                "requestId - {}, userId - {}", userId, requestId);
        return requestService.cancelEventRequest(userId, requestId);
    }
}
