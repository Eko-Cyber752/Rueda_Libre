package com.example.ruedalibreandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {

    // Dirección de Tomcat desde el emulador Android
    String servidor = "http://127.0.0.1:8080/RuedaLibre";

    Spinner spinnerTipo;
    EditText txtCliente;
    TextView txtResultado;
    Button btnEquipos, btnCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerTipo = findViewById(R.id.spinnerTipo);
        txtCliente = findViewById(R.id.txtCliente);
        txtResultado = findViewById(R.id.txtResultado);
        btnEquipos = findViewById(R.id.btnEquipos);
        btnCliente = findViewById(R.id.btnCliente);

        String[] tipos = {
                "Todos", "URBANA", "MONTAÑA", "SCOOTER"
        };

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                tipos
        );

        spinnerTipo.setAdapter(adaptador);

        btnEquipos.setOnClickListener(v -> consultarEquipos());

        btnCliente.setOnClickListener(v -> {
            String id = txtCliente.getText().toString().trim();

            if (id.isEmpty()) {
                txtResultado.setText("Ingresa el ID del cliente.");
            } else {
                consultarCliente(id);
            }
        });
    }

    // CONSULTAR EQUIPOS
    void consultarEquipos() {

        String tipo = spinnerTipo.getSelectedItem().toString();
        String ruta = "/api/equipos";

        if (!tipo.equals("Todos")) {
            try {
                ruta += "?tipo=" + URLEncoder.encode(tipo, "UTF-8");
            } catch (Exception e) {
                txtResultado.setText("Tipo de equipo incorrecto.");
                return;
            }
        }

        consultarAPI(ruta, true);
    }

    // CONSULTAR CLIENTE
    void consultarCliente(String id) {
        consultarAPI("/api/clientes?id=" + id, false);
    }

    // CONECTAR CON LA API
    void consultarAPI(String ruta, boolean esEquipo) {

        txtResultado.setText("Consultando...");

        new Thread(() -> {

            String resultado;
            HttpURLConnection conexion = null;

            try {

                URL url = new URL(servidor + ruta);

                Log.d("RUEDALIBRE", "Conectando a: " + url);

                conexion = (HttpURLConnection)
                        url.openConnection();

                conexion.setRequestMethod("GET");
                conexion.setConnectTimeout(10000);
                conexion.setReadTimeout(10000);
                conexion.setUseCaches(false);
                conexion.setRequestProperty(
                        "Accept", "application/json");
                conexion.setRequestProperty(
                        "Connection", "close");

                int codigo = conexion.getResponseCode();

                Log.d("RUEDALIBRE",
                        "Código HTTP: " + codigo);

                InputStream entrada;

                if (codigo >= 200 && codigo < 300) {
                    entrada = conexion.getInputStream();
                } else {
                    entrada = conexion.getErrorStream();
                }

                if (entrada == null) {
                    throw new Exception(
                            "El servidor no devolvió información.");
                }

                StringBuilder respuesta = new StringBuilder();

                try (BufferedReader lector = new BufferedReader(
                        new InputStreamReader(entrada, "UTF-8"))) {

                    String linea;

                    while ((linea = lector.readLine()) != null) {
                        respuesta.append(linea);
                    }
                }

                Log.d("RUEDALIBRE",
                        "Respuesta: " + respuesta);

                if (codigo != 200) {

                    JSONObject error = new JSONObject(
                            respuesta.toString());

                    resultado = error.optString(
                            "error", "Error HTTP: " + codigo);

                } else if (esEquipo) {

                    resultado = mostrarEquipos(
                            respuesta.toString());

                } else {

                    resultado = mostrarCliente(
                            respuesta.toString());
                }

            } catch (Exception e) {

                Log.e("RUEDALIBRE",
                        "Error al consultar la API", e);

                resultado = "Error: "
                        + e.getClass().getSimpleName()
                        + "\n" + e.getMessage();

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

            String textoFinal = resultado;

            runOnUiThread(() ->
                    txtResultado.setText(textoFinal));

        }).start();
    }

    // MOSTRAR EQUIPOS
    String mostrarEquipos(String respuesta) throws Exception {

        JSONArray lista = new JSONArray(respuesta);

        if (lista.length() == 0) {
            return "No hay equipos disponibles.";
        }

        StringBuilder texto = new StringBuilder();

        texto.append("EQUIPOS DISPONIBLES\n\n");

        for (int i = 0; i < lista.length(); i++) {

            JSONObject equipo = lista.getJSONObject(i);

            texto.append("Código: ")
                    .append(equipo.optString("codigo"))
                    .append("\n");

            texto.append("Tipo: ")
                    .append(equipo.optString("tipo"))
                    .append("\n");

            texto.append("Modelo: ")
                    .append(equipo.optString("modelo"))
                    .append("\n");

            texto.append("Tarifa: Q")
                    .append(equipo.optString("tarifa_hora"))
                    .append(" por hora\n");

            if (!equipo.isNull("bateria")) {

                texto.append("Batería: ")
                        .append(equipo.optInt("bateria"))
                        .append("%\n");
            }

            texto.append("--------------------------\n");
        }

        return texto.toString();
    }

    // MOSTRAR HISTORIAL DEL CLIENTE
    String mostrarCliente(String respuesta) throws Exception {

        JSONObject cliente = new JSONObject(respuesta);

        StringBuilder texto = new StringBuilder();

        texto.append("DATOS DEL CLIENTE\n\n");

        texto.append("Nombre: ")
                .append(cliente.optString("nombre"))
                .append("\n");

        texto.append("Estado: ")
                .append(cliente.optBoolean("activo")
                        ? "Activo" : "Inactivo")
                .append("\n");

        texto.append("Saldo pendiente: Q")
                .append(cliente.optString("saldo_pendiente"))
                .append("\n\n");

        texto.append("HISTORIAL DE ALQUILERES\n\n");

        JSONArray alquileres =
                cliente.getJSONArray("alquileres");

        if (alquileres.length() == 0) {
            texto.append("No tiene alquileres registrados.");
        }

        for (int i = 0; i < alquileres.length(); i++) {

            JSONObject alquiler =
                    alquileres.getJSONObject(i);

            texto.append("Alquiler #")
                    .append(alquiler.optInt("id_alquiler"))
                    .append("\n");

            texto.append("Estado: ")
                    .append(alquiler.optString("estado"))
                    .append("\n");

            texto.append("Total: Q")
                    .append(alquiler.optString("total"))
                    .append("\n");

            texto.append("Fecha de reserva: ")
                    .append(alquiler.optString("fecha_reserva"))
                    .append("\n");

            texto.append("--------------------------\n");
        }

        return texto.toString();
    }
}