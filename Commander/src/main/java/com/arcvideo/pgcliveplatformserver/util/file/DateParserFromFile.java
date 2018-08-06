package com.arcvideo.pgcliveplatformserver.util.file;

import com.arcvideo.pgcliveplatformserver.util.MapUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParserFromFile {
    private String patternFormat;
    private static final String yearPatternFormat = "${yyyy}";
    private static final String monthPatternFormat = "${MM}";
    private static final String dayPatternFormat = "${dd}";
    private static final String hourPatternFormat = "${HH}";
    private static final String minutePatternFormat = "${mm}";
    private static final String secondPatternFormat = "${ss}";
    private Calendar calendar = Calendar.getInstance();

    public DateParserFromFile(String patternFormat) {
        this.patternFormat = patternFormat;
    }

    public boolean parser(String fileName) {
        Map<Integer, Integer> map = new HashMap<>(10);
        map.put(Calendar.YEAR, patternFormat.indexOf(yearPatternFormat));
        map.put(Calendar.MONTH, patternFormat.indexOf(monthPatternFormat));
        map.put(Calendar.DATE, patternFormat.indexOf(dayPatternFormat));
        map.put(Calendar.HOUR_OF_DAY, patternFormat.indexOf(hourPatternFormat));
        map.put(Calendar.MINUTE, patternFormat.indexOf(minutePatternFormat));
        map.put(Calendar.SECOND, patternFormat.indexOf(secondPatternFormat));

        if (map.size() != 6) {
            return false;
        }

        Map<Integer, Integer> sortedMap = MapUtil.sortByValue(map);

        String patternString = getPatternString();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            calendar.clear();
            int gounpCount = matcher.groupCount();
            Iterator entries = sortedMap.entrySet().iterator();
            for (int i = 1; i <= gounpCount; ++i) {
                Map.Entry entry = (Map.Entry)entries.next();
                String str = matcher.group(i);
                if ((int)entry.getKey() == Calendar.MONTH) {
                    calendar.set((int)entry.getKey(), Integer.parseInt(str) - 1);
                }
                else {
                    calendar.set((int)entry.getKey(), Integer.parseInt(str));
                }
            }
            return true;
        }
        return false;
    }

    public Date getDate() {
        return calendar.getTime();
    }

    private String getPatternString() {
        String patternName = patternFormat;
        patternName= patternName.replace(yearPatternFormat, "(\\d{4})");
        patternName= patternName.replace(monthPatternFormat, "(\\d{2})");
        patternName= patternName.replace(dayPatternFormat, "(\\d{2})");
        patternName= patternName.replace(hourPatternFormat, "(\\d{2})");
        patternName= patternName.replace(minutePatternFormat, "(\\d{2})");
        patternName= patternName.replace(secondPatternFormat, "(\\d{2})");
        return patternName;
    }

}
