package ru.practicum.service.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.service.CommonConstants;
import ru.practicum.service.category.mapper.CategoryMapper;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.event.mapper.LocationMapper;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.user.model.User;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, LocationMapper.class})
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", expression = "java(author)")
    @Mapping(target = "event", expression = "java(event)")
    @Mapping(target = "created", expression = "java(" +
            "java.time.LocalDateTime.now())", dateFormat = CommonConstants.DATE_FORMAT)
    Comment fromDtoToModel(NewCommentDto newCommentDto, User author, Event event);

    @Mapping(target = "eventId", expression = "java(comment.getEvent().getId())")
    CommentDto fromModelToDto(Comment comment);
}
