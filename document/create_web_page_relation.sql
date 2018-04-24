drop table if exists web_page_relation;

CREATE TABLE `web_page_relation` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `url` text,
  `src_url` text,
  `crawl_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='网页关系表';


