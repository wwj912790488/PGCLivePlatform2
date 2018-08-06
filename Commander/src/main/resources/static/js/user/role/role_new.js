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
        $('#add-role-form').bootstrapValidator('validate');
        var isValid = $('#add-role-form').data("bootstrapValidator").isValid();
        if (isValid) {
            var id = $('#add-role-form').find('[name = id]').val();
            var url = id.length != 0 ? "/user/role/update" : "/user/role/save";
            $.ajax({
                url: url,
                type: 'POST',
                dataType: 'json',
                data: $('#add-role-form').serializeArray(),
                cache: false,
                success: function (data) {
                    var index = parent.layer.getFrameIndex(window.name);
                    if (data.code == 0) {
                        //得到当前iframe层的索引
                        parent.layer.close(index); //关闭
                        parent.roleTable.ajax.reload();
                    } else {
                        parent.layer.alert("添加角色失败:"+data.message);
                        parent.layer.close(index);
                    }
                },
                error: function(e) {
                    layer.alert(e);
                }
            });
        }
    });

    $('#add-role-form').bootstrapValidator({
        feedbackIcons: {
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            roleName: {
                validators: {
                    notEmpty: {
                        message: '角色名称不能为空'
                    },stringLength:{
                        max:10,
                        message:'不能超过10个字.'
                    }
                }
            },
            remarks: {
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