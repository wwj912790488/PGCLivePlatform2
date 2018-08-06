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
            "bInfo": true,
            "bSort": false,
            "bStateSave": false,
            "bPaginate": true,
            "bLengthChange": false,
            "iDisplayLength": 10,
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
            "createdRow": function( row, data, dataIndex ) {
                $(row).find(".select-item").on('click', function () {
                    updateSelectUI();
                });
            },
            "drawCallback": function(settings) {
            }
        });

        var updateSelectUI = function () {
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
        }

        $('#select_all').on('click', function() {
            if($(this).prop('checked')) {
                $(".select-item").prop('checked', true);
            } else {
                $(".select-item").prop('checked', false);
            }
        });

        $('#create-button').click(function() {
            layer.open({
                type: 2,
                title: '添加',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['480px', '560px'],
                content: '/device/control/vlan/create'
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

            bootbox.confirm({message: "确定要删除吗？", callback: function(result) {
                if (result == true) {
                    $.ajax({
                        url: "/device/control/vlan/delete",
                        type: 'POST',
                        dataType: 'json',
                        data: {ids : ids.toString()},
                        cache: false,
                        success: function (data) {
                            tableClient.ajax.reload();
                            $('#select_all').prop('checked', false);
                        },
                        error: function(e) {

                        }
                    });
                }
            }});
        });

        $('#edit-button').click(function () {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                bootbox.alert("请选择一条记录");
                return false;
            }

            var id = selected.val();
            layer.open({
                type: 2,
                title: '添加',
                //shift: 5,
                shadeClose: true,
                //shade: 0.8,
                area: ['480px', '560px'],
                content: '/device/control/vlan/edit/' + id
            });
        });

        return tableClient;
    };

    var nioType = {"CONVENE_IN":"汇聚输入", "CONVENE_OUT":"汇聚输出", "DELAYER_IN":"延时输入", "DELAYER_OUT":"延时输出", "LIVE_IN":"在线输入", "LIVE_OUT":"在线输出"};


    vlanTable = handleDatatable($("#vlan-setting-table"),
        [
            {
                "mDataProp": "id",
                "sWidth": "10px",
                "mRender": function (data, type, row, meta) {
                    return "<input type='checkbox' class='select-item' value='"+row.id+"' data-status='" + row.status +"'/>";
                }
            },
            {"mDataProp": "id"},
            {"mDataProp": "name"},
            {"mDataProp": "tag"},
            {"mDataProp": "cidr"},
            {
                "mDataProp": "nioType",
                "mRender": function (data, type, row, meta) {
                    var niotypes = row.nioType;
                    var arr = $.trim(niotypes).split(",");
                    var str = [];
                    $.each(arr, function (index, nio) {
                        var type = nioType[nio];
                        if (!type) {
                            type = nio;
                        }
                        str.push(type);
                    });

                    return str;
                }
            },
            {"mDataProp": "note"}
        ],
        {"queryUrl": "/device/control/vlan/list"}
    );
});