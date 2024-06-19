package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TransactionAlreadyEffectiveException extends FinanceException {


    @Override
    public ProblemDetail toProblemDetail() {
        var pb = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        pb.setTitle("Transaction already effective");

        return pb;
    }
}
