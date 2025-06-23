package com.invop.inventario.services;

import com.invop.inventario.dto.ProveedorArticuloDTO;
import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.dto.ProveedorSimpleDTO;
import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import com.invop.inventario.mappers.ProveedorMapper;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.ProveedorArticuloRepository;
import com.invop.inventario.repositories.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ArticuloRepository articuloRepository;
    private final ProveedorMapper proveedorMapper;
    private final ArticuloService articuloService;
    private final ProveedorArticuloRepository proveedorArticuloRepository;

    public List<ProveedorDTO> findAll() {
        return proveedorMapper.toDtoList(proveedorRepository.findByFechaBajaProveedorIsNull());
    }

    public Proveedor findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));
        if (proveedor.getFechaBajaProveedor() != null) {
            throw new IllegalArgumentException("El proveedor está dado de baja.");
        }
        return proveedor;
    }

    @Transactional
    public void saveProveedor(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = proveedorMapper.toEntity(proveedorDTO);

        for (ProveedorArticulo pa : proveedor.getProveedorArticulos()) {

            if (pa.getArticulo() == null || pa.getArticulo().getId() == null) {
                throw new IllegalArgumentException("Cada ProveedorArticulo debe tener un Articulo con un id válido");
            }

            Articulo articulo = articuloService.findById(pa.getArticulo().getId());

            if (Objects.isNull(articulo.getProveedorPredeterminado())) {
                articulo.setProveedorPredeterminado(proveedor);
                articulo.calcularStockSeguridad(pa.getDemoraEntrega(), pa.getTiempoRevision(), pa.getTipoModelo());
                articulo.calcularLoteOptimo(pa.getCargosPedido(), pa.getTipoModelo(), pa.getDemoraEntrega(), pa.getTiempoRevision());
                articulo.calcularPuntoPedido(pa.getDemoraEntrega());
                articulo.calcularInventarioMaximo();
                articulo.calcularCGI(pa.getPrecioUnitario(), pa.getCargosPedido());
            }

            pa.setProveedor(proveedor);
        }

        // Guardar proveedor y sus ProveedorArticulo en cascada
        proveedorRepository.save(proveedor);
    }

    //TODO FALTA AGREGAR QUE SETEE PROVEDOR PREDETERMINADO EN ARTICULO SI ES LA PRIMERA VEZ QUE SE AGREGA UN PROVEEDOR PARA ESE ARTICULO
    //LO AGREGUE PERO FALTA REVISAR QUE NO SE ROMPA NADA
    @Transactional
    public ProveedorDTO updateProveedor(Long id, ProveedorDTO proveedorDTO) {
        Proveedor proveedor = findById(id);

        if (proveedorDTO.getNombre() != null) {
            proveedor.setNombre(proveedorDTO.getNombre());
        }

        if (proveedorDTO.getProveedorArticulos() != null) {
            for (ProveedorArticuloDTO paDTO : proveedorDTO.getProveedorArticulos()) {
                if (paDTO.getArticulo() == null || paDTO.getArticulo().getId() == null) continue;

                Articulo articulo = articuloService.findById(paDTO.getArticulo().getId());
                Optional<ProveedorArticulo> existenteOpt = proveedorArticuloRepository.findByArticuloAndProveedor(articulo, proveedor);

                if (existenteOpt.isPresent()) {
                    ProveedorArticulo existente = existenteOpt.get();
                    if (paDTO.getDemoraEntrega() != null) existente.setDemoraEntrega(paDTO.getDemoraEntrega());
                    if (paDTO.getPrecioUnitario() != null) existente.setPrecioUnitario(paDTO.getPrecioUnitario());
                    if (paDTO.getCargosPedido() != null) existente.setCargosPedido(paDTO.getCargosPedido());
                    if (paDTO.getTiempoRevision() != null) existente.setTiempoRevision(paDTO.getTiempoRevision());
                    if (paDTO.getTipoModelo() != null) existente.setTipoModelo(paDTO.getTipoModelo());
                } else {
                    // Si no existe, lo crea y lo agrega al proveedor
                    ProveedorArticulo nuevo = new ProveedorArticulo();
                    nuevo.setProveedor(proveedor);
                    nuevo.setArticulo(articulo);
                    nuevo.setDemoraEntrega(paDTO.getDemoraEntrega());
                    nuevo.setPrecioUnitario(paDTO.getPrecioUnitario());
                    nuevo.setCargosPedido(paDTO.getCargosPedido());
                    nuevo.setTiempoRevision(paDTO.getTiempoRevision());
                    nuevo.setTipoModelo(paDTO.getTipoModelo());
                    proveedor.getProveedorArticulos().add(nuevo);

                    if (Objects.isNull(articulo.getProveedorPredeterminado())) {
                        articulo.setProveedorPredeterminado(proveedor);
                        articulo.calcularStockSeguridad(nuevo.getDemoraEntrega(), nuevo.getTiempoRevision(), nuevo.getTipoModelo());
                        articulo.calcularLoteOptimo(nuevo.getCargosPedido(), nuevo.getTipoModelo(), nuevo.getDemoraEntrega(), nuevo.getTiempoRevision());
                        articulo.calcularPuntoPedido(nuevo.getDemoraEntrega());
                        articulo.calcularInventarioMaximo();
                        articulo.calcularCGI(nuevo.getPrecioUnitario(), nuevo.getCargosPedido());
                        articuloRepository.save(articulo);
                    }
                }
            }
        }
        proveedorRepository.save(proveedor);
        return proveedorMapper.toDto(proveedor);
    }

    @Transactional
    public void deleteById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        // Verificar que no tenga órdenes de compra en estado pendiente o enviada
        boolean tieneOrdenes = ordenCompraRepository.existsByProveedorAndEstadoOrdenIn(proveedor, List.of(EstadoOrden.PENDIENTE, EstadoOrden.ENVIADO));
        if (tieneOrdenes) {
            throw new IllegalArgumentException("El proveedor tiene órdenes de compra pendientes o enviadas y no puede ser dado de baja.");
        }

        // Verificar que no sea proveedorPredeterminado en ningún artículo
        boolean esPredeterminado = articuloRepository.existsByProveedorPredeterminado(proveedor);
        if (esPredeterminado) {
            throw new IllegalArgumentException("El proveedor está seteado como proveedor predeterminado en algún artículo y no puede ser dado de baja.");
        }

        // Setear fecha de baja
        proveedor.setFechaBajaProveedor(LocalDate.now());
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
            info.put("codArticulo", articulo.getCodArticulo());
            info.put("nombreArticulo", articulo.getNombre());
            info.put("descripcionArticulo", articulo.getDescripcion());
            info.put("codArticulo", articulo.getCodArticulo());
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

    public List<ProveedorSimpleDTO> findProveedoresByArticuloId(Long articuloId) {
        List<Proveedor> proveedores = proveedorArticuloRepository.findProveedoresByArticuloId(articuloId);
        return proveedores.stream().map(proveedor -> {
            ProveedorSimpleDTO dto = new ProveedorSimpleDTO();
            dto.setId(proveedor.getId());
            dto.setNombreProveedor(proveedor.getNombre());
            return dto;
        }).toList();
    }

}
