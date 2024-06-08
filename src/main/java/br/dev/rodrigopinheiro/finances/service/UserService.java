package br.dev.rodrigopinheiro.finances.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import br.dev.rodrigopinheiro.finances.controller.UserController;
import br.dev.rodrigopinheiro.finances.controller.dto.UserDto;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.UserNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.User;
import br.dev.rodrigopinheiro.finances.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserController user;

    public UserService(UserRepository userRepository, UserController user) {
        this.userRepository = userRepository;
        this.user = user;
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }


    public UserDto createUser(User user) {
        var userCreated = userRepository.save(user);
        return new UserDto(userCreated.getName(), userCreated.getEmail(), userCreated.getPassword());

    }

    public List<UserDto> findAll() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserDto(user.getName(), user.getEmail(), user.getPassword()))
                .collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return new UserDto(user.getName(), user.getEmail(), user.getPassword());
    }

    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(id);
        } catch (Exception e) {
            throw new FinanceException();
        }
    }

    public UserDto update(Long id, UserDto userDto) {
        userRepository.findById(id).ifPresentOrElse((existingUser) -> {
            var user = userDto.toUser();
            user.setEmail(userDto.email());
            user.setPassword(userDto.password());
            user.setName(userDto.nome());
            userRepository.save(user);
        }, () -> {
            throw new UserNotFoundException(id);
        });
        return userDto;
    }
}
