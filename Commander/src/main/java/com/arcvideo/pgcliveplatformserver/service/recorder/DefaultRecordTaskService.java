package com.arcvideo.pgcliveplatformserver.service.recorder;

import com.arcvideo.pgcliveplatformserver.entity.RecoderProfile;
import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import com.arcvideo.pgcliveplatformserver.model.dashboard.RecordInfo;
import com.arcvideo.pgcliveplatformserver.repo.RecorderTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.task.TaskQueueDispatcher;
import com.arcvideo.pgcliveplatformserver.specfication.CommonSpecfication;
import com.arcvideo.rabbit.message.RecorderMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by slw on 2018/3/22.
 */
@Service
public class DefaultRecordTaskService implements RecorderTaskService {

    @Autowired
    private TaskQueueDispatcher taskQueueDispatcher;

    @Autowired
    private RecorderTaskRepo repo;

    @Autowired
    private RecorderHttpCommander httpCommander;

    @Override
    public RecorderTask findOne(Long recordId) {
        RecorderTask recorderTask = repo.findOne(recordId);
        return recorderTask;
    }

    @Transactional
    public Boolean saveRecorder(RecorderTask recorderTask) {
        if (recorderTask.getId() == null) {
            repo.save(recorderTask);
        }
        else {
            RecorderTask oldTask = repo.findOne(recorderTask.getId());
            oldTask.setRecorderTaskStatus(RecorderTask.Status.PENDING);
            oldTask.setContentId(recorderTask.getContentId());
            oldTask.setTemplateId(recorderTask.getTemplateId());
            oldTask.setSegmentLength(recorderTask.getSegmentLength());
            oldTask.setOutputPath(recorderTask.getOutputPath());
            oldTask.setFileName(recorderTask.getFileName());
            oldTask.setStartTime(recorderTask.getStartTime());
            oldTask.setEndTime(recorderTask.getEndTime());
            oldTask.setEnableThumb(recorderTask.getEnableThumb());
            oldTask.setThumbWidth(recorderTask.getThumbWidth());
            oldTask.setKeepTimes(recorderTask.getKeepTimes());
            repo.save(oldTask);
            recorderTask = oldTask;
        }
        return true;
    }

    @Override
    public Boolean addRecorder(RecorderTask recorderTask) {
        Boolean flag = saveRecorder(recorderTask);
        if (!flag)
            return false;

        RecorderMessage recorderMessage = new RecorderMessage(RecorderMessage.Type.start, recorderTask.getContentId(), recorderTask.getId());
        taskQueueDispatcher.addTask(recorderMessage);
        return true;
    }

    @Override
    public Boolean updateRecorder(RecorderTask recorderTask) {
        Boolean flag = saveRecorder(recorderTask);
        if (!flag)
            return false;

        RecorderMessage recorderMessage = new RecorderMessage(RecorderMessage.Type.editor, recorderTask.getContentId(), recorderTask.getId());
        taskQueueDispatcher.addTask(recorderMessage);
        return true;
    }

    @Override
    public Boolean startRecorder(Long recorderId) {
        RecorderTask recorderTask = repo.findOne(recorderId);
        if (recorderTask == null) {
            return false;
        }

        recorderTask.setRecorderTaskStatus(RecorderTask.Status.RUNNING);
        repo.save(recorderTask);
        RecorderMessage recorderMessage = new RecorderMessage(RecorderMessage.Type.start, recorderTask.getContentId(), recorderTask.getId());
        taskQueueDispatcher.addTask(recorderMessage);
        return true;
    }

    @Override
    public Boolean stopRecorder(Long recorderId) {
        RecorderTask recorderTask = repo.findOne(recorderId);
        if (recorderTask == null) {
            return false;
        }

        recorderTask.setRecorderTaskStatus(RecorderTask.Status.STOPPED);
        repo.save(recorderTask);
        RecorderMessage recorderMessage = new RecorderMessage(RecorderMessage.Type.stop, recorderTask.getContentId(), recorderTask.getId());
        taskQueueDispatcher.addTask(recorderMessage);
        return true;
    }

    @Override
    public Boolean removeRecorder(Long recorderId) {
        RecorderTask recorderTask = repo.findOne(recorderId);
        if (recorderTask == null) {
            return false;
        }

        recorderTask.setDeleted(true);
        repo.save(recorderTask);
        RecorderMessage recorderMessage = new RecorderMessage(RecorderMessage.Type.delete, recorderTask.getContentId(), recorderTask.getId());
        taskQueueDispatcher.addTask(recorderMessage);
        return true;
    }

    @Override
    public Page<RecorderTask> listRecord(Pageable page) {
        Page<RecorderTask> list = repo.findAll(page);
        return list;
    }

    @Override
    public Page<RecorderTask> listRecord(Specification<RecorderTask> specification, Pageable page) {
        Page<RecorderTask> list = repo.findAll(specification, page);
        return list;
    }

    @Override
    public List<RecoderProfile> listRecordTemplate() {
        return httpCommander.getRecoderProfiles();
    }

    @Override
    public RecordInfo getRecordInfo() {
        RecordInfo recordInfo = new RecordInfo();
        Integer normalCount = 0;
        Integer completeCount = 0;
        Integer alertCount = 0;
        List<RecorderTask> tasks = repo.findAll(CommonSpecfication.findAllPermitted());
        for(RecorderTask task:tasks){
            if(task.getRecorderTaskStatus() == RecorderTask.Status.RUNNING){
                normalCount++;
            }else if(task.getRecorderTaskStatus() == RecorderTask.Status.STOPPED){
                completeCount++;
            }else {
                alertCount++;
            }
        }
        recordInfo.setNormalCount(normalCount);
        recordInfo.setCompleteCount(completeCount);
        recordInfo.setAlertCount(alertCount);
        recordInfo.setTotal(tasks.size());
        return recordInfo;
    }
}
