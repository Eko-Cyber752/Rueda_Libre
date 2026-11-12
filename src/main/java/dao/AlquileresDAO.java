package dao;
import conexion.Conexion;
import modelo.Alquileres;
import modelo.Vehiculos;
import modelo.Bicicleta;
import modelo.Scooter;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
public class AlquileresDAO {

    
    public int reservar(Alquileres a) throws SQLException {
        if (a.getEquipos() == null || a.getEquipos().isEmpty() || a.getAccesorios() == null) {
            throw new SQLException("Selecciona al menos un equipo.");
        }
        if (a.getFechaEstimada() == null || a.getFechaEstimada().getTime()
                   <= System.currentTimeMillis()) {
            throw new SQLException("La fecha debe ser futura.");
        }
        List<Integer> equipos = new ArrayList<>(a.getEquipos());
        Collections.sort(equipos);
        if (new HashSet<>(equipos).size() != equipos.size()) {
            throw new SQLException("Hay equipos repetidos.");
        }
        Map<Integer, Integer> accesorios = new TreeMap<>(a.getAccesorios());
        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);
            try {
                String sql = "SELECT activo,saldo_pendiente " + "FROM clientes WHERE id_cliente=? FOR UPDATE";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, a.getIdCliente());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("activo") || rs.getBigDecimal("saldo_pendiente") .signum() > 0) {
                            throw new SQLException( "Cliente inactivo o con deuda.");
                        }
                    }
                }
                sql = "SELECT COUNT(*) FROM alquileres " + "WHERE id_cliente=? AND estado='ACTIVO'";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, a.getIdCliente());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            throw new SQLException( "El cliente tiene un alquiler activo.");
                        }
                    }
                }

                
                for (int id : equipos) {
                    sql = "SELECT tipo,estado,bateria " + "FROM equipos WHERE id_equipo=? FOR UPDATE";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() || !"DISPONIBLE".equals( rs.getString("estado"))) {
                                throw new SQLException( "Equipo no disponible: " + id);
                            }
                            if ("SCOOTER".equals(rs.getString("tipo")) && (rs.getObject("bateria") == null
                                    || rs.getInt("bateria") < 30)) {
                                throw new SQLException( "Scooter con bateria insuficiente.");
                            }
                        }
                    }
                }

                
                for (int id : accesorios.keySet()) {
                    int cantidad = accesorios.get(id);
                    if (cantidad <= 0) {
                        throw new SQLException( "Cantidad de accesorios incorrecta.");
                    }
                    int existencia;
                    sql = "SELECT existencia FROM accesorios " + "WHERE id_accesorio=? FOR UPDATE";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException( "Accesorio inexistente.");
                            }
                            existencia = rs.getInt(1);
                        }
                    }
                    sql = "SELECT COALESCE(SUM(aa.cantidad),0) " + "FROM alquiler_accesorios aa " + "JOIN alquileres al "
                        + "ON al.id_alquiler=aa.id_alquiler " + "WHERE aa.id_accesorio=? " + "AND al.estado='RESERVADO'";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            if (cantidad > existencia - rs.getInt(1)) {
                                throw new SQLException( "Accesorios insuficientes.");
                            }
                        }
                    }
                }

                
                sql = "INSERT INTO alquileres " + "(id_cliente,fecha_reserva,fecha_estimada,estado,"
                    + "deposito_total,subtotal_equipos,subtotal_accesorios," + "recargo_retraso,costo_danos,total,"
                    + "deposito_aplicado_danos,deposito_devuelto," + "saldo_generado) "
                    + "VALUES (?,NOW(),?,'RESERVADO',0,0,0,0,0,0,0,0,0)";
                int idAlquiler;
                try (PreparedStatement ps = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, a.getIdCliente());
                    ps.setTimestamp(2, a.getFechaEstimada());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException( "No se genero el alquiler.");
                        }
                        idAlquiler = rs.getInt(1);
                    }
                }

                
                for (int id : equipos) {
                    sql = "INSERT INTO alquiler_equipos " + "(id_alquiler,id_equipo,tarifa_hora,"
                        + "precio_reposicion,cargo_dano) " + "SELECT ?,id_equipo,tarifa_hora,"
                        + "precio_reposicion,0 FROM equipos " + "WHERE id_equipo=?";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setInt(1, idAlquiler);
                        ps.setInt(2, id);
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException( "Error al asignar equipo.");
                        }
                    }
                    sql = "UPDATE equipos SET estado='RESERVADO' " + "WHERE id_equipo=? AND estado='DISPONIBLE'";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException( "Equipo no disponible.");
                        }
                    }
                }

                
                for (int id : accesorios.keySet()) {
                    sql = "INSERT INTO alquiler_accesorios " + "(id_alquiler,id_accesorio,cantidad,"
                        + "tarifa_fija,precio_reposicion," + "cantidad_extraviada) " + "SELECT ?,id_accesorio,?,tarifa_fija,"
                        + "precio_reposicion,0 FROM accesorios " + "WHERE id_accesorio=?";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setInt(1, idAlquiler);
                        ps.setInt(2, accesorios.get(id));
                        ps.setInt(3, id);
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException( "Error al asignar accesorio.");
                        }
                    }
                }
                cn.commit();
                return idAlquiler;
            } catch (Exception e) {
                cn.rollback();
                throw new SQLException(e.getMessage(), e);
            }
        }
    }

    
    public List<Map<String, Object>> listarAlquileres()
            throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT a.id_alquiler,c.nombre," + "a.fecha_estimada,a.estado,a.deposito_total,"
            + "COALESCE(SUM(CASE WHEN e.tipo='SCOOTER' " + "THEN 200 ELSE 100 END),0) estimado " + "FROM alquileres a "
            + "JOIN clientes c ON a.id_cliente=c.id_cliente " + "LEFT JOIN alquiler_equipos ae "
            + "ON a.id_alquiler=ae.id_alquiler " + "LEFT JOIN equipos e ON ae.id_equipo=e.id_equipo "
            + "GROUP BY a.id_alquiler,c.nombre," + "a.fecha_estimada,a.estado,a.deposito_total "
            + "ORDER BY a.id_alquiler DESC";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_alquiler"));
                fila.put("cliente", rs.getString("nombre"));
                fila.put("estimada", rs.getTimestamp("fecha_estimada"));
                fila.put("estado", rs.getString("estado"));
                fila.put("deposito", "RESERVADO".equals(rs.getString("estado")) ? rs.getBigDecimal("estimado")
                    : rs.getBigDecimal("deposito_total"));
                lista.add(fila);
            }
        }
        return lista;
    }

    
    public void cancelar(int id) throws SQLException {
        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);
            try {
                String sql = "UPDATE alquileres " + "SET estado='CANCELADO' " + "WHERE id_alquiler=? AND estado='RESERVADO'";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException( "Solo puedes cancelar reservas.");
                    }
                }
                sql = "UPDATE equipos SET estado='DISPONIBLE' " + "WHERE estado='RESERVADO' " + "AND id_equipo IN "
                    + "(SELECT id_equipo FROM alquiler_equipos " + "WHERE id_alquiler=?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                cn.commit();
            } catch (Exception e) {
                cn.rollback();
                throw new SQLException(e.getMessage(), e);
            }
        }
    }

    
    public void entregar(int id, BigDecimal recibido)
            throws SQLException {
        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);
            try {
                String sql = "SELECT estado FROM alquileres " + "WHERE id_alquiler=? FOR UPDATE";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !"RESERVADO".equals(rs.getString(1))) {
                            throw new SQLException( "El alquiler no esta reservado.");
                        }
                    }
                }
                BigDecimal deposito = BigDecimal.ZERO;
                int cantidadEquipos = 0;
                sql = "SELECT e.codigo,e.tipo,e.tarifa_hora," + "e.estado,e.bateria " + "FROM alquiler_equipos ae "
                    + "JOIN equipos e ON ae.id_equipo=e.id_equipo " + "WHERE ae.id_alquiler=? FOR UPDATE";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            cantidadEquipos++;
                            if (!"RESERVADO".equals( rs.getString("estado"))) {
                                throw new SQLException( "Equipo no reservado.");
                            }
                            String tipo = rs.getString("tipo");
                            if ("SCOOTER".equals(tipo) && (rs.getObject("bateria") == null || rs.getInt("bateria") < 30)) {
                                throw new SQLException( "Scooter con bateria insuficiente.");
                            }
                            String codigo = rs.getString("codigo");
                            BigDecimal tarifa = rs.getBigDecimal("tarifa_hora");

                            
                            Vehiculos vehiculo;
                            if ("SCOOTER".equals(tipo)) {
                                vehiculo = new Scooter(codigo, tarifa);
                            } else {
                                vehiculo = new Bicicleta(codigo, tarifa);
                            }
                            deposito = deposito.add( vehiculo.calcularDeposito());
                        }
                    }
                }
                if (cantidadEquipos == 0 || recibido == null || recibido.compareTo(deposito) < 0) {
                    throw new SQLException( "Deposito insuficiente. Necesitas Q" + deposito);
                }
                sql = "UPDATE equipos SET estado='ALQUILADO' " + "WHERE estado='RESERVADO' AND id_equipo IN "
                    + "(SELECT id_equipo FROM alquiler_equipos " + "WHERE id_alquiler=?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    if (ps.executeUpdate() != cantidadEquipos) {
                        throw new SQLException( "No se pudieron entregar todos los equipos.");
                    }
                }

                
                sql = "SELECT id_accesorio,cantidad " + "FROM alquiler_accesorios WHERE id_alquiler=?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int accesorio = rs.getInt(1);
                            int cantidad = rs.getInt(2);
                            try (PreparedStatement p = cn.prepareStatement( "UPDATE accesorios "
                                    + "SET existencia=existencia-? " + "WHERE id_accesorio=? " + "AND existencia>=?")) {
                                p.setInt(1, cantidad);
                                p.setInt(2, accesorio);
                                p.setInt(3, cantidad);
                                if (p.executeUpdate() != 1) {
                                    throw new SQLException( "Accesorios insuficientes.");
                                }
                            }
                            movimiento(cn, accesorio, id, "SALIDA", cantidad);
                        }
                    }
                }

                
                sql = "INSERT INTO pagos " + "(id_alquiler,fecha,tipo,metodo,monto," + "monto_recibido,cambio,estado) "
                    + "VALUES (?,NOW(),'DEPOSITO','EFECTIVO'," + "?,?,?,'APROBADO')";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setBigDecimal(2, deposito);
                    ps.setBigDecimal(3, recibido);
                    ps.setBigDecimal(4, recibido.subtract(deposito));
                    ps.executeUpdate();
                }
                sql = "UPDATE alquileres " + "SET estado='ACTIVO',fecha_entrega=NOW(),"
                    + "deposito_total=? WHERE id_alquiler=? " + "AND estado='RESERVADO'";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setBigDecimal(1, deposito);
                    ps.setInt(2, id);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException( "No se pudo activar el alquiler.");
                    }
                }
                cn.commit();
            } catch (Exception e) {
                cn.rollback();
                throw new SQLException(e.getMessage(), e);
            }
        }
    }

    
    private void movimiento(Connection cn, int accesorio, int alquiler, String tipo, int cantidad)
            throws SQLException {
        String sql = "INSERT INTO movimientos_accesorios " + "(id_accesorio,id_alquiler,fecha,tipo,cantidad) "
            + "VALUES (?,?,NOW(),?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, accesorio);
            ps.setInt(2, alquiler);
            ps.setString(3, tipo);
            ps.setInt(4, cantidad);
            ps.executeUpdate();
        }
    }

    
    public List<Map<String, Object>> equiposDelAlquiler(int id)
            throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT e.id_equipo,e.codigo,e.modelo," + "e.tipo,ae.tarifa_hora " + "FROM alquiler_equipos ae "
            + "JOIN equipos e ON ae.id_equipo=e.id_equipo " + "WHERE ae.id_alquiler=?";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id", rs.getInt("id_equipo"));
                    fila.put("codigo", rs.getString("codigo"));
                    fila.put("modelo", rs.getString("modelo"));
                    fila.put("tipo", rs.getString("tipo"));
                    fila.put("tarifa", rs.getBigDecimal("tarifa_hora"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }

    
    public List<Map<String, Object>> accesoriosDelAlquiler(int id)
            throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT ac.id_accesorio,ac.nombre," + "aa.cantidad,aa.tarifa_fija " + "FROM alquiler_accesorios aa "
            + "JOIN accesorios ac " + "ON aa.id_accesorio=ac.id_accesorio " + "WHERE aa.id_alquiler=?";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id", rs.getInt("id_accesorio"));
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("cantidad", rs.getInt("cantidad"));
                    fila.put("tarifa", rs.getBigDecimal("tarifa_fija"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }

    
    public void devolver(int id, Map<Integer, Boolean> danados, Map<Integer, Integer> extraviados,
            String metodo, BigDecimal recibido, String referencia) throws SQLException {
        if (!"EFECTIVO".equals(metodo) && !"TARJETA".equals(metodo)) {
            throw new SQLException("Metodo de pago incorrecto.");
        }
        if ("TARJETA".equals(metodo) && (referencia == null || referencia.trim().isEmpty() || "RECHAZADO".equalsIgnoreCase(
                    referencia.trim()))) {
            throw new SQLException("Tarjeta rechazada.");
        }
        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);
            try {
                int cliente;
                Timestamp entrega;
                Timestamp estimada;
                BigDecimal deposito;
                String sql = "SELECT id_cliente,fecha_entrega," + "fecha_estimada,deposito_total,estado " + "FROM alquileres "
                    + "WHERE id_alquiler=? FOR UPDATE";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !"ACTIVO".equals( rs.getString("estado"))) {
                            throw new SQLException( "El alquiler no esta activo.");
                        }
                        cliente = rs.getInt("id_cliente");
                        entrega = rs.getTimestamp("fecha_entrega");
                        estimada = rs.getTimestamp("fecha_estimada");
                        deposito = rs.getBigDecimal("deposito_total");
                    }
                }
                Timestamp ahora;
                try (PreparedStatement ps = cn.prepareStatement("SELECT NOW()");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    ahora = rs.getTimestamp(1);
                }

                
                long minutos = Math.max(0, (ahora.getTime() - entrega.getTime() + 59999)
                    / 60000);
                long horas = Math.max(1, (minutos + 59) / 60);
                long retraso = Math.max(0, (ahora.getTime() - estimada.getTime() + 59999)
                    / 60000);
                long horasExtra = retraso <= 15 ? 0 : (retraso + 59) / 60;
                BigDecimal subEquipos = BigDecimal.ZERO;
                BigDecimal subAccesorios = BigDecimal.ZERO;
                BigDecimal recargo = BigDecimal.ZERO;
                BigDecimal danos = BigDecimal.ZERO;

                
                sql = "SELECT id_equipo,tarifa_hora," + "precio_reposicion " + "FROM alquiler_equipos " + "WHERE id_alquiler=?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int equipo = rs.getInt(1);
                            BigDecimal tarifa = rs.getBigDecimal(2);
                            BigDecimal reposicion = rs.getBigDecimal(3);
                            boolean danado = Boolean.TRUE.equals(danados.get(equipo));
                            subEquipos = subEquipos.add( tarifa.multiply( BigDecimal.valueOf(horas)));
                            recargo = recargo.add( tarifa.multiply( BigDecimal.valueOf(horasExtra))
                                      .multiply(new BigDecimal("1.5")));
                            if (danado) {
                                danos = danos.add(reposicion);
                            }
                            String condicion = danado ? "DAÑADO" : "BUENO";
                            String actualizar = "UPDATE alquiler_equipos SET " + "condicion_devolucion=?,cargo_dano=? "
                                + "WHERE id_alquiler=? AND id_equipo=?";
                            try (PreparedStatement p = cn.prepareStatement(actualizar)) {
                                p.setString(1, condicion);
                                p.setBigDecimal(2, danado ? reposicion : BigDecimal.ZERO);
                                p.setInt(3, id);
                                p.setInt(4, equipo);
                                p.executeUpdate();
                            }
                            actualizar = "UPDATE equipos SET " + "alquileres_desde_mantenimiento="
                                + "alquileres_desde_mantenimiento+1," + "estado=IF(?=1 OR "
                                + "alquileres_desde_mantenimiento+1>=10," + "'EN MANTENIMIENTO','DISPONIBLE') "
                                + "WHERE id_equipo=? " + "AND estado='ALQUILADO'";
                            try (PreparedStatement p = cn.prepareStatement(actualizar)) {
                                p.setInt(1, danado ? 1 : 0);
                                p.setInt(2, equipo);
                                if (p.executeUpdate() != 1) {
                                    throw new SQLException( "No se pudo devolver el equipo.");
                                }
                            }

                           
                            String consultar = "SELECT estado FROM equipos " + "WHERE id_equipo=?";
                            boolean mantenimiento;
                            try (PreparedStatement p = cn.prepareStatement(consultar)) {
                                p.setInt(1, equipo);
                                try (ResultSet r = p.executeQuery()) {
                                    r.next();
                                    mantenimiento = "EN MANTENIMIENTO".equals( r.getString(1));
                                }
                            }
                            if (mantenimiento) {
                                String insertar = "INSERT INTO mantenimientos " + "(id_equipo,fecha_inicio,motivo,"
                                    + "costo,estado) " + "VALUES (?,NOW(),?,0,'ABIERTO')";
                                try (PreparedStatement p = cn.prepareStatement(insertar)) {
                                    p.setInt(1, equipo);
                                    p.setString(2, danado ? "Equipo dañado" : "10 alquileres");
                                    p.executeUpdate();
                                }
                            }
                        }
                    }
                }

                
                sql = "SELECT id_accesorio,cantidad," + "tarifa_fija,precio_reposicion " + "FROM alquiler_accesorios "
                    + "WHERE id_alquiler=?";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int accesorio = rs.getInt(1);
                            int cantidad = rs.getInt(2);
                            int perdidos = extraviados.getOrDefault(accesorio, 0);
                            if (perdidos < 0 || perdidos > cantidad) {
                                throw new SQLException( "Cantidad extraviada incorrecta.");
                            }
                            subAccesorios = subAccesorios.add( rs.getBigDecimal(3).multiply( BigDecimal.valueOf(cantidad)));
                            danos = danos.add( rs.getBigDecimal(4).multiply( BigDecimal.valueOf(perdidos)));
                            String actualizar = "UPDATE alquiler_accesorios " + "SET cantidad_extraviada=? "
                                + "WHERE id_alquiler=? " + "AND id_accesorio=?";
                            try (PreparedStatement p = cn.prepareStatement(actualizar)) {
                                p.setInt(1, perdidos);
                                p.setInt(2, id);
                                p.setInt(3, accesorio);
                                p.executeUpdate();
                            }
                            int devueltos = cantidad - perdidos;
                            if (devueltos > 0) {
                                actualizar = "UPDATE accesorios SET " + "existencia=existencia+? " + "WHERE id_accesorio=?";
                                try (PreparedStatement p = cn.prepareStatement(actualizar)) {
                                    p.setInt(1, devueltos);
                                    p.setInt(2, accesorio);
                                    p.executeUpdate();
                                }
                                movimiento(cn, accesorio, id, "ENTRADA", devueltos);
                            }
                        }
                    }
                }

                
                BigDecimal aplicado = deposito.min(danos);
                BigDecimal devuelto = deposito.subtract(aplicado);
                BigDecimal deuda = danos.subtract(aplicado);
                BigDecimal cobro = subEquipos .add(subAccesorios) .add(recargo) .setScale(2, RoundingMode.HALF_UP);
                BigDecimal total = cobro.add(danos);
                if ("EFECTIVO".equals(metodo) && (recibido == null || recibido.compareTo(cobro) < 0)) {
                    throw new SQLException( "Efectivo insuficiente. Debes cobrar Q" + cobro);
                }
                BigDecimal cambio = "EFECTIVO".equals(metodo) ? recibido.subtract(cobro) : BigDecimal.ZERO;

                
                sql = "INSERT INTO pagos " + "(id_alquiler,fecha,tipo,metodo,monto,"
                    + "monto_recibido,cambio,estado,referencia) " + "VALUES (?,NOW(),'COBRO',?,?,?,?,'APROBADO',?)";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setString(2, metodo);
                    ps.setBigDecimal(3, cobro);
                    ps.setBigDecimal(4, "EFECTIVO".equals(metodo) ? recibido : cobro);
                    ps.setBigDecimal(5, cambio);
                    ps.setString(6, "TARJETA".equals(metodo) ? referencia : null);
                    ps.executeUpdate();
                }

                
                if (devuelto.signum() > 0) {
                    sql = "INSERT INTO pagos " + "(id_alquiler,fecha,tipo,metodo,monto," + "monto_recibido,cambio,estado) "
                        + "VALUES (?,NOW(),'DEVOLUCION_DEPOSITO'," + "?,?,0,0,'APROBADO')";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        ps.setString(2, metodo);
                        ps.setBigDecimal(3, devuelto);
                        ps.executeUpdate();
                    }
                }

                
                if (deuda.signum() > 0) {
                    sql = "UPDATE clientes SET " + "saldo_pendiente=saldo_pendiente+? " + "WHERE id_cliente=?";
                    try (PreparedStatement ps = cn.prepareStatement(sql)) {
                        ps.setBigDecimal(1, deuda);
                        ps.setInt(2, cliente);
                        ps.executeUpdate();
                    }
                }

                
                sql = "UPDATE alquileres SET " + "fecha_devolucion=?,estado='FINALIZADO',"
                    + "subtotal_equipos=?,subtotal_accesorios=?," + "recargo_retraso=?,costo_danos=?,total=?,"
                    + "deposito_aplicado_danos=?," + "deposito_devuelto=?,saldo_generado=? "
                    + "WHERE id_alquiler=? AND estado='ACTIVO'";
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setTimestamp(1, ahora);
                    ps.setBigDecimal(2, subEquipos);
                    ps.setBigDecimal(3, subAccesorios);
                    ps.setBigDecimal(4, recargo);
                    ps.setBigDecimal(5, danos);
                    ps.setBigDecimal(6, total);
                    ps.setBigDecimal(7, aplicado);
                    ps.setBigDecimal(8, devuelto);
                    ps.setBigDecimal(9, deuda);
                    ps.setInt(10, id);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException( "No se pudo finalizar el alquiler.");
                    }
                }
                cn.commit();
            } catch (Exception e) {
                cn.rollback();
                throw new SQLException(e.getMessage(), e);
            }
        }
    }

    
    public Map<String, Object> comprobante(int id)
            throws SQLException {
        Map<String, Object> datos = new HashMap<>();
        try (Connection cn = Conexion.conectar()) {
            String sql = "SELECT a.*,c.nombre,c.NIT " + "FROM alquileres a " + "JOIN clientes c ON a.id_cliente=c.id_cliente "
                + "WHERE a.id_alquiler=? " + "AND a.estado='FINALIZADO'";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException( "El alquiler no esta finalizado.");
                    }
                    datos.put("id", rs.getInt("id_alquiler"));
                    datos.put("cliente", rs.getString("nombre"));
                    datos.put("nit", rs.getString("NIT"));
                    datos.put("entrega", rs.getTimestamp("fecha_entrega"));
                    datos.put("devolucion", rs.getTimestamp("fecha_devolucion"));
                    datos.put("equipos", rs.getBigDecimal("subtotal_equipos"));
                    datos.put("accesorios", rs.getBigDecimal("subtotal_accesorios"));
                    datos.put("retraso", rs.getBigDecimal("recargo_retraso"));
                    datos.put("danos", rs.getBigDecimal("costo_danos"));
                    datos.put("total", rs.getBigDecimal("total"));
                    datos.put("deposito", rs.getBigDecimal("deposito_total"));
                    datos.put("aplicado", rs.getBigDecimal("deposito_aplicado_danos"));
                    datos.put("devuelto", rs.getBigDecimal("deposito_devuelto"));
                    datos.put("saldo", rs.getBigDecimal("saldo_generado"));
                }
            }
            List<Map<String, Object>> pagos = new ArrayList<>();
            sql = "SELECT fecha,tipo,metodo,monto,estado " + "FROM pagos WHERE id_alquiler=? " + "ORDER BY id_pago";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> pago = new HashMap<>();
                        pago.put("fecha", rs.getTimestamp("fecha"));
                        pago.put("tipo", rs.getString("tipo"));
                        pago.put("metodo", rs.getString("metodo"));
                        pago.put("monto", rs.getBigDecimal("monto"));
                        pago.put("estado", rs.getString("estado"));
                        pagos.add(pago);
                    }
                }
            }
            datos.put("pagos", pagos);
        }
        return datos;
    }
}