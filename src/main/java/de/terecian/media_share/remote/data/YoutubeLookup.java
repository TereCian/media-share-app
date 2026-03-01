package de.terecian.media_share.remote.data;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class YoutubeLookup implements CacheLoader<SongLookupRequest, YoutubeSearchResponse> {

    private final YoutubeSearch youtubeSearch;

    public YoutubeLookup(YoutubeSearch youtubeSearch) {
        this.youtubeSearch = youtubeSearch;
    }

    @Override
    public YoutubeSearchResponse load(SongLookupRequest key) throws Exception {
        return youtubeSearch.lookUp(key);
    }

    public static SongData extractData(YoutubeSearchResponse youtube, String requestingUserId) {
        try {
            String title = youtube.title();
            String thumbnailUrl = youtube.thumbnailUrl();
            String author = youtube.author();
            String vidId = youtube.vidUrl().split("v=")[1];
            int duration = youtube.length();
            return new SongData(vidId, title, thumbnailUrl, author, duration, requestingUserId);
        } catch (Exception e) {
            throw new NoSuchElementException("Could not find Data for this Request!");
        }
    }
}
