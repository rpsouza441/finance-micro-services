package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.entity.CreditCardTransaction;
import io.micrometer.common.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditCardTransactionDto(
        LocalDateTime date,
        @NonNull BigDecimal amount,
        String installmentId,
        Long creditCardStatementId
) {
    public static CreditCardTransactionDto fromCreditCard(CreditCardTransaction creditCardTransaction) {
        return new CreditCardTransactionDto(
                creditCardTransaction.getDate(),
                creditCardTransaction.getAmount(),
                creditCardTransaction.getInstallmentId(),
                creditCardTransaction.getStatement().getId()
        );
    }

    public CreditCardTransaction toCreditCardTransaction() {
    var creditCardStatement = new CreditCardStatement();
    creditCardStatement.setId(creditCardStatementId);
    return new CreditCardTransaction(date,amount,installmentId,creditCardStatement);
}


}
