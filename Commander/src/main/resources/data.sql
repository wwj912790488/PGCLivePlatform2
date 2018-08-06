/**插入菜单**/
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 1, '/dashboard', '平台概况', 0, ',0,',0,'/dashboard' from dual where not exists(select * from `u_menu` where id = 1);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 2, '/device/control', '设备管理', 0, ',0,',0,'/device/control' from dual where not exists(select * from `u_menu` where id = 2);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 3, '/content', '活动管理', 0, ',0,',2,'/content' from dual where not exists(select * from `u_menu` where id = 3);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 4, '/template', '模板管理', 0, ',0,',2,'/template' from dual where not exists(select * from `u_menu` where id = 4);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 5, '/supervisor', '活动监看', 0, ',0,',0,'/supervisor' from dual where not exists(select * from `u_menu` where id = 5);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 6, '/material', '素材管理', 0, ',0,',2,'/material' from dual where not exists(select * from `u_menu` where id = 6);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 7, '/alert', '告警管理', 0, ',0,',2,'/alert' from dual where not exists(select * from `u_menu` where id = 7);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 8, '/log', '日志管理', 0, ',0,',2,'/log' from dual where not exists(select * from `u_menu` where id = 8);
INSERT INTO `u_menu` (id,name,description,parent_id,parent_ids,role_type,url) select 9, '/user', '用户管理', 0, ',0,',2,'/user' from dual where not exists(select * from `u_menu` where id = 9);
/**插入admin用户 ，预设角色及相关权限**/
INSERT INTO user (name, password, role_type, real_name, remarks,user_id) SELECT 'admin', 'c4ca4238a0b923820dcc509a6f75849b', 0, 'admin', '','013298EEF2E64F9CB683EA1136C6F404' FROM DUAL WHERE NOT EXISTS (SELECT * FROM `user` where name='admin');

INSERT INTO u_role (id,create_time,menu_ids,role_name,role_type) SELECT 1,'2018-06-27 17:04:35' ,'1,2,3,4,5,6,7,8,9,10,','超级管理员',0 FROM DUAL WHERE NOT EXISTS (SELECT * FROM `u_role` where id = 1);
INSERT INTO u_user_role (role_id, user_id) select 1,'013298EEF2E64F9CB683EA1136C6F404' from DUAL where not exists (SELECT * FROM `u_user_role`);


/**
 * template data
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

INSERT INTO `udp_range` (id, ip_begin, ip_end, port_begin, port_end)
  select 1, '239.0.0.1', '239.255.255.255', 1, 65535 from dual where not exists(select * from `udp_range` where id=1);