drop table if exists web_page_detail;

CREATE TABLE `web_page_detail` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `url_md5` char(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `domain` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `src` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL COMMENT '文章发表时间',
  `author` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tags` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `content` mediumtext CHARACTER SET utf8mb4,
  `html` mediumtext CHARACTER SET utf8mb4,
  `view_num` int(10) DEFAULT NULL,
  `comment_num` int(10) DEFAULT NULL,
  `crawl_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_wpd` (`url_md5`,`domain`,`title`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='网页详情表';


