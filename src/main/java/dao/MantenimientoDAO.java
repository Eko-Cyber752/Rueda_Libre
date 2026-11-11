/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.Conexion;
import modelo.Mantenimiento;
import modelo.Equipo;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAO {

    public List<Equipo> equiposDisponibles()
            throws SQLException {

        List<Equipo> lista = new ArrayList<>();

        String sql = "SELECT id_equipo, codigo, modelo "
                + "FROM equipos WHERE estado='DISPONIBLE'";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo e = new Equipo();
                e.setIdEquipo(rs.getInt("id_equipo"));
                e.setCodigo(rs.getString("codigo"));
                e.setModelo(rs.getString("modelo"));
                lista.add(e);
            }
        }
        return lista;
    }

    public List<Mantenimiento> listar()
            throws SQLException {

        List<Mantenimiento> lista = new ArrayList<>();

        String sql = "SELECT m.*, e.codigo "
                + "FROM mantenimientos m "
                + "JOIN equipos e ON m.id_equipo=e.id_equipo "
                + "ORDER BY m.id_mantenimiento DESC";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mantenimiento m = new Mantenimiento();

                m.setIdMantenimiento(
                    rs.getInt("id_mantenimiento"));
                m.setIdEquipo(rs.getInt("id_equipo"));
                m.setEquipo(rs.getString("codigo"));
                m.setFechaInicio(rs.getTimestamp("fecha_inicio"));
                m.setFechaFin(rs.getTimestamp("fecha_fin"));
                m.setMotivo(rs.getString("motivo"));
                m.setCosto(rs.getBigDecimal("costo"));
                m.setEstado(rs.getString("estado"));

                lista.add(m);
            }
        }
        return lista;
    }

    public void registrar(int idEquipo, String motivo,
            BigDecimal costo) throws SQLException {

        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);

            try {
                String sqlEquipo = "UPDATE equipos "
                        + "SET estado='EN MANTENIMIENTO' "
                        + "WHERE id_equipo=? "
                        + "AND estado='DISPONIBLE'";

                try (PreparedStatement ps =
                        cn.prepareStatement(sqlEquipo)) {

                    ps.setInt(1, idEquipo);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                            "El equipo no esta disponible.");
                    }
                }

                String sql = "INSERT INTO mantenimientos "
                        + "(id_equipo, fecha_inicio, motivo, "
                        + "costo, estado) "
                        + "VALUES (?, NOW(), ?, ?, 'ABIERTO')";

                try (PreparedStatement ps =
                        cn.prepareStatement(sql)) {

                    ps.setInt(1, idEquipo);
                    ps.setString(2, motivo);
                    ps.setBigDecimal(3, costo);
                    ps.executeUpdate();
                }

                cn.commit();

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    public void finalizar(int id) throws SQLException {

        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);

            try {
                int idEquipo;

                String buscar = "SELECT id_equipo "
                        + "FROM mantenimientos "
                        + "WHERE id_mantenimiento=? "
                        + "AND estado='ABIERTO' FOR UPDATE";

                try (PreparedStatement ps =
                        cn.prepareStatement(buscar)) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                "Mantenimiento no encontrado.");
                        }
                        idEquipo = rs.getInt("id_equipo");
                    }
                }

                String cerrar = "UPDATE mantenimientos "
                        + "SET fecha_fin=NOW(), estado='CERRADO' "
                        + "WHERE id_mantenimiento=?";

                try (PreparedStatement ps =
                        cn.prepareStatement(cerrar)) {

                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                String equipo = "UPDATE equipos "
                        + "SET estado='DISPONIBLE', "
                        + "alquileres_desde_mantenimiento=0 "
                        + "WHERE id_equipo=? "
                        + "AND estado='EN MANTENIMIENTO'";

                try (PreparedStatement ps =
                        cn.prepareStatement(equipo)) {

                    ps.setInt(1, idEquipo);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                            "No se pudo liberar el equipo.");
                    }
                }

                cn.commit();

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }
}