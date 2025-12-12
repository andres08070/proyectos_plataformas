-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 13-12-2025 a las 00:33:39
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
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `campeonato`
--

INSERT INTO `campeonato` (`id`, `nombre`, `fecha_inicio`, `fecha_fin`, `ubicacion`, `num_areas`, `json_modalidades`, `fecha_creacion`, `activo`) VALUES
(2, 'amir', '2025-12-12', '2025-12-27', 'san jose de cucuta', 1, '{\"mod_1765580990329\":{\"name\":\"wer\",\"desc\":\"rrew\",\"peso\":[\"Menor a 12kg\"],\"rango\":[\"negro\"],\"edad\":[\"21 - 43 años\"],\"genero\":\"Separado\"}}', '2025-12-12 23:10:48', 1),
(3, 'ivan', '2025-12-12', '2025-12-27', 'mi casa', 12, '{\"mod_1765582240429\":{\"name\":\"Sexo\",\"desc\":\"anal full anal\",\"peso\":[\"Menor a 50kg\"],\"rango\":[\"sin correa\"],\"edad\":[\"18 - 100 años\"],\"genero\":\"Separado\"}}', '2025-12-12 23:31:46', 1);

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
(123456, 'andres', 'Masculino', 0, NULL, 'alemania', 'andresivan0807@gmail.com', 'Andres@07');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `campeonato`
--
ALTER TABLE `campeonato`
  ADD PRIMARY KEY (`id`);

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
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
