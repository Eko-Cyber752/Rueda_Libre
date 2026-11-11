<%-- 
    Document   : equipos
    Created on : 10/11/2026, 7:55:00 p. m.
    Author     : Jon
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List,modelo.Equipo"%>

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
    <title>Equipos - Rueda Libre</title>
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

    <h2>Gestión de Equipos</h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
            <%= limpiar((String)request.getAttribute("error")) %>
        </div>
    <% } %>

    <div class="card mb-4">
        <div class="card-header bg-primary text-white">
            Registrar Equipo
        </div>

        <div class="card-body">
            <form action="<%= request.getContextPath() %>/equipos"
                  method="post" class="row g-2">

                <input type="hidden" name="accion" value="registrar">

                <div class="col-md-4">
                    <label>Código</label>
                    <input name="codigo" class="form-control"
                           maxlength="30" required>
                </div>

                <div class="col-md-4">
                    <label>Tipo</label>
                    <select name="tipo" class="form-select">
                        <option value="URBANA">Bicicleta urbana</option>
                        <option value="MONTANA">Bicicleta montaña</option>
                        <option value="SCOOTER">Scooter eléctrico</option>
                    </select>
                </div>

                <div class="col-md-4">
                    <label>Modelo</label>
                    <input name="modelo" class="form-control" required>
                </div>

                <div class="col-md-4">
                    <label>Tarifa por hora (Q)</label>
                    <input type="number" name="tarifa_hora"
                           min="0" step="0.01"
                           class="form-control" required>
                </div>

                <div class="col-md-4">
                    <label>Precio de reposición (Q)</label>
                    <input type="number" name="precio_reposicion"
                           min="0" step="0.01"
                           class="form-control" required>
                </div>

                <div class="col-md-4">
                    <label>Batería % (solo scooters)</label>
                    <input type="number" name="bateria"
                           min="0" max="100"
                           class="form-control">
                </div>

                <div class="col-12">
                    <button class="btn btn-primary">
                        Guardar Equipo
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-header bg-primary text-white">
            Equipos Registrados
        </div>

        <div class="card-body table-responsive">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Tipo</th>
                        <th>Modelo</th>
                        <th>Tarifa</th>
                        <th>Reposición</th>
                        <th>Estado</th>
                        <th>Batería</th>
                        <th>Alquileres</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>
                <%
                    List<Equipo> equipos =
                        (List<Equipo>)request.getAttribute("equipos");

                    if (equipos != null) {
                        for (Equipo e : equipos) {
                %>

                <tr>
                    <td><%= limpiar(e.getCodigo()) %></td>
                    <td><%= limpiar(e.getTipo()) %></td>
                    <td><%= limpiar(e.getModelo()) %></td>
                    <td>Q <%= e.getTarifaHora() %></td>
                    <td>Q <%= e.getPrecioReposicion() %></td>

                    <td>
                        <% if ("DISPONIBLE".equals(e.getEstado())) { %>
                            <span class="badge bg-success">
                                DISPONIBLE
                            </span>
                        <% } else if ("DADO DE BAJA".equals(e.getEstado())) { %>
                            <span class="badge bg-danger">
                                DADO DE BAJA
                            </span>
                        <% } else { %>
                            <%= limpiar(e.getEstado()) %>
                        <% } %>
                    </td>

                    <td>
                        <%= e.getBateria() == null
                            ? "N/A" : e.getBateria() + "%" %>
                    </td>

                    <td>
                        <%= e.getAlquileresDesdeMantenimiento() %>
                    </td>

                    <td>
                        <% if ("DISPONIBLE".equals(e.getEstado())) { %>

                            <button type="button"
                                    class="btn btn-warning btn-sm"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#editar<%= e.getIdEquipo() %>">
                                Editar
                            </button>

                            <form action="<%= request.getContextPath() %>/equipos"
                                  method="post" class="d-inline">

                                <input type="hidden" name="accion" value="baja">
                                <input type="hidden" name="id_equipo"
                                       value="<%= e.getIdEquipo() %>">

                                <button class="btn btn-danger btn-sm"
                                        onclick="return confirm('¿Dar de baja este equipo?')">
                                    Dar de baja
                                </button>
                            </form>

                        <% } else if ("DADO DE BAJA".equals(e.getEstado())) { %>

                            <form action="<%= request.getContextPath() %>/equipos"
                                  method="post" class="d-inline">

                                <input type="hidden" name="accion"
                                       value="reactivar">

                                <input type="hidden" name="id_equipo"
                                       value="<%= e.getIdEquipo() %>">

                                <button class="btn btn-success btn-sm"
                                        onclick="return confirm('¿Reactivar este equipo?')">
                                    Reactivar
                                </button>
                            </form>

                        <% } %>
                    </td>
                </tr>

                <tr class="collapse" id="editar<%= e.getIdEquipo() %>">
                    <td colspan="9">
                        <form action="<%= request.getContextPath() %>/equipos"
                              method="post" class="row g-2">

                            <input type="hidden" name="accion"
                                   value="modificar">

                            <input type="hidden" name="id_equipo"
                                   value="<%= e.getIdEquipo() %>">

                            <div class="col-md-2">
                                <label>Código</label>
                                <input name="codigo" class="form-control"
                                       value="<%= limpiar(e.getCodigo()) %>"
                                       required>
                            </div>

                            <div class="col-md-2">
                                <label>Tipo</label>
                                <select name="tipo" class="form-select">
                                    <option value="URBANA"
                                        <%= "URBANA".equals(e.getTipo()) ? "selected" : "" %>>
                                        Urbana
                                    </option>
                                    <option value="MONTANA"
                                        <%= "MONTANA".equals(e.getTipo()) ? "selected" : "" %>>
                                        Montaña
                                    </option>
                                    <option value="SCOOTER"
                                        <%= "SCOOTER".equals(e.getTipo()) ? "selected" : "" %>>
                                        Scooter
                                    </option>
                                </select>
                            </div>

                            <div class="col-md-2">
                                <label>Modelo</label>
                                <input name="modelo" class="form-control"
                                       value="<%= limpiar(e.getModelo()) %>"
                                       required>
                            </div>

                            <div class="col-md-2">
                                <label>Tarifa</label>
                                <input type="number" name="tarifa_hora"
                                       class="form-control" min="0" step="0.01"
                                       value="<%= e.getTarifaHora() %>"
                                       required>
                            </div>

                            <div class="col-md-2">
                                <label>Reposición</label>
                                <input type="number" name="precio_reposicion"
                                       class="form-control" min="0" step="0.01"
                                       value="<%= e.getPrecioReposicion() %>"
                                       required>
                            </div>

                            <div class="col-md-2">
                                <label>Batería</label>
                                <input type="number" name="bateria"
                                       class="form-control" min="0" max="100"
                                       value="<%= e.getBateria() == null
                                           ? "" : e.getBateria() %>">
                            </div>

                            <div class="col-12">
                                <button class="btn btn-success btn-sm">
                                    Guardar Cambios
                                </button>
                            </div>
                        </form>
                    </td>
                </tr>

                <%
                        }
                    }
                %>
                </tbody>
            </table>

            <% if (equipos == null || equipos.isEmpty()) { %>
                <p class="text-center text-muted">
                    No hay equipos registrados.
                </p>
            <% } %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
