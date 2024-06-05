package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TransactionNotFoundException extends FinanceException {

  private final Long transactionId;

  public TransactionNotFoundException(Long transactionId) {
    this.transactionId = transactionId;
  }

  @Override
  public ProblemDetail toProblemDetail() {
    var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

    pd.setTitle("Transaction not Found");
    pd.setDetail("There is no Transaction with id: " + transactionId);

    return pd;
  }

}
