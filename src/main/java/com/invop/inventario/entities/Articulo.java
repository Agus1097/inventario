package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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

    @Column(name = "costo_pedido")
    private float costoPedido;

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
    private int cgi;

    @ManyToOne
    @JoinColumn(name = "id_proveedor_predeterminado")
    private Proveedor proveedorPredeterminado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_modelo")
    private TipoModelo tipoModelo;

    public int calcularCGI(float precioUnitario) {
        // demanda * precioUnitario + demanda/cantidad * costoPedido + costoAlmacenamiento*cantidad/2
        return cgi;
    };

    public int calcularStockSeguridad() {
        //periodo fijo
        // z * desviacionStandar * (numeroDiasEntreRevision + TiempoEntrega)
        // lote fijo
        // z * desviacionStandar
        return stockSeguridad;
    }

    public int calcularLoteOptimo() {
        // lote fijo
        // raizcuadrada(2*demanda*costoPedido / costoAlacemaniento)

        //todo
        //periodo fijo /// intervalo fijo
        // demanda diaria * (numeroDiasEntreRevision + TiempoEntrega) + z * desviacionStandar * (numeroDiasEntreRevision + TiempoEntrega) - nivelInventarioActual
        return loteOptimo;
    }

    public int calcularPuntoPedido() {
        // lote fijo
        // demandadiaria * tiempoEntrega + stockSeguridad
        return puntoPedido;
    }

    public int calcularInventarioMaximo() {
        // Cantidad * ( 1 - demandaDiaria/producciondiaria)
        return inventarioMaximo;
    }
}
