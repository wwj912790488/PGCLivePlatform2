/**
 * Created by slw on 2018/3/22.
 */
$(function () {
    var handleDatatable = function(dataTable, columns, urlList) {
        var timerr = null;  //定时器
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
                        return "<input type='checkbox' class='select-item' value='"+row.id+"' data-status='"+row.recorderTaskStatus+"'/>";
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
                        return moment(row.startTime).isValid() ? moment(row.startTime).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {
                    "targets": [7],
                    "render": function(data, type, row, meta) {
                        return moment(row.endTime).isValid() ? moment(row.endTime).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                }
            ],
            "createdRow": function( row, data, dataIndex ) {
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
                clearTimeout(timerr);
                timerr = setTimeout(function() {
                    tableClient.ajax.reload(null, false);
                }, 5000);
            }
        }).on('page.dt', function() {
            ids_selected = [];
        });

        table_Reload = function() {
            tableClient.ajax.reload(null, false);
        }

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
                bootbox.alert("请选择一条记录");
                return false;
            }

            $.ajax({
                url: "/task/recorder/start",
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
                bootbox.alert("请选择一条记录");
                return false;
            }

            $.ajax({
                url: "/task/recorder/stop",
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

        $('#remove-button').click(function() {
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
                url: "/task/recorder/remove",
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
            window.location.href = "/task/recorder/create";
        });

        $('#edit-button').click(function() {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                bootbox.alert("请选择一条记录");
                return false;
            }

            var id = selected.val();
            window.location.href = "/task/recorder/edit/" + id;
        });

        var updateCtrlBtnUI = function () {
            var selectedRows = $(".select-item:checked");
            if (selectedRows.length > 0) {
                $('#start-button').prop('disabled', false);
                $('#stop-button').prop('disabled', false);
                $('#remove-button').prop('disabled', false);
            } else {
                $('#start-button').prop('disabled', true);
                $('#stop-button').prop('disabled', true);
                $('#remove-button').prop('disabled', true);
            }

            if (selectedRows.length == 1) {
                $('#edit-button').prop('disabled', false);
            } else {
                $('#edit-button').prop('disabled', true);
            }
        };
    };

    handleDatatable($("#task-record-table"),
        [
            {"mDataProp": "id", "width": "10px"},
            {"mDataProp": "id"},
            {"mDataProp": "contentId"},
            {"mDataProp": "outputPath"},
            {"mDataProp": "fileName"},
            {"mDataProp": "recorderTaskStatus"},
            {"mDataProp": "startTime"},
            {"mDataProp": "endTime"}
        ],
        {"queryUrl": "/task/recorder/list"}
    );
});