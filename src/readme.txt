DROP DATABASE IF EXISTS `2pc_inventory`;
CREATE DATABASE IF NOT EXISTS `2pc_inventory` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `2pc_inventory`;

CREATE TABLE `inventory` (
  `item` varchar(32) NOT NULL,
  `inventoryNum` int NOT NULL,
  PRIMARY KEY (`item`),
  UNIQUE KEY `id_UNIQUE` (`item`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `inventoryLog` (
  `id` varchar(32) NOT NULL,
  `stage` varchar(45) DEFAULT NULL,
  `_from` varchar(128) DEFAULT NULL,
  `_to` varchar(128) DEFAULT NULL,
  `content` varchar(128) DEFAULT NULL,
  `msg` varchar(128) DEFAULT NULL,
  `port` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP DATABASE IF EXISTS `2pc_order`;
CREATE DATABASE `2pc_order` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `2pc_order`;

CREATE TABLE `order` (
  `id` varchar(32) NOT NULL,
  `iPhone` int NOT NULL,
  `iPad` int NOT NULL,
  `iMac` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `orderLog` (
  `id` varchar(32) NOT NULL,
  `stage` varchar(45) DEFAULT NULL,
  `_from` varchar(128) DEFAULT NULL,
  `_to` varchar(128) DEFAULT NULL,
  `content` varchar(128) DEFAULT NULL,
  `msg` varchar(128) DEFAULT NULL,
  `port` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;