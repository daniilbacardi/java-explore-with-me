package ru.practicum.service.comment.service;

import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto updateCommentByAdmin(Long commentId, NewCommentDto newCommentDto);

    void deleteCommentByAdmin(Long commentId);

    CommentDto getCommentByIdPublic(Long commentId);

    List<CommentDto> getCommentsForEventPublic(Long eventId, Integer from, Integer size);

    CommentDto addCommentPrivate(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateCommentPrivate(Long userId, Long commentId, NewCommentDto newCommentDto);

    List<CommentDto> getUserCommentsPrivate(Long userId, Long eventId, Integer from, Integer size);

    void deleteCommentByUser(Long userId, Long commentId);
}
