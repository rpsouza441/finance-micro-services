package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCardDto(
        @NotNull String name,
        @NotNull @DecimalMin("0.01") BigDecimal limitAmount,
        @NotNull LocalDate closingDay,
        @NotNull LocalDate dueDay
) {

    public CreditCard toCreditCard() {return  new CreditCard(name, limitAmount, closingDay, dueDay);
    }
    public static CreditCardDto fromCreditCard(CreditCard creditCard) {
        return new CreditCardDto(
                creditCard.getName(),
                creditCard.getLimitAmount(),
                creditCard.getClosingDay(),
                creditCard.getDueDay()
        );
    }
}
