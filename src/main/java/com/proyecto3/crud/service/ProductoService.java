package com.proyecto3.crud.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto3.crud.dto.ProductoRequest;
import com.proyecto3.crud.entity.Categoria;
import com.proyecto3.crud.entity.Producto;
import com.proyecto3.crud.exception.ResourceNotFoundException;
import com.proyecto3.crud.repository.CategoriaRepository;
import com.proyecto3.crud.repository.ProductoRepository;

/**
 * Lógica de negocio del CRUD de productos.
 */
@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con id: " + id));
    }

    public Producto crear(ProductoRequest request) {
        Producto producto = new Producto();
        aplicarDatos(producto, request);
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerPorId(id);
        aplicarDatos(producto, request);
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);
        productoRepository.delete(producto);
    }

    /**
     * Copia los datos del request al producto y resuelve la categoría asociada.
     */
    private void aplicarDatos(Producto producto, ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con id: " + request.getCategoriaId()));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCantidadEnStock(request.getCantidadEnStock());
        producto.setCategoria(categoria);
    }
}
