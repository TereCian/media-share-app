package de.terecian.media_share.remote.data;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterValueException extends RuntimeException {
    public InvalidParameterValueException(String message) {
        super(message);
    }
}
