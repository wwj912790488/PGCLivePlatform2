
-- ----------------------------
-- create Table structure for `content_template`
-- ----------------------------

DROP TABLE IF EXISTS `content_template`;
CREATE TABLE `content_template` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '模板名称',
  `display_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '标识名称',
  `video_format` varchar(20) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '视频编码格式',
  `audio_format` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '音频编码格式',
  `video_width` int(11) DEFAULT NULL COMMENT '分辨率：宽',
  `video_height` int(11) DEFAULT NULL COMMENT '分辨率：高',
  `video_bitrate` int(11) DEFAULT NULL COMMENT '视频码率',
  `audio_bitrate` int(11) DEFAULT NULL COMMENT '音频码率',
  `frame_rate` int(11) DEFAULT NULL COMMENT '视频帧率',
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '模板类型：1、系统默认模板；2：用户模板',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建者id',
  `create_user_org` bigint(20) DEFAULT NULL COMMENT '创建者组织id',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/**
 * 默认模板
 */
INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 1, 128, 'AAC', '高清', 25, 'IPTV高清', 1, 8000, 'H264', 1080, 1920 from dual where not exists(select * from `content_template` where id = 1);

INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 2, 96, 'AAC',  '标清', 25, 'IPTV标清', 1, 2500, 'H264', 576, 720 from dual where not exists(select * from `content_template` where id = 2);


INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 3, 128, 'AAC', '高清', 25, 'OTT高清', 1, 3000, 'H264', 1080, 1920 from dual where not exists(select * from `content_template` where id = 3);


INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 4, 64, 'AAC',  '标清', 25, 'OTT标清', 1, 1000, 'H264', 576, 720 from dual where not exists(select * from `content_template` where id = 4);

INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 5, 96, 'AAC',  '1080p', 25, '互联网1080p', 1, 2500, 'H264', 1080, 1920 from dual where not exists(select * from `content_template` where id = 5);

INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 6, 64, 'AAC',  '超清', 25, '互联网超清720p', 1, 1500, 'H264', 720, 1280 from dual where not exists(select * from `content_template` where id = 6);

INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 7, 64, 'AAC', '高清', 25, '互联网高清480p', 1, 800, 'H264', 480, 854 from dual where not exists(select * from `content_template` where id = 7);

INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 8, 48, 'AAC', '标清', 25, '互联网标清360p', 1, 500, 'H264', 360, 640 from dual where not exists(select * from `content_template` where id = 8);

INSERT INTO `content_template` (id,audio_bitrate,audio_format,display_name,frame_rate,name,type,video_bitrate,video_format,video_height,video_width)
  select 9, 32, 'AAC', '流畅', 25, '互联网流畅240p', 1, 350, 'H264', 240, 426 from dual where not exists(select * from `content_template` where id = 9);


