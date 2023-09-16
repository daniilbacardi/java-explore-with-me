package ru.practicum.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.CommonConstants;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.enums.EventSortType;
import ru.practicum.service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@ResponseStatus(HttpStatus.OK)
@Slf4j
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsPublic(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @DateTimeFormat(
                                                       pattern = CommonConstants.DATE_FORMAT) LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(
                                                       pattern = CommonConstants.DATE_FORMAT) LocalDateTime rangeEnd,
                                               @RequestParam(required = false,
                                                       defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(required = false) EventSortType sort,
                                               @RequestParam(required = false,
                                                       defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(required = false,
                                                       defaultValue = "10") @Positive Integer size,
                                               HttpServletRequest httpServletRequest) {
        log.info("EventPublicController: getEventsPublic выполнено text {}, categories {}, paid {}, " +
                "rangeStart {}, rangeEnd {}, onlyAvailable {}, sort {}, from {}, size {}, " +
                "httpServletRequest {}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, httpServletRequest);
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, httpServletRequest);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByIdPublic(@PathVariable("id") Long id,
                                           HttpServletRequest httpServletRequest) {
        log.info("EventPublicController: getEventByIdPublic выполнено id {}, " +
                "httpServletRequest {}", id, httpServletRequest);
        return eventService.getEventByIdPublic(id, httpServletRequest);
    }
}
