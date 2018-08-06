package com.arcvideo.pgcliveplatformserver.validator;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.ContentTemplateRepo;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.util.UriUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by slw on 2018/4/25.
 */
@Component
public class ContentValidator implements Validator {

    @Autowired
    private SettingService settingService;

    @Autowired
    private ContentTemplateRepo contentTemplateRepo;

    @Override
    public boolean supports(Class<?> aClass) {
        return Content.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Content content = (Content) o;

        if (StringUtils.isBlank(content.getName())) {
            errors.reject(CodeStatus.CONTENT_ERROR_NAME_EMPTY.name(), "content name is empty");
            return;
        }

        Channel master = content.getMaster();
        if (master == null) {
            errors.reject(CodeStatus.CONTENT_ERROR_MASTER_IS_NULL.name(), "content master_is_null");
            return;
        }
        else {
            if (master.getStreamType() == CommonConstants.STREAM_TYPE_PULL) {
                if (StringUtils.isBlank(master.getSourceUri())) {
                    errors.reject(CodeStatus.CONTENT_ERROR_SOURCE_URI_EMPTY.name(), "content master sourceUri is empty");
                    return;
                } else if (!master.getSourceUri().matches(CommonConstants.RegEx_RTMP)) {
                    errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_RTMP.name(), "content master sourceUri regex rtmp error");
                    return;
                }
            }

            if (master.getStreamType() == CommonConstants.STREAM_TYPE_UDP) {
                if (StringUtils.isBlank(master.getSourceUri())) {
                    errors.reject(CodeStatus.CONTENT_ERROR_SOURCE_URI_EMPTY.name(), "content master sourceUri is empty");
                    return;
                } else if (!master.getSourceUri().matches(CommonConstants.RegEx_UDP)) {
                    errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_UDP.name(), "content master sourceUri regex rtmp error");
                    return;
                }

                if (master.getProgramId() == null) {
                    errors.reject(CodeStatus.CONTENT_ERROR_PROGRAM_ID_IS_NULL.name(), "content master programId is empty");
                    return;
                }
                if (master.getAudioId() == null) {
                    errors.reject(CodeStatus.CONTENT_ERROR_AUDIO_ID_IS_NULL.name(), "content master audioId is empty");
                    return;
                }
                if (master.getSubtitleId() == null) {
                    master.setSubtitleId(-2);
                }
            }

            if (settingService.getEnableDelayer() && (master.getDuration() != null && master.getDuration() < 0)) {
                errors.reject(CodeStatus.CONTENT_ERROR_DELAY_DURATION_VALUE.name(), "content master duration value is wrong");
                return;
            }
        }

        Channel slave = content.getSlave();
        if (content.getEnableSlave()) {
            if (slave == null) {
                errors.reject(CodeStatus.CONTENT_ERROR_SLAVE_IS_NULL.name(), "content slave is empty");
                return;
            }
            else {
                if (slave.getStreamType() == CommonConstants.STREAM_TYPE_PULL) {
                    if (StringUtils.isBlank(slave.getSourceUri())) {
                        errors.reject(CodeStatus.CONTENT_ERROR_SOURCE_URI_EMPTY.name(), "content slave sourceUri is empty");
                        return;
                    } else if (!slave.getSourceUri().matches(CommonConstants.RegEx_RTMP)) {
                        errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_RTMP.name(), "content slave sourceUri regex rtmp error");
                        return;
                    }

                }

                if (slave.getStreamType() == CommonConstants.STREAM_TYPE_UDP) {
                    if (StringUtils.isBlank(slave.getSourceUri())) {
                        errors.reject(CodeStatus.CONTENT_ERROR_SOURCE_URI_EMPTY.name(), "content slave sourceUri is empty");
                        return;
                    } else if (!slave.getSourceUri().matches(CommonConstants.RegEx_UDP)) {
                        errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_UDP.name(), "content slave sourceUri regex udp error");
                        return;
                    }

                    if (slave.getProgramId() == null) {
                        errors.reject(CodeStatus.CONTENT_ERROR_PROGRAM_ID_IS_NULL.name(), "content slave programId is empty");
                        return;
                    }
                    if (slave.getAudioId() == null) {
                        errors.reject(CodeStatus.CONTENT_ERROR_AUDIO_ID_IS_NULL.name(), "content slave audioId is empty");
                        return;
                    }
                    if (slave.getSubtitleId() == null) {
                        slave.setSubtitleId(-2);
                    }
                }

                if (settingService.getEnableDelayer() && (slave.getDuration() != null && slave.getDuration() < 0)) {
                    errors.reject(CodeStatus.CONTENT_ERROR_DELAY_DURATION_VALUE.name(), "content slave duration value is wrong");
                    return;
                }
            }
        }

        String backup = content.getBackup();
        if (content.getEnableBackup()) {
            if (StringUtils.isBlank(backup)) {
                errors.reject(CodeStatus.CONTENT_ERROR_BACKUP_IS_NULL.name(), "content backup is empty");
                return;
            } else if (!backup.matches(CommonConstants.RegEx_UDP)) {
                errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_UDP.name(), "content backup regex udp error");
                return;
            }
        }

        List<LiveOutput> outputs = content.getOutputs();
        if (outputs != null) {
            for (LiveOutput output : outputs) {
                if (StringUtils.isBlank(output.getOutputUri())) {
                    errors.reject(CodeStatus.CONTENT_ERROR_OUTPUT_URI_EMPTY.name(), "content output's uri is empty");
                    return;
                } else {
                    try {
                        String protocol = UriUtil.getProtocol(output.getOutputUri());
                        output.setProtocol(protocol);

                        if (UriUtil.PROTOCOL_HLS.equals(protocol) && !output.getOutputUri().matches(CommonConstants.RegEx_HLS)) {
                            errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_HLS.name(), "content output's uri regex hls error");
                            return;
                        } else if (UriUtil.PROTOCOL_UDP.equals(protocol) && !output.getOutputUri().matches(CommonConstants.RegEx_UDP)) {
                            errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_UDP.name(), "content output's uri regex udp error");
                            return;
                        } else if (UriUtil.PROTOCOL_RTMP.equals(protocol) && !output.getOutputUri().matches(CommonConstants.RegEx_RTMP)) {
                            errors.reject(CodeStatus.CONTENT_ERROR_URI_REGEX_RTMP.name(), "content output's uri regex rtmp error");
                            return;
                        }
                    } catch (Exception e) {
                        errors.reject(CodeStatus.CONTENT_ERROR_OUTPUT_URI_PARSE.name(), "The protocol of uri is not supported, uri=" + output.getOutputUri());
                        return;
                    }
                }

                if (output.getTemplateId() == null) {
                    errors.reject(CodeStatus.CONTENT_ERROR_OUTPUT_TEMPLATE_ID_NOT_FOUND.name(), "content output's templateId is null");
                    return;
                } else {
                    ContentTemplate contentTemplate = contentTemplateRepo.findOne(output.getTemplateId());
                    if (contentTemplate == null) {
                        errors.reject(CodeStatus.CONTENT_ERROR_OUTPUT_TEMPLATE_ID_NOT_FOUND.name(), "content output's template is not found, templateId=" + output.getTemplateId());
                        return;
                    }
                }
            }
        } else {
            errors.reject(CodeStatus.CONTENT_ERROR_OUTPUT_URI_EMPTY.name(), "content outputs is null");
            return;
        }

        List<LiveLogo> logos = content.getLogos();
        if (logos != null) {
            List<LiveLogo> liveLogos = logos.stream().filter(logo -> logo.getPosType() != null && logo.getMaterialId() != null).collect(Collectors.toList());
            content.setLogos(liveLogos);
        }

        List<MotionIcon> icons = content.getIcons();
        if (icons != null) {
            List<MotionIcon> motionIcons = icons.stream().filter(icon -> icon.getPosType() != null && icon.getMaterialId() != null).collect(Collectors.toList());
            content.setIcons(motionIcons);
        }
    }
}
