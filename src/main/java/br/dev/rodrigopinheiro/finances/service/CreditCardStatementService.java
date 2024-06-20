package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardStatementDto;
import br.dev.rodrigopinheiro.finances.controller.dto.RefundStatementDto;
import br.dev.rodrigopinheiro.finances.controller.dto.WalletDto;
import br.dev.rodrigopinheiro.finances.entity.*;
import br.dev.rodrigopinheiro.finances.entity.enums.CategoryType;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.exception.*;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.CategoryRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardStatementRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardStatementService {


    private final CreditCardService creditCardService;
    private final WalletService walletService;
    private final CreditCardStatementRepository creditCardStatementRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public CreditCardStatementService(CreditCardService creditCardService,
                                      WalletService walletService,
                                      CreditCardStatementRepository creditCardStatementRepository,
                                      BankAccountRepository bankAccountRepository,
                                      TransactionRepository transactionRepository,
                                      CategoryRepository categoryRepository) {
        this.creditCardService = creditCardService;
        this.walletService = walletService;
        this.creditCardStatementRepository = creditCardStatementRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }


    public CreditCardStatementDto findOrCreateStatementDto(CreditCardStatementDto cardStatementDto) {

        var creditCardStatement = getCreditCardStatement(cardStatementDto);
        return CreditCardStatementDto.fromCreditCardStatement(creditCardStatement);
    }

    public CreditCardStatement findOrCreateStatement(CreditCardStatementDto cardStatementDto) {

        return getCreditCardStatement(cardStatementDto);

    }

    private CreditCardStatement getCreditCardStatement(CreditCardStatementDto cardStatementDto) {
        CreditCard creditCard = creditCardService.findCreditCard(cardStatementDto.creditCardId());

        return creditCardStatementRepository.findByCreditCardAndMonthAndYear(creditCard,
                        cardStatementDto.month(), cardStatementDto.year())
                .orElseGet(() -> new CreditCardStatement(cardStatementDto.month(), cardStatementDto.year(),
                        BigDecimal.ZERO, creditCard));
    }


    public CreditCardStatementDto create(CreditCardStatementDto creditCardStatementDto) {
        var creditCardStatementCreated = creditCardStatementRepository.save(creditCardStatementDto.toCreditCardStatement());

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
