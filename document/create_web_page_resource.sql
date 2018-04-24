drop table if exists web_page_resource;

CREATE TABLE `web_page_resource` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `url` text,
  `resource_url` text,
  `resource_type` smallint(2) DEFAULT NULL,
  `crawl_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='网页资源表';
