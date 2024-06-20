package br.dev.rodrigopinheiro.finances.controller.dto;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record RefundStatementDto(
        @NonNull Long statementId,
        @NonNull Long bankAccountId,
        @NonNull @DecimalMin("0.01") BigDecimal amount
) {
}
