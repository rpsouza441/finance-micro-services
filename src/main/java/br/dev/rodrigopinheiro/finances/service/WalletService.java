import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.Wallet;
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
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    protected void updateWalletBalance(Long userId, double amount) {
        Wallet wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(userRepository.findById(userId).orElseThrow());
            wallet.setBalance(0);
        }
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }

}
