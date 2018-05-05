drop table if exists web_page_resource;

CREATE TABLE `web_page_resource` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `url_detail_id` bigint(15) DEFAULT NULL,
  `url` text,
  `resource_url` text,
  `resource_type` smallint(2) DEFAULT NULL,
  `crawl_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网页资源表'
