package ru.practicum.service.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.service.event.dto.LocationDto;
import ru.practicum.service.event.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(target = "id", ignore = true)
    Location fromDtoToModel(LocationDto locationDto);
}
