var initFolderSelector = function () {
    $('#jstree_div').jstree({
        "core": {
            'force_text': true,
            "animation": 0,
            "check_callback": true,
            "themes": {"stripes": true, "icons": true},
        },
        "types": {
            "#": {
                "max_children": 2000,
                "max_depth": 2000,
                "valid_children": ["root"]
            },
            "root": {

                "valid_children": ["default"]
            },
            "default": {
                "valid_children": ["default", "file"]
            },
            "file": {
                "valid_children": []
            }
        },
        'contextmenu': {
            'items': function (node) {
                return {
                    "create_folder": {
                        "separator_before": false,
                        "separator_after": true,
                        "_disabled": false, //(this.check("create_node", data.reference, {}, "last")),
                        "label": "新建目录",
                        "action": function (data) {
                            var inst = $.jstree.reference(data.reference),
                                obj = inst.get_node(data.reference);
                            inst.create_node(obj, {}, "last", function (new_node) {
                                setTimeout(function () {
                                    inst.edit(new_node);
                                }, 0);
                            });
                        }
                    },
                };
            }
        },

        "plugins": [
            "contextmenu", "types", "themes", "json_data"/* "dnd", "search", "state", "types", "wholerow",*/
        ],
    }).bind("rename_node.jstree",function(event,data){
        var pathparent = data.node.parent;
        var foldername = data.text;
        $.ajax({
            url: "/path/createfolder",
            type: 'POST',
            dataType: 'text',
            data:{path:pathparent, name:foldername},
            success: function (data) {
                if (data == "error") {
                    alert("创建文件夹失败！文件夹可能已存在！");
                }
            },
            error: function (data) {
                   alert("创建文件夹失败！文件夹可能已存在！");
            },
        });
        $('#jstree_div').jstree(true).refresh();
    });

    $('#jstree_div').on("changed.jstree", function (e, data) {
        $('#select-path-modal-title span').html(data.selected);
    });

};

var handleFolderSelector = function (foldermode, queryurl, selectedCallback,path) {
    $('#jstree_div').jstree(true).settings.core.data = {
        'url': queryurl,
        'type': 'POST',
        'data': function (node) {
            if (node.id == '#') {
                return {"foldermode": foldermode, 'id': '', 'path': path};
            }
            else {
                return {"foldermode": foldermode, 'id': node.id, 'path': path};
            }
        }
    };
    $('#jstree_div').jstree(true).refresh();
    $('#select-path-modal').modal()
        .one('click', '#openfile-ok', function (e) {
            var path = $('#select-path-modal-title span').html();
            selectedCallback(path);
        });


};
