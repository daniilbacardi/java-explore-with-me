package ru.practicum.service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController {
    private static final String ADMIN_PATH = "/admin/categories";
    private static final String PUBLIC_PATH = "/categories";
    private final CategoryService categoryService;

    @PostMapping(value = ADMIN_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("CategoryController: addCategory выполнено {}", newCategoryDto);
        return categoryService.addCategory(newCategoryDto);
    }

    @PatchMapping(value = ADMIN_PATH + "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("CategoryController: updateCategory выполнено catId {}, categoryDto {}", catId, categoryDto);
        return categoryService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping(value = ADMIN_PATH + "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long catId) {
        log.info("CategoryController: deleteCategory выполнено {}", catId);
        categoryService.deleteCategory(catId);
    }

    @GetMapping(value = PUBLIC_PATH)
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories(
            @RequestParam (name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam (name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("CategoryController: getAllCategories выполнено");
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping(value = PUBLIC_PATH + "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable("catId") Long catId) {
        log.info("CategoryController: getCategoryById выполнено {}", catId);
        return categoryService.getCategoryDtoById(catId);
    }
}
