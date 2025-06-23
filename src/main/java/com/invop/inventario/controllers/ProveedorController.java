package com.invop.inventario.controllers;

import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.dto.ProveedorSimpleDTO;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.mappers.ProveedorMapper;
import com.invop.inventario.services.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
@CrossOrigin(origins = "http://localhost:5173")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;
    @Autowired
    private ProveedorMapper proveedorMapper;

    @GetMapping
    public List<ProveedorDTO> getAll() {
        return proveedorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorMapper.toDto(proveedorService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProveedorDTO proveedorDTO) {
        proveedorService.saveProveedor(proveedorDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> update(@PathVariable Long id, @Valid @RequestBody ProveedorDTO proveedorDTO) {
        return ResponseEntity.ok(proveedorService.updateProveedor(id, proveedorDTO));
    }

    @PutMapping("/baja/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{proveedorId}/articulos")
    public List<Map<String, Object>> getArticulosPorProveedor(@PathVariable Long proveedorId) {
        return proveedorService.getArticulosPorProveedor(proveedorId);
    }

    @GetMapping("/articulo/{articuloId}")
    public List<ProveedorSimpleDTO> getProveedoresByArticulo(@PathVariable Long articuloId) {
        return proveedorService.findProveedoresByArticuloId(articuloId);
    }
}
