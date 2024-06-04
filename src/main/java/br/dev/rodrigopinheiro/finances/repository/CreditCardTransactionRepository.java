package br.dev.rodrigopinheiro.finances.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.CreditCardTransaction;

public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long> {

  Optional<List<CreditCardTransaction>> findByInstallmentId(String installmentId);
}
