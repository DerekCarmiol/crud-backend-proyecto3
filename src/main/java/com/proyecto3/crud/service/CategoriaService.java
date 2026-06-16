package com.proyecto3.crud.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto3.crud.dto.CategoriaRequest;
import com.proyecto3.crud.entity.Categoria;
import com.proyecto3.crud.exception.BusinessException;
import com.proyecto3.crud.exception.ResourceNotFoundException;
import com.proyecto3.crud.repository.CategoriaRepository;
import com.proyecto3.crud.repository.ProductoRepository;

/**
 * Lógica de negocio del CRUD de categorías.
 */
@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con id: " + id));
    }

    public Categoria crear(CategoriaRequest request) {
        Categoria categoria = new Categoria(request.getNombre(), request.getDescripcion());
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = obtenerPorId(id);
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        Categoria categoria = obtenerPorId(id);
        if (productoRepository.existsByCategoriaId(id)) {
            throw new BusinessException(
                    "No se puede eliminar la categoría porque tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
    }
}
