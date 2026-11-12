/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;

public class Scooter extends Vehiculos {

    public Scooter(String codigo, BigDecimal tarifaHora) {
        super(codigo, tarifaHora);
    }

    @Override
    public BigDecimal calcularDeposito() {
        return new BigDecimal("200.00");
    }
}