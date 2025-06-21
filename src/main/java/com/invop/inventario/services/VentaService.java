package com.invop.inventario.services;

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
    public Venta saveVenta(Venta venta) {
        Articulo articulo = articuloRepository.findById(venta.getArticulo().getId())
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        // Validar stock suficiente
        if (venta.getCantidad() > articulo.getStockActual()) {
            throw new IllegalArgumentException("No hay suficiente stock para realizar la venta");
        }

        // Actualizar stock
        articulo.setStockActual(articulo.getStockActual() - venta.getCantidad());
        articuloRepository.save(articulo);

        // Calcular y setear montoTotal de la venta
        float montoVenta = venta.getCantidad() * articulo.getCostoVenta();
        venta.setMontoTotal(montoVenta);

        // Obtener ProveedorArticulo para el proveedor predeterminado
        if (articulo.getProveedorPredeterminado() != null) {
            ProveedorArticulo proveedorArticulo = proveedorArticuloRepository
                    .findByArticuloAndProveedor(articulo, articulo.getProveedorPredeterminado())
                    .orElseThrow(() -> new EntityNotFoundException("No existe relación Proveedor-Articulo para este artículo y proveedor"));

            boolean esLoteFijo = proveedorArticulo.getTipoModelo() != null &&
                    proveedorArticulo.getTipoModelo().getId().equals(TipoModelo.LOTE_FIJO.getId());
            boolean stockEsMenorOIgualPuntoPedido = articulo.getStockActual() <= articulo.getPuntoPedido();

            boolean tieneOrdenPendienteOEnviada = ordenCompraRepository
                    .existsByArticuloAndEstadoIn(articulo, List.of("PENDIENTE", "ENVIADA"));

            if (esLoteFijo && stockEsMenorOIgualPuntoPedido && !tieneOrdenPendienteOEnviada) {
                OrdenCompra orden = new OrdenCompra();
                orden.setArticulo(articulo);
                orden.setProveedor(articulo.getProveedorPredeterminado());
                orden.setCantidad(articulo.getLoteOptimo());
                orden.setEstadoOrden(EstadoOrden.PENDIENTE);
                orden.setFechaCreacionOrdenCompra(LocalDate.now());
                // Calcular y setear montoTotal de la orden de compra usando precioUnitario de ProveedorArticulo
                float montoOrden = articulo.getLoteOptimo() * proveedorArticulo.getPrecioUnitario();
                orden.setMontoTotal(montoOrden);

                ordenCompraRepository.save(orden);
            }
        }

        venta.setArticulo(articulo);
        venta.setFechaVenta(new Date());
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
