package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.TransactionDto;
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

    @PostMapping
    public ResponseEntity<TransactionDto> create(@RequestBody @Valid TransactionDto transactionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.create(transactionDto.toTransaction()));
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
        return ResponseEntity.status(HttpStatus.CREATED).body( transactionService.update(id, transactionDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        transactionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
