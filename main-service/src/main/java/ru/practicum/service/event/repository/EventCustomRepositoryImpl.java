package ru.practicum.service.event.repository;

import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventCustomRepositoryImpl implements EventCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Set<Event> findEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        Predicate predicate = criteriaBuilder.conjunction();
        if (text != null && !text.isBlank()) {
            Predicate annotation = criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate description = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + text.toLowerCase() + "%");
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(annotation, description));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("category").in(categories));
        }
        if (paid != null) {
            predicate = criteriaBuilder.and(predicate, root.get("paid").in(paid));
        }
        if (rangeStart != null && rangeEnd != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                    LocalDateTime.now()));
        } else {
            if (rangeStart != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                        rangeStart));
            }
            if (rangeEnd != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"),
                        rangeEnd));
            }
        }
        predicate = criteriaBuilder.and(predicate, root.get("state").in(EventState.PUBLISHED));
        criteriaQuery.select(root).where(predicate);
        List<Event> eventsList = entityManager.createQuery(criteriaQuery).setFirstResult(from)
                .setMaxResults(size).getResultList();
        return new HashSet<>(eventsList);
    }

    public Set<Event> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                        Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        Predicate predicate = criteriaBuilder.conjunction();
        if (users != null && !users.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("initiator").in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("state").in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("category").in(categories));
        }
        if (rangeStart != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                    rangeStart));
        }
        if (rangeEnd != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"),
                    rangeEnd));
        }
        criteriaQuery.select(root).where(predicate);
        List<Event> eventsList = entityManager.createQuery(criteriaQuery).setFirstResult(from)
                .setMaxResults(size).getResultList();
        return new HashSet<>(eventsList);
    }
}
