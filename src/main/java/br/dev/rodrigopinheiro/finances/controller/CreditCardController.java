package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.CreditCardDto;
import br.dev.rodrigopinheiro.finances.service.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit-card")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }


    @PostMapping
    public ResponseEntity<CreditCardDto> create(@RequestBody @Valid CreditCardDto creditCardDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(creditCardService.create(creditCardDto));
    }

    @GetMapping
    public List<CreditCardDto> getAll() {
        return creditCardService.findAll();
    }

    @GetMapping("{id}")
    public CreditCardDto get(@PathVariable("id") Long id) {
        return creditCardService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<CreditCardDto> update(@PathVariable("id") Long id, @RequestBody @Valid CreditCardDto creditCardDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditCardService.update(id, creditCardDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        creditCardService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
