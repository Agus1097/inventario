package com.invop.inventario.repositories;

import com.invop.inventario.entities.EstadoOrden;
import com.invop.inventario.entities.OrdenCompra;
import com.invop.inventario.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository <OrdenCompra, Long> {

    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
    FROM orden_compra oc
    JOIN estado_orden eo ON oc.estado_orden_id = eo.id
    WHERE oc.proveedor_id = :proveedorId AND eo.nombre IN (:nombres)
""", nativeQuery = true)
    boolean existsByProveedorAndEstadoOrdenNombreIn(@Param("proveedorId") Long proveedorId, @Param("nombres") List<String> nombres);

}
