package de.terecian.media_share.remote.infra;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.terecian.media_share.remote.data.SongLookupRequest;
import de.terecian.media_share.remote.data.YoutubeLookup;
import de.terecian.media_share.remote.data.YoutubeSearchResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheBeanInitializer {

    @Bean
    public LoadingCache<SongLookupRequest, YoutubeSearchResponse> songDataCache(YoutubeLookup youtubeLookup) {
        return Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(90)).build(youtubeLookup);
    }
}
