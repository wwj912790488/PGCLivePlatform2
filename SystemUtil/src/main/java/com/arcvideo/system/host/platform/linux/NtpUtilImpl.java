package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.NtpUtil;
import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.arcvideo.system.model.NTPStatus;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NtpUtilImpl implements NtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(NtpUtilImpl.class);

    @Override
    public NTPStatus getNTPStatus() {
        NTPStatus ntpStatus = new NTPStatus();
        //get service status;
        List<String> lines = ExecutingCommand.runShellNative("service ntpd status");
        for (String line : lines) {
            if (line.contains("running")) {
                ntpStatus.setServerRunning(true);
                break;
            }
        }
        //get ntp servers
        ntpStatus.setNtpServers(getNtpServers());
        return ntpStatus;
    }

    private List<String> getNtpServers() {
        List<String> ntpServersList = new ArrayList<String>();
        List<String> ntpFileStringList = FileUtil.readFile("/etc/ntp.conf");
        for (String strContent : ntpFileStringList) {
            if (strContent.matches("^server\\s.*$")) {
                String[] results = strContent.split("\\s+");
                if (results.length > 1 && results[1].length() > 0) {
                    ntpServersList.add(results[1]);
                }
            }
        }
        return ntpServersList;
    }

    @Override
    public Integer setNTPServers(List<String> servers) {
        String filename = "/etc/ntp.conf";
        String filetmp = "/etc/ntp.conf.tmp";
        StringBuffer sb = new StringBuffer();
        Integer status = 0;
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String strContent = null;
                while ((strContent = br.readLine()) != null) {
                    if (strContent.startsWith("server")) {
                        strContent = "";
                    }
                    sb.append(strContent).append("\n");
                }
            }
        } catch (IOException e) {
            status = 1;
            e.printStackTrace();
        }
        sb.append("server 127.127.1.0 "+"\n");
        for (String server : servers) {
            sb.append("server " + server + "\n");
        }
        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filetmp))) {
                bw.write(sb.toString());
            }
        } catch (IOException e) {
            status = 2;
            e.printStackTrace();
        }

        File file = new File(filename);
        File tmp = new File(filetmp);
        file.delete();
        tmp.renameTo(file);
        return status;
    }

    @Override
    public boolean updateNTPServer(String ip) {
        String cmd = "ntpdate -u " + ip;
        try {
            ExecutingCommand.runShellNative(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean setSystemTime(Date date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String strDate = df.format(date);

            String cmd = "date -s \"" + strDate + "\"";
            ExecutingCommand.runShellNative(cmd);
            //write the time to CMOS
            ExecutingCommand.runShellNative("clock -w");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean syncWithNTP(NTPStatus ntp) {
        String cmd = null;
        // to start ntp service
        if (ntp.getServerRunning()) {
            List<String> servers = ntp.getNtpServers();
            if (servers.size() == 0)
                return true;

            // first: stop ntpd service
            cmd = "service ntpd stop";
            ExecutingCommand.runShellNative(cmd);

            // force to sync right now
            cmd = "ntpdate -u " + servers.get(0);
            ExecutingCommand.runShellNative(cmd);

            // add server address to conf
            setNTPServers(servers);

            // start the ntp service
            cmd = "service ntpd start";
            ExecutingCommand.runShellNative(cmd);

            // start service on startup
            cmd = "chkconfig ntpd on";
            ExecutingCommand.runShellNative(cmd);
        } else {
            //stop ntp service
            cmd = "service ntpd stop";
            ExecutingCommand.runShellNative(cmd);
        }
        return true;
    }
}
