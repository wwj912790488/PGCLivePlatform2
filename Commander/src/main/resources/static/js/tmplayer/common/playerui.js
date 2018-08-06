/**
 * Created by slw on 2018/3/28.
 */

var g_player = null;
function IP_OnBoxChanged(left, top, right, bottom) {
    g_player.OnBoxChanged(left, top, right, bottom);
}
function IP_OnTimeChanged(time) {
    g_player.OnTimeChanged(time);
}

function TMPlayer(param) {
    var PLAYER_WIDTH = 480;
    var PLAYER_HEIGHT = 480;

    this.dom = param.dom;
    this.domPlayer = null;
    this.inputDom = param.inputDom;

    this.localURI = null;
    this.inputType = null;
    this.frameRate = 25;
    this.program = {programId: -1, videoId: -1, audioId: -1, subtitleId: -1};
    this.inputSetting = {srcIp: "", allowProgramIdChange: "0"};
    this.programList = null;
    this.aspectRatio = {x: 0, y: 0};
    this.resolutionWidth = null;
    this.resolutionHeight = null;
    this.bDeviceStarted = false;
    this.timer = null;

    this.fnTimeChanged = param.fnTimeChanged;
    this.fnTrimRangeChanged = param.fnTrimRangeChanged;
    this.fnGetTrimInfo = param.fnGetTrimInfo;
    this.fnOnClose = param.fnOnClose;
    this.showTrimBox = false;
    g_player = this;

    this.InitPlayer = function () {
        if(this.domPlayer != null) return;
        this.domPlayer = Player_Init(this.dom);
        if(this.domPlayer != null) {
            uDomAddEvent(this.domPlayer, "TimePosition", IP_OnTimeChanged);
            uDomAddEvent(this.domPlayer, "SelBoxPosition", IP_OnBoxChanged);
        }
    }

    this.OnTimeChanged = function (time) {
        if ($.isFunction(this.fnTimeChanged)) {
            this.fnTimeChanged(time);
        }
    }

    this.OnBoxChanged = function (left, top, right, bottom) {
        var o = {};
        o.x = left;
        o.y = top;
        o.width = right - left;
        o.height = bottom - top;
        this.fnTrimRangeChanged(o);
    }

    this.BeginSelectBox = function () {
        if(this.domPlayer == null) return;
        var range = this.fnGetTrimInfo();
        if(range == null) {
            range = {x: 0, y: 0, width: 0, height: 0};
        }
        this.domPlayer.SetSelBoxPosition(range.x, range.y, range.x+range.width, range.y+range.height);
        this.domPlayer.SelectRectBox(1, 0xff00ff00);
        this.showTrimBox = true;
    }

    this.EndSelectBox = function() {
        if(this.domPlayer == null) return;
        this.domPlayer.SelectRectBox(0, 0xffffffff);
        this.showTrimBox = false;
    }

    this.Play = function () {
        this.InitPlayer();
        if(this.domPlayer == null) return;
        if (this.localURI == null) return;
        this.inputType = this.GetInputType();

        //if(this.inputType == INPUT_TYPE_SDI
        //    || this.inputType == INPUT_TYPE_SDI4K
        //    || this.inputType == INPUT_TYPE_CVBS
        //    || this.inputType == INPUT_TYPE_HDMI
        //    || this.inputType == INPUT_TYPE_ASI) {
        //
        //    this.keepAlive();
        //    this.StartDevicePreview();
        //}
        //else if(this.inputType == INPUT_TYPE_NETWORK
        //    && uGetProtocol(this.localURI) == "udp") {
        //    this.keepAlive();
        //    this.startUdpPreview();
        //}
        //else {
        //    var _url = URI2HttpURL(this.localURI);
        //    this.PlayURI(_url);
        //}

        var _url = URI2HttpURL(this.localURI);
        this.PlayURI(_url);
    }

    this.PlayURI = function (httpURI) {
        if(this.domPlayer == null) {
            return;
        }

        httpURI = string_trim(httpURI);

        if(this.programList == null) {
            Player_SelectTrack(this.domPlayer, this.localURI, this.program);
        } else {
            Player_AddCompose(this.domPlayer, this.localURI, this.programList);
        }

        var mediaType = Player_GetMediaType(this.inputType, this.localURI);
        var ret = this.domPlayer.LoadMedia(httpURI, "", mediaType);
        if(ret != 0) return;

        var aspectRatioX = 0;
        var aspectRatioY = 0;
        if(this.aspectRatio.x == 0 || this.aspectRatio.y == 0) {
            aspectRatioX = this.domPlayer.GetVideoDisplayWidth();
            aspectRatioY = this.domPlayer.GetVideoDisplayHeight();
        } else {
            aspectRatioX = this.aspectRatio.x;
            aspectRatioY = this.aspectRatio.y;
        }

        if(this.resolutionWidth == 0 || this.resolutionHeight == 0) {
            this.resolutionWidth = this.domPlayer.GetVideoWidth();
            this.resolutionHeight = this.domPlayer.GetVideoHeight();
        }

        if(aspectRatioX >= aspectRatioY) {
            this.domPlayer.width = PLAYER_WIDTH;
            this.domPlayer.height = PLAYER_WIDTH * aspectRatioY / aspectRatioX;
        } else {
            this.domPlayer.height = PLAYER_HEIGHT;
            this.domPlayer.width = PLAYER_HEIGHT * aspectRatioX / aspectRatioY;
        }

        if (ret == 0) {
            ret = this.domPlayer.Play();
        }
        return ret;
    }

    this.GetInputType = function() {
        var type = INPUT_TYPE_FILE;
        var file =  this.localURI;
        if ( 0 <= file.indexOf("://")) {
            type = INPUT_TYPE_NETWORK;
        }
        if (0 <= file.indexOf("sdi:/")){
            type = INPUT_TYPE_SDI;
        }
        return type;
    }

    /*o = {
     * uri: ,
     * size:,
     * video: {codec: , bitrate: , aspectRatio:, }
     * audio: {codec:, bitrate:, channel:, samplerate:, bitdepth:}
     * }*/
    this.SetMediaInfo = function(o) {
        this.ClearMediaInfo();

        if(o == null) return ;

        this.localURI = o.uri;
        this.inputType = o.inputType;


        if(o.video != null) {
            this.frameRate = parseFloat(o.video.framerate);
            if(isNaN(this.frameRate) || this.frameRate < 1) {
                this.frameRate = 25;
            }

            if(o.video.aspectRatio != null) {
                var aspectArr = o.video.aspectRatio.match(/\d+/g);
                value = parseInt(aspectArr[0]);
                if(isNaN(value)) value = 0;
                this.aspectRatio.x = value;
                value = parseInt(aspectArr[1]);
                if(isNaN(value)) value = 0;
                this.aspectRatio.y = value;
            }

            if(o.video.resolution != null) {
                var arr = o.video.resolution.match(/\d+/g);
                value = parseInt(arr[0]);
                if(isNaN(value)) value = 0;
                this.resolutionWidth = value;
                value = parseInt(arr[1]);
                if(isNaN(value)) value = 0;
                this.resolutionHeight = value;
            }
        }

        if(o.program != null) {
            this.program.programId = o.program.programId;
            this.program.videoId = o.program.videoId;
            this.program.audioId = o.program.audioId;
            this.program.subtitleId = o.program.subtitleId;
        }
    }

    this.ClearMediaInfo = function() {
        this.localURI = "";
        this.inputType = null;
        this.frameRate = 25;
        this.aspectRatio.x = 0;
        this.aspectRatio.y = 0;
        this.program.programId = -1;
        this.program.videoId = -1;
        this.program.audioId = -1;
        this.program.subtitleId = -1;
        this.resolutionWidth = 0;
        this.resolutionHeight = 0;
        this.programList = null;
    };

    this.SetProgramList = function(list) {
        this.programList = list;
    };

    /*
     * duration: +: forward, -: backward
     * */
    this.SeekFromCurrent = function(duration) {
        if(this.domPlayer == null) return;

        var state = this.domPlayer.GetPlayState();
        if(state == STATE_RUNNING) {
            this.domPlayer.Pause();
        } else if(state == STATE_PAUSED || state == STATE_STOPPED) {
        } else {
            return;
        }

        var accurate = 1;
        var flag = 2;
        var mode = accurate * 0x01000000 + flag;
        this.domPlayer.Seek(duration, mode);
    };

    this.StepFrame = function(frame) {
        if(this.domPlayer == null) return;

        if(frame == null) {
            return;
        } else {
            frame = parseInt(frame);
            if(isNaN(value)) return;
        }

        this.domPlayer.Step(frame);
    };

    /*
     * return {ms:, hour: , minute: , second: , millisecond: }
     */
    this.GetCurrentTimeObject = function() {
        if(this.domPlayer == null) return;
        var ms = this.domPlayer.GetPlaybackTime();
        if(ms < 0) ms = 0;
        var o = uMs2Hmsm(ms);
        o.ms = ms;
        var text = "{0}:{1}:{2}:{3}";
        o.text = text.format(o.hour, o.minute, o.second, o.millisecond);
        return o;
    };

    /* timeText: 'h:m:s:ms' */
    this.JumpByTimeText = function(timeText) {
        var o = uTimeText2Object(timeText);

        var accurate = 1;
        var flag = 1;
        var mode = accurate * 0x01000000 + flag;
        this.domPlayer.Pause();
        this.domPlayer.Seek(o.ms, mode);
    };

    this.Snapshot = function(path, name) {
        if(!ValidateFilePath(path)) return;

        if(path.charAt(path.length-1) != "\\") {
            path += "\\";
        }
        if(name == null || name.length == 0) {
            name = this.domPlayer.GetPlaybackTime();
            name = String(name);
        }
        if(!ValidateFileName(name)) return;

        var filePath = path + name + ".jpg";
        var type = 4;//jpeg
        var ret = this.domPlayer.SaveVideoSnapshot(filePath, type, 0, 0);
    }

    this.Close = function() {
        //TMPlayer operate.
        if(this.domPlayer != null) {
            this.domPlayer.close();
            this.RestorePlayer();
        }

        if(this.timer != null) {
            clearInterval(this.timer);
        }

        if($.isFunction(this.fnOnClose)) {
            this.fnOnClose();
        };
    };

    this.keepAlive = function() {
        this.timer = setInterval(function () {
            var _requestURL = "keepAlivePreview";
            $.ajax({
                url: _requestURL,
                type: 'POST',
                dataType: 'json',
                data: {},
                cache: false,
                success: function (data) {
                    var monitorUrl = ParseMonitorUrl(data);
                    if(monitorUrl != null) {
                        context.bDeviceStarted = true;
                    }
                    var _url = URI2HttpURL(monitorUrl);
                    context.PlayURI(_url);
                }
            });
        }, 15000);
    }

    this.StartDevicePreview = function() {
        var _requestURL = "startSDIPreview";
        if(this.inputType == INPUT_TYPE_SDI
            || this.inputType == INPUT_TYPE_SDI4K) {
            _requestURL = "startSDIPreview";
        }
        else if(this.inputType == INPUT_TYPE_CVBS) {
            _requestURL = "startCvbsPreview";
        }
        else if(this.inputType == INPUT_TYPE_HDMI) {
            _requestURL = "startHdmiPreview";
        }
        else if(this.inputType == INPUT_TYPE_ASI) {
            _requestURL = "startASIPreview";
        }

        $.ajax({
            url: _requestURL,
            type: 'POST',
            dataType: 'json',
            data: {"uri": this.localURI},
            cache: false,
            success: function (data) {
                var monitorUrl = ParseMonitorUrl(data);
                if(monitorUrl != null) {
                    context.bDeviceStarted = true;
                }

                var _url = URI2HttpURL(monitorUrl);
                context.PlayURI(_url);
            }
        });
    };

    this.startUdpPreview = function() {
        var _requestURL = "startUdpPreview";
        var _param = {"uri": this.localURI,
            "programId": this.program.programId,
            "audioId": this.program.audioId,
            "subtitleId":  this.program.subtitleId,
            "srcIp": this.inputSetting.srcIp,
            "allowProgramIdChange": this.inputSetting.allowProgramIdChange};
        $.ajax({
            url: _requestURL,
            type: 'POST',
            dataType: 'json',
            data: _param,
            cache: false,
            success: function (data) {
                var monitorUrl = ParseMonitorUrl(data);
                if(monitorUrl != null) {
                    context.bDeviceStarted = true;
                }

                var _url = URI2HttpURL(monitorUrl);
                context.PlayURI(_url);
            }
        });
    }

    this.stopPreview = function() {
        if(!this.bDeviceStarted) return;

        if(this.inputType == INPUT_TYPE_SDI
            || this.inputType == INPUT_TYPE_SDI4K
            || this.inputType == INPUT_TYPE_CVBS
            || this.inputType == INPUT_TYPE_HDMI
            || this.inputType == INPUT_TYPE_ASI) {
            this.StopDevicePreview();
        }
        else if(this.inputType == INPUT_TYPE_NETWORK
            && uGetProtocol(this.localURI) == "udp") {
            this.stopUdpPreview();
        }
    }
    
    this.StopDevicePreview = function () {
        var _requestURL = "stopSDIPreview";
        if(this.inputType == INPUT_TYPE_SDI
            || this.inputType == INPUT_TYPE_SDI4K) {
            _requestURL = "stopSDIPreview";
        }
        else if(this.inputType == INPUT_TYPE_CVBS) {
            _requestURL = "stopCvbsPreview";
        }
        else if(this.inputType == INPUT_TYPE_HDMI) {
            _requestURL = "stopHdmiPreview";
        }
        else if(this.inputType == INPUT_TYPE_ASI) {
            _requestURL = "stopASIPreview";
        }

        $.ajax({
            url: _requestURL,
            type: 'POST',
            dataType: 'json',
            data: {"uri": this.localURI},
            cache: false,
            success: function (data) {
                context.bDeviceStarted = false;
            }
        });
    }

    this.stopUdpPreview = function() {
        var _requestURL = "stopUdpPreview";
        var _param = {"uri": this.localURI,
            "programId": this.program.programId,
            "audioId": this.program.audioId,
            "subtitleId":  this.program.subtitleId,
            "srcIp": this.inputSetting.srcIp};

        $.ajax({
            url: _requestURL,
            type: 'POST',
            dataType: 'json',
            data: _param,
            cache: false,
            success: function (data) {
                context.bDeviceStarted = false;
            }
        });
    }

    this.RestorePlayer = function() {
        this.domPlayer.ChangeAspectRatio(0, 0, -100, 0);
        this.domPlayer.RemoveComposeTitle(-1);
        this.EndSelectBox();
    };

    this.UriChange = function() {
        this.localURI = this.inputDom.val().trim();
        this.Close();
        this.Play();
    }

    function ValidateFilePath(path) {
        if(path == null || path.length == 0) {
            return false;
        }
        if(path.match(/^\w:\\/gi) == null ||
            path.match(/\\\\/gi) != null) {
            return false;
        }
        var path2 = path.substring(2);
        if(path2.match(/[\/:*?"<>|]/gi) != null) {
            return false;
        }

        return true;
    }

    function ValidateFileName(fileName) {
        if(fileName == null || fileName.length == 0) {
            return false;
        }
        if(fileName.match(/[\\\/:*?"<>|]/gi) != null) {
            return false;
        }

        return true;
    }
}