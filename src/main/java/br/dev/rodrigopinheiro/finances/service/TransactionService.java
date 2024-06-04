package br.dev.rodrigopinheiro.finances.service;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

@Service
public class TransactionService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    public TransactionService(WalletService walletService, TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    public void markTransactionAsEffective(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow();
        if (!transaction.isEffective()) {
            transaction.setEffective(true);
            transactionRepository.save(transaction);
            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                walletService.creditWalletBalance(
                    transaction.getBankAccount().getUser().getId(), 
                    transaction.getAmount()
                );
            } else {
                walletService.debitWalletBalance(
                    transaction.getBankAccount().getUser().getId(), 
                    transaction.getAmount()
                );
            }
            
          
        }
    }
}
