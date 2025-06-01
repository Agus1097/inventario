package com.invop.inventario.services;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.repositories.ArticuloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ArticuloService {
    @Autowired
    private ArticuloRepository articuloRepository;

    public List<Articulo> findAll() {
        return articuloRepository.findAll();
    }

    @Transactional
    public Articulo saveArticulo(Articulo articulo) {
        if (articulo.getDescripcion() == null) {
            throw new IllegalArgumentException("La descripción del artículo no puede estar vacía");
        }

        if (articulo.getCodArticulo() == null) {
            throw new IllegalArgumentException("El codigo del articulo no puede estar vacío");
        }

        if (articulo.getNombre() == null) {
            throw new IllegalArgumentException("El nombre del articulo no puede estar vacío");
        }

        if (articuloRepository.existsByCodArticulo(articulo.getCodArticulo())) {
            throw new IllegalArgumentException("El artículo ya existe");
        }

        return articuloRepository.save(articulo);
    }

    public Optional<Articulo> findById(Long id) {
        return articuloRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        Articulo a = articuloRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No se encontro el articulo"));
        a.setFechaBajaArticulo(LocalDate.now());
        articuloRepository.save(a);
    }
}
