package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.User;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BankAccountDto(
        @NotNull String bankName,
        BigDecimal bankBalance,
        @NotNull Long userId

) {
    public BankAccount toBankAccount() {
        var user = new User();
        user.setId(userId);
        return new BankAccount(bankName,bankBalance, user);
    }
}
