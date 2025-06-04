package com.invop.inventario.controllers;

import com.invop.inventario.dtos.OrdenCompraArticuloDTO;
import com.invop.inventario.dtos.OrdenCompraCreatedDTO;
import com.invop.inventario.dtos.OrdenCompraUpdateDTO;
import com.invop.inventario.entities.OrdenCompra;
import com.invop.inventario.services.OrdenCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ordenCompra")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @PostMapping
    public ResponseEntity<OrdenCompraCreatedDTO> createOrdenCompra(@RequestBody List<OrdenCompraArticuloDTO> dto) {
        return new ResponseEntity<>(ordenCompraService.createOrdenCompra(dto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<OrdenCompra> updateOrdenCompra(@RequestBody OrdenCompraUpdateDTO dto) {
        return ResponseEntity.ok(ordenCompraService.updateOrdenCompra(dto));
    }

    @PutMapping("/{ordenCompraId}/confimar")
    public ResponseEntity<?> confirmOrdenCompra(@PathVariable Long ordenCompraId) {
        ordenCompraService.confirmOrdenCompra(ordenCompraId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("{ordenCompraId}/cancelar")
    public ResponseEntity<?> cancelOrdenCompra(@PathVariable Long ordenCompraId) {
        ordenCompraService.cancelOrdenCompra(ordenCompraId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
