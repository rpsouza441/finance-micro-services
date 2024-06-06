package br.dev.rodrigopinheiro.finances.exception;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class InsufficientBalanceException extends FinanceException {

  private final BigDecimal value;

  public InsufficientBalanceException(BigDecimal value) {
    this.value = value;
  }

  @Override
  public ProblemDetail toProblemDetail() {
    var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

    pd.setTitle("Insufficient balance");
    pd.setDetail("You cannot transfer a value (" + value + ") bigger than your current balance.");

    return pd;
  }

}
