package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommonConstants;
import ru.practicum.dto.model.EndpointHit;
import ru.practicum.dto.model.ViewStats;
import ru.practicum.server.exception.DateException;
import ru.practicum.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody @Valid EndpointHit endpointHit) {
        statsService.addHit(endpointHit);
        log.info("StatsController: addHit выполнено {}", endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam (value = "start")
                                        @DateTimeFormat(pattern = CommonConstants.DATE_FORMAT) LocalDateTime start,
                                    @RequestParam (value = "end")
                                    @DateTimeFormat(pattern = CommonConstants.DATE_FORMAT) LocalDateTime end,
                                    @RequestParam (value = "uris", required = false) List<String> uris,
                                    @RequestParam (value = "unique",required = false,
                                            defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new DateException("Установлены неправильные даты");
        }
        log.info("StatsController: getStats выполнено start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
