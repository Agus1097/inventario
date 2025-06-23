package com.invop.inventario.repositories;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProveedorArticuloRepository extends JpaRepository<ProveedorArticulo, Long> {
    Optional<ProveedorArticulo> findByArticuloAndProveedor(Articulo articulo, Proveedor proveedor);

    @Query("SELECT pa.proveedor FROM ProveedorArticulo pa WHERE pa.articulo.id = :articuloId")
    List<Proveedor> findProveedoresByArticuloId(@Param("articuloId") Long articuloId);
}