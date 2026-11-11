<%-- 
    Document   : clientes
    Created on : 10/11/2026, 4:49:33 p. m.
    Author     : adele - Jonh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List,modelo.Cliente"%>

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
    <title>Clientes - Rueda Libre</title>
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

    <h2>Gestión de Clientes</h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
            <%= limpiar((String) request.getAttribute("error")) %>
        </div>
    <% } %>

    <div class="card mb-4">
        <div class="card-header bg-primary text-white">
            Registrar Cliente
        </div>

        <div class="card-body">
            <form action="<%= request.getContextPath() %>/clientes"
                  method="post" class="row g-2">

                <input type="hidden" name="accion" value="registrar">

                <div class="col-md-3">
                    <input name="identificador" class="form-control"
                           placeholder="NIT" maxlength="30" required>
                </div>

                <div class="col-md-3">
                    <input name="nombre" class="form-control"
                           placeholder="Nombre" maxlength="100" required>
                </div>

                <div class="col-md-3">
                    <input name="telefono" class="form-control"
                           placeholder="Teléfono" maxlength="20">
                </div>

                <div class="col-md-3">
                    <input type="email" name="correo"
                           class="form-control"
                           placeholder="Correo" maxlength="100">
                </div>

                <div class="col-12">
                    <button class="btn btn-primary">
                        Guardar Cliente
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-header bg-primary text-white">
            Clientes Registrados
        </div>

        <div class="card-body table-responsive">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>NIT</th>
                        <th>Nombre</th>
                        <th>Teléfono</th>
                        <th>Correo</th>
                        <th>Saldo</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>
                <%
                    List<Cliente> clientes =
                        (List<Cliente>) request.getAttribute("clientes");

                    if (clientes != null) {
                        for (Cliente c : clientes) {
                %>
                <tr>
                    <td><%= c.getIdCliente() %></td>
                    <td><%= limpiar(c.getIdentificador()) %></td>
                    <td><%= limpiar(c.getNombre()) %></td>
                    <td><%= limpiar(c.getTelefono()) %></td>
                    <td><%= limpiar(c.getCorreo()) %></td>
                    <td>Q <%= c.getSaldoPendiente() %></td>

                    <td>
                        <% if (c.isActivo()) { %>
                            <span class="badge bg-success">Activo</span>
                        <% } else { %>
                            <span class="badge bg-danger">Inactivo</span>
                        <% } %>
                    </td>

                    <td>
                        <button class="btn btn-warning btn-sm"
                                data-bs-toggle="collapse"
                                data-bs-target="#editar<%= c.getIdCliente() %>">
                            Editar
                        </button>

                        <form action="<%= request.getContextPath() %>/clientes"
                              method="post" class="d-inline">

                            <input type="hidden" name="id_cliente"
                                   value="<%= c.getIdCliente() %>">

                            <% if (c.isActivo()) { %>
                                <input type="hidden" name="accion"
                                       value="desactivar">
                                <button class="btn btn-danger btn-sm">
                                    Desactivar
                                </button>
                            <% } else { %>
                                <input type="hidden" name="accion"
                                       value="activar">
                                <button class="btn btn-success btn-sm">
                                    Activar
                                </button>
                            <% } %>
                        </form>
                    </td>
                </tr>

                <tr class="collapse" id="editar<%= c.getIdCliente() %>">
                    <td colspan="8">
                        <form action="<%= request.getContextPath() %>/clientes"
                              method="post" class="row g-2">

                            <input type="hidden" name="accion"
                                   value="modificar">
                            <input type="hidden" name="id_cliente"
                                   value="<%= c.getIdCliente() %>">

                            <div class="col-md-3">
                                <input name="identificador"
                                       class="form-control"
                                       value="<%= limpiar(c.getIdentificador()) %>"
                                       required>
                            </div>

                            <div class="col-md-3">
                                <input name="nombre" class="form-control"
                                       value="<%= limpiar(c.getNombre()) %>"
                                       required>
                            </div>

                            <div class="col-md-3">
                                <input name="telefono" class="form-control"
                                       value="<%= limpiar(c.getTelefono()) %>">
                            </div>

                            <div class="col-md-3">
                                <input type="email" name="correo"
                                       class="form-control"
                                       value="<%= limpiar(c.getCorreo()) %>">
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

            <% if (clientes == null || clientes.isEmpty()) { %>
                <p class="text-center text-muted">
                    No hay clientes registrados.
                </p>
            <% } %>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
