package com.arcvideo.pgcliveplatformserver.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;

/**
 * Created by zfl on 2018/5/11.
 */
public class XmlUtil {

    public static <T> T xmlStringToObject(String xml, Class<T> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller um = context.createUnmarshaller();
        T result = (T) um.unmarshal(new ByteArrayInputStream(xml.getBytes()));
        return result;
    }
}
