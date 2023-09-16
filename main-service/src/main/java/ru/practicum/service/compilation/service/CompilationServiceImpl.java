package ru.practicum.service.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.service.compilation.mapper.CompilationMapper;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.repository.CompilationRepository;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.exception.EntityNotFoundException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.info("CompilationServiceImpl: addCompilation вызван");
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            events = eventService.getEventsByIdsList(newCompilationDto.getEvents());
            if (newCompilationDto.getEvents().size() != events.size()) {
                throw new EntityNotFoundException("События не найдены");
            }
        }
        Compilation newCompilation = compilationRepository.save(
                compilationMapper.fromDtoToModel(newCompilationDto, events));
        log.info("CompilationServiceImpl: addCompilation выполнено {}",
                compilationRepository.findById(newCompilation.getId()));
        return getCompilationById(newCompilation.getId());
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("СompilationServiceImpl: updateCompilation вызван");
        Compilation compilation = getCompilation(compId);
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventService.getEventsByIdsList(updateCompilationRequest.getEvents());
            if (events.size() != updateCompilationRequest.getEvents().size()) {
                throw new EntityNotFoundException("События не найдены");
            }
            compilation.setEvents(events);
        }
        compilationRepository.save(compilation);
        log.info("CompilationServiceImpl: updateCompilation выполнено id = {}", compId);
        return getCompilationById(compId);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        log.info("CompilationServiceImpl: deleteCompilation выполнено id = {}", compId);
        compilationRepository.delete(getCompilation(compId));
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("СompilationServiceImpl: getAllCompilations вызван");
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        Pageable page = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page);
        Set<Event> events = new HashSet<>();
        compilations.forEach(compilation -> events.addAll(compilation.getEvents()));
        Map<Long, EventShortDto> eventsMap = new HashMap<>();
        eventService.getEventShortWithViewsAndRequests(new ArrayList<>(events))
                .forEach(eventShortDto -> eventsMap.put(eventShortDto.getId(), eventShortDto));
        if (pinned != null) {
            compilations.forEach(compilation -> {
                List<EventShortDto> shortList = new ArrayList<>();
                compilation.getEvents().forEach(event -> shortList.add(eventsMap.get(event.getId())));
                compilationDtoList.add(compilationMapper.fromModelToDto(compilation, shortList));
            });
        } else {
            compilationRepository.findAll(page).forEach(compilation -> {
                List<EventShortDto> shortList = new ArrayList<>();
                compilation.getEvents().forEach(event -> shortList.add(eventsMap.get(event.getId())));
                compilationDtoList.add(compilationMapper.fromModelToDto(compilation, shortList));
            });
        }
        log.info("СompilationServiceImpl: getAllCompilations выполнено");
        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("СompilationServiceImpl: getCompilationById вызван");
        Compilation compilation = getCompilation(compId);
        List<EventShortDto> events = eventService.getEventShortWithViewsAndRequests(compilation.getEvents());
        log.info("СompilationServiceImpl: getCompilationById выполнено id {}", compId);
        return compilationMapper.fromModelToDto(compilation, events);
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Подборка не найдена"));
    }
}
