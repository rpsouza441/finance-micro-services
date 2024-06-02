package br.dev.rodrigopinheiro.finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;

public interface CreditCardStatementRepository extends JpaRepository<CreditCardStatement, Long> {

}
