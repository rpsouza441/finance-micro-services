package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Category;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferTransactionDto(
        @NonNull String description,
        String note,
        @NonNull @DecimalMin("0.01") BigDecimal amount,
        @NonNull boolean isRecurrent,
        @NonNull boolean isEffective,
        LocalDateTime creationDate,
        LocalDateTime effectivedDate,
        @NonNull Long fromBankAccountId,
        @NonNull Long toBankAccountId

) {


}
