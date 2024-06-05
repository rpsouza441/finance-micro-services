package br.dev.rodrigopinheiro.finances.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.dev.rodrigopinheiro.finances.exception.FinanceException;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(FinanceException.class)
  public ProblemDetail handleFinanceException(FinanceException e) {
    return e.toProblemDetail();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    var fieldErrors = e.getFieldErrors()
        .stream()
        .map(f -> new InvalidParam(f.getField(), f.getDefaultMessage()))
        .toList();
    var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Your request parameters didn't validate.");
    pd.setProperty("Invalid-Params", fieldErrors);
    return pd;
  }

  private record InvalidParam(String name, String reason) {
  }

}
