/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PruebaConexion {

    public static void main(String[] args) {

        String sql = "SELECT COUNT(*) FROM clientes";

        try (
            Connection cn = Conexion.conectar();
            PreparedStatement ps =
                cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                System.out.println("CONEXION EXITOSA");
                System.out.println(
                    "Clientes: " + rs.getInt(1)
                );
            }

        } catch (Exception e) {
            System.out.println(
                "ERROR: " + e.getMessage()
            );
        }
    }
}