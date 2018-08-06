/**
 * Created by slw on 2018/3/20.
 */
$(function() {
    $('#side-menu').metisMenu();

    var url = window.location;

    var element = $('.sidebar ul.nav a').filter(function() {
        var index = url.href.indexOf(this.href);
        return index == 0;
    }).addClass('active').parent();

    while (true) {
        if (element.is('li')) {
            element = element.parent().addClass('in').parent().addClass('active');
        } else {
            break;
        }
    };


});