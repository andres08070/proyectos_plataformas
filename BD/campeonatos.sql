-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 12-12-2025 a las 17:59:53
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
  `id` int(11) NOT NULL,
  `nombre` varchar(150) DEFAULT NULL,
  `fechaInicio` varchar(20) DEFAULT NULL,
  `fechaFin` varchar(20) DEFAULT NULL,
  `ubicacion` varchar(200) DEFAULT NULL,
  `modalidades` varchar(500) DEFAULT NULL,
  `combate_pesos_data` varchar(500) DEFAULT NULL,
  `combate_rangos_data` varchar(500) DEFAULT NULL,
  `combate_edades_data` varchar(500) DEFAULT NULL,
  `combate_genero_data` varchar(500) DEFAULT NULL,
  `figuras_pesos_data` varchar(500) DEFAULT NULL,
  `figuras_rangos_data` varchar(500) DEFAULT NULL,
  `figuras_edades_data` varchar(500) DEFAULT NULL,
  `figuras_genero_data` varchar(500) DEFAULT NULL,
  `defensa_pesos_data` varchar(500) DEFAULT NULL,
  `defensa_rangos_data` varchar(500) DEFAULT NULL,
  `defensa_edades_data` varchar(500) DEFAULT NULL,
  `defensa_genero_data` varchar(500) DEFAULT NULL,
  `demo_pesos_data` varchar(500) DEFAULT NULL,
  `demo_rangos_data` varchar(500) DEFAULT NULL,
  `demo_edades_data` varchar(500) DEFAULT NULL,
  `demo_genero_data` varchar(500) DEFAULT NULL,
  `numAreas` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `campeonato`
--

INSERT INTO `campeonato` (`id`, `nombre`, `fechaInicio`, `fechaFin`, `ubicacion`, `modalidades`, `combate_pesos_data`, `combate_rangos_data`, `combate_edades_data`, `combate_genero_data`, `figuras_pesos_data`, `figuras_rangos_data`, `figuras_edades_data`, `figuras_genero_data`, `defensa_pesos_data`, `defensa_rangos_data`, `defensa_edades_data`, `defensa_genero_data`, `demo_pesos_data`, `demo_rangos_data`, `demo_edades_data`, `demo_genero_data`, `numAreas`) VALUES
(1, 'ivan', '2025-12-13', '2025-12-13', 'san jose de cucuta', 'COMBATE,FIGURAS,DEFENSA', '21-30,20-30', 'negro,rojo,blanco', '12-14', 'Separado', '23-54', '', '', 'Separado', '23-43', '', '', 'Separado', '', '', '', 'Separado', 10),
(2, 'o aña', '2025-12-05', '2025-12-27', 'sexooooooooo', 'COMBATE', '21-30', '', '', 'Separado', '', '', '', 'Separado', '', '', '', 'Separado', '', '', '', 'Separado', 11),
(3, '88147850', '2025-12-13', '2025-12-31', 'siiiiiiii', 'COMBATE', '', 'negro,blanco', '', 'Separado', '', '', '', 'Separado', '', '', '', 'Separado', '', '', '', 'Separado', 1);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
