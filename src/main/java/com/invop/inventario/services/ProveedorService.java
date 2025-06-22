package com.invop.inventario.services;

import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import com.invop.inventario.mappers.ProveedorMapper;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ArticuloRepository articuloRepository;
    private final ProveedorMapper proveedorMapper;
    private final ArticuloService articuloService;

    public List<Proveedor> findAll() {
        return proveedorRepository.findByFechaBajaProveedorIsNull();
    }

    public Proveedor findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));
        if (proveedor.getFechaBajaProveedor() != null) {
            throw new IllegalArgumentException("El proveedor está dado de baja.");
        }
        return proveedor;
    }

    @Transactional
    public Proveedor saveProveedor(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = proveedorMapper.toEntity(proveedorDTO);

        for (ProveedorArticulo pa : proveedor.getProveedorArticulos()) {
            Articulo articulo = articuloService.findById(pa.getArticulo().getId());

            if (Objects.isNull(articulo.getProveedorPredeterminado())) {
                articulo.setProveedorPredeterminado(proveedor);
                articulo.calcularStockSeguridad(pa.getDemoraEntrega(), pa.getTiempoRevision(), pa.getTipoModelo());
                articulo.calcularLoteOptimo(pa.getCargosPedido(), pa.getTipoModelo(), pa.getDemoraEntrega(), pa.getTiempoRevision());
                articulo.calcularPuntoPedido(pa.getDemoraEntrega());
                articulo.calcularInventarioMaximo();
                articulo.calcularCGI(pa.getPrecioUnitario(), pa.getCargosPedido());
            }
        }

        // Guardar proveedor y sus ProveedorArticulo en cascada
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public Proveedor updateProveedor(Long id, Proveedor proveedorDetails) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        proveedor.setNombre(proveedorDetails.getNombre());

        // if (proveedorDetails.getProveedorArticulos() == null || proveedorDetails.getProveedorArticulos().isEmpty()) {
        //     throw new IllegalArgumentException("Debe asociar al menos un ProveedorArticulo al proveedor.");
        // }

        // Actualizar o agregar ProveedorArticulo según corresponda
        for (ProveedorArticulo paNuevo : proveedorDetails.getProveedorArticulos()) {
            if (paNuevo.getArticulo() == null) {
                throw new IllegalArgumentException("Cada ProveedorArticulo debe estar asociado a un Articulo.");
            }
            if (paNuevo.getTipoModelo() == null) {
                throw new IllegalArgumentException("Cada ProveedorArticulo debe tener un TipoModelo.");
            }
            if (paNuevo.getDemoraEntrega() <= 0) {
                throw new IllegalArgumentException("La demora de entrega debe ser mayor a 0.");
            }
            if (paNuevo.getPrecioUnitario() <= 0) {
                throw new IllegalArgumentException("El precio unitario debe ser mayor a 0.");
            }
            if (paNuevo.getCargosPedido() < 0) {
                throw new IllegalArgumentException("Los cargos de pedido no pueden ser negativos.");
            }
            if (paNuevo.getTiempoRevision() <= 0) {
                throw new IllegalArgumentException("El tiempo de revisión debe ser mayor a 0.");
            }

            // Buscar si ya existe un ProveedorArticulo para ese Articulo
            ProveedorArticulo existente = proveedor.getProveedorArticulos().stream()
                    .filter(pa -> pa.getArticulo().getId().equals(paNuevo.getArticulo().getId()))
                    .findFirst()
                    .orElse(null);

            if (existente != null) {
                // Actualizar los valores del existente
                existente.setDemoraEntrega(paNuevo.getDemoraEntrega());
                existente.setPrecioUnitario(paNuevo.getPrecioUnitario());
                existente.setCargosPedido(paNuevo.getCargosPedido());
                existente.setTiempoRevision(paNuevo.getTiempoRevision());
                existente.setTipoModelo(paNuevo.getTipoModelo());
            } else {
                // Agregar el nuevo ProveedorArticulo
                proveedor.getProveedorArticulos().add(paNuevo);
            }

            // Setear proveedorPredeterminado si corresponde
            if (paNuevo.getArticulo().getProveedorPredeterminado() == null) {
                paNuevo.getArticulo().setProveedorPredeterminado(proveedor);
            }
        }

        // Eliminar ProveedorArticulo que ya no estén en la nueva lista
        proveedor.getProveedorArticulos().removeIf(paExistente ->
                proveedorDetails.getProveedorArticulos().stream()
                        .noneMatch(paNuevo -> paNuevo.getArticulo().getId().equals(paExistente.getArticulo().getId()))
        );

        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public void deleteById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        // Verificar que no tenga órdenes de compra en estado pendiente o enviada
        boolean tieneOrdenes = ordenCompraRepository.existsByProveedorAndEstadoOrdenIn(
                proveedor, List.of(EstadoOrden.PENDIENTE, EstadoOrden.ENVIADO)
        );
        if (tieneOrdenes) {
            throw new IllegalArgumentException("El proveedor tiene órdenes de compra pendientes o enviadas y no puede ser dado de baja.");
        }

        // Verificar que no sea proveedorPredeterminado en ningún artículo
        boolean esPredeterminado = articuloRepository.existsByProveedorPredeterminado(proveedor);
        if (esPredeterminado) {
            throw new IllegalArgumentException("El proveedor está seteado como proveedor predeterminado en algún artículo y no puede ser dado de baja.");
        }

        // Setear fecha de baja
        proveedor.setFechaBajaProveedor(java.time.LocalDate.now());
        proveedorRepository.save(proveedor);
    }

    public List<Map<String, Object>> getArticulosPorProveedor(Long proveedorId) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (ProveedorArticulo pa : proveedor.getProveedorArticulos()) {
            Articulo articulo = pa.getArticulo();
            boolean esPredeterminado = proveedor.equals(articulo.getProveedorPredeterminado());

            Map<String, Object> info = new HashMap<>();
            // Datos del artículo
            info.put("articuloId", articulo.getId());
            info.put("nombreArticulo", articulo.getNombre());
            info.put("descripcionArticulo", articulo.getDescripcion());
            // Datos de ProveedorArticulo
            info.put("demoraEntrega", pa.getDemoraEntrega());
            info.put("precioUnitario", pa.getPrecioUnitario());
            info.put("cargosPedido", pa.getCargosPedido());
            info.put("tiempoRevision", pa.getTiempoRevision());
            info.put("tipoModelo", pa.getTipoModelo() != null ? pa.getTipoModelo().getNombre() : null);
            // Datos del proveedor
            info.put("proveedorId", proveedor.getId());
            info.put("nombreProveedor", proveedor.getNombre());
            // Si es predeterminado
            info.put("esPredeterminado", esPredeterminado);

            resultado.add(info);
        }
        return resultado;
    }

    private void calcularProveedorPredeterminado(){

    }
}
