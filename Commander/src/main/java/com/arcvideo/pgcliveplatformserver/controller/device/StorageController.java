package com.arcvideo.pgcliveplatformserver.controller.device;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.StorageSettings;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.repo.StorageSettingsRepo;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.system.SystemInfo;
import com.arcvideo.system.host.platform.linux.MountUtilImpl;
import com.arcvideo.system.model.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/settings/storage")
public class StorageController {
    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private StorageSettingsRepo storageSettingsRepo;

    @Autowired
    private ServerSettingService serverSettingService;

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<StorageSettings>> listStorage(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<StorageSettings> reponseJson = new DatatableResponse<>();
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Page<StorageSettings> page = storageSettingsRepo.findAll(new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean(reponseJson);
    }

    @RequestMapping(value = "")
    public String addStorage(Model model) {
        StorageSettings storage = new StorageSettings();
        model.addAttribute("storage", storage);
        return "device/storage";
    }

    @RequestMapping(value = "edit")
    public String editStorage(Model model,@RequestParam Long id) {
        StorageSettings storage = storageSettingsRepo.findOne(id);
        if(storage==null){
            storage = new StorageSettings();
        }
        model.addAttribute("storage", storage);
        return "device/storage";
    }


    @RequestMapping(value = "/mount", method = RequestMethod.GET)
    @ResponseBody
    @OperationLog(operation = "挂载存储", fieldNames = "id")
    public ResultBean mountStorage(@RequestParam Long id) {
        ResultBean resultBean = new ResultBean();
        try {
            StorageSettings storageSettings = storageSettingsRepo.findOne(id);
            if (SystemInfo.getMountUtil().mountStorage(storageSettings.toStorage())) {
                storageSettings.setMounted(true);
                storageSettingsRepo.save(storageSettings);
//                serverSettingService.updateAllServerStorage();
            } else {
                resultBean.setCode(-100);
                resultBean.setMessage("mount failed!");
            }
        } catch (Exception e) {
            resultBean.setCode(-1);
            resultBean.setMessage("mount error:"+e.getMessage());
        }
        return resultBean;
    }

    @RequestMapping(value = "/unmount", method = RequestMethod.GET)
    @ResponseBody
    @OperationLog(operation = "卸载存储", fieldNames = "id")
    public ResultBean unmountStorage(@RequestParam Long id) {
        ResultBean resultBean = new ResultBean();
        try {
            StorageSettings storageSettings = storageSettingsRepo.findOne(id);
            if (SystemInfo.getMountUtil().unmountStorage(storageSettings.toStorage(), true)) {
                storageSettings.setMounted(false);
                storageSettingsRepo.save(storageSettings);
//                serverSettingService.updateAllServerStorage();
            }else {
                resultBean.setCode(-100);
                resultBean.setMessage("unmount failed!");
            }
        } catch (Exception e) {
            resultBean.setCode(-1);
            resultBean.setMessage("unmount error:"+e.getMessage());
        }
        return resultBean;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    @OperationLog(operation = "删除存储", fieldNames = "id")
    public ResultBean deleteStorage(@RequestParam Long id) {
        ResultBean resultBean = new ResultBean();
        try {
            StorageSettings storageSettings = storageSettingsRepo.findOne(id);
            if (SystemInfo.getMountUtil().unmountStorage(storageSettings.toStorage(), true)) {
                storageSettingsRepo.delete(id);
//                serverSettingService.updateAllServerStorage();
            }else {
                resultBean.setCode(-100);
                resultBean.setMessage("delete storage failed!");
            }
        } catch (Exception e) {
            resultBean.setCode(-1);
            resultBean.setMessage("delete storage error:"+e.getMessage());
        }
        return new ResultBean();
    }

    @RequestMapping(value = "/addstorage", method = RequestMethod.POST)
    @ResponseBody
    @OperationLog(operation = "新增存储", fieldNames = "storageSettings")
    public ResultBean addStorage(Model model, @ModelAttribute StorageSettings storageSettings) {
        ResultBean resultBean = new ResultBean();
        try {
            if (storageSettings.getId() != null) {
                SystemInfo.getMountUtil().unmountStorage(storageSettingsRepo.findOne(storageSettings.getId()).toStorage(), true);
            }
            if (SystemInfo.getMountUtil().mountStorage(storageSettings.toStorage())) {
                storageSettings.setMounted(true);
                storageSettingsRepo.save(storageSettings);
//                serverSettingService.updateAllServerStorage();
            }else{
                resultBean.setCode(-100);
                resultBean.setMessage("add storage failed!");
            }
        } catch (Exception e) {
            resultBean.setCode(-1);
            resultBean.setMessage("add storage error:"+e.getMessage());
        }
        return resultBean;
    }

    @RequestMapping(value = "/checkstoragename", method = RequestMethod.POST)
    @ResponseBody
    public boolean checkStorageName(String name, String id) {
        //判断挂载存储名字与数据库里是否有重复
        if (name == null || "".equals(name)) {
            return false;
        }
        List<StorageSettings> storageList = storageSettingsRepo.findAllByName(name);
        //id不为空，表明是编辑，需要特殊处理
        if(id != null && !"".equals(id)){
        	Long dataId = Long.valueOf(id);
        	if (storageList != null && storageList.size() > 0){
	        	if(storageList.size() > 1){
	        		return false;
	        	}
	        	//如果当前编辑ID和数据库根据name查出来的ID不一致,表示重复
	        	if(!dataId.equals(storageList.get(0).getId())){
	        		return false;
	        	}
        	}
        }else{
        	if (storageList != null && storageList.size() > 0) {
                return false;
            }
        }
        
        return true;
    }

    @RequestMapping(value = "/querystorage", method = RequestMethod.POST)
    @ResponseBody
    public Storage queryStorage(@RequestParam Long id) {
        Storage storage = null;
        StorageSettings storageSettings = storageSettingsRepo.findOne(id);
        if (storageSettings != null) {
            storage = storageSettings.toStorage();
        } else {
            storage = new Storage("", "", "cifs");
        }
        return storage;
    }

    private List<Storage> getStorageList(boolean isLocla) {
        List<Storage> storageList = new ArrayList<>();
        List<Storage> unStorageList = new ArrayList<>();
        List<StorageSettings> storageSettingsList = storageSettingsRepo.findAll();
        boolean isOnline = false;
        MountUtilImpl mountUtilImpl = new MountUtilImpl();
        List<Storage> mountedStorageList = mountUtilImpl.getMountedStorageList("cifs", "nfs", "oss");
        if (isLocla) {
            for (StorageSettings storageSettings : storageSettingsList) {
                boolean found = false;
                if(storageSettings.getType() .equals("oss")){
                    storageSettings.setPath("//oss:"+ storageSettings.getPath().split("/")[0].trim());
                }
                for (Storage mountedStorage : mountedStorageList) {
                    if (MountUtilImpl.isSameMountRemotePath(storageSettings.getPath(), mountedStorage.getPath()) &&
                            storageSettings.getName().equals(mountedStorage.getName())) {
                        Storage storage = storageSettings.toStorage();
                        storage.setMounted(true);
                        storageList.add(storage);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Storage storage = storageSettings.toStorage();
                    storage.setMounted(false);
                    storageList.add(storage);
                }
            }
            return storageList;
        } else {
            for (Storage mountedStorage : mountedStorageList) {
                boolean isSave = false;
                for (StorageSettings storageSettings : storageSettingsList) {
                    if (MountUtilImpl.isSameMountRemotePath(storageSettings.getPath(), mountedStorage.getPath()) &&
                            storageSettings.getName().equals(mountedStorage.getName())) {
                        isSave = true;
                        break;
                    }
                }
                if (!isSave) {
                    unStorageList.add(mountedStorage);
                }
            }
            return unStorageList;
        }

    }
}
