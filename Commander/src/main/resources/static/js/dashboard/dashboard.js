/**
 * Created by zfl on 2018/7/23.
 */
$(function(){
    $.ajax({
        url: "/dashboard/dashboardInfo",
        type: 'GET',
        dataType: 'json',
        cache: false,
        success: function (data) {
            if(data.code == 0){
                var connectHtml = "";
                var serverInfos = data.data.connectInfo.serverInfos;
                for(var i=0;i<serverInfos.length;i++){
                    var imgUrl = "";
                    if(serverInfos[i].connectType == "CONNECTED"){
                        imgUrl = '/images/Concatenation.png';
                    }else if(serverInfos[i].connectType == "NOT_INSTALLED"){
                        imgUrl = '/images/Notinstalled.png';
                    }else if(serverInfos[i].connectType == "OPENING"){
                        imgUrl = '/images/Opening.png';
                    }
                    connectHtml+="<div class='transcoder_div'>";
                    connectHtml+="<span><img src='"+imgUrl+"' class='transcoder_img'></span>";
                    connectHtml+="<span><div>"+ServerType[serverInfos[i].serverType]+"</div></span>";
                    connectHtml+="</div>";
                }
                $("#connect-info").html(connectHtml);

                $("#contentCount").html(data.data.contentInfo.total);
                $("#contentNormalCount").html(data.data.contentInfo.normalCount);
                $("#contentCompleteCount").html(data.data.contentInfo.completeCount);
                $("#contentAlertCount").html(data.data.contentInfo.alertCount);

                $("#recordCount").html(data.data.recordInfo.total);
                $("#recordNormalCount").html(data.data.recordInfo.normalCount);
                $("#recordCompleteCount").html(data.data.recordInfo.completeCount);
                $("#recordAlertCount").html(data.data.recordInfo.alertCount);

                var alertHtml = "";
                var alertInfo = data.data.alertInfo.alerts;
                for(var i=0;i<alertInfo.length;i++){
                    var imgUrl = "";
                    if(alertInfo[i].level == "NOTIFY"){
                        imgUrl = '/images/one.png';
                    }else if(alertInfo[i].level == "WARNING"){
                        imgUrl = '/images/two.png';
                    }else if(alertInfo[i].level == "ERROR"){
                        imgUrl = '/images/three.png';
                    }

                    var createTime =moment(alertInfo[i].createdAt).isValid() ? moment(alertInfo[i].createdAt).format('YYYY-MM-DD HH:mm:ss') : "";
                    alertHtml+="<div><div class='statistics_alarm_text_div'>告警类型";
                    alertHtml+="<span><img src='"+imgUrl+"'></span>";
                    alertHtml+="<div class='statistics_alarm_time_div'>告警时间："+createTime+"</div>";
                    alertHtml+="<div class='statistics_alarm_content_div'>IP地址："+alertInfo[i].ip+"</div>";
                    alertHtml+="<div class='statistics_alarm_content_div'>告警描述："+alertInfo[i].description+"</div>";
                    alertHtml+="</div><div class='statistics_hr_div'><hr class='statistics_alarm_hr' color='#252932'/></div></div>"
                }

                $("#alert-info").html(alertHtml);
            }
        }
    });

});
