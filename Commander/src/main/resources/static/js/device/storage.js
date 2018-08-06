/**
 * Created by slw on 2018/3/22.
 */
$(function () {
    $('#add-storage-form').submit(function() {
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
                var index = parent.layer.getFrameIndex(window.name);
                if (data.code == 0) {
                     //得到当前iframe层的索引
                    parent.layer.close(index); //关闭
                    //parent.location.href = "device";
                    parent.storageTable.ajax.reload();
                } else {
                    parent.layer.alert("新增存储失败:"+data.message);
                    parent.layer.close(index);
                }
            },
            error: function(e) {
                parent.layer.alert("新增存储失败!");
            }
        });
        return false;
    });
});