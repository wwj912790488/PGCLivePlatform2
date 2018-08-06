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
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
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
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                parent.location.href = "/supervisor";
            }
        });
        return false;
    });

    $("#cancel-button").click(function(){
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index);
    });

    $("#outputType").change(function(){
        if($("#outputType").val() == "UDP"){
            $(".output-path-group").show();
            $(".ops-group").hide();
            $("#opsId").attr("required",false);
            $("#opsId").attr("disabled",true);
        }else {
            $(".output-path-group").hide();
            $(".ops-group").show();
            $("#opsId").attr("disabled",false);
            $("#opsId").attr("required",true);
        }
    });

    $("#outputType").trigger("change");

    if($("#isBind")){

    }
});