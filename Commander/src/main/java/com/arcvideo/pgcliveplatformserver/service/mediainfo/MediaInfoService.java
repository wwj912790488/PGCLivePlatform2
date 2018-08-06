package com.arcvideo.pgcliveplatformserver.service.mediainfo;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.LiveTask;
import com.arcvideo.pgcliveplatformserver.model.mediainfo.*;
import com.arcvideo.pgcliveplatformserver.repo.LiveTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.live.LiveTaskService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.util.UuidUtil;
import com.arcvideo.pgcliveplatformserver.util.exec.CommanderExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by slw on 2018/3/30.
 */
@Service
public class MediaInfoService {
    Logger logger = LoggerFactory.getLogger(MediaInfoService.class);

    private static final String MEDIAINFO_EXE_NAME = "mediaanalyze.exe";
    private static long timeout = 6000;

    private static final String LIVE_TASK_GET_THUMBNAIL_URL = "{server_address}/api/task/{id}/progress_thumb";

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private LiveTaskRepo liveTaskRepo;

    @Autowired
    private RestTemplate restTemplate;

    public MediaInfo getMediaInfo(String inputPath) {
        if (StringUtils.isEmpty(inputPath)) {
            return null;
        }
        Path exePath = Paths.get(settingService.getTranscoderDir(), MEDIAINFO_EXE_NAME);
        Path tempPath = Paths.get(FileUtils.getTempDirectoryPath(), UuidUtil.getUuid() + "_temp");
        String[] commandLine = {exePath.toString(), "-i", inputPath, "-o", tempPath.toString()};
        logger.info("getmediainfo cmd = {}", StringUtils.join(commandLine, " "));
        try {
            int exitValue = CommanderExecutor.execute(commandLine, exePath.getParent().toFile(), timeout);
            logger.info("getmediainfo exitValue = {}", exitValue);
        } catch (IOException e) {
            logger.error("getmediainfo error {}", e);
        }

        if (Files.exists(tempPath)) {
            MediaInfo mediaInfo = new MediaInfo();
            boolean flag = mediaInfo.parse(tempPath.toString());
            logger.info("mediainfo parse ret={}", flag);
            FileUtils.deleteQuietly(tempPath.toFile());

            if (flag) {
                return mediaInfo;
            }
        }
        return null;
    }

    public String getVideoInfo(MediaInfo mediaInfo) {
        String videoInfo = "";
        if (mediaInfo != null) {
            if (mediaInfo.getPrograms() != null) {
                Program program = mediaInfo.getPrograms().get(0);
                if (program.getVideoSize() > 0) {
                    videoInfo += program.getVideos().get(0).getCodec() +
                            " " + program.getVideos().get(0).getResolution() +
                            " " + program.getVideos().get(0).getAspectRatio() +
                            " " + program.getVideos().get(0).getFrameRate() +
                            " " + program.getVideos().get(0).getBitrate();
                }
                if (program.getAudioSize() > 0) {
                    videoInfo += " | " + program.getAudios().get(0).getCodec() +
                            " " + program.getAudios().get(0).getChannel() +
                            " " + program.getAudios().get(0).getSampleRate() +
                            " " + program.getAudios().get(0).getBitrate();
                }
                if (program.getSubtitleSize() > 0) {
                    videoInfo += " | " + program.getSubtitles().get(0).getName() +
                            "& " + program.getSubtitles().get(0).getLanguage();
                }
            } else {
                if (mediaInfo.getVideo() != null) {
                    Video video = mediaInfo.getVideo();
                    videoInfo += video.getCodec() +
                            " " + video.getResolution() +
                            " " + video.getAspectRatio() +
                            " " + video.getFrameRate() +
                            " " + video.getBitrate();
                }
                if (mediaInfo.getAudio() != null) {
                    Audio audio = mediaInfo.getAudio();
                    videoInfo += " | " + audio.getCodec() +
                            " " + audio.getChannel() +
                            " " + audio.getSampleRate() +
                            " " + audio.getBitrate();
                }
                if (mediaInfo.getSubtitle() != null) {
                    Subtitle subtitle = mediaInfo.getSubtitle();
                    videoInfo += " | " + subtitle.getName() +
                            "& " + subtitle.getLanguage();
                }
            }
        }

        return videoInfo;
    }

    public byte[] getLiveTaskThumb(Long taskId) {
        LiveTask liveTask = liveTaskRepo.findFirstByContentId(taskId);
        if (liveTask != null && liveTask.getLiveTaskId() != null) {
            ResponseEntity<byte[]> response = restTemplate.exchange(LIVE_TASK_GET_THUMBNAIL_URL, HttpMethod.GET, null, byte[].class, serverSettingService.getLiveServerAddress(), liveTask.getLiveTaskId());
            if (response != null && response.getBody() != null) {
                return response.getBody();
            }
        }

        return null;
    }

}
