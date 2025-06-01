package com.invop.inventario.controllers;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.services.ArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articulos")
public class ArticuloController {

    @Autowired
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
        return articuloService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> deleteArticulo(@PathVariable Long id) {
        articuloService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
