package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.BankAccountDto;
import br.dev.rodrigopinheiro.finances.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("/bank-account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountDto> create(@RequestBody @Valid BankAccountDto bankAccountDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bankAccountService.create(bankAccountDto.toBankAccount()));
    }

    @GetMapping
    public List<BankAccountDto> getAll() {
        return bankAccountService.findAll();
    }

    @GetMapping("{id}")
    public BankAccountDto get(@PathVariable("id") Long id) {
        return bankAccountService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<BankAccountDto> update(@PathVariable("id") Long id, @RequestBody @Valid BankAccountDto bankAccountDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body( bankAccountService.update(id, bankAccountDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        bankAccountService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
