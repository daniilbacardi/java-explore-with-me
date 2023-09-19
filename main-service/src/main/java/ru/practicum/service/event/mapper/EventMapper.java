package ru.practicum.service.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.service.CommonConstants;
import ru.practicum.service.category.mapper.CategoryMapper;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.NewEventDto;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.Location;
import ru.practicum.service.user.model.User;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, LocationMapper.class})
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(category)")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())",
            dateFormat = CommonConstants.DATE_FORMAT)
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "initiator", expression = "java(initiator)")
    Event fromDtoToModel(NewEventDto newEventDto, User initiator, Category category, Location location);

    EventFullDto fromModelToFullDto(Event event, Long confirmedRequests, Long views);

    EventShortDto fromModelToShortDto(Event event, Long confirmedRequests, Long views);
}
