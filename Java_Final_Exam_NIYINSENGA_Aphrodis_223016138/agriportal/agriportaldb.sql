-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 19, 2025 at 08:28 PM
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
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
(8, 'Ibishyimbo', 'Local', '2025-11-17', '2026-03-09', 'Planted', 130, 8);

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
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
(7, 'example:Habimana Eric', 'habimanaeric2@gmail.com', 'habimanaeric2@gmail.com', 'Example Eric', 'umugabo');

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
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
(8, '', '', '', '');

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
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
(9, 'Ex:bb-busoro113R', 'Rubavu-Rugerero', 3, 4);

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
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `forecasts`
--

INSERT INTO `forecasts` (`id`, `forecast_date`, `weather_condition`, `temperature`, `rainfall`, `recommendation`) VALUES
(1, '2025-11-09', 'cold heavy', 16, 2, 'Guhinga Ibihumyo ndetse mukirinda kwanika imyaka hanze'),
(5, '2025-11-04', 'Imvura nyinshi', 22, 1.2, 'Guhinga Ibirayi'),
(6, '2025-11-08', 'Cold heavy', 16, 2, 'Guhinga umuceri no  kwanura'),
(3, '2025-10-26', 'Light sun', 25, 1.2, 'Kwanika imyaka'),
(4, '2025-10-27', 'light sun', 25, 1.2, 'Kwanika imyaka'),
(7, '2025-12-13', 'Cloudy', 15, 0, 'Light jacket recommended, no need for an umbrella.');

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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `harvest`
--

INSERT INTO `harvest` (`id`, `crop_id`, `harvest_date`, `quantity`, `notes`, `created_at`) VALUES
(1, 1, '2025-10-26', 4000, 'all times', '2025-10-26 09:04:28'),
(2, 3, '2025-10-31', 400, 'Every week', '2025-10-31 11:14:19'),
(3, 4, '2025-11-08', 400, 'Every week', '2025-11-08 21:37:42'),
(4, 5, '2025-12-17', 380, 'Harvest are available at market', '2025-12-17 13:46:40');

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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `irrigation_schedule`
--

INSERT INTO `irrigation_schedule` (`id`, `field_id`, `schedule_date`, `duration_hours`, `water_volume`, `notes`, `created_at`) VALUES
(1, 1, '2025-10-26', 1, 4, 'inshuro 3 kumunsi', '2025-10-26 08:25:52'),
(2, 5, '2025-10-31', 1, 3, 'three times per day', '2025-10-31 11:13:25'),
(3, 8, '2025-11-08', 1, 2, '3 time per day', '2025-11-08 21:34:20'),
(4, 7, '2025-11-14', 2, 2, 'one time per day', '2025-11-08 21:36:47');

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
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `market`
--

INSERT INTO `market` (`id`, `harvest_id`, `market_name`, `price`, `status`, `created_at`) VALUES
(1, 1, 'Rugerero', 2000, 'Listed', '2025-10-26 09:21:32'),
(3, 1, 'Bushenge', 4000, 'Arrive at market', '2025-10-28 09:36:41'),
(4, 2, 'Rubavu market main', 3000, 'Listed', '2025-10-31 11:15:05'),
(5, 3, 'Rubavu', 500, 'Listed', '2025-11-08 21:38:10'),
(6, 3, 'Huye Main Market', 600, 'IN THE WAY', '2025-11-08 21:39:27'),
(7, 3, 'Gisenyi Market', 600, 'Listed', '2025-11-08 21:41:33');

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
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
(13, 3, 4, 'TestCustomer1', 226, '2025-12-12', 'Pending');

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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`id`, `order_id`, `amount`, `method`, `status`, `paid_at`) VALUES
(1, 7, 85500, 'MOMO', 'COMPLETED', '2025-10-29 04:27:29'),
(2, 7, 85500, 'CARD', 'COMPLETED', '2025-10-29 04:27:32'),
(5, 11, 0, 'CASH', 'COMPLETED', '2025-11-08 19:18:47'),
(4, 9, 0, 'CARD', 'COMPLETED', '2025-10-31 09:21:59');

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
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `description`, `price`, `quantity`, `category`, `seller_id`) VALUES
(1, 'Fresh Maize', 'High-quality organic maize', 800, 2, 'Cereals', 1),
(4, 'Ibigori', 'Global', 500, 1740, 'AA', 1),
(3, 'Umuceri', 'Local', 1500, 3713, 'A', 1),
(5, 'Ibinyomoro', 'Local', 3000, 390, 'A', 3),
(6, 'Ibitoki', 'Ikinyamunyu', 500, 3980, 'BB', 4);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
