package de.terecian.media_share.remote.data;

import com.github.felipeucelli.javatube.Search;
import com.github.felipeucelli.javatube.Youtube;
import org.springframework.stereotype.Component;

@Component
public class YoutubeSearch {
    
    public YoutubeSearchResponse lookUp(SongLookupRequest request) throws Exception {
        Youtube youtube = switch (request.lookupType()) {
            case VIDEO_ID -> new Youtube("/v=" + request.lookupTerm());
            case TITLE -> new Search(request.lookupTerm()).getVideosResults().getFirst();
            case LINK -> new Youtube(request.lookupTerm());
        };
        return new YoutubeSearchResponse(youtube.getTitle(), youtube.getThumbnailUrl(), youtube.getAuthor(), youtube.getUrl(), youtube.length());
    }
}
