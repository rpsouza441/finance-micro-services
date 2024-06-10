package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CategoryNotFoundException extends FinanceException {


  public CategoryNotFoundException() {

  }

  @Override
  public ProblemDetail toProblemDetail() {
    var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

    pd.setTitle("Category not Found");

    return pd;
  }

}
