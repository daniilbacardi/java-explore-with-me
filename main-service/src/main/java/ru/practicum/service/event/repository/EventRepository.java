package ru.practicum.service.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.service.event.model.Event;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventCustomRepository {
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Page<Event> findAllByInitiatorId(Long userId, Pageable page);

    Set<Event> findAllByIdIn(Set<Long> eventsId);
}
