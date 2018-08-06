package com.arcvideo.pgcliveplatformserver.util.file;

import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderUtil {
    public static String[] getList(boolean folder, File rootFolderFile) {
        String[] fileAndfolders = null;
        if (rootFolderFile.exists()) {
            if (folder) {
                fileAndfolders = rootFolderFile.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        File file = FileUtils.getFile(dir, name);
                        return file.isDirectory();
                    }
                });
            } else {
                fileAndfolders = rootFolderFile.list();
            }
        }
        return fileAndfolders;
    }

    public static List queryFolderAndFileList(boolean folderMode, String id, String defaultRootFolderName, ContentFileFilter contentFileFilter) {
        List folderList = new ArrayList();
        List subfolderList = new ArrayList();
        String rootFolderName = id;
        boolean hasSubFolder = false;
        if (StringUtils.isEmpty(id)) {
            rootFolderName = defaultRootFolderName;
            hasSubFolder = true;
        }
        File rootFolderFile = FileUtils.getFile(rootFolderName);
        if (rootFolderFile.exists()) {
            String[] fileAndfolders = FolderUtil.getList(folderMode, rootFolderFile);
            if (fileAndfolders != null) {
                for (String name : fileAndfolders) {
                    File file = new File(rootFolderName, name);
                    Map map = new HashMap();
                    Map map1 = new HashMap();
                    String fileName = file.getName();
                    map.put("id", file.getAbsolutePath());
                    map.put("text", fileName);
                    if (file.isDirectory()) {
                        map.put("icon", "fa fa-folder");
                    } else {
                        //是否是视频文件?
                        if (contentFileFilter != null && contentFileFilter.accept(fileName)) {
                            map.put("icon", "fa fa-film");
                        }
                        else {
                            map.put("icon", "fa fa-file");
                        }
                    }

                    map.put("children", file.isDirectory());
                    subfolderList.add(map);
                }
            }
            if (hasSubFolder) {
                Map map = new HashMap();
                map.put("id", rootFolderFile.getAbsolutePath());
                map.put("text", rootFolderFile.getName());
                map.put("children", subfolderList);
                map.put("icon", "fa fa-folder");
                folderList.add(map);
            }
        }
        if (hasSubFolder) {
            return folderList;
        } else {
            return subfolderList;
        }
    }

    public static List queryFirstFolderAndFileList(boolean folderMode, String id, String defaultRootFolderName, ContentFileFilter contentFileFilter, String path) {
        List folderList = new ArrayList();
        List subfolderList = new ArrayList();
        String rootFolderName = id;
        boolean hasSubFolder = false;
        if (StringUtils.isEmpty(id)) {
            rootFolderName = defaultRootFolderName;
            hasSubFolder = true;
        }
        Map map1 = new HashMap();
        File rootFolderFile = FileUtils.getFile(rootFolderName);
        if (rootFolderFile.exists()) {
            String[] fileAndfolders = FolderUtil.getList(folderMode, rootFolderFile);
            if (fileAndfolders != null) {
                for (String name : fileAndfolders) {
                    File file = new File(rootFolderName, name);
                    Map map = new HashMap();
                    String fileName = file.getName();
                    map.put("id", file.getAbsolutePath());
                    map.put("text", fileName);
                    if(path.startsWith(file.getAbsolutePath()) ){
                        if(path.equals(file.getAbsolutePath())){
                           map1.put("selected",true);
                        }else {
                            String fileParentPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator));
                            String parentPath = path.substring(0, path.lastIndexOf(File.separator));
                            if(!fileParentPath.equals(parentPath)){
                                map1.put("opened",true);
                            }
                        }
                        map.put("state",map1);
                    }
                    if (file.isDirectory()) {
                        map.put("icon", "fa fa-folder");
                    } else {
                        //是否是视频文件?
                        if (contentFileFilter != null && contentFileFilter.accept(fileName)) {
                            map.put("icon", "fa fa-film");
                        }
                        else {
                            map.put("icon", "fa fa-file");
                        }
                    }

                    map.put("children", file.isDirectory());
                    subfolderList.add(map);
                }
            }
            if (hasSubFolder) {
                Map map = new HashMap();
                map.put("id", rootFolderFile.getAbsolutePath());
                map.put("text", rootFolderFile.getName());
                map.put("children", subfolderList);
                map.put("icon", "fa fa-folder");
                map.put("state",map1);
                folderList.add(map);
            }
        }
        if (hasSubFolder) {
            String jsonArr = JSONArray.toJSONString(folderList);
            System.out.println(jsonArr);
            return folderList;
        } else {
            String jsonArr = JSONArray.toJSONString(subfolderList);
            System.out.println(jsonArr);
            return subfolderList;
        }
    }
}
