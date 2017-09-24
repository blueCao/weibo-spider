-- 微博热搜新闻
CREATE TABLE weibo_top_summary (
   news_id int NOT NULL AUTO_INCREMENT,
   date timestamp NOT NULL,
   filtered bool NOT NULL DEFAULT false,
   filtered_words char(128) NULL,
   search_times int NOT NULL,
   log_id bigint NOT NULL,
   title char(128) NOT NULL,
   CONSTRAINT weibo_top_summary_pk PRIMARY KEY (news_id)
) DEFAULT CHARSET=utf8 COMMENT '存储新浪微博标题，日期，是否被过滤（是否有明星字样），过滤的字样，,搜索指数,对应的日志id，是否已经阅读';
CREATE INDEX weibo_top_summary_idx_title ON weibo_top_summary (title);

-- 日志
CREATE TABLE IF NOT EXISTS spider_log (
   log_id bigint NOT NULL COMMENT '将date转换成 成long * 10 + 0到9的随机数    生成ID',
   date timestamp NOT NULL,
   content mediumtext  CHARACTER SET utf8mb4 NOT NULL COMMENT '抓取的内容信息',
   url varchar(65000) NOT NULL,
   CONSTRAINT spider_log_pk PRIMARY KEY (log_id)
) COMMENT '抓取记录（抓取时间，日志的类别，抓取的网页内容）';