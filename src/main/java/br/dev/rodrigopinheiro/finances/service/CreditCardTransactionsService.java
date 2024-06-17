package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardTransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.InstallmentDto;
import br.dev.rodrigopinheiro.finances.controller.dto.WalletDto;
import br.dev.rodrigopinheiro.finances.exception.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.entity.CreditCardTransaction;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardStatementRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardTransactionRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreditCardTransactionsService {

    private final BankAccountRepository bankAccountRepository;

    private final CreditCardRepository creditCardRepository;

    private final TransactionRepository transactionRepository;

    private final CreditCardTransactionRepository creditCardTransactionRepository;

    private final CreditCardStatementRepository creditCardStatementRepository;

    private final WalletService walletService;

    public CreditCardTransactionsService(BankAccountRepository bankAccountRepository, CreditCardRepository creditCardRepository,
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

    public void addCreditCardTransaction(InstallmentDto installmentDto) {
        var creditCard = creditCardRepository.findById(installmentDto.cardId()).orElseThrow(() ->
                new CreditCardNotFoundException(installmentDto.cardId()));

        String installmentId = UUID.randomUUID().toString();

        for (int i = 0; i < installmentDto.installments(); i++) {
            var installment = new CreditCardTransaction(LocalDateTime.now().plusMonths(i),
                    installmentDto.amount().divide(BigDecimal.valueOf(installmentDto.installments()), 2, RoundingMode.HALF_UP),
                    false, installmentId);
            installment.setStatement(findOrCreateStatement(creditCard, installment.getDate()));
            creditCardTransactionRepository.save(installment);
        }

    }

    public void refundCreditCardTransaction(String installmentId) {
        List<CreditCardTransaction> creditCardTransactions = creditCardTransactionRepository
                .findByInstallmentId(installmentId).orElseThrow(() -> new TransactionInstallMentNotFoundException(installmentId));
        for (CreditCardTransaction installment : creditCardTransactions) {
            if (!installment.isRefunded()) {

                //Busca os bancos que foram debitadas para dar o credito
                List<BankAccount> bankAccounts = creditCardTransactionRepository.findBankAccountsByInstallmentId(installment.getStatement().getId());
                for (BankAccount bankAccount : bankAccounts) {
                    bankAccount.credit(installment.getAmount());
                    bankAccountRepository.save(bankAccount);
                }

                installment.setRefunded(true);
                creditCardTransactionRepository.save(installment);
                walletService.creditWalletBalance(new WalletDto(installment.getAmount(),
                        installment.getStatement().getCreditCard().getUser().getId())
                );
            }
        }
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

        //Debit from wallet
        walletService.debitWalletBalance(new WalletDto(statement.getAmountDue(), bankAccount.getUser().getId()));
    }

    private CreditCardStatement findOrCreateStatement(CreditCard creditCard, LocalDateTime date) {
        int month = date.getMonthValue();
        int year = date.getYear();
        return creditCardStatementRepository.findByCreditCardAndMonthAndYear(creditCard, month, year)
                .orElseGet(() -> {
                    return new CreditCardStatement(month, year, BigDecimal.ZERO, creditCard);
                });
    }


    public CreditCardTransactionDto create(CreditCardTransactionDto creditCardTransactionDto) {
        var CreditCardTransactionCreated = creditCardTransactionRepository.save(creditCardTransactionDto.toCreditCardTransaction());
        return new CreditCardTransactionDto(CreditCardTransactionCreated.getDate(), CreditCardTransactionCreated.getAmount(),
                CreditCardTransactionCreated.getInstallmentId(), CreditCardTransactionCreated.getStatement().getId());

    }

    public List<CreditCardTransactionDto> findAll() {

        List<CreditCardTransaction> creditCardTransactions = creditCardTransactionRepository.findAll();

        return creditCardTransactions.stream().map(creditCardTransaction -> new CreditCardTransactionDto(
                creditCardTransaction.getDate(), creditCardTransaction.getAmount(),
                creditCardTransaction.getInstallmentId(), creditCardTransaction.getStatement().getId())).collect(Collectors.toList());
    }

    public CreditCardTransactionDto findById(Long id) {
        var creditCardTransaction = creditCardTransactionRepository.findById(id).orElseThrow(() ->
                new CreditCardTransactionNotFoundException(id));

        return new CreditCardTransactionDto(creditCardTransaction.getDate(), creditCardTransaction.getAmount(),
                creditCardTransaction.getInstallmentId(), creditCardTransaction.getStatement().getId());
    }


    public void delete(Long id) {
        try {
            creditCardTransactionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CreditCardNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }

    public CreditCardTransactionDto update(Long id, CreditCardTransactionDto creditCardTransactionDto) {
        var updatedSCreditCardTransaction = creditCardTransactionRepository.findById(id).map((existingCreditCardTransaction) -> {
            if (creditCardTransactionDto.date() != null) {
                existingCreditCardTransaction.setDate(creditCardTransactionDto.date());
            }
            existingCreditCardTransaction.setAmount(creditCardTransactionDto.amount());
            if (creditCardTransactionDto.installmentId().isEmpty()) {
                existingCreditCardTransaction.setInstallmentId(creditCardTransactionDto.installmentId());
            }
            if (creditCardTransactionDto.creditCardStatementId() != null) {
                var creditCardStatement = creditCardStatementRepository.findById(creditCardTransactionDto.creditCardStatementId())
                        .orElseThrow(() -> new CreditCardStatementNotFoundException(creditCardTransactionDto.creditCardStatementId()));
                existingCreditCardTransaction.setStatement(creditCardStatement);
            }

            return creditCardTransactionRepository.save(existingCreditCardTransaction);
        }).orElseThrow(() ->
                new CreditCardNotFoundException(id)
        );
        return CreditCardTransactionDto.fromCreditCard(updatedSCreditCardTransaction);
    }
}


