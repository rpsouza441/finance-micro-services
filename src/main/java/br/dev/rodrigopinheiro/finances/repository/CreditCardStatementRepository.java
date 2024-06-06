package br.dev.rodrigopinheiro.finances.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;

public interface CreditCardStatementRepository extends JpaRepository<CreditCardStatement, Long> {

  Optional<CreditCardStatement> findByCreditCardAndMonthAndYear(CreditCard creditCard, int month, int year);

}
