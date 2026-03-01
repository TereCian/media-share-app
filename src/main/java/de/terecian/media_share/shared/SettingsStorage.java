package de.terecian.media_share.shared;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import de.terecian.media_share.local.settings.Settings;
import de.terecian.media_share.remote.data.RemoteSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class SettingsStorage {

    public static final String DEFAULT_SETTINGS_LOCATION = System.getProperty("user.home") + "/.mediarequest";
    private static final String SETTINGS_FILE_NAME = "settings.json";
    private static final String REMOTE_SETTINGS_FILE_NAME = "remote-settings.json";
    private final File storage;
    private final File remoteSettingsStorage;
    private final ObjectMapper objectMapper;

    public SettingsStorage(@Value("${application.media-share.settings-dir:}") String settingStorageLocation, ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        settingStorageLocation = (settingStorageLocation != null && !settingStorageLocation.isBlank()) ?
                settingStorageLocation : DEFAULT_SETTINGS_LOCATION;
        File storageDir = new File(settingStorageLocation);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        storage = new File(storageDir, SETTINGS_FILE_NAME);
        if (!storage.exists()) {
            storage.createNewFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storage, Settings.defaultSettings());
        }
        remoteSettingsStorage = new File(storageDir, REMOTE_SETTINGS_FILE_NAME);
        if (!remoteSettingsStorage.exists()) {
            remoteSettingsStorage.createNewFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(remoteSettingsStorage, RemoteSettings.defaultRemoteSettings());
        }
    }

    private synchronized void writeSettings(Settings settings) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(storage, settings);
    }

    private synchronized void writeRemoteSettings(RemoteSettings settings) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(remoteSettingsStorage, settings);
    }

    public synchronized void changeSingleSetting(String path, String value, SettingType type) throws IOException {
        JsonNode wholeTree = objectMapper.readTree(type == SettingType.LOCAL ? storage : remoteSettingsStorage);
        JsonPointer pathPointer = JsonPointer.compile(path);
        JsonNode nodeBeforeTheOneToEdit = wholeTree.at(pathPointer.head());
        if (wholeTree.equals(nodeBeforeTheOneToEdit)) {
            ((ObjectNode) nodeBeforeTheOneToEdit).set(path.substring(1), new TextNode(value));
        } else {
            ((ObjectNode) nodeBeforeTheOneToEdit).set(pathPointer.tail().toString().substring(1), new TextNode(value));
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(type == SettingType.LOCAL ? storage : remoteSettingsStorage, wholeTree);
    }

    public Settings getSettings() {
        try {
            if (storage.exists()) {
                return objectMapper.readValue(storage, Settings.class);
            }
            Settings initialDefaultSettings = Settings.defaultSettings();
            writeSettings(initialDefaultSettings);
            return initialDefaultSettings;
        } catch (IOException e) {
            return Settings.defaultSettings();
        }
    }

    public RemoteSettings getRemoteSettings() {
        try {
            if (remoteSettingsStorage.exists()) {
                return objectMapper.readValue(remoteSettingsStorage, RemoteSettings.class);
            }
            RemoteSettings initialDefaultSettings = RemoteSettings.defaultRemoteSettings();
            writeRemoteSettings(initialDefaultSettings);
            return initialDefaultSettings;
        } catch (IOException e) {
            return RemoteSettings.defaultRemoteSettings();
        }
    }

    public enum SettingType {
        LOCAL, REMOTE
    }
}
