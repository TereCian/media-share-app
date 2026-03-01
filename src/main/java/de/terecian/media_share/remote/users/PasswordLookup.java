package de.terecian.media_share.remote.users;

import de.terecian.media_share.shared.SettingsStorage;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PasswordLookup {

    private final String hashedPassword;
    public static final String HASH_PREFIX = "ENC-";
    public static final String STATIC_SALT = "$5$static";
    private static final File PASSWORD_FILE = new File(SettingsStorage.DEFAULT_SETTINGS_LOCATION + "/remote-pass");

    public PasswordLookup(@Value("${password:}") String password) {
        if (password != null && !password.isBlank()) {
            String hashedPassword = doubleHash(password);
            try (FileWriter writer = new FileWriter(PASSWORD_FILE)) {
                writer.write(hashedPassword);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException("Error saving password", e);
            }
            this.hashedPassword = hashedPassword;
        } else if (PASSWORD_FILE.exists()) {
            try (FileReader reader = new FileReader(PASSWORD_FILE)){
                hashedPassword = reader.readAllAsString();
            } catch (IOException e) {
                throw new RuntimeException("Error reading password from file!", e);
            }
        } else {
            throw new IllegalStateException("Please choose a password!");
        }
    }

    public boolean validatePassword(String password) {
        return Sha2Crypt.sha256Crypt(password.getBytes(StandardCharsets.UTF_8), STATIC_SALT).equals(hashedPassword);
    }

    private String doubleHash(String input) {
        return Sha2Crypt.sha256Crypt((HASH_PREFIX + Sha2Crypt.sha256Crypt(input.getBytes(StandardCharsets.UTF_8), STATIC_SALT))
                .getBytes(StandardCharsets.UTF_8), STATIC_SALT);
    }
}
