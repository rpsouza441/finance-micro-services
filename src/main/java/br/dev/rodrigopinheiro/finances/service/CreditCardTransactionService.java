package br.dev.rodrigopinheiro.finances.service;


import br.dev.rodrigopinheiro.finances.repository.CreditCardTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreditCardTransactionService {

    private final CreditCardTransactionRepository creditCardTransactionRepository;

    public CreditCardTransactionService(CreditCardTransactionRepository creditCardTransactionRepository) {
        this.creditCardTransactionRepository = creditCardTransactionRepository;
    }
}
