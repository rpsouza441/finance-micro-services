package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.*;
import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import br.dev.rodrigopinheiro.finances.exception.*;
import br.dev.rodrigopinheiro.finances.repository.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.entity.CreditCardTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreditCardTransactionService {

    private final BankAccountRepository bankAccountRepository;

    private final CreditCardRepository creditCardRepository;


    private final CreditCardTransactionRepository creditCardTransactionRepository;

    private final CreditCardStatementRepository creditCardStatementRepository;

    private final WalletService walletService;
    private final CreditCardStatementService creditCardStatementService;
    private final CategoryRepository categoryRepository;

    public CreditCardTransactionService(BankAccountRepository bankAccountRepository, CreditCardRepository creditCardRepository,
                                        CreditCardTransactionRepository creditCardTransactionRepository,
                                        CreditCardStatementRepository creditCardStatementRepository, WalletService walletService, CreditCardStatementService creditCardStatementService, CategoryRepository categoryRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.creditCardRepository = creditCardRepository;
        this.creditCardTransactionRepository = creditCardTransactionRepository;
        this.creditCardStatementRepository = creditCardStatementRepository;
        this.walletService = walletService;
        this.creditCardStatementService = creditCardStatementService;
        this.categoryRepository = categoryRepository;
    }

    //TODO
    public List<CreditCardTransactionDto> addCreditCardTransaction(InstallmentDto installmentDto) {
        var creditCard = creditCardRepository.findById(installmentDto.creditCardId()).orElseThrow(() ->
                new CreditCardNotFoundException(installmentDto.creditCardId()));
        var statement = creditCardStatementRepository.findById(installmentDto.statementId()).orElseThrow(() ->
                new CreditCardStatementNotFoundException(installmentDto.statementId()));

        var category = categoryRepository.findByName(installmentDto.category().name()).orElseThrow(CategoryNotFoundException::new);

        String installmentId = UUID.randomUUID().toString();

        List<CreditCardTransactionDto> creditCardTransactionDtos = new ArrayList<>();

        for (int i = 0; i < installmentDto.installments(); i++) {
            // Criando o LocalDateTime com o ano e mês fornecidos,
            // o dia é o primeiro do mês e o tempo é meia-noite
            LocalDateTime dateTime = LocalDateTime.of(statement.getYear(), statement.getMonth(), 1, 0, 0);

            var installment = new CreditCardTransaction(
                    dateTime.plusMonths(i),
                    installmentDto.amount().divide(BigDecimal.valueOf(installmentDto.installments()), 2, RoundingMode.HALF_UP),
                    installmentId,
                    statement,
                    category);


            CreditCardStatement cardStatement = creditCardStatementService
                    .findOrCreateStatement(
                            new CreditCardStatementDto(
                                    installment.getDate().getYear(),
                                    installment.getDate().getMonthValue(),
                                    BigDecimal.ZERO,
                                    creditCard.getId()));
            installment.setStatement(cardStatement);

            //Add amount due on Statement
            cardStatement.addAmountDue(installment.getAmount());

            CreditCardTransaction creditCardTransactionCreated = creditCardTransactionRepository.save(installment);

            cardStatement.getCreditCardTransactions().add(creditCardTransactionCreated);

            creditCardStatementRepository.save(cardStatement);
            creditCardTransactionDtos.add(CreditCardTransactionDto.fromCreditCard(creditCardTransactionCreated));
        }
        return creditCardTransactionDtos;
    }

    //TODO
    public List<CreditCardTransactionDto> refundCreditCardTransaction(String installmentId) {
        List<CreditCardTransaction> creditCardTransactions = creditCardTransactionRepository
                .findByInstallmentId(installmentId).orElseThrow(() -> new TransactionInstallMentNotFoundException(installmentId));
        List<CreditCardTransactionDto> creditCardTransactionDtos = new ArrayList<>();

        for (CreditCardTransaction installment : creditCardTransactions) {
            if (!installment.isRefunded()) {
                creditCardTransactionDtos.add(CreditCardTransactionDto.fromCreditCard(installment));

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
        return creditCardTransactionDtos;
    }


//    public CreditCardTransactionDto create(CreditCardTransactionDto creditCardTransactionDto) {
//        var CreditCardTransactionCreated = creditCardTransactionRepository.save(creditCardTransactionDto.toCreditCardTransaction());
//        return new CreditCardTransactionDto(CreditCardTransactionCreated.getDate(), CreditCardTransactionCreated.getAmount(),
//                CreditCardTransactionCreated.getInstallmentId(), CreditCardTransactionCreated.getStatement().getId());
//
//    }

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


