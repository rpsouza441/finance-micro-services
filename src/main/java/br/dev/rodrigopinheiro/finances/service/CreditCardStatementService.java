package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardStatementDto;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.CreditCardStatementNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.CreditCardStatementRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardStatementService {


    private final CreditCardStatementRepository creditCardStatementRepository;
    private final CreditCardService creditCardService;

    public CreditCardStatementService(CreditCardStatementRepository creditCardStatementRepository, CreditCardService creditCardService) {
        this.creditCardStatementRepository = creditCardStatementRepository;
        this.creditCardService = creditCardService;
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
        creditCardStatementRepository.findById(id).ifPresentOrElse((existingCreditCardStatement) -> {
            var creditCard = creditCardService.findCreditCard(creditCardStatementDto.creditCardId());
            existingCreditCardStatement.setMonth(creditCardStatementDto.month());
            existingCreditCardStatement.setYear(creditCardStatementDto.year());
            existingCreditCardStatement.setAmountDue(creditCardStatementDto.amountDue());
            existingCreditCardStatement.setCreditCard(creditCard);

            creditCardStatementRepository.save(existingCreditCardStatement);
        }, () -> {
            throw new CreditCardStatementNotFoundException(id);
        });
        return creditCardStatementDto;
    }
}
