/**
 * Created by slw on 2018/3/21.
 */
$(function () {
    bootbox.setLocale("zh_CN");

    if (ClipboardJS.isSupported()) {
        var clipboard = new ClipboardJS('.btn-clipboard');
    }

    var myTimeOut = false;
    function setMyInterval(func, delay) {
        if(myTimeOut) return;
        func();
        setTimeout(function () {
            setMyInterval(func, delay)
        }, delay);
    }

    var handleDatatable = function(dataTable, columns, urlList) {
        var oldThis = null;
        var oldTr = null;
        var oldRow = null;
        var ids_selected = [];
        var id_detail = null;
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
            "iDisplayLength": 15,
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
                var status = $("#content-form").find("[name = status]").val();
                var keyword = $("#content-form").find("[name = keyword]").val();
                aoData.push(
                    {"name" : "keyword", "value" : keyword},
                    {"name" : "status", "value" : status});
            },
            "columnDefs": [
                {
                    "targets": [0],
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
                    "targets": [5],
                    "createdCell": function (td, cellData, rowData, row, col) {
                        $(td).hover(function() {
                            var titleValue = "";
                            if (rowData.master != null) {
                                if (rowData.master.streamType == 1) {
                                    titleValue = rowData.master.pushUri;
                                } else {
                                    titleValue = rowData.master.sourceUri;
                                }
                            }
                            $(td).prop('title', titleValue);
                        }, function() {
                            $(td).prop('title', "");
                        });
                    }
                }
            ],
            "createdRow": function( row, data, dataIndex ) {
                var _this = $(row).find('td.details-control')[0];
                var _tr = $(_this).closest('tr');
                var _row = tableClient.row(_tr);
                if (data.id == id_detail) {
                    oldTr = _tr;
                    oldRow = _row;
                    oldThis = _this;

                    $.ajax({
                        url: "/content/listDetail",
                        type: 'POST',
                        dataType: 'json',
                        data: {contentIds : id_detail.toString()},
                        cache: false,
                        success: function (data) {
                            if (data.code == 0 && data.data != null) {
                                RefreshTdDetailData(_row, _tr, data.data[id_detail]);
                            }
                        }
                    })
                }

                $(row).find(".select-item").on('click', function () {
                    updateSelectUI();
                    updateCtrlBtnUI();
                });
            },
            "drawCallback": function(settings) {
                updateCtrlBtnUI();
                RefreshTableData("#content-table");
            }
        }).on('page.dt', function() {
            oldTr = null;
            oldRow = null;
            oldThis = null;
            ids_selected = [];
            id_detail= null;
        }).on('click', 'td.details-control', function () {
            var tr = $(this).closest('tr');
            var row = tableClient.row(tr);

            if (oldThis != null && oldThis !== this) {
                oldRow.child.hide();
                oldTr.removeClass('shown');
            }
            oldTr = tr;
            oldRow = row;
            oldThis = this;

            if (row.child.isShown()) {
                // This row is already open - close it
                row.child.hide();
                tr.removeClass('shown');

                id_detail = null;
                myTimeOut = true;
            }
            else {
                // Open this row
                var id = row.data().id;
                $.ajax({
                    url: "/content/listDetail",
                    type: 'POST',
                    dataType: 'json',
                    data: {contentIds : id.toString()},
                    cache: false,
                    success: function (data) {
                        if (data.code == 0 && data.data != null) {
                            RefreshTdDetailData(row, tr, data.data[id]);
                        }
                    }
                })
            }
        } );

        var RefreshTableData = function (tableId) {
            var dataTable = $(tableId).dataTable();
            var sData = dataTable.fnGetData();
            var ids = [];
            for (var i = 0; i < sData.length; i++) {
                ids.push(sData[i].id);
            }

            var linksStatusIndex = 9;
            var contentStatusIndex = 4;
            $.ajax({
                url: "/content/listDetail",
                type: 'POST',
                dataType: 'json',
                data: {contentIds : ids.toString()},
                cache: false,
                success: function (data) {
                    if (data.code == 0 && data.data != null) {
                        var result = data.data;
                        for (var i = 0; i < sData.length; i++) {
                            var id = sData[i].id;
                            var detail = result[id];
                            if (detail != null && detail != '') {
                                dataTable.fnUpdate($("<div/>").handlebars($("#content_row_buttons_template"), detail), i, linksStatusIndex, false);
                                dataTable.fnUpdate(content_statusType[detail.status], i, contentStatusIndex, false);
                                $('#content-row-' + id).data("status", detail.status);

                                if (id == id_detail) {
                                    var master = detail.master;
                                    if (master != null) {
                                        $('#master-random-udp-' + id).text(master.udpUri);
                                        var masterClipboard = $('#master-random-clipboard-' + id);
                                        if (master.udpUri != null && master.udpUri.length > 0) {
                                            masterClipboard.data('clipboard-text', master.udpUri);
                                            masterClipboard.show();
                                        } else {
                                            masterClipboard.hide();
                                        }
                                    }

                                    var slave = detail.slave;
                                    if (slave != null) {
                                        $('#slave-random-udp-' + id).text(slave.udpUri);
                                        var slaveClipboard = $('#slave-random-clipboard-' + id);
                                        if (slave.udpUri != null && slave.udpUri.length > 0) {
                                            slaveClipboard.data('clipboard-text', slave.udpUri);
                                            slaveClipboard.show();
                                        } else {
                                            slaveClipboard.hide();
                                        }
                                    }
                                }
                            }
                        }
                        updateCtrlBtnUI();
                    }
                }
            }).always(function() {
                setTimeout(function () {
                    RefreshTableData(tableId)
                }, 5000);
            });
        }

        var RefreshTdDetailData = function (row, tr, data) {
            row.child($("<div/>").handlebars($("#table_detail_template"), data)).show();
            tr.addClass('shown');
            id_detail = data.id;

            var detailDom = $('#content-detail-' + data.id);

            var masterUri = detailDom.find('.master-source-uri').text();
            var masterMediaInfo = detailDom.find('.master-media-info');
            if (masterUri && masterMediaInfo.length > 0) {
                GetVideoInfo(masterUri, masterMediaInfo);
            }

            var slaveUri = detailDom.find('.slave-source-uri').text();
            var slaveMediaInfo = detailDom.find('.slave-media-info');
            if (slaveUri && slaveMediaInfo.length > 0) {
                GetVideoInfo(slaveUri, slaveMediaInfo);
            }

            var backupUri = detailDom.find('.backup-source-uri').text();
            var backupMediaInfo = detailDom.find('.backup-media-info');
            if (backupUri && backupMediaInfo.length > 0) {
                GetVideoInfo(backupUri, backupMediaInfo);
            }

            myTimeOut = false;
            setMyInterval(function () {
                RefreshImg(data);
            }, 2000);
        }

        var GetVideoInfo = function(path, dom) {
            $.ajax({
                url: "/api/getVideoInfo?path=" + path,
                type: 'POST',
                cache: false,
                success: function (data) {
                    if (data.code == 0 && data.data != null) {
                        dom.text(data.data);
                    }
                }
            });
        }

        var RefreshImg = function(data) {
            var img = $('#content-detail-' + data.id).find('.live-task-thumb');
            if (img.length > 0 &&  data.id && data.status == "RUNNING") {
                var url = '/api/getLiveTaskThumb?';
                url += 'taskId=' + data.id + '&width=120' + '&rnd=' + Math.random();
                var bgImg = new Image();
                bgImg.onload = function() {
                    img.attr('src', url);
                };
                bgImg.src = url;
            }
        }

        table_Reload = function() {
            reloadTable();
        }

        $('#search-button').click(function() {
            reloadTableAll();
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
                bootbox.alert("请选择一条记录");
                return false;
            }

            $.ajax({
                url: "/content/start",
                type: 'POST',
                dataType: 'json',
                data: {contentIds : ids.toString()},
                cache: false,
                success: function (data) {
                    reloadTable();
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

            bootbox.confirm({message: "确定要停止该频道及其相关联的任务吗？", callback: function(result) {
                if (result == true) {
                    $.ajax({
                        url: "/content/stop",
                        type: 'POST',
                        dataType: 'json',
                        data: {contentIds : ids.toString()},
                        cache: false,
                        success: function (data) {
                            reloadTable();
                        },
                        error: function(e) {

                        }
                    });
                }
            }});
        });

        $('#edit-button').click(function() {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                bootbox.alert("请选择一条记录");
                return false;
            }

            var id = selected.val();
            window.location.href = "/content/edit/" + id;
        });


        $('#copy-button').click(function() {
            var selected = $(".select-item:checked");
            if (selected.length != 1) {
                bootbox.alert("请选择一条记录");
                return false;
            }

            var id = selected.val();
            window.location.href = "/content/copy/" + id;
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

            bootbox.confirm({message: "确定要删除该频道及其相关联的任务吗？", callback: function(result) {
                if (result == true) {
                    $.ajax({
                        url: "/content/remove",
                        type: 'POST',
                        dataType: 'json',
                        data: {contentIds : ids.toString()},
                        cache: false,
                        success: function (data) {
                            reloadTableAll();
                            updateSelectUI();
                        },
                        error: function(e) {

                        }
                    });
                }
            }});

        });

        $('#create-button').click(function() {
            window.location.href = "/content/create";
        });

        $('#switch-auto-button').click(function () {
            switchContent("AUTO");
        });

        $('#switch-master-button').click(function () {
            switchContent("MASTER");
        });

        $('#switch-slave-button').click(function () {
            switchContent("SLAVE");
        });

        $('#switch-backup-button').click(function () {
            switchContent("BACKUP");
        });

        var switchContent = function (switchType) {
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
                url: "/content/switchIp",
                type: 'POST',
                dataType: 'json',
                data: {contentIds : ids.toString(), switchType: switchType},
                cache: false,
                success: function (data) {
                    if (data.code != 0) {
                        bootbox.alert(data.message);
                    } else {
                        bootbox.alert("切换成功");
                    }
                    reloadTable();
                },
                error: function(e) {
                    bootbox.alert("切换失败");
                }
            });
        }

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

        var updateCtrlBtnUI = function () {
            var selectedRows = $(".select-item:checked");
            if (selectedRows.length > 0) {
                $('#start-button').prop('disabled', false);
                $.each(selectedRows, function(index, row) {
                    if ($(row).data('status') == 'RUNNING' || $(row).data('status') == 'STARTING' || $(row).data('status') == 'STOPPING') {
                        $('#start-button').prop('disabled', true);
                        return;
                    }
                });

                $('#remove-button').prop('disabled', false);
                $.each(selectedRows, function(index, row) {
                    if ($(row).data('status') == 'RUNNING' || $(row).data('status') == 'STARTERROR' || $(row).data('status') == 'STOPERROR' ||
                        $(row).data('status') == 'STARTING' || $(row).data('status') == 'STOPPING') {
                        $('#remove-button').prop('disabled', true);
                        return;
                    }
                });

                $('#stop-button').prop('disabled', false);
                $.each(selectedRows, function(index, row) {
                    if ($(row).data('status') == 'PENDING' || $(row).data('status') == 'STOPPED' || $(row).data('status') == 'STARTING' || $(row).data('status') == 'STOPPING') {
                        $('#stop-button').prop('disabled', true);
                        return;
                    }
                });

                $('#switch-button-group, #switch-backup-button, #switch-slave-button, #switch-master-button, #switch-auto-button').prop('disabled', false);
                $.each(selectedRows, function(index, row) {
                    if ($(row).data('status') != 'RUNNING') {
                        $('#switch-button-group, #switch-backup-button, #switch-slave-button, #switch-master-button, #switch-auto-button').prop('disabled', true);
                        return;
                    }
                });
            } else {
                $('#start-button').prop('disabled', true);
                $('#stop-button').prop('disabled', true);
                $('#remove-button').prop('disabled', true);
                $('#switch-button-group, #switch-backup-button, #switch-slave-button, #switch-master-button, #switch-auto-button').prop('disabled', true);
            }

            if (selectedRows.length == 1) {
                $('#copy-button').prop('disabled', false);
                $('#edit-button').prop('disabled', false);
                if (selectedRows.data('status') == 'RUNNING' || selectedRows.data('status') == 'STARTERROR' || selectedRows.data('status') == 'STOPERROR' ||
                    selectedRows.data('status') == 'STARTING' || selectedRows.data('status') == 'STOPPING') {
                    $('#edit-button').prop('disabled', true);
                }
            } else {
                $('#copy-button').prop('disabled', true);
                $('#edit-button').prop('disabled', true);
            }
        };
        
        var reloadTableAll = function () {
            oldTr = null;
            oldRow = null;
            oldThis = null;
            ids_selected = [];
            id_detail = null;
            tableClient.ajax.reload();
            $('#select_all').prop('checked', false);
        }

        var reloadTable = function() {
            oldTr = null;
            oldRow = null;
            oldThis = null;
            tableClient.ajax.reload(null, false);
        }
    };

    var content_statusType = {
        "PENDING": "未开始",
        "STARTING": "启动中",
        "RUNNING": "进行中",
        "STOPPING": "停止中",
        "STOPPED": "已停止",
        "STARTERROR": "启动失败",
        "STOPERROR": "停止失败"};

    handleDatatable($("#content-table"),
        [
            {
                "mDataProp": "id",
                "sWidth": "10px",
                "mRender": function (data, type, row, meta) {
                    return "<input type='checkbox' class='select-item' id='content-row-"+ row.id +"' value='"+row.id+"' data-status='" + row.status +"'/>";
                }
            },
            {
                "className": 'details-control',
                "orderable": false,
                "data": null,
                "defaultContent": '',
                "sWidth": "20px"
            },
            {"mDataProp": "id", "sWidth": "30px"},
            {"mDataProp": "name", "sWidth": "140px"},
            {
                "defaultContent": "",
            },
            {
                "mDataProp": "master.sourceUri",
                "className": 'table_column',
                "mRender": function (data, type, row, meta) {
                    if (row.master != null) {
                        if (row.master.streamType == 1) {
                            return row.master.pushUri;
                        } else {
                            return row.master.sourceUri;
                        }
                    }
                    return "";
                }
            },
            {"mDataProp": "monitorOrgName"},
            {"mDataProp": "monitorUserName"},
            {"mDataProp": "telephone"},
            {
                "defaultContent": "",
                "className": 'text-center'
            }
        ],
        {"queryUrl": "/content/list"}
    );
});