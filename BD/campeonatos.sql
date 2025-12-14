-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 14-12-2025 a las 18:55:05
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `campeonatos`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `campeonato`
--

CREATE TABLE `campeonato` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `ubicacion` varchar(255) DEFAULT NULL,
  `num_areas` int(11) DEFAULT 1,
  `json_modalidades` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`json_modalidades`)),
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `activo` tinyint(1) DEFAULT 1,
  `id_admin` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `campeonato`
--

INSERT INTO `campeonato` (`id`, `nombre`, `fecha_inicio`, `fecha_fin`, `ubicacion`, `num_areas`, `json_modalidades`, `fecha_creacion`, `activo`, `id_admin`) VALUES
(6, 'Prueba 1', '2025-12-13', '2025-12-14', 'En tu casa', 12, '{\"mod_1765672405244\":{\"name\":\"Modalidad 1\",\"desc\":\"asd\",\"peso\":[\"Menor a 12kg\"],\"rango\":[\"blanco - negro\"],\"edad\":[\"20 años\"],\"genero\":null}}', '2025-12-14 00:34:09', 1, 1077294332),
(7, 'prueba 2', '2025-12-13', '2025-12-28', 'mi casa', 12, '{\"mod_1765675718428\":{\"name\":\"modo 1\",\"desc\":\"pues si ni modo 1\",\"peso\":[\"Menor a 32kg\"],\"rango\":[],\"edad\":[],\"genero\":null},\"mod_1765675730097\":{\"name\":\"modo 2\",\"desc\":\"pues si ni modo 2\",\"peso\":[\"Menor a 43kg\"],\"rango\":[],\"edad\":[],\"genero\":null}}', '2025-12-14 01:29:25', 1, 1077294332),
(8, 'prueba 3', '2025-12-13', '2025-12-27', 'En tu casa', 10, '{\"mod_1765676482412\":{\"name\":\"modo 1\",\"desc\":\"addsa\",\"peso\":[\"Menor a 58kg\",\"Menor a 23kg\",\"Menor a 32kg\"],\"rango\":[],\"edad\":[],\"genero\":null},\"mod_1765676578162\":{\"name\":\"modo 2\",\"desc\":\"\",\"peso\":[\"Menor a 58kg\"],\"rango\":[],\"edad\":[],\"genero\":null},\"mod_1765676587396\":{\"name\":\"modo 3\",\"desc\":\"\",\"peso\":[\"Menor a 42kg\"],\"rango\":[],\"edad\":[],\"genero\":null}}', '2025-12-14 01:45:59', 1, 1077294332),
(9, 'Prueba 1 de cuenta 1', '2025-12-14', '2025-12-31', 'san jose de cucuta', 12, '{\"mod_1765730644200\":{\"name\":\"modo 1\",\"desc\":\"pues modo 1\",\"peso\":[\"Menor a 12kg\"],\"rango\":[\"negro\"],\"edad\":[],\"genero\":null},\"mod_1765730663579\":{\"name\":\"modo 2\",\"desc\":\"mas modo 2\",\"peso\":[\"Mayor a 80kg\"],\"rango\":[],\"edad\":[],\"genero\":null}}', '2025-12-14 16:44:44', 1, 10772850);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `campeonatos_inscripcion`
--

CREATE TABLE `campeonatos_inscripcion` (
  `id_inscripcion` bigint(20) NOT NULL,
  `id_campeonato` bigint(20) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_modalidad` varchar(50) NOT NULL,
  `fecha_inscripcion` timestamp NOT NULL DEFAULT current_timestamp(),
  `estado` varchar(20) DEFAULT 'Pendiente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `campeonatos_inscripcion`
--

INSERT INTO `campeonatos_inscripcion` (`id_inscripcion`, `id_campeonato`, `id_usuario`, `id_modalidad`, `fecha_inscripcion`, `estado`) VALUES
(12, 7, 1077294332, 'mod_1765675730097', '2025-12-14 01:33:01', 'Pendiente'),
(13, 6, 1077294332, 'mod_1765672405244', '2025-12-14 01:46:09', 'Pendiente'),
(15, 8, 1077294332, 'mod_1765676482412', '2025-12-14 04:26:48', 'Pendiente'),
(16, 8, 1077294332, 'mod_1765676578162', '2025-12-14 04:27:16', 'Pendiente'),
(17, 6, 1234567, 'mod_1765672405244', '2025-12-14 16:51:59', 'Pendiente'),
(18, 7, 1234567, 'mod_1765675718428', '2025-12-14 16:52:28', 'Pendiente'),
(19, 9, 1234567, 'mod_1765730663579', '2025-12-14 16:52:56', 'Pendiente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `ID_documento` int(11) NOT NULL,
  `nombreC` varchar(150) NOT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `edad` int(11) DEFAULT NULL,
  `cinturon_rango` varchar(20) DEFAULT NULL,
  `Nacionalidad` varchar(20) DEFAULT NULL,
  `Correo` varchar(120) DEFAULT NULL,
  `Contraseña` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`ID_documento`, `nombreC`, `sexo`, `edad`, `cinturon_rango`, `Nacionalidad`, `Correo`, `Contraseña`) VALUES
(123456, 'Cuenta de prueba', 'Femenino', 0, NULL, 'bolivia', 'cuentassfa3@gmail.com', 'Andresivan@2'),
(1234567, 'prueba 2', 'Femenino', 0, NULL, 'alemania', 'cuentassfa2@gmail.com', 'Andresivan@2'),
(10772850, 'ivan', 'Masculino', 0, NULL, 'alemania', 'cuentassfa1@gmail.com', 'AndresIvan@2'),
(1077294332, 'andres gonzalez', 'masculino', 20, 'negro', 'colombia', 'andresivan0807@gmail.com', 'Andresivan@2');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `campeonato`
--
ALTER TABLE `campeonato`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_admin` (`id_admin`),
  ADD KEY `id_admin_2` (`id_admin`);

--
-- Indices de la tabla `campeonatos_inscripcion`
--
ALTER TABLE `campeonatos_inscripcion`
  ADD PRIMARY KEY (`id_inscripcion`),
  ADD UNIQUE KEY `uk_inscripcion` (`id_campeonato`,`id_usuario`,`id_modalidad`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`ID_documento`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `campeonato`
--
ALTER TABLE `campeonato`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `campeonatos_inscripcion`
--
ALTER TABLE `campeonatos_inscripcion`
  MODIFY `id_inscripcion` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `campeonato`
--
ALTER TABLE `campeonato`
  ADD CONSTRAINT `fk_campeonato_usuario` FOREIGN KEY (`id_admin`) REFERENCES `usuarios` (`ID_documento`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `campeonatos_inscripcion`
--
ALTER TABLE `campeonatos_inscripcion`
  ADD CONSTRAINT `campeonatos_inscripcion_ibfk_1` FOREIGN KEY (`id_campeonato`) REFERENCES `campeonato` (`id`),
  ADD CONSTRAINT `campeonatos_inscripcion_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`ID_documento`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
