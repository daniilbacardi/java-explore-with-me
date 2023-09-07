package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStats;
import ru.practicum.server.model.Stats;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT new ru.practicum.dto.model.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats as s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.model.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats as s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getStatsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.model.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats as s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getStatsAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.model.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats as s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getStatsAndUrisWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
