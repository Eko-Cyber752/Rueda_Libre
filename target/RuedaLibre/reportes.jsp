<%-- 
    Document   : reporte
    Created on : 11/11/2026, 7:59:44 p. m.
    Author     : adele
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="java.math.BigDecimal"%>

<%!
    String limpiar(Object valor) {
        if (valor == null) return "";
        return valor.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    List<Map<String,Object>> datos =
        (List<Map<String,Object>>)request.getAttribute("datos");

    String tipo = (String)request.getAttribute("tipo");
    String inicio = (String)request.getAttribute("inicio");
    String fin = (String)request.getAttribute("fin");

    String titulo = "";

    if ("ingresos".equals(tipo)) titulo = "Ingresos por fechas";
    if ("equipos".equals(tipo)) titulo = "Equipos más alquilados";
    if ("retrasos".equals(tipo)) titulo = "Alquileres con retrasos";
    if ("mantenimientos".equals(tipo)) titulo = "Costos de mantenimiento";
    if ("inventario".equals(tipo)) titulo = "Accesorios con pocas existencias";
    if ("deudores".equals(tipo)) titulo = "Clientes con deudas";

    BigDecimal total = BigDecimal.ZERO;

    if (datos != null) {
        for (Map<String,Object> fila : datos) {
            Object valor = null;

            if ("ingresos".equals(tipo)) valor = fila.get("Total_Q");
            if ("retrasos".equals(tipo)) valor = fila.get("Recargo_Q");
            if ("mantenimientos".equals(tipo)) valor = fila.get("Costo_Q");
            if ("deudores".equals(tipo)) valor = fila.get("Deuda_Q");

            if (valor != null) {
                total = total.add(new BigDecimal(valor.toString()));
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Reportes - Rueda Libre</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        @media print {
            .no-imprimir { display: none !important; }
            body { background: white !important; }
            .card { border: none !important; }
        }
    </style>
</head>

<body class="bg-light">

<nav class="navbar navbar-dark bg-primary no-imprimir">
    <div class="container">
        <a class="navbar-brand"
           href="<%= request.getContextPath() %>/index.html">
            Rueda Libre
        </a>
    </div>
</nav>

<div class="container mt-4 mb-5">

    <div class="no-imprimir">

        <a href="<%= request.getContextPath() %>/index.html"
           class="btn btn-secondary mb-3">
            Volver
        </a>

        <h2>Gestión de Reportes</h2>

        <form action="<%= request.getContextPath() %>/reportes"
              method="get" class="card card-body mb-4">

            <label class="form-label">Seleccionar reporte</label>

            <select name="tipo" class="form-select mb-3">

                <option value="ingresos"
                    <%= "ingresos".equals(tipo) ? "selected" : "" %>>
                    1. Ingresos por fechas
                </option>

                <option value="equipos"
                    <%= "equipos".equals(tipo) ? "selected" : "" %>>
                    2. Equipos más alquilados
                </option>

                <option value="retrasos"
                    <%= "retrasos".equals(tipo) ? "selected" : "" %>>
                    3. Retrasos y recargos
                </option>

                <option value="mantenimientos"
                    <%= "mantenimientos".equals(tipo) ? "selected" : "" %>>
                    4. Costos de mantenimiento
                </option>

                <option value="inventario"
                    <%= "inventario".equals(tipo) ? "selected" : "" %>>
                    5. Inventario bajo
                </option>

                <option value="deudores"
                    <%= "deudores".equals(tipo) ? "selected" : "" %>>
                    6. Clientes deudores
                </option>

            </select>

            <p class="text-muted small">
                Las fechas se aplican al reporte de ingresos.
                Puedes dejarlas vacías para consultar todo.
            </p>

            <div class="row mb-3">

                <div class="col-md-6">
                    <label class="form-label">Desde</label>

                    <input type="date" name="inicio"
                           class="form-control"
                           value="<%= limpiar(inicio) %>">
                </div>

                <div class="col-md-6">
                    <label class="form-label">Hasta</label>

                    <input type="date" name="fin"
                           class="form-control"
                           value="<%= limpiar(fin) %>">
                </div>

            </div>

            <button type="submit" class="btn btn-primary">
                Consultar reporte
            </button>

        </form>

    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger no-imprimir">
            <%= limpiar(request.getAttribute("error")) %>
        </div>
    <% } %>

    <div class="card">

        <div class="card-body">

            <h2 class="text-center text-primary">
                RUEDA LIBRE
            </h2>

            <h4 class="text-center"><%= titulo %></h4>

            <p class="text-center text-muted">
                Reporte generado por el sistema
            </p>

            <hr>

            <% if (datos != null && !datos.isEmpty()) { %>

            <div class="table-responsive">

                <table class="table table-bordered table-striped">

                    <thead>
                        <tr>
                            <% for (String columna :
                                    datos.get(0).keySet()) { %>

                                <th><%= limpiar(columna)
                                    .replace("_", " ") %></th>

                            <% } %>
                        </tr>
                    </thead>

                    <tbody>

                        <% for (Map<String,Object> fila : datos) { %>
                        <tr>
                            <% for (Object valor : fila.values()) { %>
                                <td><%= limpiar(valor) %></td>
                            <% } %>
                        </tr>
                        <% } %>

                    </tbody>

                </table>

            </div>

            <p><strong>Registros encontrados:</strong>
                <%= datos.size() %>
            </p>

            <% if ("ingresos".equals(tipo)
                    || "retrasos".equals(tipo)
                    || "mantenimientos".equals(tipo)
                    || "deudores".equals(tipo)) { %>

                <h5>Total: Q <%= total %></h5>

            <% } %>

            <% } else { %>

                <p class="text-center text-muted">
                    No hay datos para este reporte.
                </p>

            <% } %>

        </div>
    </div>

    <div class="text-center mt-3 no-imprimir">

        <button onclick="window.print()"
                class="btn btn-success">
            Imprimir / Guardar PDF
        </button>

    </div>

</div>

</body>
</html>