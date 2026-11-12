/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.Conexion;
import java.sql.*;
import java.util.*;

public class ReporteDAO {

    public List<Map<String, Object>> listar(
            String tipo, String inicio, String fin)
            throws SQLException {

        String sql;

        switch (tipo) {

            case "ingresos":
                sql = "SELECT DATE(fecha) AS Fecha, "
                    + "metodo AS Metodo, "
                    + "COUNT(*) AS Pagos, "
                    + "SUM(monto) AS Total_Q "
                    + "FROM pagos "
                    + "WHERE tipo='COBRO' AND estado='APROBADO' ";
                break;

            case "equipos":
                sql = "SELECT e.tipo AS Tipo, "
                    + "COUNT(*) AS Alquileres "
                    + "FROM alquiler_equipos ae "
                    + "JOIN equipos e ON ae.id_equipo=e.id_equipo "
                    + "JOIN alquileres a ON ae.id_alquiler=a.id_alquiler "
                    + "WHERE a.estado='FINALIZADO' "
                    + "GROUP BY e.tipo ORDER BY Alquileres DESC";
                break;

            case "retrasos":
                sql = "SELECT a.id_alquiler AS Alquiler, "
                    + "c.nombre AS Cliente, "
                    + "a.fecha_estimada AS Fecha_estimada, "
                    + "a.fecha_devolucion AS Fecha_devolucion, "
                    + "a.recargo_retraso AS Recargo_Q "
                    + "FROM alquileres a "
                    + "JOIN clientes c ON a.id_cliente=c.id_cliente "
                    + "WHERE a.estado='FINALIZADO' "
                    + "AND a.recargo_retraso>0 "
                    + "ORDER BY a.fecha_devolucion DESC";
                break;

            case "mantenimientos":
                sql = "SELECT m.id_mantenimiento AS ID, "
                    + "e.codigo AS Equipo, "
                    + "m.fecha_inicio AS Inicio, "
                    + "m.fecha_fin AS Fin, "
                    + "m.motivo AS Motivo, "
                    + "m.costo AS Costo_Q, "
                    + "m.estado AS Estado "
                    + "FROM mantenimientos m "
                    + "JOIN equipos e ON m.id_equipo=e.id_equipo "
                    + "ORDER BY m.fecha_inicio DESC";
                break;

            case "inventario":
                sql = "SELECT codigo AS Codigo, "
                    + "nombre AS Accesorio, "
                    + "existencia AS Existencia "
                    + "FROM accesorios "
                    + "WHERE existencia<=5 "
                    + "ORDER BY existencia ASC";
                break;

            case "deudores":
                sql = "SELECT NIT, nombre AS Cliente, "
                    + "telefono AS Telefono, "
                    + "saldo_pendiente AS Deuda_Q "
                    + "FROM clientes "
                    + "WHERE saldo_pendiente>0 "
                    + "ORDER BY saldo_pendiente DESC";
                break;

            default:
                throw new SQLException("Reporte no válido.");
        }

        if ("ingresos".equals(tipo)) {
            if (inicio != null && !inicio.isEmpty()) {
                sql += "AND DATE(fecha)>=? ";
            }

            if (fin != null && !fin.isEmpty()) {
                sql += "AND DATE(fecha)<=? ";
            }

            sql += "GROUP BY DATE(fecha),metodo "
                 + "ORDER BY Fecha DESC,Metodo";
        }

        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if ("ingresos".equals(tipo)) {
                int n = 1;

                if (inicio != null && !inicio.isEmpty()) {
                    ps.setDate(n++, java.sql.Date.valueOf(inicio));
                }

                if (fin != null && !fin.isEmpty()) {
                    ps.setDate(n++, java.sql.Date.valueOf(fin));
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData columnas = rs.getMetaData();
                int cantidad = columnas.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();

                    for (int i = 1; i <= cantidad; i++) {
                        fila.put(columnas.getColumnLabel(i),
                                 rs.getObject(i));
                    }

                    lista.add(fila);
                }
            }
        }

        return lista;
    }
}