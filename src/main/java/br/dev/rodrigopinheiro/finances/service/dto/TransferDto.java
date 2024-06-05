package br.dev.rodrigopinheiro.finances.service.dto;

import java.math.BigDecimal;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMin;

public record TransferDto(
    @NonNull Long fromAccountId,
    @NonNull Long toAccountId,
    @NonNull @DecimalMin("0.01") BigDecimal amount,
    @NonNull boolean isEffective

) {
}
