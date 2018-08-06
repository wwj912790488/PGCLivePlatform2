<?xml version="1.0" encoding="UTF-8"?>
<task>
<#if taskName?exists>
    <name>${taskName}</name>
    <encodingoption>Custom</encodingoption>
    <priority>5</priority>
</#if>
    <streams>
        <stream id="${taskId?c}"><!-- 透传stream -->
            <videopassthrough/>
            <audiopassthrough/>
        </stream>
    </streams>
    <inputs>
        <network>
            <protocol>TSOverUDP</protocol>
            <uri>${inputUri!""}</uri>
            <hsyqedit>0</hsyqedit>
            <programid>-1</programid>
            <audiotrackid>-1</audiotrackid>
            <subtitleid>-2</subtitleid>
            <distinguishav>0</distinguishav>
            <delayoutputtime>${delay!""}</delayoutputtime>
            <srcip>${srcip_in!""}</srcip>
            <allowprogramidchange>1</allowprogramidchange>
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
                    <uri></uri>
                    <inputtype>Network</inputtype>
                    <programid>-1</programid>
                    <audiotrackid>-1</audiotrackid>
                    <subtitleid>-2</subtitleid>
                    <srcip/>
                </padding_location>
                <padding_type>1</padding_type>
                <audiodelay>0</audiodelay>
            </preprocessor>
        </network>
    </inputs>
    <outputgroups>
        <udpstreaming>
            <active>1</active>
            <container>UDPOverTS</container>
            <tssetting>
                <servername/>
                <serviceprovider/>
                <serviceid>1</serviceid>
                <totalbitrate/>
                <bitratemode>-1</bitratemode>
                <videodelay>0</videodelay>
                <cutaudio/>
                <audiomergeframe/>
                <pmtpid>256</pmtpid>
                <videopid>4113</videopid>
                <audiopid>4352</audiopid>
                <pcrpid/>
                <audioprocessmode>0</audioprocessmode>
            </tssetting>
            <description>Live UDP</description>
            <uri>${(outputUri)!""}</uri>
            <srcip>${srcip_out!""}</srcip>
            <srcport>1234</srcport>
            <buffersize>65535</buffersize>
            <ttl>255</ttl>
            <igmpsourceip/>
            <output streamref="${taskId?c}" />
        </udpstreaming>
    </outputgroups>
</task>