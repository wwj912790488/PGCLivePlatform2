$(function () {
    bootbox.setLocale("zh_CN");
    $("#cancel-button").click(function(){
        var index = parent.layer.getFrameIndex(window.name); //得到当前iframe层的索引
        parent.layer.close(index);
    });

    $(".select-item").on('click', function () {
        $(".select-item").prop('checked', false);
        $(this).prop('checked', true);
    });

    $("#save-button").click(function(){
        if($(".select-item:checked").length!=1){
            layer.alert("请选择一个活动");
            return;
        }
        var itemInfo = {
            posIdx:$("#posIdx").val(),
            screenId:$("#supervisorScreenId").val(),
            contentId:$(".select-item:checked").val(),
            outputType:$("#outputType").val(),
        };
        $.ajax({
            url: "/supervisor/itemSettings/save",
            type: 'POST',
            dataType: 'json',
            data: itemInfo,
            cache: false,
            success: function (data) {
                if (data.code != 0)  {
                    bootbox.alert({
                        title: "失败",
                        message: data.message
                    });
                }
                var index = parent.layer.getFrameIndex(window.name); //得到当前iframe层的索引
                parent.layer.close(index); //关闭
                parent.location.href = "/supervisor";
            },
            error: function(e) {
                bootbox.alert({
                    title: "ERROR",
                    message: e
                });
                var index = parent.layer.getFrameIndex(window.name); //得到当前iframe层的索引
                parent.layer.close(index); //关闭
                parent.location.href = "/supervisor";
            }
        });

    });
});