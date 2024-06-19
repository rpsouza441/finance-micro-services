package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardStatementDto;
import br.dev.rodrigopinheiro.finances.service.CreditCardStatementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/credit-card-statement")
public class CreditCardStatementController {

    private final CreditCardStatementService creditCardStatementService;

    public CreditCardStatementController(CreditCardStatementService creditCardStatementService) {
        this.creditCardStatementService = creditCardStatementService;
    }

    //Statement isn't created or updated by user interaction, is created by credit card transaction
//    @PostMapping
//    public ResponseEntity<CreditCardStatementDto> create(@RequestBody @Valid CreditCardStatementDto creditCardStatementDto) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(creditCardStatementService.create(creditCardStatementDto));
//    }
//    @PutMapping("{id}")
//    public ResponseEntity<CreditCardStatementDto> update(@PathVariable("id") Long id, @RequestBody @Valid CreditCardStatementDto creditCardStatementDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(creditCardStatementService.update(id, creditCardStatementDto));
//    }

    @GetMapping
    public List<CreditCardStatementDto> getAll() {
        return creditCardStatementService.findAll();
    }

    @GetMapping()
    public CreditCardStatementDto get(@RequestBody @Valid CreditCardStatementDto creditCardStatementDto) {
        return creditCardStatementService.findOrCreateStatementDto(creditCardStatementDto);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        creditCardStatementService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
