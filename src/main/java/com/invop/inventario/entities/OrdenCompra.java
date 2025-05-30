import com.invop.inventario.entities.DetalleOrden;
import com.invop.inventario.entities.EstadoOrden;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_compra")
@Data
public class OrdenCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaOrdenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_orden", nullable = false)
    private EstadoOrden estadoOrden;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_orden_compra")
    private List<DetalleOrden> detalles = new ArrayList<>();


}