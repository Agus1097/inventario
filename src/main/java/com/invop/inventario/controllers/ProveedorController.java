package com.invop.inventario.controllers;

import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.services.ProveedorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/proveedor")
@AllArgsConstructor
public class ProveedorController {

    private ProveedorService proveedorService;

    @GetMapping
    public List<Proveedor> getAll() {
        return proveedorService.findAll();
    }
}
