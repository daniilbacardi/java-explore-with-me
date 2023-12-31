package ru.practicum.service.stats;

import ru.practicum.dto.model.ViewStats;
import ru.practicum.service.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StatsService {
    void addHit(HttpServletRequest request);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    Map<Long, Long> getViews(Set<Event> events);

    Map<Long, Long> getConfirmedRequests(Set<Event> events);
}
