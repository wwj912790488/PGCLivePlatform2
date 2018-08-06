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
            "columnDefs": [
                {
                    "targets": [5],
                    "createdCell": function (td, cellData, rowData, row, col) {
                        //定时刷新表格时不会清除选中状态
                        $(td).hover(function(){
                            $(td).prop('title', rowData.description);
                        }, function(){
                            $(td).prop('title', "");
                        });
                    }
                }
            ],
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
                var contentId = $("#alert-current-form").find("[name = contentId]").val();
                var type = $("#alert-current-form").find("[name = type]").val();
                var serverType = $("#alert-current-form").find("[name = serverType]").val();
                var level = $("#alert-current-form").find("[name = level]").val();
                var keyword = $("#alert-current-form").find("[name = keyword]").val();
                var startTime =  $('#alert-current-form').find('[name = startTime]').val();
                var endTime =  $('#alert-current-form').find('[name = endTime]').val();
                aoData.push(
                    {"name": "contentId", "value": contentId},
                    {"name" : "type", "value" : type},
                    {"name" : "serverType", "value" : serverType},
                    {"name" : "level", "value" : level},
                    {"name" : "startTime", "value" : startTime},
                    {"name" : "endTime", "value" : endTime},
                    {"name" : "keyword", "value" : keyword});
            },
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

        $('#search-button').click(function() {
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
        });

        $("#alert-current-form").find("[name = type], [name = serverType], [name = level]").on('change', function() {
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
        })

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

            bootbox.confirm({
                message: "确定要忽略告警吗？",
                buttons: {
                    confirm: {
                        label: '确定',
                        className: 'btn-primary'
                    },
                    cancel: {
                        label: '取消',
                        className: 'btn-default'
                    }
                },
                callback: function(result) {
                if (result == true) {
                    $.ajax({
                        url: "/alert/remove",
                        type: 'POST',
                        dataType: 'json',
                        data: {ids: ids.toString()},
                        cache: false,
                        success: function (data) {
                            tableClient.ajax.reload();
                        },
                        error: function (e) {

                        }
                    });
                }
            }});
        });

        var startTimepicker =  $('#alert-current-form').find('[name = startTime]');
        var endTimepicker =  $('#alert-current-form').find('[name = endTime]');
        startTimepicker.datetimepicker({
            format: 'YYYY-MM-DD HH:mm:ss',
            locale: 'zh-cn'
        });

        endTimepicker.datetimepicker({
            format: 'YYYY-MM-DD HH:mm:ss',
            locale: 'zh-cn'
        });

        startTimepicker.on("dp.change", function (e) {
            endTimepicker.data("DateTimePicker").minDate(e.date);
        });

        endTimepicker.on("dp.change", function (e) {
            startTimepicker.data("DateTimePicker").maxDate(e.date);
        });
    };

    var serverType = {
        "RECORDER": "收录系统",
        "CONVENE": "汇聚系统",
        "LIVE": "在线系统",
        "SUPERVISOR": "监看系统",
        "DELAYER": "延时系统",
        "IPSWITCH": "IP切换器",
        "PGC": "PGC平台"
    }

    var levelType = {
        "WARNING": "警告",
        "ERROR": "错误",
        "NOTIFY": "通知",
    }

    var alertType = {
        "TASK": "任务",
        "SYSTEM": "系统",
        "DEVICE": "设备",
        "HARDWARE": "硬件",
        "SOURCE": "信源"
    }

    handleDatatable($("#alert-current-list-table"),
        [
            {
                "mDataProp": "id",
                "width": "10px",
                "mRender":function (data, type, row, meta) {
                    return "<input type='checkbox' class='select-item' value='" + row.id + "'/>";
                }
            },
            {"mDataProp": "id", "sWidth": "40px"},
            {
                "mDataProp": "type",
                "sWidth": "70px",
                "mRender": function (data, type, row, meta) {
                    return alertType[row.type]==undefined?"":alertType[row.type];
                }
            },
            {
                "mDataProp": "serverType",
                "sWidth": "70px",
                "mRender": function (data, type, row, meta) {
                    return serverType[row.serverType]==undefined?"":serverType[row.serverType];
                }
            },
            {
                "mDataProp": "level",
                "sWidth": "40px",
                "mRender": function (data, type, row, meta) {
                    return levelType[row.level]==undefined?"":levelType[row.level];
                }
            },
            {
                "mDataProp": "description"
            },
            {
                "mDataProp": "createdAt",
                "sWidth": "160px",
                "mRender": function (data, type, row, meta) {
                    return moment(row.createdAt).isValid() ? moment(row.createdAt).format('YYYY-MM-DD HH:mm:ss') : "";
                }
            },
            {
                "mDataProp": "ip",
                "sWidth": "120px"
            }
        ],
        {
            "queryUrl": "/alert/list"
        }
    );
});