package com.invop.inventario.controllers;

import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.mappers.ProveedorMapper;
import com.invop.inventario.services.ProveedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

    private ProveedorService proveedorService;
    private ProveedorMapper proveedorMapper;

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

            Proveedor proveedor = proveedorMapper.toEntity(proveedorDTO);
            ProveedorDTO savedProveedorDTO = proveedorMapper.toDto(proveedor);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProveedorDTO);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error al dar de alta un proveedor" + e.getMessage());
        }

    }

}
