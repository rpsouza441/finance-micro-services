package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class FinanceException extends RuntimeException {

  public ProblemDetail toProblemDetail() {
    var pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setTitle("Finance micro-service server error");

    return pd;
  }

}
