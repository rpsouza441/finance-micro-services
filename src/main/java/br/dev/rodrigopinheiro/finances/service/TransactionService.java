import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class TransactionService {

    WalletService walletService;
    private TransactionRepository transactionRepository;

    public TransactionService(WalletService walletService, TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    public void markTransactionAsEffective(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow();
        if (!transaction.isEffective()) {
            transaction.setEffective(true);
            transactionRepository.save(transaction);
            walletService.updateWalletBalance(transaction.getBankAccount().getUser().getId(),
                    transaction.getType() == TransactionType.CREDIT ? transaction.getAmount()
                            : -transaction.getAmount());
        }
    }
}
