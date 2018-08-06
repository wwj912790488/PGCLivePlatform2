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
                var keyword = $("#log-form").find("[name = keyword]").val();
                var startTime =  $('#log-form').find('[name = startTime]').val();
                var endTime =  $('#log-form').find('[name = endTime]').val();
                aoData.push(
                    {"name" : "startTime", "value" : startTime},
                    {"name" : "endTime", "value" : endTime},
                    {"name" : "keyword", "value" : keyword});
            },
            "columnDefs": [
                {
                    "targets": [6],
                    "createdCell": function (td, cellData, rowData, row, col) {
                        //定时刷新表格时不会清除选中状态
                        $(td).hover(function(){
                            $(td).prop('title', rowData.params);
                        }, function(){
                            $(td).prop('title', "");
                        });
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

        $('#search-button').click(function() {
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
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

            bootbox.confirm({message: "确定要删除日志吗？", callback: function(result) {
                if (result == true) {
                    $.ajax({
                        url: "/log/remove",
                        type: 'POST',
                        dataType: 'json',
                        data: {ids: ids.toString()},
                        cache: false,
                        success: function (data) {
                            tableClient.ajax.reload();
                        }
                    });
                }
            }});
        });

        var startTimepicker =  $('#log-form').find('[name = startTime]');
        var endTimepicker =  $('#log-form').find('[name = endTime]');
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


    handleDatatable($("#alert-list-table"),
        [
            {
                "mDataProp": "id",
                "sWidth": "10px",
                "mRender":function (data, type, row, meta) {
                    return "<input type='checkbox' class='select-item' value='" + row.id + "'/>";
                }
            },
            {"mDataProp": "id", "sWidth": "40px"},
            {
                "mDataProp": "createTime",
                "sWidth": "160px",
                "mRender": function (data, type, row, meta) {
                    return moment(row.createTime).isValid() ? moment(row.createTime).format('YYYY-MM-DD HH:mm:ss') : "";
                }
            },
            {
                "mDataProp": "username",
                "sWidth": "100px"
            },
            {
                "mDataProp": "operation",
                "sWidth": "100px"
            },
            {
                "mDataProp": "url",
                "sWidth": "300px"
            },
            {
                "mDataProp": "params"
            },
            {
                "mDataProp": "ip",
                "sWidth": "120px"
            }
        ],
        {
            "queryUrl": "/log/list"
        }
    );
});