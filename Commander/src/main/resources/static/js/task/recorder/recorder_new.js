/**
 * Created by slw on 2018/3/22.
 */
$(function () {
    $('#add-recorder-task-form').submit(function() {
        var _this = this;
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }

        var contentId = $(this).find("[name = contentId]").val();
        if (contentId == null) {
            return false;
        }

        var id = $(this).find('[name = id]').val();
        var url = id.length != 0 ? "/task/recorder/update" : "/task/recorder/add";
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            data: $(_this).serializeArray(),
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    window.location.href = '/task/recorder';
                } else {
                    bootbox.alert({
                        title: "新建收录失败",
                            message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "新建收录失败",
                    message: e
                });
            }
        });
        return false;
    });

    var startTimepicker =  $('#add-recorder-task-form').find('[name = startTime]');
    var endTimepicker =  $('#add-recorder-task-form').find('[name = endTime]');
    startTimepicker.datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        locale: 'zh-cn'
    });

    endTimepicker.datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        locale: 'zh-cn',
        useCurrent: false
    });

    startTimepicker.on("dp.change", function (e) {
        endTimepicker.data("DateTimePicker").minDate(e.date);
    });

    endTimepicker.on("dp.change", function (e) {
        startTimepicker.data("DateTimePicker").maxDate(e.date);
    });

    $('#add-recorder-task-form').find('[name = enableThumb]').on('change', function () {
        var _this = this;
        var thumbWidth = $('#add-recorder-task-form').find('[name = thumbWidth]');
        if ($(_this).prop('checked')) {
            thumbWidth.prop("disabled", false);
            thumbWidth.prop("required", true);
        } else {
            thumbWidth.prop("disabled", true);
            thumbWidth.prop("required", false);
        }
        $('#add-recorder-task-form').validator('destroy').validator();
    });

    $('#enableKeepTimes').on('change', function () {
        var _this = this;
        var KeepTimes = $('#add-recorder-task-form').find('[name = KeepTimes]');
        if ($(_this).prop('checked')) {
            KeepTimes.prop("disabled", false);
            KeepTimes.prop("required", true);
        } else {
            KeepTimes.prop("disabled", true);
            KeepTimes.prop("required", false);
        }
        $('#add-recorder-task-form').validator('destroy').validator();
    });

    $('#enableStartTime').on('change', function () {
        var _this = this;
        var startTime = $('#add-recorder-task-form').find('[name = startTime]');
        if ($(_this).prop('checked')) {
            startTime.prop("disabled", false);
            startTime.prop("required", true);
        } else {
            startTime.prop("disabled", true);
            startTime.prop("required", false);
        }
        $('#add-recorder-task-form').validator('destroy').validator();
    });

    $('#enableEndTime').on('change', function () {
        var _this = this;
        var endTime = $('#add-recorder-task-form').find('[name = endTime]');
        if ($(_this).prop('checked')) {
            endTime.prop("disabled", false);
            endTime.prop("required", true);
        } else {
            endTime.prop("disabled", true);
            endTime.prop("required", false);
        }
        $('#add-recorder-task-form').validator('destroy').validator();
    });

    $('#select-path-modal-div').handlebars($('#select_path_modal_template'));
    var outputPath = $('#add-recorder-task-form').find('[name = outputPath]');
    var folderSelectedCallback = function (path) {
        outputPath.val(path);
        outputPath.change();
    }
    initFolderSelector();
    $('#path-addon-btn').on('click', function () {
        handleFolderSelector(true, '/path/querycontentfolderlist', folderSelectedCallback, outputPath.val());
    });

});