<%-- 
    Document   : comprobante
    Created on : 11/11/2026, 8:19:19 p. m.
    Author     : adele
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List,java.util.Map"%>

<%!
    String limpiar(Object dato) {
        if (dato == null) return "";
        return dato.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    Map<String, Object> c =
        (Map<String, Object>)
        request.getAttribute("comprobante");

    if (c == null) {
        response.sendRedirect(
            request.getContextPath() + "/alquileres");
        return;
    }

    List<Map<String, Object>> pagos =
        (List<Map<String, Object>>) c.get("pagos");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Comprobante - Rueda Libre</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        @media print {
            .no-imprimir { display: none; }
            .card { border: none; }
        }
    </style>
</head>

<body class="bg-light">

<div class="container mt-4 mb-5" style="max-width:750px">

    <div class="no-imprimir mb-3">

        <a href="<%= request.getContextPath() %>/alquileres"
           class="btn btn-secondary">
            Volver
        </a>

        <button onclick="window.print()"
                class="btn btn-primary">
            Imprimir / Guardar PDF
        </button>

    </div>

    <div class="card">
        <div class="card-body p-4">

            <h2 class="text-center text-primary">
                RUEDA LIBRE
            </h2>

            <h5 class="text-center">
                Comprobante de alquiler
            </h5>

            <p class="text-center">
                Número: <%= c.get("id") %>
            </p>

            <hr>

            <p>
                <strong>Cliente:</strong>
                <%= limpiar(c.get("cliente")) %>
            </p>

            <p>
                <strong>NIT:</strong>
                <%= limpiar(c.get("nit")) %>
            </p>

            <p>
                <strong>Fecha de entrega:</strong>
                <%= limpiar(c.get("entrega")) %>
            </p>

            <p>
                <strong>Fecha de devolución:</strong>
                <%= limpiar(c.get("devolucion")) %>
            </p>

            <h5 class="mt-4">Detalle del cobro</h5>

            <table class="table table-bordered">

                <tr>
                    <td>Alquiler de equipos</td>
                    <td class="text-end">
                        Q <%= c.get("equipos") %>
                    </td>
                </tr>

                <tr>
                    <td>Accesorios</td>
                    <td class="text-end">
                        Q <%= c.get("accesorios") %>
                    </td>
                </tr>

                <tr>
                    <td>Recargo por retraso</td>
                    <td class="text-end">
                        Q <%= c.get("retraso") %>
                    </td>
                </tr>

                <tr>
                    <td>Daños y pérdidas</td>
                    <td class="text-end">
                        Q <%= c.get("danos") %>
                    </td>
                </tr>

                <tr class="table-primary">
                    <th>Total</th>
                    <th class="text-end">
                        Q <%= c.get("total") %>
                    </th>
                </tr>

            </table>

            <h5 class="mt-4">Depósito de garantía</h5>

            <table class="table table-bordered">

                <tr>
                    <td>Depósito recibido</td>
                    <td class="text-end">
                        Q <%= c.get("deposito") %>
                    </td>
                </tr>

                <tr>
                    <td>Depósito aplicado a daños</td>
                    <td class="text-end">
                        Q <%= c.get("aplicado") %>
                    </td>
                </tr>

                <tr>
                    <td>Depósito devuelto</td>
                    <td class="text-end">
                        Q <%= c.get("devuelto") %>
                    </td>
                </tr>

                <tr>
                    <td>Saldo pendiente generado</td>
                    <td class="text-end">
                        Q <%= c.get("saldo") %>
                    </td>
                </tr>

            </table>

            <h5 class="mt-4">Pagos registrados</h5>

            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Tipo</th>
                        <th>Método</th>
                        <th>Monto</th>
                        <th>Estado</th>
                    </tr>
                </thead>

                <tbody>

                    <% if (pagos != null) {
                        for (Map<String, Object> p : pagos) {
                    %>

                    <tr>
                        <td><%= limpiar(p.get("tipo")) %></td>
                        <td><%= limpiar(p.get("metodo")) %></td>
                        <td>Q <%= p.get("monto") %></td>
                        <td><%= limpiar(p.get("estado")) %></td>
                    </tr>

                    <% }
                    } %>

                </tbody>
            </table>

            <hr>

            <p class="text-center text-muted">
                Gracias por utilizar Rueda Libre.
            </p>

        </div>
    </div>

</div>

</body>
</html>