/**
 * Created by slw on 2018/3/22.
 */
$(function () {
    $('#add-supervisor-task-form').submit(function() {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }
        var templateType = $("#templateType").val();
        var ids=[];
        $.each($("select[name='single-channel']"), function (index, obj) {
            if($(obj).val()!=null && $(obj).val()!=''){
                ids.push($(obj).val());
            }
        });
        if(ids.length<=0){
            return false;
        }
        $("#channelIds").val(ids.toString());

        $.ajax({
            url: $(this).attr("action"),
            type: 'POST',
            dataType: 'json',
            data: $(this).serializeArray(),
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    window.location.href = "/task/supervisor";
                } else {
                    bootbox.alert({
                        title: "新建任务失败",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "新建任务失败",
                    message: e
                });
            }
        });
        return false;
    });

    $("#templateType").change(function(){
        var templateType = $("#templateType").val();
        if(templateType==""||templateType==undefined){
            return;
        }
        var a = templateType.split("*");
        var line = a[0];
        var row = a[1];
        var width = 90/line;
        var num = line*row;
        var html ='<select class="form-control" name="single-channel" required="required" ' +
            'style="display: inline;width:'+width+'%"></select>';
        var div = $(".channel_select");
        div.html("");
        var label = '<label autofocus="autofocus" style="width: 100%">监看频道</label>';
        var innerHtml="";
        innerHtml+=label;
        for(var i=0;i<num;i++){
            innerHtml+=html;
        }
        div.html(innerHtml);

        $.ajax({
            url: "supervisorChannels",
            type: 'GET',
            dataType: 'json',
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    var result = data.data;
                    if (result!=null && result.length>0) {
                        $.each($("select[name='single-channel']"), function (index, obj) {
                            $(obj).empty();
                            for(var i=0;i<result.length;i++){
                                $(obj).append("<option value='"+result[i].id+"'>"+result[i].name+"</option>")
                            }
                            var channelIds = $("#channelIds").val();
                            var ids = channelIds.split(",");
                            if(channelIds!=undefined&&channelIds!=null) {
                                $(obj).val(ids[index]);
                            }
                        });
                    }
                    $('#add-supervisor-task-form').validator('destroy').validator();
                } else {
                    bootbox.alert({
                        title: "获取监看频道失败！",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "获取监看频道错误！",
                    message: e
                });
            }
        });
    });

    $("#templateType").trigger("change");
});