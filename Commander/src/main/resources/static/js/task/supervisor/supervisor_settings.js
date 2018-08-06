/**
 * Created by slw on 2018/3/22.
 */
$(function () {
    $('#screen-settings-form').submit(function() {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }
        $.ajax({
            url: $(this).attr("action"),
            type: 'POST',
            dataType: 'json',
            data: $(this).serializeArray(),
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    var index = parent.layer.getFrameIndex(window.name); //得到当前iframe层的索引
                    parent.layer.close(index); //关闭
                    parent.location.href = "/supervisor";
                } else {
                    bootbox.alert({
                        title: "失败",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "失败",
                    message: e
                });
                var index = parent.layer.getFrameIndex(window.name); //得到当前iframe层的索引
                parent.layer.close(index); //关闭
                parent.location.href = "/supervisor";
            }
        });
        return false;
    });

    $("#cancel-button").click(function(){
        var index = parent.layer.getFrameIndex(window.name); //得到当前iframe层的索引
        parent.layer.close(index); //关闭
    });

    $("#outputType").change(function(){
        if($("#outputType").val() == "IP"){
            $(".output-path-group").show();
        }else {
            $(".output-path-group").hide();
        }
    });

    $("#outputType").trigger("change");
});