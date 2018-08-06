$(document).ready(function() {
    var _interval;

    _interval = setInterval(function () {
        $.ajax({
            url: "/alert/message",
            type: "POST",
            dataType: "json",
            success: function (data) {
                if (data != null && data != undefined) {
                    Messenger({
                        extraClasses: 'messenger-fixed messenger-theme-air  messenger-on-top messenger-on-right'
                    }).post({
                        message: data.description,
                        type: "error",
                        hideAfter: 30,
                        showCloseButton: true,
                        maxMessages:5
                    });
                }
            }
        });
    }, 20000);

});
