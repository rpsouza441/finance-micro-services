package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.Category;
import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import jakarta.validation.constraints.NotNull;

public record CategoryDto(
        @NotNull String name
) {
    public Category toCategory() {
        return new Category(name);
    }

    public static CategoryDto fromCategory(Category category) {
        return new CategoryDto(
                category.getName()
        );
    }
}
