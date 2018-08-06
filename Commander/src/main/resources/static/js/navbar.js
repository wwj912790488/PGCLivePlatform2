/**
 * Created by yhf on 2018/5/16.
 */
$(function(){
    setMenuHeight();
    setContextHeight();
    initRetractMenu();
    /******layui加载全局皮肤 begin******/
    layer.config({
        skin: 'demo-class'
    })
    /******layui加载全局皮肤 end******/
    $(".retractMenu").click(function() {
        if($('.retractMenu').hasClass("menu-show")) {
            drawMenu();
        } else {
            showMenu();
        }
    });
});

$(window).resize(function(){
    setMenuHeight();
    setContextHeight();
    retractMenu();
});

function initRetractMenu(){
    if(document.body.clientWidth < 1280){
        drawMenu();
    }else {
        showMenu();
    }
}

function retractMenu(){
    if(document.body.clientWidth < 1280){
        if ($(".retractMenu").hasClass("menu-show")) {
            drawMenu();
        }
    }else {
        if (!$(".retractMenu").hasClass("menu-show")) {
            showMenu();
        }
    }
}

function drawMenu(){
    var secondLi = $('.nav-second-li');
    $.each(secondLi, function(index, ele) {
       if ($(ele).hasClass('active')) {
           $(ele).children("a").click();
       }
    });
    $('.menu-name').hide();
    $('.fa.arrow').hide();
    $(".sidebar").animate({width:"60px"},100);
    $(".main-top").animate({paddingLeft:"80px"},100);
    $('.retractMenu').removeClass("menu-show");
    initMouseoverMenu();
    $(".nav-second-li > a").css('pointer-events', 'none');//去掉a标签中的onclick事件
}

function showMenu(){
    $('.retractMenu').addClass("menu-show");
    unMouseoverMenu();
    $(".main-top").animate({paddingLeft:"185px"},100,"swing",function(){
        $(".sidebar").animate({width:"165px"},0);
        $('.menu-name').show();
        $('.fa.arrow').show();
        var url = window.location;
        var element = $('.sidebar ul.nav a').filter(function() {
            var index = url.href.indexOf(this.href);
            return index == 0;
        }).addClass('active').parent();

        var secondLi = $(element).closest(".nav-second-li");
        if (secondLi.length > 0 && !$(secondLi).hasClass('active')) {
            secondLi.children("a").click();
        }

        $(".nav-second-li > a").css('pointer-events', 'auto');
    });
}

/**
 * 绑定菜单栏鼠标经过事件
 * @returns
 */
function initMouseoverMenu(){
    $(".nav-second-li").mouseover(function(){
        var curMenuObj = $(this);
        curMenuObj.children("ul").show();
        curMenuObj.find('.menu-name').show();
        curMenuObj.children("ul").addClass("floatMenuUL");
        curMenuObj.addClass("floatMenuLi");
    });

    $(".nav-second-li").mouseout(function() {
        var curMenuObj = $(this);
        curMenuObj.removeClass("floatMenuLi");
        curMenuObj.children("ul").removeClass("floatMenuUL");
        curMenuObj.children("ul").css('display','');
        curMenuObj.find('.menu-name').hide();
    });
}

/**
 * 解除绑定菜单栏鼠标经过事件
 * @returns
 */
function unMouseoverMenu(){
    $(".nav-second-li").unbind("mouseover");
    $(".nav-second-li").unbind("mouseout");
}

function setContextHeight() {
    var bk_div_height = $(window).height() - 51;
    //$('#page-wrapper').css('min-height', bk_div_height);
    $('#dashboard_bk_div').css('min-height', $(document).height());
    //$('#dashboard_bk_div').css('min-width', $(document).width());
}

function setMenuHeight(){
    var path = window.location.pathname;
    if (path === '/dashboard') {
        var navbar_bk_div_height = $(document).height() - 51;
        $('#navbar_bk_div').height(navbar_bk_div_height);
        //$('#page-wrapper').height(navbar_bk_div_height);
    }else{
        var navbar_bk_div_height = $(window).height() - 51;
        $('#navbar_bk_div').height(navbar_bk_div_height);
        //$('.main-top').width($(document).width()-$('#navbar_bk_div').width()-30);
        $('.main-top').height(navbar_bk_div_height);
    }

    if(document.body.clientWidth<768){
        $('.user-info').hide();
    }else {
        $('.user-info').show();
    }
    $('#set-admin').click(function() {
        bootbox.prompt({
            size: "small",
            title: "请输入超级管理员密码!",
            inputType: 'password',
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
            callback: function(result){
                if (result) {
                    $.ajax({
                        url: "/user/set",
                        type: 'POST',
                        dataType: 'json',
                        data:{adminPassword:result},
                        cache: false,
                        success: function (data) {
                            if (data.code === 0) {
                                bootbox.alert({
                                    message:"成为超管，请重新登录!",
                                    buttons: {
                                        ok:{label:'确认',className:'btn-primary'}
                                    },
                                    callback: function (result) {
                                        window.location.href = "/logout";
                                    }
                                });
                            } else {
                                var msg = data.message;
                                var dialog = bootbox.dialog({message: '<div class="text-center"><i class="fa fa-spin fa-spinner"></i>' + msg + '</div>'});
                                setTimeout(function(){
                                    dialog.modal('hide');
                                }, 1000);
                            }

                        },
                        error: function(e) {

                        }
                    });
                }

            }
        });
    });

    $('#change-button').click(function() {
        layer.open({
            type: 2,
            title: '修改密码',
            //shift: 5,
            shadeClose: true,
            //shade: 0.8,
            area: ['420px', '250px'],
            content: '/user/selfPassword'
        });
    });
}