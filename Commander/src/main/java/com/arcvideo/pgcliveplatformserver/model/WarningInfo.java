package com.arcvideo.pgcliveplatformserver.model;

public class WarningInfo {
    private boolean warning = false;
    private long warningCount = 0;

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public long getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(long warningCount) {
        this.warningCount = warningCount;
    }

    public void incrementWarningCount() {
        ++warningCount;
    }

    public void resetWarningCount() {
        warningCount = 0;
    }
}
