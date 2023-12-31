package ru.practicum.server.service;

import ru.practicum.dto.model.EndpointHit;
import ru.practicum.dto.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void addHit(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
