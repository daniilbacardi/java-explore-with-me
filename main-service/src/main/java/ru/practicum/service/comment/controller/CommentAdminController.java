package ru.practicum.service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Validated
@Slf4j
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentByAdmin(@PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CommentAdminController updateCommentByAdmin выполнено {}", commentId);
        return commentService.updateCommentByAdmin(commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable("commentId") Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
        log.info("CommentAdminController deleteCommentByAdmin выполнено");
    }
}
