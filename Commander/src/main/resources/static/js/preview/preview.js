/**
 * Created by slw on 2018/3/27.
 */
$(function() {
    //if (flvjs.isSupported()) {
    //    var videoElement = document.getElementById('videoPlayer');
    //    var flvPlayer = flvjs.createPlayer({
    //        type: 'flv',
    //        url: 'http://172.17.230.172/flv/1522138455169'
    //    });
    //    flvPlayer.attachMediaElement(videoElement);
    //    flvPlayer.load();
    //    flvPlayer.play();
    //}

    var param = {};
    param.dom = $("#player-container").get(0);
    param.inputDom = $("#player-path");
    var tmplayer = new TMPlayer(param);
    tmplayer.InitPlayer();
    tmplayer.Play();
    setTimeout(function () {
        tmplayer.UriChange();
    }, 100);


    $("#player-path").on('change', function () {
        tmplayer.UriChange();
    });
});