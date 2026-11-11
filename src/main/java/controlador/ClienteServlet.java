/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.ClienteDAO;
import modelo.Cliente;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO dao = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("clientes", dao.listar());
            request.getRequestDispatcher("/clientes.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error al consultar clientes", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        try {

            if ("registrar".equals(accion)
                    || "modificar".equals(accion)) {

                Cliente c = new Cliente();

                c.setIdentificador(
                    request.getParameter("identificador").trim());
                c.setNombre(
                    request.getParameter("nombre").trim());
                c.setTelefono(request.getParameter("telefono"));
                c.setCorreo(request.getParameter("correo"));

                if (c.getIdentificador().isEmpty()
                        || c.getNombre().isEmpty()) {
                    request.setAttribute("error",
                            "El NIT y el nombre son obligatorios.");
                    doGet(request, response);
                    return;
                }

                if ("registrar".equals(accion)) {
                    dao.registrar(c);
                } else {
                    c.setIdCliente(Integer.parseInt(
                        request.getParameter("id_cliente")));
                    dao.modificar(c);
                }

            } else if ("desactivar".equals(accion)) {

                int id = Integer.parseInt(
                    request.getParameter("id_cliente"));
                dao.desactivar(id);

            } else if ("activar".equals(accion)) {

                int id = Integer.parseInt(
                    request.getParameter("id_cliente"));
                dao.activar(id);

            } else {
                request.setAttribute("error", "Accion no valida.");
                doGet(request, response);
                return;
            }

            response.sendRedirect(
                request.getContextPath() + "/clientes");

        } catch (SQLException e) {

            String mensaje = e.getErrorCode() == 1062
                    ? "El NIT ya esta registrado."
                    : "No se pudo guardar el cliente.";

            request.setAttribute("error", mensaje);
            doGet(request, response);

        } catch (NumberFormatException | NullPointerException e) {

            request.setAttribute("error", "Datos no validos.");
            doGet(request, response);
        }
    }
}