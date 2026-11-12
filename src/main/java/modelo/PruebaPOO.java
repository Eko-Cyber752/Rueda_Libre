/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;

public class PruebaPOO {

    public static void main(String[] args) {

        Vehiculos bicicleta = new Bicicleta(
            "B001", new BigDecimal("25.00"));

        Vehiculos scooter = new Scooter(
            "S001", new BigDecimal("45.00"));

        System.out.println("Deposito bicicleta: Q"
            + bicicleta.calcularDeposito());

        System.out.println("Deposito scooter: Q"
            + scooter.calcularDeposito());
    }
}