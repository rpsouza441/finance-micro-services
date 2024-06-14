package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.exception.TransactionNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final BankAccountService bankAccountService;
    private final CategoryService categoryService;

    public TransactionService(WalletService walletService, TransactionRepository transactionRepository,
                              BankAccountService bankAccountService, CategoryService categoryService) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
        this.categoryService = categoryService;
    }

    public void markTransactionAsEffective(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        if (!transaction.isEffective()) {
            transaction.setEffective(true);
            transactionRepository.save(transaction);
            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                walletService.creditWalletBalance(
                        transaction.getBankAccount().getUser().getId(),
                        transaction.getAmount());
            } else {
                walletService.debitWalletBalance(
                        transaction.getBankAccount().getUser().getId(),
                        transaction.getAmount());
            }

        }
    }

    public Transaction findTransaction(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }


    public TransactionDto create(Transaction transaction) {
        var transactionCreated = transactionRepository.save(transaction);
        return TransactionDto.fromTransaction(transaction);

    }

    public List<TransactionDto> findAll() {

        List<Transaction> categories = transactionRepository.findAll();

        return categories.stream()
                .map(TransactionDto::fromTransaction)
                .collect(Collectors.toList());
    }

    public TransactionDto findById(Long id) {
        var transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));

        return TransactionDto.fromTransaction(transaction);


    }

    public void delete(Long id) {
        try {
            transactionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new TransactionNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }

    public TransactionDto update(Long id, TransactionDto transactionDto) {
        transactionRepository.findById(id).ifPresentOrElse((existingTransaction) -> {

            var bankAccount = bankAccountService.findByIdBankAccount(transactionDto.bankAccountId());
            var category= categoryService.findCategory(transactionDto.categoryId());


            if (!transactionDto.description().isEmpty()){existingTransaction.setDescription(transactionDto.description());}
            if (!transactionDto.note().isEmpty()){existingTransaction.setDescription(transactionDto.note());}
            existingTransaction.setAmount(transactionDto.amount());
            if (transactionDto.interest()!=null){existingTransaction.setInterest(transactionDto.interest());}
            if (transactionDto.discount()!=null){existingTransaction.setDiscount(transactionDto.discount());}
            existingTransaction.setTransactionType(transactionDto.transactionType());
            existingTransaction.setRecurrent(transactionDto.isRecurrent());
            existingTransaction.setEffective(transactionDto.isEffective());
            if(transactionDto.creationDate()!=null){existingTransaction.setCreationDate(transactionDto.creationDate());}
            existingTransaction.setDueDate(transactionDto.dueDate());
            if(transactionDto.effectivedDate()!= null){existingTransaction.setEffectivedDate(transactionDto.effectivedDate());}
            existingTransaction.setBankAccount(bankAccount);
            existingTransaction.setCategory(category);


            transactionRepository.save(existingTransaction);
        }, () -> {
            throw new TransactionNotFoundException(id);
        });
        return transactionDto;
    }

}
