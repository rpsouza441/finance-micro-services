package br.dev.rodrigopinheiro.finances.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TransactionInstallMentNotFoundException extends FinanceException {

    private final String installmentId;

    public TransactionInstallMentNotFoundException(String installmentId) {
        this.installmentId = installmentId;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        pd.setTitle("Credit Card Statement ID not Found");
        pd.setDetail("There is no Credit Card Statement with Statement Id: " + installmentId);

        return pd;
    }


}
