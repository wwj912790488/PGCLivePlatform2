/**
 * Created by slw on 2018/3/20.
 */

$(document).ready(function () {
    var handleDatatable = function(dataTable, columns, urlList, fnCreatedRow) {
        var dataTablesLanguage = {
            "sLengthMenu": "每页显示 _MENU_ 条记录",
            "sZeroRecords": "没有检索到数据",
            "sInfo": "当前数据为从第 _START_ 到第 _END_ 条数据；总共有 _TOTAL_ 条记录",
            "sInfoFiltered": "(从 _MAX_ 条记录中搜索)",
            "sInfoEmpty": "",
            "sProcessing": "正在加载数据...",
            "sSearch": "搜索",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "前页",
                "sNext": "后页",
                "sLast": "尾页"
            }
        };

        var tableClient = dataTable.DataTable({
            "bFilter": false,
            "bInfo": true,
            "bSort": false,
            "bStateSave": false,
            "bPaginate": true,
            "bAutoWidth": false,
            "sPaginationType": "full_numbers",
            "oLanguage": dataTablesLanguage,
            "bServerSide": true,
            "bProcessing": false,
            "bDestroy": false,
            "sAjaxSource": urlList.queryUrl,
            "aoColumns": columns,
            "fnServerData": function(sSource, aoData, fnCallback) {
                $.ajax({
                    "dataType": 'json',
                    "type": "POST",
                    "url": sSource,
                    "data": aoData,
                    "success": function(data) {
                        fnCallback(data.data);
                    },
                    "timeout": 30000
                });
            },
            "fnServerParams" : function(aoData) {
            },
            "columnDefs": [
                {
                    "defaultContent": '',
                    "targets": ['_all']
                },
                {
                    'orderable': false,
                    "targets": ['_all']
                }
            ],
            "createdRow": fnCreatedRow
        });

        return tableClient;
    };

    var fnCreatedRow = function( row, data, dataIndex ) {
        if(data!=null && data.mounted){
            $(row).find("button.mount-button").hide();
        }else{
            $(row).find("button.umount-button").hide();
        }
        $(row).find("button.mount-button").on('click', function() {
            $.ajax({
                url: "settings/storage/mount",
                type: 'GET',
                data:{id:data.id},
                cache: false,
                success: function (data) {
                    if (data.code == 0) {
                        tableClient.ajax.reload();
                    } else {
                        parent.layer.alert("挂载存储失败:"+data.message);
                    }
                },
                error: function(e) {
                    parent.layer.alert("挂载存储失败!");
                }
            });
        });

        $(row).find("button.edit-button").on('click', function () {
            layer.open({
                type: 2,
                title: '编辑存储',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['520px', '550px'],
                content: '/settings/storage/edit?id='+ data.id //iframe的url
            });
        });

        $(row).find("button.umount-button").on('click', function () {
            $.ajax({
                url: "settings/storage/unmount",
                type: 'GET',
                data:{id:data.id},
                cache: false,
                success: function (data) {
                    if (data.code == 0) {
                        storageTable.ajax.reload();
                    } else {
                        parent.layer.alert("卸载存储失败:"+data.message);
                    }
                },
                error: function(e) {
                    parent.layer.alert("卸载存储失败!");
                }
            });
        });

        $(row).find("button.delete-button").on('click', function () {
            $.ajax({
                url: "settings/storage/delete",
                type: 'GET',
                data:{id:data.id},
                cache: false,
                success: function (data) {
                    if (data.code == 0) {
                        storageTable.ajax.reload();
                    } else {
                        bootbox.alert({
                            title: "删除存储失败",
                            message: data.message
                        });
                    }
                },
                error: function(e) {
                    bootbox.alert({
                        title: "删除存储失败",
                        message: e
                    });
                }
            });
        });
    };

    storageTable = handleDatatable($("#commander-storage-table"),
        [
            {"mDataProp": "id"},
            {"mDataProp": "name"},
            {"mDataProp": "path"},
            {
                "mDataProp": "mounted",
                "mRender": function (data, type, row, meta) {
                    if(row.mounted){
                        return "已挂载";
                    }else{
                        return "未挂载";
                    }
                }
            },
            {
                "defaultContent": $("<div/>").handlebars($("#storage_row_buttons_template"))
            }
        ],
        {"queryUrl": "/settings/storage/list"},
        fnCreatedRow
    );

    $('#commander-setting-storage-tab').find(".add-button").click(function() {
        layer.open({
            type: 2,
            title: '新增存储',
            //shift: 5,
            shadeClose: true,
            //shade: 0.8,
            area: ['520px', '550px'],
            content: '/settings/storage' //iframe的url
        });
    });

    var fnUdpCreatedRow = function( row, data, dataIndex ) {
        $(row).find("button.delete-button").on('click', function () {
            $.ajax({
                url: "/device/setting/udp/delete/" + data.id,
                type: 'GET',
                cache: false,
                success: function (data) {
                    if (data.code == 0) {
                        udpSettingTable.ajax.reload();
                    } else {
                        bootbox.alert({
                            title: "删除失败",
                            message: data.message
                        });
                    }
                },
                error: function(e) {
                    bootbox.alert({
                        title: "删除失败",
                        message: e
                    });
                }
            });
        });
    }

    $('#commander-setting-udp-tab').find(".add-button").click(function() {
        layer.open({
            type: 2,
            title: '新增udp地址',
            //shift: 5,
            shadeClose: true,
            //shade: 0.8,
            area: ['520px', '280px'],
            content: '/device/setting/udp/create' //iframe的url,
        });
    });

  /*  $('#commander-setting-udp-tab').find("#materialUpLoad").click(function() {


    });*/

    $(".nav-tabs a").on('shown.bs.tab', function(event){
        var a = $(event.target).attr("href");
        if (a == "#commander-setting-storage-tab") {
            storageTable.ajax.reload();
        } else if (a == "#commander-setting-udp-tab") {
            udpSettingTable.ajax.reload();
        } else if (a == "#commander-setting-import-tab") {
            udpSettingTable.ajax.reload();
        }

    });

    $('#udp-range-form').submit(function() {
        if ($('#save-button').hasClass('disabled')) {
            return false;
        }
        var _this = this;
        var id = $(this).find('[name = id]').val();
        var url = "/device/control/udp/save";
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            data: $(_this).serializeArray(),
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    bootbox.alert("设置成功");
                } else {
                    bootbox.alert({
                        title: "设置失败",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "设置失败",
                    message: e
                });
            }
        });
        return false;
    });

});