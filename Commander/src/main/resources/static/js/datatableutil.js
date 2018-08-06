var dataTablesLanguage = {
    "sLengthMenu": "每页显示 _MENU_ 条记录",
    "sZeroRecords": "没有检索到数据",
    "sInfo": "当前数据为从第 _START_ 到第 _END_ 条数据；总共有 _TOTAL_ 条记录",
    "sInfoFiltered": "(从 _MAX_ 条记录中搜索)",
    "sInfoEmpty": "没有数据要显示",
    "sProcessing": "正在加载数据...",
    "sSearch": "搜索",
    "oPaginate": {
        "sFirst": "首页",
        "sPrevious": "前页",
        "sNext": "后页",
        "sLast": "尾页"
    }
};

var handleDatatable = function (dataTable, columns, urlList) {
    var tableClient = dataTable.DataTable({
        "iDisplayLength": 10,
        "bFilter": false,
        "bInfo": true,
        "bSort": false,
        "bStateSave": false,
        "autoWidth": false,
        "bPaginate": true,
        "sPaginationType": "full_numbers",
        "oLanguage": dataTablesLanguage,
        "bServerSide": true,
        "bProcessing": true,
        "bDestroy": true,
        "sAjaxSource": urlList.queryUrl,
        "aoColumns": columns,
        "fnServerData": function (sSource, aoData, fnCallback) {
            $.ajax({
                "dataType": 'json',
                "type": "POST",
                "url": sSource,
                "data": aoData,
                "success": function (data) {
                    fnCallback(data);
                    $('#selectAll').prop("checked", false);
                },
                "timeout": 30000
            });
        },
        "fnServerParams": function (aoData) {
            var searchText = $("#searchText").val();
            var filterFromToDate = $('#filterFromToDate span').html();
            aoData.push({"name": "searchText", "value": searchText}, {
                "name": "filterFromToDate",
                "value": filterFromToDate
            });
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
                    return "<input type='checkbox' id='" + row.id + "' value='" + row.id + "' />";
                }
            },
        ],
        "createdRow": function (row, data, index) {
        },
    });

    $('#selectAll').change(function () {
        var cells = tableClient.cells().nodes();
        $(cells).find(':checkbox').prop('checked', $(this).is(':checked'));
    });

    $('#buttonSearch').click(function () {
//           tableClient.clear().draw();
        tableClient.ajax.reload();
    });

    $('#buttonInsert').click(function () {
        window.location.href = urlList.insertUrl;
    });

    $('#buttonEdit').click(function () {
        var cells = tableClient.cells().nodes();
        var cellsChecked = $(cells).find(':checkbox:checked');
        if (cellsChecked.length != 1) {
            bootbox.alert("请单选一条记录");
            return;
        }
        var valueChecked = $(cells).find(':checkbox:checked').map(
            function () {
                return this.value;
            }).get().join(",");
        window.location.href = urlList.editUrl + "?checked=" + valueChecked;
    });

    $('#buttonDelete').click(function () {
        var cells = tableClient.cells().nodes();
        var valuesChecked = $(cells).find(':checkbox:checked').map(
            function () {
                return this.value;
            }).get().join(",");
        if (valuesChecked == "") {
            bootbox.alert("请选择至少一条记录");
            return;
        }
        bootbox.confirm({
            message: "确定要删除吗？", callback: function (result) {
                if (result == true) {
                    $(this).callAjax("POST", urlList.deleteUrl, valuesChecked);
                }
            }
        });
    });

    $.fn.callAjax = function (type, method, checkeds) {
        $.ajax({
            type: type,
            url: method,
            dataType: "text",
            timeout: 30000,
            data: {checked: checkeds},
            success: function (data) {
                if (data == "ok") {
                    tableClient.clear().draw();
                    tableClient.ajax.reload();
                }
            },
            error: function (e) {
                if (e.status == 405) {
                    bootbox.alert("没有权限操作。");
                }
                else {
                    bootbox.alert("出错！");
                }
            }
        });
    }
};
