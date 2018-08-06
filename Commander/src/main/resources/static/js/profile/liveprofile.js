/**
 * Created by zfl on 2018/4/17.
 */
$(document).ready(function () {
    var liveAddress = $("#liveAddress").val();
    if(liveAddress!=null){
        $("#live_frame").attr("src",liveAddress+"/listProfile.action");
    }
});