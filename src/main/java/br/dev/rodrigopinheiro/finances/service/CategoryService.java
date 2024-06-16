package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.CategoryDto;
import br.dev.rodrigopinheiro.finances.entity.Category;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.CategoryNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.CategoryRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }


    public CategoryDto create(Category category) {
        var categoryCreated = categoryRepository.save(category);
        return new CategoryDto(categoryCreated.getName());

    }

    public List<CategoryDto> findAll() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryDto(category.getName()))
                .collect(Collectors.toList());
    }

    public CategoryDto findById(Long id) {
        var category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);

        return new CategoryDto(category.getName());
    }

    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CategoryNotFoundException();
        } catch (Exception e) {
            throw new FinanceException();
        }
    }
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        categoryRepository.findById(id).ifPresentOrElse((existingCategory) -> {
            existingCategory.setName(categoryDto.name());
            categoryRepository.save(existingCategory);
        }, () -> {
            throw new CategoryNotFoundException();
        });
        return categoryDto;
    }
}
