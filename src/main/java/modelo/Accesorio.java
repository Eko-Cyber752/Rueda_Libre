/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;

public class Accesorio {

    private int idAccesorio;
    private String codigo;
    private String nombre;
    private BigDecimal tarifaFija;
    private BigDecimal precioReposicion;
    private int existencia;

    public int getIdAccesorio() {
        return idAccesorio;
    }

    public void setIdAccesorio(int id) {
        idAccesorio = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getTarifaFija() {
        return tarifaFija;
    }

    public void setTarifaFija(BigDecimal tarifa) {
        tarifaFija = tarifa;
    }

    public BigDecimal getPrecioReposicion() {
        return precioReposicion;
    }

    public void setPrecioReposicion(BigDecimal precio) {
        precioReposicion = precio;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }
}