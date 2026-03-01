package de.terecian.media_share.local.settings;

public record Settings(Resolution resolution, String remoteUrl, String remotePass) {

    private static final Resolution DEFAULT_RESOLUTION = Resolution.RES360P;
    private static final String DEFAULT_REMOTE_URL = "http://localhost:8080";
    private static final String DEFAULT_REMOTE_PASS = "";

    public static Settings defaultSettings() {
        return new Settings(DEFAULT_RESOLUTION, DEFAULT_REMOTE_URL, DEFAULT_REMOTE_PASS);
    }
}
