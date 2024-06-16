package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.BankAccountDto;
import br.dev.rodrigopinheiro.finances.controller.dto.WalletDto;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.exception.BankAccountNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;
import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.TransferDto;

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


    public void creditBankAccount(TransactionDto transactionDto) {
        BankAccount account = bankAccountRepository.findById(transactionDto.bankAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transactionDto.bankAccountId()));
        account.credit(transactionDto.amount());

        Transaction transaction = new Transaction(transactionDto.amount(), TransactionType.CREDIT,
                transactionDto.isEffective(), account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (transactionDto.isEffective()) {
            walletService.creditWalletBalance(new WalletDto( transactionDto.amount(), account.getUser().getId()));
        }
    }

    public void debitBankAccount(TransactionDto transactionDto) {
        BankAccount account = bankAccountRepository.findById(transactionDto.bankAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transactionDto.bankAccountId()));
        account.debit(transactionDto.amount());

        Transaction transaction = new Transaction(transactionDto.amount(), TransactionType.DEBIT,
                transactionDto.isEffective(), account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (transactionDto.isEffective()) {
            walletService.debitWalletBalance(new WalletDto( transactionDto.amount(), account.getUser().getId()));
        }
    }

    public void transferBetweenAccounts(TransferDto transferDto) {
        var fromAccount = bankAccountRepository.findById(transferDto.fromAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transferDto.fromAccountId()));
        var toAccount = bankAccountRepository.findById(transferDto.toAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException(transferDto.toAccountId()));

        fromAccount.debit(transferDto.amount());
        toAccount.credit(transferDto.amount());

        Transaction debitTransaction = new Transaction(transferDto.amount(), TransactionType.TRANSFER,
                transferDto.isEffective(), fromAccount);
        Transaction creditTransaction = new Transaction(transferDto.amount(), TransactionType.TRANSFER,
                transferDto.isEffective(), toAccount);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
        if (transferDto.isEffective()) {
            walletService.debitWalletBalance(new WalletDto(transferDto.amount(), fromAccount.getUser().getId()));
            walletService.creditWalletBalance(new WalletDto(transferDto.amount(), toAccount.getUser().getId()));
        }
    }

    public BankAccountDto create(BankAccount bankAccount) {
        var bankAccountCreated = bankAccountRepository.save(bankAccount);
        return new BankAccountDto(bankAccountCreated.getBankName(), bankAccountCreated.getBankBalance(), bankAccountCreated.getUser().getId());
    }

    public List<BankAccountDto> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        return bankAccounts.stream()
                .map(bankAccount -> new BankAccountDto(bankAccount.getBankName(), bankAccount.getBankBalance(), bankAccount.getUser().getId()))
                .collect(Collectors.toList());

    }

    public BankAccountDto findById(Long id) {
        var bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException(id));
        return new BankAccountDto(bankAccount.getBankName(), bankAccount.getBankBalance(), bankAccount.getUser().getId());

    }
    public BankAccount findByIdBankAccount(Long id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException(id));

    }
    public BankAccountDto update(Long id, BankAccountDto bankAccountDto) {
        var updatedBankAcount=bankAccountRepository.findById(id).map((existingBankAccount) -> {
            existingBankAccount.setBankName(bankAccountDto.bankName());
            existingBankAccount.setBankBalance(bankAccountDto.bankBalance());
            var user = userService.findUser(bankAccountDto.userId());
            existingBankAccount.setUser(user);
            return bankAccountRepository.save(existingBankAccount);
        }).orElseThrow( () -> new BankAccountNotFoundException(id));
        return BankAccountDto.fromBankAccount(updatedBankAcount);
    }

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
