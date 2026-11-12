<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List,java.util.Map"%>
<%@page import="modelo.Cliente,modelo.Equipo,modelo.Accesorio"%>

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
    List<Cliente> clientes =
        (List<Cliente>) request.getAttribute("clientes");

    List<Equipo> equipos =
        (List<Equipo>) request.getAttribute("equipos");

    List<Accesorio> accesorios =
        (List<Accesorio>) request.getAttribute("accesorios");

    List<Map<String, Object>> alquileres =
        (List<Map<String, Object>>) request.getAttribute("alquileres");

    Integer detalleId =
        (Integer) request.getAttribute("detalleId");

    List<Map<String, Object>> detalleEquipos =
        (List<Map<String, Object>>) request.getAttribute("detalleEquipos");

    List<Map<String, Object>> detalleAccesorios =
        (List<Map<String, Object>>) request.getAttribute("detalleAccesorios");
%>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Alquileres - Rueda Libre</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<nav class="navbar navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand"
           href="<%= request.getContextPath() %>/index.html">
            Rueda Libre
        </a>
    </div>
</nav>

<div class="container mt-4 mb-5">

    <a href="<%= request.getContextPath() %>/index.html"
       class="btn btn-secondary mb-3">
        Volver al inicio
    </a>

    <h2>Gestión de Alquileres</h2>

    <p class="text-muted">
        Registrar reservas, entregar, cancelar y devolver equipos.
    </p>

    <% if (request.getAttribute("error") != null) { %>

    <div class="alert alert-danger">
        <%= limpiar(request.getAttribute("error")) %>
    </div>

    <% } %>

    <!-- REGISTRAR RESERVA -->
    <div class="card mb-4">

        <div class="card-header bg-primary text-white">
            Registrar nueva reserva
        </div>

        <div class="card-body">

            <form action="<%= request.getContextPath() %>/alquileres"
                  method="post">

                <input type="hidden"
                       name="accion"
                       value="reservar">

                <!-- CLIENTE -->
                <div class="mb-3">

                    <label class="form-label">Cliente</label>

                    <select name="id_cliente"
                            class="form-select"
                            required>

                        <option value="">
                            Seleccione un cliente
                        </option>

                        <% if (clientes != null) {
                            for (Cliente c : clientes) {
                                if (c.isActivo() &&
                                    c.getSaldoPendiente() <= 0) {
                        %>

                        <option value="<%= c.getIdCliente() %>">
                            <%= limpiar(c.getNombre()) %>
                            - <%= limpiar(c.getIdentificador()) %>
                        </option>

                        <% }
                            }
                        } %>

                    </select>

                </div>

                <!-- EQUIPOS -->
                <div class="mb-3">

                    <label class="form-label">
                        Equipos disponibles
                    </label>

                    <div class="border rounded p-3">

                        <%
                            int disponibles = 0;

                            if (equipos != null) {
                                for (Equipo e : equipos) {

                                    boolean disponible =
                                        "DISPONIBLE".equals(e.getEstado());

                                    boolean bateriaCorrecta =
                                        !"SCOOTER".equals(e.getTipo())
                                        || (e.getBateria() != null
                                        && e.getBateria() >= 30);

                                    if (disponible && bateriaCorrecta) {
                                        disponibles++;
                        %>

                        <div class="form-check mb-2">

                            <input class="form-check-input"
                                   type="checkbox"
                                   name="equipos"
                                   id="equipo<%= e.getIdEquipo() %>"
                                   value="<%= e.getIdEquipo() %>">

                            <label class="form-check-label"
                                   for="equipo<%= e.getIdEquipo() %>">

                                <%= limpiar(e.getCodigo()) %>
                                - <%= limpiar(e.getModelo()) %>
                                (<%= limpiar(e.getTipo()) %>)
                                - Q<%= e.getTarifaHora() %>/hora

                            </label>

                        </div>

                        <% }
                            }
                        } %>

                        <% if (disponibles == 0) { %>

                        <p class="text-muted mb-0">
                            No hay equipos disponibles.
                        </p>

                        <% } %>

                    </div>
                </div>

                <!-- ACCESORIOS -->
                <div class="mb-3">

                    <label class="form-label">
                        Accesorios opcionales
                    </label>

                    <% if (accesorios != null) {
                        for (Accesorio a : accesorios) {
                            if (a.getExistencia() > 0) {
                    %>

                    <div class="row align-items-center mb-2">

                        <div class="col-md-8">

                            <div class="form-check">

                                <input class="form-check-input"
                                       type="checkbox"
                                       name="accesorios"
                                       id="acc<%= a.getIdAccesorio() %>"
                                       value="<%= a.getIdAccesorio() %>">

                                <label class="form-check-label"
                                       for="acc<%= a.getIdAccesorio() %>">

                                    <%= limpiar(a.getNombre()) %>
                                    - Q<%= a.getTarifaFija() %>
                                    (Existencia: <%= a.getExistencia() %>)

                                </label>

                            </div>

                        </div>

                        <div class="col-md-4">

                            <input type="number"
                                   class="form-control"
                                   name="cantidad_<%= a.getIdAccesorio() %>"
                                   min="1"
                                   max="<%= a.getExistencia() %>"
                                   value="1">

                        </div>

                    </div>

                    <% }
                        }
                    } %>

                </div>

                <!-- FECHA -->
                <div class="mb-3">

                    <label class="form-label">
                        Fecha y hora estimada de devolución
                    </label>

                    <input type="datetime-local"
                           name="fecha_estimada"
                           class="form-control"
                           required>

                </div>

                <button type="submit" class="btn btn-primary">
                    Registrar reserva
                </button>

            </form>

        </div>
    </div>

    <!-- LISTADO DE ALQUILERES -->
    <div class="card mb-4">

        <div class="card-header bg-primary text-white">
            Alquileres registrados
        </div>

        <div class="card-body table-responsive">

            <table class="table table-bordered table-striped">

                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cliente</th>
                        <th>Fecha estimada</th>
                        <th>Estado</th>
                        <th>Depósito</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>

                    <% if (alquileres != null) {
                        for (Map<String, Object> a : alquileres) {

                            int id = (Integer) a.get("id");
                            String estado = (String) a.get("estado");
                    %>

                    <tr>

                        <td><%= id %></td>

                        <td>
                            <%= limpiar(a.get("cliente")) %>
                        </td>

                        <td>
                            <%= limpiar(a.get("estimada")) %>
                        </td>

                        <td>

                            <% if ("RESERVADO".equals(estado)) { %>

                            <span class="badge bg-warning text-dark">
                                RESERVADO
                            </span>

                            <% } else if ("ACTIVO".equals(estado)) { %>

                            <span class="badge bg-success">
                                ACTIVO
                            </span>

                            <% } else if ("FINALIZADO".equals(estado)) { %>

                            <span class="badge bg-secondary">
                                FINALIZADO
                            </span>

                            <% } else if ("CANCELADO".equals(estado)) { %>

                            <span class="badge bg-danger">
                                CANCELADO
                            </span>

                            <% } else { %>

                            <span class="badge bg-secondary">
                                <%= limpiar(estado) %>
                            </span>

                            <% } %>

                        </td>

                        <td>
                            Q<%= a.get("deposito") %>
                        </td>

                        <td>

                            <!-- ENTREGAR Y CANCELAR -->
                            <% if ("RESERVADO".equals(estado)) { %>

                            <form action="<%= request.getContextPath() %>/alquileres"
                                  method="post"
                                  class="mb-2">

                                <input type="hidden"
                                       name="accion"
                                       value="entregar">

                                <input type="hidden"
                                       name="id_alquiler"
                                       value="<%= id %>">

                                <input type="number"
                                       name="monto_recibido"
                                       class="form-control form-control-sm mb-2"
                                       placeholder="Efectivo recibido"
                                       min="0"
                                       step="0.01"
                                       required>

                                <button type="submit"
                                        class="btn btn-success btn-sm">
                                    Entregar
                                </button>

                            </form>

                            <form action="<%= request.getContextPath() %>/alquileres"
                                  method="post">

                                <input type="hidden"
                                       name="accion"
                                       value="cancelar">

                                <input type="hidden"
                                       name="id_alquiler"
                                       value="<%= id %>">

                                <button type="submit"
                                        class="btn btn-danger btn-sm"
                                        onclick="return confirm('¿Cancelar esta reserva?')">
                                    Cancelar
                                </button>

                            </form>

                            <!-- DEVOLVER -->
                            <% } else if ("ACTIVO".equals(estado)) { %>

                            <a class="btn btn-primary btn-sm"
                               href="<%= request.getContextPath() %>/alquileres?detalle=<%= id %>#devolucion">
                                Devolver
                            </a>

                            <!-- COMPROBANTE -->
                            <% } else if ("FINALIZADO".equals(estado)) { %>

                            <a class="btn btn-info btn-sm"
                               href="<%= request.getContextPath() %>/alquileres?comprobante=<%= id %>">
                                Ver comprobante
                            </a>

                            <% } else { %>

                            <span class="text-muted">
                                Sin acciones
                            </span>

                            <% } %>

                        </td>

                    </tr>

                    <% }
                    } %>

                </tbody>

            </table>

            <% if (alquileres == null || alquileres.isEmpty()) { %>

            <p class="text-center text-muted">
                No hay alquileres registrados.
            </p>

            <% } %>

        </div>
    </div>

    <!-- DEVOLUCIÓN -->
    <% if (detalleId != null) { %>

    <div class="card mb-4" id="devolucion">

        <div class="card-header bg-success text-white">
            Devolución del alquiler #<%= detalleId %>
        </div>

        <div class="card-body">

            <form action="<%= request.getContextPath() %>/alquileres"
                  method="post">

                <input type="hidden"
                       name="accion"
                       value="devolver">

                <input type="hidden"
                       name="id_alquiler"
                       value="<%= detalleId %>">

                <h5>1. Estado de los equipos</h5>

                <p class="text-muted">
                    Marca solamente los equipos que regresaron dañados.
                </p>

                <% if (detalleEquipos != null) {
                    for (Map<String, Object> e : detalleEquipos) {

                        int idEquipo = (Integer) e.get("id");
                %>

                <div class="border rounded p-2 mb-2">

                    <strong>
                        <%= limpiar(e.get("codigo")) %>
                        - <%= limpiar(e.get("modelo")) %>
                    </strong>

                    <div class="form-check mt-2">

                        <input type="checkbox"
                               class="form-check-input"
                               name="danado_<%= idEquipo %>"
                               id="danado<%= idEquipo %>"
                               value="SI">

                        <label class="form-check-label"
                               for="danado<%= idEquipo %>">
                            Equipo dañado
                        </label>

                    </div>

                </div>

                <% }
                } %>

                <hr>

                <h5>2. Accesorios extraviados</h5>

                <p class="text-muted">
                    Escribe 0 si devolvieron todos los accesorios.
                </p>

                <% if (detalleAccesorios != null) {
                    for (Map<String, Object> a : detalleAccesorios) {

                        int idAcc = (Integer) a.get("id");
                        int cantidad = (Integer) a.get("cantidad");
                %>

                <div class="row align-items-center mb-3">

                    <div class="col-md-8">

                        <label class="form-label">
                            <%= limpiar(a.get("nombre")) %>
                            (Alquilados: <%= cantidad %>)
                        </label>

                    </div>

                    <div class="col-md-4">

                        <input type="number"
                               class="form-control"
                               name="extraviado_<%= idAcc %>"
                               min="0"
                               max="<%= cantidad %>"
                               value="0"
                               required>

                    </div>

                </div>

                <% }
                } %>

                <hr>

                <h5>3. Pago del alquiler</h5>

                <div class="mb-3">

                    <label class="form-label">
                        Método de pago
                    </label>

                    <select name="metodo"
                            class="form-select"
                            id="metodoPago"
                            required>

                        <option value="EFECTIVO">
                            Efectivo
                        </option>

                        <option value="TARJETA">
                            Tarjeta simulada
                        </option>

                    </select>

                </div>

                <div class="mb-3">

                    <label class="form-label">
                        Efectivo recibido
                    </label>

                    <input type="number"
                           name="monto_recibido"
                           class="form-control"
                           min="0"
                           step="0.01"
                           value="0">

                    <small class="text-muted">
                        Completar si el pago es en efectivo.
                    </small>

                </div>

                <div class="mb-3">

                    <label class="form-label">
                        Referencia de tarjeta
                    </label>

                    <input type="text"
                           name="referencia"
                           class="form-control"
                           placeholder="Ejemplo: TARJETA123">

                    <small class="text-muted">
                        Completar solamente para tarjeta simulada.
                    </small>

                </div>

                <div class="alert alert-info">
                    El sistema calculará automáticamente las horas
                    utilizadas, recargos, daños y depósito.
                </div>

                <button type="submit"
                        class="btn btn-success"
                        onclick="return confirm('¿Confirmar devolución y cobro?')">
                    Confirmar devolución
                </button>

                <a href="<%= request.getContextPath() %>/alquileres"
                   class="btn btn-secondary">
                    Volver
                </a>

            </form>

        </div>
    </div>

    <% } %>

    <p class="text-center text-muted mt-4">
        Rueda Libre - Proyecto Final Programación II
    </p>

</div>

</body>
</html>