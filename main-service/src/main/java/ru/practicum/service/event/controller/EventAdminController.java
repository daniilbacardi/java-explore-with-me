package ru.practicum.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.CommonConstants;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
@ResponseStatus(HttpStatus.OK)
@Slf4j
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public Set<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                            @RequestParam(required = false) List<EventState> states,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) @DateTimeFormat(
                                                     pattern = CommonConstants.DATE_FORMAT) LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(
                                                     pattern = CommonConstants.DATE_FORMAT) LocalDateTime rangeEnd,
                                            @RequestParam(required = false, defaultValue = "0")
                                                @PositiveOrZero Integer from,
                                            @RequestParam(required = false, defaultValue = "10")
                                                 @Positive Integer size) {
        log.info("EventAdminController: getEventsAdmin выполнено users {}, states {}, " +
                "categories {}, rangeStart {}, rangeEnd {}, from {}, size {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable("eventId") Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("EventAdminController: updateEventAdmin выполнено eventId {}, " +
                "updateEventAdminRequest {}", eventId, updateEventAdminRequest);
        return eventService.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}
