package modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Alquileres {

    private int idAlquiler;
    private int idCliente;
    private Timestamp fechaEstimada;
    private String estado = "RESERVADO";

    private List<Integer> equipos = new ArrayList<>();
    private Map<Integer, Integer> accesorios = new LinkedHashMap<>();

    public int getIdAlquiler() {
        return idAlquiler;
    }

    public void setIdAlquiler(int id) {
        idAlquiler = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int id) {
        idCliente = id;
    }

    public Timestamp getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(Timestamp fecha) {
        fechaEstimada = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Integer> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Integer> equipos) {
        this.equipos = equipos;
    }

    public Map<Integer, Integer> getAccesorios() {
        return accesorios;
    }

    public void setAccesorios(Map<Integer, Integer> accesorios) {
        this.accesorios = accesorios;
    }
}