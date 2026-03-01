package de.terecian.media_share.local.settings;

import de.terecian.media_share.remote.users.PasswordLookup;
import de.terecian.media_share.shared.SettingsStorage;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class SettingsPage {

    private final SettingsStorage settingsStorage;

    public SettingsPage(SettingsStorage settingsStorage) {
        this.settingsStorage = settingsStorage;
    }

    @GetMapping("/settings")
    public String settingsPage() {
        return "settings.html";
    }

    @GetMapping("/settings/resolution")
    public String resolutionSetting(Model model) {
        model.addAttribute("currentValue", settingsStorage.getSettings().resolution().getLocalizedName());
        return "settingElements/resolution.html";
    }

    @PostMapping("/settings/resolution")
    public String changeResolution(Model model, @RequestParam("resolution") String resolution) throws IOException {
        Resolution newResolution = Resolution.of(resolution);
        settingsStorage.changeSingleSetting("/resolution", String.valueOf(newResolution), SettingsStorage.SettingType.LOCAL);
        return resolutionSetting(model);
    }

    @GetMapping("/settings/remoteUrl")
    public String remoteUrlSetting(Model model) {
        model.addAttribute("remoteUrl", settingsStorage.getSettings().remoteUrl());
        return "settingElements/remoteUrl.html";
    }

    @PostMapping("/settings/remoteUrl")
    public String changeRemoteUrl(Model model, @RequestParam("remoteUrl") String newRemoteUrl) throws IOException {
        settingsStorage.changeSingleSetting("/remoteUrl", newRemoteUrl, SettingsStorage.SettingType.LOCAL);
        return remoteUrlSetting(model);
    }

    @GetMapping("/settings/remotePass")
    public String remotePasswordSetting(Model model) {
        model.addAttribute("remotePass", settingsStorage.getSettings().remotePass());
        model.addAttribute("remoteUrl", settingsStorage.getSettings().remoteUrl());
        model.addAttribute("isValidFormat", settingsStorage.getSettings().remotePass().startsWith(PasswordLookup.HASH_PREFIX));
        return "settingElements/remotePass.html";
    }

    @PostMapping("/settings/remotePass")
    public String changeRemotePassword(Model model, @RequestParam("password") String newRemotePass) throws IOException {
        if (!newRemotePass.startsWith(PasswordLookup.HASH_PREFIX) && !newRemotePass.isBlank()) {
            String actualPassword = PasswordLookup.HASH_PREFIX + Sha2Crypt.sha256Crypt(newRemotePass.getBytes(StandardCharsets.UTF_8), PasswordLookup.STATIC_SALT);
            settingsStorage.changeSingleSetting("/remotePass", actualPassword, SettingsStorage.SettingType.LOCAL);
        }
        return remotePasswordSetting(model);
    }
}
