# RUEDA LIBRE

## Proyecto Final de Programación II

**Carrera:** Ingeniería en Sistemas

&#x20;
**Proyecto:** Sistema de alquiler de bicicletas y scooters



**Integrantes:** Jonatan Guadalupe Domínguez Barrera // 7690-25-17982
Alexis Humberto De León Campos // 7690-25-18160

&#x20;
**GitHub:** https://github.com/Eko-Cyber752/Rueda_Libre



## 1\. Descripción del proyecto

Rueda Libre es un programa que creamos para llevar el control del alquiler de bicicletas y Scooter.

El sistema permite registrar clientes, equipos, reservas, pagos y devoluciones. También tiene una aplicación Android donde podemos consultar los equipos disponibles y los alquileres de un cliente.



## 2\. Programas utilizados

Para realizar nuestro proyecto utilizamos:

* Java y NetBeans para crear el sistema.
* MySQL para guardar la información.
* Tomcat para ejecutar la página web.
* Android Studio para crear la aplicación móvil.
* Git y GitHub para compartir nuestro trabajo.
* 

## 3\. ¿Qué puede hacer el sistema?

* Registrar y modificar clientes.
* Agregar bicicletas, Scooter y accesorios.
* Crear y cancelar reservas.
* Registrar alquileres y devoluciones.
* Calcular los pagos y depósitos.
* Generar comprobantes.
* Llevar el control de mantenimientos.
* Consultar reportes.
* Ver equipos disponibles e historial desde Android.





## 4\. Archivos del proyecto

Nuestro trabajo incluye:

* **Proyecto web:** programa principal realizado en NetBeans.
* **Aplicación Android:** aplicación creada en Android Studio.
* **Base de datos:** archivo `rueda\_libre.sql`.
* **Diagramas:** muestran cómo está organizado el sistema.
* **Capturas:** imágenes del funcionamiento del programa.





## 5\. Cómo ejecutar el proyecto web

1. Abrir MySQL Workbench e importar la base de datos `rueda\_libre.sql`.
2. Abrir el proyecto RuedaLibre en NetBeans.
3. Revisar que la conexión con MySQL tenga el usuario y contraseña correctos.
4. Verificar que Tomcat 9 esté configurado.
5. Ejecutar el proyecto con Run.
6. Abrir la siguiente dirección en el navegador:

`http://localhost:8080/RuedaLibre/`



## 6\. Cómo ejecutar la aplicación Android

1. Primero debemos tener funcionando el proyecto web.
2. Abrir RuedaLibreAndroid en Android Studio.
3. Iniciar el emulador de Android.
4. Abrir la terminal y escribir estos comandos:

```powershell
\\\& "$env:LOCALAPPDATA\\\\Android\\\\Sdk\\\\platform-tools\\\\adb.exe" devices
\\\& "$env:LOCALAPPDATA\\\\Android\\\\Sdk\\\\platform-tools\\\\adb.exe" reverse tcp:8080 tcp:8080
```

5. Verificar que en `MainActivity.java` esté esta dirección:

```java
String servidor = "http://127.0.0.1:8080/RuedaLibre";
```

6. Ejecutar la aplicación con Run.

Después podemos consultar los equipos disponibles y buscar el historial de un cliente.

Nota**:** Si reiniciamos el emulador, tenemos que realizar otra vez lo segundo



## 7\. Reportes

El sistema cuenta con seis reportes que nos ayudan a consultar ingresos, equipos utilizados, alquileres atrasados, mantenimientos, accesorios y clientes con pagos pendientes.

También podemos guardarlos en PDF desde el navegador.



## 8\. Lo aprendido

Durante el desarrollo del proyecto utilizamos clases, objetos, herencia y otros temas vistos en Programación II.

También aprendimos a conectar Java con MySQL y a trabajar con una aplicación web y una aplicación Android.



## 9\. Conclusión

Con este proyecto logramos poner en práctica varios temas que aprendimos durante el curso de Programación II.

Aunque tuvimos algunas dificultades durante el desarrollo, pudimos comprender mejor cómo funciona un sistema que utiliza una base de datos, una página web y una aplicación móvil.

También aprendimos a trabajar en equipo y compartir nuestros avances por medio de GitHub.

