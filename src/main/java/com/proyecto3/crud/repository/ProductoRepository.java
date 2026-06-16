package com.proyecto3.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto3.crud.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCategoriaId(Long categoriaId);
}
