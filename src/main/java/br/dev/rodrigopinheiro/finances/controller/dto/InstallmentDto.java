package br.dev.rodrigopinheiro.finances.controller.dto;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record InstallmentDto(
        @NonNull Long cardId,
        @NonNull @DecimalMin("0.01") BigDecimal amount,
        @NonNull int installments,
        @NonNull Long categoryId
) {

}
