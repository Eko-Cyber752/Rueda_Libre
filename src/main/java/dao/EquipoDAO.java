
package dao;

import conexion.Conexion;
import modelo.Equipo;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class EquipoDAO {

    public boolean registrar(Equipo e) throws SQLException {
        String sql = "INSERT INTO equipos "
                + "(codigo,tipo,modelo,tarifa_hora,"
                + "precio_reposicion,estado,bateria,"
                + "alquileres_desde_mantenimiento) "
                + "VALUES (?,?,?,?,?,'DISPONIBLE',?,0)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getTipo());
            ps.setString(3, e.getModelo());
            ps.setBigDecimal(4, e.getTarifaHora());
            ps.setBigDecimal(5, e.getPrecioReposicion());

            if (e.getBateria() == null)
                ps.setNull(6, Types.INTEGER);
            else
                ps.setInt(6, e.getBateria());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Equipo> listar() throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipos ORDER BY id_equipo";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo e = new Equipo();

                e.setIdEquipo(rs.getInt("id_equipo"));
                e.setCodigo(rs.getString("codigo"));
                e.setTipo(rs.getString("tipo"));
                e.setModelo(rs.getString("modelo"));
                e.setTarifaHora(rs.getBigDecimal("tarifa_hora"));
                e.setPrecioReposicion(
                    rs.getBigDecimal("precio_reposicion"));
                e.setEstado(rs.getString("estado"));

                int bateria = rs.getInt("bateria");
                e.setBateria(rs.wasNull() ? null : bateria);

                e.setAlquileresDesdeMantenimiento(
                    rs.getInt("alquileres_desde_mantenimiento"));

                lista.add(e);
            }
        }
        return lista;
    }

    public boolean modificar(Equipo e) throws SQLException {
        String sql = "UPDATE equipos SET codigo=?,tipo=?,"
                + "modelo=?,tarifa_hora=?,precio_reposicion=?,"
                + "bateria=? WHERE id_equipo=? "
                + "AND estado='DISPONIBLE'";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getTipo());
            ps.setString(3, e.getModelo());
            ps.setBigDecimal(4, e.getTarifaHora());
            ps.setBigDecimal(5, e.getPrecioReposicion());

            if (e.getBateria() == null)
                ps.setNull(6, Types.INTEGER);
            else
                ps.setInt(6, e.getBateria());

            ps.setInt(7, e.getIdEquipo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean darDeBaja(int id) throws SQLException {
        String sql = "UPDATE equipos SET estado='DADO DE BAJA' "
                + "WHERE id_equipo=? AND estado='DISPONIBLE'";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean reactivar(int id) throws SQLException {
        String sql = "UPDATE equipos SET estado='DISPONIBLE' "
                + "WHERE id_equipo=? AND estado='DADO DE BAJA'";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
