package br.dev.rodrigopinheiro.finances.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.Wallet;
import br.dev.rodrigopinheiro.finances.repository.WalletRepository;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    private final UserService userService;

    public WalletService(WalletRepository walletRepository, UserService userService) {
        this.walletRepository = walletRepository;
        this.userService = userService;
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
                .orElseGet(() -> new Wallet(BigDecimal.ZERO, userService.findUser(userId)));
    }

    public Wallet create(Wallet wallet) {
        return walletRepository.save(wallet);

    }
}
