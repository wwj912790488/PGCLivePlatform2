/**
 * @author yxu
 */
$(function () {
    $('#cancel-button').click(function() {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index); //关闭
    });

    $('#save-button').on('click', function() {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }
        $('#add-user-form').bootstrapValidator('validate');
        var isValid = $('#add-user-form').data("bootstrapValidator").isValid();
        if (isValid) {
            var index = parent.layer.getFrameIndex(window.name);
            var url = "/user/password/change" ;
            $.ajax({
                url: url,
                type: 'POST',
                dataType: 'json',
                data: $('#add-user-form').serializeArray(),
                cache: false,
                success: function (data) {
                    parent.layer.close(index); //关闭
                    if (data.code === 0) {
                        parent.layer.alert("修改成功");
                    } else {
                        parent.layer.close(index); //关闭
                        parent.layer.alert(data.message);
                    }

                },
                error: function(e) {
                    parent.layer.close(index); //关闭
                    parent.layer.alert("修改密码失败");
                }
            });
        }
    });



    $('#add-user-form').bootstrapValidator({
        excluded: ':disabled',
        feedbackIcons: {
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            password:{
                message:'密码非法',
                validators:{
                    notEmpty:{
                        message:'密码不能为空'
                    },
                    stringLength:{
                        min:3,
                        max:9,
                        message:'密码长度必须位于3到9之间'
                    },
                    regexp:{
                        regexp:/^[a-zA-Z0-9_\.]+$/,
                        message:'密码不支持特殊字符'
                    }
                }
            },confirmPassword:{
                message:'密码非法',
                validators:{
                    notEmpty:{
                        message:'密码不能为空'
                    },
                    stringLength:{
                        min:3,
                        max:9,
                        message:'密码长度必须位于3到9之间'
                    },
                    identical:{
                        field:'password',
                        message:'两次密码输入不一致'
                    },
                    regexp:{
                        regexp:/^[a-zA-Z0-9_\.]+$/,
                        message:'密码不支持特殊字符'
                    }
                }
            }
        }
    });

});