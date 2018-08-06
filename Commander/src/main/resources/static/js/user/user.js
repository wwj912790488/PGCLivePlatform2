
$(function () {
    var handleDatatable = function(dataTable, columns, urlList) {
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
            "bLengthChange": false,
            "bInfo": false,
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
                var keyword = $("#content-form").find("[name = keyword]").val();
                aoData.push(
                    {"name" : "keyword", "value" : keyword});
            },
            "columnDefs": [
                {
                    "defaultContent": '',
                    "targets": ['_all']
                },
                {
                    'orderable': false,
                    "targets": ['_all']
                },
                {
                    "targets": [0],
                    "render": function(data, type, row, meta) {
                        return "<input type='checkbox' class='select-item' onclick='disableBtn()' value='"+row.userId+"'/>";
                    }
                },
                {
                    "targets": [6],
                    "render": function(data, type, row, meta) {
                        return moment(row.createTime).isValid() ? moment(row.createTime).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {
                    "targets": [7],
                    "render": function(data, type, row, meta) {
                        return data != null && data.length > 25 ? '<span title="'+data+'">'+data.substr( 0, 23 )+'...</span>' : data;
                    }
                }
            ],
            "drawCallback": function(settings) {
                var api = this.api();
                var startIndex = api.context[0]._iDisplayStart; // 获取本页开始的条数
                api.column(1).nodes().each(function(cell, i) {
                    cell.innerHTML = startIndex + i + 1;
                });
            }
        });

        var table_Reload = function() {
            tableClient.ajax.reload(null, false);
        }

        $('#search-button').click(function() {
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
        });

        $('#select_all').on('click', function() {
            if($(this).prop('checked')) {
                $(".select-item").prop('checked', true);
                $('#auth-button').attr("disabled","disabled");
                $('#edit-button').attr("disabled","disabled");
                $('#change-pw-button').attr("disabled","disabled");

            } else {
                $(".select-item").prop('checked', false);
                $('#auth-button').removeAttr("disabled");
                $('#edit-button').removeAttr("disabled");
                $('#change-pw-button').removeAttr("disabled");
            }
        });

        $('#auth-button').click(function() {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                layer.alert("请选择一条记录");
                return false;
            }
            var id = selected.val();
            layer.open({
                skin: 'demo-class',
                type: 2,
                title: '用户授权',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['520px', '455px'],
                content: ["/user/authPage?userId=" + id,'no']
            });
        });

        $('#create-button').click(function() {
            layer.open({
                type: 2,
                title: '创建用户',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['480px', '560px'],
                content: ['/user/create','no']
            });
        });

        $('#edit-button').click(function() {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                layer.alert("请选择一条记录");
                return false;
            }
            var userId = selected.val();
            layer.open({
                type: 2,
                title: '编辑用户',
                shadeClose: true,
                area: ['480px', '560px'],
                content: ['/user/edit?userId=' + userId,'no']
            });
        });


        $('#sync-button').click(function() {
            $.ajax({
                url: "/user/sync",
                type: 'POST',
                dataType: 'json',
                cache: false,
                success: function (data) {
                    var dialog = bootbox.dialog({message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> 同步成功，跳转中...</div>'});
                    setTimeout(function(){
                        dialog.modal('hide');
                        tableClient.ajax.reload();
                    }, 1000);
                },
                error: function(e) {

                }
            });
        });

        $('#change-pw-button').click(function() {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                layer.alert("请选择一条记录");
                return false;
            }
            var userId = selected.val();
            layer.open({
                type: 2,
                title: '修改密码',
                //shift: 5,
                shadeClose: true,
                scrollbar: false,
                //shade: 0.8,
                area: ['470px', '240px'],
                content: ['/user/password?userId=' + userId,'no']
            });
        });

        $('#disable-button').click(function() {
            var selected = $(".select-item:checked");
            var ids = [];
            $.each(selected, function (index, obj) {
                ids.push($(obj).val());
            });
            if (ids.length === 0) {
                layer.alert("请选择一条记录");
                return false;
            }

            layer.confirm('您确定要注销掉所选中的用户？', {
                btn: ['确定','取消'] //按钮
            }, function(index){
                $.ajax({
                    url: "/user/disable",
                    type: 'POST',
                    dataType: 'json',
                    cache: false,
                    data:{userIds:ids.toString()},
                    success: function (data) {
                        layer.close(index);
                        if (data.code === 0) {
                            var dialog = bootbox.dialog({message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i> 注销成功</div>'});
                            setTimeout(function(){
                                dialog.modal('hide');
                                tableClient.ajax.reload();
                            }, 1000);
                        }else {
                            layer.alert(data.message);
                        }
                    },
                    error: function(e) {
                        layer.close(index);
                        layer.alert(e);
                    }
                });
            }, function(){
            });
        });


        $('#remove-button').click(function() {
            var selected = $(".select-item:checked");
            var ids = [];
            $.each(selected, function (index, obj) {
                ids.push($(obj).val());
            });
            if (ids.length == 0) {
                layer.alert("请选择一条记录");
                return false;
            }

            $.ajax({
                url: "/task/supervisor/remove",
                type: 'POST',
                dataType: 'json',
                data: {taskIds : ids.toString()},
                cache: false,
                success: function (data) {
                    tableClient.ajax.reload();
                },
                error: function(e) {

                }
            });
        });

        $('#refresh-button').click(function() {
            window.location.href="/alert";
        });

        return tableClient;

    };

    userTable = handleDatatable($("#user-list-table"),
        [
            {"mDataProp": "", "width": "10px"},
            {"mDataProp": "id"},
            {"mDataProp": "name"},
            {"mDataProp": "realName"},
            {"mDataProp": "roleName"},
            {"mDataProp": "companyName"},
            {"mDataProp": "createTime"},
            {"mDataProp": "remarks"}
        ],
        {"queryUrl": "/user/list"}
    );

});

function disableBtn() {
    var selected = $(".select-item:checked");
    var ids = [];
    $.each(selected, function (index, obj) {
        ids.push($(obj).val());
    });
    if (ids.length > 1) {
        $('#auth-button').attr("disabled","disabled");
        $('#edit-button').attr("disabled","disabled");
        $('#change-pw-button').attr("disabled","disabled");
    }else {
        $('#auth-button').removeAttr("disabled");
        $('#edit-button').removeAttr("disabled");
        $('#change-pw-button').removeAttr("disabled");
    }
}
