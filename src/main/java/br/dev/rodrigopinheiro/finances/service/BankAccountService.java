import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.repository.BankAccountRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardStatementRepository;
import br.dev.rodrigopinheiro.finances.repository.CreditCardTransactionRepository;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;
import br.dev.rodrigopinheiro.finances.repository.UserRepository;
import br.dev.rodrigopinheiro.finances.repository.WalletRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BankAccountService {

    private BankAccountRepository bankAccountRepository;

    private TransactionRepository transactionRepository;

    private WalletService walletService;

    public void creditBankAccount(Long accountId, double amount, boolean isEffective) {
        BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
        account.setBalance(account.getBalance() + amount);

        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setType(TransactionType.CREDIT);
        transaction.setEffective(isEffective);
        transaction.setBankAccount(account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (isEffective) {
            walletService.updateWalletBalance(account.getUser().getId(), amount);
        }
    }

    public void debitBankAccount(Long accountId, double amount, boolean isEffective) {
        BankAccount account = bankAccountRepository.findById(accountId).orElseThrow();
        account.setBalance(account.getBalance() - amount);

        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEBIT);
        transaction.setEffective(isEffective);
        transaction.setBankAccount(account);

        transactionRepository.save(transaction);
        bankAccountRepository.save(account);

        if (isEffective) {
            walletService.updateWalletBalance(account.getUser().getId(), -amount);
        }
    }

    public void transferBetweenAccounts(Long fromAccountId, Long toAccountId, double amount, boolean isEffective) {
        BankAccount fromAccount = bankAccountRepository.findById(fromAccountId).orElseThrow();
        BankAccount toAccount = bankAccountRepository.findById(toAccountId).orElseThrow();

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        Transaction debitTransaction = new Transaction();
        debitTransaction.setDate(LocalDateTime.now());
        debitTransaction.setAmount(amount);
        debitTransaction.setType(TransactionType.TRANSFER);
        debitTransaction.setEffective(isEffective);
        debitTransaction.setBankAccount(fromAccount);

        Transaction creditTransaction = new Transaction();
        creditTransaction.setDate(LocalDateTime.now());
        creditTransaction.setAmount(amount);
        creditTransaction.setType(TransactionType.TRANSFER);
        creditTransaction.setEffective(isEffective);
        creditTransaction.setBankAccount(toAccount);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        if (isEffective) {
            walletService.updateWalletBalance(fromAccount.getUser().getId(), -amount);
            walletService.updateWalletBalance(toAccount.getUser().getId(), amount);
        }
    }

}
