package com.invop.inventario.controllers;

import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.ArticuloDatoDTO;
import com.invop.inventario.dto.EditarArticuloDTO;
import com.invop.inventario.services.ArticuloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articulos")
@CrossOrigin(origins = "http://localhost:5173")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @GetMapping
    public Page<ArticuloDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return articuloService.findAll(page, size);
    }

    @GetMapping("/a-asignar")
    public List<ArticuloDatoDTO> getAllArticuloDatoDTO() {
        return articuloService.getAllArticuloDatoDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articuloService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ArticuloDTO dto) {
        articuloService.saveArticulo(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody EditarArticuloDTO dto) {
        articuloService.updateArticulo(id, dto);
        return ResponseEntity.ok("Actualizado");
    }

    @PutMapping("/baja/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        articuloService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/proveedor-predeterminado")
    public ResponseEntity<?> setProveedorPredeterminado(@PathVariable Long id, @RequestParam(required = false) Long proveedorId) {
        articuloService.setProveedorPredeterminado(id, proveedorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/a-reponer")
    public List<ArticuloDTO> getArticulosAReponer() {
        return articuloService.getArticulosAReponer();
    }

    @GetMapping("/faltantes")
    public List<ArticuloDTO> getArticulosFaltantes() {
        return articuloService.getArticulosFaltantes();
    }
}
