package br.dev.rodrigopinheiro.finances.service;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.entity.CreditCardTransaction;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.exception.BankAccountNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.CreditCardStatementNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.StatementAlreadyPaidException;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardStatementRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardTransactionRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreditCardService {

    private final BankAccountRepository bankAccountRepository;

    private final CreditCardRepository creditCardRepository;

    private final TransactionRepository transactionRepository;

    private final CreditCardTransactionRepository creditCardTransactionRepository;

    private final CreditCardStatementRepository creditCardStatementRepository;

    private final WalletService walletService;

    public CreditCardService(BankAccountRepository bankAccountRepository, CreditCardRepository creditCardRepository,
            TransactionRepository transactionRepository,
            CreditCardTransactionRepository creditCardTransactionRepository,
            CreditCardStatementRepository creditCardStatementRepository, WalletService walletService) {
        this.bankAccountRepository = bankAccountRepository;
        this.creditCardRepository = creditCardRepository;
        this.transactionRepository = transactionRepository;
        this.creditCardTransactionRepository = creditCardTransactionRepository;
        this.creditCardStatementRepository = creditCardStatementRepository;
        this.walletService = walletService;
    }

    public void addCreditCardTransaction(Long cardId, BigDecimal amount, int installments, boolean isEffective) {
        CreditCard creditCard = creditCardRepository.findById(cardId).orElseThrow();
        String installmentId = UUID.randomUUID().toString();

        for (int i = 0; i < installments; i++) {
            CreditCardTransaction installment = new CreditCardTransaction();
            installment.setDate(LocalDateTime.now().plusMonths(i));
            installment.setAmount(amount.divide(BigDecimal.valueOf(installments)));
            installment.setRefunded(false);
            installment.setInstallmentId(installmentId);
            installment.setStatement(findOrCreateStatement(creditCard, installment.getDate()));

            creditCardTransactionRepository.save(installment);
        }

        if (isEffective) {
            walletService.debitWalletBalance(creditCard.getUser().getId(), amount);
        }
    }

    public void refundCreditCardTransaction(String installmentId) {
        Optional<List<CreditCardTransaction>> optionalTransactions = creditCardTransactionRepository
                .findByInstallmentId(installmentId);
        List<CreditCardTransaction> installments = optionalTransactions.orElse(Collections.emptyList());
        for (CreditCardTransaction installment : installments) {
            if (!installment.isRefunded()) {
                installment.setRefunded(true);
                creditCardTransactionRepository.save(installment);
                walletService.creditWalletBalance(installment.getStatement().getCreditCard().getUser().getId(),
                        installment.getAmount());
            }
        }
    }

    public void payCreditCardStatement(Long statementId, Long bankAccountId, BigDecimal amount) {
        // To-Do exceptions
        CreditCardStatement statement = creditCardStatementRepository.findById(statementId).orElseThrow();

        BankAccount account = bankAccountRepository.findById(bankAccountId).orElseThrow();

        if (account.isBalanceEqualOrGreaterThan(amount)) {
            throw new RuntimeException("Insufficient funds in the bank account.");
        }

        account.debit(amount);

        Transaction paymentTransaction = new Transaction();
        paymentTransaction.setCreationDate(LocalDateTime.now());
        paymentTransaction.setAmount(amount);
        paymentTransaction.setTransactionType(TransactionType.DEBIT);
        paymentTransaction.setEffective(true); // Payment is effective
        paymentTransaction.setBankAccount(account);

        transactionRepository.save(paymentTransaction);
        bankAccountRepository.save(account);

        statement.setAmountPayed(amount);
        statement.setPayed(true); // Mark the statement as paid
        creditCardStatementRepository.save(statement);

        walletService.debitWalletBalance(account.getUser().getId(), amount);
    }

    public void payCreditCardStatement(Long statementId, Long bankAccountId) {
        CreditCardStatement statement = creditCardStatementRepository.findById(statementId)
                .orElseThrow(() -> new CreditCardStatementNotFoundException(statementId));

        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new BankAccountNotFoundException(bankAccountId));

        if (statement.isPayed()) {
            throw new StatementAlreadyPaidException("Statement is already paid");
        }

        // Create a transaction to record the payment
        Transaction transaction = new Transaction(statement.getAmountDue(), TransactionType.DEBIT, true, statement,
                bankAccount);

        // Update the statement to mark it as paid
        statement.setPayed(true);
        statement.setEffectivedDate(LocalDateTime.now());

        // Save the transaction and update the statement
        transactionRepository.save(transaction);
        creditCardStatementRepository.save(statement);

        // Update the account balance
        bankAccount.debit(statement.getAmountDue());
        bankAccountRepository.save(bankAccount);
    }

    private CreditCardStatement findOrCreateStatement(CreditCard creditCard, LocalDateTime date) {
        int month = date.getMonthValue();
        int year = date.getYear();
        return creditCardStatementRepository.findByCreditCardAndMonthAndYear(creditCard, month, year)
                .orElseGet(() -> {
                    CreditCardStatement statement = new CreditCardStatement();
                    statement.setCreditCard(creditCard);
                    statement.setMonth(month);
                    statement.setYear(year);
                    statement.setAmountDue(0.0);
                    return creditCardStatementRepository.save(statement);
                });
    }

}
