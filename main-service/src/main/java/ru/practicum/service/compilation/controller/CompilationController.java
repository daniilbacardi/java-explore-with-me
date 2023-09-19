package ru.practicum.service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.service.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationController {
    private static final String ADMIN_PATH = "/admin/compilations";
    private static final String PUBLIC_PATH = "/compilations";
    private final CompilationService compilationService;

    @PostMapping(value = ADMIN_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("CompilationController: addCompilation выполнено {}", newCompilationDto);
        return compilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping(value = ADMIN_PATH + "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable("compId") Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("CompilationController: updateCompilation выполнено id = {}", compId);
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping(value = ADMIN_PATH + "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        log.info("CompilationController: deleteCompilation выполнено id = {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @GetMapping(value = PUBLIC_PATH)
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAllCompilations(
            @RequestParam (required = false) Boolean pinned,
            @RequestParam (name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam (name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("CompilationController: getAllCompilations выполнено");
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping(value = PUBLIC_PATH + "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable("compId") Long compId) {
        log.info("CompilationController: getCompilationById выполнено id {}", compId);
        return compilationService.getCompilationById(compId);
    }
}
