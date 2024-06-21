package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.TransferTransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.WalletDto;
import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Category;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.CategoryType;
import br.dev.rodrigopinheiro.finances.exception.*;
import br.dev.rodrigopinheiro.finances.repository.CategoryRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final BankAccountService bankAccountService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public TransactionService(WalletService walletService, TransactionRepository transactionRepository,
                              BankAccountService bankAccountService, CategoryService categoryService, CategoryRepository categoryRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    // Debits a transaction from the bank account if there is sufficient balance, returns the saved transaction as DTO.
    public TransactionDto debitTransaction(TransactionDto transactionDto) {
        BankAccount bankAccount = bankAccountService.findBankAccountById(transactionDto.bankAccountId());

        // Check if the bank account has sufficient balance
        if (bankAccount.isBalanceEqualOrGreaterThan(transactionDto.amount())) {
            checkIsEffectivedAndCreditOrDebitWalletBankAccount(transactionDto, bankAccount);
            // Persist the transaction with the debited balance
            return TransactionDto.fromTransaction(
                    transactionRepository.save(transactionDto.toTransaction())
            );
        } else {
            throw new InsufficientBalanceException(transactionDto.amount());
        }

    }

    // Credits a transaction to the bank account, returns the saved transaction as DTO.
    public TransactionDto creditTransaction(TransactionDto transactionDto) {
        BankAccount bankAccount = bankAccountService.findBankAccountById(transactionDto.bankAccountId());
        checkIsEffectivedAndCreditOrDebitWalletBankAccount(transactionDto, bankAccount);
        // Persist the transaction with the credited balance
        return TransactionDto.fromTransaction(
                transactionRepository.save(transactionDto.toTransaction())
        );
    }


    // Transfers amount between accounts, returns the transactions as a list of DTOs.
    public List<TransactionDto> transferBetweenAccounts(TransferTransactionDto transferTransactionDto) {
        // Transfer between accounts
        BankAccount debitAccount = bankAccountService.findBankAccountById(transferTransactionDto.fromBankAccountId());
        BankAccount creditAccount = bankAccountService.findBankAccountById(transferTransactionDto.toBankAccountId());

        List<TransactionDto> transactionDtos = new ArrayList<>();

        // Check if the debit account has sufficient balance
        if (debitAccount.isBalanceEqualOrGreaterThan(transferTransactionDto.amount())) {

            var category = categoryRepository.findByName(
                    CategoryType.TRANSFER.name()).orElseThrow(CategoryNotFoundException::new);
            var debitTransaction = createTransferTransaction(transferTransactionDto, category, debitAccount);
            var creditTransaction = createTransferTransaction(transferTransactionDto, category, creditAccount);

            if (transferTransactionDto.isEffective()) {
                debitAccount.debit(transferTransactionDto.amount());
                creditAccount.credit(transferTransactionDto.amount());
            }

            // Persist both transactions and add to the list
            transactionDtos.add(TransactionDto.fromTransaction(transactionRepository.save(debitTransaction)));
            transactionDtos.add(TransactionDto.fromTransaction(transactionRepository.save(creditTransaction)));
        } else {
            throw new InsufficientBalanceException(transferTransactionDto.amount());
        }
        return transactionDtos;
    }

    private static Transaction createTransferTransaction(TransferTransactionDto transferTransactionDto, Category category, BankAccount account) {
        return new Transaction(
                transferTransactionDto.description(),
                transferTransactionDto.note(),
                transferTransactionDto.amount(),
                TransactionType.TRANSFER,
                transferTransactionDto.isRecurrent(),
                transferTransactionDto.isEffective(),
                transferTransactionDto.creationDate(),
                category,
                account);
    }

    // Marks a transaction as effective and updates the bank account and wallet balances accordingly.
    public TransactionDto markTransactionAsEffective(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        BankAccount bankAccount = bankAccountService.findBankAccountById(transaction.getBankAccount().getId());

        if (!transaction.isEffective()) {
            transaction.setEffective(true);
            switch (transaction.getTransactionType()) {
                case DEBIT:
                    bankAccountService.debit(bankAccount, transaction.getAmount());
                    walletService.debitWalletBalance(new WalletDto(transaction.getAmount(), bankAccount.getUser().getId()));
                case CREDIT:
                    bankAccountService.credit(bankAccount, transaction.getAmount());
                    walletService.creditWalletBalance(new WalletDto(transaction.getAmount(), bankAccount.getUser().getId()));

            }
            return TransactionDto.fromTransaction(transactionRepository.save(transaction));
        } else {
            throw new TransactionAlreadyEffectiveException();
        }
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
