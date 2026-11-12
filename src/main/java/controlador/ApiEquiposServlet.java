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

@WebServlet("/api/equipos")
public class ApiEquiposServlet extends HttpServlet {

    private String texto(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String tipo = req.getParameter("tipo");

        String sql = "SELECT id_equipo,codigo,tipo,modelo,"
                + "tarifa_hora,bateria "
                + "FROM equipos WHERE estado='DISPONIBLE' "
                + "AND (tipo<>'SCOOTER' OR bateria>=30)";

        if (tipo != null && !tipo.trim().isEmpty()) {
            sql += " AND tipo=?";
        }

        sql += " ORDER BY tipo,codigo";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (tipo != null && !tipo.trim().isEmpty()) {
                ps.setString(1, tipo);
            }

            StringBuilder json = new StringBuilder("[");
            boolean primero = true;

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    if (!primero) json.append(",");
                    primero = false;

                    json.append("{")
                        .append("\"id_equipo\":")
                        .append(rs.getInt("id_equipo"))
                        .append(",\"codigo\":\"")
                        .append(texto(rs.getString("codigo")))
                        .append("\",\"tipo\":\"")
                        .append(texto(rs.getString("tipo")))
                        .append("\",\"modelo\":\"")
                        .append(texto(rs.getString("modelo")))
                        .append("\",\"tarifa_hora\":")
                        .append(rs.getBigDecimal("tarifa_hora"))
                        .append(",\"bateria\":");

                    Object bateria = rs.getObject("bateria");
                    json.append(bateria == null ? "null" : bateria);

                    json.append("}");
                }
            }

            json.append("]");
            resp.getWriter().print(json);

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print(
                "{\"error\":\"Error al consultar equipos\"}");
        }
    }
}