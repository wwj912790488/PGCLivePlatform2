/**
 * Created by slw on 2018/3/21.
 */
$(function () {
    $.fn.handlebars = function(template, data) {
        var compiled = {};
        if (template instanceof jQuery) {
            template = $(template).html();
        }
        compiled[template] = Handlebars.compile(template);
        this.html(compiled[template](data));
        return this.html();
    };

    var type = {"LT": "左上", "RT": "右上", "LB": "左下", RB: "右下"};

    Handlebars.registerHelper({
        'if_eq': function(a, b, opts) {
            if(a == b)
                return opts.fn(this);
            else
                return opts.inverse(this);
        },
        'getSourceURI': function (channel) {
            if (channel == null)
                return;
            if (channel.streamType == 1) {
                return channel.pushUri;
            } else {
                return channel.sourceUri;
            }
        },
        'ifSourceUriEmpty': function(channel, opts) {
            var flag = false;
            if (channel != null) {
                if (channel.streamType == 1) {
                    flag = channel.pushUri != null && channel.pushUri != 'undefined' && channel.pushUri.length > 0;
                } else {
                    flag = channel.sourceUri != null && channel.sourceUri != 'undefined' && channel.sourceUri.length > 0;
                }
            }

            if (flag) {
                return opts.fn(this);
            } else {
                return opts.inverse(this);
            }
        },
        'getConveneStateInfo': function(content) {
            var m_state = '-';
            var s_state = '-';
            if (content.status == "RUNNING") {
                var master = content.master;
                if (master != null && master.status == 'RUNNING') {
                    if (master.streamStatus == "PUSHING") {
                        m_state = 'Y';
                    } else {
                        m_state = 'N';
                    }
                }
                var slave = content.slave;
                if (slave != null && slave.status == 'RUNNING') {
                    if (slave.streamStatus == "PUSHING") {
                        s_state = 'Y';
                    } else {
                        s_state = 'N';
                    }
                }
            }
            return "汇聚 ["+ m_state +"] [" + s_state + "]"
        },
        'getConveneStateLevel': function(content) {
            var labelLevel = 'label-default';
            if (content.status == "RUNNING") {
                var master = content.master;
                var slave = content.slave;
                if ((master != null && master.status == 'RUNNING') || (slave != null && slave.status == 'RUNNING')) {
                    labelLevel = 'label-success';
                    if (content.conveneAlertLevel == 'ERROR') {
                        labelLevel = 'label-danger';
                    } else if (content.conveneAlertLevel == 'WARNING') {
                        labelLevel = 'label-warning';
                    }
                }
            }
            return labelLevel;
        },
        'getDelayerStateInfo': function(content) {
            var m_duration = "-";
            var s_duration = "-";
            if (content.status == "RUNNING") {
                var master = content.master;
                if (master != null && master.delayerTask != null && master.delayerTask.status == 'RUNNING') {
                    m_duration = master.duration ? master.duration : 0;
                }

                var slave = content.slave;
                if (slave != null && slave.delayerTask != null && slave.delayerTask.status == 'RUNNING') {
                    s_duration = slave.duration ? slave.duration : 0;
                }
            }

            return "延时 ["+ m_duration +"] [" + s_duration + "]"
        },
        'getDelayerStateLevel': function(content) {
            var labelLevel = 'label-default';
            if (content.status == "RUNNING") {
                var master = content.master;
                var slave = content.slave;
                if ((master != null && master.delayerTask != null && master.delayerTask.status == 'RUNNING') ||
                    (slave != null && slave.delayerTask != null && slave.delayerTask.status == 'RUNNING')) {
                    labelLevel = 'label-success';
                    if (content.delayerAlertLevel == 'ERROR') {
                        labelLevel = 'label-danger';
                    } else if (content.delayerAlertLevel == 'WARNING') {
                        labelLevel = 'label-warning';
                    }
                }
            }
            return labelLevel;
        },
        'getIpSwitchStateInfo': function(content) {
            var type = "切换器";
            var m_source = "";
            var s_source = "";
            var b_source = "";
            if (content.status == "RUNNING") {
                var ipSwitch = content.ipswitch;
                if (ipSwitch != null) {
                    if (ipSwitch.currentType == 'AUTO') {
                        type = "自动 ";
                    } else {
                        type = "手动 ";
                    }

                    if (ipSwitch.masterSourceStatus == 'PUSHING') {
                        m_source = 'Y';
                    } else {
                        m_source = 'N';
                    }

                    if (ipSwitch.slaveSourceStatus == 'PUSHING') {
                        s_source = 'Y';
                    } else {
                        s_source = 'N';
                    }

                    if (ipSwitch.backupSourceStatus == 'PUSHING') {
                        b_source = 'Y';
                    } else {
                        b_source = 'N';
                    }

                    if (ipSwitch.currentSource == 'MASTER') {
                        m_source = "[" + m_source + "]";
                    } else if (ipSwitch.currentSource == 'SLAVE') {
                        s_source = "[" + s_source + "]";
                    } else if (ipSwitch.currentSource == 'BACKUP') {
                        b_source = "[" + b_source + "]";
                    }
                }
            }

            return type + m_source + s_source + b_source;
        },
        'getIpSwitchStateLevel': function (content) {
            var labelLevel = 'label-default';
            var ipSwitch = content.ipswitch;
            if (content.status == "RUNNING" && ipSwitch != null && ipSwitch.status == "RUNNING") {
                labelLevel = 'label-success';
                if (content.ipSwitchAlertLevel == 'ERROR') {
                    labelLevel = 'label-danger';
                } else if (content.ipSwitchAlertLevel == 'WARNING') {
                    labelLevel = 'label-warning';
                }
            }
            return labelLevel;
        },
        'getLiveStateLevel': function (content) {
            var labelLevel = 'label-default';
            var liveTask = content.liveTask;
            if (content.status == "RUNNING" && liveTask != null && liveTask.liveTaskStatus == "RUNNING") {
                labelLevel = 'label-success';
                if (content.liveAlertLevel == 'ERROR') {
                    labelLevel = 'label-danger';
                } else if (content.liveAlertLevel == 'WARNING') {
                    labelLevel = 'label-warning';
                }
            }
            return labelLevel;
        },
        'positionFormat': function(posType) {
            return type[posType];
        }
    });
});