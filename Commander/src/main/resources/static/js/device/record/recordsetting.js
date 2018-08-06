$(document).ready(function () {
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
                    "targets": [2],
                    "render": function(data, type, row, meta) {
                        if(row.mounted){
                            return "已挂载";
                        }else{
                            return "未挂载";
                        }
                    }
                }
            ],
            "drawCallback": function(settings) {
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

        table_Reload = function() {
            tableClient.ajax.reload(null, false);
        }
    };

    handleDatatable($("#record-storage-table"),
        [
            {"mDataProp": "name"},
            {"mDataProp": "path"},
            {"mDataProp": "mounted"}
        ],
        {"queryUrl": "/device/recorder/recordstorage"}
    );
});