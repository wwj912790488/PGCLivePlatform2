/**
 * Created by slw on 2018/3/22.
 */
$(function () {
    $('#cancel-button').click(function() {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index); //关闭
    });
    $('#add-material-form').submit(function () {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }
        var name = $("#name").val();
        var description = $("#description").val();
        $.ajaxFileUpload({
            url: $(this).attr("action")  + "?name=" + name+"&description="+description,
            secureuri: false,
            fileElementId: 'file',
            dataType: 'json',
            success: function (data, status) {
                if (data.code == 0) {
                    var index = parent.layer.getFrameIndex(window.name); //得到当前iframe层的索引
                    parent.layer.close(index); //关闭
                    parent.location.href = "/material/motionIcon";

                } else {
                    parent.layer.alert("失败 " + data.message);
                }
            },
            error: function (data, status, e) {
                parent.layer.alert("失败");
            }
        });
        return false;
    });

    $('#add-material-form').bootstrapValidator({
        feedbackIcons: {
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            name: {
                validators: {
                    notEmpty: {
                        message: '名称不能为空'
                    },stringLength:{
                        max:20,
                        message:'不能超过20个字.'
                    }
                }
            },
            file: {
                validators: {
                    notEmpty: {
                        message: '文件不能为空'
                    }
                }
            },
            description: {
                validators: {
                    stringLength:{
                        max:100,
                        message:'描述不能超过100字.'
                    }
                }
            }
        }
    });

});