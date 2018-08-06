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
                        return "<input type='checkbox' class='select-item' onclick='disableBtn()' value='"+row.id+"'/>";
                    }
                },
                {
                    "targets": [3],
                    "render": function(data, type, row, meta) {
                        return moment(row.createTime).isValid() ? moment(row.createTime).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {
                    "targets": [6],
                    "render": function(data, type, row, meta) {
                        return data != null && data.length > 60 ? '<span title="'+data+'">'+data.substr( 0, 58 )+'...</span>' : data;
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

        table_Reload = function() {
            tableClient.ajax.reload(null, false);
        };
        $('#select_all').on('click', function() {
            if($(this).prop('checked')) {
                $(".select-item").prop('checked', true);
                $('#edit-button').attr("disabled","disabled");
            } else {
                $(".select-item").prop('checked', false);
                $('#edit-button').removeAttr("disabled");
            }
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
                url: "/user/role/remove",
                type: 'POST',
                dataType: 'json',
                data: {roleIds : ids.toString()},
                cache: false,
                success: function (data) {
                    if (data.code == 0) {
                        tableClient.ajax.reload();
                    } else {
                        bootbox.alert({
                            title: "删除角色",
                            message: data.message
                        });
                    }
                },
                error: function(e) {

                }
            });
        });

        $('#edit-button').click(function() {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                layer.alert("请选择一条记录");
                return false;
            }

            var id = selected.val();

            layer.open({
                type: 2,
                title: '编辑角色',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['520px', '600px'],
                content: ['/user/role/edit?roleId=' + id,'no']
            });
        });

        $('#create-button').click(function() {
            layer.open({
                type: 2,
                title: '创建角色',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['520px', '600px'],
                content: ['/user/role/create','no']
            });
        });

        $('#search-button').click(function() {
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
        });

        $('#refresh-button').click(function() {
            window.location.href="/user/role";
        });
        return tableClient;
    };



    roleTable = handleDatatable($("#role-list-table"),
        [
            {"mDataProp": "", "width": "10px"},
            {"mDataProp": "id"},
            {"mDataProp": "roleName"},
            {"mDataProp": "createTime"},
            {"mDataProp": "companyName"},
            {"mDataProp": "createUserName"},
            {"mDataProp": "remarks"}
        ],
        {"queryUrl": "/user/role/list"}
    );
});

function disableBtn() {
    var selected = $(".select-item:checked");
    var ids = [];
    $.each(selected, function (index, obj) {
        ids.push($(obj).val());
    });
    if (ids.length > 1) {
        $('#edit-button').attr("disabled","disabled");
    }else {
        $('#edit-button').removeAttr("disabled");
    }
}