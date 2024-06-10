package br.dev.rodrigopinheiro.finances.controller.dto;

import jakarta.validation.constraints.NotNull;

public record CategoryDto(
        @NotNull String name
) {
}
