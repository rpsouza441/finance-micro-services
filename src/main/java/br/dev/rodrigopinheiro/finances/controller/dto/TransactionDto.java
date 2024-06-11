package br.dev.rodrigopinheiro.finances.controller.dto;

import java.math.BigDecimal;

import br.dev.rodrigopinheiro.finances.entity.Transaction;
import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMin;

public record TransactionDto(
        @NonNull Long accountId,
        @NonNull @DecimalMin("0.01") BigDecimal amount,
        @NonNull boolean isEffective) {

    public Transaction toTransaction() {
        return new Transaction(

        );
    }
}
