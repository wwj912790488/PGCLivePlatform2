/**
 * Created by zfl on 2018/4/17.
 */
$(document).ready(function () {
    var recordAddress = $("#recordAddress").val();
    if(recordAddress!=null){
        $("#record_frame").attr("src",recordAddress+"/FramelistProfile.action");
    }
});