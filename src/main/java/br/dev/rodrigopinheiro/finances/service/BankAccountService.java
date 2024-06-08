package br.dev.rodrigopinheiro.finances.service;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.exception.BankAccountNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;
import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.TransferDto;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public BankAccountService(BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository,
            WalletService walletService) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    public void creditBankAccount(TransactionDto transactionDto) {
        BankAccount account = bankAccountRepository.findById(transactionDto.accountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transactionDto.accountId()));
        account.credit(transactionDto.amount());

        Transaction transaction = new Transaction(transactionDto.amount(), TransactionType.CREDIT,
                transactionDto.isEffective(), account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (transactionDto.isEffective()) {
            walletService.creditWalletBalance(account.getUser().getId(), transactionDto.amount());
        }
    }

    public void debitBankAccount(TransactionDto transactionDto) {
        BankAccount account = bankAccountRepository.findById(transactionDto.accountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transactionDto.accountId()));
        account.debit(transactionDto.amount());

        Transaction transaction = new Transaction(transactionDto.amount(), TransactionType.DEBIT,
                transactionDto.isEffective(), account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (transactionDto.isEffective()) {
            walletService.debitWalletBalance(account.getUser().getId(), transactionDto.amount());
        }
    }

    public void transferBetweenAccounts(TransferDto transferDto) {
        var fromAccount = bankAccountRepository.findById(transferDto.fromAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transferDto.fromAccountId()));
        var toAccount = bankAccountRepository.findById(transferDto.toAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transferDto.toAccountId()));

        fromAccount.debit(transferDto.amount());
        toAccount.credit(transferDto.amount());

        Transaction debitTransaction = new Transaction(transferDto.amount(), TransactionType.TRANSFER,
                transferDto.isEffective(), fromAccount);
        Transaction creditTransaction = new Transaction(transferDto.amount(), TransactionType.TRANSFER,
                transferDto.isEffective(), toAccount);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
        if (transferDto.isEffective()) {
            walletService.debitWalletBalance(fromAccount.getUser().getId(), transferDto.amount());
            walletService.creditWalletBalance(toAccount.getUser().getId(), transferDto.amount());
        }
    }

}
