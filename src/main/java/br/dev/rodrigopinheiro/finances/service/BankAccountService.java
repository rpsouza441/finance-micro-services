package br.dev.rodrigopinheiro.finances.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final TransactionRepository transactionRepository;

    private final  WalletService walletService;

    

    public BankAccountService(BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository,
            WalletService walletService) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    public void creditBankAccount(Long accountId, BigDecimal amount, boolean isEffective) {
        BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
        account.credit(amount);

        Transaction transaction = new Transaction();
        transaction.setCreationDate(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setEffective(isEffective);
        transaction.setBankAccount(account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (isEffective) {
            walletService.creditWalletBalance(account.getUser().getId(), amount);
        }
    }

    public void debitBankAccount(Long accountId, BigDecimal amount, boolean isEffective) {
        BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
        account.debit(amount);

        Transaction transaction = new Transaction();
        transaction.setCreationDate(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setEffective(isEffective);
        transaction.setBankAccount(account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (isEffective) {
            walletService.debitWalletBalance(account.getUser().getId(), amount);
        }
    }

    public void transferBetweenAccounts(Long fromAccountId, Long toAccountId, BigDecimal amount, boolean isEffective) {
        BankAccount fromAccount = bankAccountRepository.findById(fromAccountId).orElseThrow();
        BankAccount toAccount = bankAccountRepository.findById(toAccountId).orElseThrow();

        fromAccount.debit(amount);
        toAccount.credit(amount);

        Transaction debitTransaction = new Transaction();
        debitTransaction.setCreationDate(LocalDateTime.now());
        debitTransaction.setAmount(amount);
        debitTransaction.setTransactionType(TransactionType.TRANSFER);
        debitTransaction.setEffective(isEffective);
        debitTransaction.setBankAccount(fromAccount);

        Transaction creditTransaction = new Transaction();
        creditTransaction.setCreationDate(LocalDateTime.now());
        creditTransaction.setAmount(amount);
        creditTransaction.setTransactionType(TransactionType.TRANSFER);
        creditTransaction.setEffective(isEffective);
        creditTransaction.setBankAccount(toAccount);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        if (isEffective) {
            walletService.debitWalletBalance(fromAccount.getUser().getId(), amount);
            walletService.creditWalletBalance(toAccount.getUser().getId(), amount);
        }
    }

}
