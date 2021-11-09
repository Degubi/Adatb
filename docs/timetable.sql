-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Gép: 127.0.0.1
-- Létrehozás ideje: 2021. Nov 09. 16:11
-- Kiszolgáló verziója: 10.4.21-MariaDB
-- PHP verzió: 7.3.31

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
('MAB123', 5, 'Vasas Piroska'),
('NEM666', 8, 'Senki se Tudja');

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
(9, 'Hittantanár');

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
(9, 3, '17:30', 8, 'IDC666', 7, 3);

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
(9, '11. A');

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
('KALIMA', 'Piros Alma', 6),
('NEMTOM', 'Tehát Jerry', 9);

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
(10, 'Hittan');

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
(6, 2, 3, 33, 0);

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
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT a táblához `ora`
--
ALTER TABLE `ora`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT a táblához `osztaly`
--
ALTER TABLE `osztaly`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT a táblához `tantargy`
--
ALTER TABLE `tantargy`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT a táblához `terem`
--
ALTER TABLE `terem`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

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
