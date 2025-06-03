package com.invop.inventario.services;

import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.mappers.ArticuloMapper;
import com.invop.inventario.mappers.ProveedorMapper;
import com.invop.inventario.repositories.ArticuloRepository;
import com.invop.inventario.repositories.EstadoOrdenRepository;
import com.invop.inventario.repositories.OrdenCompraRepository;
import com.invop.inventario.repositories.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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

            // aca poner el nombre de los estados bien
            List<String> estados = List.of("EN_CURSO","PENDIENTE");
            boolean estaPendiente = ordenCompraRepository.existsByProveedorAndEstadoOrdenNombreIn(proveedorId, estados);

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
