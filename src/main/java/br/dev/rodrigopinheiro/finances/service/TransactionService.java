package br.dev.rodrigopinheiro.finances.service;

import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.exception.TransactionNotFoundException;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.Transaction;
import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;
import br.dev.rodrigopinheiro.finances.exception.TransactionNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    public TransactionService(WalletService walletService, TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
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
        return new TransactionDto(transactionCreated.getBankAccount().getId(),
                transactionCreated.getAmount(), transactionCreated.isEffective());

    }

    public List<TransactionDto> findAll() {

        List<Transaction> categories = transactionRepository.findAll();

        return categories.stream()
                .map(transaction -> new TransactionDto(transaction.getBankAccount().getId(),
                        transaction.getAmount(), transaction.isEffective()))
                .collect(Collectors.toList());
    }

    public TransactionDto findById(Long id) {
        var transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));

        return new TransactionDto(transaction.getBankAccount().getId(),
                transaction.getAmount(), transaction.isEffective());
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

            //TODO
            // Finalizar a DTO ou criar uma para cada ocasiao.
            existingTransaction.setAmount(transactionDto.amount());
            existingTransaction.setEffective(transactionDto.isEffective());

            transactionRepository.save(existingTransaction);
        }, () -> {
            throw new TransactionNotFoundException(id);
        });
        return transactionDto;
    }

}
