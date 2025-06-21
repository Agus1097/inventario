package com.invop.inventario.services;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.OrdenCompra;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.ProveedorArticuloRepository;
import com.invop.inventario.repositories.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;
    @Autowired
    private ArticuloRepository articuloRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private ProveedorArticuloRepository proveedorArticuloRepository;

    public List<OrdenCompra> findAll() {
        return ordenCompraRepository.findAll();
    }

    public OrdenCompra findById(Long id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden de compra no encontrada"));
    }

    @Transactional
    public OrdenCompra saveOrdenCompra(OrdenCompra ordenCompra) {
        Articulo articulo = articuloRepository.findById(ordenCompra.getArticulo().getId())
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));
        Proveedor proveedor = proveedorRepository.findById(ordenCompra.getProveedor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        // Verificar si ya existe una orden pendiente o enviada para este artículo
        boolean existeOrden = ordenCompraRepository.existsByArticuloAndEstadoIn(
                articulo, List.of("PENDIENTE", "ENVIADA")
        );
        if (existeOrden) {
            throw new IllegalArgumentException("Ya existe una orden de compra pendiente o enviada para este artículo.");
        }

        ordenCompra.setArticulo(articulo);
        ordenCompra.setProveedor(proveedor);
        ordenCompra.setFechaCreacionOrdenCompra(LocalDate.now());
        ordenCompra.setEstadoOrden(EstadoOrden.PENDIENTE);

        // Obtener ProveedorArticulo y calcular montoTotal
        ProveedorArticulo proveedorArticulo = proveedorArticuloRepository
                .findByArticuloAndProveedor(articulo, proveedor)
                .orElseThrow(() -> new EntityNotFoundException("No existe relación Proveedor-Articulo para este artículo y proveedor"));

        float montoTotal = ordenCompra.getCantidad() * proveedorArticulo.getPrecioUnitario();
        ordenCompra.setMontoTotal(montoTotal);

        return ordenCompraRepository.save(ordenCompra);
    }

    @Transactional
    public OrdenCompra updateOrdenCompra(Long id, OrdenCompra ordenCompraDetails) {
        OrdenCompra ordenCompra = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden de compra no encontrada"));

        EstadoOrden estadoActual = ordenCompra.getEstadoOrden();
        EstadoOrden nuevoEstado = ordenCompraDetails.getEstadoOrden();

        // Solo se puede editar la cantidad si la orden está en estado pendiente
        if (EstadoOrden.PENDIENTE.equals(estadoActual)) {
            if (ordenCompraDetails.getCantidad() != ordenCompra.getCantidad()) {
                // Recalcular montoTotal usando precioUnitario de ProveedorArticulo
                ProveedorArticulo proveedorArticulo = proveedorArticuloRepository
                        .findByArticuloAndProveedor(ordenCompra.getArticulo(), ordenCompra.getProveedor())
                        .orElseThrow(() -> new EntityNotFoundException("No existe relación Proveedor-Articulo para este artículo y proveedor"));
                ordenCompra.setCantidad(ordenCompraDetails.getCantidad());
                float montoTotal = ordenCompra.getCantidad() * proveedorArticulo.getPrecioUnitario();
                ordenCompra.setMontoTotal(montoTotal);
            }
            // Permitir pasar a estado cancelado
            if (EstadoOrden.CANCELADO.equals(nuevoEstado)) {
                ordenCompra.setEstadoOrden(EstadoOrden.CANCELADO);
            }
            // Permitir pasar a estado enviada
            else if (EstadoOrden.ENVIADO.equals(nuevoEstado)) {
                ordenCompra.setEstadoOrden(EstadoOrden.ENVIADO);
            }
        } else if (EstadoOrden.ENVIADO.equals(estadoActual)) {
            // No se puede modificar cantidad ni cancelar
            if (ordenCompraDetails.getCantidad() != ordenCompra.getCantidad()) {
                throw new IllegalArgumentException("No se puede modificar la cantidad de una orden enviada.");
            }
            if (EstadoOrden.CANCELADO.equals(nuevoEstado)) {
                throw new IllegalArgumentException("No se puede cancelar una orden enviada.");
            }
            // Permitir pasar a estado finalizada
            if (EstadoOrden.FINALIZADO.equals(nuevoEstado)) {
                ordenCompra.setEstadoOrden(EstadoOrden.FINALIZADO);
                // Actualizar stockActual del artículo
                Articulo articulo = articuloRepository.findById(ordenCompra.getArticulo().getId())
                                                    .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));
                articulo.setStockActual(articulo.getStockActual() + ordenCompra.getCantidad());
                articuloRepository.save(articulo);

                // Informar si el stockActual no supera el puntoPedido
                if (articulo.getStockActual() <= articulo.getPuntoPedido()) {
                    // Aquí puedes lanzar una excepción, retornar un mensaje, o registrar un aviso según tu arquitectura
                    // Ejemplo: lanzar excepción con mensaje informativo
                    throw new IllegalStateException("El stock actual del artículo sigue siendo igual o menor al punto de pedido.");
                }
            }
        } else if (EstadoOrden.FINALIZADO.equals(estadoActual) || EstadoOrden.CANCELADO.equals(estadoActual)) {
            throw new IllegalArgumentException("No se puede modificar una orden finalizada o cancelada.");
        }

        return ordenCompraRepository.save(ordenCompra);
    }

    @Transactional
    public void deleteById(Long id) {
        ordenCompraRepository.deleteById(id);
    }

    public List<OrdenCompra> findByArticulo(Long articuloId) {
        Articulo articulo = articuloRepository.findById(articuloId)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));
        return ordenCompraRepository.findByArticulo(articulo);
    }
}
