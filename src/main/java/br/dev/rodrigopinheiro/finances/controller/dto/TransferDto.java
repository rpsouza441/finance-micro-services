package br.dev.rodrigopinheiro.finances.controller.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

public record TransferDto(
    @NotNull Long fromAccountId,
    @NotNull Long toAccountId,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    @NotNull boolean isEffective

) {
}
