package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.CategoryDto;
import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardStatementDto;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.entity.Wallet;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.CreditCardStatementNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.CreditCardStatementRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardStatementService {


    private final CreditCardStatementRepository creditCardStatementRepository;

    public CreditCardStatementService(CreditCardStatementRepository creditCardStatementRepository) {
        this.creditCardStatementRepository = creditCardStatementRepository;
    }

    public CreditCardStatement findCreditCardStatement(Long id) {
        return creditCardStatementRepository.findById(id).orElseThrow(() -> new CreditCardStatementNotFoundException(id));
    }


    public CreditCardStatementDto create(CreditCardStatement creditCardStatement) {
        var categoryCreated = creditCardStatementRepository.save(creditCardStatement);
        //TODO implementar a dto
        return new CreditCardStatementDto();
    }

    public List<CreditCardStatementDto> findAll() {

        List<CreditCardStatement> creditCardStatements = creditCardStatementRepository.findAll();

        return creditCardStatements.stream()
                //TODO preencher dto
                .map(creditCardStatement -> new CreditCardStatementDto())
                .collect(Collectors.toList());
    }

    public CreditCardStatementDto findById(Long id) {
        var creditCardStatement = creditCardStatementRepository.findById(id).orElseThrow(() -> new CreditCardStatementNotFoundException(id));

        return new CreditCardStatementDto();
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
          //    TODO atualizar a statement

            creditCardStatementRepository.save(existingCreditCardStatement);
        }, () -> {
            throw new CreditCardStatementNotFoundException(id);
        });
        return creditCardStatementDto;
    }
}
