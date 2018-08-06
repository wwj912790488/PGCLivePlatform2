$(function () {
    var handleDatatable = function (dataTable, columns, urlList) {
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
            "iDisplayLength": 15,
            "bLengthChange": false,
            "bAutoWidth": false,
            "sPaginationType": "full_numbers",
            "oLanguage": dataTablesLanguage,
            "bServerSide": true,
            "bProcessing": false,
            "bDestroy": false,
            "sAjaxSource": urlList.queryUrl,
            "aoColumns": columns,
            "fnServerData": function (sSource, aoData, fnCallback) {
                $.ajax({
                    "dataType": 'json',
                    "type": "POST",
                    "url": sSource,
                    "data": aoData,
                    "success": function (data) {
                        fnCallback(data.data);
                    },
                    "timeout": 30000
                });
            },
            "fnServerParams": function (aoData) {
                var keyword = $("#material-from").find("[name = keyword]").val();
                aoData.push(
                    {"name": "keyword", "value": keyword});
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
                    "render": function (data, type, row, meta) {
                        return "<input type='checkbox' class='select-item' value='" + row.id + "'/>";
                    }
                },
                {
                    "targets": [6],
                    "render": function(data, type, row, meta) {
                        return data != null && data.length > 60 ? '<span title="'+data+'">'+data.substr( 0, 58 )+'...</span>' : data;
                    }
                }
            ],
            "drawCallback": function (settings) {
                $(".select-item").on('click', function () {
                    var selected = $(".select-item:checked");
                    var all_select = $(".select-item");
                    if (selected.length == all_select.length) {
                        $("#select_all").prop('checked', true);
                    } else {
                        $("#select_all").prop('checked', false);
                    }
                });

                if ($(".select-item:checked").length == 0 || $(".select-item:checked").length < $(".select-item").length) {
                    $('#select_all').prop('checked', false);
                }
            }
        });

        table_Reload = function () {
            tableClient.ajax.reload(null, false);
        }

        $('#select_all').on('click', function () {
            if ($(this).prop('checked')) {
                $(".select-item").prop('checked', true);
            } else {
                $(".select-item").prop('checked', false);
            }
        });

        $('#remove-button').click(function () {
            var selected = $(".select-item:checked");
            var ids = [];
            $.each(selected, function (index, obj) {
                ids.push($(obj).val());
            });
            if (ids.length == 0) {
                bootbox.alert("请选择一条记录");
                return false;
            }

            $.ajax({
                url: "/material/deleteLogo",
                type: 'POST',
                dataType: 'json',
                data: {ids: ids.toString()},
                cache: false,
                success: function (data) {
                    if (data.code == 0) {
                        tableClient.ajax.reload();
                    } else {
                        var dataList = data.data;
                        var msg = "";
                        for (var i = 0; i < dataList.length; i++) {
                            msg = msg + "<p>" + dataList[i] + "</p>"
                        }
                        dialog(msg);
                        /* parent.layer.alert("失败 " + data.message);*/
                    }

                },
                error: function (e) {
                    parent.layer.alert("失败");
                }
            });
        });
        $('#search-button').click(function () {
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
        });

        var dialog = function (msg) {
            bootbox.dialog({
                title: '活动管理正在使用,台标不能删除',
                message: msg,
                buttons: {
                    ok: {
                        label: "确定",
                        className: 'btn-info',
                        callback: function () {
                        }
                    }
                }
            });

        }


        $('#create-button').click(function () {
            layer.open({
                type: 2,
                title: '添加台标',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['500px', '550px'],
                content: '/material/addLogo' //iframe的url
            });
        });
    };

    handleDatatable($("#material-logo-table"),
        [
            {"mDataProp": "id", "sWidth": "10px"},
            {"mDataProp": "id","sWidth": "30px"},
            {"mDataProp": "name","sWidth": "100px"},
            {
                "mDataProp": "createDate",
                "sWidth": "160px",
                "mRender": function (data, type, row, meta) {
                    return moment(row.createDate).isValid() ? moment(row.createDate).format('YYYY-MM-DD HH:mm:ss') : "";
                }
            },
            {"mDataProp": "createUserName","sWidth": "100px"},

            {"mDataProp": "content","className": 'table_column'},
            {"mDataProp": "description","sWidth": "240px"}
        ],
        {"queryUrl": "/material/list"}
    );
});