package com.invop.inventario.services;

import com.invop.inventario.dto.CrearOrdenCompraDTO;
import com.invop.inventario.dto.OrdenCompraDTO;
import com.invop.inventario.entities.*;
import com.invop.inventario.mappers.OrdenCompraMapper;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.ProveedorArticuloRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class OrdenCompraService {

    private OrdenCompraRepository ordenCompraRepository;
    private ArticuloService articuloService;
    private ProveedorService proveedorService;
    private ProveedorArticuloRepository proveedorArticuloRepository;
    private OrdenCompraMapper ordenCompraMapper;

    public List<OrdenCompraDTO> findAll() {
        return ordenCompraMapper.toDtoList(ordenCompraRepository.findAll());
    }

    public OrdenCompra findById(Long id) {
        return ordenCompraRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Orden de compra no encontrada"));
    }

    public OrdenCompraDTO getOrdenCompraById(Long id) {
        OrdenCompra ordenCompra = findById(id);
        return ordenCompraMapper.toDto(ordenCompra);
    }

    @Transactional
    public OrdenCompraDTO saveOrdenCompra(CrearOrdenCompraDTO dto) {
        Articulo articulo = articuloService.findById(dto.getArticuloId());
        Proveedor proveedor = proveedorService.findById(dto.getProveedorId());

        // Verificar si ya existe una orden pendiente o enviada para este artículo
        boolean existeOrden = ordenCompraRepository.existsByArticuloAndEstadoOrdenIn(
                articulo,
                List.of(EstadoOrden.PENDIENTE, EstadoOrden.ENVIADO));
        if (existeOrden) {
            throw new IllegalArgumentException("Ya existe una orden de compra pendiente o enviada para este artículo.");
        }

        OrdenCompra ordenCompra = new OrdenCompra();

        ordenCompra.setCantidad(dto.getCantidad());
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

        return ordenCompraMapper.toDto(ordenCompraRepository.save(ordenCompra));
    }

    @Transactional
    public OrdenCompraDTO updateOrdenCompra(Long id, OrdenCompra ordenCompraDetails) {
        OrdenCompra ordenCompra = findById(id);

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
            if (EstadoOrden.CANCELADO.equals(nuevoEstado)) {
                throw new IllegalArgumentException("No se puede cancelar una orden enviada.");
            }
            // Permitir pasar a estado finalizada
            if (EstadoOrden.FINALIZADO.equals(nuevoEstado)) {
                ordenCompra.setEstadoOrden(EstadoOrden.FINALIZADO);
                // Actualizar stockActual del artículo
                Articulo articulo = articuloService.findById(ordenCompra.getArticulo().getId());
                articulo.setStockActual(articulo.getStockActual() + ordenCompra.getCantidad());
                articuloService.saveUpdate(articulo);

                // Informar si el stockActual no supera el puntoPedido
            }
        } else if (EstadoOrden.FINALIZADO.equals(estadoActual) || EstadoOrden.CANCELADO.equals(estadoActual)) {
            throw new IllegalArgumentException("No se puede modificar una orden finalizada o cancelada.");
        }

        return ordenCompraMapper.toDto(ordenCompraRepository.save(ordenCompra));
    }

    @Transactional
    public void deleteById(Long id) {
        ordenCompraRepository.deleteById(id);
    }

    public List<OrdenCompraDTO> findByArticulo(Long articuloId) {
        Articulo articulo = articuloService.findById(articuloId);
        return ordenCompraMapper.toDtoList(ordenCompraRepository.findByArticulo(articulo));
    }
}
