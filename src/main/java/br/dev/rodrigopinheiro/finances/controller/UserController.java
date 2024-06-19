package br.dev.rodrigopinheiro.finances.controller;

import br.dev.rodrigopinheiro.finances.controller.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.dev.rodrigopinheiro.finances.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(userDto));
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll();
    }

    @GetMapping("{id}")
    public UserDto get(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long id, @RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.update(id, userDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
