package br.dev.rodrigopinheiro.finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

}
