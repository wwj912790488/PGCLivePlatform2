$(function () {
    $('#vlan-setting-form').submit(function() {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }
        var _this = this;
        $.ajax({
            url: $(_this).attr("action"),
            type: 'POST',
            dataType: 'json',
            data: $(_this).serializeArray(),
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index); //关闭
                    parent.vlanTable.ajax.reload();
                } else {
                    parent.layer.alert("添加失败:" + data.message);
                }
            },
            error: function(e) {
                parent.layer.alert("添加失败:" + e);
            }
        });
        return false;
    });

    $('#cancel-button').click(function() {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.close(index); //关闭
    });
});