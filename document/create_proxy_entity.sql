CREATE TABLE `proxy_entity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `host` varchar(45) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `location` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `anonymous_type` varchar(50) DEFAULT NULL,
  `res_time` varchar(50) DEFAULT NULL,
  `is_using` tinyint(1) DEFAULT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `last_use_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_pe` (`host`,`port`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

