package br.dev.rodrigopinheiro.finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
