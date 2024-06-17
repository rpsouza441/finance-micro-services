package br.dev.rodrigopinheiro.finances.repository;

import java.util.List;
import java.util.Optional;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.CreditCardTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long> {

    Optional<List<CreditCardTransaction>> findByInstallmentId(String installmentId);


    @Query("SELECT DISTINCT t.bankAccount FROM CreditCardTransaction cct " +
            "JOIN cct.statement s " +
            "JOIN s.transactions t " +
            "WHERE cct.statement.id = :installmentId")
    List<BankAccount> findBankAccountsByInstallmentId(@Param("installmentId") Long installmentId);


}
