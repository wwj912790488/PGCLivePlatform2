package com.arcvideo.pgcliveplatformserver.validator;

import com.arcvideo.pgcliveplatformserver.entity.UdpRange;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.util.IPUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by slw on 2018/6/22.
 */
@Component
public class UdpRangeValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UdpRange.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UdpRange udpRange = (UdpRange) o;

        String ipBegin = udpRange.getIpBegin();
        if (StringUtils.isBlank(ipBegin)) {
            errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_BEGIN.name(), "UdpRange ipBegin can not be empty");
            return;
        }

        String ipEnd = udpRange.getIpEnd();
        if (StringUtils.isBlank(ipEnd)) {
            errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_END.name(), "UdpRange ipEnd can not be empty");
            return;
        }

        Integer portBegin = udpRange.getPortBegin();
        if (portBegin == null || portBegin < UdpRange.PORT_BEGIN || portBegin > UdpRange.PORT_END) {
            errors.reject(CodeStatus.UDP_RANGE_ERROR_PORT_BEGIN.name(), "UdpRange portBegin must gt 1 and lt 65535");
            return;
        }

        Integer portEnd = udpRange.getPortEnd();
        if (portEnd == null || portEnd < UdpRange.PORT_BEGIN || portEnd > UdpRange.PORT_END) {
            errors.reject(CodeStatus.UDP_RANGE_ERROR_PORT_END.name(), "UdpRange portEnd must gt 1 and lt 65535");
            return;
        }

        if (portBegin > portEnd) {
            errors.reject(CodeStatus.UDP_RANGE_ERROR_PORT_RANGE.name(), "UdpRange portBegin can not be great than portEnd");
            return;
        }

        try {
            Long longIpBgn = IPUtil.ipToLong(ipBegin);
            if (longIpBgn < UdpRange.IP_BEGIN) {
                errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_RANGE.name(), "UdpRange ipBegin can not be less than 224.0.1.0");
                return;
            }
            else if (longIpBgn > UdpRange.IP_END) {
                errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_RANGE.name(), "UdpRange ipBegin can not be great than 239.255.255.255");
                return;
            }

            Long longIpEnd = IPUtil.ipToLong(ipEnd);
            if (longIpEnd < UdpRange.IP_BEGIN) {
                errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_RANGE.name(), "UdpRange ipEnd can not be less than 224.0.1.0");
                return;
            }
            else if (longIpEnd > UdpRange.IP_END) {
                errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_RANGE.name(), "UdpRange ipEnd can not be great than 239.255.255.255");
                return;
            }

            if (longIpBgn > longIpEnd) {
                errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_RANGE.name(), "UdpRange ipBegin can not be great than ipEnd");
                return;
            }
        } catch (Exception e) {
            errors.reject(CodeStatus.UDP_RANGE_ERROR_IP_RANGE.name(), e.getMessage());
        }
    }
}
