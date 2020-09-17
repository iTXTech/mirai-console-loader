package org.itxtech.mcl.component;

public interface DownloadObserver {
    void updateProgress(int total, int current);
}
