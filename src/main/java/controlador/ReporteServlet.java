/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.ReporteDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/reportes")
public class ReporteServlet extends HttpServlet {

    ReporteDAO dao = new ReporteDAO();

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String tipo = req.getParameter("tipo");
        String inicio = req.getParameter("inicio");
        String fin = req.getParameter("fin");

        if (tipo == null || tipo.isEmpty()) {
            tipo = "ingresos";
        }

        try {
            if (inicio != null && !inicio.isEmpty()
                    && fin != null && !fin.isEmpty()
                    && inicio.compareTo(fin) > 0) {
                throw new IllegalArgumentException(
                    "La fecha inicial es mayor que la final.");
            }

            List<Map<String, Object>> datos =
                    dao.listar(tipo, inicio, fin);

            req.setAttribute("datos", datos);

        } catch (SQLException | IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("datos", new ArrayList<>());
        }

        req.setAttribute("tipo", tipo);
        req.setAttribute("inicio", inicio == null ? "" : inicio);
        req.setAttribute("fin", fin == null ? "" : fin);

        req.getRequestDispatcher("/reportes.jsp")
           .forward(req, resp);
    }
}