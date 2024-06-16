package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.User;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record UserDto(
        @NotNull String name,
        @NotNull String email,
        @NotNull String password
){

    public User toUser() {
        return  new User(name, email, password);
    }

    public UserDto fromUser(User updatedUser) {
        return new UserDto(updatedUser.getName(), updatedUser.getEmail(), updatedUser.getPassword());
    }
}
