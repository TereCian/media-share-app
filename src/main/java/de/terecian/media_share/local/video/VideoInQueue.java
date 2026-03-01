package de.terecian.media_share.local.video;

import com.github.felipeucelli.javatube.Stream;
import com.github.felipeucelli.javatube.Youtube;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

public class VideoInQueue {
    private final String videoID;
    private final Semaphore waitForDownload;
    private File downloadLocationDummyFile;

    public VideoInQueue(String videoID) throws IOException {
        this.videoID = videoID;
        waitForDownload = new Semaphore(0);
    }

    public String getVideoID() {
        return videoID;
    }

    public Semaphore getWaitForDownload() {
        return waitForDownload;
    }

    public void startDownload(Function<Collection<Stream>, Stream> videoParamDecider) throws IOException {
        if (downloadLocationDummyFile != null) {
            return;
        }
        downloadLocationDummyFile = File.createTempFile("mediashare-preload", "");
        Thread.ofVirtual().start(() -> {
            try {
                doDownload("https://youtube.com/watch?v=" + videoID, videoParamDecider);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void doDownload(String fullUrlString, Function<Collection<Stream>, Stream> videoParamDecider) throws Exception {
       videoParamDecider.apply(new Youtube(fullUrlString).streams().getAll()).download(downloadLocationDummyFile.getPath(),
               (long1, long2) -> {
            if (long1.longValue() == long2.longValue()) {
                waitForDownload.release();
            }
        });
    }

    public File getDownloadLocationDummyFile() {
        return downloadLocationDummyFile;
    }
}
