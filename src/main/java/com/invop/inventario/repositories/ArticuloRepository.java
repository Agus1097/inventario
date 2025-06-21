package com.invop.inventario.repositories;

import com.invop.inventario.entities.Articulo;
import com.invop.inventario.entities.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    boolean existsByCodArticulo(Long codArticulo);

    boolean existsByProveedorPredeterminado(Proveedor proveedorPredeterminado);

    // Artículos a reponer: stockActual <= puntoPedido y que NO tengan orden de compra pendiente o enviada
    @Query("SELECT a FROM Articulo a WHERE a.stockActual <= a.puntoPedido AND " +
           "NOT EXISTS (SELECT oc FROM OrdenCompra oc WHERE oc.articulo = a AND oc.estado IN ('PENDIENTE', 'ENVIADA'))")
    List<Articulo> findArticulosAReponer();

    // Artículos faltantes: stockActual <= stockSeguridad
    List<Articulo> findByStockActualLessThanEqual(int stockSeguridad);


    Page<Articulo> findByFechaBajaArticuloIsNull(Pageable pageable);
}
