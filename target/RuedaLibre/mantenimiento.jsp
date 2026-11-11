<%-- 
    Document   : mantenimiento
    Created on : 10/11/2026, 10:23:24 p. m.
    Author     : adele
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List,modelo.Equipo,modelo.Mantenimiento"%>

<%!
    String limpiar(String t) {
        if (t == null) return "";
        return t.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mantenimiento - Rueda Libre</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<nav class="navbar bg-primary">
    <div class="container">
        <a href="index.html" class="navbar-brand text-white">
            Rueda Libre
        </a>
    </div>
</nav>

<div class="container mt-4">

    <a href="index.html" class="btn btn-secondary mb-3">
        Volver
    </a>

    <h2>Gestión de Mantenimiento</h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
            <%= limpiar((String) request.getAttribute("error")) %>
        </div>
    <% } %>

    <div class="card mb-4">

        <div class="card-header bg-primary text-white">
            Registrar Mantenimiento
        </div>

        <div class="card-body">
            <form action="<%= request.getContextPath() %>/mantenimiento"
                  method="post" class="row g-3">

                <input type="hidden" name="accion" value="registrar">

                <div class="col-md-4">
                    <label class="form-label">Equipo</label>

                    <select name="id_equipo" class="form-select" required>
                        <option value="">Seleccione un equipo</option>

                        <%
                            List<Equipo> equipos =
                                (List<Equipo>) request.getAttribute("equipos");

                            if (equipos != null) {
                                for (Equipo e : equipos) {
                        %>
                            <option value="<%= e.getIdEquipo() %>">
                                <%= limpiar(e.getCodigo()) %>
                                - <%= limpiar(e.getModelo()) %>
                            </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Motivo</label>
                    <input name="motivo" class="form-control"
                           maxlength="200" required>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Costo (Q)</label>
                    <input type="number" name="costo"
                           min="0" step="0.01"
                           class="form-control" required>
                </div>

                <div class="col-12">
                    <button class="btn btn-primary">
                        Registrar Mantenimiento
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card">

        <div class="card-header bg-primary text-white">
            Historial de Mantenimientos
        </div>

        <div class="card-body table-responsive">

            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Equipo</th>
                        <th>Fecha de inicio</th>
                        <th>Fecha de fin</th>
                        <th>Motivo</th>
                        <th>Costo</th>
                        <th>Estado</th>
                        <th>Acción</th>
                    </tr>
                </thead>

                <tbody>
                <%
                    List<Mantenimiento> lista =
                        (List<Mantenimiento>)
                        request.getAttribute("mantenimientos");

                    if (lista != null) {
                        for (Mantenimiento m : lista) {
                %>
                    <tr>
                        <td><%= m.getIdMantenimiento() %></td>
                        <td><%= limpiar(m.getEquipo()) %></td>
                        <td><%= m.getFechaInicio() %></td>
                        <td><%= m.getFechaFin() == null
                            ? "Pendiente" : m.getFechaFin() %></td>
                        <td><%= limpiar(m.getMotivo()) %></td>
                        <td>Q <%= m.getCosto() %></td>

                        <td>
                            <% if ("ABIERTO".equals(m.getEstado())) { %>
                                <span class="badge bg-warning text-dark">
                                    ABIERTO
                                </span>
                            <% } else { %>
                                <span class="badge bg-success">
                                    CERRADO
                                </span>
                            <% } %>
                        </td>

                        <td>
                            <% if ("ABIERTO".equals(m.getEstado())) { %>
                                <form action="<%= request.getContextPath() %>/mantenimiento"
                                      method="post">

                                    <input type="hidden" name="accion"
                                           value="finalizar">

                                    <input type="hidden"
                                           name="id_mantenimiento"
                                           value="<%= m.getIdMantenimiento() %>">

                                    <button class="btn btn-success btn-sm"
                                            onclick="return confirm('¿Finalizar mantenimiento?')">
                                        Finalizar
                                    </button>
                                </form>
                            <% } %>
                        </td>
                    </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>

            <% if (lista == null || lista.isEmpty()) { %>
                <p class="text-muted text-center">
                    No hay mantenimientos registrados.
                </p>
            <% } %>

        </div>
    </div>
</div>

</body>
</html>