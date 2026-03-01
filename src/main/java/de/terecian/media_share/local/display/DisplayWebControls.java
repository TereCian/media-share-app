package de.terecian.media_share.local.display;

import de.terecian.media_share.local.video.ScheduledVideoSync;
import de.terecian.media_share.local.video.VideoInQueue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Optional;

@Controller
public class DisplayWebControls {
    private final DisplayControllerBean displayWindow;
    private final ScheduledVideoSync queueSync;

    public DisplayWebControls(DisplayControllerBean displayWindow, ScheduledVideoSync queueSync) throws IOException {
        this.displayWindow = displayWindow;
        this.queueSync = queueSync;
    }

    @GetMapping("/display")
    public String getDisplayControlRow() {
        return "displayControls/displayControlRow.html";
    }

    @PostMapping("/display/show")
    @ResponseBody
    public String showDisplay() {
        displayWindow.openDisplay();
        return "";
    }

    @PostMapping("/display/start")
    @ResponseBody
    public String startNextSong() {
        Optional<VideoInQueue> nextVideo = queueSync.getNextPreloadedVideo();
        nextVideo.ifPresent(s -> {
            try {
                displayWindow.addLinkedVideoSource(s);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return "";
    }

    @PostMapping("/display/skip")
    @ResponseBody
    public String skipCurrent() {
        displayWindow.endPlay();
        return "";
    }

    @PostMapping("/display/pause")
    @ResponseBody
    public String changePauseStatus() {
        displayWindow.pause();
        return "";
    }
}
