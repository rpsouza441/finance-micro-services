package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardDto;
import br.dev.rodrigopinheiro.finances.service.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/credit-card")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }


    @PostMapping
    public ResponseEntity<CreditCardDto> createCreditCard(@RequestBody @Valid CreditCardDto creditCardDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(creditCardService.create(creditCardDto.toCreditCard()));
    }

    @GetMapping
    public List<CreditCardDto> getAllCreditCards() {
        return creditCardService.findAll();
    }

    @GetMapping("{id}")
    public CreditCardDto getCreditCard(@PathVariable("id") Long id) {
        return creditCardService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<CreditCardDto> updateCreditCard(@PathVariable("id") Long id, @RequestBody @Valid CreditCardDto creditCardDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditCardService.update(id, creditCardDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCreditCard(@PathVariable("id") Long id) {
        creditCardService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
