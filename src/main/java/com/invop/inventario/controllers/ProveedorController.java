package com.invop.inventario.controllers;

import com.invop.inventario.dto.BorrarProveedorArticuloDTO;
import com.invop.inventario.dto.ErrorDTO;
import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.mappers.ProveedorMapper;
import com.invop.inventario.services.ProveedorService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/proveedores")
@CrossOrigin(origins = "http://localhost:5173")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;
    @Autowired
    private ProveedorMapper proveedorMapper;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(proveedorService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(proveedorMapper.toDto(proveedorService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProveedorDTO proveedorDTO) {
        try {
            proveedorService.saveProveedor(proveedorDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProveedorDTO proveedorDTO) {
        try {
            return ResponseEntity.ok(proveedorService.updateProveedor(id, proveedorDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            proveedorService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @GetMapping("/{proveedorId}/articulos")
    public ResponseEntity<?> getArticulosPorProveedor(@PathVariable Long proveedorId) {
        try {
            return ResponseEntity.ok(proveedorService.getArticulosPorProveedor(proveedorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @GetMapping("/articulo/{articuloId}")
    public ResponseEntity<?> getProveedoresByArticulo(@PathVariable Long articuloId) {
        try {
            return ResponseEntity.ok(proveedorService.findProveedoresByArticuloId(articuloId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> borrarProveedorArticulo(@RequestBody BorrarProveedorArticuloDTO dto) {
        try {
            proveedorService.borrarProveedorArticulo(dto.getIdArticulo(), dto.getIdProveedor());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        }
    }
}
