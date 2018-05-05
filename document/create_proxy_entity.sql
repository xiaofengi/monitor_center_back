CREATE TABLE `proxy_entity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `host` varchar(45) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `using` tinyint(1) DEFAULT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8