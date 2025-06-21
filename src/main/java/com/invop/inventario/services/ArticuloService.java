package com.invop.inventario.services;


import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.ProveedorRepository;
import com.invop.inventario.repositories.ProveedorArticuloRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ArticuloService {
    @Autowired
    private ArticuloRepository articuloRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private ProveedorArticuloRepository proveedorArticuloRepository;
    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    public Page<Articulo> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return articuloRepository.findByFechaBajaArticuloIsNull(pageable);
    }

    @Transactional
    public Articulo saveArticulo(Articulo articulo) {
        if (articulo.getDescripcion() == null || articulo.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del artículo no puede estar vacía");
        }
        if (articulo.getCodArticulo() == null) {
            throw new IllegalArgumentException("El código del artículo no puede estar vacío");
        }
        if (articulo.getNombre() == null || articulo.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del artículo no puede estar vacío");
        }
        if (articuloRepository.existsByCodArticulo(articulo.getCodArticulo())) {
            throw new IllegalArgumentException("El artículo ya existe");
        }
        return articuloRepository.save(articulo);
    }

    public Articulo findById(Long id) {
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El artículo no existe"));
        if (articulo.getFechaBajaArticulo() != null) {
            throw new IllegalArgumentException("El artículo está dado de baja.");
        }
        return articulo;
    }

    @Transactional
    public Articulo updateArticulo(Long id, Articulo articuloDetails) {
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado con id: " + id));

        if (articuloDetails.getDescripcion() == null || articuloDetails.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del artículo no puede estar vacía");
        }
        if (articuloDetails.getCodArticulo() == null) {
            throw new IllegalArgumentException("El código del artículo no puede estar vacío");
        }
        if (articuloDetails.getNombre() == null || articuloDetails.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del artículo no puede estar vacío");
        }

        articulo.setCodArticulo(articuloDetails.getCodArticulo());
        articulo.setNombre(articuloDetails.getNombre());
        articulo.setDescripcion(articuloDetails.getDescripcion());
        articulo.setDemandaArticulo(articuloDetails.getDemandaArticulo());
        articulo.setCostoAlmacenamiento(articuloDetails.getCostoAlmacenamiento());
        articulo.setCostoVenta(articuloDetails.getCostoVenta());
        articulo.setStockActual(articuloDetails.getStockActual());
        articulo.setProveedorPredeterminado(articuloDetails.getProveedorPredeterminado());
        articulo.setProduccionDiaria(articuloDetails.getProduccionDiaria());
        articulo.setZ(articuloDetails.getZ());
        articulo.setDesviacionEstandar(articuloDetails.getDesviacionEstandar());

        // Obtener datos específicos de ProveedorArticulo
        Proveedor proveedor = articulo.getProveedorPredeterminado();
        int demoraEntrega = 0;
        float tiempoRevision = 0f;
        float precioUnitario = 0f;
        float cargosPedido = 0f;
        Long modeloInventario = 0L;

        if (proveedor != null) {
            ProveedorArticulo proveedorArticulo = proveedorArticuloRepository
                    .findByArticuloAndProveedor(articulo, proveedor)
                    .orElseThrow(() -> new EntityNotFoundException("No existe relación Proveedor-Articulo para este artículo y proveedor"));

            demoraEntrega = proveedorArticulo.getDemoraEntrega();
            cargosPedido = proveedorArticulo.getCargosPedido();
            tiempoRevision = proveedorArticulo.getTiempoRevision();
            precioUnitario = proveedorArticulo.getPrecioUnitario();
            modeloInventario = proveedorArticulo.getTipoModelo().getId();
        }

        // Recalcular campos derivados usando los datos de ProveedorArticulo
        articulo.calcularLoteOptimo(cargosPedido, modeloInventario, demoraEntrega, tiempoRevision);
        articulo.calcularStockSeguridad(demoraEntrega, tiempoRevision, modeloInventario);
        articulo.calcularPuntoPedido(demoraEntrega);
        articulo.calcularInventarioMaximo();
        articulo.calcularCGI(precioUnitario, cargosPedido);

        return articuloRepository.save(articulo);
    }

    @Transactional
    public void deleteById(Long id) {
        Articulo a = articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el artículo"));
        if (a.getFechaBajaArticulo() != null) {
            throw new IllegalArgumentException("El artículo ya se encuentra desactivado");
        }
        if (a.getStockActual() != 0) {
            throw new IllegalArgumentException("El artículo tiene unidades en stock, no puede ser dado de baja");
        }
        // Verificar órdenes de compra pendientes o enviadas
        boolean tieneOrdenesPendientesOEnviadas = ordenCompraRepository
                .existsByArticuloAndEstadoIn(a, List.of("PENDIENTE", "ENVIADO"));
        if (tieneOrdenesPendientesOEnviadas) {
            throw new IllegalArgumentException("El artículo tiene órdenes de compra pendientes o enviadas y no puede ser dado de baja");
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

    public List<Articulo> getArticulosAReponer() {
        return articuloRepository.findArticulosAReponer();
    }

    public List<Articulo> getArticulosFaltantes() {
        // Devuelve todos los artículos cuyo stockActual <= stockSeguridad
        return articuloRepository.findAll().stream()
                .filter(a -> a.getStockActual() <= a.getStockSeguridad())
                .toList();
    }
}
