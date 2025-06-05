package com.invop.inventario.controllers;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.services.ArticuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/articulos")
public class ArticuloController {

    private ArticuloService articuloService;

    //TODO argregar paginado
    @GetMapping
    public ResponseEntity<List<Articulo>> getAllArticulos() {
        return ResponseEntity.ok(articuloService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createArticulo(@RequestBody Articulo articulo) {
        try {
            Articulo savedArticulo = articuloService.saveArticulo(articulo);
            return new ResponseEntity<>(savedArticulo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar el artículo");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Articulo> getArticuloById(@PathVariable Long id) {
        return ResponseEntity.ok(articuloService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> deleteArticulo(@PathVariable Long id) {
        articuloService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
