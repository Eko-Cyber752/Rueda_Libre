-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: rueda_libre
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accesorios`
--

DROP TABLE IF EXISTS `accesorios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accesorios` (
  `id_accesorio` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tarifa_fija` decimal(10,2) NOT NULL,
  `precio_reposicion` decimal(10,2) NOT NULL,
  `existencia` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_accesorio`),
  UNIQUE KEY `codigo` (`codigo`),
  CONSTRAINT `accesorios_chk_1` CHECK ((`tarifa_fija` >= 0)),
  CONSTRAINT `accesorios_chk_2` CHECK ((`precio_reposicion` >= 0)),
  CONSTRAINT `accesorios_chk_3` CHECK ((`existencia` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accesorios`
--

LOCK TABLES `accesorios` WRITE;
/*!40000 ALTER TABLE `accesorios` DISABLE KEYS */;
INSERT INTO `accesorios` VALUES (1,'01AA','CASCO MOTOCROSS',600.00,800.00,20),(2,'02AA','CASCO MOUNTAIN RISK',450.00,700.00,15),(3,'03AA','REDBULL MASK',800.00,1200.00,5),(4,'04AA','CASCO PREDATOR',600.00,1500.00,10),(5,'05AA','CASCO AURRERA',250.00,350.00,25);
/*!40000 ALTER TABLE `accesorios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquiler_accesorios`
--

DROP TABLE IF EXISTS `alquiler_accesorios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquiler_accesorios` (
  `id_alquiler` int NOT NULL,
  `id_accesorio` int NOT NULL,
  `cantidad` int NOT NULL,
  `tarifa_fija` decimal(10,2) NOT NULL,
  `precio_reposicion` decimal(10,2) NOT NULL,
  `cantidad_extraviada` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_alquiler`,`id_accesorio`),
  KEY `id_accesorio` (`id_accesorio`),
  CONSTRAINT `alquiler_accesorios_ibfk_1` FOREIGN KEY (`id_alquiler`) REFERENCES `alquileres` (`id_alquiler`),
  CONSTRAINT `alquiler_accesorios_ibfk_2` FOREIGN KEY (`id_accesorio`) REFERENCES `accesorios` (`id_accesorio`),
  CONSTRAINT `alquiler_accesorios_chk_1` CHECK ((`cantidad` > 0)),
  CONSTRAINT `alquiler_accesorios_chk_2` CHECK (((`tarifa_fija` >= 0) and (`precio_reposicion` >= 0))),
  CONSTRAINT `alquiler_accesorios_chk_3` CHECK (((`cantidad_extraviada` >= 0) and (`cantidad_extraviada` <= `cantidad`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquiler_accesorios`
--

LOCK TABLES `alquiler_accesorios` WRITE;
/*!40000 ALTER TABLE `alquiler_accesorios` DISABLE KEYS */;
INSERT INTO `alquiler_accesorios` VALUES (6,1,1,600.00,800.00,0),(7,1,1,600.00,800.00,0),(8,1,1,600.00,800.00,0),(9,1,1,600.00,800.00,0),(10,1,1,600.00,800.00,0),(12,1,1,600.00,800.00,0);
/*!40000 ALTER TABLE `alquiler_accesorios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquiler_equipos`
--

DROP TABLE IF EXISTS `alquiler_equipos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquiler_equipos` (
  `id_alquiler` int NOT NULL,
  `id_equipo` int NOT NULL,
  `tarifa_hora` decimal(10,2) NOT NULL,
  `precio_reposicion` decimal(10,2) NOT NULL,
  `condicion_devolucion` enum('PENDIENTE','BUENO','DANADO') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `cargo_dano` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id_alquiler`,`id_equipo`),
  KEY `id_equipo` (`id_equipo`),
  CONSTRAINT `alquiler_equipos_ibfk_1` FOREIGN KEY (`id_alquiler`) REFERENCES `alquileres` (`id_alquiler`),
  CONSTRAINT `alquiler_equipos_ibfk_2` FOREIGN KEY (`id_equipo`) REFERENCES `equipos` (`id_equipo`),
  CONSTRAINT `alquiler_equipos_chk_1` CHECK (((`tarifa_hora` >= 0) and (`precio_reposicion` >= 0) and (`cargo_dano` >= 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquiler_equipos`
--

LOCK TABLES `alquiler_equipos` WRITE;
/*!40000 ALTER TABLE `alquiler_equipos` DISABLE KEYS */;
INSERT INTO `alquiler_equipos` VALUES (6,1,25.00,500.00,'DANADO',500.00),(7,2,45.00,1200.00,'DANADO',1200.00),(8,1,25.00,500.00,'DANADO',500.00),(9,2,45.00,1200.00,'DANADO',1200.00),(10,3,70.00,2000.00,'BUENO',0.00),(11,1,25.00,500.00,'BUENO',0.00),(12,2,45.00,1200.00,'BUENO',0.00);
/*!40000 ALTER TABLE `alquiler_equipos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquileres`
--

DROP TABLE IF EXISTS `alquileres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquileres` (
  `id_alquiler` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int NOT NULL,
  `fecha_reserva` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_entrega` datetime DEFAULT NULL,
  `fecha_estimada` datetime NOT NULL,
  `fecha_devolucion` datetime DEFAULT NULL,
  `estado` enum('RESERVADO','ACTIVO','FINALIZADO','CANCELADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'RESERVADO',
  `deposito_total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `subtotal_equipos` decimal(10,2) NOT NULL DEFAULT '0.00',
  `subtotal_accesorios` decimal(10,2) NOT NULL DEFAULT '0.00',
  `recargo_retraso` decimal(10,2) NOT NULL DEFAULT '0.00',
  `costo_danos` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `deposito_aplicado_danos` decimal(10,2) NOT NULL DEFAULT '0.00',
  `deposito_devuelto` decimal(10,2) NOT NULL DEFAULT '0.00',
  `saldo_generado` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id_alquiler`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `alquileres_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `alquileres_chk_1` CHECK ((`fecha_estimada` > `fecha_reserva`)),
  CONSTRAINT `alquileres_chk_2` CHECK (((`deposito_total` >= 0) and (`subtotal_equipos` >= 0) and (`subtotal_accesorios` >= 0))),
  CONSTRAINT `alquileres_chk_3` CHECK (((`recargo_retraso` >= 0) and (`costo_danos` >= 0) and (`total` >= 0))),
  CONSTRAINT `alquileres_chk_4` CHECK (((`deposito_aplicado_danos` >= 0) and (`deposito_devuelto` >= 0) and (`saldo_generado` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquileres`
--

LOCK TABLES `alquileres` WRITE;
/*!40000 ALTER TABLE `alquileres` DISABLE KEYS */;
INSERT INTO `alquileres` VALUES (6,1,'2026-11-10 22:57:34','2026-11-10 23:17:16','2026-11-10 22:58:00','2026-11-10 23:34:31','FINALIZADO',100.00,25.00,600.00,37.50,500.00,1162.50,100.00,0.00,400.00),(7,2,'2026-11-10 23:18:33','2026-11-10 23:18:38','2026-11-10 23:19:00','2026-11-10 23:36:28','FINALIZADO',100.00,45.00,600.00,67.50,1200.00,1912.50,100.00,0.00,1100.00),(8,4,'2026-11-10 23:39:28','2026-11-10 23:39:46','2026-11-11 06:01:00','2026-11-11 09:12:47','FINALIZADO',100.00,250.00,600.00,150.00,500.00,1500.00,100.00,0.00,400.00),(9,5,'2026-11-10 23:40:17','2026-11-10 23:40:22','2026-11-11 12:30:00','2026-11-11 19:45:17','FINALIZADO',100.00,945.00,600.00,540.00,1200.00,3285.00,100.00,0.00,1100.00),(10,3,'2026-11-11 20:12:43','2026-11-11 20:12:53','2026-11-11 20:13:00','2026-11-11 20:14:01','FINALIZADO',200.00,70.00,600.00,0.00,0.00,670.00,0.00,200.00,0.00),(11,3,'2026-11-11 22:00:37','2026-11-11 22:00:42','2026-11-11 22:01:00','2026-11-11 22:01:46','FINALIZADO',100.00,25.00,0.00,0.00,0.00,25.00,0.00,100.00,0.00),(12,3,'2026-11-11 22:13:08','2026-11-11 22:13:13','2026-11-11 22:14:00','2026-11-11 22:13:31','FINALIZADO',100.00,45.00,600.00,0.00,0.00,645.00,0.00,100.00,0.00);
/*!40000 ALTER TABLE `alquileres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `NIT` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `saldo_pendiente` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `identificador` (`NIT`),
  CONSTRAINT `clientes_chk_1` CHECK ((`saldo_pendiente` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'12345678-9','TheGoat','55548989','TheGoat@gmail.com',1,400.00),(2,'12345678-0','Carlangas','65658989','Carlagas123@gmail.com',1,1100.00),(3,'12345678-1','Mazariegos','12123636','MAZAGOAT@gmail.com',1,0.00),(4,'12345678-2','Jonh','78982654','JONH@gmail.com',1,400.00),(5,'12345678-3','Alexis','45458585','Alexis@gmail.com',1,1100.00),(6,'12345678-4','Mark','46589531','Makaco@gmail.com',1,0.00),(7,'12345678-5','Antoni','65481235','Navidad@gmail.com',1,0.00);
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipos`
--

DROP TABLE IF EXISTS `equipos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipos` (
  `id_equipo` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo` enum('URBANA','MONTANA','SCOOTER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `modelo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tarifa_hora` decimal(10,2) NOT NULL,
  `precio_reposicion` decimal(10,2) NOT NULL,
  `estado` enum('DISPONIBLE','RESERVADO','ALQUILADO','EN MANTENIMIENTO','DADO DE BAJA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DISPONIBLE',
  `bateria` int DEFAULT NULL,
  `alquileres_desde_mantenimiento` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_equipo`),
  UNIQUE KEY `codigo` (`codigo`),
  CONSTRAINT `equipos_chk_1` CHECK ((`tarifa_hora` >= 0)),
  CONSTRAINT `equipos_chk_2` CHECK ((`precio_reposicion` >= 0)),
  CONSTRAINT `equipos_chk_3` CHECK (((`bateria` is null) or (`bateria` between 0 and 100))),
  CONSTRAINT `equipos_chk_4` CHECK ((`alquileres_desde_mantenimiento` >= 0)),
  CONSTRAINT `equipos_chk_5` CHECK ((((`tipo` = _utf8mb4'SCOOTER') and (`bateria` is not null)) or ((`tipo` <> _utf8mb4'SCOOTER') and (`bateria` is null))))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipos`
--

LOCK TABLES `equipos` WRITE;
/*!40000 ALTER TABLE `equipos` DISABLE KEYS */;
INSERT INTO `equipos` VALUES (1,'01','URBANA','BMX',25.00,500.00,'DISPONIBLE',NULL,1),(2,'02EE','MONTANA','BEARBIKE',45.00,1200.00,'DISPONIBLE',NULL,1),(3,'03EE','SCOOTER','BYD',70.00,2000.00,'DISPONIBLE',100,1),(4,'04EE','SCOOTER','CHN',80.00,2500.00,'DISPONIBLE',30,0);
/*!40000 ALTER TABLE `equipos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantenimientos`
--

DROP TABLE IF EXISTS `mantenimientos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimientos` (
  `id_mantenimiento` int NOT NULL AUTO_INCREMENT,
  `id_equipo` int NOT NULL,
  `fecha_inicio` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_fin` datetime DEFAULT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `costo` decimal(10,2) NOT NULL DEFAULT '0.00',
  `estado` enum('ABIERTO','CERRADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ABIERTO',
  PRIMARY KEY (`id_mantenimiento`),
  KEY `id_equipo` (`id_equipo`),
  CONSTRAINT `mantenimientos_ibfk_1` FOREIGN KEY (`id_equipo`) REFERENCES `equipos` (`id_equipo`),
  CONSTRAINT `mantenimientos_chk_1` CHECK ((`costo` >= 0)),
  CONSTRAINT `mantenimientos_chk_2` CHECK (((`fecha_fin` is null) or (`fecha_fin` >= `fecha_inicio`)))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimientos`
--

LOCK TABLES `mantenimientos` WRITE;
/*!40000 ALTER TABLE `mantenimientos` DISABLE KEYS */;
INSERT INTO `mantenimientos` VALUES (1,1,'2026-11-10 22:31:07','2026-11-10 22:32:07','Chasis Doblado',400.00,'CERRADO'),(2,1,'2026-11-10 23:34:31','2026-11-10 23:35:18','Daño',0.00,'CERRADO'),(4,2,'2026-11-10 23:36:28','2026-11-10 23:36:48','Daño',0.00,'CERRADO'),(5,1,'2026-11-11 09:12:47','2026-11-11 20:07:21','Daño',0.00,'CERRADO'),(7,2,'2026-11-11 19:45:17','2026-11-11 20:07:19','Daño',0.00,'CERRADO');
/*!40000 ALTER TABLE `mantenimientos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimientos_accesorios`
--

DROP TABLE IF EXISTS `movimientos_accesorios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos_accesorios` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `id_accesorio` int NOT NULL,
  `id_alquiler` int DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo` enum('ENTRADA','SALIDA','DEVOLUCION') COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad` int NOT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `id_accesorio` (`id_accesorio`),
  KEY `id_alquiler` (`id_alquiler`),
  CONSTRAINT `movimientos_accesorios_ibfk_1` FOREIGN KEY (`id_accesorio`) REFERENCES `accesorios` (`id_accesorio`),
  CONSTRAINT `movimientos_accesorios_ibfk_2` FOREIGN KEY (`id_alquiler`) REFERENCES `alquileres` (`id_alquiler`),
  CONSTRAINT `movimientos_accesorios_chk_1` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimientos_accesorios`
--

LOCK TABLES `movimientos_accesorios` WRITE;
/*!40000 ALTER TABLE `movimientos_accesorios` DISABLE KEYS */;
INSERT INTO `movimientos_accesorios` VALUES (1,1,NULL,'2026-11-10 22:14:26','ENTRADA',10),(2,1,NULL,'2026-11-10 22:14:47','ENTRADA',10),(3,1,6,'2026-11-10 23:17:16','SALIDA',1),(4,1,7,'2026-11-10 23:18:38','SALIDA',1),(5,1,6,'2026-11-10 23:34:31','ENTRADA',1),(7,1,7,'2026-11-10 23:36:28','ENTRADA',1),(8,1,8,'2026-11-10 23:39:46','SALIDA',1),(9,1,9,'2026-11-10 23:40:22','SALIDA',1),(11,1,8,'2026-11-11 09:12:47','ENTRADA',1),(14,1,9,'2026-11-11 19:45:17','ENTRADA',1),(15,1,10,'2026-11-11 20:12:53','SALIDA',1),(18,1,10,'2026-11-11 20:14:01','ENTRADA',1),(19,1,12,'2026-11-11 22:13:13','SALIDA',1),(20,1,12,'2026-11-11 22:13:31','ENTRADA',1),(21,2,NULL,'2026-11-11 22:14:38','ENTRADA',15),(22,3,NULL,'2026-11-11 22:14:58','ENTRADA',5),(23,4,NULL,'2026-11-11 22:15:56','ENTRADA',10),(24,5,NULL,'2026-11-11 22:16:45','ENTRADA',25);
/*!40000 ALTER TABLE `movimientos_accesorios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagos`
--

DROP TABLE IF EXISTS `pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos` (
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `id_alquiler` int NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo` enum('DEPOSITO','COBRO','DEVOLUCION_DEPOSITO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `metodo` enum('EFECTIVO','TARJETA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `monto_recibido` decimal(10,2) DEFAULT NULL,
  `cambio` decimal(10,2) NOT NULL DEFAULT '0.00',
  `estado` enum('APROBADO','RECHAZADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'APROBADO',
  `referencia` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `id_alquiler` (`id_alquiler`),
  CONSTRAINT `pagos_ibfk_1` FOREIGN KEY (`id_alquiler`) REFERENCES `alquileres` (`id_alquiler`),
  CONSTRAINT `pagos_chk_1` CHECK (((`monto` >= 0) and (`cambio` >= 0))),
  CONSTRAINT `pagos_chk_2` CHECK (((`monto_recibido` is null) or (`monto_recibido` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagos`
--

LOCK TABLES `pagos` WRITE;
/*!40000 ALTER TABLE `pagos` DISABLE KEYS */;
INSERT INTO `pagos` VALUES (1,6,'2026-11-10 23:17:16','DEPOSITO','EFECTIVO',100.00,100.00,0.00,'APROBADO',NULL),(2,7,'2026-11-10 23:18:38','DEPOSITO','EFECTIVO',100.00,100.00,0.00,'APROBADO',NULL),(3,6,'2026-11-10 23:34:31','COBRO','TARJETA',662.50,662.50,0.00,'APROBADO','BANK123'),(4,7,'2026-11-10 23:36:28','COBRO','TARJETA',712.50,712.50,0.00,'APROBADO','Baka123'),(5,8,'2026-11-10 23:39:46','DEPOSITO','EFECTIVO',100.00,100.00,0.00,'APROBADO',NULL),(6,9,'2026-11-10 23:40:22','DEPOSITO','EFECTIVO',100.00,100.00,0.00,'APROBADO',NULL),(8,8,'2026-11-11 09:12:47','COBRO','TARJETA',1000.00,1000.00,0.00,'APROBADO','Bank123'),(9,9,'2026-11-11 19:45:17','COBRO','EFECTIVO',2085.00,2085.00,0.00,'APROBADO',NULL),(10,10,'2026-11-11 20:12:53','DEPOSITO','EFECTIVO',200.00,200.00,0.00,'APROBADO',NULL),(11,10,'2026-11-11 20:14:01','COBRO','EFECTIVO',670.00,670.00,0.00,'APROBADO',NULL),(12,10,'2026-11-11 20:14:01','DEVOLUCION_DEPOSITO','EFECTIVO',200.00,0.00,0.00,'APROBADO',NULL),(13,11,'2026-11-11 22:00:42','DEPOSITO','EFECTIVO',100.00,100.00,0.00,'APROBADO',NULL),(14,11,'2026-11-11 22:01:46','COBRO','EFECTIVO',25.00,25.00,0.00,'APROBADO',NULL),(15,11,'2026-11-11 22:01:46','DEVOLUCION_DEPOSITO','EFECTIVO',100.00,0.00,0.00,'APROBADO',NULL),(16,12,'2026-11-11 22:13:13','DEPOSITO','EFECTIVO',100.00,100.00,0.00,'APROBADO',NULL),(17,12,'2026-11-11 22:13:31','COBRO','EFECTIVO',645.00,645.00,0.00,'APROBADO',NULL),(18,12,'2026-11-11 22:13:31','DEVOLUCION_DEPOSITO','EFECTIVO',100.00,0.00,0.00,'APROBADO',NULL);
/*!40000 ALTER TABLE `pagos` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-11 22:33:52
