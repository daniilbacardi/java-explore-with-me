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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@ResponseStatus(HttpStatus.OK)
@Validated
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addCommentPrivate(@PathVariable Long userId,
                                        @RequestParam Long eventId,
                                        @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CommentPrivateController addCommentPrivate выполнено userId - {}, eventId - {}", userId, eventId);
        return commentService.addCommentPrivate(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentPrivate(@PathVariable("userId") Long userId,
                                           @PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CommentPrivateController updateCommentPrivate " +
                "выполнено userId - {}, commentId - {}", userId, commentId);
        return commentService.updateCommentPrivate(userId, commentId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getUserCommentsPrivate(@PathVariable("userId") Long userId,
                                                   @RequestParam(required = false) Long eventId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("CommentPrivateController getUserCommentsPrivate выполнено userId - {}, " +
                "eventId - {}, from - {}, size - {}", userId, eventId, from, size);
        return commentService.getUserCommentsPrivate(userId, eventId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable("userId") Long userId,
                                    @PathVariable("commentId") Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
        log.info("CommentPrivateController deleteCommentByUser выполнено");
    }
}
