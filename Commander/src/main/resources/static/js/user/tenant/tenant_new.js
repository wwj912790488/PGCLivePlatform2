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
        $('#add-tenant-form').bootstrapValidator('validate');
        var isValid = $('#add-tenant-form').data("bootstrapValidator").isValid();
        if (isValid) {
            var id = $('#add-tenant-form').find('[name = id]').val();
            var url = id.length != 0 ? "/user/tenant/update" : "/user/tenant/save";
            $.ajax({
                url: url,
                type: 'POST',
                dataType: 'json',
                data: $('#add-tenant-form').serializeArray(),
                cache: false,
                success: function (data) {
                    var index = parent.layer.getFrameIndex(window.name);

                    if (data.code == 0) {
                        //得到当前iframe层的索引
                        parent.layer.close(index); //关闭
                        parent.tenantTable.ajax.reload();
                    } else {
                        $('#alert-form-id').alert(data.message);
                    }
                },
                error: function(e) {
                    layer.alert(e);
                }
            });
        }
    });



    $('#add-tenant-form').bootstrapValidator({
        feedbackIcons: {
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            companyName: {
                validators: {
                    notEmpty: {
                        message: '名称不能为空'
                    },stringLength:{
                        max:10,
                        message:'不能超过10个汉字.'
                    },remote : {
                        url : '/user/tenant/checkName',
                        message : "名称已经存在！",
                        delay : 500,
                        type : 'post'
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