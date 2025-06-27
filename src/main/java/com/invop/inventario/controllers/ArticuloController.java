package com.invop.inventario.controllers;

import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.EditarArticuloDTO;
import com.invop.inventario.dto.ProveedorPredeterminadoDTO;
import com.invop.inventario.services.ArticuloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/articulos")
@CrossOrigin(origins = "http://localhost:5173")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(articuloService.findAll(page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/a-asignar")
    public ResponseEntity<?> getAllArticuloDatoDTO() {
        try {
            return ResponseEntity.ok(articuloService.getAllArticuloDatoDTO());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(articuloService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ArticuloDTO dto) {
        try {
            articuloService.saveArticulo(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody EditarArticuloDTO dto) {
        try {
            articuloService.updateArticulo(id, dto);
            return ResponseEntity.ok("Actualizado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/baja/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            articuloService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/proveedor-predeterminado")
    public ResponseEntity<?> setProveedorPredeterminado(@PathVariable Long id, @RequestBody ProveedorPredeterminadoDTO dto) {
        try {
            articuloService.setProveedorPredeterminado(id, dto.getProveedorId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/a-reponer")
    public ResponseEntity<?> getArticulosAReponer() {
        try {
            return ResponseEntity.ok(articuloService.getArticulosAReponer());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/faltantes")
    public ResponseEntity<?> getArticulosFaltantes() {
        try {
            return ResponseEntity.ok(articuloService.getArticulosFaltantes());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
