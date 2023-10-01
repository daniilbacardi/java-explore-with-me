package ru.practicum.service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@ResponseStatus(HttpStatus.OK)
@Validated
@Slf4j
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getCommentByIdPublic(@PathVariable("commentId") Long commentId) {
        log.info("CommentPublicController getCommentByIdPublic выполнено {}", commentId);
        return commentService.getCommentByIdPublic(commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsForEventPublic(@RequestParam Long eventId,
                                                      @RequestParam(required = false, defaultValue = "0")
                                                      @PositiveOrZero Integer from,
                                                      @RequestParam(required = false, defaultValue = "10")
                                                          @Positive Integer size) {
        log.info("CommentPublicController getCommentsForEventPublic выполнено " +
                "eventId - {}, from - {}, size - {}", eventId, from, size);
        return commentService.getCommentsForEventPublic(eventId, from, size);
    }
}
