#!/bin/sh
echo "[INFO] update pgcliveplatformserver start"
mysql -uroot -proot -N -e "
use pgcliveplatformserver;
ALTER TABLE delayer_task MODIFY status VARCHAR(255);
UPDATE delayer_task SET status = null;

ALTER TABLE ip_switch_task MODIFY status VARCHAR(255);
UPDATE ip_switch_task SET status = null;

ALTER TABLE live_task MODIFY live_task_status VARCHAR(255);
UPDATE live_task SET live_task_status = null;

ALTER TABLE recorder_task MODIFY recorder_task_status VARCHAR(255);
UPDATE recorder_task SET recorder_task_status = null;"
echo "[INFO] update pgcliveplatformserver end"