package br.dev.rodrigopinheiro.finances.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Category;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransactionDto(
        String description,
        String note,
        @NonNull @DecimalMin("0.01") BigDecimal amount,
        BigDecimal interest,
        BigDecimal discount,
        @NonNull TransactionType transactionType,
        boolean isRecurrent,
        boolean isEffective,
        LocalDateTime creationDate,
        @NotNull LocalDateTime dueDate,
        LocalDateTime effectivedDate,
        @NonNull Long bankAccountId,
        @NonNull Long categoryId

) {

    public Transaction toTransaction() {
        var bankAccount = new BankAccount();
        bankAccount.setId(bankAccountId);
        var category = new Category();
        category.setId(categoryId);
        return new Transaction(
                description, note, amount, interest, discount, transactionType,
                isRecurrent, isEffective, creationDate, dueDate, effectivedDate, category, bankAccount
        );
    }



    public static TransactionDto fromTransaction(Transaction transaction) {
        // Verificações de campos que não são @NotNull
        String description = transaction.getDescription() != null ? transaction.getDescription() : "";
        String note = transaction.getNote() != null ? transaction.getNote() : "";
        BigDecimal interest = transaction.getInterest() != null ? transaction.getInterest() : BigDecimal.ZERO;
        BigDecimal discount = transaction.getDiscount() != null ? transaction.getDiscount() : BigDecimal.ZERO;
        LocalDateTime creationDate = transaction.getCreationDate() != null ? transaction.getCreationDate() : LocalDateTime.now();
        LocalDateTime effectivedDate = transaction.getEffectivedDate() != null ? transaction.getEffectivedDate() : null;


        return new TransactionDto(
                description,
                note,
                transaction.getAmount(),
                interest,
                discount,
                transaction.getTransactionType(),
                transaction.isRecurrent(),
                transaction.isEffective(),
                creationDate,
                transaction.getDueDate(),
                effectivedDate,
                transaction.getBankAccount().getId(),
                transaction.getCategory().getId()
        );
    }
}
