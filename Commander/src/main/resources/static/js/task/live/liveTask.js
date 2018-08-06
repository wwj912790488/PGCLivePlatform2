$(function () {
    bootbox.setLocale("zh_CN");
    var handleDatatable = function(dataTable, columns, urlList) {
        var timer = null;  //定时器
        var ids_selected = [];

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
                var contentId = $("#live-task-form").find("[name = 'content.id']").val();
                var status = $("#live-task-form").find("[name = 'status']").val();
                var keyword = $("#live-task-form").find("[name = 'keyword']").val();
                aoData.push(
                    {"name" : "contentId", "value" : contentId},
                    {"name" : "status", "value" : status},
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
                        return "<input type='checkbox' class='select-item' value='"+row.id+"' data-status='"+row.liveTaskStatus+"'/>";
                    },
                    "createdCell": function (td, cellData, rowData, row, col) {
                        //定时刷新表格时不会清除选中状态
                        $(td).find('.select-item').prop("checked", $.inArray(rowData.id, ids_selected) >= 0);
                        $(td).on('click', '.select-item', function() {
                            if ($(this).prop('checked')) {
                                ids_selected.push(rowData.id);
                            } else {
                                ids_selected.splice($.inArray(rowData.id, ids_selected), 1);
                            }
                        });
                    }
                },
                {
                    "targets": [6],
                    "render": function(data, type, row, meta) {
                        return ((row.startTime!=undefined) && moment(row.startTime).isValid()) ? moment(row.startTime).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {
                    "targets": [7],
                    "render": function(data, type, row, meta) {
                        return row.totalOutputCount+"("+row.totalOutputGroupCount+")";
                    }
                },
                {
                    "targets": [9],
                    "render": function(data, type, row, meta) {
                        return $("<div/>").handlebars($("#live_row_buttons_template"), row);
                    }
                }
            ],
            "createdRow": function(row, data, dataIndex) {
                $(row).find(".select-item").on('click', function () {
                    var selected = $(".select-item:checked");
                    var all_select = $(".select-item");
                    if (selected.length == all_select.length) {
                        $("#select_all").prop('checked', true);
                    } else {
                        $("#select_all").prop('checked', false);
                    }

                    if ($(".select-item:checked").length == 0 || $(".select-item:checked").length < $(".select-item").length) {
                        $('#select_all').prop('checked', false);
                    }

                    updateCtrlBtnUI();
                });
            },
            "drawCallback": function(settings) {
                updateCtrlBtnUI();
                clearTimeout(timer);
                timer = setTimeout(function() {
                    tableClient.ajax.reload(null, false);
                }, 5000);
            }
        }).on('page.dt', function() {
            ids_selected = [];
        });

        table_Reload = function() {
            tableClient.ajax.reload(null, false);
        };

        $('#search-button').click(function() {
            ids_selected = [];
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
        });

        $('#reset-button').click(function() {
            $("#live-task-form").find("[name = 'content.id']").val("");
            $("#live-task-form").find("[name = 'status']").val("");
            $("#live-task-form").find("[name = 'keyword']").val("");
        });

        $('#select_all').on('click', function() {
            if($(this).prop('checked')) {
                $(".select-item").prop('checked', true);

                $.each($(".select-item"), function (index, obj) {
                    var id = parseInt($(obj).val());
                    if ($.inArray(id, ids_selected) < 0) {
                        ids_selected.push(id);
                    }
                });
            } else {
                $(".select-item").prop('checked', false);
                ids_selected = [];
            }
            updateCtrlBtnUI();
        });

        $('#start-button').click(function() {
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
                url: "/task/live/start",
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

        $('#stop-button').click(function() {
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
                url: "/task/live/stop",
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

        $('#edit-button').click(function() {
            var selected = $(".select-item:checked");
            var taskId = selected.val();
            if (selected.length != 1) {
                layer.alert("请选择一条记录");
                return false;
            }
            window.location.href = "/task/live/edit/?taskId="+ taskId;
        });

        $('#copy-button').click(function() {
            var selected = $(".select-item:checked");
            var taskId = selected.val();
            if (selected.length != 1) {
                layer.alert("请选择一条记录");
                return false;
            }
            window.location.href = "/task/live/copy/?taskId="+ taskId;
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
                url: "/task/live/remove",
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

        $('#create-button').click(function() {
            window.location.href = "/task/live/create";
        });

        var updateCtrlBtnUI = function () {
            var selectedRows = $(".select-item:checked");
            if (selectedRows.length > 0) {
                $('#start-button').prop('disabled', false);
                $('#stop-button').prop('disabled', false);
                $('#remove-button').prop('disabled', false);
                $.each(selectedRows, function(index, row) {
                    if ($(row).data('status') == '运行') {
                        $('#start-button').prop('disabled', true);
                        $('#remove-button').prop('disabled', true);
                        return;
                    }
                });

                $.each(selectedRows, function(index, row) {
                    if (!($(row).data('status') == '运行')) {
                        $('#stop-button').prop('disabled', true);
                        return;
                    }
                });

                if(selectedRows.length == 1){
                    $('#copy-button').prop('disabled', false);
                }else {
                    $('#copy-button').prop('disabled', true);
                }
            } else {
                $('#start-button').prop('disabled', true);
                $('#stop-button').prop('disabled', true);
                $('#edit-button').prop('disabled', true);
                $('#remove-button').prop('disabled', true);
            }

        };
    };

    handleDatatable($("#task-live-table"),
        [
            {"mDataProp": "id", "width": "10px"},
            {"mDataProp": "id"},
            {"mDataProp": "name"},
            {"mDataProp": "content.name"},
            {"mDataProp": "inputUri"},
            {"mDataProp": "createUser"},
            {"mDataProp": "startTime"},
            {"mDataProp": "outputNum"},
            {"mDataProp": "liveTaskStatus"},
            {
                "defaultContent": ""
            }
        ],
        {"queryUrl": "/task/live/list"}
    );
});