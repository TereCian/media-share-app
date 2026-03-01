package de.terecian.media_share.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_MODIFIED)
public class NotChangedException extends RuntimeException {
    public NotChangedException(String message) {
        super(message);
    }
}
