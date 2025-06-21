package com.invop.inventario.controllers;

import com.invop.inventario.entities.OrdenCompra;
import com.invop.inventario.services.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orden-compra")
@CrossOrigin(origins = "http://localhost:5173")
public class OrdenCompraController {

    @Autowired
    private OrdenCompraService ordenCompraService;

    @GetMapping
    public List<OrdenCompra> getAll() {
        return ordenCompraService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompra> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrdenCompra> create(@RequestBody OrdenCompra ordenCompra) {
        return ResponseEntity.ok(ordenCompraService.saveOrdenCompra(ordenCompra));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenCompra> update(@PathVariable Long id, @RequestBody OrdenCompra ordenCompra) {
        return ResponseEntity.ok(ordenCompraService.updateOrdenCompra(id, ordenCompra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ordenCompraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/articulo/{articuloId}")
    public List<OrdenCompra> getByArticulo(@PathVariable Long articuloId) {
        return ordenCompraService.findByArticulo(articuloId);
    }
}
