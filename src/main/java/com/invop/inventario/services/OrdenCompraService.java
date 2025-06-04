package com.invop.inventario.services;

import com.invop.inventario.dtos.OrdenCompraArticuloDTO;
import com.invop.inventario.dtos.OrdenCompraCreatedDTO;
import com.invop.inventario.dtos.OrdenCompraUpdateDTO;
import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.DetalleOrden;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.OrdenCompra;
import com.invop.inventario.repositories.OrdenCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ArticuloService articuloService;

    public OrdenCompra ordenComprafindById(Long id) {
        return ordenCompraRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Orden de compra no encontrada"));
    }

    //TODO: VER LO DEL PROVEEDOR DETEMINADO Y LOTE
    @Transactional
    public OrdenCompraCreatedDTO createOrdenCompra(List<OrdenCompraArticuloDTO> ordenCompraArticuloDTOS) {
        List<DetalleOrden> detalleOrdenes = new ArrayList<>();
        float montoTotal = 0;

        OrdenCompra ordenCompra = OrdenCompra.builder()
                .estadoOrden(EstadoOrden.PENDIENTE)
                .build();

        for (OrdenCompraArticuloDTO dto : ordenCompraArticuloDTOS) {
            Articulo articulo = articuloService.findById(dto.getCodArticulo());
            float precioUnitario = getPrecioUnitario(articulo);

            DetalleOrden detalleOrden = DetalleOrden.builder()
                    .ordenCompra(ordenCompra) // <- clave
                    .articulo(articulo)
                    .cantidadArticulo(dto.getCantidadArticulo())
                    .precioUnitario(precioUnitario)
                    .precioTotal(precioUnitario * dto.getCantidadArticulo())
                    .build();

            montoTotal += detalleOrden.getPrecioTotal();
            detalleOrdenes.add(detalleOrden);
        }

        ordenCompra.setDetalles(detalleOrdenes);
        ordenCompra.setMontoTotal(montoTotal);

        Long ordenCompraId = ordenCompraRepository.save(ordenCompra).getId();

        return OrdenCompraCreatedDTO.builder()
                .ordenCompraId(ordenCompraId)
                .build();
    }

    //TODO: IMPLEMENTAR MAPSTRUCT Y DEVOLVER UN DTO
    @Transactional
    public OrdenCompra updateOrdenCompra(OrdenCompraUpdateDTO ordenCompraUpdateDTO) {

        OrdenCompra ordenCompra = ordenComprafindById(ordenCompraUpdateDTO.getOrdenCompraId());
        if (!ordenCompra.getEstadoOrden().equals(EstadoOrden.PENDIENTE)) {
            throw new RuntimeException("No se puede actualizar orden compra porque no tiene estado pendiente");
        }

        Map<Long, OrdenCompraArticuloDTO> articulosDTOMap = ordenCompraUpdateDTO.getDetalles().stream()
                .collect(Collectors.toMap(OrdenCompraArticuloDTO::getCodArticulo, Function.identity()));

        ordenCompra.getDetalles().forEach(detalleOrden -> {
            Long codArticulo = detalleOrden.getArticulo().getCodArticulo();
            OrdenCompraArticuloDTO dto = articulosDTOMap.get(codArticulo);

            if (dto != null) {
                detalleOrden.setCantidadArticulo(dto.getCantidadArticulo());
            } else {
                detalleOrden.setFechaBaja(LocalDateTime.now());
            }
        });

        return ordenCompraRepository.save(ordenCompra);
    }

    //TODO: PROBABLEMENTE HAY QUE MODIFICAR LA LOGICA
    public void confirmOrdenCompra(Long ordenCompraId) {
        OrdenCompra ordenCompra = ordenComprafindById(ordenCompraId);
        if (!ordenCompra.getEstadoOrden().equals(EstadoOrden.PENDIENTE)) {
            throw new RuntimeException("No se puede confirmar orden compra porque no tiene estado pendiente");
        }

        ordenCompra.setEstadoOrden(EstadoOrden.ENVIADO);
        List<Long> articulosIds = ordenCompra.getDetalles().stream().map(s -> s.getArticulo().getCodArticulo()).toList();
        Map<Long, Integer> mapArticuloCantidad = ordenCompra.getDetalles()
                .stream()
                .filter(detalle -> detalle.getArticulo() != null && detalle.getArticulo().getCodArticulo() != null)
                .collect(Collectors.toMap(
                        detalle -> detalle.getArticulo().getCodArticulo(),
                        DetalleOrden::getCantidadArticulo
                ));

        for (Long articuloId : articulosIds) {
            int cantidadArticulo = mapArticuloCantidad.get(articuloId);
            Articulo articulo = articuloService.findById(articuloId);
            articulo.setStockActual(articulo.getStockActual() + cantidadArticulo);
            articuloService.saveArticulo(articulo);
        }
    }

    public void cancelOrdenCompra(Long ordenCompraId) {
        OrdenCompra ordenCompra = ordenComprafindById(ordenCompraId);
        if (!ordenCompra.getEstadoOrden().equals(EstadoOrden.PENDIENTE)) {
            throw new RuntimeException("No se puede cancelar orden compra porque no tiene estado pendiente");
        }
        ordenCompra.setEstadoOrden(EstadoOrden.CANCELADO);
        ordenCompraRepository.save(ordenCompra);
    }

    private float getPrecioUnitario(Articulo articulo) {
        return articulo.getCostoCompra() + articulo.getCostoAlmacenamiento() + articulo.getCostoPedido();
    }
}
