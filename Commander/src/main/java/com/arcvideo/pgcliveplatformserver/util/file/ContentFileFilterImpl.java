package com.arcvideo.pgcliveplatformserver.util.file;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Date;

public class ContentFileFilterImpl implements ContentFileFilter {
    private String[] fileFilterList;
    private String fullFileNamePattern;
    private String[] excludeExtensionList;
    private String keyword;
    private Date fromtime;
    private Date totime;

    public ContentFileFilterImpl(String fileFilterString, String fullFileNamePattern, String excludeExtensionString, String keyword, Date fromtime, Date totime) {
        if (StringUtils.isNotEmpty(fileFilterString)) {
            fileFilterString = fileFilterString.toLowerCase();
            fileFilterList = StringUtils.split(fileFilterString, '|');
        }
        if (StringUtils.isNotEmpty(excludeExtensionString)) {
            excludeExtensionString = excludeExtensionString.toLowerCase();
            excludeExtensionList = StringUtils.split(excludeExtensionString, '|');
        }

        this.fullFileNamePattern = fullFileNamePattern;
        this.keyword = keyword;
        this.fromtime = fromtime;
        this.totime = totime;
    }

    @Override
    public boolean accept(String path) {
        if (fullFileNamePattern != null) {
            return findFileNameInRules(FilenameUtils.getName(path)) && !findExitInExcludeExt(FilenameUtils.getName(path));
        } else {
            return findExtInRules(path);
        }
    }

    @Override
    public boolean acceptFile(String path) {
        boolean ret = findExtInRules(path);
        if (ret && this.keyword != null) {
            ret = findKeywordInRules(path, this.keyword);
        }
        if (ret && this.fromtime != null && this.totime != null) {
            File file = new File(path);
            long time = file. lastModified();
            Date d = new Date(time);

            ret = (time >= this.fromtime.getTime() && time < this.totime.getTime()) ;
        }
        return ret;
    }

    @Override
    public boolean acceptFolder(String path) {
        boolean ret = true;
        if (ret && this.keyword != null) {
            ret = findKeywordInRules(path, this.keyword);
        }
        if (ret && this.fromtime != null && this.totime != null) {
            File file = new File(path);
            long time = file. lastModified();
            ret = (time >= this.fromtime.getTime() && time < this.totime.getTime()) ;
        }
        return ret;
    }

    private boolean findFileNameInRules(String fileName) {
        DateParserFromFile dateParserFromFile = new DateParserFromFile(fullFileNamePattern);
        return dateParserFromFile.parser(fileName);
    }

    private boolean findExtInRules(String fileName) {
        if (fileFilterList == null || fileFilterList.length == 0) {
            return true;
        }
        final String lowerFileName = fileName.toLowerCase();
        return FilenameUtils.isExtension(lowerFileName, fileFilterList);
    }

    private boolean findKeywordInRules(String path, String keyword) {
        int index = path.indexOf(keyword);
        return index >= 0;
    }

    private boolean findExitInExcludeExt(String fileName) {
        if (excludeExtensionList == null || excludeExtensionList.length == 0) {
            return false;
        }
        final String lowerFileName = fileName.toLowerCase();
        return FilenameUtils.isExtension(lowerFileName, excludeExtensionList);
    }
}
