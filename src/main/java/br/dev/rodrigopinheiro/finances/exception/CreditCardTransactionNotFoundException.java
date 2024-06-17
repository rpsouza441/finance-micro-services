package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CreditCardTransactionNotFoundException  extends FinanceException {

    private final Long creditCardTransactionId;

    public CreditCardTransactionNotFoundException(Long creditCardTransactionId) {
        this.creditCardTransactionId = creditCardTransactionId;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        pd.setTitle("Statement not Found");
        pd.setDetail("There is no Statement with id: " + creditCardTransactionId);

        return pd;
    }

}
