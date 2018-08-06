
$(function () {
    //
    var ids_selected = [];
    var one_selected = {};

    var random = 10000;
    var columns = [
        {"mDataProp": "id", "width": "10px"},
        {"mDataProp": "id"},
        {"mDataProp": "name"},
        {"mDataProp": "displayName"},
        {"mDataProp": "resolution"},
        {"mDataProp": "videoFormat"},
        {"mDataProp":"frameRate"},
        {"mDataProp": "videoBitrate"},
        {"mDataProp": "audioFormat"},
        {"mDataProp": "audioBitrate"},
        {"mDataProp": "type"}

    ]

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

    var tempTables = $("#template-table").DataTable({
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
        "sAjaxSource": "/template/list",
        "aoColumns": columns,
        "fnServerData":function (sSource, aoData, fnCallback) {
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
        "fnServerParams":function (aoData) {
            var keyword = $("#content-form").find("[name = keyword]").val();
            aoData.push({"name" : "keyword", "value" : keyword});
        },
        "columnDefs":[
            {
            "targets": [0],
            "render": function(data, type, row, meta) {
                return "<input type='checkbox' class='select-item' value='" + row.id + "' data-type='" + row.type + "'/>";
             }
            },
            {
                "targets": [4],
                "render": function(data, type, row, meta) {
                    return row.videoWidth +"x"+row.videoHeight;
                }
            },
            {
                "targets": [10],
                "render": function(data, type, row, meta) {
                    return row.type == "1"? "默认模板":"自定义模板";
                }
            }

        ],
        "createdRow":function (row, data, dataIndex) {
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

                one_selected = data;

                updateCtrlBtnUI();
            });
        }

    }).on('page.dt',function () {
        ids_selected = [];
        one_selected = {};
    });

    $('#search-button').click(function() {
        ids_selected = [];
        one_selected = {};
        ids_shown = [];
        tempTables.ajax.reload();
        $('#select_all').prop('checked', false);
    });

    $('#template-add').on('hidden.bs.modal',function (e) {
        $("#content-panl").html('');
    })

    $('#create').click(function() {
        ids_selected = [];
        one_selected = {};
        tempTables.ajax.reload();
        $('#select_all').prop('checked', false);
        var newData = {};
        newData.indexs = "random" + random++;
        newData.videoWidth = "";
        newData.videoHeight = "";


        $('#modelTitle').html('添加模板');
        $("#content-panl").html($("<div/>").handlebars($("#content_template_detail"), newData));
        $('#template-add').modal({backdrop: 'static', keyboard: false});
        $('#template-add').modal('show');

        initFormSubmit(newData,"/template/add");

    });

    $('#edit').click(function() {

        var selected = $(".select-item:checked");
        var ids = [];
        $.each(selected, function (index, obj) {
            ids.push($(obj).val());
        });
        if (ids.length == 0) {
            bootbox.alert("请选择一条记录");
            return false;
        }

        if (ids.length > 1) {
            bootbox.alert("只能选择一条数据编辑");
            return false;
        }
        var tempdata = one_selected  ;
        tempdata.indexs = tempdata.id ;

        $('#modelTitle').html('编辑模板');

        $("#content-panl").html($("<div/>").handlebars($("#content_template_detail"), tempdata));
        //init data
        $("#Resolution-" + tempdata.id + "").val(13);
        $("#audioBitrate").find("option[text='" + tempdata.audioBitrate + "']").attr("selected", true);

        $("#frameRate").find("option[value='" + tempdata.frameRate + "']").attr("selected", true);
        $('#template-add').modal({backdrop: 'static', keyboard: false});
        $('#template-add').modal('show');

        initFormSubmit(tempdata,"/template/update");

        console.log(tempTables);
        console.log(tempTables.data());

    });

    $("#remove").click(function () {
        var selected = $(".select-item:checked");
        var ids = [];
        $.each(selected, function (index, obj) {
            ids.push($(obj).val());
        });
        if (ids.length == 0) {
            bootbox.alert("请选择一条记录");
            return false;
        };

        bootbox.confirm({message: "确定要删除模板？", callback: function(result) {
            if (result == true) {
                $.ajax({
                    url: "/template/delete",
                    type: 'POST',
                    dataType: 'json',
                    data: {ids : ids.toString()},
                    cache: false,
                    success: function (data) {
                        if(data.code === 0){
                            tempTables.ajax.reload();
                        }else{
                            bootbox.alert(data.message);
                        }

                    },
                    error: function(e) {

                    }
                });
            }
        }});
        
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

    var initFormSubmit = function (data,url) {
        $("#form-" + data.indexs).bootstrapValidator({
            live: 'enabled',
            submitButtons: 'button[type="submit"]',
            message: '输入值格式不正确',
            fields: {
                name:{
                    validators:{
                        notEmpty:{
                            message:"名称不能为空"
                        },
                        stringLength: {
                            max: 10,
                            message:'最多10个字符'
                        }
                    }
                },
                displayName:{
                    validators:{
                        stringLength: {
                            max: 10,
                            message:'最多10个字符'
                        }
                    }
                },
                videoBitrate: {
                    validators:{
                      notEmpty:{
                          message:"码率不能为空"
                      },
                      digits:{
                          message:"码率必须为数字"
                      }
                    }
                },
                videoWidth: {
                    trigger:"change",
                    validators:{
                        notEmpty:{
                            message:"分辨率宽不能为空"
                        },
                        digits:{
                            message:"分辨率宽必须为数字"
                        }
                    }
                },
                videoHeight: {
                    trigger:"change",
                    validators:{
                        notEmpty:{
                            message:"分辨率高不能为空"
                        },
                        digits:{
                            message:"分辨率高必须为数字"
                        }
                    }
                },
                audioBitrate: {
                    validators:{
                        notEmpty:{
                            message:"音频码率不能为空"
                        },
                        digits:{
                            message:"音频码率必须为数字"
                        }
                    }
                }
            },
            submitHandler:  function (validator, form, submitButton) {

                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType: 'json',
                    data: $("#form-" + data.indexs).serialize(),
                    cache: false,
                    success: function (data) {
                        if(data.code === 0){
                            $("#content-panl").html('');
                            $('#template-add').modal('hide');
                            tempTables.ajax.reload();
                        }else{
                            bootbox.alert(data.message);
                        }

                    },
                    error: function(e) {

                    }
                });
            }
        });
    }

    var updateCtrlBtnUI = function () {

        var selectedRows = $(".select-item:checked");
        var hasDefaultTemp = false ;
        if (selectedRows.length > 0) {
            $('#remove').prop('disabled', false);

            if(selectedRows.length > 1){
                $('#edit').prop('disabled', true);
            }else{
                $('#edit').prop('disabled', false);
            }

            $.each(selectedRows, function(index, row) {
                //选中有默认模板时,禁止编辑
                if ($(row).data('type') === 1) {
                    hasDefaultTemp = true ;
                    return false ;
                }
            });

            if(hasDefaultTemp){
                $('#remove').prop('disabled', true);
                $('#edit').prop('disabled', true);
            }

        } else {
            $('#edit').prop('disabled', true);
            $('#remove').prop('disabled', true);
        }
    };

})

var  initSizeChange = function(id) {
    var b = $("#Resolution-" + id + "").val(),
        c = $("#Resolution-" + id + "").find("option:selected"),
        d = c.attr("pwidth"),
        e = c.attr("pheight"),
        f = 11 == b ? !1 : !0;


    $("#width1-" + id + " ,#height1-" + id + "").prop("readonly", f);
    $("#width1-" + id).val(d).change();
    $("#height1-" + id ).val(e).change();
     $("#form-" + id).data('bootstrapValidator').resetForm();

}