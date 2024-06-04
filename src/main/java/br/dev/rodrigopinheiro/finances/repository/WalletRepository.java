package br.dev.rodrigopinheiro.finances.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {


    Optional<Wallet> findByUserId(Long userId);

}
