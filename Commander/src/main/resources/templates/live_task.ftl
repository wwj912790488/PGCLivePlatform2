<?xml version="1.0" encoding="UTF-8"?>
<task>
<#if taskName?exists>
    <name>${taskName}</name>
    <encodingoption>Custom</encodingoption>
    <priority>5</priority>
</#if>
    <streams>
<#if outputs?exists>
<#list templates as template>
    <#if template.videoFormat == 'videopassthrough' || template.videoFormat == 'audiopassthrough'>
        <stream id="${template.id?c}"><!-- 透传stream -->
            <videopassthrough/>
            <audiopassthrough/>
        </stream>
    <#else>
        <stream id="${template.id?c}"><!-- 非透传stream -->
            <${template.videoFormat!'h264'}>
            <profile>Main</profile>
            <level>-1</level>
        <#if template.videoWidth?exists && template.videoHeight?exists>
            <width>${template.videoWidth?c}</width>
            <height>${template.videoHeight?c}</height>
        </#if>
            <par>source</par>
            <par_x>-1</par_x>
            <par_y>-1</par_y>
            <#if template.frameRate?exists>
            <fr>non-source</fr>
            <fr_x>${template.frameRate?c}</fr_x>
            <fr_y>1</fr_y>
            <#else>
            <fr>source</fr>
            <fr_x>-1</fr_x>
            <fr_y>-1</fr_y>
            </#if>
            <interpolate>1</interpolate>
            <frameratesourcemode>0</frameratesourcemode>
            <fillonlost>0</fillonlost>
            <rc>VBR</rc>
            <bitrate>${template.videoBitrate?c}</bitrate>
            <#assign maxbitrate = (template.videoBitrate * 1.5)?long>
            <#assign bufsize = (maxbitrate / 8)?long>
            <#assign buffillpct = (bufsize * 8000 / maxbitrate)?long>
            <maxbitrate>${maxbitrate?c}</maxbitrate>
            <cqquantizer>0</cqquantizer>
            <transform8x8>1</transform8x8>
            <intraprediction8x8>1</intraprediction8x8>
            <bufsize>${bufsize?c}</bufsize>
            <buffillpct>${buffillpct?c}</buffillpct>
            <gopsize>100</gopsize>
            <gopmodeid>1</gopmodeid>
            <scd>0</scd>
            <bframe>3</bframe>
            <cabac>1</cabac>
            <loopfilter>1</loopfilter>
            <refframe>1</refframe>
            <interlace>0</interlace>
            <threadcount>4</threadcount>
            <lookheadframe>1</lookheadframe>
            <smartborder>1</smartborder>
            <twopass>0</twopass>
            <deviceid>0</deviceid>
            <qualitylevel>0</qualitylevel>
            <simhd>
                <deblock>0</deblock>
                <deinterlace>2</deinterlace>
                <denoise>0</denoise>
                <denoisemethod>0</denoisemethod>
                <delight>0</delight>
                <sharpen>0</sharpen>
                <antialias>0</antialias>
                <antishaking>-1</antishaking>
                <brightness>0</brightness>
                <contrast>0</contrast>
                <saturation>0</saturation>
                <hue>0</hue>
                <resizealg>3</resizealg>
                <deinterlacealg>3</deinterlacealg>
            </simhd>
            <preprocessor>
        <#if template.logos?exists>
            <#list template.logos as logo>
                <logo>
                    <uri>${logo.uri!""}</uri>
                    <top>${(logo.posY?c)!0}</top>
                    <left>${(logo.posX?c)!0}</left>
                    <opacity>100</opacity>
                    <resize>${(logo.resize?c)!100}</resize>
                    <start>0:0:0:0</start>
                </logo>
            </#list>
        </#if>
                <motioniconbegin></motioniconbegin>
            <#if template.icons?exists>
                <#list template.icons as icon>
                    <motionicon>
                        <posindex>-1</posindex>
                        <name>${icon.id?c}</name>
                        <path>${icon.uri!""}</path>
                        <posx>${(icon.posX?c)!0}</posx>
                        <posy>${(icon.posY?c)!0}</posy>
                        <operate>0</operate>
                        <initialactive>0</initialactive>
                        <framerate>25</framerate>
                        <imageformat>2</imageformat>
                        <isloop>1</isloop>
                        <runtimevisible>-1</runtimevisible>
                        <timeintervalmode>-1</timeintervalmode>
                        <DelayTime>0</DelayTime>
                    </motionicon>
                </#list>
            </#if>
                </preprocessor>
            </${template.videoFormat!'h264'}>
            <${template.audioFormat!'aac'}>
                <profile>LC</profile>
                <channel>2</channel>
                <bitrate>${(template.audioBitrate?c)!'64000'}</bitrate>
                <samplerate>32000</samplerate>
                <volumemode>0</volumemode>
                <balancedb>-30</balancedb>
                <balancelevel>0</balancelevel>
                <boostlevel>0</boostlevel>
                <channelprocessing>None</channelprocessing>
            </${template.audioFormat!'aac'}>
        </stream>
            </#if>
        </#list>
    </#if>
    </streams>
    <inputs>
        <network>
            <protocol>TSOverUDP</protocol>
            <uri>${masterUri!""}</uri>
            <hsyqedit>0</hsyqedit>
            <programid>${(masterProgramId?c)!-1}</programid>
            <audiotrackid>${(masterAudioId?c)!-1}</audiotrackid>
            <subtitleid>${(masterSubtitleId?c)!-2}</subtitleid>
            <distinguishav>0</distinguishav>
            <srcip>${srcip_in!""}</srcip>
            <allowprogramidchange>1</allowprogramidchange>
        <#if slaveUri?exists>
            <candidatelocation>
                <uri>${slaveUri!""}</uri>
                <inputtype>Network</inputtype>
                <programid>${(slaveProgramId?c)!-1}</programid>
                <audiotrackid>${(slaveAudioId?c)!-1}</audiotrackid>
                <subtitleid>${(slaveSubtitleId?c)!-2}</subtitleid>
                <srcip>${srcip_in!""}</srcip>
                <MacAddr></MacAddr>
                <IpAddr></IpAddr>
                <SourceIpAddr></SourceIpAddr>
            </candidatelocation>
        </#if>
            <preprocessor>
                <cropping>
                    <enabled>0</enabled>
                    <top>0</top>
                    <left>0</left>
                    <width>0</width>
                    <height>0</height>
                    <applyallclips>0</applyallclips>
                </cropping>
                <timesliceclipping>
                    <enabled>0</enabled>
                    <trimmed>1</trimmed>
                </timesliceclipping>
                <padding_location>
                    <uri>${backupUri!""}</uri>
                    <inputtype>Network</inputtype>
                    <programid>${(backupProgramId?c)!-1}</programid>
                    <audiotrackid>${(backupAudioId?c)!-1}</audiotrackid>
                    <subtitleid>${(backupSubtitleId?c)!-2}</subtitleid>
                    <srcip>${srcip_in!""}</srcip>
                </padding_location>
                <padding_type>1</padding_type>
                <audiodelay>0</audiodelay>
            </preprocessor>
        </network>
    </inputs>
    <outputgroups>
<#if outputs?exists>
<#list outputs as output>
    <#if output.protocol == "hls">
        <applestreaming><!-- hls_output -->
            <container>HLS</container>
            <description>Live HLS</description>
            <uri>${(output.uri)!""}</uri>
            <targetname>${output.targetName!'index'}</targetname>
            <segmentlength>4</segmentlength>
            <segmentname>${r'${starttime}-${id}-${seq}'}</segmentname>
            <playlistname>${r'${id}'}</playlistname>
            <deleteuploaded>1</deleteuploaded>
            <output streamref="${output.templateId?c}" />
        </applestreaming>
    <#elseif output.protocol == "rtmp">
        <flashstreaming><!-- rtmp_output -->
            <container>RTMP</container>
            <description>Live RTMP</description>
            <uri>${(output.uri)!""}</uri>
            <output streamref="${output.templateId?c}" />
        </flashstreaming>
    <#elseif output.protocol == "udp">
        <udpstreaming>
            <active>1</active>
            <container>UDPOverTS</container>
            <tssetting>
                <servername/>
                <serviceprovider/>
                <serviceid>1</serviceid>
                <totalbitrate/>
                <bitratemode>-1</bitratemode>
                <videodelay/>
                <cutaudio/>
                <audiomergeframe/>
                <pmtpid>${(masterProgramId?c)!'256'}</pmtpid>
                <videopid>${(masterAudioId?c)!'4113'}</videopid>
                <audiopid>${(masterSubtitleId?c)!'4352'}</audiopid>
                <pcrpid/>
                <audioprocessmode>0</audioprocessmode>
            </tssetting>
            <description>Live UDP</description>
            <uri>${(output.uri)!""}</uri>
            <srcip>${srcip_out!""}</srcip>
            <srcport>1234</srcport>
            <buffersize>65535</buffersize>
            <ttl>255</ttl>
            <igmpsourceip/>
            <output streamref="${output.templateId?c}" />
        </udpstreaming>
    </#if>
</#list>
</#if>
    </outputgroups>
</task>