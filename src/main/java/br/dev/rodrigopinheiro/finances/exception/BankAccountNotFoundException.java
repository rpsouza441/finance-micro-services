package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class BankAccountNotFoundException extends FinanceException {

  private final Long bankAccontId;

  public BankAccountNotFoundException(Long bankAccontId) {
    this.bankAccontId = bankAccontId;
  }

  @Override
  public ProblemDetail toProblemDetail() {
    var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

    pd.setTitle("Bank Account not Found");
    pd.setDetail("There is no BankAccount with id: " + bankAccontId);

    return pd;
  }

}
