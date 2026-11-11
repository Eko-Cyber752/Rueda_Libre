/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.*;
import modelo.Alquileres;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/alquileres")
public class AlquileresServlet extends HttpServlet {

    AlquileresDAO dao = new AlquileresDAO();

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("clientes", new ClienteDAO().listar());
            req.setAttribute("equipos", new EquipoDAO().listar());
            req.setAttribute("accesorios", new AccesorioDAO().listar());
            req.setAttribute("alquileres", dao.listarAlquileres());

            String detalle = req.getParameter("detalle");

            if (detalle != null) {
                int id = Integer.parseInt(detalle);

                req.setAttribute("detalleId", id);
                req.setAttribute("detalleEquipos",
                    dao.equiposDelAlquiler(id));
                req.setAttribute("detalleAccesorios",
                    dao.accesoriosDelAlquiler(id));
            }

            req.getRequestDispatcher("/alquileres.jsp")
               .forward(req, resp);

        } catch (SQLException | NumberFormatException e) {
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

            if ("cancelar".equals(accion)) {

                int id = Integer.parseInt(
                    req.getParameter("id_alquiler"));

                dao.cancelar(id);

            } else if ("entregar".equals(accion)) {

                int id = Integer.parseInt(
                    req.getParameter("id_alquiler"));

                BigDecimal efectivo = new BigDecimal(
                    req.getParameter("monto_recibido"));

                dao.entregar(id, efectivo);

            } else if ("reservar".equals(accion)) {

                Alquileres a = new Alquileres();

                a.setIdCliente(Integer.parseInt(
                    req.getParameter("id_cliente")));

                a.setFechaEstimada(Timestamp.valueOf(
                    LocalDateTime.parse(
                        req.getParameter("fecha_estimada"))));

                String[] seleccion =
                    req.getParameterValues("equipos");

                if (seleccion == null || seleccion.length == 0) {
                    throw new IllegalArgumentException(
                        "Selecciona al menos un equipo.");
                }

                List<Integer> equipos = new ArrayList<>();

                for (String id : seleccion) {
                    equipos.add(Integer.parseInt(id));
                }

                a.setEquipos(equipos);

                Map<Integer, Integer> accesorios =
                    new HashMap<>();

                String[] seleccionAcc =
                    req.getParameterValues("accesorios");

                if (seleccionAcc != null) {
                    for (String id : seleccionAcc) {

                        int idAcc = Integer.parseInt(id);

                        int cantidad = Integer.parseInt(
                            req.getParameter("cantidad_" + idAcc));

                        if (cantidad <= 0) {
                            throw new IllegalArgumentException(
                                "Cantidad incorrecta.");
                        }

                        accesorios.put(idAcc, cantidad);
                    }
                }

                a.setAccesorios(accesorios);
                dao.reservar(a);

            } else if ("devolver".equals(accion)) {

                int id = Integer.parseInt(
                    req.getParameter("id_alquiler"));

                String metodo = req.getParameter("metodo");
                String referencia = req.getParameter("referencia");

                BigDecimal efectivo = BigDecimal.ZERO;

                if ("EFECTIVO".equals(metodo)) {
                    efectivo = new BigDecimal(
                        req.getParameter("monto_recibido"));
                }

                Map<Integer, Boolean> danados = new HashMap<>();
                Map<Integer, Integer> extraviados = new HashMap<>();

                // Identificar equipos dañados.
                for (Map<String, Object> e :
                        dao.equiposDelAlquiler(id)) {

                    int idEquipo = (Integer)e.get("id");

                    String valor = req.getParameter(
                        "danado_" + idEquipo);

                    danados.put(idEquipo,
                        "SI".equals(valor));
                }

                // Leer cantidades de accesorios extraviados.
                for (Map<String, Object> a :
                        dao.accesoriosDelAlquiler(id)) {

                    int idAccesorio = (Integer)a.get("id");

                    int cantidad = Integer.parseInt(
                        req.getParameter(
                            "extraviado_" + idAccesorio));

                    extraviados.put(idAccesorio, cantidad);
                }

                dao.devolver(id, danados, extraviados,
                    metodo, efectivo, referencia);

            } else {
                throw new IllegalArgumentException(
                    "Acción desconocida.");
            }

            resp.sendRedirect(
                req.getContextPath() + "/alquileres");

        } catch (SQLException e) {

            req.setAttribute("error", e.getMessage());
            doGet(req, resp);

        } catch (IllegalArgumentException |
                 NullPointerException e) {

            req.setAttribute("error",
                "Revisa los datos ingresados.");
            doGet(req, resp);
        }
    }
}