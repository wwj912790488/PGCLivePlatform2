package com.arcvideo.system.util.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class M3u8Parser {
    private static final Logger logger = LoggerFactory.getLogger(M3u8Parser.class);

    private static ConcurrentHashMap<String, List<HttpFileInfo>> m3u8InfoMap = new ConcurrentHashMap<>();

    private List<HttpFileInfo> httpFileInfoList = new ArrayList<>();

    private boolean endList = false;

    public M3u8Parser() {
    }

    public boolean parseM3u8File(String fileName) throws IOException {
//        if (m3u8InfoMap.containsKey(fileName)) {
//            httpFileInfoList = m3u8InfoMap.get(fileName);
//            return true;
//        }
        boolean ret = __parseM3u8File(fileName);
//        if (ret) {
//            m3u8InfoMap.put(fileName, httpFileInfoList);
//        }
        return ret;
    }

    private boolean __parseM3u8File(String fileName) throws IOException {
        URL url = new URL(fileName);
        HttpURLConnection httpURLConnection = null;
        String lastSegmentFileName = "";
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            try (LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                String line = lineNumberReader.readLine();
                if (line == null || !line.startsWith("#EXTM3U")) {
                    return false;
                }
                boolean is_variant = false;
                boolean is_segment = false;
                boolean discontinuity = false;
                while ((line = lineNumberReader.readLine()) != null) {
                    if (line.startsWith("#EXT-X-STREAM-INF:")) {
                        is_variant = true;
                    }
                    else if (line.startsWith("#EXT-X-DISCONTINUITY")) {
                        discontinuity = true;
                    }
                    else if (line.startsWith("#EXT-X-TARGETDURATION:")) {
                    } else if (line.startsWith("#EXT-X-MEDIA-SEQUENCE:")) {
                    } else if (line.startsWith("#EXT-X-ENDLIST")) {
                        endList = true;
                    } else if (line.startsWith("#EXTINF:")) {
                        is_segment = true;
                    } else if (line.startsWith("#")) {
                        continue;
                    } else {
                        if (is_segment) {
                            String segmentFileName = line.substring(0, line.indexOf("?"));
                            Long startPos = 0L;
                            Long endPos = 0L;
                            URL segmentUrl = new URL(line);
                            Map<String, List<String>> mapList = splitQuery(segmentUrl);
                            List<String> startPosList = mapList.get("s");
                            List<String> endPosList = mapList.get("e");
                            if (startPosList.size() > 0){
                                startPos = Long.parseLong(startPosList.get(0));
                            }
                            if (endPosList.size() > 0){
                                endPos = Long.parseLong(endPosList.get(0));
                            }

                            boolean addedItem = false;
                            if (discontinuity || segmentFileName.compareTo(lastSegmentFileName) != 0) {
                                httpFileInfoList.add(new HttpFileInfo(startPos, endPos, segmentFileName));
                                lastSegmentFileName = segmentFileName;
                                addedItem = true;
                            }
                            else {
                                if (httpFileInfoList.size() > 0) {
                                    int lastIndex = httpFileInfoList.size() - 1;
                                    HttpFileInfo httpFileInfo = httpFileInfoList.get(lastIndex);
                                    if (httpFileInfo.getFileName().compareTo(segmentFileName) == 0) {
                                        if (httpFileInfo.getStartPos() > startPos) {
                                            httpFileInfo.setStartPos(startPos);
                                        }
                                        if (httpFileInfo.getEndPos() < endPos) {
                                            httpFileInfo.setEndPos(endPos);
                                        }
                                        httpFileInfoList.set(lastIndex, httpFileInfo);
                                        addedItem = true;
                                    }
                                }
                            }
                            if (!addedItem) {
                                httpFileInfoList.add(new HttpFileInfo(startPos, endPos, segmentFileName));
                                lastSegmentFileName = segmentFileName;
                            }
                            is_segment = false;
                            discontinuity = false;
                        } else if (is_variant) {
                            String fileM3u8 = line;
                            __parseM3u8File(fileM3u8);
                            is_variant = false;
                        }
                    }
                }
            }
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        processFileList();
        return this.httpFileInfoList.size() > 0;
    }

    public List<HttpFileInfo> getHttpFileInfoList() {
        return this.httpFileInfoList;
    }

    public boolean isEnd() {
        return this.endList;
    }

    private void processFileList() throws IOException {
        List<HttpFileInfo> newFileList = new ArrayList<>();
        for (HttpFileInfo httpFileInfo : httpFileInfoList) {
            newFileList.add(httpFileInfo);
        }
        httpFileInfoList.clear();
        int i = 0;
        int endtime = 45;
        for (HttpFileInfo httpFileInfo : newFileList) {
            if (i++ == endtime) {
                httpFileInfoList.add(httpFileInfo);
                break;
            }
        }
    }

    public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }

}
