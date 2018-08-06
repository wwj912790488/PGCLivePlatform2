$(function () {

    var forTB = $("#forTB").val();
    if(forTB == "true"){
        $("#addButton").hide();
        $.ajax({
            url: "/supervisor/supervisorScreens",
            type: 'GET',
            dataType: 'json',
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    var result = data.data;

                    var totalWidth = 415;
                    var totalHeight = 240;
                    //var templateTypes = ["1*1","3*2","3*3","4*3","3*4"];

                    var screens = new Array();
                    for(var i=0;i<result.length;i++){
                        var templateType = result[i].templateType;
                        if(templateType == null || templateType == ""){
                            templateType = "1*1";
                        }
                        var a = templateType.split("*");
                        var line = a[0];
                        var row = a[1];
                        var width = totalWidth/line+"px";
                        var height = totalHeight/row+"px";
                        var count = line*row;

                        var screenInfos = new Array();

                        var info = result[i].screenInfos;
                        for(var j=0;j<count;j++){
                            var screenInfo = {};
                            if(count>1){
                                screenInfo.posIdx = j;
                                screenInfo.screenTitle = "";
                            }
                            screenInfo.width = width;
                            screenInfo.height = height;
                            for(var k=0;k<info.length;k++){
                                if(info[k].posIdx == j){
                                    screenInfo.contentId = info[k].contentId;
                                    screenInfo.sourceFrom = info[k].sourceFrom;
                                    screenInfo.screenTitle = info[k].screenTitle;
                                }
                            }
                            screenInfos.push(screenInfo);
                        }

                        var obj = {};
                        obj.id = result[i].id;
                        obj.deviceId = result[i].deviceId;
                        obj.screenInfos = screenInfos;
                        obj.name = (result[i].name==null?(i+1)+"号大屏":result[i].name);
                        screens.push(obj);
                    }

                    $("#screens").handlebars($("#screen_template"), screens);

                    $(".item").each(function(){
                        var title = $(this).text().trim();
                        if(title == ""){
                            $(this).addClass("item-content");
                            //$(this).style.backgroundImage = "./images/default/add_h.png";
                        }else {
                            $(this).hover(function () {
                                var hoverContent = $(this).children('.hover-content');
                                hoverContent.fadeIn();
                            }, function () {
                                var hoverContent = $(this).children('.hover-content');
                                hoverContent.fadeOut();
                            });
                        }
                    });

                    $(".wrap .item-info").click(function(){
                        var screenId = $(this).parents(".wrap").find("input[name='screenId']").val();
                        var contentId = $(this).children("input[name='contentId']").val();
                        if(contentId!=""){
                            return;
                        }
                        var posIdx = $(this).children("input[name='posIdx']").val();
                        if(posIdx==""){
                            layer.alert("请先配置大屏信息!");
                            return;
                        }
                        layer.open({
                            type: 2,
                            title: '添加活动',
                            //shift: 5,
                            shadeClose: true,
                            //shade: 0.8,
                            area: ['420px', '420px'],
                            content: '/supervisor/screenItemSettings?screenId='+screenId+"&posIdx="+posIdx //iframe的url
                        });
                    });

                    $(".wrap .item-info .item-edit").click(function(){
                        var screenId = $(this).parents(".wrap").find("input[name='screenId']").val();
                        var posIdx = $(this).parents(".item-info").children("input[name='posIdx']").val();
                        if(posIdx==""){
                            layer.alert("请先配置大屏信息!");
                            return;
                        }
                        layer.open({
                            type: 2,
                            title: '添加活动',
                            //shift: 5,
                            shadeClose: true,
                            //shade: 0.8,
                            area: ['420px', '420px'],
                            content: '/supervisor/screenItemSettings?screenId='+screenId+"&posIdx="+posIdx //iframe的url
                        });
                    });

                    $(".wrap .item-info .item-delete").click(function(){
                        var screenId = $(this).parents(".wrap").find("input[name='screenId']").val();
                        var posIdx = $(this).parents(".item-info").children("input[name='posIdx']").val();
                        $.ajax({
                            url: "/supervisor/itemSettings/delete",
                            type: 'POST',
                            dataType: 'json',
                            data: {screenId : screenId,posIdx:posIdx},
                            cache: false,
                            success: function (data) {
                                window.location.href="/supervisor"
                            },
                            error: function(e) {

                            }
                        });
                    });

                    $(".setting-btn").click(function(){
                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();

                        layer.open({
                            type: 2,
                            title: '监看设置',
                            //shift: 5,
                            shadeClose: true,
                            //shade: 0.8,
                            area: ['420px', '420px'],
                            content: '/supervisor/screenSettings?screenId='+id //iframe的url
                        });
                    });

                    $(".start-btn").click(function(){
                        var canStart = false;
                        $(".item-info").each(function () {
                            var contentId = $(this).find("input[name='contentId']").val();
                            if(contentId!=null && contentId!=""){
                                canStart =true;
                            }
                        });
                        if(!canStart){
                            layer.alert("请先配置活动信息!");
                            return;
                        }
                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();

                        $.ajax({
                            url: "/supervisor/startSupervisorTask",
                            type: 'POST',
                            dataType: 'json',
                            data: {id : id},
                            cache: false,
                            success: function (data) {
                                window.location.href="/supervisor"
                            },
                            error: function(e) {

                            }
                        });
                    });

                    $(".stop-btn").click(function(){

                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();

                        $.ajax({
                            url: "/supervisor/stopSupervisorTask",
                            type: 'POST',
                            dataType: 'json',
                            data: {id : id},
                            cache: false,
                            success: function (data) {
                                window.location.href="/supervisor"
                            },
                            error: function(e) {

                            }
                        });
                    });

                    $(".remove-btn").click(function(){
                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();

                        bootbox.confirm({message: "确定要删除？", callback: function(result) {
                            if (result == true) {
                                $.ajax({
                                    url: "/supervisor/delete",
                                    type: 'POST',
                                    dataType: 'json',
                                    data: {id : id},
                                    cache: false,
                                    success: function (data) {
                                        window.location.href="/supervisor"
                                    },
                                    error: function(e) {

                                    }
                                });
                            }
                        }});
                    });

                    function updateTaskStatus(){
                        $.ajax({
                            url: "/supervisor/task/status",
                            type: 'GET',
                            cache: false,
                            success: function (data) {
                                if(data.code == 0){
                                    var tasks = data.data;
                                    for(var i =0;i<tasks.length;i++){
                                        var deviceId = tasks[i].deviceId;
                                        var status = tasks[i].supervisorTaskStatus;
                                        var lastAlert = tasks[i].lastAlert;

                                        $(".wrap").each(function(){
                                            if($(this).find("input[name='deviceId']").val() == deviceId){
                                                $(this).find(".TaskStatus").prop('title', "");
                                                if(status == '错误'){
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGreen").removeClass("TaskStatusGrey").removeClass("TaskStatusOrange").addClass("TaskStatusRed");
                                                }else if(lastAlert!=null){
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGreen").removeClass("TaskStatusGrey").removeClass("TaskStatusRed").addClass("TaskStatusOrange");
                                                    $(this).find(".TaskStatus").prop('title', lastAlert);
                                                } else if(status == '运行'){
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGrey").removeClass("TaskStatusRed").removeClass("TaskStatusOrange").addClass("TaskStatusGreen");
                                                }else {
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGreen").removeClass("TaskStatusRed").removeClass("TaskStatusOrange").addClass("TaskStatusGrey");
                                                }
                                            }
                                        });
                                    }
                                }
                            },
                            error: function(e) {

                            }
                        });
                    };

                    updateTaskStatus();

                    var myTimeOut = false;
                    function setMyInterval(func, delay) {
                        if(myTimeOut) return;
                        func();
                        setTimeout(function () {
                            setMyInterval(func, delay)
                        }, delay);
                    }

                    setMyInterval(function(){
                        updateTaskStatus();
                    },5000);

                } else {
                    bootbox.alert({
                        title: "获取监看信息失败！",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "获取监看信息错误！",
                    message: e
                });
            }
        });
    }else {
        $("#addButton").show();

        $.ajax({
            url: "/supervisor/supervisorScreens",
            type: 'GET',
            dataType: 'json',
            cache: false,
            success: function (data) {
                if (data.code == 0) {
                    var result = data.data;

                    var totalWidth = 415;
                    var totalHeight = 240;
                    //var templateTypes = ["1*1","3*2","3*3","4*3","3*4"];

                    var screens = new Array();
                    for(var i=0;i<result.length;i++){
                        var templateType = result[i].templateType;
                        if(templateType == null || templateType == ""){
                            templateType = "1*1";
                        }
                        var a = templateType.split("*");
                        var line = a[0];
                        var row = a[1];
                        var width = totalWidth/line+"px";
                        var height = totalHeight/row+"px";
                        var count = line*row;

                        var screenInfos = new Array();

                        var info = result[i].screenInfos;
                        for(var j=0;j<count;j++){
                            var screenInfo = {};
                            if(count>1){
                                screenInfo.posIdx = j;
                                screenInfo.screenTitle = "";
                            }
                            screenInfo.width = width;
                            screenInfo.height = height;
                            for(var k=0;k<info.length;k++){
                                if(info[k].posIdx == j){
                                    screenInfo.contentId = info[k].contentId;
                                    screenInfo.sourceFrom = info[k].sourceFrom;
                                    screenInfo.screenTitle = info[k].screenTitle;
                                }
                            }
                            screenInfos.push(screenInfo);
                        }

                        var obj = {};
                        obj.id = result[i].id;
                        obj.deviceId = result[i].deviceId;
                        obj.screenInfos = screenInfos;
                        obj.name = (result[i].name==null?(i+1)+"号大屏":result[i].name);
                        obj.bind = result[i].bind;
                        screens.push(obj);
                    }

                    $("#screens").handlebars($("#screen_template"), screens);

                    $(".item").each(function(){
                        var title = $(this).text().trim();
                        if(title == ""){
                            $(this).addClass("item-content");
                            //$(this).style.backgroundImage = "./images/default/add_h.png";
                        }else {
                            $(this).hover(function () {
                                var hoverContent = $(this).children('.hover-content');
                                hoverContent.fadeIn();
                            }, function () {
                                var hoverContent = $(this).children('.hover-content');
                                hoverContent.fadeOut();
                            });
                        }
                    });

                    $(".wrap .item-info").click(function(){
                        var screenId = $(this).parents(".wrap").find("input[name='screenId']").val();
                        var contentId = $(this).children("input[name='contentId']").val();
                        if(contentId!=""){
                            return;
                        }
                        var posIdx = $(this).children("input[name='posIdx']").val();
                        if(posIdx==""){
                            layer.alert("请先配置大屏信息!");
                            return;
                        }
                        layer.open({
                            type: 2,
                            title: '添加活动',
                            //shift: 5,
                            shadeClose: true,
                            //shade: 0.8,
                            area: ['420px', '420px'],
                            content: '/supervisor/screenItemSettings?screenId='+screenId+"&posIdx="+posIdx //iframe的url
                        });
                    });

                    $(".wrap .item-info .item-edit").click(function(){
                        var screenId = $(this).parents(".wrap").find("input[name='screenId']").val();
                        var posIdx = $(this).parents(".item-info").children("input[name='posIdx']").val();
                        if(posIdx==""){
                            layer.alert("请先配置大屏信息!");
                            return;
                        }
                        layer.open({
                            type: 2,
                            title: '添加活动',
                            //shift: 5,
                            shadeClose: true,
                            //shade: 0.8,
                            area: ['420px', '420px'],
                            content: '/supervisor/screenItemSettings?screenId='+screenId+"&posIdx="+posIdx //iframe的url
                        });
                    });

                    $(".wrap .item-info .item-delete").click(function(){
                        var screenId = $(this).parents(".wrap").find("input[name='screenId']").val();
                        var posIdx = $(this).parents(".item-info").children("input[name='posIdx']").val();
                        $.ajax({
                            url: "/supervisor/itemSettings/delete",
                            type: 'POST',
                            dataType: 'json',
                            data: {screenId : screenId,posIdx:posIdx},
                            cache: false,
                            success: function (data) {
                                window.location.href="/supervisor"
                            },
                            error: function(e) {

                            }
                        });
                    });

                    $(".setting-btn").click(function(){
                        var bind = $(this).parents(".wrap").find("input[name='bind']").val();
                        if(bind == "true"){
                            layer.alert("请先停止任务!");
                            return;
                        }
                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();

                        layer.open({
                            type: 2,
                            title: '监看设置',
                            //shift: 5,
                            shadeClose: true,
                            //shade: 0.8,
                            area: ['420px', '420px'],
                            content: '/supervisor/editScreen?screenId='+id //iframe的url
                        });
                    });

                    $(".start-btn").click(function(){
                        var canStart = false;
                        $(this).parents(".wrap").find(".item-info").each(function () {
                            var contentId = $(this).find("input[name='contentId']").val();
                            if(contentId!=null && contentId!=""){
                                canStart =true;
                            }
                        });

                        if(!canStart){
                            layer.alert("请先配置活动信息!");
                            return;
                        }

                        var statusSpan = $(this).parent().children(":first");
                        if(!statusSpan.hasClass("TaskStatusGrey")){
                            canStart = false;
                        }

                        if(!canStart){
                            layer.alert("任务正在运行");
                            return;
                        }
                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();

                        $.ajax({
                            url: "/supervisor/startSupervisorTask",
                            type: 'POST',
                            dataType: 'json',
                            data: {id : id},
                            cache: false,
                            success: function (data) {
                                window.location.href="/supervisor"
                            },
                            error: function(e) {

                            }
                        });
                    });

                    $(".stop-btn").click(function(){
                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();
                        $.ajax({
                            url: "/supervisor/stopSupervisorTask",
                            type: 'POST',
                            dataType: 'json',
                            data: {id : id},
                            cache: false,
                            success: function (data) {
                                window.location.href="/supervisor"
                            },
                            error: function(e) {

                            }
                        });
                    });

                    $(".remove-btn").click(function(){

                        var canDelete = false;
                        var statusSpan = $(this).parent().children(":first");
                        if(statusSpan.hasClass("TaskStatusGrey")){
                            canDelete = true;
                        }
                        if(!canDelete){
                            layer.alert("请先停止任务");
                            return;
                        }
                        var id = $(this).parents(".wrap").find("input[name='screenId']").val();

                        bootbox.confirm({message: "确定要删除？", callback: function(result) {
                            if (result == true) {
                                $.ajax({
                                    url: "/supervisor/delete",
                                    type: 'POST',
                                    dataType: 'json',
                                    data: {id : id},
                                    cache: false,
                                    success: function (data) {
                                        window.location.href="/supervisor"
                                    },
                                    error: function(e) {

                                    }
                                });
                            }
                        }});
                    });

                    function updateTaskStatus(){
                        $.ajax({
                            url: "/supervisor/task/status",
                            type: 'GET',
                            cache: false,
                            success: function (data) {
                                if(data.code == 0){
                                    var tasks = data.data;
                                    for(var i =0;i<tasks.length;i++){
                                        var screenId = tasks[i].screenId;
                                        var status = tasks[i].supervisorTaskStatus;
                                        var lastAlert = tasks[i].lastAlert;

                                        $(".wrap").each(function(){
                                            var id = $(this).find("input[name='screenId']").val();
                                            if(id == screenId){
                                                $(this).find(".TaskStatus").prop('title', "");
                                                if(status == '错误'){
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGreen").removeClass("TaskStatusGrey").removeClass("TaskStatusOrange").addClass("TaskStatusRed");
                                                }else if(lastAlert!=null){
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGreen").removeClass("TaskStatusGrey").removeClass("TaskStatusRed").addClass("TaskStatusOrange");
                                                    $(this).find(".TaskStatus").prop('title', lastAlert);
                                                } else if(status == '运行'){
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGrey").removeClass("TaskStatusRed").removeClass("TaskStatusOrange").addClass("TaskStatusGreen");
                                                }else {
                                                    $(this).find(".TaskStatus").removeClass("TaskStatusGreen").removeClass("TaskStatusRed").removeClass("TaskStatusOrange").addClass("TaskStatusGrey");
                                                }
                                            }
                                        });
                                    }
                                }
                            },
                            error: function(e) {

                            }
                        });
                    };

                    updateTaskStatus();

                    var myTimeOut = false;
                    function setMyInterval(func, delay) {
                        if(myTimeOut) return;
                        func();
                        setTimeout(function () {
                            setMyInterval(func, delay)
                        }, delay);
                    }

                    setMyInterval(function(){
                        updateTaskStatus();
                    },5000);

                } else {
                    bootbox.alert({
                        title: "获取监看信息失败！",
                        message: data.message
                    });
                }
            },
            error: function(e) {
                bootbox.alert({
                    title: "获取监看信息错误！",
                    message: e
                });
            }
        });

        $("#add-screen-button").click(function(){

            $.ajax({
                url: "/supervisor/capacityValidate",
                type: 'GET',
                cache: false,
                success: function (data) {
                    if(data.code == 0){
                        layer.open({
                            type: 2,
                            title: '新增大屏',
                            //shift: 5,
                            shadeClose: true,
                            //shade: 0.8,
                            area: ['420px', '420px'],
                            content: '/supervisor/addScreen'
                        });
                    }else {
                        bootbox.alert({
                            message: data.message
                        });
                    }
                },
                error: function(e) {

                }
            });


        });
    }

});