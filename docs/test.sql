-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Gép: 127.0.0.1
-- Létrehozás ideje: 2021. Nov 07. 19:58
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
-- Adatbázis: `test`
--

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `diak`
--

CREATE TABLE `diak` (
  `neptunKod` varchar(6) NOT NULL,
  `osztalyAzonosito` int(11) NOT NULL,
  `nev` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- A tábla adatainak kiíratása `diak`
--

INSERT INTO `diak` (`neptunKod`, `osztalyAzonosito`, `nev`) VALUES
('ASD123', 2, 'Piros Lili'),
('MAB123', 3, 'Mézga Géza');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `kepzettseg`
--

CREATE TABLE `kepzettseg` (
  `azonosito` int(11) NOT NULL,
  `megnevezes` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- A tábla adatainak kiíratása `kepzettseg`
--

INSERT INTO `kepzettseg` (`azonosito`, `megnevezes`) VALUES
(3, 'Irodalom Tanár'),
(4, 'Magyartanár');

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- A tábla adatainak kiíratása `ora`
--

INSERT INTO `ora` (`azonosito`, `napIndex`, `idopont`, `tantargyAzonosito`, `tanarSzemelyiSzam`, `osztalyAzonosito`, `teremAzonosito`) VALUES
(4, 3, '15:20', 1, 'IOLNA123', 3, 1),
(5, 2, '15:20', 1, 'IOLNA123', 3, 2);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `osztaly`
--

CREATE TABLE `osztaly` (
  `azonosito` int(11) NOT NULL,
  `megnevezes` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- A tábla adatainak kiíratása `osztaly`
--

INSERT INTO `osztaly` (`azonosito`, `megnevezes`) VALUES
(2, '9. B'),
(3, '11. C');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `tanar`
--

CREATE TABLE `tanar` (
  `szemelyiSzam` varchar(20) NOT NULL,
  `nev` text NOT NULL,
  `kepzettsegAzonosito` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- A tábla adatainak kiíratása `tanar`
--

INSERT INTO `tanar` (`szemelyiSzam`, `nev`, `kepzettsegAzonosito`) VALUES
('IOLNA123', 'Fekete Kefeke', 4);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `tantargy`
--

CREATE TABLE `tantargy` (
  `azonosito` int(11) NOT NULL,
  `nev` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- A tábla adatainak kiíratása `tantargy`
--

INSERT INTO `tantargy` (`azonosito`, `nev`) VALUES
(1, 'Irodalom'),
(2, 'Nyelvtan');

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- A tábla adatainak kiíratása `terem`
--

INSERT INTO `terem` (`azonosito`, `teremSzam`, `epuletSzam`, `ferohelyekSzama`, `vanEProjektor`) VALUES
(1, 52, 13, 53, 1),
(2, 13, 23, 2, 0);

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
  ADD PRIMARY KEY (`azonosito`);

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
  ADD PRIMARY KEY (`azonosito`);

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
  ADD PRIMARY KEY (`azonosito`);

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
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT a táblához `ora`
--
ALTER TABLE `ora`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT a táblához `osztaly`
--
ALTER TABLE `osztaly`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT a táblához `tantargy`
--
ALTER TABLE `tantargy`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT a táblához `terem`
--
ALTER TABLE `terem`
  MODIFY `azonosito` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

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
