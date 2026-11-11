/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.MantenimientoDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/mantenimiento")
public class MantenimientoServlet extends HttpServlet {

    private final MantenimientoDAO dao = new MantenimientoDAO();

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("equipos", dao.equiposDisponibles());
            req.setAttribute("mantenimientos", dao.listar());

            req.getRequestDispatcher("/mantenimiento.jsp")
               .forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        try {
            String accion = req.getParameter("accion");

            if ("registrar".equals(accion)) {

                int idEquipo = Integer.parseInt(
                    req.getParameter("id_equipo"));

                String motivo = req.getParameter("motivo").trim();

                BigDecimal costo = new BigDecimal(
                    req.getParameter("costo"));

                if (idEquipo <= 0 || motivo.isEmpty()
                        || costo.signum() < 0) {
                    throw new IllegalArgumentException();
                }

                dao.registrar(idEquipo, motivo, costo);

            } else if ("finalizar".equals(accion)) {

                int id = Integer.parseInt(
                    req.getParameter("id_mantenimiento"));

                if (id <= 0) {
                    throw new IllegalArgumentException();
                }

                dao.finalizar(id);

            } else {
                throw new IllegalArgumentException();
            }

            resp.sendRedirect(
                req.getContextPath() + "/mantenimiento");

        } catch (SQLException e) {
            req.setAttribute("error",
                "No se pudo realizar la operacion: "
                + e.getMessage());
            doGet(req, resp);

        } catch (IllegalArgumentException |
                 NullPointerException e) {

            req.setAttribute("error",
                "Revisa los datos ingresados.");
            doGet(req, resp);
        }
    }
}