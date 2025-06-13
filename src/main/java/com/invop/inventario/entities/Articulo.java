package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table
@Data
public class Articulo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "cod_articulo")
    private Long codArticulo;

    private String nombre;

    private String descripcion;

    @Column(name ="produccion_diaria")
    private float produccionDiaria;

    @Column(name = "demanda_articulo")
    private float demandaArticulo;

    @Column(name = "costo_almacenamiento")
    private float costoAlmacenamiento;

    @Column(name = "costo_compra")
    private float costoCompra;

    @Column(name = "fecha_baja_articulo")
    private LocalDate fechaBajaArticulo;

    @Column(name = "punto_pedido")
    private int puntoPedido;

    @Column(name = "stock_seguridad")
    private int stockSeguridad;

    @Column(name = "inventario_maximo")
    private int inventarioMaximo;

    @Column(name = "lote_optimo")
    private int loteOptimo;

    @Column(name = "stock_actual")
    private int stockActual;

    @Column
    private float z;

    @Column
    private float desviacionEstandar;

    @Column
    private int numeroDiasEntreRevision;

    @Column
    private int producciondiaria;

    @ManyToOne
    @JoinColumn(name = "id_proveedor_predeterminado")
    private Proveedor proveedorPredeterminado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_modelo")
    private TipoModelo tipoModelo;

    public float calcularCGI(float precioUnitario, float costoPedido) {
        // demanda * precioUnitario + demanda/cantidad * costoPedido + costoAlmacenamiento*cantidad/2

        return this.demandaArticulo * precioUnitario + this.demandaArticulo / this.loteOptimo * costoPedido + this.costoAlmacenamiento * this.loteOptimo/2;
    };

    public int calcularStockSeguridad(int demoraEntrega) {
        //periodo fijo
        // z * desviacionStandar * (numeroDiasEntreRevision + TiempoEntrega)
        // lote fijo
        // z * desviacionStandar

        if (Objects.equals(this.getTipoModelo().getId(), TipoModelo.LOTE_FIJO.getId())) {
            this.stockSeguridad = (int) (this.z * this.desviacionEstandar);
        } else {
            this.stockSeguridad = (int) (this.z * this.desviacionEstandar) * (this.numeroDiasEntreRevision + demoraEntrega);
        }
    }

    public void calcularLoteOptimo(float cargosPedido, int demoraEntrega) {
        // lote fijo
        // raizcuadrada(2*demanda*costoPedido / costoAlacemaniento)

        //todo
        //periodo fijo /// intervalo fijo
        // demanda diaria * (numeroDiasEntreRevision + TiempoEntrega) + z * desviacionStandar * (numeroDiasEntreRevision + TiempoEntrega) - nivelInventarioActual

        if (Objects.equals(this.getTipoModelo().getId(), TipoModelo.LOTE_FIJO.getId())) {
            this.loteOptimo = (int) (2 * this.demandaArticulo * cargosPedido / this.costoAlmacenamiento);
        } else {
            this.loteOptimo = (int) (this.demandaArticulo * ( this.numeroDiasEntreRevision + demoraEntrega ) + this.stockSeguridad - this.stockActual);
        }
    }

    public int calcularPuntoPedido(int demoraEntrega) {
        // lote fijo
        // demandadiaria * tiempoEntrega + stockSeguridad
        this.puntoPedido = (int) (this.demandaArticulo * demoraEntrega + this.stockSeguridad);
    }

    public int calcularInventarioMaximo() {
        // Cantidad * ( 1 - demandaDiaria/producciondiaria)
        return (int) (this.loteOptimo * (1 - this.demandaArticulo / 365 / this.produccionDiaria));
    }
}
