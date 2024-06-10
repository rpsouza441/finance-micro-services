package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.User;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        @NotNull String name,
        @NotNull String email,
        @NotNull String password
){

    public User toUser() {
        return  new User(name, email, password);
    }
}
