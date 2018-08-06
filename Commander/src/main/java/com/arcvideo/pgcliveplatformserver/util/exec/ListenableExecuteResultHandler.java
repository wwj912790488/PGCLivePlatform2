package com.arcvideo.pgcliveplatformserver.util.exec;

import com.google.common.util.concurrent.SettableFuture;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

public class ListenableExecuteResultHandler implements ExecuteResultHandler{

    private SettableFuture<Integer> future;

    public ListenableExecuteResultHandler(SettableFuture<Integer> future) {
        this.future = future;
    }

    @Override
    public void onProcessComplete(int i) {
        future.set(i);
    }

    @Override
    public void onProcessFailed(ExecuteException e) {
        future.setException(e);
    }
}
