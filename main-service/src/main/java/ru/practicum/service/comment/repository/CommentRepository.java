package ru.practicum.service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.service.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId, Pageable page);

    List<Comment> findAllByAuthorIdAndEventId(Long userId, Long eventId, Pageable page);

    List<Comment> findAllByAuthorId(Long userId, Pageable page);
}
