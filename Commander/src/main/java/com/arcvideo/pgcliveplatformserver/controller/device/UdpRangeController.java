package com.arcvideo.pgcliveplatformserver.controller.device;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.UdpRange;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.UdpRangeRepo;
import com.arcvideo.pgcliveplatformserver.validator.UdpRangeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by slw on 2018/3/20.
 */
@Controller
@RequestMapping("device")
public class UdpRangeController {
    private static final Logger logger = LoggerFactory.getLogger(UdpRangeController.class);

    @Autowired
    private Environment env;

    @Autowired
    private UdpRangeRepo udpRangeRepo;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private UdpRangeValidator udpRangeValidator;

    @RequestMapping(value = "control", method = RequestMethod.GET)
    public String udpRangePage(Model model) {
        UdpRange udpRange = udpRangeRepo.findOne(1L);
        if (udpRange == null) {
            udpRange = new UdpRange();
        }
        model.addAttribute("udpRange", udpRange);
        model.addAttribute("version", env.getProperty("version"));
        return "device/control";
    }

    @RequestMapping(value = "control/udp/save")
    @ResponseBody
    public ResultBean saveUdpRange(UdpRange udpRange, BindingResult bindingResult) {
        udpRangeValidator.validate(udpRange, bindingResult);
        ResultBean result = resultBeanBuilder.ok();
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                result = resultBeanBuilder.builder(codeStatus);
            } else {
                result = resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        } else {
            udpRange.setId(1L);
            udpRangeRepo.save(udpRange);
        }
        return result;
    }

    @RequestMapping(value = "control/version")
    public String versionInfo(Model model) {
        model.addAttribute("version", env.getProperty("version"));
        return "device/version";
    }
}
