package com.arcvideo.pgcliveplatformserver.validator;

import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

/**
 * Created by slw on 2018/5/8.
 */
@Component
public class RecorderValidtor implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return RecorderTask.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RecorderTask recorder = (RecorderTask) target;

        if (recorder.getContentId() == null && recorder.getContentId() <= 0) {
            errors.rejectValue("contentId", "validate.recorder.contentId.empty", "recorder's contentId can not be empty or le zero");
        }

        if (recorder.getTemplateId() == null && recorder.getTemplateId() <= 0) {
            errors.rejectValue("templateId", "validate.recorder.templateId.empty", "recorder's templateId can not be empty or le zero");
        }

        if (recorder.getSegmentLength() == null && recorder.getSegmentLength() <= 0) {
            errors.rejectValue("segmentLength", "validate.recorder.segmentLength.empty", "recorder's segmentLength can not be empty or le zero");
        }

        if (StringUtils.isBlank(recorder.getOutputPath())) {
            errors.rejectValue("outputPath", "validate.recorder.outputPath.empty", "recorder's outputPath can not be empty");
        }

        if (StringUtils.isBlank(recorder.getFileName())) {
            errors.rejectValue("fileName", "validate.recorder.fileName.empty", "recorder's fileName can not be empty");
        }

        Date startTime = recorder.getStartTime();
        Date endTime = recorder.getEndTime();
        if (startTime != null && endTime != null) {
            if (startTime.getTime() > endTime.getTime()) {
                errors.reject("validate.recorder.startTimeAndEndTime.error", "The end time must be greater than or equal to the start time");
            }
        }
    }
}
