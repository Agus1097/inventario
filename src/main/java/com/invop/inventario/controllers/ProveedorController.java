package com.invop.inventario.controllers;

import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.mappers.ProveedorMapper;
import com.invop.inventario.repositories.ProveedorRepository;
import com.invop.inventario.services.ProveedorService;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.query.criteria.JpaRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorRepository proveedorRepository;
    private ProveedorService proveedorService;
    private ProveedorMapper proveedorMapper;

    public ProveedorController(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> getAll() {

        List<Proveedor> proveedores = proveedorService.findAll();
        List<ProveedorDTO> dtos = proveedorMapper.toDtoList(proveedores);

        if (proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(dtos);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProveedorDTO proveedorDTO) {
        try{
            if ( proveedorDTO.getArticulosProveedor().isEmpty() ) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Necesita almenos un articulo asociado, para darse de alta como proveedor");
            }

            Proveedor proveedor = proveedorMapper.toEntity(proveedorDTO);
            ProveedorDTO savedProveedorDTO = proveedorMapper.toDto(proveedor);

            proveedorService.save(proveedor);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProveedorDTO);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al dar de alta un proveedor" + e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (proveedorService.isDeletable(id)){
                proveedorService.darBajaProveedor(id);
                return ResponseEntity.ok().body("Proveedor con id " + id + " dado de baja correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No se puede dar de baja proveedores determinados o con ordenes de compra pendiente o en curso");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error dar de baja un proveedor " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> mostrarArticulos(@PathVariable Long id) {
        try {

            return ResponseEntity.ok().body(proveedorService.listarArticulos(id));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al listar los articulo de un proveedor" + e.getMessage());
        }
    }



}
