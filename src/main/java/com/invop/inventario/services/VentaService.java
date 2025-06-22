package com.invop.inventario.services;

import com.invop.inventario.dto.CrearVentaDTO;
import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.OrdenCompra;
import com.invop.inventario.entities.ProveedorArticulo;
import com.invop.inventario.entities.TipoModelo;
import com.invop.inventario.entities.Venta;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.VentaRepository;
import com.invop.inventario.repositories.ProveedorArticuloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.time.LocalDate;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private ArticuloRepository articuloRepository;
    @Autowired
    private OrdenCompraRepository ordenCompraRepository;
    @Autowired
    private ProveedorArticuloRepository proveedorArticuloRepository;

    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    public Venta findById(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
    }

    @Transactional
    public Venta saveVenta(CrearVentaDTO dto) {
        System.out.println("DTO recibido: " + dto);
        Articulo articulo = articuloRepository.findById(dto.getArticuloId())
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        if (dto.getCantidad() > articulo.getStockActual()) {
            throw new IllegalArgumentException("No hay suficiente stock para realizar la venta");
        }

        // Actualizar stock
        articulo.setStockActual(articulo.getStockActual() - dto.getCantidad());
        articuloRepository.save(articulo);

        // Crear entidad Venta
        Venta venta = new Venta();
        venta.setArticulo(articulo);
        venta.setCantidad(dto.getCantidad());
        venta.setMontoTotal(dto.getCantidad() * articulo.getCostoVenta());
        venta.setFechaVenta(new Date());

        // Lógica de generación de orden (si aplica)
        if (articulo.getProveedorPredeterminado() != null) {
            ProveedorArticulo proveedorArticulo = proveedorArticuloRepository
                    .findByArticuloAndProveedor(articulo, articulo.getProveedorPredeterminado())
                    .orElseThrow(() -> new EntityNotFoundException("No existe relación Proveedor-Articulo para este artículo y proveedor"));

            boolean esLoteFijo = proveedorArticulo.getTipoModelo() != null &&
                    proveedorArticulo.getTipoModelo().getId().equals(TipoModelo.LOTE_FIJO.getId());

            boolean stockBajo = articulo.getStockActual() <= articulo.getPuntoPedido();

            boolean yaTieneOrden = ordenCompraRepository.existsByArticuloAndEstadoOrdenIn(
                    articulo,
                    List.of(EstadoOrden.PENDIENTE, EstadoOrden.ENVIADO)
            );

            if (esLoteFijo && stockBajo && !yaTieneOrden) {
                OrdenCompra orden = new OrdenCompra();
                orden.setArticulo(articulo);
                orden.setProveedor(articulo.getProveedorPredeterminado());
                orden.setCantidad(articulo.getLoteOptimo());
                orden.setEstadoOrden(EstadoOrden.PENDIENTE);
                orden.setFechaCreacionOrdenCompra(LocalDate.now());
                orden.setMontoTotal(articulo.getLoteOptimo() * proveedorArticulo.getPrecioUnitario());

                ordenCompraRepository.save(orden);
            }
        }

        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta updateVenta(Long id, Venta ventaDetails) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));

        Articulo articulo = articuloRepository.findById(ventaDetails.getArticulo().getId())
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        venta.setArticulo(articulo); // Asegura que el artículo es el correcto de la base de datos
        venta.setCantidad(venta.getCantidad()); // Ya viene del request, pero puedes dejarlo explícito
        venta.setMontoTotal(ventaDetails.getMontoTotal());
        // No se recomienda cambiar el artículo ni la fecha de venta en una actualización normal

        return ventaRepository.save(venta);
    }

    @Transactional
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }
}
