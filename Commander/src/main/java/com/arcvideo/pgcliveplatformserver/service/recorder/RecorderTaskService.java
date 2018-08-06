package com.arcvideo.pgcliveplatformserver.service.recorder;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.RecoderProfile;
import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import com.arcvideo.pgcliveplatformserver.model.dashboard.RecordInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Created by slw on 2018/3/22.
 */
public interface RecorderTaskService {
    RecorderTask findOne(Long recordId);
    Boolean addRecorder(RecorderTask recorderTask);
    Boolean updateRecorder(RecorderTask recorderTask);
    Boolean startRecorder(Long recorderId);
    Boolean stopRecorder(Long recorderId);
    Boolean removeRecorder(Long recorderId);
    Page<RecorderTask> listRecord(Pageable page);
    Page<RecorderTask> listRecord(Specification<RecorderTask> specification, Pageable page);
    List<RecoderProfile> listRecordTemplate();

    RecordInfo getRecordInfo();
}
