package com.arcvideo.pgcliveplatformserver.controller;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.repo.LiveLogoRepo;
import com.arcvideo.pgcliveplatformserver.repo.MotionIconRepo;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialIconService;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialLogoService;
import com.arcvideo.pgcliveplatformserver.specfication.MaterialIconSpecfication;
import com.arcvideo.pgcliveplatformserver.specfication.MaterialLogoSpecfication;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.pgcliveplatformserver.util.UuidUtil;
import com.arcvideo.pgcliveplatformserver.util.file.compressZip;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zfl on 2018/5/4.
 */
@Controller
@RequestMapping("material")
public class MaterialController {

    private static final Logger logger = LoggerFactory.getLogger(MaterialController.class);

    private static String MATERIAL_ADD = "material/material_add";

    private static String MATERIAL_MOTIONICON_ADD = "material/material_motionicon_add";

    @Autowired
    private MaterialIconService materialIconService;

    @Autowired
    private MaterialLogoService materialLogoService;

    @Autowired
    private LiveLogoRepo liveLogoRepo;

    @Autowired
    private MotionIconRepo motionIconRepo;


    @Autowired
    private ContentService contentService;

    @Value("${default.logo.path}")
    private String LOGO_DEST_DIR_PATH;
    @Value("${default.motion.icon.path}")
    private String ICON_DEST_DIR_PATH;

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<MaterialLogo>> listMaterial(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<MaterialLogo> reponseJson = new DatatableResponse<>();
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        //Integer type = MaterialIcon.MATERIAL_TYPE_LOGO;
        String keyword = parametresAjax.getFirst("keyword");
        Specification<MaterialLogo> specification = MaterialLogoSpecfication.listByTypeAndCompanyIdAndKeyword(companyId, keyword);
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");

        Page<MaterialLogo> page = materialLogoService.listMaterial(specification, new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "motionicon/list")
    @ResponseBody
    public ResultBean<DatatableResponse<MaterialIcon>> listMaterialMotionIcon(@RequestBody MultiValueMap<String, String> parametresAjax, HttpServletRequest httpServletRequest) {
        DatatableResponse<MaterialIcon> reponseJson = new DatatableResponse<>();
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        String keyword = parametresAjax.getFirst("keyword");
        Specification<MaterialIcon> specification = MaterialIconSpecfication.listByTypeAndCompanyIdAndKeyword(companyId, keyword);
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Page<MaterialIcon> page = materialIconService.listMaterial(specification, new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "")
    public String material() {
        return "material/material";
    }

    @RequestMapping(value = "motionIcon")
    public String motionIcon() {
        return "material/material_motionicon";
    }

    @RequestMapping(value = "addLogo")
    public String addLogo(Model model) {
        MaterialLogo materialLogo = new MaterialLogo();
        model.addAttribute("material", materialLogo);
        return MATERIAL_ADD;
    }

    @RequestMapping(value = "addIcon")
    public String addIcon(Model model) {
        MaterialIcon materialIcon = new MaterialIcon();
        model.addAttribute("material", materialIcon);
        return MATERIAL_MOTIONICON_ADD;
    }


    @RequestMapping(value = "saveLogo")
    @ResponseBody
    public ResultBean saveLogo(@RequestParam(required = true) String name,@RequestParam(required = true) String description, @RequestParam("file") MultipartFile file) {
        ResultBean resultBean = new ResultBean();
        try {
            // get dest file name or path
            String destPath = LOGO_DEST_DIR_PATH;
            String fileName = file.getOriginalFilename();
            String fileExt = FilenameUtils.getExtension(fileName);
            String baseName = FilenameUtils.getBaseName(fileName);
            MaterialLogo materialLogo = new MaterialLogo();
            if (!StringUtils.equalsIgnoreCase(fileExt, "PNG") && !StringUtils.equalsIgnoreCase(fileExt, "BMP")) {
                resultBean.setCode(ResultBean.FAIL);
                resultBean.setMessage("文件要以对应的格式");
                return resultBean;
            }
            String fullName = baseName + "-" + UuidUtil.getUuid() + "." + fileExt;
            File finalFile = FileUtils.getFile(destPath, fullName);
            if (finalFile.exists()) {
                resultBean.setCode(ResultBean.FAIL);
                resultBean.setMessage("文件已存在");
                return resultBean;
            }
            FileUtils.writeByteArrayToFile(finalFile, file.getBytes());
            materialLogo.setContent(finalFile.getPath());
            materialLogo.setName(URLDecoder.decode(name, "UTF-8"));
            materialLogo.setCreateUserId(UserUtil.getSsoLoginUserId());
            materialLogo.setCreateUserName(UserUtil.getSsoLoginId());
            materialLogo.setCompanyId(UserUtil.getSsoCompanyId());
            materialLogo.setCreateDate(new Date());
            materialLogo.setDescription(description);
            materialLogoService.save(materialLogo);
        } catch (IOException e) {
            resultBean.setCode(ResultBean.FAIL);
            resultBean.setMessage("failed!");
        }

        return resultBean;
    }

    @RequestMapping(value = "saveIcon")
    @ResponseBody
    public ResultBean saveIcon(@RequestParam(required = true) String name, @RequestParam(required = true) String description,@RequestParam("file") MultipartFile file) {
        ResultBean resultBean = new ResultBean();
        try {
            // get dest file name or path
            String destPath = ICON_DEST_DIR_PATH;
            String fileName = file.getOriginalFilename();
            String fileExt = FilenameUtils.getExtension(fileName);
            String baseName = FilenameUtils.getBaseName(fileName);
            MaterialIcon materialIcon = new MaterialIcon();
            String realDestPath = destPath + baseName + "-" + UuidUtil.getUuid() + "/";
            if (!StringUtils.equalsIgnoreCase(fileExt, "ZIP")) {
                resultBean.setCode(ResultBean.FAIL);
                resultBean.setMessage("文件应为ZIP压缩文件");
                return resultBean;
            }
            File finalFile = FileUtils.getFile(realDestPath);
            if (finalFile.exists()) {
                resultBean.setCode(ResultBean.FAIL);
                resultBean.setMessage("文件夹已存在");
                return resultBean;
            }
            List<String> urlList1 = new ArrayList<>();
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            final File excelFile = File.createTempFile(UuidUtil.getUuid(), prefix);
            // MultipartFile to File
            file.transferTo(excelFile);
            compressZip.unZip(excelFile, realDestPath, urlList1);
            materialIcon.setContent(realDestPath);
            materialIcon.setName(URLDecoder.decode(name, "UTF-8"));
            materialIcon.setCreateUserId(UserUtil.getSsoLoginUserId());
            materialIcon.setCreateUserName(UserUtil.getSsoLoginId());
            materialIcon.setCompanyId(UserUtil.getSsoCompanyId());
            materialIcon.setCreateDate(new Date());
            materialIcon.setDescription(description);
            materialIconService.save(materialIcon);
        } catch (IOException e) {
            resultBean.setCode(ResultBean.FAIL);
            resultBean.setMessage("failed!");
        }

        return resultBean;
    }

    @RequestMapping(value = "deleteLogo")
    @ResponseBody
    public ResultBean deleteLogo(@RequestParam(required = true) String ids) {
        ResultBean resultBean = new ResultBean();
       /* List<Content> contentList = contentService.findByRunningContentList(Content.Status.RUNNING);
        for (Content content : contentList) {
            List<LiveLogo> liveLogoList = content.getLogos();
            for (LiveLogo liveLogo : liveLogoList) {
                if (ids.equals(liveLogo.getMaterialId())) {
                    resultBean.setCode(ResultBean.FAIL);
                    resultBean.setMessage("failed!");
                }
            }
            List<MotionIcon> motionIconList = content.getIcons();
            for (MotionIcon motionIcon : motionIconList) {
                if (ids.equals(motionIcon.getMaterialId())) {
                    resultBean.setCode(ResultBean.FAIL);
                    resultBean.setMessage("failed!");
                }
            }
        }*/

        checkDeleteLogo(resultBean, ids);
        if (resultBean.getCode() == 0) {
            materialLogoService.delete(ids);
        }
        return resultBean;
    }

    private ResultBean checkDeleteLogo(ResultBean resultBean, String ids) {
        String[] idList = ids.trim().split(",");
        List<String> stringList = new ArrayList<>();
        for (String id : idList) {
            List<LiveLogo> liveLogoList = liveLogoRepo.findByMaterialId(Long.valueOf(id));
            for (LiveLogo liveLogo : liveLogoList) {
                Content content = contentService.findById(liveLogo.getContentId());
                if (Content.Status.RUNNING.equals(content.getStatus()) || Content.Status.PENDING.equals(content.getStatus()) ) {
                    resultBean.setCode(ResultBean.FAIL);
                    resultBean.setMessage("failed!");
                    StringBuilder sb = new StringBuilder().append("活动编号为：").append(content.getId()).append(" 名称为：").append(content.getName()).append(" 使用了台标编号为：").append(id);
                    stringList.add(sb.toString());
                }
            }

        }
        resultBean.setData(stringList);
        return resultBean;
    }

    @RequestMapping(value = "deleteIcon")
    @ResponseBody
    public ResultBean deleteIcon(@RequestParam(required = true) String ids) {
        ResultBean resultBean = new ResultBean();
       /* List<Content> contentList = contentService.findByRunningContentList(Content.Status.RUNNING);
        for (Content content : contentList) {
            List<LiveLogo> liveLogoList = content.getLogos();
            for (LiveLogo liveLogo : liveLogoList) {
                if (ids.equals(liveLogo.getMaterialId())) {
                    resultBean.setCode(ResultBean.FAIL);
                    resultBean.setMessage("failed!");
                }
            }
            List<MotionIcon> motionIconList = content.getIcons();
            for (MotionIcon motionIcon : motionIconList) {
                if (ids.equals(motionIcon.getMaterialId())) {
                    resultBean.setCode(ResultBean.FAIL);
                    resultBean.setMessage("failed!");
                }
            }
        }*/
        checkDeleteIcon(resultBean, ids);
        if (resultBean.getCode() == 0) {
            materialIconService.delete(ids);
        }
        return resultBean;
    }

    private ResultBean checkDeleteIcon(ResultBean resultBean, String ids) {
        String[] idList = ids.trim().split(",");
        List<String> stringList = new ArrayList<>();
        for (String id : idList) {
            List<MotionIcon> liveLogoList = motionIconRepo.findByMaterialId(Long.valueOf(id));
            for (MotionIcon motionIcon : liveLogoList) {
                Content content = contentService.findById(motionIcon.getContentId());
                if (Content.Status.RUNNING.equals(content.getStatus()) || Content.Status.PENDING.equals(content.getStatus())) {
                    resultBean.setCode(ResultBean.FAIL);
                    resultBean.setMessage("failed!");
                    StringBuilder sb = new StringBuilder().append("活动编号为：").append(content.getId()).append(" 名称为：").append(content.getName()).append(" 使用了动图编号为：").append(id);
                    stringList.add(sb.toString());
                }
            }

        }
        resultBean.setData(stringList);
        return resultBean;
    }


}
