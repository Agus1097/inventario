package com.invop.inventario.services;

import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.repositories.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    public boolean tieneArticulo(ProveedorDTO proveedorDTO) {

    }
}
