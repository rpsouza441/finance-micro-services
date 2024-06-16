package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.CategoryDto;
import br.dev.rodrigopinheiro.finances.entity.Category;
import br.dev.rodrigopinheiro.finances.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(new Category(categoryDto.name())));
    }

    @GetMapping
    public List<CategoryDto> getAll() {
        return categoryService.findAll();
    }

    @GetMapping("{id}")
    public CategoryDto get(@PathVariable("id") Long id) {
        return categoryService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable("id") Long id, @RequestBody @Valid CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body( categoryService.update(id, categoryDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
