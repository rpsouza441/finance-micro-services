package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CreditCardStatementNotFoundException extends FinanceException {

  private final Long statementId;

  public CreditCardStatementNotFoundException(Long statementId) {
    this.statementId = statementId;
  }

  @Override
  public ProblemDetail toProblemDetail() {
    var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

    pd.setTitle("Statement not Found");
    pd.setDetail("There is no TransactStatement with id: " + statementId);

    return pd;
  }

}
