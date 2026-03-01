package de.terecian.media_share.local.video;

import de.terecian.media_share.local.display.DisplayControllerBean;
import de.terecian.media_share.shared.SettingsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledVideoSync {
    private static final int SECONDS_BEFORE_PRELOAD_START = 60;
    private final DisplayControllerBean displayController;
    private final SettingsStorage settings;
    private final StreamDecider streamDecider;
    private LinkedList<VideoInQueue> preloadedQueue;
    private String cachedRemoteUrl = "";
    private RestClient cachedRestClient;
    private String lastListEtag = "";
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledVideoSync.class);

    public ScheduledVideoSync(DisplayControllerBean displayController, SettingsStorage settings, StreamDecider streamDecider) {
        this.displayController = displayController;
        this.settings = settings;
        this.streamDecider = streamDecider;
        this.preloadedQueue = new LinkedList<>();
    }

    public synchronized Optional<VideoInQueue> getNextPreloadedVideo() {
        if (!preloadedQueue.isEmpty() ) {
            VideoInQueue next = preloadedQueue.poll();
            if (next == null) {
                throw new NoSuchElementException();
            }
            removeFromRemoteQueue(next.getVideoID());
            return Optional.of(next);
        }
        return Optional.empty();
    }

    private void removeFromRemoteQueue(String toRemove) {
        try {
            getClient().delete().uri("/api/delete")
                    .header("id", toRemove)
                    .header("password", settings.getSettings().remotePass())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            LOGGER.warn("Exception trying to remove element {} from queue!", toRemove);
        }
    }


    private RestClient getClient() {
        String configuredRemoteUrl = settings.getSettings().remoteUrl();
        if (cachedRestClient != null && !cachedRemoteUrl.isBlank() && cachedRemoteUrl.equals(configuredRemoteUrl)) {
            return cachedRestClient;
        } else {
            RestClient newClient = RestClient.builder().baseUrl(settings.getSettings().remoteUrl()).build();
            cachedRemoteUrl = configuredRemoteUrl;
            cachedRestClient = newClient;
            return newClient;
        }
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public synchronized void updateQueue() throws IOException {
        try {
            List<String> updatedList = getQueueIdsFromRemote();
            if (updatedList == null) {
                // no changes
                return;
            }
            LinkedList<VideoInQueue> newList = new LinkedList<>();
            LinkedList<VideoInQueue> oldList = preloadedQueue;
            for (String videoId : updatedList) {
                Optional<VideoInQueue> existing = oldList.stream()
                        .filter(existingPreload -> videoId.equals(existingPreload.getVideoID())).findAny();
                if (existing.isPresent()) {
                    newList.addLast(existing.get());
                } else {
                    newList.addLast(new VideoInQueue(videoId));
                }
            }
            preloadedQueue = newList;
            oldList.removeAll(newList);
            checkForPreloadStart();
        } catch (Exception e) {
            LOGGER.warn("Exception retrieving queue information from remote!", e);
        }
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    private void checkForPreloadStart() throws IOException {
        if (!preloadedQueue.isEmpty() && displayController.getSecondsRemainingOnCurrentVideo() <= SECONDS_BEFORE_PRELOAD_START) {
            preloadedQueue.getFirst().startDownload(streamDecider);
        }
    }

    private List<String> getQueueIdsFromRemote() {
        try {
            ResponseEntity<ListOfUpcomingSongs> response = getClient().get()
                    .uri("/api/upcoming")
                    .ifNoneMatch(lastListEtag)
                    .retrieve()
                    .toEntity(ListOfUpcomingSongs.class);
            lastListEtag = response.getHeaders().getETag();
            if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(304))) {
                return null;
            }
            return response.getBody().upcomingSongs();
        } catch (Exception e) {
            LOGGER.warn("Exception trying to retrieve upcoming song data, proceeding with no changes", e);
            return null;
        }
    }

    record ListOfUpcomingSongs(List<String> upcomingSongs) {}
}
