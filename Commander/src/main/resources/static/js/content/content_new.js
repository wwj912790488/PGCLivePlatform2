/**
 * Created by slw on 2018/3/21.
 */
$(function () {
    var getSelectedIndexByPid = function (dataList, selectedPid, returnDefaultValue) {
        if (dataList != null && selectedPid != null && selectedPid.trim().length > 0) {
            var pid = parseInt(selectedPid);
            for (var i = 0; i < dataList.length; ++i) {
                if (dataList[i].pid == pid) {
                    returnDefaultValue = i;
                    break;
                }
            }
        }
        return returnDefaultValue;
    }

    $('#add-content-form').submit(function() {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }
        if($('.error_msg').html()!=""){
            $('#backup').focus();
            return false;
        }
        var id = $(this).find('[name = id]').val();
        var url = id.length != 0 ? "/content/update" : "/content/add";
        var formData = getFormDataData();
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            data: formData,
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    window.location.href = "/content";
                } else {
                    bootbox.alert({
                        title: "添加频道失败",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "添加频道失败",
                    message: e
                });
            }
        });
        return false;
    });

    var getFormDataData = function () {
        var formData = $('#add-content-form').serializeArray();
        $('.output-item').each(function(index, item) {
            var templateId = $(item).find("#output-templateId").val();
            var outputUri = $(item).find("#output-outputUri").val();
            if (templateId != null && templateId != 'undefined' && templateId != "" &&
                outputUri != null && outputUri != 'undefined' && outputUri != "") {
                formData.push({ name: "outputs[" + index + "].templateId", value: templateId });
                formData.push({ name: "outputs[" + index + "].outputUri", value: outputUri });
            }
        });

        $('.logo-item').each(function (index, item) {
            var posType = $(item).find("#logo-posType").val();
            var materialId = $(item).find("#logo-materialId").val();
            if (posType != null && posType != 'undefined' && posType != "" &&
                materialId != null && materialId != 'undefined' && materialId != "") {
                formData.push({ name: "logos[" + index + "].posType", value: posType });
                formData.push({ name: "logos[" + index + "].materialId", value: materialId });
            }
        });

        $('.icon-item').each(function (index, item) {
            var posType = $(item).find("#icon-posType").val();
            var materialId = $(item).find("#icon-materialId").val();
            if (posType != null && posType != 'undefined' && posType != "" &&
                materialId != null && materialId != 'undefined' && materialId != "") {
                formData.push({ name: "icons[" + index + "].posType", value: posType });
                formData.push({ name: "icons[" + index + "].materialId", value: materialId });
            }
        });

        return formData;
    }

    var bindRemoveButtonEvent = function () {
        $.each($(".output-item"), function (index, obj) {
            $(obj).find('.remove-button').on('click', function () {
                $(obj).remove();
                var outputLabels = $('.output-item').find('.output-label-index');
                $.each(outputLabels, function(index, ele) {
                    $(ele).html('输出设置 ' + (index + 1) + ':');
                });
            });
        });

        $.each($(".logo-item"), function (index, obj) {
            $(obj).find('.remove-button').on('click', function () {
                $(obj).remove();
            });
        });

        $.each($(".icon-item"), function (index, obj) {
            $(obj).find('.remove-button').on('click', function () {
                $(obj).remove();
            });
        });
    }

    bindRemoveButtonEvent();

    $('#add-output-button').on('click', function() {
        var outputGroup = $('#output-group');
        var count = outputGroup.find(".output-item").length;
        if (count >= 4) {
            return;
        }
        var lastOutputItem = outputGroup.find(".output-item:last");
        var outputItem = $("<div class='output-item'/>");
        var data = {};
        data.index = count;
        data.count = count + 1;
        outputItem.handlebars($("#content_output_item_template"), data);
        if (lastOutputItem.length > 0) {
            lastOutputItem.after(outputItem);
        } else {
            outputGroup.append(outputItem);
        }

        outputItem.find('.remove-button').on('click', function() {
            outputItem.remove();
            var outputLabels = $('.output-item').find('.output-label-index');
            $.each(outputLabels, function(index, ele) {
                $(ele).html('输出设置 ' + (index + 1) + ':');
            });
            $('#add-content-form').validator('destroy').validator();
        })

        outputItem.find('.protocol-select').on('change', function() {
            var outputUri = $(this).closest(".input-group").find("#output-outputUri");
            updateProtocolSelectUI($(this), outputUri);
        });

        $('#add-content-form').validator('destroy').validator();
    });

    $("#add-logo-button").on('click', function() {
        var logoGroup = $("#logo-group");
        var count = logoGroup.find('.logo-item').length;
        if (count >= 2) {
            return;
        }

        var lastLogoItem = logoGroup.find(".logo-item:last");
        var logoItem = $("<div class='logo-item'/>");
        var data = {};
        data.index = count;
        data.count = count + 1;
        logoItem.handlebars($("#content_logo_item_template"), data);
        if (lastLogoItem.length > 0) {
            lastLogoItem.after(logoItem);
        } else {
            logoGroup.append(logoItem);
        }

        logoItem.find('.remove-button').on('click', function () {
           logoItem.remove();
            $('#add-content-form').validator('destroy').validator();
        });

        logoItem.find(".logo-pos-select").on('change', function() {
            var _this = this;
            var logoMaterialSelect = $(_this).closest(".form-group").find(".logo-material-select");
            updateMaterialSelectUI($(_this), logoMaterialSelect);
        });

        $('#add-content-form').validator('destroy').validator();
    });

    $("#add-icon-button").on('click', function() {
        var iconGroup = $("#icon-group");
        var count = iconGroup.find('.icon-item').length;
        if (count >= 2) {
            return;
        }

        var lastIconItem = iconGroup.find(".icon-item:last");
        var iconItem = $("<div class='icon-item'/>");
        var data = {};
        data.index = count;
        data.count = count + 1;
        iconItem.handlebars($("#content_icon_item_template"), data);
        if (lastIconItem.length > 0) {
            lastIconItem.after(iconItem);
        } else {
            iconGroup.append(iconItem);
        }

        iconItem.find('.remove-button').on('click', function () {
            iconItem.remove();
            $('#add-content-form').validator('destroy').validator();
        });

        iconItem.find(".icon-pos-select").on('change', function() {
            var _this = this;
            var logoMaterialSelect = $(_this).closest(".form-group").find(".icon-material-select");
            updateMaterialSelectUI($(_this), logoMaterialSelect);
        });

        $('#add-content-form').validator('destroy').validator();
    });

    $('.protocol-select').on('change', function() {
        var outputUri = $(this).closest(".input-group").find("#output-outputUri");
        updateProtocolSelectUI($(this), outputUri);
    });

    var updateProtocolSelectUI = function (protocol, outputUri) {
        if (protocol.val() == "hls") {
            outputUri.prop('placeholder', 'http://www.arcvideo.com/index.m3u8');
            outputUri.prop('pattern', '^http://.*');
        } else if (protocol.val() == "rtmp") {
            outputUri.prop('placeholder', 'rtmp://www.arcvideo.com/live/test');
            outputUri.prop('pattern', '^rtmp://.*');
        } else if (protocol.val() == 'udp') {
            outputUri.prop('placeholder', 'udp://239.10.10.10:1234');
            outputUri.prop('pattern', '^udp://.*');
        }
        $('#add-content-form').validator('destroy').validator();
    };

    $('.logo-pos-select').on('change', function() {
        var _this = this;
        var logoMaterialSelect = $(_this).closest(".form-group").find(".logo-material-select");
        updateMaterialSelectUI($(_this), logoMaterialSelect);
    });

    $('.icon-pos-select').on('change', function() {
        var _this = this;
        var logoMaterialSelect = $(_this).closest(".form-group").find(".icon-material-select");
        updateMaterialSelectUI($(_this), logoMaterialSelect);
    });

    var updateMaterialSelectUI = function(posSelect, materialSelect) {
        if (posSelect.val().length != 0) {
            materialSelect.prop("disabled", false);
            materialSelect.prop("required", true);
        } else {
            materialSelect.prop("disabled", true);
            materialSelect.prop("required", false);
        }
        $('#add-content-form').validator('destroy').validator();
    }

    $('#add-content-form').find('[name = "master.streamType"]').on('change', function() {
        var _this = this;
        var sourceUri = $('#add-content-form').find('[name = "master.sourceUri"]');
        updateSourceUriUI($(_this), sourceUri);

        var programId = $('#add-content-form').find('[name = "master.programId"]');
        var audioId = $('#add-content-form').find('[name = "master.audioId"]');
        var subtitleId = $('#add-content-form').find('[name = "master.subtitleId"]');
        updateMediaParamsUI($(_this), sourceUri, programId, audioId, subtitleId);

        $('#add-content-form').validator('destroy').validator();
    });

    $('#add-content-form').find('[name = "master.sourceUri"]').on('change', function () {
        var _this = this;
        var streamType = $('#add-content-form').find('[name = "master.streamType"]');
        var path =  $(_this).val();
        if (streamType.val() != 3) {
            return;
        }

        var programDom = $('#add-content-form').find('[name = "master.programId"]');
        var audioDom = $('#add-content-form').find('[name = "master.audioId"]');
        var subtitleDom = $('#add-content-form').find('[name = "master.subtitleId"]');

        var programId = $('#master-program-id');
        getMediaInfo($(_this), programDom, audioDom, subtitleDom, path, programId);
    });

    $('#add-content-form').find('[name = "slave.sourceUri"]').on('change', function () {
        var _this = this;
        var streamType = $('#add-content-form').find('[name = "slave.streamType"]');
        var path =  $(_this).val();
        if (streamType.val() != 3) {
            return;
        }

        var programDom = $('#add-content-form').find('[name = "slave.programId"]');
        var audioDom = $('#add-content-form').find('[name = "slave.audioId"]');
        var subtitleDom = $('#add-content-form').find('[name = "slave.subtitleId"]');

        var programId = $('#slave-program-id');
        getMediaInfo($(_this), programDom, audioDom, subtitleDom, path, programId);
    });

    $('#add-content-form').find('[name = "backup"]').on('change', function () {
        var _this = this;
        var path =  $(_this).val();
        getBackupMediaInfo($(_this), path);
    });

    $('#add-content-form').find('[name = "master.programId"]').on('change', function () {
        var _this = this;
        var mediaInfo = $(_this).data("mediainfo");
        if (mediaInfo != null) {
            var selectedIndex = getSelectedIndexByPid(mediaInfo.programs, $(_this).val(), 0);
            var selectedProgram = mediaInfo.programs[selectedIndex];

            var audioDom = $('#add-content-form').find('[name = "master.audioId"]');
            var audioId = $('#master-audio-id');

            var subtitleDom = $('#add-content-form').find('[name = "master.subtitleId"]');
            var subtitleId = $('#master-subtitle-id');
            updateAudioSelect(audioDom, selectedProgram.audios, audioId);
            updateSubtitleSelect(subtitleDom, selectedProgram.subtitles, subtitleId)
        }
    });

    $('#add-content-form').find('[name = "slave.programId"]').on('change', function () {
        var _this = this;
        var mediaInfo = $(_this).data("mediainfo");
        if (mediaInfo != null) {
            var selectedIndex = getSelectedIndexByPid(mediaInfo.programs, $(_this).val(), 0);
            var selectedProgram = mediaInfo.programs[selectedIndex];

            var audioDom = $('#add-content-form').find('[name = "slave.audioId"]');
            var audioId = $('#slave-audio-id');

            var subtitleDom = $('#add-content-form').find('[name = "slave.subtitleId"]');
            var subtitleId = $('#slave-subtitle-id');

            updateAudioSelect(audioDom, selectedProgram.audios, audioId);
            updateSubtitleSelect(subtitleDom, selectedProgram.subtitles, subtitleId)
        }
    });

    var getBackupMediaInfo = function (dom, path) {
        var waitimg = dom.closest('.form-group').find('.waitimg');
        waitimg.removeClass('hidden');
        dom.closest('.form-group').find('.error_msg').html("");
        $.ajax({
            url: "/api/getmediainfo?path=" + path,
            type: 'POST',
            cache: false,
            success: function (data) {
                waitimg.addClass('hidden');
                if (data.code == 0 && data.data != null && data.data.programs.length>1) {
                    dom.closest('.form-group').find('.error_msg').html("不能选择复合流!");
                }
            },
            error: function(e) {
                waitimg.addClass('hidden');
            }
        }).always(function () {
            waitimg.addClass('hidden');
        });
    }

    var getMediaInfo = function (dom, programIdDom, audioIdDom, subtitleIdDom, path, programId) {
        var waitimg = dom.closest('.form-group').find('.waitimg');
        waitimg.removeClass('hidden');
        $.ajax({
            url: "/api/getmediainfo?path=" + path,
            type: 'POST',
            cache: false,
            success: function (data) {
                waitimg.addClass('hidden');
                if (data.data != null) {
                    programIdDom.data("mediainfo", data.data);
                    updateProgramSelect(programIdDom, programIdDom.data("mediainfo").programs, programId);
                }
                else {
                    programIdDom.removeData("mediainfo");
                    programIdDom.empty();
                    audioIdDom.empty();
                    subtitleIdDom.empty();
                }
                $('#add-content-form').validator('destroy').validator();
            },
            error: function(e) {
                waitimg.addClass('hidden');
            }
        }).always(function () {
            waitimg.addClass('hidden');
        });
    }

    var updateProgramSelect = function (dom, programs, programId) {
        dom.empty("");
        if (programs != null) {
            $.each(programs, function (index, program) {
                dom.append('<option value='+ program.pid +'>' + program.name + '</option>')
            });
            if (programId.val() != -1) {
                dom.val(programId.val());
                programId.val(-1);
            }
        }
        dom.change();
    };

    var updateAudioSelect = function(dom, audios, audioId) {
        dom.empty("");
        if (audios != null) {
            $.each(audios, function (index, audio) {
                dom.append('<option value='+ audio.pid +'>' + audio.name + '</option>')
            });
            if (audioId.val() != -1) {
                dom.val(audioId.val());
                audioId.val(-1);
            }
        }
    }

    var updateSubtitleSelect = function (dom, subtitles, subtitleId) {
        dom.empty("");
        if (subtitles != null) {
            $.each(subtitles, function (index, subtitles) {
                dom.append('<option value='+ subtitles.pid +'>' + subtitles.name + '</option>')
            });
            if (subtitleId.val() != -1) {
                dom.val(subtitleId.val());
                subtitleId.val(-1);
            }
        }
    }

    $('#add-content-form').find('[name = "slave.streamType"]').on('change', function() {
        var _this = this;
        var sourceUri = $('#add-content-form').find('[name = "slave.sourceUri"]');
        updateSourceUriUI($(_this), sourceUri);

        var programId = $('#add-content-form').find('[name = "slave.programId"]');
        var audioId = $('#add-content-form').find('[name = "slave.audioId"]');
        var subtitleId = $('#add-content-form').find('[name = "slave.subtitleId"]');
        updateMediaParamsUI($(_this), sourceUri, programId, audioId, subtitleId);
        $('#add-content-form').validator('destroy').validator();
    });

    //$('#add-content-form').find('[name = "backup.streamType"]').on('change', function() {
    //    var _this = this;
    //    var sourceUri = $('#add-content-form').find('[name = "backup.sourceUri"]');
    //    updateSourceUriUI($(_this), sourceUri);
    //
    //    $('#add-content-form').validator('destroy').validator();
    //});

    $("#masterEnableDelay").on('change', function() {
        var _this = this;
        var duration = $('#add-content-form').find('[name = "master.duration"]');
        updateEnableDelayUI($(_this), duration);
        $('#add-content-form').validator('destroy').validator();
    });

    $("#slaveEnableDelay").on('change', function() {
        var _this = this;
        var duration = $('#add-content-form').find('[name = "slave.duration"]');
        updateEnableDelayUI($(_this), duration);
        $('#add-content-form').validator('destroy').validator();
    });

    //$('#add-content-form').find('[name = "backup.enableDelay"]').on('change', function() {
    //    var _this = this;
    //    var duration = $('#add-content-form').find('[name = "backup.duration"]');
    //    updateEnableDelayUI($(_this), duration);
    //    $('#add-content-form').validator('destroy').validator();
    //});

    $('#add-content-form').find('[name = "enableSlave"]').on('change', function() {
        var _this = this;
        var streamType = $('#add-content-form').find('[name = "slave.streamType"]');
        var sourceUri = $('#add-content-form').find('[name = "slave.sourceUri"]');
        var enableDelay = $('#slaveEnableDelay');
        var duration =  $('#add-content-form').find('[name = "slave.duration"]');
        var programId = $('#add-content-form').find('[name = "slave.programId"]');
        var audioId = $('#add-content-form').find('[name = "slave.audioId"]');
        var subtitleId = $('#add-content-form').find('[name = "slave.subtitleId"]');
        var id = $('#add-content-form').find('[name = "slave.id"]');
        var slavePanel = $('#slave-panel');
        updatePanelUI($(_this), streamType, sourceUri, enableDelay, duration, programId, audioId, subtitleId, id, slavePanel);
        $('#add-content-form').validator('destroy').validator();
    });

    $('#add-content-form').find('[name = "enableBackup"]').on('change', function() {
        var _this = this;
        var backup = $('#add-content-form').find('[name = "backup"]');
        var streamType = $('#backupStreamType');
        var backPanel = $('#back-panel');
        if ($(_this).prop('checked')) {
            backPanel.show();
            backup.prop("disabled", false);
            backup.prop("required", true);
            backup.prop('pattern', '^udp://.*');
            streamType.prop("disabled", false);
        }
        else {
            backPanel.hide();
            backup.prop("disabled", true);
            backup.prop("required", false);
            backup.removeAttr('pattern');
            streamType.prop("disabled", true);
        }

        $('#add-content-form').validator('destroy').validator();
    });

    $('#add-content-form').find('[name = "enableIpSwitch"]').on('change', function () {
        var _this = this;
        var slaveCheck = $('#add-content-form').find('[name = "enableSlave"]');
        var slaveBackup = $('#add-content-form').find('[name = "enableBackup"]');
        if ($(_this).prop('checked')) {
            $('#ip_switch_area').show();
        }
        else {
            $('#ip_switch_area').hide();
            slaveCheck.prop('checked', false);
            slaveCheck.change();
            slaveBackup.prop('checked', false);
            slaveBackup.change();
        }
    });

    var startTimepicker =  $('#add-content-form').find('[name = startTime]');
    var endTimepicker =  $('#add-content-form').find('[name = endTime]');
    startTimepicker.datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        locale: 'zh-cn'
    });

    endTimepicker.datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        locale: 'zh-cn'
    });

    startTimepicker.on("dp.change", function (e) {
        endTimepicker.data("DateTimePicker").minDate(e.date);
    });

    endTimepicker.on("dp.change", function (e) {
        startTimepicker.data("DateTimePicker").maxDate(e.date);
    });

    var updateSourceUriUI = function (streamType, sourceUri) {
        sourceUri.val(null);
        if ($(streamType).val() == 1) {
            sourceUri.prop("disabled", true);
            sourceUri.prop("required", false);
        } else {
            sourceUri.prop("disabled", false);
            sourceUri.prop("required", true);
        }

        updateUriPattern(streamType, sourceUri);
    }

    var updateUriPattern = function (streamType, sourceUri) {
        if ($(streamType).val() == 2) {
            sourceUri.prop('pattern', '^rtmp://.*');
        } else if ($(streamType).val() == 3) {
            sourceUri.prop('pattern', '^udp://.*');
        } else {
            sourceUri.removeAttr('pattern');
        }
    }

    var updateMediaParamsUI = function (streamType, sourceUri, programId, audioId, subtitleId) {
        if ($(streamType).val() == 3) {
            programId.closest('.form-group').show();
            audioId.closest('.form-group').show();
            subtitleId.closest('.form-group').show();
            programId.prop("required", true);
            programId.prop("disabled", false);
            audioId.prop("required", true);
            audioId.prop("disabled", false);
            subtitleId.prop("disabled", false);

            sourceUri.change();
        } else {
            programId.closest('.form-group').hide();
            audioId.closest('.form-group').hide();
            subtitleId.closest('.form-group').hide();
            programId.prop("required", false);
            programId.prop("disabled", true);
            programId.empty();
            audioId.prop("required", false);
            audioId.prop("disabled", true);
            audioId.empty();
            subtitleId.prop("disabled", true);
            subtitleId.empty();
        }
    }

    var updateEnableDelayUI = function (enableDelay, duration) {
        if (enableDelay.prop('checked')) {
            duration.prop("disabled", false);
            duration.prop("required", true);
            if (duration.val().length == 0) {
                duration.val(5);
            }
        } else {
            duration.prop("disabled", true);
            duration.prop("required", false);
        }
    }

    var updatePanelUI = function(checkedDom, streamType, sourceUri, enableDelay, duration, programId, audioId, subtitleId, id, divPanel) {
        if ($(checkedDom).prop('checked')) {
            divPanel.show();
            streamType.prop("disabled", false);
            enableDelay.prop("disabled", false);
            id.prop("disabled", false);
            updateSourceUriUI(streamType, sourceUri);
            updateEnableDelayUI(enableDelay, duration);
            updateMediaParamsUI(streamType, sourceUri, programId, audioId, subtitleId);
        } else {
            divPanel.hide();
            streamType.prop("disabled", true);
            sourceUri.prop("disabled", true);
            sourceUri.prop("required", false);
            enableDelay.prop("disabled", true);
            duration.prop("disabled", true);
            duration.prop("required", false);
            programId.prop("disabled", true);
            programId.prop("required", false);
            audioId.prop("disabled", true);
            audioId.prop("required", false);
            subtitleId.prop("disabled", true);
            id.prop("disabled", true);
            id.prop("required", false);
        }
    }

    var masterStreamType = $('#add-content-form').find('[name = "master.streamType"]');
    var masterSourceUri = $('#add-content-form').find('[name = "master.sourceUri"]');
    if (masterStreamType.val() == 3 && masterSourceUri.val().length > 0) {
        var programId = $('#add-content-form').find('[name = "master.programId"]');
        var audioId = $('#add-content-form').find('[name = "master.audioId"]');
        var subtitleId = $('#add-content-form').find('[name = "master.subtitleId"]');
        updateMediaParamsUI(masterStreamType, masterSourceUri, programId, audioId, subtitleId);
    }
    updateUriPattern(masterStreamType, masterSourceUri);

    var slaveStreamType = $('#add-content-form').find('[name = "slave.streamType"]');
    var slaveSourceUri = $('#add-content-form').find('[name = "slave.sourceUri"]');
    if (slaveStreamType.val() == 3 && slaveSourceUri.val().length > 0) {
        var programId = $('#add-content-form').find('[name = "slave.programId"]');
        var audioId = $('#add-content-form').find('[name = "slave.audioId"]');
        var subtitleId = $('#add-content-form').find('[name = "slave.subtitleId"]');
        updateMediaParamsUI(slaveStreamType, slaveSourceUri, programId, audioId, subtitleId);
    }
    updateUriPattern(slaveStreamType, slaveSourceUri);
});