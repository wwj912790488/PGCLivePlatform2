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
            var id = $('#add-user-form').find('[name = id]').val();
            var url = id.length != 0 ? "/user/update" : "/user/save";
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
                if (data.code === 0) {
                    $('#role-form-id').empty();
                    $('#role-form-id').append("<option  disabled='disabled'>请选择角色</option>");
                    var roleList = data.data;
                    roleList.forEach(function(role) {
                        $('#role-form-id').append("<option value=" + role.id + ">" + role.roleName + "</option>");
                    });
                }
                $('#role-form-id').change();//触发一次值改变事件，使bootstrapValidator能够验证动态改变的值，同时bootstrapValidator中需要验证的字段需要加上 trigger:"change",
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
            name: {
                message: 'The username is not valid',
                validators: {
                    notEmpty: {
                        message: '登录名不能为空'
                    },
                    remote : {
                        url : '/user/checkName',
                        message : "该用户名已经存在！",
                        delay : 500,
                        type : 'post'
                    }
                }
            },
            companyId: {
                validators: {
                    notEmpty: {
                        message: '必须选择'
                    }
                }
            },
            /** roleId: {
                validators: {
                    notEmpty: {
                        message: '请选择角色',
                        trigger:"change"
                    }
                }
            }, **/
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
            },remarks:{
                validators:{
                    stringLength:{
                        max:100,
                        message:'最多只能输入100个字'
                    }
                }
            }
        }
    });

});