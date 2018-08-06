/**
 * Created by slw on 2018/3/22.
 */
$(function () {
    $('#add-live-task-form').submit(function() {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }

        var urls=[];
        $.each($("input[name='single-output']"), function (index, obj) {
            if($(obj).val()!=null && $(obj).val()!=''){
                urls.push($(obj).val());
            }
        });
        if(urls.length<=0){
            return false;
        }
        $("#outputUri").val(urls.toString());

        $.ajax({
            url: $(this).attr("action"),
            type: 'POST',
            dataType: 'json',
            data: $(this).serializeArray(),
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    window.location.href = "/task/live";
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

    $("#templateId").change(function(){
        var templateId = $("#templateId").val();
        var liveAddress = $("#liveAddress").val();
        if(templateId==""||templateId==undefined){
            return;
        }
        var div = $(".output-group");
        div.html("");

        $.ajax({
            url: "/task/live/liveProfile",
            type: 'GET',
            data:{templateId:templateId},
            dataType: 'json',
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    var result = data.data;
                    var count = result.outputNum;
                    var types = result.outputGroupTypes;
                    if (count>0) {
                        var innerHtml="";
                        for(var i=1;i<=count;i++){
                            var label = '<label autofocus="autofocus" style="width: 100%">输出'+i+'</label>';
                            var html;
                            if(types[i-1] == "httpstreaming"){
                                html =  '<div class="input-group"><span class="input-group-addon">'+liveAddress+'/ts/</span><input class="form-control single-output" name="single-output" required="required"></div>';
                            }else{
                                html ='<input class="form-control single-output" name="single-output" required="required">';
                            }

                            innerHtml = innerHtml+label+html;
                        }
                        div.html(innerHtml);
                        $.each($("input[name='single-output']"), function (index, obj) {
                            $(obj).empty();
                            var outputUri = $("#outputUri").val();
                            var urls = outputUri.split(",");
                            if(urls!=undefined && urls!=null && urls!="") {
                                $(obj).val(urls[index]);
                            }else {
                                if(types[index] == "flashstreaming"){
                                    $(obj).attr("placeholder","rtmp://127.0.0.1:1234/rtmp/test");
                                }else if(types[index] == "udpstreaming"){
                                    $(obj).attr("placeholder","udp://224.1.1.1:1234");
                                }else if(types[index] == "httpstreaming"){
                                    $(obj).attr("placeholder","xxx");
                                }else if(types[index] == "applestreaming"){
                                    $(obj).attr("placeholder","http://127.0.0.1:1234/hls/index.m3u8");
                                }
                            }
                        });
                        $('#add-live-task-form').validator('destroy').validator();
                    }
                } else {
                    //parent.layer.alert("获取频道失败 "+data.message);
                    bootbox.alert({
                        title: "获取模板失败",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                //parent.layer.alert("获取频道失败");
                bootbox.alert({
                    title: "获取模板错误",
                    message: "error to get profile message."
                });
            }
        });
    });

    $("#templateId").trigger("change");
});