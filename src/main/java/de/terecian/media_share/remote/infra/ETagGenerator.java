package de.terecian.media_share.remote.infra;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Component
public class ETagGenerator {

    public String generateEtag(Collection<String> forList) {
        if (forList.isEmpty()) {
            return "000000";
        }
        long resultLong = 0;
        int i = 1;
        for (String songId : forList) {
            resultLong += (long) songId.hashCode() * i++;
        }
        ByteBuffer resultBuffer = ByteBuffer.allocate(Long.BYTES);
        resultBuffer.putLong(resultLong);
        return Base64.getEncoder().encodeToString(resultBuffer.array());
    }
}
