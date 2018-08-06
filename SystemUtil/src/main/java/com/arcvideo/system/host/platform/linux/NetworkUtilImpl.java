package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.arcvideo.system.host.NetworkUtil;
import com.arcvideo.system.model.Eth;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtilImpl implements NetworkUtil {
    private static final Logger logger = LoggerFactory.getLogger(NetworkUtilImpl.class);

    private static final String PREFIX_CFGETH = "ifcfg-";

    private static File getNetworkscriptDir() {
        return new File("/etc/sysconfig/network-scripts");
    }

    private static String decodeProp(String val) {
        return val == null ? null : val.replace('"', ' ').trim();
    }

    private static boolean isStaticIp(Properties prop) {
        String bootproto = prop.getProperty("BOOTPROTO");
        return !("dhcp".equalsIgnoreCase(decodeProp(bootproto)));
    }

    private static Properties loadEth(File ethi) throws IOException {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(ethi)) {
            prop.load(fis);
        }
        return prop;
    }

    private static void storeEth(Properties prop, File ethi) throws IOException {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(ethi), "utf-8");
             PrintWriter writer = new PrintWriter(outputStreamWriter)) {
            for (String name : prop.stringPropertyNames()) {
                writer.println(name + "=" + prop.getProperty(name));
            }
        }
    }

    private static Eth propToEth(Properties prop) {
        Eth eth = new Eth(decodeProp(prop.getProperty("DEVICE")));
        boolean staticIP = isStaticIp(prop);
        eth.setIsDHCP(!staticIP);
        String name = prop.getProperty("NAME");
        eth.setName(name == null ? "" : name.replace("\"", ""));
        if (staticIP) {
            eth.setIp(decodeProp(prop.getProperty("IPADDR")));
            eth.setMask(decodeProp(prop.getProperty("NETMASK")));
            eth.setGateway(decodeProp(prop.getProperty("GATEWAY")));
            eth.setDns1(decodeProp(prop.getProperty("DNS1")));
            eth.setDns2(decodeProp(prop.getProperty("DNS2")));
        }
        if ("yes".equalsIgnoreCase(decodeProp(prop.getProperty("SLAVE")))
                || "yes".equalsIgnoreCase(decodeProp(prop.getProperty("slave")))) {
            eth.setMaster(decodeProp(prop.getProperty("MASTER")));
        }
        if (decodeProp(prop.getProperty("BONDING_OPTS")) != null && eth != null) {
            Map<String, String> options = parseSubOptions(decodeProp(prop.getProperty("BONDING_OPTS")));
            eth.setMode(StringHelper.toInteger(options.get("mode"), -1));
            eth.setPrimary(options.get("primary"));
            eth.setPrimaryReselect(StringHelper.toInteger(options.get("primary_reselect")));
        }
        return eth;
    }

    private static void fillEth(Eth eth) {
        List<String> lines = ExecutingCommand.runShellNative("ifconfig " + eth.getId());
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("inet addr:")) {
                if (eth.getIsDHCP()) {
                    String[] arr = line.split("\\s");
                    for (int i = 0; i < arr.length; i++) {
                        if (arr[i].startsWith("addr:")) {
                            eth.setIp(arr[i].substring(arr[i].indexOf(':') + 1).trim());
                        } else if (arr[i].startsWith("Mask:")) {
                            eth.setMask(arr[i].substring(arr[i].indexOf(':') + 1).trim());
                        }
                    }
                }
            } else if (line.indexOf("RUNNING") != -1) {
                eth.setStatus("RUNNING");
            }
            eth.setActivity(getActivityEthID(eth.getId()));
        }
    }

    @Override
    public List<Eth> findAllEths(){
        // return eths;
        ArrayList<Eth> ret = new ArrayList<Eth>();
        // find all bond eths
        ret.addAll(findAllEths(true));
        // find all free eths
        ret.addAll(findAllEths(false));
        return ret;
    }

    @Override
    public List<Eth> findAllEths(boolean isbond) {
        try {
            final File dir = getNetworkscriptDir();
            final boolean isbondfinal = isbond;
            String[] names = dir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    String regex = null;
                    if (isbondfinal) {
                        regex = "ifcfg-bond\\d+$";
                    } else {
                        regex = "ifcfg-eth\\d+$";
                    }
                    if (name.matches(regex)) {
                        return true;
                    }
                    return false;
                }
            });
            ArrayList<Eth> ret = new ArrayList<Eth>();
            if (names != null) {
                Arrays.sort(names);
                for (int i = 0; i < names.length; i++) {
                    File ethi = new File(dir, names[i]);
                    Eth theEth = propToEth(loadEth(ethi));
                    theEth.setIsbond(isbond);
                    if (theEth.getId() == null) {
                        theEth.setId(names[i].substring(names[i].lastIndexOf('-') + 1));
                    }
                    fillEth(theEth);
                    if (!isbond) {
                        int sp = getEthSpeed(theEth.getId());
                        theEth.setSpeed(sp < 1024 ? sp + "Mbps" : sp / 1024 + "Gbps");
                    }
                    ret.add(theEth);
                }
            }
            return ret;
        } catch (IOException ie) {
            logger.error("listEth io error: " + ie.getMessage() + ie.getCause());
        } catch (Exception e) {
            logger.error("listEth other error: " + e.getMessage() + e.getCause());
        }
        return null;
    }

    private static void restartEth(String ethId) {
        ExecutingCommand.runShellNative("ifdown " + ethId);
        ExecutingCommand.runShellNative("ifup " + ethId);
    }

    @Override
    public void updateEth(Eth eth){
        try {
            long start = System.currentTimeMillis();
            logger.debug("updateEth start");
            boolean changed = false;
            File ethi = new File(getNetworkscriptDir(), PREFIX_CFGETH + eth.getId());
            // Properties prop = loadEth(ethi);
            Properties prop = null;
            if (ethi.exists()) {
                prop = loadEth(ethi);
            } else {
                prop = new Properties();
                prop.setProperty("DEVICE", eth.getId());
            }
            logger.debug("updateEth,id = " + eth.getId() + ",primary = " + eth.getPrimary() + ",isBond = " + eth.getIsbond());
            // check ip static
            if (isStaticIp(prop) == eth.getIsDHCP()) {
                prop.setProperty("BOOTPROTO", eth.getIsDHCP() ? "dhcp" : "static");
                changed = true;
            }

            String[][] editable = {{"IPADDR", eth.getIp()}, {"NETMASK", eth.getMask()},
                    {"GATEWAY", eth.getGateway()}, {"TYPE", "Ethernet"}, {"ONBOOT", "yes"}, {"NM_CONTROLLED", "no"}};

            for (int i = 0; i < editable.length; i++) {
                String k = editable[i][0];
                String val1 = editable[i][1];
                String val0 = decodeProp(prop.getProperty(k));
                if (val1 == null || val1.length() == 0) {
                    if (val0 != null) {
                        prop.remove(k);
                        changed = true;
                    }
                } else if (!val1.equalsIgnoreCase(val0)) {
                    prop.setProperty(k, val1);
                    changed = true;
                }
            }

            Map<String, String> bondingOptionsMap = new LinkedHashMap<>();
            if (eth.getIsbond()) {
                bondingOptionsMap.put("mode", String.valueOf(eth.getMode()));
                bondingOptionsMap.put("miimon", "100");
                if (eth.getMode() == 1) {
                    bondingOptionsMap.put("primary", eth.getPrimary());
                    if(eth.getPrimaryReselect()!= null){
                        bondingOptionsMap.put("primary_reselect", String.valueOf(eth.getPrimaryReselect()));
                    }
                }
            }

            // check the bonding options is changed or not.
            if (!changed && eth.getIsbond()) {
                String bondingOptions = decodeProp(prop.getProperty("BONDING_OPTS"));
                Map<String, String> options = parseSubOptions(bondingOptions);
                if (!compareSubOptionsMap(bondingOptionsMap, options)){
                    changed = true;
                }
            }

            if (changed) {
                if (eth.getIsbond()){
                    prop.setProperty("BONDING_OPTS", buildSubOptions(bondingOptionsMap));
                }
                storeEth(prop, ethi);
                restartEth(eth.getId());
            }
            long spend = System.currentTimeMillis() - start;
            logger.debug("updateEth end, spend: " + spend + "(ms)");
        } catch (IOException ie) {
            logger.error("updateEth io error: " + ie.getMessage() + ie.getCause());
        } catch (Exception e) {
            logger.error("updateEth other error: " + e.getMessage() + e.getCause());
        }
    }

    private static String buildSubOptions(Map<String, String> options) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\"");
        boolean hasOption = false;
        for (Map.Entry<String, String> option : options.entrySet()) {
            if (option.getValue() == null){
                continue;
            }
            if (hasOption){
                buffer.append(" ");
            }
            else{
                hasOption = true;
            }
            buffer.append(option.getKey()).append("=").append(option.getValue());
        }
        buffer.append("\"");
        return buffer.toString();
    }

    private static Pattern p = Pattern.compile("^\\s*([^\\s]*)\\s*=\\s*([^\\s]*)\\s*$");

    private static Map<String, String> parseSubOptions(String options) {
        Map<String, String> map = new LinkedHashMap<>();
        if (options != null) {
            String[] optionsArray = options.split("\\s+");
            for (String option : optionsArray) {
                Matcher m = p.matcher(option);
                if (m.matches()){
                    map.put(m.group(1), m.group(2));
                }
            }
        }
        return map;
    }

    private static boolean compareSubOptionsMap(Map<String, String> options1, Map<String, String> options2) {
        if (options1.size() != options2.size()){
            return false;
        }
        if (!options1.isEmpty()) {
            for (String option : options1.keySet()) {
                if (!options2.containsKey(option)){
                    return false;
                }
                if (!optionEquals(options1.get(option), options2.get(option))){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean optionEquals(String option1, String option2) {
        return (option1 != null) ? (option1.equalsIgnoreCase(option2)) : (option2 == null);
    }

    private static class EthSample {
        long bytesTime = 0;
        long bytesRx = 0;
        long bytesTx = 0;
        int ret = 0;
    }

    /**
     * eth speed cache - max 16
     */
    private int[] ethSpeed = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    /**
     * eth samples
     */
    private EthSample[] ethSamples = new EthSample[ethSpeed.length];

    private int getEthSpeed(String ethId) {
        int ethIndex = Integer.parseInt(ethId.substring(3));
        if (ethIndex < ethSpeed.length && ethSpeed[ethIndex] != 0) {
            return ethSpeed[ethIndex];
        }
        int ret = 0;
        String cmd = "ethtool eth" + ethIndex + " | grep Speed";
        String res = ExecutingCommand.getShellFirstAnswer(cmd);
        if (StringUtils.isNotEmpty(res)) {
            int p = res.indexOf(':');
            int p11 = res.indexOf("Mb");
            int p12 = res.indexOf("Gb");
            int p1 = p12 != -1 ? p12 : p11;
            if (p != -1 & p1 != -1) {
                ret = Integer.parseInt(res.substring(p + 1, p1).trim());
                if (p12 != -1){
                    ret *= 1024;
                }

            }

            if (ethIndex < ethSpeed.length) {
                ethSpeed[ethIndex] = ret;
            }
        }
        return ret;
    }

    private boolean checkEth(String ethId) {
        List<String> lines = ExecutingCommand.runShellNative("ifconfig " + ethId);
        for (String line : lines) {
            if (line.indexOf("RUNNING") != -1) {
                return true;
            }
        }
        return false;
    }

    private static String getActivityEthID(String masterID) {
        String activity = null;
        List<String> stringList = ExecutingCommand.runShellNative("cat /proc/net/bonding/" + masterID);
        for (String line : stringList) {
            line = line.trim();
            String[] arr = line.split("\\s");
            if (line.indexOf("Currently Active Slave:") != -1) {
                activity = arr[3].trim();
                logger.debug("getActivityEthID,activity = " + activity);
                break;
            }
        }
        return activity;
    }

    @Override
    public int getEthUsedRate(String ethId) throws IOException {
        try {
            int ethIndex = Integer.parseInt(ethId.substring(3));
            if (ethIndex < 0 || ethIndex >= ethSamples.length) {
                return 0;
            }
            //if eth is disconnected, return -1 directly
            if (!checkEth(ethId)) {
                //resetSpeed(ethId);
                return -1;
            }

            int ethSpeed = getEthSpeed(ethId);
            if (ethSpeed == 0){
                return 0;
            }


            if (ethSamples[ethIndex] == null) {
                ethSamples[ethIndex] = new EthSample();
            }

            long n = System.currentTimeMillis();
            long interval = n - ethSamples[ethIndex].bytesTime;
            if (interval > 2000) { // 2s min req interval
                EthSample s = new EthSample();
                s.bytesTime = n;
                s.bytesRx = getEthBytes(ethId, true);
                s.bytesTx = getEthBytes(ethId, false);
                if (interval > 300000) { // > 5 minites, then get again
                    EthSample tmp = ethSamples[ethIndex];
                    ethSamples[ethIndex] = s;
                    s = tmp;
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                    s.bytesTime = System.currentTimeMillis();
                    s.bytesRx = getEthBytes(ethId, true);
                    s.bytesTx = getEthBytes(ethId, false);

                }
                long bytes = (s.bytesRx + s.bytesTx - ethSamples[ethIndex].bytesRx - ethSamples[ethIndex].bytesTx); // bytes
                long rate = bytes * 1000 / (s.bytesTime - ethSamples[ethIndex].bytesTime); // Bps
                s.ret = (int) (rate * 8 * 10000 / 1024 / 1024 / ethSpeed);// val * 10000
                if (s.ret < 0) {
                    //if change eth's setting, the new data will be zero, the ret will be minus.
                    s.ret = 0;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("============" + ethIndex + "=============");
                    logger.debug("interval: " + (s.bytesTime - ethSamples[ethIndex].bytesTime));
                    logger.debug("bytes = " + bytes + ", rate = " + rate + "Bps, percent = " + s.ret + "%");
                }
                ethSamples[ethIndex] = s;
            }

            return ethSamples[ethIndex].ret;
        } catch (Exception e) {
            logger.error("get used rate other error: " + e.getMessage() + " Caused by: " + e.getCause());
            throw new IOException(e.getMessage(), e.getCause());
        }
    }

    private static long getEthBytes(String ethId, boolean rx) throws IOException {
        FileInputStream fis = null;
        try {
            StringBuilder path = new StringBuilder(64);
            path.append("/sys/class/net/").append(ethId).append("/statistics/").append(rx ? "rx_bytes" : "tx_bytes");
            File file = new File(path.toString());
            String data = null;

            byte[] buf = new byte[256];
            fis = new FileInputStream(file);
            int len = fis.read(buf);
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    if (buf[i] == 0) {
                        len = i;
                        break;
                    }
                }
                data = new String(buf, 0, len);
                data = data.trim();
            }
            logger.debug(path.toString() + ":" + data);
            return Long.parseLong(data);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e.getCause());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void bond(Eth mbond, String[] slaveEthId){
        try {
            long start = System.currentTimeMillis();
            logger.debug("bond start");
            if (slaveEthId != null && slaveEthId.length < 2){
                return;
            }

            // find old slaves
            ArrayList<String> slaveList = new ArrayList<String>();
            List<Eth> allEths = findAllEths(false);
            for (Eth e : allEths) {
                if (mbond.getId().equals(e.getMaster())) {
                    slaveList.add(e.getId());
                }
            }

            // ubound old slaves
            List<String> oldSlaveEthId = slaveList;
            for (int i = 0; i < oldSlaveEthId.size(); i++) {
                logger.info("ifenslave -d " + mbond.getId() + " " + oldSlaveEthId.get(i));
                // ubond slaves
                try {
                    ExecutingCommand.runShellNativeWithException("ifenslave -d " + mbond.getId() + " " + oldSlaveEthId.get(i));
                }catch (IOException e){
                    logger.error("ifenslave error: {}" , e);
                }
                // TODO: when ubound, dhcp or none;
                Properties pm = new Properties();
                pm.setProperty("DEVICE", oldSlaveEthId.get(i));
                pm.setProperty("BOOTPROTO", "none");// if set as dhcp, will spend too long(5s) to determine ip.
                pm.setProperty("TYPE", "Ethernet");
                pm.setProperty("ONBOOT", "yes");
                storeEth(pm, new File(getNetworkscriptDir(), PREFIX_CFGETH + oldSlaveEthId.get(i)));
                restartEth(oldSlaveEthId.get(i));
            }
            if (slaveEthId == null) {
                ExecutingCommand.runShellNativeWithException("ifdown " + mbond.getId());
                File bondf = new File(getNetworkscriptDir(), PREFIX_CFGETH + mbond.getId());
                bondf.delete();
                updateModProbe(mbond.getId(), true);
            } else {
                for (int i = 0; i < slaveEthId.length; i++) {
                    Properties pm = new Properties();
                    pm.setProperty("DEVICE", slaveEthId[i]);
                    pm.setProperty("BOOTPROTO", "none");
                    pm.setProperty("TYPE", "Ethernet");
                    pm.setProperty("ONBOOT", "yes");
                    pm.setProperty("NM_CONTROLLED", "no");
                    pm.setProperty("MASTER", mbond.getId());
                    pm.setProperty("SLAVE", "yes");
                    // update slaves
                    storeEth(pm, new File(getNetworkscriptDir(), PREFIX_CFGETH + slaveEthId[i]));
                    restartEth(slaveEthId[i]);

                }
                // update master
                mbond.setIsbond(true);
                updateEth(mbond);
                updateModProbe(mbond.getId(), false);
            }
            long spent = System.currentTimeMillis() - start;
            logger.debug("bond end, spend: " + spent + "(ms)");
        } catch (IOException ie) {
            logger.error("bond io error: {}" + ie);
        } catch (Exception e) {
            logger.error("bond other error: " + e.getMessage() + e.getCause());
        }
    }

    private void updateModProbe(String bondId, boolean rm) throws IOException {
        File fmp = new File("/etc/modprobe.d/dist.conf");
        StringBuffer sb = new StringBuffer();
        boolean needModified = true;
        try (BufferedReader br = new BufferedReader(new FileReader(fmp))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("alias " + bondId)) {
                    if (rm){
                        continue;
                    }
                    else {
                        needModified = false;
                    }

                } else{
                    sb.append(line).append("\n");
                }

            }
            if (needModified && !rm){
                sb.append("alias " + bondId + " bonding");
            }
        }

        if (needModified) {
            try (BufferedWriter fos = new BufferedWriter(new FileWriter(fmp))) {
                fos.write(sb.toString());
            }
        }
    }

    private static void setBondOptions(Eth eth, String strOption) {
        if (strOption != null && eth != null) {
            Map<String, String> options = parseSubOptions(strOption);
            eth.setMode(StringHelper.toInteger(options.get("mode"), -1));
            eth.setPrimary(options.get("primary"));
            eth.setPrimaryReselect(StringHelper.toInteger(options.get("primary_reselect")));
        }
    }
}
