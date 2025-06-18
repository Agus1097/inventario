package com.invop.inventario.repositories;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProveedorArticuloRepository extends JpaRepository<ProveedorArticulo, Long> {
    Optional<ProveedorArticulo> findByArticuloAndProveedor(Articulo articulo, Proveedor proveedor);
}