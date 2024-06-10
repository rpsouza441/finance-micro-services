package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CreditCardNotFoundException extends FinanceException {

  private final Long creditcardId;

  public CreditCardNotFoundException(Long creditcardId) {
    this.creditcardId = creditcardId;
  }

  @Override
  public ProblemDetail toProblemDetail() {
    var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

    pd.setTitle("Credit Card not Found");
    pd.setDetail("There is no Credit Card with id: " + creditcardId);

    return pd;
  }

}
