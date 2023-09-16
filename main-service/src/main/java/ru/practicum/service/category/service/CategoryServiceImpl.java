package ru.practicum.service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.mapper.CategoryMapper;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.exception.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("CategoryServiceImpl: addCategory вызван");
        CategoryDto category = categoryMapper.fromModelToDto(
                categoryRepository.save(categoryMapper.fromNewDtoToModel(newCategoryDto)));
        log.info("CategoryServiceImpl: addCategory выполнено {}", category);
        return category;
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("CategoryServiceImpl: updateCategory вызван");
        Category updatedCategory = categoryRepository.findById(catId).orElseThrow(()
                -> new EntityNotFoundException("Категория не найдена"));
        updatedCategory.setName(categoryDto.getName());
        log.info("CategoryServiceImpl: updateCategory выполнено {}", updatedCategory);
        return categoryMapper.fromModelToDto(categoryRepository.save(updatedCategory));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("CategoryServiceImpl: deleteCategory вызван");
        categoryRepository.delete(categoryRepository.findById(catId).orElseThrow(
                () -> new EntityNotFoundException("Категория не найдена")));
        log.info("CategoryServiceImpl: deleteCategory выполнено {}", catId);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("CategoryServiceImpl: getAllCategories вызван");
        Pageable page = PageRequest.of(from / size, size);
        log.info("CategoryServiceImpl: getAllCategories выполнено");
        return categoryRepository.findAll(page).getContent().stream()
                .map(categoryMapper::fromModelToDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryDtoById(Long catId) {
        log.info("CategoryServiceImpl: getCategoryDtoById выполнено {}", catId);
        return categoryMapper.fromModelToDto(categoryRepository.findById(catId).orElseThrow(()
                -> new EntityNotFoundException("Категория не найдена")));
    }

    @Override
    public Category getCategoryById(Long catId) {
        log.info("CategoryServiceImpl: getCategoryById выполнено {}", catId);
        return categoryRepository.findById(catId).orElseThrow(()
                -> new EntityNotFoundException("Категория не найдена"));
    }
}
