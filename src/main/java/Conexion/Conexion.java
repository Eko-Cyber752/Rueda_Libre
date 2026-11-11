/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public static Connection conectar()
            throws SQLException {

        String url =
            "jdbc:mysql://localhost:3306/rueda_libre";

        String usuario = "root";
        String clave = "ROOT";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "No se encontro el controlador MySQL", e
            );
        }

        return DriverManager.getConnection(
            url, usuario, clave
        );
    }
}