package de.terecian.media_share.local.video;

import com.github.felipeucelli.javatube.Stream;
import de.terecian.media_share.shared.SettingsStorage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@Component
public class StreamDecider implements Function<Collection<Stream>, Stream> {

    private final SettingsStorage settings;

    public StreamDecider(SettingsStorage settings) {
        this.settings = settings;
    }

    @Override
    public Stream apply(Collection<Stream> streams) {
        int resolutionTarget = settings.getSettings().resolution().getHeightInNumber();
        streams.removeIf(stream -> stream.getResolution() == null);
        streams.removeIf(stream -> !stream.includeAudioTrack());
        ArrayList<Stream> workingCopy = new ArrayList<>(streams);
        workingCopy.removeIf(stream -> extractResolutionInNumber(stream.getResolution()) != resolutionTarget);
        if (!workingCopy.isEmpty()) {
            return workingCopy.getFirst();
        }
        workingCopy = new ArrayList<>(streams);
        workingCopy.removeIf(stream -> extractResolutionInNumber(stream.getResolution()) < resolutionTarget);
        Stream currentBest = null;
        int currentBestDiff = Integer.MAX_VALUE;
        for (Stream currentStream : workingCopy) {
            if (extractResolutionInNumber(currentStream.getResolution()) - resolutionTarget < currentBestDiff) {
                currentBest = currentStream;
                currentBestDiff = extractResolutionInNumber(currentStream.getResolution()) - resolutionTarget;
            }
        }
        if (currentBest != null) {
            return currentBest;
        }
        return streams.iterator().next();
    }

    private int extractResolutionInNumber(String resolutionName) {
        return Integer.parseInt(resolutionName.substring(0, resolutionName.indexOf('p')));
    }
}
