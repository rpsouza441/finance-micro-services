import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.BankAccount;
import br.dev.rodrigopinheiro.finances.entity.CreditCard;
import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;
import br.dev.rodrigopinheiro.finances.entity.CreditCardTransaction;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
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
public class CreditCardService {

    private CreditCardRepository creditCardRepository;

    private TransactionRepository transactionRepository;

    private CreditCardTransactionRepository creditCardTransactionRepository;

    private CreditCardStatementRepository creditCardStatementRepository;

    private WalletService walletService;

    public CreditCardService(CreditCardRepository creditCardRepository, TransactionRepository transactionRepository,
            CreditCardTransactionRepository creditCardTransactionRepository,
            CreditCardStatementRepository creditCardStatementRepository, WalletService walletService) {
        this.creditCardRepository = creditCardRepository;
        this.transactionRepository = transactionRepository;
        this.creditCardTransactionRepository = creditCardTransactionRepository;
        this.creditCardStatementRepository = creditCardStatementRepository;
        this.walletService = walletService;
    }

    public void addCreditCardTransaction(Long cardId, double amount, int installments, boolean isEffective) {
        CreditCard creditCard = creditCardRepository.findById(cardId).orElseThrow();
        String installmentId = UUID.randomUUID().toString();

        for (int i = 0; i < installments; i++) {
            CreditCardTransaction installment = new CreditCardTransaction();
            installment.setDate(LocalDateTime.now().plusMonths(i));
            installment.setAmount(amount / installments);
            installment.setRefunded(false);
            installment.setInstallmentId(installmentId);
            installment.setStatement(findOrCreateStatement(creditCard, installment.getDate()));

            creditCardTransactionRepository.save(installment);
        }

        if (isEffective) {
            walletService.updateWalletBalance(creditCard.getUser().getId(), -amount);
        }
    }

    public void refundCreditCardTransaction(String installmentId) {
        List<CreditCardTransaction> installments = creditCardTransactionRepository.findByInstallmentId(installmentId);
        for (CreditCardTransaction installment : installments) {
            if (!installment.isRefunded()) {
                installment.setRefunded(true);
                creditCardTransactionRepository.save(installment);
                updateWalletBalance(installment.getStatement().getCreditCard().getUser().getId(),
                        installment.getAmount());
            }
        }
    }

    public void payCreditCardStatement(Long statementId, Long bankAccountId) {
        CreditCardStatement statement = creditCardStatementRepository.findById(statementId).orElseThrow();
        BankAccount account = bankAccountRepository.findById(bankAccountId).orElseThrow();

        double amountDue = statement.getAmountDue();

        if (account.getBalance() < amountDue) {
            throw new RuntimeException("Insufficient funds in the bank account.");
        }

        account.setBalance(account.getBalance() - amountDue);

        Transaction paymentTransaction = new Transaction();
        paymentTransaction.setDate(LocalDateTime.now());
        paymentTransaction.setAmount(amountDue);
        paymentTransaction.setType(TransactionType.DEBIT);
        paymentTransaction.setEffective(true); // Payment is effective
        paymentTransaction.setBankAccount(account);

        transactionRepository.save(paymentTransaction);
        bankAccountRepository.save(account);

        statement.setAmountDue(0);
        statement.setPaid(true); // Mark the statement as paid
        creditCardStatementRepository.save(statement);

        walletService.updateWalletBalance(account.getUser().getId(), -amountDue);
    }

}
