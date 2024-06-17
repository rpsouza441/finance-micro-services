package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardStatementDto;
import br.dev.rodrigopinheiro.finances.controller.dto.WalletDto;
import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.exception.BankAccountNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.CreditCardStatementNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.InsufficientBalanceException;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardStatementRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardStatementService {


    private final CreditCardService creditCardService;
    private final WalletService walletService;
    private final CreditCardStatementRepository creditCardStatementRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public CreditCardStatementService(CreditCardStatementRepository creditCardStatementRepository,
                                      CreditCardService creditCardService,
                                      WalletService walletService,
                                      BankAccountRepository bankAccountRepository,
                                      TransactionRepository transactionRepository) {
        this.creditCardStatementRepository = creditCardStatementRepository;
        this.creditCardService = creditCardService;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    public void payCreditCardStatement(Long statementId, Long bankAccountId, BigDecimal amount) {
        CreditCardStatement statement = creditCardStatementRepository.findById(statementId)
                .orElseThrow(() -> new CreditCardStatementNotFoundException(statementId));

        BankAccount account = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new BankAccountNotFoundException(bankAccountId));

        if (account.isBalanceEqualOrGreaterThan(amount)) {
            throw new InsufficientBalanceException(amount);
        }

        account.debit(amount);

        Transaction paymentTransaction = new Transaction(amount, TransactionType.DEBIT, true, account);

        transactionRepository.save(paymentTransaction);
        bankAccountRepository.save(account);

        statement.setAmountPayed(amount);
        statement.setPayed(true); // Mark the statement as paid
        creditCardStatementRepository.save(statement);

        walletService.debitWalletBalance(new WalletDto(amount, account.getUser().getId()));
    }


    public CreditCardStatement findCreditCardStatement(Long id) {
        return creditCardStatementRepository.findById(id).orElseThrow(() -> new CreditCardStatementNotFoundException(id));
    }


    public CreditCardStatementDto create(CreditCardStatement creditCardStatement) {
        var creditCardStatementCreated = creditCardStatementRepository.save(creditCardStatement);

        return new CreditCardStatementDto(creditCardStatementCreated.getMonth(), creditCardStatementCreated.getYear(),
                creditCardStatementCreated.getAmountDue(), creditCardStatementCreated.getCreditCard().getId());
    }

    public List<CreditCardStatementDto> findAll() {

        List<CreditCardStatement> creditCardStatements = creditCardStatementRepository.findAll();

        return creditCardStatements.stream()

                .map(creditCardStatement -> new CreditCardStatementDto(creditCardStatement.getMonth(), creditCardStatement.getYear(),
                        creditCardStatement.getAmountDue(), creditCardStatement.getCreditCard().getId()))
                .collect(Collectors.toList());
    }

    public CreditCardStatementDto findById(Long id) {
        var creditCardStatement = creditCardStatementRepository.findById(id).orElseThrow(() -> new CreditCardStatementNotFoundException(id));

        return new CreditCardStatementDto(creditCardStatement.getMonth(), creditCardStatement.getYear(), creditCardStatement.getAmountDue(),
                creditCardStatement.getCreditCard().getId());
    }

    public void delete(Long id) {
        try {
            creditCardStatementRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CreditCardStatementNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }

    public CreditCardStatementDto update(Long id, CreditCardStatementDto creditCardStatementDto) {
        var updatedCreditCardStatement = creditCardStatementRepository.findById(id).map((existingCreditCardStatement) -> {
            var creditCard = creditCardService.findCreditCard(creditCardStatementDto.creditCardId());
            existingCreditCardStatement.setMonth(creditCardStatementDto.month());
            existingCreditCardStatement.setYear(creditCardStatementDto.year());
            existingCreditCardStatement.setAmountDue(creditCardStatementDto.amountDue());
            existingCreditCardStatement.setCreditCard(creditCard);

            return creditCardStatementRepository.save(existingCreditCardStatement);
        }).orElseThrow(() ->
                new CreditCardStatementNotFoundException(id));
        return CreditCardStatementDto.fromCreditCardStatement(updatedCreditCardStatement);
    }
}
