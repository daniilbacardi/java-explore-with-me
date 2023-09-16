package ru.practicum.service.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation fromDtoToModel(NewCompilationDto newCompilationDto, List<Event> events);

    @Mapping(target = "events", source = "events")
    CompilationDto fromModelToDto(Compilation compilation, List<EventShortDto> events);
}
