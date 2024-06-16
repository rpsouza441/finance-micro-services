package br.dev.rodrigopinheiro.finances.service;

import java.math.BigDecimal;

import br.dev.rodrigopinheiro.finances.exception.UserNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.UserRepository;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.Wallet;
import br.dev.rodrigopinheiro.finances.repository.WalletRepository;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    protected void creditWalletBalance(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletOrCreate(userId);
        wallet.credit(amount);
        walletRepository.save(wallet);
    }

    protected void debitWalletBalance(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletOrCreate(userId);
        wallet.debit(amount);
        walletRepository.save(wallet);
    }

    private Wallet getWalletOrCreate(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> new Wallet(BigDecimal.ZERO,
                        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId))));
    }

    public Wallet create(Wallet wallet) {
        return walletRepository.save(wallet);

    }
}
