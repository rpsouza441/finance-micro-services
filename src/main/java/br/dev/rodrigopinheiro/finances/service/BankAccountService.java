package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.BankAccountDto;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.exception.BankAccountNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final UserService userService;

    public BankAccountService(BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository, WalletService walletService, UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
        this.userService = userService;
    }

    // Debits the specified amount from the given bank account, saves the updated bank account.
    public void debit(BankAccount bankAccount, BigDecimal value) {
        BankAccount renewedBankAccount = findBankAccountById(bankAccount.getId());
        renewedBankAccount.debit(value);
        bankAccountRepository.save(renewedBankAccount);
    }

    // Credits the specified amount to the given bank account, saves the updated bank account.
    public void credit(BankAccount bankAccount, BigDecimal value) {
        BankAccount renewedBankAccount = findBankAccountById(bankAccount.getId());
        renewedBankAccount.credit(value);
        bankAccountRepository.save(renewedBankAccount);
    }

    // Creates a new bank account, saves it, and returns its DTO.
    public BankAccountDto create(BankAccount bankAccount) {
        var bankAccountCreated = bankAccountRepository.save(bankAccount);
        return new BankAccountDto(bankAccountCreated.getBankName(), bankAccountCreated.getBankBalance(), bankAccountCreated.getUser().getId());
    }

    // Finds all bank accounts and returns them as a list of DTOs.
    public List<BankAccountDto> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        return bankAccounts.stream()
                .map(bankAccount -> new BankAccountDto(bankAccount.getBankName(), bankAccount.getBankBalance(), bankAccount.getUser().getId()))
                .collect(Collectors.toList());

    }

    // Finds a bank account by its ID and returns its DTO.
    public BankAccountDto findBankAccountDtoById(Long id) {
        var bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException(id));
        return new BankAccountDto(bankAccount.getBankName(), bankAccount.getBankBalance(), bankAccount.getUser().getId());

    }

    // Finds a bank account by its ID.
    public BankAccount findBankAccountById(Long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException(id));

    }

    // Alias for findBankAccountById(Long id)
    public BankAccount findByIdBankAccount(Long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException(id));

    }

    // Updates a bank account with new data from its DTO and returns the updated DTO.
    public BankAccountDto update(Long id, BankAccountDto bankAccountDto) {
        var updatedBankAcount = bankAccountRepository.findById(id).map((existingBankAccount) -> {
            existingBankAccount.setBankName(bankAccountDto.bankName());
            existingBankAccount.setBankBalance(bankAccountDto.bankBalance());
            var user = userService.findUser(bankAccountDto.userId());
            existingBankAccount.setUser(user);
            return bankAccountRepository.save(existingBankAccount);
        }).orElseThrow(() -> new BankAccountNotFoundException(id));
        return BankAccountDto.fromBankAccount(updatedBankAcount);
    }

    // Deletes a bank account by its ID, throws custom exceptions on failure.
    public void delete(Long id) {
        try {
            bankAccountRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new BankAccountNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }
}
