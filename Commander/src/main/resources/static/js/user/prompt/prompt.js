$(function () {
    bootbox.alert({
        message:"您当前没有权限,请管理员分配,或者点击右上角成为超管!",
        buttons: {
            ok:{label:'确认',className:'btn-primary'}
        }
    });
});