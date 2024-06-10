package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.CreditCardController;
import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardDto;
import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.CreditCardNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.CreditCardRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    public CreditCardService(CreditCardRepository creditCardRepository, CreditCardController creditCard) {
        this.creditCardRepository = creditCardRepository;
    }

    public CreditCard findCreditCard(Long id) {
        return creditCardRepository.findById(id).orElseThrow(() -> new CreditCardNotFoundException(id));
    }


    public CreditCardDto create(CreditCard creditCard) {
        var creditCardCreated = creditCardRepository.save(creditCard);
        return new CreditCardDto(creditCardCreated.getName(), creditCardCreated.getLimitAmount(),
                creditCardCreated.getClosingDay(), creditCardCreated.getDueDay());

    }

    public List<CreditCardDto> findAll() {

        List<CreditCard> creditCards = creditCardRepository.findAll();

        return creditCards.stream().map(creditCard -> new CreditCardDto(creditCard.getName(), creditCard.getLimitAmount(), creditCard.getClosingDay(), creditCard.getDueDay())).collect(Collectors.toList());
    }

    public CreditCardDto findById(Long id) {
        var creditCard = creditCardRepository.findById(id).orElseThrow(() -> new CreditCardNotFoundException(id));

        return new CreditCardDto(creditCard.getName(), creditCard.getLimitAmount(), creditCard.getClosingDay(), creditCard.getDueDay());
    }


    public void delete(Long id) {
        try {
            creditCardRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CreditCardNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }

    public CreditCardDto update(Long id, CreditCardDto creditCardDto) {
        creditCardRepository.findById(id).ifPresentOrElse((existingCreditCard) -> {
            existingCreditCard.setName(creditCardDto.name());
            existingCreditCard.setLimitAmount(creditCardDto.limitAmount());
            existingCreditCard.setClosingDay(creditCardDto.closingDay());
            existingCreditCard.setDueDay(creditCardDto.dueDay());
            creditCardRepository.save(existingCreditCard);
        }, () -> {
            throw new CreditCardNotFoundException(id);
        });
        return creditCardDto;
    }
}
