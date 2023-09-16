package ru.practicum.service.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.service.event.mapper.EventMapper;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.model.Request;
import ru.practicum.service.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, EventMapper.class})
public interface RequestMapper {
    @Mapping(target = "event", expression = "java(request.getEvent().getId())")
    @Mapping(target = "requester", expression = "java(request.getRequester().getId())")
    ParticipationRequestDto fromModelToDto(Request request);
}
