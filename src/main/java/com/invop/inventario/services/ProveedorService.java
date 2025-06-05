package com.invop.inventario.services;

import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.mappers.ArticuloMapper;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ArticuloRepository articuloRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private ArticuloMapper articuloMapper;



    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> findById(Long id) { return this.proveedorRepository.findById(id); }

    public Boolean existById(Long id) { return this.proveedorRepository.existsById(id); }

    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Boolean isDeletable (Long proveedorId) {
        try{
            final Proveedor proveedor = proveedorRepository.findById(proveedorId).get();

            boolean esPredeterminado = articuloRepository.existsByProveedorPredeterminado(proveedor);

            // TODO: tmb hay un estado en_curso?
            boolean estaPendiente = ordenCompraRepository.existsByProveedor_IdAndEstadoOrden(proveedorId, EstadoOrden.PENDIENTE);

            return esPredeterminado && estaPendiente;

        }catch (Exception e){
            return false;
        }
    }

    public void darBajaProveedor(Long proveedorId) throws Exception{

        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new Exception("Proveedor no encontrado"));

        proveedor.setFechaBajaProveedor(LocalDate.now());

        proveedorRepository.save(proveedor);

    }

    public List<ArticuloDTO> listarArticulos(Long proveedorId) throws Exception{
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new Exception("Proveedor no encontrado"));

        List<ArticuloDTO> listaArticulos = new ArrayList<>();

        proveedor.getProveedorArticulos().forEach(proveedorArticulo -> {
            listaArticulos.add(articuloMapper.toDto(proveedorArticulo.getArticulo()));
        });

        return listaArticulos;
    }

}
