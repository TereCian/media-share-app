package de.terecian.media_share.remote.data;

import java.time.Duration;

public record RemoteSettings(Integer maxRequestsPerUser, Duration maximumLength) {

    public static RemoteSettings defaultRemoteSettings() {
        return new RemoteSettings(1, Duration.ofSeconds(420));
    }
}
