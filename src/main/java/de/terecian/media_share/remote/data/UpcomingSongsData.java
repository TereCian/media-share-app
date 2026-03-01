package de.terecian.media_share.remote.data;

import com.github.benmanes.caffeine.cache.LoadingCache;
import de.terecian.media_share.shared.SettingsStorage;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UpcomingSongsData {

    private final LoadingCache<SongLookupRequest, YoutubeSearchResponse> cachedLookup;
    private final List<SongData> songQueue;
    private final SettingsStorage settings;

    public UpcomingSongsData(LoadingCache<SongLookupRequest, YoutubeSearchResponse> cachedLookup, SettingsStorage settings) {
        this.cachedLookup = cachedLookup;
        this.settings = settings;
        this.songQueue = new ArrayList<>();
    }

    public Collection<String> listOfUpcomingSongs() {
        return songQueue.stream().map(SongData::youtubeId).toList();
    }

    public void deleteSongFromQueue(String id, String deletingUserId, boolean administrativeDelete) {
        songQueue.removeIf(requestData -> requestData.youtubeId().equals(id)
                && (administrativeDelete || requestData.requestingUserId().equals(deletingUserId)));
    }

    public void addSongToQueue(String id, String addingUserId) {
        if (songQueue.stream().filter(queueElement -> queueElement.requestingUserId().equals(addingUserId)).count() >=
                settings.getRemoteSettings().maxRequestsPerUser()) {
            throw new InvalidParameterValueException("Too many Items already in the queue to add another");
        }
        SongData toAdd = cachedLookUp(new SongLookupRequest(id, LookupType.VIDEO_ID), addingUserId);
        if (settings.getRemoteSettings().maximumLength().getSeconds() < toAdd.lengthSeconds()) {
            throw new InvalidParameterValueException("Song is too long!");
        }
        songQueue.add(toAdd);
    }

    public Collection<SongData> getSongQueue() {
        return songQueue;
    }

    public void move(String id, MoveMode move) {
        SongData toMove = songQueue.stream().filter(songData -> id.equals(songData.youtubeId())).findAny().orElseThrow();
        int oldIndex = songQueue.indexOf(toMove);
        int newIndex = switch (move) {
            case UP_ALL -> 0;
            case DOWN_ALL -> songQueue.size() - 1;
            case UP_ONE -> songQueue.indexOf(toMove) - 1;
            case DOWN_ONE -> songQueue.indexOf(toMove) + 1;
            case null -> songQueue.indexOf(toMove);
        };
        songQueue.add(newIndex, toMove);
        songQueue.remove(oldIndex);
    }

    public SongData cachedLookUp(SongLookupRequest request, String requestingUserId) {
        return YoutubeLookup.extractData(cachedLookup.get(request), requestingUserId);
    }

    public enum MoveMode {
        UP_ONE, DOWN_ONE, UP_ALL, DOWN_ALL
    }
}
