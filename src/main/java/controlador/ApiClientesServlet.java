/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import conexion.Conexion;
import java.io.*;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/api/clientes")
public class ApiClientesServlet extends HttpServlet {

    private String texto(String valor) {
        if (valor == null) return "";

        StringBuilder s = new StringBuilder();

        for (char c : valor.toCharArray()) {
            if (c == '"') s.append("\\\"");
            else if (c == '\\') s.append("\\\\");
            else if (c == '\n') s.append("\\n");
            else if (c == '\r') s.append("\\r");
            else if (c == '\t') s.append("\\t");
            else if (c < 32)
                s.append(String.format("\\u%04x", (int)c));
            else s.append(c);
        }

        return s.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");

        int id;

        try {
            id = Integer.parseInt(req.getParameter("id"));
            if (id <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().print(
                "{\"error\":\"ID de cliente incorrecto\"}");
            return;
        }

        try (Connection cn = Conexion.conectar()) {

            String sql = "SELECT id_cliente,nombre,activo,"
                    + "saldo_pendiente FROM clientes "
                    + "WHERE id_cliente=?";

            StringBuilder json = new StringBuilder();

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        resp.setStatus(404);
                        resp.getWriter().print(
                            "{\"error\":\"Cliente no encontrado\"}");
                        return;
                    }

                    json.append("{\"id_cliente\":")
                        .append(rs.getInt("id_cliente"))
                        .append(",\"nombre\":\"")
                        .append(texto(rs.getString("nombre")))
                        .append("\",\"activo\":")
                        .append(rs.getBoolean("activo"))
                        .append(",\"saldo_pendiente\":")
                        .append(rs.getBigDecimal("saldo_pendiente"))
                        .append(",\"alquileres\":[");
                }
            }

            sql = "SELECT id_alquiler,fecha_reserva,"
                + "fecha_entrega,fecha_devolucion,"
                + "estado,total FROM alquileres "
                + "WHERE id_cliente=? ORDER BY id_alquiler DESC";

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean primero = true;

                    while (rs.next()) {
                        if (!primero) json.append(",");
                        primero = false;

                        json.append("{\"id_alquiler\":")
                            .append(rs.getInt("id_alquiler"))
                            .append(",\"fecha_reserva\":\"")
                            .append(rs.getTimestamp("fecha_reserva"))
                            .append("\",\"fecha_entrega\":");

                        Timestamp entrega =
                            rs.getTimestamp("fecha_entrega");

                        json.append(entrega == null
                            ? "null" : "\"" + entrega + "\"");

                        json.append(",\"fecha_devolucion\":");

                        Timestamp devolucion =
                            rs.getTimestamp("fecha_devolucion");

                        json.append(devolucion == null
                            ? "null" : "\"" + devolucion + "\"");

                        json.append(",\"estado\":\"")
                            .append(texto(rs.getString("estado")))
                            .append("\",\"total\":")
                            .append(rs.getBigDecimal("total"))
                            .append("}");
                    }
                }
            }

            json.append("]}");
            resp.getWriter().print(json);

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print(
                "{\"error\":\"Error al consultar cliente\"}");
        }
    }
}