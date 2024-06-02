package br.dev.rodrigopinheiro.finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.finances.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
