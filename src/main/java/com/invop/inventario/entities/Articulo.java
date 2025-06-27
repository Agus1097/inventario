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

    @Column(name = "produccion_diaria")
    private float produccionDiaria;

    @Column
    private float CGI;

    @Column(name = "demanda_articulo")
    private float demandaArticulo;

    @Column(name = "costo_almacenamiento")
    private float costoAlmacenamiento;

    @Column(name = "costo_venta")
    private float costoVenta;

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

    @Column(name = "desviacion_estandar")
    private float desviacionEstandar;

    @ManyToOne
    @JoinColumn(name = "id_proveedor_predeterminado")
    private Proveedor proveedorPredeterminado;

    public void calcularTodo(float precioUnitario, float costoPedido, int demoraEntrega, float tiempoRevision, TipoModelo tipoModelo) {
        
        calcularStockSeguridad(demoraEntrega, tiempoRevision, tipoModelo);
        calcularLoteOptimo(costoPedido, tipoModelo, demoraEntrega, tiempoRevision);
        calcularPuntoPedido(demoraEntrega, tipoModelo);
        calcularInventarioMaximo(tipoModelo);
        calcularCGI(precioUnitario, costoPedido);
    }

    public void calcularCGI(float precioUnitario, float costoPedido) {
        // demanda * precioUnitario + demanda/cantidad * costoPedido + costoAlmacenamiento*cantidad/2

        this.CGI = this.demandaArticulo * precioUnitario + this.demandaArticulo / this.loteOptimo * costoPedido + this.costoAlmacenamiento * this.loteOptimo / 2;
    }


    public void calcularStockSeguridad(int demoraEntrega, float tiempoRevision, TipoModelo tipoModelo) {
        //periodo fijo
        // z * desviacionStandar * (numeroDiasEntreRevision + TiempoEntrega)
        // lote fijo
        // z * desviacionStandar

        if (Objects.equals(tipoModelo, TipoModelo.LOTE_FIJO)) {
            this.stockSeguridad = (int) (this.z * this.desviacionEstandar);
        } else {
            this.stockSeguridad = (int) ((this.z * this.desviacionEstandar) * (tiempoRevision + demoraEntrega));
        }
    }

    public void calcularLoteOptimo(float cargosPedido, TipoModelo tipoModelo, int demoraEntrega, float tiempoRevision) {
        // lote fijo
        // raizcuadrada(2*demanda*costoPedido / costoAlacemaniento)

        //todo
        //periodo fijo /// intervalo fijo
        // demanda diaria * (numeroDiasEntreRevision + TiempoEntrega) + z * desviacionStandar * (numeroDiasEntreRevision + TiempoEntrega) - nivelInventarioActual

        if (Objects.equals(tipoModelo, TipoModelo.LOTE_FIJO)) {
            this.loteOptimo = (int) Math.pow((2 * this.demandaArticulo * cargosPedido / this.costoAlmacenamiento), 0.5);
        } else {
            this.loteOptimo = (int) (this.demandaArticulo / 365 * (tiempoRevision + demoraEntrega) + this.stockSeguridad - this.stockActual);
        }
    }

    public void calcularPuntoPedido(int demoraEntrega, TipoModelo tipoModelo) {
        if (Objects.equals(tipoModelo, TipoModelo.LOTE_FIJO)) {
            // lote fijo
            // demanda diaria * (numeroDiasEntreRevision + TiempoEntrega) + z * desviacionStandar * (numeroDiasEntreRevision + TiempoEntrega) - nivelInventarioActual
            this.puntoPedido = (int) (this.demandaArticulo * demoraEntrega + this.stockSeguridad);
        } else {
            this.puntoPedido = 0;
        }
        
    }

    public void calcularInventarioMaximo(TipoModelo tipoModelo) {
        // Cantidad * ( 1 - demandaDiaria/producciondiaria)
        if (Objects.equals(tipoModelo, TipoModelo.INTERVALO_FIJO)) {
            this.inventarioMaximo = (int) (this.loteOptimo * (1 - this.demandaArticulo / 365 / this.produccionDiaria));
        } else {
            this.inventarioMaximo = 0;
        }
        
    }
}
