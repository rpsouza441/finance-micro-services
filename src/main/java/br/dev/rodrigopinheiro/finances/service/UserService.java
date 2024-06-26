package br.dev.rodrigopinheiro.finances.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import br.dev.rodrigopinheiro.finances.controller.dto.UserDto;
import br.dev.rodrigopinheiro.finances.entity.Wallet;
import br.dev.rodrigopinheiro.finances.exception.FinanceException;
import br.dev.rodrigopinheiro.finances.exception.UserNotFoundException;
import br.dev.rodrigopinheiro.finances.repository.WalletRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.finances.entity.User;
import br.dev.rodrigopinheiro.finances.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }


    public UserDto create(UserDto userDto) {

        //TODO
        // conferir implementacao de wallet com user
        var user= userDto.toUser();
        var newWallet = new Wallet(BigDecimal.ZERO, user);
        user.setWallet(newWallet);
        var userCreated = userRepository.save(user);
        newWallet.setUser(userCreated);
        walletRepository.save(newWallet);
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
        var updatedUser = userRepository.findById(id).map(existingUser -> {
            existingUser.setName(userDto.name());
            existingUser.setEmail(userDto.email());
            existingUser.setPassword(userDto.password());
            return userRepository.save(existingUser);

        }).orElseThrow(() -> new UserNotFoundException(id));
        return userDto.fromUser(updatedUser);
    }
}
