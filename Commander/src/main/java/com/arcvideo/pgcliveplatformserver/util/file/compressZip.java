package com.arcvideo.pgcliveplatformserver.util.file;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Created by wwj on 2018/6/19.
 */
public class compressZip {

    public static boolean unZip(File zipFile, String descDir, List<String> urlList) {
        boolean flag = false;
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            //指定编码，否则压缩包里面不能有中文目录
            //System.out.println(zipFile.getName().substring(0,zipFile.getName().indexOf(".")));
            zip = new ZipFile(zipFile, Charset.forName("gbk"));
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (descDir + zipEntryName).replace("/", File.separator);
                //System.out.println(zipEntryName);
                //判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                if (!file.exists()) {
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                //保存文件路径信息
                urlList.add(outPath);

                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[2048];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            flag = true;
            //必须关闭，否则无法删除该zip文件
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void main(String[] args) {
        String zipPath = "C:\\com\\tga.zip";
        File zipFile = new File(zipPath);
        String descDir = "C:\\com\\aaa\\";
        List<String> urlList1 = new ArrayList<>();
        unZip(zipFile, descDir, urlList1);
        System.out.println(urlList1);

    }

}
