
package controlador;

import dao.EquipoDAO;
import modelo.Equipo;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/equipos")
public class EquipoServlet extends HttpServlet {

    private final EquipoDAO dao = new EquipoDAO();

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("equipos", dao.listar());
            req.getRequestDispatcher("/equipos.jsp")
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
        String accion = req.getParameter("accion");

        try {
            boolean guardado = false;

            if ("registrar".equals(accion) ||
                "modificar".equals(accion)) {

                Equipo e = new Equipo();

                e.setCodigo(req.getParameter("codigo").trim());
                e.setTipo(req.getParameter("tipo"));
                e.setModelo(req.getParameter("modelo").trim());

                e.setTarifaHora(new BigDecimal(
                    req.getParameter("tarifa_hora")));

                e.setPrecioReposicion(new BigDecimal(
                    req.getParameter("precio_reposicion")));

                String bateria = req.getParameter("bateria");

                if ("SCOOTER".equals(e.getTipo())) {
                    e.setBateria(Integer.parseInt(bateria));
                } else {
                    e.setBateria(null);
                }

                if (e.getCodigo().isEmpty() ||
                    e.getModelo().isEmpty() ||
                    e.getTarifaHora().signum() < 0 ||
                    e.getPrecioReposicion().signum() < 0 ||
                    (e.getBateria() != null &&
                     (e.getBateria() < 0 || e.getBateria() > 100))) {
                    throw new IllegalArgumentException();
                }

                if (!"URBANA".equals(e.getTipo()) &&
                    !"MONTANA".equals(e.getTipo()) &&
                    !"SCOOTER".equals(e.getTipo())) {
                    throw new IllegalArgumentException();
                }

                if ("registrar".equals(accion)) {
                    guardado = dao.registrar(e);
                } else {
                    e.setIdEquipo(Integer.parseInt(
                        req.getParameter("id_equipo")));
                    guardado = dao.modificar(e);
                }

            } else if ("baja".equals(accion)) {

                int id = Integer.parseInt(
                    req.getParameter("id_equipo"));
                guardado = dao.darDeBaja(id);

            } else if ("reactivar".equals(accion)) {

                int id = Integer.parseInt(
                    req.getParameter("id_equipo"));
                guardado = dao.reactivar(id);
            }

            if (!guardado) {
                req.setAttribute("error",
                    "No se pudo realizar la operación. "
                    + "Revisa el estado del equipo.");
                doGet(req, resp);
                return;
            }

            resp.sendRedirect(
                req.getContextPath() + "/equipos");

        } catch (SQLException e) {

            req.setAttribute("error", e.getErrorCode() == 1062
                ? "El código del equipo ya existe."
                : "Error al guardar el equipo.");
            doGet(req, resp);

        } catch (IllegalArgumentException | NullPointerException e) {

            req.setAttribute("error",
                "Revisa los datos, precios y batería.");
            doGet(req, resp);
        }
    }
}
