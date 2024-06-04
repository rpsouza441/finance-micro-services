package br.dev.rodrigopinheiro.finances.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.User;
import br.dev.rodrigopinheiro.finances.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found for id: " + id));
    }

}
