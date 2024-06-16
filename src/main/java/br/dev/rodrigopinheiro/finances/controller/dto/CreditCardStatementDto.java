package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreditCardStatementDto (
        @NotNull int month,
        @NotNull int year,
        @NotNull BigDecimal amountDue,
        @NotNull Long creditCardId


){


    public CreditCardStatement toCreditCardStatement(){
        var creditCard = new CreditCard();
        creditCard.setId(creditCardId);
        return new CreditCardStatement(month,year,amountDue, creditCard);

    }
    public static CreditCardStatementDto fromCreditCardStatement(CreditCardStatement creditCardStatement) {
        return new CreditCardStatementDto(
                creditCardStatement.getMonth(),
                creditCardStatement.getYear(),
                creditCardStatement.getAmountDue(),
                creditCardStatement.getCreditCard().getId()
        );
    }
}
