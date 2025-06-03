package com.invop.inventario.services;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.ProveedorRepository;
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
    @Autowired
    private ProveedorRepository proveedorRepository;

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

        //Para modelo Lote Fijo
        if (articulo.getTipoModelo().getId() == 1) {
            articulo.setLoteOptimo(articulo.calcularLoteOptimo());
            articulo.setPuntoPedido(articulo.calcularPuntoPedido());
            articulo.setStockSeguridad(articulo.calcularStockSeguridad());
        } else { //Para modelo Inrervalo Fijo
            articulo.setStockSeguridad(articulo.calcularStockSeguridad());
            articulo.setInventarioMaximo(articulo.calcularInventarioMaximo());
        }

        return articuloRepository.save(articulo);
    }

    public Optional<Articulo> findById(Long id) {
        return articuloRepository.findById(id);
    }

    public Articulo updateArticulo(Long id, Articulo articuloDetails) {
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado con id: " + id));

        if (articuloDetails.getDescripcion() == null) {
            throw new IllegalArgumentException("La descripción del artículo no puede estar vacía");
        }

        if (articuloDetails.getCodArticulo() == null) {
            throw new IllegalArgumentException("El codigo del articulo no puede estar vacío");
        }

        if (articuloDetails.getNombre() == null) {
            throw new IllegalArgumentException("El nombre del articulo no puede estar vacío");
        }

        articulo.setCodArticulo(articuloDetails.getCodArticulo());
        articulo.setNombre(articuloDetails.getNombre());
        articulo.setDescripcion(articuloDetails.getDescripcion());
        articulo.setDemandaArticulo(articuloDetails.getDemandaArticulo());
        articulo.setCostoAlmacenamiento(articuloDetails.getCostoAlmacenamiento());
        articulo.setCostoPedido(articuloDetails.getCostoPedido());
        articulo.setCostoCompra(articuloDetails.getCostoCompra());
        articulo.setStockActual(articuloDetails.getStockActual());
        articulo.setProveedorPredeterminado(articuloDetails.getProveedorPredeterminado());
        articulo.setTipoModelo(articuloDetails.getTipoModelo());

        articulo.setCgi(articulo.calcularCGI());

        //Para modelo Lote Fijo
        if (articulo.getTipoModelo().getId() == 1) {
            articulo.setLoteOptimo(articulo.calcularLoteOptimo());
            articulo.setPuntoPedido(articulo.calcularPuntoPedido());
            articulo.setStockSeguridad(articulo.calcularStockSeguridad());
        } else { //Para modelo Intervalo Fijo
            articulo.setStockSeguridad(articulo.calcularStockSeguridad());
            articulo.setInventarioMaximo(articulo.calcularInventarioMaximo());
        }

        return articuloRepository.save(articulo);
    }

    @Transactional
    public void deleteById(Long id) {
        Articulo a = articuloRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No se encontro el articulo"));
        if (a.getFechaBajaArticulo() != null) {
            throw new IllegalArgumentException("El artículo ya se encuentra desactivado");
        }
        if (a.getStockActual() != 0) {
            throw new IllegalArgumentException("El artículo tiene unidades en stock, no puede ser dado de baja");
        }
        a.setFechaBajaArticulo(LocalDate.now());
        articuloRepository.save(a);
    }

    @Transactional
    public Articulo setProveedorPredeterminado(Long articuloId, Long proveedorId) {
        Articulo articulo = articuloRepository.findById(articuloId)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado con id: " + articuloId));

        if (proveedorId == null) {
            articulo.setProveedorPredeterminado(null);
            return articuloRepository.save(articulo);
        }

        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con id: " + proveedorId));

        articulo.setProveedorPredeterminado(proveedor);
        return articuloRepository.save(articulo);
    }




//    @Transactional
//    public void activateById(Long id) {
//        Articulo a = articuloRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No se encontro el articulo"));
//        if (a.getFechaBajaArticulo() == null) {
//            throw new IllegalArgumentException("El artículo ya se encuentra activado");
//        }
//        a.setFechaBajaArticulo(null);
//        articuloRepository.save(a);
//    }
}
