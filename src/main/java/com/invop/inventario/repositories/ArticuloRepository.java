package com.invop.inventario.repositories;

import com.invop.inventario.entities.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    boolean existsByCodArticulo(Long codArticulo);
}
