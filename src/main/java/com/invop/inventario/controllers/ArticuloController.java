package com.invop.inventario.controllers;

import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.ArticuloDatoDTO;
import com.invop.inventario.dto.EditarArticuloDTO;
import com.invop.inventario.entities.Articulo;
import com.invop.inventario.services.ArticuloService;
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
    public Page<Articulo> getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return articuloService.findAll(page, size);
    }

    @GetMapping("/a-asignar")
    public List<ArticuloDatoDTO> getAllArticuloDatoDTO() {
        return articuloService.getAllArticuloDatoDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Articulo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articuloService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Articulo> create(@RequestBody ArticuloDTO dto) {
        return ResponseEntity.ok(articuloService.saveArticulo(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Articulo> update(@PathVariable Long id, @RequestBody EditarArticuloDTO dto) {
        Articulo actualizado = articuloService.updateArticulo(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articuloService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/proveedor-predeterminado")
    public ResponseEntity<Articulo> setProveedorPredeterminado(@PathVariable Long id, @RequestParam(required = false) Long proveedorId) {
        Articulo updated = articuloService.setProveedorPredeterminado(id, proveedorId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/a-reponer")
    public List<Articulo> getArticulosAReponer() {
        return articuloService.getArticulosAReponer();
    }

    @GetMapping("/faltantes")
    public List<Articulo> getArticulosFaltantes() {
        return articuloService.getArticulosFaltantes();
    }
}
