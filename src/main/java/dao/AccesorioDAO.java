/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.Conexion;
import modelo.Accesorio;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class AccesorioDAO {

    public void registrar(Accesorio a) throws SQLException {
        String sql = "INSERT INTO accesorios "
                + "(codigo,nombre,tarifa_fija,precio_reposicion,existencia) "
                + "VALUES (?,?,?,?,?)";

        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);

            try {
                int id;

                try (PreparedStatement ps = cn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, a.getCodigo());
                    ps.setString(2, a.getNombre());
                    ps.setBigDecimal(3, a.getTarifaFija());
                    ps.setBigDecimal(4, a.getPrecioReposicion());
                    ps.setInt(5, a.getExistencia());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se obtuvo el ID.");
                        }
                        id = rs.getInt(1);
                    }
                }

                if (a.getExistencia() > 0) {
                    guardarMovimiento(cn, id, a.getExistencia());
                }

                cn.commit();

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    public List<Accesorio> listar() throws SQLException {
        List<Accesorio> lista = new ArrayList<>();

        String sql = "SELECT * FROM accesorios "
                   + "ORDER BY id_accesorio";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Accesorio a = new Accesorio();

                a.setIdAccesorio(rs.getInt("id_accesorio"));
                a.setCodigo(rs.getString("codigo"));
                a.setNombre(rs.getString("nombre"));
                a.setTarifaFija(rs.getBigDecimal("tarifa_fija"));
                a.setPrecioReposicion(
                    rs.getBigDecimal("precio_reposicion"));
                a.setExistencia(rs.getInt("existencia"));

                lista.add(a);
            }
        }

        return lista;
    }

    public boolean modificar(Accesorio a) throws SQLException {
        String sql = "UPDATE accesorios SET codigo=?, "
                + "nombre=?, tarifa_fija=?, precio_reposicion=? "
                + "WHERE id_accesorio=?";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, a.getCodigo());
            ps.setString(2, a.getNombre());
            ps.setBigDecimal(3, a.getTarifaFija());
            ps.setBigDecimal(4, a.getPrecioReposicion());
            ps.setInt(5, a.getIdAccesorio());

            return ps.executeUpdate() > 0;
        }
    }

    public void abastecer(int id, int cantidad)
            throws SQLException {

        if (cantidad <= 0) {
            throw new SQLException("La cantidad debe ser positiva.");
        }

        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);

            try {
                String sql = "UPDATE accesorios "
                        + "SET existencia=existencia+? "
                        + "WHERE id_accesorio=?";

                int filas;

                try (PreparedStatement ps =
                        cn.prepareStatement(sql)) {

                    ps.setInt(1, cantidad);
                    ps.setInt(2, id);
                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    throw new SQLException("Accesorio no encontrado.");
                }

                guardarMovimiento(cn, id, cantidad);
                cn.commit();

            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    private void guardarMovimiento(
            Connection cn, int id, int cantidad)
            throws SQLException {

        String sql = "INSERT INTO movimientos_accesorios "
                + "(id_accesorio,id_alquiler,fecha,tipo,cantidad) "
                + "VALUES (?,NULL,NOW(),'ENTRADA',?)";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, cantidad);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> historial()
            throws SQLException {

        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT m.fecha, a.nombre, "
                + "m.tipo, m.cantidad, m.id_alquiler "
                + "FROM movimientos_accesorios m "
                + "JOIN accesorios a "
                + "ON m.id_accesorio=a.id_accesorio "
                + "ORDER BY m.fecha DESC, m.id_movimiento DESC";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();

                m.put("fecha", rs.getTimestamp("fecha"));
                m.put("nombre", rs.getString("nombre"));
                m.put("tipo", rs.getString("tipo"));
                m.put("cantidad", rs.getInt("cantidad"));
                m.put("alquiler", rs.getObject("id_alquiler"));

                lista.add(m);
            }
        }

        return lista;
    }
}