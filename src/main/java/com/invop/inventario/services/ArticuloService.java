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

    public Page<ArticuloDTO> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Articulo> articulos = articuloRepository.findByFechaBajaArticuloIsNull(pageable);

        return articulos.map(articuloMapper::toArticuloDto);
    }

    public List<ArticuloDatoDTO> getAllArticuloDatoDTO() {
        List<Articulo> articulos = articuloRepository.findAll();
        return articuloMapper.toDtoList(articulos);
    }

    @Transactional
    public void saveArticulo(@Valid ArticuloDTO dto) {
        if (articuloRepository.existsByCodArticulo(dto.getCodArticulo())) {
            throw new IllegalArgumentException("El artículo ya esta creado");
        }

        Articulo articulo = articuloMapper.toEntityArticulo(dto);
        articuloRepository.save(articulo);
    }

    public Articulo findById(Long id) {
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El artículo no existe"));
        if (articulo.getFechaBajaArticulo() != null) {
            throw new IllegalArgumentException("El artículo está dado de baja.");
        }
        return articulo;
    }

    public ArticuloDTO getById(Long id) {
        Articulo articulo = findById(id);
        return articuloMapper.toArticuloDto(articulo);
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
    public void setProveedorPredeterminado(Long articuloId, Long proveedorId) {
        Articulo articulo = articuloRepository.findById(articuloId)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado con id: " + articuloId));

        Proveedor proveedor;
        if (proveedorId != null) {
            proveedor = proveedorRepository.findById(proveedorId)
                    .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con id: " + proveedorId));
        } else {
            proveedor = null;
        }

        articulo.setProveedorPredeterminado(proveedor);
        articuloRepository.save(articulo);
    }

    public List<ArticuloDTO> getArticulosAReponer() {
        List<Articulo> articulos = articuloRepository.findArticulosAReponer();
        return articuloMapper.toArticuloDtoList(articulos);
    }

    public List<ArticuloDTO> getArticulosFaltantes() {
        // Devuelve todos los artículos cuyo stockActual <= stockSeguridad
        List<Articulo> articulos = articuloRepository.findAll().stream()
                .filter(a -> a.getStockActual() <= a.getStockSeguridad())
                .toList();
        return articuloMapper.toArticuloDtoList(articulos);
    }
}
