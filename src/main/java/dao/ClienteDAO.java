/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.Conexion;
import modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean registrar(Cliente c) throws SQLException {

        String sql = "INSERT INTO clientes "
                + "(NIT, nombre, telefono, correo, activo, saldo_pendiente) "
                + "VALUES (?, ?, ?, ?, 1, 0)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getIdentificador());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Cliente> listar() throws SQLException {

        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id_cliente";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente c = new Cliente();

                c.setIdCliente(rs.getInt("id_cliente"));
                c.setIdentificador(rs.getString("NIT"));
                c.setNombre(rs.getString("nombre"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                c.setActivo(rs.getBoolean("activo"));
                c.setSaldoPendiente(rs.getDouble("saldo_pendiente"));

                lista.add(c);
            }
        }

        return lista;
    }

    public boolean modificar(Cliente c) throws SQLException {

        String sql = "UPDATE clientes SET "
                + "NIT=?, nombre=?, telefono=?, correo=? "
                + "WHERE id_cliente=?";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getIdentificador());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            ps.setInt(5, c.getIdCliente());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean desactivar(int id) throws SQLException {

        String sql = "UPDATE clientes SET activo=0 "
                + "WHERE id_cliente=?";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean activar(int id) throws SQLException {

        String sql = "UPDATE clientes SET activo=1 "
                + "WHERE id_cliente=?";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}