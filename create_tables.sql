-- 微博热搜新闻
CREATE TABLE weibo_top_summary (
   news_id int NOT NULL AUTO_INCREMENT,
   date timestamp NOT NULL,
   filtered bool NOT NULL DEFAULT false,
   filtered_words char(128) CHARACTER SET utf8mb4 NULL,
   search_times int NOT NULL,
   log_id bigint NOT NULL,
   title char(128) NOT NULL,
   hot char(128) NULL COMMENT '热，新，荐，爆',
   url varchar(65000) NOT NULL,
   rank smallint NULL COMMENT '微博热搜排名',
   CONSTRAINT weibo_top_summary_pk PRIMARY KEY (news_id)
) DEFAULT CHARSET=utf8 COMMENT '存储新浪微博标题，日期，是否被过滤（是否有明星字样），过滤的字样，,搜索指数,对应的日志id，是否已经阅读';
CREATE INDEX weibo_top_summary_idx_title ON weibo_top_summary (title);

-- 日志
CREATE TABLE IF NOT EXISTS spider_log (
   log_id bigint NOT NULL COMMENT ' time_in_milli左移10位 + 0到1023的随机数（补足剩余10位）    生成ID',
   date timestamp NOT NULL,
   content mediumtext  CHARACTER SET utf8mb4 NOT NULL COMMENT '抓取的内容信息',
   url varchar(65000) NOT NULL,
   successful bool NOT NULL COMMENT '是否抓取成功',
   CONSTRAINT spider_log_pk PRIMARY KEY (log_id)
 ) DEFAULT CHARSET=utf8 COMMENT '抓取记录（抓取时间，日志的类别，抓取的网页内容）';