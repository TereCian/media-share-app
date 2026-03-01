package de.terecian.media_share.local.display;

import com.github.felipeucelli.javatube.Youtube;
import de.terecian.media_share.local.video.VideoInQueue;
import org.springframework.stereotype.Component;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.StatusApi;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

@Component
public class DisplayControllerBean {

    private OuterContainer lastKnownContainer;
    private boolean currentlyPlaying = false;
    private boolean isPaused = false;

    public void openDisplay() {
        OuterContainer newInstance = OuterContainer.getINSTANCE();
        if (newInstance != lastKnownContainer) {
            lastKnownContainer = newInstance;
            // register callbacks
            newInstance.getFrame().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    currentlyPlaying = false;
                }
            });
            newInstance.getMediaPlayer().mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter(){
                @Override
                public void finished(MediaPlayer mediaPlayer) {
                    currentlyPlaying = false;
                }

                @Override
                public void error(MediaPlayer mediaPlayer) {
                    currentlyPlaying = false;
                }
            });
        }
    }

    public boolean pause() {
        isPaused = !isPaused;
        if (isPaused) {
            lastKnownContainer.getMediaPlayer().mediaPlayer().controls().pause();
        } else {
            lastKnownContainer.getMediaPlayer().mediaPlayer().controls().play();
        }
        return isPaused;
    }

    public void endPlay() {
        if (!currentlyPlaying) {
            return;
        }
        lastKnownContainer.getMediaPlayer().mediaPlayer().controls().stop();
        currentlyPlaying = false;
    }

    public void addLinkedVideoSource(VideoInQueue video) {
        if (currentlyPlaying) {
            return;
        }
        isPaused = false;
        currentlyPlaying = true;
        video.getWaitForDownload().acquireUninterruptibly();
        lastKnownContainer.getMediaPlayer().mediaPlayer().media().play(findActualVideoFile(video.getDownloadLocationDummyFile()).getAbsolutePath());
    }

    private File findActualVideoFile(File dummyFile) {
        for (File fileInTemp : dummyFile.getParentFile().listFiles()) {
            if (fileInTemp.getName().startsWith(dummyFile.getName()) && !fileInTemp.getName().equals(dummyFile.getName())) {
                return fileInTemp;
            }
        }
        return null;
    }

    public int getSecondsRemainingOnCurrentVideo() {
        if (!currentlyPlaying) {
            return 0;
        }
        StatusApi status = lastKnownContainer.getMediaPlayer().mediaPlayer().status();
        return (int) (((status.length() - status.time()) / 1000d) * status.rate());
    }
}
