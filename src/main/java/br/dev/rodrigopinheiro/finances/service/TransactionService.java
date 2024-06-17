package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.WalletDto;
import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.exception.InsufficientBalanceException;
import br.dev.rodrigopinheiro.finances.exception.TransactionNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final BankAccountService bankAccountService;
    private final CategoryService categoryService;

    public TransactionService(WalletService walletService, TransactionRepository transactionRepository,
                              BankAccountService bankAccountService, CategoryService categoryService) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
        this.categoryService = categoryService;
    }

    // Método para realizar débito em uma transação
    public void debitTransaction(TransactionDto transactionDto) {
        BankAccount bankAccount = bankAccountService.findBankAccountById(transactionDto.bankAccountId());

        // Verifica se há saldo suficiente na conta bancária
        if (bankAccount.isBalanceEqualOrGreaterThan(transactionDto.amount())) {
            checkIsEffectivedAndCreditOrDebitWalletBankAccount(transactionDto, bankAccount);
            // Persiste a transação com o saldo debitado
            transactionRepository.save(transactionDto.toTransaction());
        } else {
            throw new InsufficientBalanceException(transactionDto.amount());
        }
    }

    // Método para realizar crédito em uma transação
    public void creditTransaction(TransactionDto transactionDto) {
        BankAccount bankAccount = bankAccountService.findBankAccountById(transactionDto.bankAccountId());
        checkIsEffectivedAndCreditOrDebitWalletBankAccount(transactionDto, bankAccount);
        // Persiste a transação com o saldo creditado
        transactionRepository.save(transactionDto.toTransaction());
    }


    // Método para transferir entre contas
    public void transferBetweenAccounts(TransactionDto debitTransactionDto, TransactionDto creditTransactionDto) {
        // Realiza transferência entre contas
        BankAccount debitAccount = bankAccountService.findBankAccountById(debitTransactionDto.bankAccountId());
        BankAccount creditAccount = bankAccountService.findBankAccountById(creditTransactionDto.bankAccountId());

        // Verifica se há saldo suficiente na conta de débito
        if (debitAccount.isBalanceEqualOrGreaterThan(debitTransactionDto.amount())) {

            checkIsEffectivedAndCreditOrDebitWalletBankAccount(creditTransactionDto, creditAccount);
            checkIsEffectivedAndCreditOrDebitWalletBankAccount(debitTransactionDto, debitAccount);
            // Persiste ambas as transações
            transactionRepository.save(debitTransactionDto.toTransaction());
            transactionRepository.save(creditTransactionDto.toTransaction());
        } else {
            throw new InsufficientBalanceException(debitTransactionDto.amount());
        }
    }


    public void markTransactionAsEffective(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        BankAccount bankAccount = bankAccountService.findBankAccountById(transaction.getBankAccount().getId());

        if (!transaction.isEffective()) {
            transaction.setEffective(true);
            transactionRepository.save(transaction);
            switch (transaction.getTransactionType()) {
                case DEBIT:
                    bankAccountService.debit(bankAccount, transaction.getAmount());
                    walletService.debitWalletBalance(new WalletDto(transaction.getAmount(), bankAccount.getUser().getId()));
                case CREDIT:
                    bankAccountService.credit(bankAccount, transaction.getAmount());
                    walletService.creditWalletBalance(new WalletDto(transaction.getAmount(), bankAccount.getUser().getId()));

            }


        }
    }


    public Transaction findTransaction(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }


    public List<TransactionDto> findAll() {

        List<Transaction> categories = transactionRepository.findAll();

        return categories.stream()
                .map(TransactionDto::fromTransaction)
                .collect(Collectors.toList());
    }

    public TransactionDto findById(Long id) {
        var transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        return TransactionDto.fromTransaction(transaction);
    }

    public void delete(Long id) {
        try {
            transactionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new TransactionNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }

    public TransactionDto update(Long id, TransactionDto transactionDto) {
        var updatedTransaction = transactionRepository.findById(id).map(existingTransaction -> {

            var bankAccount = bankAccountService.findByIdBankAccount(transactionDto.bankAccountId());
            var category = categoryService.findCategory(transactionDto.categoryId());


            if (!transactionDto.description().isEmpty()) {
                existingTransaction.setDescription(transactionDto.description());
            }
            if (!transactionDto.note().isEmpty()) {
                existingTransaction.setDescription(transactionDto.note());
            }
            existingTransaction.setAmount(transactionDto.amount());
            if (transactionDto.interest() != null) {
                existingTransaction.setInterest(transactionDto.interest());
            }
            if (transactionDto.discount() != null) {
                existingTransaction.setDiscount(transactionDto.discount());
            }
            existingTransaction.setTransactionType(transactionDto.transactionType());
            existingTransaction.setRecurrent(transactionDto.isRecurrent());
            existingTransaction.setEffective(transactionDto.isEffective());
            if (transactionDto.creationDate() != null) {
                existingTransaction.setCreationDate(transactionDto.creationDate());
            }
            existingTransaction.setDueDate(transactionDto.dueDate());
            if (transactionDto.effectivedDate() != null) {
                existingTransaction.setEffectivedDate(transactionDto.effectivedDate());
            }
            existingTransaction.setBankAccount(bankAccount);
            existingTransaction.setCategory(category);


            return transactionRepository.save(existingTransaction);
        }).orElseThrow(() -> new TransactionNotFoundException(id));
        return TransactionDto.fromTransaction(updatedTransaction);
    }

    //Checa se está efetivado, caso seja de debito chama as funcoes debit caso seja credito chama credit
    private void checkIsEffectivedAndCreditOrDebitWalletBankAccount(TransactionDto transactionDto, BankAccount bankAccount) {
        if (transactionDto.isEffective()) {

            switch (transactionDto.transactionType()) {
                case CREDIT:
                    // Credita o valor da transação no saldo da conta bancária
                    bankAccountService.credit(bankAccount, transactionDto.amount());
                    walletService.creditWalletBalance(new WalletDto(transactionDto.amount(), bankAccount.getUser().getId()));
                case DEBIT:
                    // Debita o valor da transação do saldo da conta bancária
                    bankAccountService.debit(bankAccount, transactionDto.amount());
                    walletService.debitWalletBalance(new WalletDto(transactionDto.amount(), bankAccount.getUser().getId()));
            }
        }
    }


}
