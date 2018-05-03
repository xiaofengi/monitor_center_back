drop table if exists web_page_detail;

CREATE TABLE `web_page_detail` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `url_md5` char(32) DEFAULT NULL,
  `url` varchar(2000) DEFAULT NULL,
  `domain` varchar(200) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `src` varchar(500) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL COMMENT '文章发表时间',
  `author` varchar(200) DEFAULT NULL,
  `keyword` varchar(200) DEFAULT NULL,
  `tags` varchar(200) DEFAULT NULL,
  `content` mediumtext CHARACTER SET utf8mb4,
  `html` mediumtext CHARACTER SET utf8mb4,
  `view_num` int(10) DEFAULT NULL,
  `comment_num` int(10) DEFAULT NULL,
  `crawl_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_wpd` (`url_md5`,`domain`,`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网页详情表'


