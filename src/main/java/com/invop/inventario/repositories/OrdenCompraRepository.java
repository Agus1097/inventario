package com.invop.inventario.repositories;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.OrdenCompra;
import com.invop.inventario.entities.Proveedor;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository <OrdenCompra, Long> {

    boolean existsByProveedor_IdAndEstadoOrden(Long proveedorId, EstadoOrden estado);

    boolean existsByArticuloAndEstadoOrdenIn(Articulo articulo, List<EstadoOrden> estados);

    boolean existsByProveedorAndEstadoOrdenIn(Proveedor proveedor, List<EstadoOrden> estados);

    //boolean existsByProveedorPredeterminado(Proveedor proveedor);

    List<OrdenCompra> findByArticulo(Articulo articulo);
}
