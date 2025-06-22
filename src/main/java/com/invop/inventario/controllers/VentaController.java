package com.invop.inventario.controllers;

import com.invop.inventario.dto.CrearVentaDTO;
import com.invop.inventario.entities.Venta;
import com.invop.inventario.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "http://localhost:5173")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public List<Venta> getAll() {
        return ventaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Venta> create(@RequestBody CrearVentaDTO dto) {

        return ResponseEntity.ok(ventaService.saveVenta(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> update(@PathVariable Long id, @RequestBody Venta venta) {
        return ResponseEntity.ok(ventaService.updateVenta(id, venta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
