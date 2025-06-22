package com.invop.inventario.services;

import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.ArticuloDatoDTO;
import com.invop.inventario.dto.EditarArticuloDTO;
import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import com.invop.inventario.mappers.ArticuloMapper;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.ProveedorArticuloRepository;
import com.invop.inventario.repositories.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProveedorArticuloRepository proveedorArticuloRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ArticuloMapper articuloMapper;

    public Page<Articulo> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return articuloRepository.findByFechaBajaArticuloIsNull(pageable);
    }

    public List<ArticuloDatoDTO> getAllArticuloDatoDTO() {
        List<Articulo> articulos = articuloRepository.findAll();
        return articuloMapper.toDtoList(articulos);
    }

    @Transactional
    public Articulo saveArticulo(@Valid ArticuloDTO dto) {
        if (articuloRepository.existsByCodArticulo(dto.getCodArticulo())) {
            throw new IllegalArgumentException("El artículo ya esta creado");
        }
        Articulo articulo = articuloMapper.toEntityArticulo(dto);
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
    public void updateArticulo(Long id, EditarArticuloDTO dto) {
        Articulo articulo = findById(id);
        articuloMapper.updateArticuloFromDto(dto, articulo);

        // Obtener datos específicos de ProveedorArticulo (para cálculos)
        Proveedor proveedor = articulo.getProveedorPredeterminado();

        if (proveedor != null) {
            ProveedorArticulo pa = proveedorArticuloRepository.findByArticuloAndProveedor(articulo, proveedor)
                    .orElseThrow(() -> new EntityNotFoundException("No existe relación Proveedor-Articulo para este artículo y proveedor"));

            articulo.calcularLoteOptimo(pa.getCargosPedido(), pa.getTipoModelo(), pa.getDemoraEntrega(), pa.getTiempoRevision());
            articulo.calcularStockSeguridad(pa.getDemoraEntrega(), pa.getTiempoRevision(), pa.getTipoModelo());
            articulo.calcularPuntoPedido(pa.getDemoraEntrega());
            articulo.calcularInventarioMaximo();
            articulo.calcularCGI(pa.getPrecioUnitario(), pa.getCargosPedido());
        }

        articuloRepository.save(articulo);
    }

    @Transactional
    public void deleteById(Long id) {
        Articulo a = findById(id);
        if (a.getFechaBajaArticulo() != null) {
            throw new IllegalArgumentException("El artículo ya se encuentra desactivado");
        }
        if (a.getStockActual() != 0) {
            throw new IllegalArgumentException("El artículo tiene unidades en stock, no puede ser dado de baja");
        }
        // Verificar órdenes de compra pendientes o enviadas
        boolean tieneOrdenPendienteOEnviada = ordenCompraRepository
                .existsByArticuloAndEstadoOrdenIn(
                        a,
                        List.of(EstadoOrden.PENDIENTE, EstadoOrden.ENVIADO)
                );
        if (tieneOrdenPendienteOEnviada) {
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
