package br.dev.rodrigopinheiro.finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.CreditCard;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

}
