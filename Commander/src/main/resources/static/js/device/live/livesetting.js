$(document).ready(function () {
    $("#live-setting-server a").on( 'shown.bs.tab', function () {
        var liveAddress = $("#liveAddress").val();
        if(liveAddress!=null){
            /*$.ajax({
                type: "GET",
                url: liveAddress+"/#listServer.action?type=0",
                dataType:"jsonp",
                beforeSend: function(xhr, settings){
                    xhr.setRequestHeader("SM_USER", "Admin");},
                success: function(data){

                    /!*var iframe = document.getElementById('live_frame');
                     iframe.contentWindow.contents = data;
                     iframe.src = 'javascript:window["contents"]';*!/

                    $("#live_frame").attr('src',"data:text/html;charset=utf-8," + data)
                },
                error: function(data){
                    alert("error");
                }
            });*/

            $("#live_frame").attr("src",liveAddress+"/#listServer.action?type=0");
        }
    });

});