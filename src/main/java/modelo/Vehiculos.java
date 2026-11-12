/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;

public abstract class Vehiculos {

    private String codigo;
    private BigDecimal tarifaHora;

    public Vehiculos(String codigo, BigDecimal tarifaHora) {
        this.codigo = codigo;
        this.tarifaHora = tarifaHora;
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public abstract BigDecimal calcularDeposito();
}