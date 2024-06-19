package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.enums.CategoryType;
import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record InstallmentDto(
        @NonNull Long creditCardId,
        @NonNull Long statementId,
        @NonNull @DecimalMin("0.01") BigDecimal amount,
        @NonNull int installments,
        @NonNull CategoryType category
) {

}
