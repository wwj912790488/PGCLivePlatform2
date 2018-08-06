package com.arcvideo.pgcliveplatformserver.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zfl on 2018/5/11.
 */
public class XmlDateAdapter extends XmlAdapter<String, Date> {
    private SimpleDateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date unmarshal(String v) throws Exception {
        if ("".equals(v)) {
            return null;
        }
        return yyyyMMddHHmm.parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return yyyyMMddHHmm.format(v);
    }
}
