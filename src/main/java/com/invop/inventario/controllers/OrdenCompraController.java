package com.invop.inventario.controllers;

import com.invop.inventario.dto.CrearOrdenCompraDTO;
import com.invop.inventario.dto.ErrorDTO;
import com.invop.inventario.dto.OrdenCompraDTO;
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
    public List<OrdenCompraDTO> getAll() {
        return ordenCompraService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.getOrdenCompraById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CrearOrdenCompraDTO dto) {
        try {
            return ResponseEntity.ok(ordenCompraService.saveOrdenCompra(dto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorDTO(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody OrdenCompra ordenCompra) {
        try {
            return ResponseEntity.ok(ordenCompraService.updateOrdenCompra(id, ordenCompra));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorDTO(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ordenCompraService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/articulo/{articuloId}")
    public List<OrdenCompraDTO> getByArticulo(@PathVariable Long articuloId) {
        return ordenCompraService.findByArticulo(articuloId);
    }
}
