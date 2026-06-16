package com.proyecto3.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto3.crud.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
