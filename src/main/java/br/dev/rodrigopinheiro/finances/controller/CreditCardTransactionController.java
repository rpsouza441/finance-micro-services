package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardTransactionDto;
import br.dev.rodrigopinheiro.finances.controller.dto.InstallmentDto;
import br.dev.rodrigopinheiro.finances.controller.dto.RefundStatementDto;
import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
import br.dev.rodrigopinheiro.finances.service.CreditCardTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit-card-transaction")
public class CreditCardTransactionController {

    private final CreditCardTransactionService creditCardTransactionService;

    public CreditCardTransactionController(CreditCardTransactionService creditCardTransactionService) {
        this.creditCardTransactionService = creditCardTransactionService;
    }


    @PostMapping
    public ResponseEntity<List<CreditCardTransactionDto>> create(@RequestBody @Valid InstallmentDto installmentDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(creditCardTransactionService.addCreditCardTransaction(installmentDto));
    }

    @GetMapping
    public List<CreditCardTransactionDto> getAll() {
        return creditCardTransactionService.findAll();
    }

    @GetMapping("{id}")
    public CreditCardTransactionDto get(@PathVariable("id") Long id) {
        return creditCardTransactionService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<List<CreditCardTransactionDto>> refundTransactions(@PathVariable("id") String installmentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditCardTransactionService.refundCreditCardTransaction(installmentId));
    }

    @PutMapping("{id}")
    public ResponseEntity<CreditCardTransactionDto> update(@PathVariable("id") Long id,
                                                           @RequestBody @Valid CreditCardTransactionDto creditCardTransactionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditCardTransactionService.update(id, creditCardTransactionDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        creditCardTransactionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
