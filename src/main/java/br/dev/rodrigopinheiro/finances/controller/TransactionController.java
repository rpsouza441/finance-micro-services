package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.TransferTransactionDto;
import br.dev.rodrigopinheiro.finances.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/credit")
    public ResponseEntity<TransactionDto> createCredit(@RequestBody @Valid TransactionDto transactionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.creditTransaction(transactionDto));
    }

    @PostMapping("/debit")
    public ResponseEntity<TransactionDto> createDebit(@RequestBody @Valid TransactionDto transactionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.debitTransaction(transactionDto));
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<TransactionDto>> createTransfer(@RequestBody @Valid TransferTransactionDto transferTransactionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transferBetweenAccounts(transferTransactionDto));
    }

    @GetMapping
    public List<TransactionDto> getAll() {
        return transactionService.findAll();
    }

    @GetMapping("{id}")
    public TransactionDto get(@PathVariable("id") Long id) {
        return transactionService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<TransactionDto> update(@PathVariable("id") Long id, @RequestBody @Valid TransactionDto transactionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.update(id, transactionDto));
    }

    @PutMapping("{id}")
    public ResponseEntity<TransactionDto> markEffective(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.markTransactionAsEffective(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        transactionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
