package com.arcvideo.pgcliveplatformserver.model.mediainfo;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;

public class MediaInfo
{
    private String container;
    private long size;
    private int programsSize;
    private List<Program> programs;
    private Video video;
    private Audio audio;
    private Subtitle subtitle;
    private long duration;

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getProgramsSize() {
        return programsSize;
    }

    public void setProgramsSize(int programsSize) {
        this.programsSize = programsSize;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public Subtitle getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Subtitle subtitle) {
        this.subtitle = subtitle;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean parse(String fileName) {
        Document document = null;
        try {
            document = new SAXReader().read(fileName);
            return analyze(document);
        } catch (DocumentException e) {
        }
        return false;
    }

    private boolean analyze(Document document)
    {
        try
        {
            Element root = document.getRootElement();
            Element temp = root.element("container");
            if (temp != null) {
                setContainer(temp.getText());
            }
            temp = root.element("size");
            if (temp != null) {
                setSize(Long.parseLong(temp.getText()));
            }
            //programs
            Element programsElem = root.element("programs");
            if (programsElem != null) {
                List<Element> psElem = (List<Element>)programsElem.elements("program");
                setProgramsSize(psElem.size());
                List<Program> programs = new ArrayList<Program>();
                for (Element pElem : psElem)
                {
                    Program program = new Program();
                    // name
                    temp = pElem.element("name");
                    if (temp != null) {
                        program.setName(temp.getText());
                    }
                    // used
                    temp = pElem.element("used");
                    if (temp != null) {
                        program.setUsed(temp.getText());
                    }
                    // program ID
                    program.setPid(Integer.parseInt(pElem.attributeValue("idx")));

                    getProgramInfo(program, pElem);
                    programs.add(program);
                }
                setPrograms(programs);
            }
            else {
                Element videoElem = root.element("video");
                if (videoElem != null) {
                    video = getVideo(videoElem);
                }
                Element audioElem = root.element("audio");
                if (audioElem != null) {
                    audio = getAudio(audioElem);
                }
                Element subtitleElem = root.element("subtitle");
                if (subtitleElem != null) {
                    subtitle = getSubtitle(subtitleElem);
                }
                Element durationElement = root.element("duration");
                if (durationElement != null) {
                    setDuration(Long.parseLong(durationElement.getText()));
                }
            }
            return true;
        }
        catch (Exception e) {
        }
        return false;
    }

    private void getProgramInfo(Program program, Element element) {
        Element videosElem = element.element("videos");
        if (videosElem != null) {
            List<Video> videos = getVideos(videosElem);
            program.setVideoSize(videos.size());
            program.setVideos(videos);
        }
        // audios
        Element audiosElem = element.element("audios");
        if (audiosElem != null) {
            List<Audio> audios = getAudios(audiosElem);
            program.setAudioSize(audios.size());
            program.setAudios(audios);
        }

        // subtitles
        Element subtitlesElem = element.element("subtitles");
        if (subtitlesElem != null) {
            List<Subtitle> subtitleList = getSubtitles(subtitlesElem);
            program.setSubtitleSize(subtitleList.size());
            program.setSubtitles(subtitleList);
        }
    }

    private Video getVideo(Element videoElem) {
        Element temp = null;
        Video video = new Video();
        // pid
        temp = videoElem.element("pid");
        if (temp != null) {
            video.setPid(Integer.parseInt(temp.getText()));
        }
        //name
        temp = videoElem.element("name");
        if (temp != null) {
            video.setName(temp.getText());
        }
        //used
        temp = videoElem.element("used");
        if (temp != null) {
            video.setUsed(temp.getText());
        }
        // codec
        temp = videoElem.element("codec");
        if (temp != null) {
            video.setCodec(temp.getText());
        }
        // duration
        temp = videoElem.element("duration");
        if (temp != null) {
            video.setDuration(temp.getText());
        }
        // bitrate
        temp = videoElem.element("bitrate");
        if (temp != null) {
            video.setBitrate(temp.getText());
        }
        // framerate
        temp = videoElem.element("framerate");
        if (temp != null) {
            video.setFrameRate(temp.getText());
        }
        // resolution
        temp = videoElem.element("resolution");
        if (temp != null) {
            video.setResolution(temp.getText());
        }
        // aspect_ratio
        temp = videoElem.element("aspect_ratio");
        if (temp != null) {
            video.setAspectRatio(temp.getText());
        }
        // rotation
        temp = videoElem.element("Rotation");
        if (temp != null) {
            video.setRotation(temp.getText());
        }
        return video;
    }

    private List getVideos(Element videosElem) {
        List<Element> vsElem = videosElem.elements("video");
        List<Video> videos = new ArrayList<Video>();
        for (Element vElem: vsElem) {
            Video video = getVideo(vElem);
            videos.add(video);
        }
        return videos;
    }

    private Audio getAudio(Element audioElem) {
        Element temp = null;
        Audio audio = new Audio();
        // pid
        temp = audioElem.element("pid");
        if (temp != null) {
            audio.setPid(Integer.parseInt(temp.getText()));
        }
        //name
        temp = audioElem.element("name");
        if (temp != null) {
            audio.setName(temp.getText());
        }
        //language
        temp = audioElem.element("language");
        if (temp != null) {
            audio.setLanguage(temp.getText());
        }
        //used
        temp = audioElem.element("used");
        if (temp != null) {
            audio.setUsed(temp.getText());
        }
        // codec
        temp = audioElem.element("codec");
        if (temp != null) {
            audio.setCodec(temp.getText());
        }
        // duration
        temp = audioElem.element("duration");
        if (temp != null) {
            audio.setDuration(temp.getText());
        }
        // bitrate
        temp = audioElem.element("bitrate");
        if (temp != null) {
            audio.setBitrate(temp.getText());
        }
        //channel
        temp = audioElem.element("channel");
        if (temp != null) {
            audio.setChannel(temp.getText());
        }
        // samplerate
        temp = audioElem.element("samplerate");
        if (temp != null) {
            audio.setSampleRate(temp.getText());
        }
        // bitdepth
        temp = audioElem.element("bitdepth");
        if (temp != null) {
            audio.setBitDepth(temp.getText());
        }
        return audio;
    }

    private List getAudios(Element audiosElem) {
        List<Element> asElem = audiosElem.elements("audio");
        List<Audio> audios = new ArrayList<Audio>();
        for (Element aElem: asElem) {
            Audio audio = getAudio(aElem);
            audios.add(audio);
        }
        return audios;
    }

    private Subtitle getSubtitle(Element subtitleElem) {
        Element temp = null;
        Subtitle subtitle = new Subtitle();
        // pid
        temp = subtitleElem.element("pid");
        if (temp != null) {
            subtitle.setPid(Integer.parseInt(temp.getText()));
        }
        // name
        temp = subtitleElem.element("name");
        if (temp != null) {
            subtitle.setName(temp.getText());
        }
        // language
        temp = subtitleElem.element("language");
        if (temp != null) {
            subtitle.setLanguage(temp.getText());
        }
        // used
        temp = subtitleElem.element("used");
        if (temp != null) {
            subtitle.setUsed(temp.getText());
        }
        return subtitle;
    }

    private List getSubtitles(Element subtitlesElem) {
        List<Element> ssElem = subtitlesElem.elements("subtitle");
        List<Subtitle> subtitles = new ArrayList<Subtitle>();
        for (Element sElem: ssElem) {
            Subtitle subtitle = getSubtitle(sElem);
            subtitles.add(subtitle);
        }
        return subtitles;
    }

    public boolean isValid() {
        return video != null || audio != null || (programs != null && programs.size() > 0);
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "container='" + container + '\'' +
                ", size=" + size +
                ", programsSize=" + programsSize +
                ", programs=" + programs +
                ", video=" + video +
                ", audio=" + audio +
                ", subtitle=" + subtitle +
                ", duration=" + duration +
                '}';
    }
}
