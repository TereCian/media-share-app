package de.terecian.media_share.remote.data;

public record SongData(String youtubeId, String fullName, String thumbnailLink, String channelName, int lengthSeconds, String requestingUserId) {

    public String idAsJson() {
        return "{\"id\": \"" + youtubeId + "\"}";
    }
}
