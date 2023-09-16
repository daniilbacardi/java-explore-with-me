package ru.practicum.service.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto fromModelToDto(Category category);

    Category fromNewDtoToModel(NewCategoryDto newCategoryDto);
}
