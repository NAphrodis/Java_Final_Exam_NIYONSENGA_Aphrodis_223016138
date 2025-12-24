-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 24, 2025 at 05:47 PM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `agriportaldb`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
CREATE TABLE IF NOT EXISTS `admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`id`, `username`, `password`, `name`) VALUES
(1, 'Aphrodis', 'umugore', 'NIYONSENGA Aphrodis'),
(3, 'Admin1', 'umugore', 'Adminstrator');

-- --------------------------------------------------------

--
-- Table structure for table `crop`
--

DROP TABLE IF EXISTS `crop`;
CREATE TABLE IF NOT EXISTS `crop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `crop_name` varchar(100) NOT NULL,
  `variety` varchar(100) DEFAULT NULL,
  `planted_date` date DEFAULT NULL,
  `expected_harvest_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `expected_yield` double DEFAULT NULL,
  `field_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `field_id` (`field_id`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `crop`
--

INSERT INTO `crop` (`id`, `crop_name`, `variety`, `planted_date`, `expected_harvest_date`, `status`, `expected_yield`, `field_id`) VALUES
(1, 'Umuceri', 'Local', '2025-10-26', '2026-01-12', 'Planted', 5000, 1),
(2, 'Ibijumba', 'Global', '2025-10-27', '2026-03-23', 'Planted', 6000, 4),
(3, 'Ibinyomoro', 'Local', '2025-10-31', '2026-03-22', 'Planted', 4000, 5),
(4, 'Ibitoki', 'International', '2025-11-08', '2026-02-22', 'Planted', 3000, 7),
(5, 'Umuceri', 'Local', '2025-12-17', '2026-04-09', 'Planted', 400, 8),
(6, 'Maize', 'local Product', '2025-06-02', '2026-01-02', 'Planted', 200, 7),
(7, 'Cassava', 'Global', '2025-09-12', '2026-03-08', 'Planted', 400, 8),
(8, 'Ibishyimbo', 'Local', '2025-11-17', '2026-03-09', 'Planted', 130, 8),
(9, 'Maize', 'ZAM 07', '2024-09-15', '2025-01-20', 'Growing', 1500, 3),
(10, 'Potatoes', 'Kinigi', '2024-10-01', '2025-01-15', 'Growing', 5000, 2),
(11, 'Coffee', 'Arabica', '2023-01-01', '2025-05-10', 'Mature', 800, 5),
(12, 'Beans', 'RWR 2245', '2024-10-10', '2024-12-30', 'Harvested', 400, 14),
(13, 'Rice', 'Kigoli', '2024-08-20', '2024-12-15', 'Harvested', 2500, 9),
(14, 'Tea', 'PF1', '2022-05-01', '2024-11-30', 'Continuous', 3000, 4),
(15, 'Wheat', 'Kenya 1', '2024-09-05', '2025-02-10', 'Growing', 1200, 6),
(16, 'Cassava', 'NAROCAS', '2024-03-01', '2025-03-01', 'Growing', 8000, 13),
(17, 'Bananas', 'FIA 17', '2023-06-12', '2024-12-25', 'Mature', 4500, 15),
(18, 'Vegetables', 'Cabbage', '2024-11-01', '2025-02-01', 'Growing', 600, 12),
(19, 'Sorghum', 'Local White', '2024-09-20', '2025-01-15', 'Growing', 900, 7),
(20, 'Sweet Potatoes', 'Orange-Fleshed', '2024-10-05', '2025-01-20', 'Growing', 2000, 1),
(21, 'Pineapple', 'Smooth Cayenne', '2023-08-01', '2024-12-10', 'Harvested', 3500, 10),
(22, 'Passion Fruit', 'Purple', '2024-02-15', '2024-12-20', 'Mature', 1200, 11),
(23, 'Peas', 'Green Field', '2024-11-05', '2025-01-30', 'Growing', 300, 8);

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
CREATE TABLE IF NOT EXISTS `customers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `username` varchar(80) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `name`, `email`, `phone`, `username`, `password`) VALUES
(1, 'Charlie Buyer', 'charlie@example.com', '250700333444', 'charlie', 'pass1234'),
(2, 'Habimana Piere', 'habimanapiere22@gmail.com', '+23788987676', 'Piere', 'Habipiere'),
(3, 'UWIMPUHWE Claudine', 'uwimpuhweclaudine2@gmail.com', 'uwimpuhweclaudine2@gmail.com', 'Claudine', 'cla1234'),
(4, 'TestCustomer1', 'testcustomer1@gmail.com', 'testcustomer1@gmail.com', 'Customer1', '123456'),
(5, 'Test Customer2', 'testcustomer2@gmail.com', '473878778', 'customer2', '123456'),
(6, 'TestCustomer2', 'testcustomer1@gmail.com', 'testcustomer1@gmail.com', 'Customer3', '1234567'),
(7, 'example:Habimana Eric', 'habimanaeric2@gmail.com', 'habimanaeric2@gmail.com', 'Example Eric', 'umugabo'),
(8, 'Inyange Industries', 'info@inyange.rw', '0780000001', 'inyange', 'hashed_pass'),
(9, 'Sina Gerard Ltd', 'info@sinagerard.rw', '0780000002', 'nyirangarama', 'hashed_pass'),
(10, 'Africa Improved Foods', 'contact@aif.rw', '0780000003', 'aif_rwanda', 'hashed_pass'),
(11, 'Zamu Market', 'sales@zamu.rw', '0780000004', 'zamu_buyer', 'hashed_pass'),
(12, 'Kigali City Market', 'kcm@kigali.rw', '0780000005', 'kcm_admin', 'hashed_pass'),
(13, 'Paul Kagame Store', 'paul@store.rw', '0788111222', 'paul_k', 'hashed_pass'),
(14, 'Dative Uwimana', 'dative@mail.rw', '0788222333', 'dative_u', 'hashed_pass'),
(15, 'Eric Shyaka', 'eric@mail.rw', '0788333444', 'eric_s', 'hashed_pass'),
(16, 'Beata Mukarubayiza', 'beata@mail.rw', '0788444555', 'beata_m', 'hashed_pass'),
(17, 'Alphonse Karemera', 'alphonse@mail.rw', '0788555666', 'alphonse_k', 'hashed_pass'),
(18, 'Safi Gaju', 'safi@mail.rw', '0788666777', 'safi_g', 'hashed_pass'),
(19, 'Olivier Tuyisenge', 'olivier@mail.rw', '0788777888', 'olivier_t', 'hashed_pass'),
(20, 'Solange Niwemwiza', 'solange@mail.rw', '0788888999', 'solange_n', 'hashed_pass'),
(21, 'Ferdinand Mbonigaba', 'ferd@mail.rw', '0788999000', 'ferd_m', 'hashed_pass'),
(22, 'Divine Iradukunda', 'divine@mail.rw', '0788000111', 'divine_i', 'hashed_pass');

-- --------------------------------------------------------

--
-- Table structure for table `farmers`
--

DROP TABLE IF EXISTS `farmers`;
CREATE TABLE IF NOT EXISTS `farmers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `farmers`
--

INSERT INTO `farmers` (`id`, `name`, `phone`, `username`, `password`) VALUES
(1, 'Jane Farmer', '0788000000', 'jane', 'farmer123'),
(2, 'UKWISHAKA Eric', 'ukwishakaeric6@gmail.com', 'Ukwishaka', 'Ukwishaka'),
(3, 'IRANKUNDA Fulgence', 'irankundafulgence@gmail.com', 'Fulgence', 'ful1234'),
(4, 'Test Farmer1', 'testfarmer1@gmail.com', 'Farmer1', '123456'),
(5, 'Niyikiza Emmanuel', '078563536', 'Emmanuel', 'emmanuel'),
(6, 'Farmer2', '646747878', 'Test Farmer2', '123456'),
(7, 'TestFarmer2', 'testfarmer1@gmail.com', 'Farmer2', '123456'),
(9, 'Jean Bosco Habimana', '0788123456', 'bosco_h', 'pass123'),
(10, 'Marie Claire Mutoni', '0788223344', 'claire_m', 'pass123'),
(11, 'Emmanuel Kwizera', '0788334455', 'kwizera_e', 'pass123'),
(12, 'Alice Uwera', '0788445566', 'uwera_a', 'pass123'),
(13, 'Theophile Nsengimana', '0788556677', 'theo_n', 'pass123'),
(14, 'Sifa Gakwaya', '0788667788', 'sifa_g', 'pass123'),
(15, 'Anicet Rwigema', '0788778899', 'anicet_r', 'pass123'),
(16, 'Claudine Mukamana', '0788889900', 'claudine_m', 'pass123'),
(17, 'Damien Ngabo', '0788990011', 'ngabo_d', 'pass123'),
(18, 'Esther Nyirabizeyimana', '0788112233', 'esther_n', 'pass123'),
(19, 'Fidel Mugisha', '0788224466', 'fidel_m', 'pass123'),
(20, 'Grace Umutoni', '0788335577', 'grace_u', 'pass123'),
(21, 'Hassan Murenzi', '0788446688', 'hassan_m', 'pass123'),
(22, 'Innocent Rugamba', '0788557799', 'innocent_r', 'pass123'),
(23, 'Janet Ingabire', '0788668800', 'janet_i', 'pass123');

-- --------------------------------------------------------

--
-- Table structure for table `fields`
--

DROP TABLE IF EXISTS `fields`;
CREATE TABLE IF NOT EXISTS `fields` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `area_ha` double DEFAULT '0',
  `farmer_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `farmer_id` (`farmer_id`)
) ENGINE=MyISAM AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `fields`
--

INSERT INTO `fields` (`id`, `name`, `location`, `area_ha`, `farmer_id`) VALUES
(1, 'BB- Ubuhinzi A', 'Rubavu', 7, 1),
(5, 'CC-Ubuhizi', 'Burera', 4, 3),
(3, 'AA-Ubushogwe', NULL, 0, 2),
(4, 'AA-Zibumba', 'Huye District', 3, 1),
(6, 'CB-Amashyamba', 'Huye', 6, 3),
(7, 'GG-Kiyovu22A', 'Rubavu-rugerero', 5, 4),
(8, 'CC-KIVUMU2211R', 'Huye-Tumba', 2, 4),
(9, 'Ex:bb-busoro113R', 'Rubavu-Rugerero', 3, 4),
(10, 'Kanyinya Plot', 'Nyarugenge, Kigali', 2.5, 1),
(11, 'Kinigi Potato Field', 'Musanze, Northern', 5, 2),
(12, 'Nyagatare Maize Farm', 'Nyagatare, Eastern', 10, 3),
(13, 'Rubavu Tea Estate', 'Rubavu, Western', 15.2, 4),
(14, 'Huye Coffee Hill', 'Huye, Southern', 4.5, 5),
(15, 'Gicumbi Wheat Farm', 'Gicumbi, Northern', 3.8, 6),
(16, 'Bugesera Irrigation Plot', 'Bugesera, Eastern', 8, 7),
(17, 'Nyamagabe Forest Edge', 'Nyamagabe, Southern', 1.2, 8),
(18, 'Rwamagana Rice Swamp', 'Rwamagana, Eastern', 6.5, 9),
(19, 'Karongi Hillside', 'Karongi, Western', 2.2, 10),
(20, 'Rutsiro Coffee Farm', 'Rutsiro, Western', 3.5, 11),
(21, 'Gakenke Vegetable Garden', 'Gakenke, Northern', 0.8, 12),
(22, 'Kamonyi Cassava Field', 'Kamonyi, Southern', 4, 13),
(23, 'Muhanga Bean Plot', 'Muhanga, Southern', 2.1, 14),
(24, 'Kayonza Banana Plantation', 'Kayonza, Eastern', 7.5, 15);

-- --------------------------------------------------------

--
-- Table structure for table `forecasts`
--

DROP TABLE IF EXISTS `forecasts`;
CREATE TABLE IF NOT EXISTS `forecasts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `forecast_date` date NOT NULL,
  `weather_condition` varchar(100) DEFAULT NULL,
  `temperature` double DEFAULT NULL,
  `rainfall` double DEFAULT NULL,
  `recommendation` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `forecasts`
--

INSERT INTO `forecasts` (`id`, `forecast_date`, `weather_condition`, `temperature`, `rainfall`, `recommendation`) VALUES
(1, '2025-11-09', 'cold heavy', 16, 2, 'Guhinga Ibihumyo ndetse mukirinda kwanika imyaka hanze'),
(5, '2025-11-04', 'Imvura nyinshi', 22, 1.2, 'Guhinga Ibirayi'),
(6, '2025-11-08', 'Cold heavy', 16, 2, 'Guhinga umuceri no  kwanura'),
(3, '2025-10-26', 'Light sun', 25, 1.2, 'Kwanika imyaka'),
(4, '2025-10-27', 'light sun', 25, 1.2, 'Kwanika imyaka'),
(7, '2025-12-13', 'Cloudy', 15, 0, 'Light jacket recommended, no need for an umbrella.'),
(8, '2024-12-25', 'Heavy Rain', 22.5, 45, 'Ensure drainage channels are clear.'),
(9, '2024-12-26', 'Sunny', 28, 0, 'Good day for weeding.'),
(10, '2024-12-27', 'Partly Cloudy', 24, 5, 'Apply fertilizer if soil is moist.'),
(11, '2024-12-28', 'Light Rain', 21, 12, 'No irrigation needed.'),
(12, '2024-12-29', 'Thunderstorms', 20, 60, 'Keep livestock indoors.'),
(13, '2024-12-30', 'Mist/Fog', 18, 2, 'Check for fungal diseases in potatoes.'),
(14, '2024-12-31', 'Sunny', 29.5, 0, 'Harvest beans today.'),
(15, '2025-01-01', 'Cloudy', 23, 0, 'Prepare soil for next season.'),
(16, '2025-01-02', 'Sunny', 30, 0, 'Irrigation required for vegetables.'),
(17, '2025-01-03', 'Light Rain', 22, 8, 'Good for planting tubers.'),
(18, '2025-01-04', 'Partly Cloudy', 25, 0, 'Spray organic pesticides.'),
(19, '2025-01-05', 'Heavy Rain', 21, 50, 'Monitor for erosion on hillsides.'),
(20, '2025-01-06', 'Sunny', 27, 0, 'Dry your harvested maize.'),
(21, '2025-01-07', 'Sunny', 28, 0, 'Continue drying activities.'),
(22, '2025-01-08', 'Cloudy', 24, 3, 'General maintenance.');

-- --------------------------------------------------------

--
-- Table structure for table `harvest`
--

DROP TABLE IF EXISTS `harvest`;
CREATE TABLE IF NOT EXISTS `harvest` (
  `id` int NOT NULL AUTO_INCREMENT,
  `crop_id` int DEFAULT NULL,
  `harvest_date` date DEFAULT NULL,
  `quantity` double DEFAULT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `crop_id` (`crop_id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `harvest`
--

INSERT INTO `harvest` (`id`, `crop_id`, `harvest_date`, `quantity`, `notes`, `created_at`) VALUES
(1, 1, '2025-10-26', 4000, 'all times', '2025-10-26 09:04:28'),
(2, 3, '2025-10-31', 400, 'Every week', '2025-10-31 11:14:19'),
(3, 4, '2025-11-08', 400, 'Every week', '2025-11-08 21:37:42'),
(4, 5, '2025-12-17', 380, 'Harvest are available at market', '2025-12-17 13:46:40'),
(5, 4, '2024-12-20', 450, 'Excellent bean quality from Muhanga plot.', '2025-12-24 17:43:24'),
(6, 5, '2024-12-10', 2600, 'Rice harvest successful despite early rain.', '2025-12-24 17:43:24'),
(7, 13, '2024-12-05', 3800, 'Sweetest pineapples from Karongi hillside.', '2025-12-24 17:43:24'),
(8, 6, '2024-11-28', 150, 'Weekly tea plucking session.', '2025-12-24 17:43:24'),
(9, 1, '2025-01-20', 1600, 'Maize dried and ready for storage.', '2025-12-24 17:43:24'),
(10, 2, '2025-01-15', 5200, 'Large Kinigi potatoes from Musanze.', '2025-12-24 17:43:24'),
(11, 3, '2025-05-15', 850, 'First major coffee wash of the season.', '2025-12-24 17:43:24'),
(12, 7, '2025-02-15', 1300, 'Gicumbi wheat stored in dry sacks.', '2025-12-24 17:43:24'),
(13, 8, '2025-03-05', 7800, 'High starch cassava content found.', '2025-12-24 17:43:24'),
(14, 9, '2025-01-05', 4700, 'Kayonza banana bunches - Grade A.', '2025-12-24 17:43:24'),
(15, 10, '2025-02-10', 580, 'Fresh cabbages delivered to local market.', '2025-12-24 17:43:24'),
(16, 11, '2025-01-20', 950, 'Sorghum harvested for traditional processing.', '2025-12-24 17:43:24'),
(17, 12, '2025-01-25', 2100, 'Orange-fleshed sweet potatoes.', '2025-12-24 17:43:24'),
(18, 14, '2024-12-22', 1100, 'Purple passion fruits - export quality.', '2025-12-24 17:43:24'),
(19, 15, '2025-02-05', 350, 'Green peas for Kigali wholesale.', '2025-12-24 17:43:24');

-- --------------------------------------------------------

--
-- Table structure for table `irrigation_schedule`
--

DROP TABLE IF EXISTS `irrigation_schedule`;
CREATE TABLE IF NOT EXISTS `irrigation_schedule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `field_id` int DEFAULT NULL,
  `schedule_date` date DEFAULT NULL,
  `duration_hours` double DEFAULT NULL,
  `water_volume` double DEFAULT NULL,
  `notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `field_id` (`field_id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `irrigation_schedule`
--

INSERT INTO `irrigation_schedule` (`id`, `field_id`, `schedule_date`, `duration_hours`, `water_volume`, `notes`, `created_at`) VALUES
(1, 1, '2025-10-26', 1, 4, 'inshuro 3 kumunsi', '2025-10-26 08:25:52'),
(2, 5, '2025-10-31', 1, 3, 'three times per day', '2025-10-31 11:13:25'),
(3, 8, '2025-11-08', 1, 2, '3 time per day', '2025-11-08 21:34:20'),
(4, 7, '2025-11-14', 2, 2, 'one time per day', '2025-11-08 21:36:47'),
(5, 7, '2024-12-25', 4, 500, 'Bugesera solar pump session.', '2025-12-24 17:43:42'),
(6, 3, '2024-12-26', 6, 800, 'Nyagatare morning irrigation.', '2025-12-24 17:43:42'),
(7, 15, '2024-12-27', 3, 300, 'Banana plantation hydration.', '2025-12-24 17:43:42'),
(8, 12, '2024-12-28', 2, 150, 'Vegetable garden misting.', '2025-12-24 17:43:42'),
(9, 1, '2024-12-29', 4.5, 400, 'Kanyinya plot base watering.', '2025-12-24 17:43:42'),
(10, 9, '2024-12-30', 5, 600, 'Rwamagana rice swamp maintenance.', '2025-12-24 17:43:42'),
(11, 13, '2024-12-31', 3, 350, 'Kamonyi cassava field.', '2025-12-24 17:43:42'),
(12, 2, '2025-01-01', 2, 200, 'Light watering for Kinigi potatoes.', '2025-12-24 17:43:42'),
(13, 5, '2025-01-02', 4, 450, 'Huye coffee hill irrigation.', '2025-12-24 17:43:42'),
(14, 6, '2025-01-03', 3.5, 300, 'Gicumbi wheat soil moisture.', '2025-12-24 17:43:42'),
(15, 10, '2025-01-04', 2.5, 250, 'Karongi hillside pineapples.', '2025-12-24 17:43:42'),
(16, 11, '2025-01-05', 4, 400, 'Rutsiro coffee irrigation.', '2025-12-24 17:43:42'),
(17, 4, '2025-01-06', 5, 700, 'Rubavu tea estate.', '2025-12-24 17:43:42'),
(18, 8, '2025-01-07', 2, 180, 'Nyamagabe forest edge peas.', '2025-12-24 17:43:42'),
(19, 14, '2025-01-08', 3, 320, 'Muhanga bean plot.', '2025-12-24 17:43:42');

-- --------------------------------------------------------

--
-- Table structure for table `market`
--

DROP TABLE IF EXISTS `market`;
CREATE TABLE IF NOT EXISTS `market` (
  `id` int NOT NULL AUTO_INCREMENT,
  `harvest_id` int DEFAULT NULL,
  `market_name` varchar(150) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `status` varchar(64) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `harvest_id` (`harvest_id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `market`
--

INSERT INTO `market` (`id`, `harvest_id`, `market_name`, `price`, `status`, `created_at`) VALUES
(1, 1, 'Rugerero', 2000, 'Listed', '2025-10-26 09:21:32'),
(3, 1, 'Bushenge', 4000, 'Arrive at market', '2025-10-28 09:36:41'),
(4, 2, 'Rubavu market main', 3000, 'Listed', '2025-10-31 11:15:05'),
(5, 3, 'Rubavu', 500, 'Listed', '2025-11-08 21:38:10'),
(6, 3, 'Huye Main Market', 600, 'IN THE WAY', '2025-11-08 21:39:27'),
(7, 3, 'Gisenyi Market', 600, 'Listed', '2025-11-08 21:41:33'),
(8, 1, 'Kimironko Market', 850, 'Available', '2025-12-24 17:44:05'),
(9, 2, 'Nyabugogo Market', 1250, 'Available', '2025-12-24 17:44:05'),
(10, 3, 'Gisenyi Border Market', 550, 'Sold Out', '2025-12-24 17:44:05'),
(11, 4, 'Huye Central Market', 3200, 'Available', '2025-12-24 17:44:05'),
(12, 5, 'Nyagatare Local Market', 380, 'In Stock', '2025-12-24 17:44:05'),
(13, 6, 'Musanze Market', 480, 'Available', '2025-12-24 17:44:05'),
(14, 7, 'Kigali City Market', 2600, 'In Stock', '2025-12-24 17:44:05'),
(15, 8, 'Byumba Market', 750, 'Available', '2025-12-24 17:44:05'),
(16, 9, 'Muhanga Market', 650, 'Limited', '2025-12-24 17:44:05'),
(17, 10, 'Kayonza Market', 280, 'Available', '2025-12-24 17:44:05'),
(18, 11, 'Kigeme Market', 600, 'In Stock', '2025-12-24 17:44:05'),
(19, 12, 'Gicumbi Market', 550, 'Available', '2025-12-24 17:44:05'),
(20, 13, 'Rwamagana Market', 320, 'In Stock', '2025-12-24 17:44:05'),
(21, 14, 'Karongi Market', 1300, 'Limited', '2025-12-24 17:44:05'),
(22, 15, 'Rubavu Market', 1900, 'In Stock', '2025-12-24 17:44:05');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `buyer_id` int DEFAULT NULL,
  `buyer_name` varchar(150) DEFAULT NULL,
  `quantity_ordered` int NOT NULL,
  `order_date` date NOT NULL,
  `status` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `buyer_id` (`buyer_id`)
) ENGINE=MyISAM AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `product_id`, `buyer_id`, `buyer_name`, `quantity_ordered`, `order_date`, `status`) VALUES
(1, 1, 1, 'Aphrodis Niyonsenga', 10, '2025-10-20', 'Completed'),
(2, 2, 2, 'Habimana Piere', 20, '2025-10-21', 'Accepted'),
(3, 1, 2, 'Habimana Piere', 5, '2025-10-21', 'Accepted'),
(4, 1, 2, 'Habimana Piere', 2, '2025-10-24', 'Pending'),
(5, 1, 2, 'Habimana Piere', 1, '2025-10-25', 'Pending'),
(6, 4, 2, 'Habimana Piere', 30, '2025-10-28', 'Pending'),
(7, 3, 2, 'Habimana Piere', 57, '2025-10-29', 'Completed'),
(9, 5, 3, 'UWIMPUHWE Claudine', 10, '2025-10-31', 'Completed'),
(10, 4, 3, 'UWIMPUHWE Claudine', 4, '2025-11-07', 'Pending'),
(11, 6, 4, 'TestCustomer1', 20, '2025-11-08', 'Accepted'),
(12, 4, 4, 'TestCustomer1', 226, '2025-12-12', 'Pending'),
(13, 3, 4, 'TestCustomer1', 226, '2025-12-12', 'Pending'),
(14, 1, 1, 'Inyange Industries', 500, '2024-12-20', 'Completed'),
(15, 3, 2, 'Sina Gerard Ltd', 50, '2024-12-21', 'Pending'),
(16, 4, 3, 'Africa Improved Foods', 1000, '2024-12-22', 'Shipped'),
(17, 2, 4, 'Zamu Market', 100, '2024-12-23', 'Processing'),
(18, 5, 5, 'Kigali City Market', 200, '2024-12-24', 'Completed'),
(19, 7, 6, 'Paul Kagame Store', 300, '2024-12-24', 'Completed'),
(20, 9, 7, 'Dative Uwimana', 20, '2024-12-24', 'Delivered'),
(21, 10, 8, 'Eric Shyaka', 50, '2024-12-24', 'Processing'),
(22, 12, 9, 'Beata Mukarubayiza', 40, '2024-12-24', 'Shipped'),
(23, 13, 10, 'Alphonse Karemera', 60, '2024-12-24', 'Completed'),
(24, 14, 11, 'Safi Gaju', 100, '2024-12-24', 'Pending'),
(25, 15, 12, 'Olivier Tuyisenge', 150, '2024-12-24', 'Processing'),
(26, 6, 13, 'Solange Niwemwiza', 10, '2024-12-24', 'Completed'),
(27, 8, 14, 'Ferdinand Mbonigaba', 25, '2024-12-24', 'Delivered'),
(28, 11, 15, 'Divine Iradukunda', 30, '2024-12-24', 'Shipped');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `method` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `paid_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`id`, `order_id`, `amount`, `method`, `status`, `paid_at`) VALUES
(1, 7, 85500, 'MOMO', 'COMPLETED', '2025-10-29 04:27:29'),
(2, 7, 85500, 'CARD', 'COMPLETED', '2025-10-29 04:27:32'),
(5, 11, 0, 'CASH', 'COMPLETED', '2025-11-08 19:18:47'),
(4, 9, 0, 'CARD', 'COMPLETED', '2025-10-31 09:21:59'),
(6, 1, 225000, 'Bank Transfer', 'Successful', '2025-12-24 17:44:26'),
(7, 2, 125000, 'MomoPay', 'Pending', '2025-12-24 17:44:26'),
(8, 3, 350000, 'Bank Transfer', 'Successful', '2025-12-24 17:44:26'),
(9, 4, 70000, 'MomoPay', 'Successful', '2025-12-24 17:44:26'),
(10, 5, 160000, 'MomoPay', 'Successful', '2025-12-24 17:44:26'),
(11, 6, 360000, 'Bank Transfer', 'Successful', '2025-12-24 17:44:26'),
(12, 7, 5000, 'MomoPay', 'Successful', '2025-12-24 17:44:26'),
(13, 8, 30000, 'Cash', 'Pending', '2025-12-24 17:44:26'),
(14, 9, 20000, 'MomoPay', 'Successful', '2025-12-24 17:44:26'),
(15, 10, 72000, 'MomoPay', 'Successful', '2025-12-24 17:44:26'),
(16, 11, 30000, 'Bank Transfer', 'Pending', '2025-12-24 17:44:26'),
(17, 12, 75000, 'MomoPay', 'Successful', '2025-12-24 17:44:26'),
(18, 13, 15000, 'MomoPay', 'Successful', '2025-12-24 17:44:26'),
(19, 14, 75000, 'Cash', 'Successful', '2025-12-24 17:44:26'),
(20, 15, 54000, 'MomoPay', 'Successful', '2025-12-24 17:44:26');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `description` text,
  `price` double NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `category` varchar(80) DEFAULT NULL,
  `seller_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `seller_id` (`seller_id`)
) ENGINE=MyISAM AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `description`, `price`, `quantity`, `category`, `seller_id`) VALUES
(1, 'Fresh Maize', 'High-quality organic maize', 800, 2, 'Cereals', 1),
(4, 'Ibigori', 'Global', 500, 1740, 'AA', 1),
(3, 'Umuceri', 'Local', 1500, 3713, 'A', 1),
(5, 'Ibinyomoro', 'Local', 3000, 390, 'A', 3),
(6, 'Ibitoki', 'Ikinyamunyu', 500, 3980, 'BB', 4),
(7, 'Kinigi Potatoes', 'Freshly harvested in Musanze', 450, 1000, 'Tubers', 2),
(8, 'Gicumbi Wheat', 'High protein wheat flour source', 700, 500, 'Grains', 6),
(9, 'Arabica Coffee', 'Green coffee beans from Huye', 2500, 200, 'Beverage', 5),
(10, 'Maize Grain', 'Dried yellow maize from Nyagatare', 350, 2000, 'Grains', 3),
(11, 'Huye Beans', 'Dry red beans - RWR variety', 800, 300, 'Legumes', 14),
(12, 'Akabanga Chillies', 'Extra hot small chillies', 1500, 50, 'Vegetables', 12),
(13, 'Rwamagana Rice', 'Grade 1 Long grain rice', 1200, 800, 'Grains', 9),
(14, 'Rubavu Tea', 'Loose leaf black tea', 3000, 100, 'Beverage', 4),
(15, 'Kayonza Bananas', 'Ripe dessert bananas', 250, 400, 'Fruits', 15),
(16, 'Kamonyi Cassava Flour', 'Fine white cassava flour', 600, 600, 'Flour', 13),
(17, 'Nyamagabe Peas', 'Shelled green peas', 1800, 150, 'Vegetables', 8),
(18, 'Karongi Pineapple', 'Sweet organic pineapples', 500, 300, 'Fruits', 10),
(19, 'Rutsiro Passion Fruit', 'Sweet purple variety', 1200, 250, 'Fruits', 11),
(20, 'Kanyinya Sweet Potatoes', 'Orange-fleshed variety', 300, 700, 'Tubers', 1),
(21, 'Bugesera Sorghum', 'Malt quality sorghum', 500, 900, 'Grains', 7);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
