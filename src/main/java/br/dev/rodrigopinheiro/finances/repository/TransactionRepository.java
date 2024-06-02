package br.dev.rodrigopinheiro.finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
