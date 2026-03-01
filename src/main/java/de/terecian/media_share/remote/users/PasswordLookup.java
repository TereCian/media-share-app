package de.terecian.media_share.remote.users;

import org.apache.commons.codec.digest.Sha2Crypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PasswordLookup {

    private final String hashedPassword;
    public static final String HASH_PREFIX = "ENC-";
    public static final String STATIC_SALT = "$5$static";

    public PasswordLookup(@Value("${passwordHash:}") String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean validatePassword(String password) {
        return Sha2Crypt.sha256Crypt(password.getBytes(StandardCharsets.UTF_8), STATIC_SALT).equals(hashedPassword);
    }

    static void main() {
        String pass = "toast";
        System.out.println(Sha2Crypt.sha256Crypt((HASH_PREFIX + Sha2Crypt.sha256Crypt(pass.getBytes(StandardCharsets.UTF_8), STATIC_SALT)).getBytes(StandardCharsets.UTF_8), STATIC_SALT));
    }
}
