package com.arcvideo.pgcliveplatformserver.validator;

import com.arcvideo.pgcliveplatformserver.entity.VlanSetting;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.VlanSettingRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.stream.Stream;

/**
 * Created by slw on 2018/7/4.
 */
@Component
public class VlanSettingValidator implements Validator {
    @Autowired
    private VlanSettingRepo vlanSettingRepo;

    @Override
    public boolean supports(Class<?> clazz) {
        return VlanSetting.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        VlanSetting vlanSetting = (VlanSetting) o;

        String cidr = vlanSetting.getCidr();
        if (StringUtils.isEmpty(cidr)) {
            errors.reject(CodeStatus.VLAN_SETTING_ERROR_CIDR_EMPTY.name(), "VlanSetting cidr can not be empty");
            return;
        } else {
            VlanSetting oldVlanSetting = vlanSettingRepo.findFirstByCidr(cidr);
            if (oldVlanSetting != null && oldVlanSetting.getId() != vlanSetting.getId()) {
                errors.reject(CodeStatus.VLAN_SETTING_ERROR_CIDR_EXISTS.name(), "VlanSetting cidr exist");
                return;
            }
        }

        String nioType = vlanSetting.getNioType();
        if (StringUtils.isBlank(nioType)) {
            errors.reject(CodeStatus.VLAN_SETTING_ERROR_NIOTYPE_EMPTY.name(), "VlanSetting nioType can not be empty");
            return;
        } else {
            String[] nioArr = nioType.trim().split(",");
            for (String nio : nioArr) {
                boolean flag = Stream.of(VlanSetting.NioType.ALL).map(type -> type.name()).anyMatch(type -> type.equals(nio));
                if (!flag) {
                    errors.reject(CodeStatus.VLAN_SETTING_ERROR_NIOTYPE_NOT_FOUND.name(), "VlanSetting nioType not found:" + nio);
                    return;
                }
                else {
                    VlanSetting oldVlanSetting = vlanSettingRepo.findFirstByNioTypeContaining(nio);
                    if (oldVlanSetting != null && oldVlanSetting.getId() != vlanSetting.getId()) {
                        errors.reject(CodeStatus.VLAN_SETTING_ERROR_NIOTYPE_EXISTS.name(), "VlanSetting nioType exist:" + nio);
                        return;
                    }
                }
            }
        }

        String name = vlanSetting.getName();
        if (StringUtils.isEmpty(name)) {
            errors.reject(CodeStatus.VLAN_SETTING_ERROR_NAME_EMPTY.name(), "VlanSetting name can not be empty");
            return;
        } else {
            VlanSetting oldVlanSetting = vlanSettingRepo.findFirstByName(name);
            if (oldVlanSetting != null && oldVlanSetting.getId() != vlanSetting.getId()) {
                errors.reject(CodeStatus.VLAN_SETTING_ERROR_NAME_EXISTS.name(), "VlanSetting name exist");
                return;
            }
        }
    }
}
