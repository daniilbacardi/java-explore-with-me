package ru.practicum.service.event.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventCustomRepository {
    List<Event> findEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, Integer from, Integer size);

    List<Event> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
