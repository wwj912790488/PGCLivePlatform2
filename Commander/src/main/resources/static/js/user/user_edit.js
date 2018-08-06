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
            var url = "/user/update";
            $.ajax({
                url: url,
                type: 'POST',
                dataType: 'json',
                data: $('#add-user-form').serializeArray(),
                cache: false,
                success: function (data) {
                    var index = parent.layer.getFrameIndex(window.name);

                    if (data.code == 0) {
                        //得到当前iframe层的索引
                        parent.layer.close(index); //关闭
                        parent.userTable.ajax.reload();
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


    $('#company-form-id').change(function(){
        var companyId = $(this).val();
        $.ajax({
            url: '/user/role/findRoles',
            type: 'POST',
            dataType: 'json',
            data: {companyId:companyId},
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    $('#role-form-id').empty();
                    $('#role-form-id').append("<option value=\"0\" disabled=\"disabled\"></option>");
                    var roleList = data.data;
                    roleList.forEach(function(role) {
                        $('#role-form-id').append("<option value=" + role.id + ">" + role.roleName + "</option>");
                    });
                }
            }
        });
    });



    $('#add-user-form').bootstrapValidator({
        excluded: ':disabled',
        feedbackIcons: {
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            companyId: {
                validators: {
                    notEmpty: {
                        message: '租户必须被选择'
                    }
                }
            },remarks:{
            validators:{
                stringLength:{
                    max:100,
                    message:'最多只能输入100个字'
                }
            }
        }
            /** roleId: {
                validators: {
                    notEmpty: {
                        message: '角色必须被选择'
                    }
                }
            } **/
        }
    });

});