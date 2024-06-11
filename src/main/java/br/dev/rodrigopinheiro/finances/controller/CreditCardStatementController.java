package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardStatementDto;
import br.dev.rodrigopinheiro.finances.service.CreditCardStatementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("/credit-card-statement")
public class CreditCardStatementController {

    private final CreditCardStatementService creditCardStatementService;
    public CreditCardStatementController(CreditCardStatementService creditCardStatementService) {
        this.creditCardStatementService = creditCardStatementService;
    }

    @PostMapping
    public ResponseEntity<CreditCardStatementDto> createCreditCardStatement(@RequestBody @Valid CreditCardStatementDto creditCardStatementDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(creditCardStatementService.create(creditCardStatementDto.toCreditCardStatement()));
    }

    @GetMapping
    public List<CreditCardStatementDto> getAllCreditCardStatements() {
        return creditCardStatementService.findAll();
    }

    @GetMapping("{id}")
    public CreditCardStatementDto getCreditCardStatement(@PathVariable("id") Long id) {
        return creditCardStatementService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<CreditCardStatementDto> updateCreditCardStatement(@PathVariable("id") Long id, @RequestBody @Valid CreditCardStatementDto creditCardStatementDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body( creditCardStatementService.update(id, creditCardStatementDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCreditCardStatement(@PathVariable("id") Long id) {
        creditCardStatementService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
