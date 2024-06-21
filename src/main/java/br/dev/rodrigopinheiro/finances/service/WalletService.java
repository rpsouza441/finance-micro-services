package br.dev.rodrigopinheiro.finances.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import br.dev.rodrigopinheiro.finances.controller.dto.WalletDto;
import br.dev.rodrigopinheiro.finances.entity.Wallet;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.UserNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.WalletNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.UserRepository;
import br.dev.rodrigopinheiro.finances.repository.WalletRepository;
import org.springframework.dao.EmptyResultDataAccessException;
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

    // Credits a wallet balance for a user.
    protected void creditWalletBalance(WalletDto walletDto) {
        Wallet wallet = getWalletOrCreate(walletDto.userId());
        wallet.credit(walletDto.ballance());
        walletRepository.save(wallet);
    }

    // Debits a wallet balance for a user.
    protected void debitWalletBalance(WalletDto walletDto) {
        Wallet wallet = getWalletOrCreate(walletDto.userId());
        wallet.debit(walletDto.ballance());
        walletRepository.save(wallet);
    }

    private Wallet getWalletOrCreate(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> new Wallet(BigDecimal.ZERO,
                        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId))));
    }

    public WalletDto create(WalletDto walletDto) {
        var user = userRepository.findById(walletDto.userId()).orElseThrow(() ->
                new UserNotFoundException(walletDto.userId()));
        var newWallet = new Wallet(walletDto.ballance(), user);
        var walletCreated = walletRepository.save(newWallet);
        user.setWallet(walletCreated);
        userRepository.save(user);
        walletRepository.save(newWallet);
        return new WalletDto(walletCreated.getBalance(), walletCreated.getUser().getId());

    }

    public List<WalletDto> findAll() {

        List<Wallet> wallets = walletRepository.findAll();

        return wallets.stream()
                .map(wallet -> new WalletDto(wallet.getBalance(), wallet.getUser().getId()))
                .collect(Collectors.toList());
    }

    public WalletDto findById(Long id) {
        var wallet = walletRepository.findById(id).orElseThrow(() -> new WalletNotFoundException(id));

        return new WalletDto(wallet.getBalance(), wallet.getUser().getId());
    }

    public void delete(Long id) {
        try {
            walletRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new WalletNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }

    public WalletDto update(Long id, WalletDto walletDto) {

        Wallet updatedWallet = walletRepository.findById(id).map(existingWallet -> {
            var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            existingWallet.setBalance(walletDto.ballance());
            var walletCreated = walletRepository.save(existingWallet);
            user.setWallet(walletCreated);
            userRepository.save(user);
            return walletCreated;

        }).orElseThrow(() -> new WalletNotFoundException(id));

        return new WalletDto(updatedWallet.getBalance(), updatedWallet.getUser().getId());
    }
}
