package ru.practicum.service.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.model.ViewStats;
import ru.practicum.service.CommonConstants;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.request.repository.RequestRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void addHit(HttpServletRequest httpServletRequest) {
        log.info("StatsServiceImpl addHit вызван");

        statsClient.addHit("main-service",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(CommonConstants.FORMATTER),
                        CommonConstants.FORMATTER));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("StatsServiceImpl getStats вызван");
        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, unique);
        try {
            return Arrays.asList(objectMapper.readValue(
                    objectMapper.writeValueAsString(response.getBody()), ViewStats[].class));
        } catch (IOException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public Map<Long, Long> getViews(Set<Event> events) {
        log.info("StatsServiceImpl getViews вызван");
        Map<Long, Long> views = new HashMap<>();
        if (events.isEmpty()) {
            return views;
        }
        Set<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null).collect(Collectors.toSet());
        Optional<LocalDateTime> timePublished = publishedEvents.stream().map(Event::getPublishedOn)
                .filter(Objects::nonNull).min(LocalDateTime::compareTo);
        if (timePublished.isPresent()) {
            LocalDateTime start = timePublished.get();
            List<String> uris = publishedEvents.stream().map(Event::getId)
                    .map(id -> ("/events/" + id)).collect(Collectors.toList());
            List<ViewStats> stats = getStats(start, LocalDateTime.now(), uris, true);
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri().split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
            });
        }
        log.info("StatsServiceImpl getViews выполнено {}", views.size());
        return views;
    }

    @Override
    public Map<Long, Long> getConfirmedRequests(Set<Event> events) {
        log.info("StatsServiceImpl getConfirmedRequests вызван");
        List<Long> eventsId = events.stream().filter(event -> event.getPublishedOn() != null)
                .map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> requests = new HashMap<>();
        if (!eventsId.isEmpty()) {
            requestRepository.getAllEventIdsConfirmed(eventsId).forEach(eventWithRequests ->
                    requests.put(eventWithRequests.getEventId(), eventWithRequests.getConfirmedRequests()));
        }
        log.info("StatsServiceImpl getConfirmedRequests выполнено {}", requests);
        return requests;
    }
}
