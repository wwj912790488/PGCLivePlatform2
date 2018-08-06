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
                "sFirst": "<<",
                "sPrevious": "<",
                "sNext": ">",
                "sLast": ">>"
            }
        };

        var tableClient = dataTable.DataTable({
            "bFilter": false,
            "bInfo": false,
            "bLengthChange": false,
            "bSort": false,
            "bStateSave": false,
            "bPaginate": false,
            "bAutoWidth": false,
            "sPaginationType": "full_numbers",
            "oLanguage": dataTablesLanguage,
            "bServerSide": true,
            "bProcessing": false,
            "bDestroy": false,
            "sAjaxSource": urlList.queryUrl,
            "scrollY": "200px",
            "scrollCollapse": "true",
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

                        var indx = $.inArray(row.id,arrRoleIds);
                        if (indx >= 0) {
                            return "<input type='checkbox' class='select-item' value='"+row.id+"' checked/>";
                        }else {
                            return "<input type='checkbox' class='select-item' value='"+row.id+"'/>";
                        }
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


        $('#select_all').on('click', function() {
            if($(this).prop('checked')) {
                $(".select-item").prop('checked', true);
            } else {
                $(".select-item").prop('checked', false);
            }
        });





        $('#cancel-button').click(function() {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index); //关闭
        });

        $('#auth-button').click(function() {
            var userId = $("#userId").val();
            var selected = $(".select-item:checked");
            var ids = [];
            $.each(selected, function (index, obj) {
                ids.push($(obj).val());
            });
            $.ajax({
                url: "/user/auth",
                type: 'POST',
                dataType: 'json',
                data: {roleIdList : ids.toString(),userId : userId},
                cache: false,
                success: function (data) {
                    var index = parent.layer.getFrameIndex(window.name);
                    if (data.code == 0) {
                        parent.layer.close(index); //关闭
                        parent.userTable.ajax.reload();
                    } else {
                        parent.layer.alert("授权失败:"+data.message);
                        parent.layer.close(index);
                    }
                },
                error: function(e) {

                }
            });
        });

    };

    handleDatatable($("#role-list-table"),
        [
            {"mDataProp": "", "width": "10px","align":"center"},
            {"mDataProp": "id","width": "50px"},
            {"mDataProp": "roleName"},
        ],
        {"queryUrl": "/user/role/auth/list"}
    );
});