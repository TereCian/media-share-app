package de.terecian.media_share.local.settings;

import java.util.NoSuchElementException;

public enum Resolution {
    RES144P("144 p", 144),
    RES240P("240 p",  240),
    RES360P("360 p", 360),
    RES480P("480 p", 480),
    RES720P("720 p", 720),
    RES1080P("1080 p", 1080);


    private final String localizedName;
    private final int resolutionInNumber;

    Resolution(String localizedName, int resolutionInNumber) {
        this.localizedName = localizedName;
        this.resolutionInNumber = resolutionInNumber;
    }

    public static Resolution of(String localizedName) {
        return switch (localizedName) {
            case "144 p" -> RES144P;
            case "240 p" -> RES240P;
            case "360 p" -> RES360P;
            case "480 p" -> RES480P;
            case "720 p" -> RES720P;
            case "1080 p" -> RES1080P;
            case null, default -> throw new NoSuchElementException();
        };
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public int getHeightInNumber() {
        return resolutionInNumber;
    }

    public int getWidthInNumber() {
        return (getHeightInNumber() * 16) / 9;
    }
}
