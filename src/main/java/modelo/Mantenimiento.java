/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Mantenimiento {

    private int idMantenimiento;
    private int idEquipo;
    private String equipo;
    private Timestamp fechaInicio;
    private Timestamp fechaFin;
    private String motivo;
    private BigDecimal costo;
    private String estado;

    public int getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(int id) {
        idMantenimiento = id;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int id) {
        idEquipo = id;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Timestamp fecha) {
        fechaInicio = fecha;
    }

    public Timestamp getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Timestamp fecha) {
        fechaFin = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
