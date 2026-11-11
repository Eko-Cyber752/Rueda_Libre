/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.AccesorioDAO;
import modelo.Accesorio;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/accesorios")
public class AccesorioServlet extends HttpServlet {

    private final AccesorioDAO dao = new AccesorioDAO();

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("accesorios", dao.listar());
            req.setAttribute("movimientos", dao.historial());

            req.getRequestDispatcher("/accesorios.jsp")
               .forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(
                "Error al consultar accesorios", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");

        try {
            if ("registrar".equals(accion) ||
                "modificar".equals(accion)) {

                Accesorio a = new Accesorio();

                a.setCodigo(req.getParameter("codigo").trim());
                a.setNombre(req.getParameter("nombre").trim());

                a.setTarifaFija(new BigDecimal(
                    req.getParameter("tarifa_fija")));

                a.setPrecioReposicion(new BigDecimal(
                    req.getParameter("precio_reposicion")));

                if (a.getCodigo().isEmpty() ||
                    a.getNombre().isEmpty() ||
                    a.getTarifaFija().signum() < 0 ||
                    a.getPrecioReposicion().signum() < 0) {
                    throw new IllegalArgumentException();
                }

                if ("registrar".equals(accion)) {
                    a.setExistencia(Integer.parseInt(
                        req.getParameter("existencia")));

                    if (a.getExistencia() < 0) {
                        throw new IllegalArgumentException();
                    }

                    dao.registrar(a);

                } else {
                    a.setIdAccesorio(Integer.parseInt(
                        req.getParameter("id_accesorio")));

                    if (!dao.modificar(a)) {
                        throw new IllegalArgumentException();
                    }
                }

            } else if ("abastecer".equals(accion)) {

                int id = Integer.parseInt(
                    req.getParameter("id_accesorio"));

                int cantidad = Integer.parseInt(
                    req.getParameter("cantidad"));

                if (id <= 0 || cantidad <= 0) {
                    throw new IllegalArgumentException();
                }

                dao.abastecer(id, cantidad);

            } else {
                throw new IllegalArgumentException();
            }

            resp.sendRedirect(
                req.getContextPath() + "/accesorios");

        } catch (SQLException e) {

            String mensaje = e.getErrorCode() == 1062
                ? "El codigo ya existe."
                : "No se pudo guardar. Revisa los datos.";

            req.setAttribute("error", mensaje);
            doGet(req, resp);

        } catch (NumberFormatException | NullPointerException e) {

            req.setAttribute("error",
                "Revisa las cantidades y los precios.");
            doGet(req, resp);

        } catch (IllegalArgumentException e) {

            req.setAttribute("error",
                "Los datos no son validos.");
            doGet(req, resp);
        }
    }
}