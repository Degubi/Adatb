-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Gép: 127.0.0.1
-- Létrehozás ideje: 2021. Nov 25. 15:55
-- Kiszolgáló verziója: 10.4.21-MariaDB
-- PHP verzió: 8.0.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `timetable`
--
CREATE DATABASE IF NOT EXISTS `timetable` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `timetable`;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `diak`
--

CREATE TABLE `diak` (
  `neptunKod` varchar(6) NOT NULL,
  `osztalyAzonosito` int(11) NOT NULL,
  `nev` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `diak`
--

INSERT INTO `diak` (`neptunKod`, `osztalyAzonosito`, `nev`) VALUES
('IDC123', 8, 'Piros Alma'),
('IDK321', 4, 'Néni, a Piroska'),
('KEK420', 7, 'Balázs Péter'),
('MAB123', 5, 'Vasas Piroska'),
('MEH567', 8, 'Neve Sincs'),
('NEM666', 8, 'Senki se Tudja'),
('POP999', 8, 'Degubi'),
('YOLO43', 13, 'Ivan Dimitri');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `kepzettseg`
--

CREATE TABLE `kepzettseg` (
  `azonosito` int(11) NOT NULL,
  `megnevezes` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `kepzettseg`
--

INSERT INTO `kepzettseg` (`azonosito`, `megnevezes`) VALUES
(5, 'Magyartanár'),
(6, 'Matektanár'),
(7, 'Lakatos'),
(8, 'Zenetanár'),
(9, 'Hittantanár'),
(10, 'Tesitanár'),
(11, 'Informatikatanár');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `ora`
--

CREATE TABLE `ora` (
  `azonosito` int(11) NOT NULL,
  `napIndex` int(11) NOT NULL,
  `idopont` varchar(5) NOT NULL,
  `tantargyAzonosito` int(11) NOT NULL,
  `tanarSzemelyiSzam` varchar(20) NOT NULL,
  `osztalyAzonosito` int(11) NOT NULL,
  `teremAzonosito` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `ora`
--

INSERT INTO `ora` (`azonosito`, `napIndex`, `idopont`, `tantargyAzonosito`, `tanarSzemelyiSzam`, `osztalyAzonosito`, `teremAzonosito`) VALUES
(6, 4, '20:00', 5, 'NEMTOM', 8, 3),
(7, 1, '08:00', 4, 'KALIMA', 8, 6),
(8, 2, '14:00', 6, 'ASDASD', 6, 5),
(9, 3, '17:30', 8, 'IDC666', 7, 3),
(10, 0, '08:00', 9, 'NEMTOM', 8, 5),
(11, 3, '19:20', 8, 'KALIMA', 6, 6),
(12, 1, '11:30', 4, 'ASDASD', 9, 4),
(13, 1, '10:30', 8, 'KALIMA', 4, 4),
(14, 0, '17:15', 3, 'IDC666', 4, 6),
(15, 4, '07:00', 7, 'NEMTOM', 7, 5),
(16, 1, '17:30', 5, 'KALIMA', 5, 5),
(17, 1, '12:15', 9, 'KALIMA', 8, 6),
(18, 4, '06:00', 5, 'ASDASD', 4, 5),
(19, 3, '06:00', 5, 'NEMTOM', 9, 3),
(20, 0, '06:00', 9, 'MEH321', 5, 8),
(21, 2, '15:00', 8, 'NEMTOM', 5, 9),
(22, 4, '21:00', 10, 'MEH321', 8, 5),
(23, 1, '16:00', 11, 'MEH321', 7, 6),
(24, 1, '16:45', 4, 'NEMTOM', 13, 8),
(25, 4, '17:15', 6, 'KALIMA', 7, 5),
(26, 0, '09:00', 12, 'IDC666', 12, 7),
(27, 3, '12:00', 7, 'ASDASD', 13, 4),
(28, 2, '17:00', 12, 'ASDASD', 8, 9),
(29, 1, '20:00', 6, 'MEH321', 12, 7),
(30, 0, '13:00', 7, 'IDC666', 11, 7),
(31, 3, '17:45', 5, 'NEMTOM', 11, 5),
(32, 4, '16:30', 11, 'YOLO321', 13, 6),
(33, 3, '14:45', 5, 'YOLO321', 13, 4),
(34, 1, '08:00', 7, 'NUB859', 13, 9),
(35, 0, '10:00', 11, 'NUB859', 13, 9);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `osztaly`
--

CREATE TABLE `osztaly` (
  `azonosito` int(11) NOT NULL,
  `megnevezes` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `osztaly`
--

INSERT INTO `osztaly` (`azonosito`, `megnevezes`) VALUES
(4, '9. B'),
(5, '10. A'),
(6, '12. C'),
(7, '11. D'),
(8, '8. A'),
(9, '11. A'),
(11, '9. A'),
(12, '10. B'),
(13, '13. E');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `tanar`
--

CREATE TABLE `tanar` (
  `szemelyiSzam` varchar(20) NOT NULL,
  `nev` text NOT NULL,
  `kepzettsegAzonosito` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `tanar`
--

INSERT INTO `tanar` (`szemelyiSzam`, `nev`, `kepzettsegAzonosito`) VALUES
('ASDASD', 'Kovács Péter', 7),
('IDC666', 'Meggyőző Győző', 8),
('III111', 'Szabó Ernő', 11),
('KALIMA', 'Piros Alma', 6),
('MEH321', 'Illidan Stormrage', 10),
('NEMTOM', 'Tehát Jerry', 9),
('NUB859', 'Bene Levente', 9),
('YOLO321', 'Lakatos István', 10);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `tantargy`
--

CREATE TABLE `tantargy` (
  `azonosito` int(11) NOT NULL,
  `nev` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `tantargy`
--

INSERT INTO `tantargy` (`azonosito`, `nev`) VALUES
(3, 'Irodalom'),
(4, 'Nyelvtan'),
(5, 'Kalkulus'),
(6, 'Történelem'),
(7, 'Földrajz'),
(8, 'Zene'),
(9, 'Testnevelés'),
(10, 'Hittan'),
(11, 'Dimat'),
(12, 'Adatb');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `terem`
--

CREATE TABLE `terem` (
  `azonosito` int(11) NOT NULL,
  `teremSzam` int(11) NOT NULL,
  `epuletSzam` int(11) NOT NULL,
  `ferohelyekSzama` int(11) NOT NULL,
  `vanEProjektor` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `terem`
--

INSERT INTO `terem` (`azonosito`, `teremSzam`, `epuletSzam`, `ferohelyekSzama`, `vanEProjektor`) VALUES
(3, 13, 42, 50, 0),
(4, 42, 69, 21, 1),
(5, 16, 42, 120, 1),
(6, 2, 3, 33, 0),
(7, 15, 42, 420, 0),
(8, 19, 3, 25, 1),
(9, 6, 42, 50, 1);

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `diak`
--
ALTER TABLE `diak`
  ADD PRIMARY KEY (`neptunKod`),
  ADD KEY `osztalyAzonosito` (`osztalyAzonosito`);

--
-- A tábla indexei `kepzettseg`
--
ALTER TABLE `kepzettseg`
  ADD PRIMARY KEY (`azonosito`),
  ADD UNIQUE KEY `megnevezes` (`megnevezes`) USING HASH;

--
-- A tábla indexei `ora`
--
ALTER TABLE `ora`
  ADD PRIMARY KEY (`azonosito`),
  ADD KEY `osztalyAzonosito` (`osztalyAzonosito`),
  ADD KEY `tanarSzemelyiSzam` (`tanarSzemelyiSzam`),
  ADD KEY `teremAzonosito` (`teremAzonosito`),
  ADD KEY `tantargyAzonosito` (`tantargyAzonosito`);

--
-- A tábla indexei `osztaly`
--
ALTER TABLE `osztaly`
  ADD PRIMARY KEY (`azonosito`),
  ADD UNIQUE KEY `megnevezes` (`megnevezes`) USING HASH;

--
-- A tábla indexei `tanar`
--
ALTER TABLE `tanar`
  ADD PRIMARY KEY (`szemelyiSzam`),
  ADD UNIQUE KEY `szemelyiSzam` (`szemelyiSzam`),
  ADD KEY `Kepzettseg_osszekotes` (`kepzettsegAzonosito`);

--
-- A tábla indexei `tantargy`
--
ALTER TABLE `tantargy`
  ADD PRIMARY KEY (`azonosito`),
  ADD UNIQUE KEY `nev` (`nev`) USING HASH;

--
-- A tábla indexei `terem`
--
ALTER TABLE `terem`
  ADD PRIMARY KEY (`azonosito`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `kepzettseg`
--
ALTER TABLE `kepzettseg`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT a táblához `ora`
--
ALTER TABLE `ora`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT a táblához `osztaly`
--
ALTER TABLE `osztaly`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT a táblához `tantargy`
--
ALTER TABLE `tantargy`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT a táblához `terem`
--
ALTER TABLE `terem`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `diak`
--
ALTER TABLE `diak`
  ADD CONSTRAINT `diak_ibfk_1` FOREIGN KEY (`osztalyAzonosito`) REFERENCES `osztaly` (`azonosito`);

--
-- Megkötések a táblához `ora`
--
ALTER TABLE `ora`
  ADD CONSTRAINT `ora_ibfk_1` FOREIGN KEY (`osztalyAzonosito`) REFERENCES `osztaly` (`azonosito`),
  ADD CONSTRAINT `ora_ibfk_2` FOREIGN KEY (`tanarSzemelyiSzam`) REFERENCES `tanar` (`szemelyiSzam`),
  ADD CONSTRAINT `ora_ibfk_3` FOREIGN KEY (`teremAzonosito`) REFERENCES `terem` (`azonosito`),
  ADD CONSTRAINT `ora_ibfk_4` FOREIGN KEY (`tantargyAzonosito`) REFERENCES `tantargy` (`azonosito`);

--
-- Megkötések a táblához `tanar`
--
ALTER TABLE `tanar`
  ADD CONSTRAINT `Kepzettseg_osszekotes` FOREIGN KEY (`kepzettsegAzonosito`) REFERENCES `kepzettseg` (`azonosito`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
