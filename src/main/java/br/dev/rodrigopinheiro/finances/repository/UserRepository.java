package br.dev.rodrigopinheiro.finances.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

}
