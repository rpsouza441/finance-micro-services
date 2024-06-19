package br.dev.rodrigopinheiro.finances.repository;

import br.dev.rodrigopinheiro.finances.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
