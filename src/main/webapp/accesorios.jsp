<%-- 
    Document   : Accesorios
    Created on : 10/11/2026, 9:57:58 p. m.
    Author     : adele
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List,java.util.Map,modelo.Accesorio"%>

<%!
    String limpiar(Object valor) {
        if (valor == null) return "";
        String t = valor.toString();

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
    <title>Accesorios - Rueda Libre</title>

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

    <h2>Gestión de Accesorios</h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
            <%= limpiar(request.getAttribute("error")) %>
        </div>
    <% } %>

    <div class="card mb-4">
        <div class="card-header bg-primary text-white">
            Registrar Accesorio
        </div>

        <div class="card-body">
            <form action="<%= request.getContextPath() %>/accesorios"
                  method="post" class="row g-2">

                <input type="hidden" name="accion" value="registrar">

                <div class="col-md-4">
                    <label>Código</label>
                    <input name="codigo" maxlength="30"
                           class="form-control" required>
                </div>

                <div class="col-md-4">
                    <label>Nombre</label>
                    <input name="nombre" maxlength="100"
                           class="form-control" required>
                </div>

                <div class="col-md-4">
                    <label>Existencia inicial</label>
                    <input type="number" name="existencia"
                           min="0" value="0"
                           class="form-control" required>
                </div>

                <div class="col-md-6">
                    <label>Tarifa fija (Q)</label>
                    <input type="number" name="tarifa_fija"
                           min="0" step="0.01"
                           class="form-control" required>
                </div>

                <div class="col-md-6">
                    <label>Precio de reposición (Q)</label>
                    <input type="number" name="precio_reposicion"
                           min="0" step="0.01"
                           class="form-control" required>
                </div>

                <div class="col-12 mt-3">
                    <button class="btn btn-primary">
                        Guardar Accesorio
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header bg-primary text-white">
            Accesorios Registrados
        </div>

        <div class="card-body table-responsive">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Nombre</th>
                        <th>Tarifa</th>
                        <th>Reposición</th>
                        <th>Existencia</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>
                <%
                    List<Accesorio> accesorios =
                        (List<Accesorio>) request.getAttribute("accesorios");

                    if (accesorios != null) {
                        for (Accesorio a : accesorios) {
                %>

                <tr>
                    <td><%= limpiar(a.getCodigo()) %></td>
                    <td><%= limpiar(a.getNombre()) %></td>
                    <td>Q <%= a.getTarifaFija() %></td>
                    <td>Q <%= a.getPrecioReposicion() %></td>
                    <td><%= a.getExistencia() %></td>

                    <td>
                        <button type="button"
                                class="btn btn-warning btn-sm"
                                data-bs-toggle="collapse"
                                data-bs-target="#editar<%= a.getIdAccesorio() %>">
                            Editar
                        </button>

                        <button type="button"
                                class="btn btn-success btn-sm"
                                data-bs-toggle="collapse"
                                data-bs-target="#stock<%= a.getIdAccesorio() %>">
                            Abastecer
                        </button>
                    </td>
                </tr>

                <tr class="collapse" id="editar<%= a.getIdAccesorio() %>">
                    <td colspan="6">
                        <form action="<%= request.getContextPath() %>/accesorios"
                              method="post" class="row g-2">

                            <input type="hidden" name="accion"
                                   value="modificar">

                            <input type="hidden" name="id_accesorio"
                                   value="<%= a.getIdAccesorio() %>">

                            <div class="col-md-3">
                                <label>Código</label>
                                <input name="codigo" class="form-control"
                                       value="<%= limpiar(a.getCodigo()) %>"
                                       required>
                            </div>

                            <div class="col-md-3">
                                <label>Nombre</label>
                                <input name="nombre" class="form-control"
                                       value="<%= limpiar(a.getNombre()) %>"
                                       required>
                            </div>

                            <div class="col-md-3">
                                <label>Tarifa fija</label>
                                <input type="number" name="tarifa_fija"
                                       class="form-control" min="0" step="0.01"
                                       value="<%= a.getTarifaFija() %>"
                                       required>
                            </div>

                            <div class="col-md-3">
                                <label>Reposición</label>
                                <input type="number" name="precio_reposicion"
                                       class="form-control" min="0" step="0.01"
                                       value="<%= a.getPrecioReposicion() %>"
                                       required>
                            </div>

                            <div class="col-12">
                                <button class="btn btn-success btn-sm">
                                    Guardar Cambios
                                </button>
                            </div>
                        </form>
                    </td>
                </tr>

                <tr class="collapse" id="stock<%= a.getIdAccesorio() %>">
                    <td colspan="6">
                        <form action="<%= request.getContextPath() %>/accesorios"
                              method="post" class="row g-2 align-items-end">

                            <input type="hidden" name="accion"
                                   value="abastecer">

                            <input type="hidden" name="id_accesorio"
                                   value="<%= a.getIdAccesorio() %>">

                            <div class="col-md-4">
                                <label>Cantidad que ingresará</label>
                                <input type="number" name="cantidad"
                                       min="1" class="form-control" required>
                            </div>

                            <div class="col-md-4">
                                <button class="btn btn-success">
                                    Agregar al inventario
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

            <% if (accesorios == null || accesorios.isEmpty()) { %>
                <p class="text-muted text-center">
                    No hay accesorios registrados.
                </p>
            <% } %>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header bg-primary text-white">
            Historial de Movimientos
        </div>

        <div class="card-body table-responsive">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Accesorio</th>
                        <th>Tipo</th>
                        <th>Cantidad</th>
                        <th>Alquiler</th>
                    </tr>
                </thead>

                <tbody>
                <%
                    List<Map<String, Object>> movimientos =
                        (List<Map<String, Object>>)
                        request.getAttribute("movimientos");

                    if (movimientos != null) {
                        for (Map<String, Object> m : movimientos) {
                %>
                <tr>
                    <td><%= limpiar(m.get("fecha")) %></td>
                    <td><%= limpiar(m.get("nombre")) %></td>
                    <td><%= limpiar(m.get("tipo")) %></td>
                    <td><%= m.get("cantidad") %></td>
                    <td>
                        <%= m.get("alquiler") == null
                            ? "Sin alquiler" : m.get("alquiler") %>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>

            <% if (movimientos == null || movimientos.isEmpty()) { %>
                <p class="text-muted text-center">
                    No hay movimientos registrados.
                </p>
            <% } %>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>