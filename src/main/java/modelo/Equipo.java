/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Jon
 */
import java.math.BigDecimal;

public class Equipo {

    private int idEquipo;
    private String codigo, tipo, modelo, estado;
    private BigDecimal tarifaHora, precioReposicion;
    private Integer bateria;
    private int alquileresDesdeMantenimiento;

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int v) { idEquipo = v; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String v) { codigo = v; }

    public String getTipo() { return tipo; }
    public void setTipo(String v) { tipo = v; }

    public String getModelo() { return modelo; }
    public void setModelo(String v) { modelo = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { estado = v; }

    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal v) { tarifaHora = v; }

    public BigDecimal getPrecioReposicion() { return precioReposicion; }
    public void setPrecioReposicion(BigDecimal v) { precioReposicion = v; }

    public Integer getBateria() { return bateria; }
    public void setBateria(Integer v) { bateria = v; }

    public int getAlquileresDesdeMantenimiento() {
        return alquileresDesdeMantenimiento;
    }

    public void setAlquileresDesdeMantenimiento(int v) {
        alquileresDesdeMantenimiento = v;
    }
}
