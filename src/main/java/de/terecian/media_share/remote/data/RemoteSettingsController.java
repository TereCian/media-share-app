package de.terecian.media_share.remote.data;

import de.terecian.media_share.shared.SettingsStorage;
import de.terecian.media_share.remote.users.PasswordLookup;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class RemoteSettingsController {

    private final SettingsStorage settingsStorage;
    private final PasswordLookup passwordChecker;

    public RemoteSettingsController(SettingsStorage settingsStorage, PasswordLookup passwordChecker) {
        this.settingsStorage = settingsStorage;
        this.passwordChecker = passwordChecker;
    }

    @GetMapping("/admin/remoteSettings")
    public String getRemoteSettingsBlock(@RequestHeader("password") String password) {
        if (!passwordChecker.validatePassword(password)) {
            throw new AuthenticationException("Password did not match");
        }
        return "remoteSettingElements/remoteSettingBlock.html";
    }

    @GetMapping("/admin/settings/limit")
    public String getSongLimitSetting(Model model, @RequestHeader("password") String password) {
        if (!passwordChecker.validatePassword(password)) {
            throw new AuthenticationException("Password did not match");
        }
        model.addAttribute("perUserLimit", settingsStorage.getRemoteSettings().maxRequestsPerUser());
        return "remoteSettingElements/songsPerUserLimit.html";
    }

    @PostMapping("/admin/settings/limit")
    public String setSongLimitSetting(Model model, @RequestHeader("password") String password, @RequestParam(value = "limit") String limit) throws IOException {
        if (!passwordChecker.validatePassword(password)) {
            throw new AuthenticationException("Password did not match");
        }
        int newLimit = Integer.parseInt(limit);
        if (newLimit <= 0) {
            throw new InvalidParameterValueException("Limit must be greater than 0");
        }
        settingsStorage.changeSingleSetting("/maxRequestsPerUser", "" + newLimit, SettingsStorage.SettingType.REMOTE);
        return getSongLimitSetting(model, password);
    }

    @GetMapping("/admin/settings/length")
    public String getSongLengthLimit(Model model, @RequestHeader("password") String password) {
        if (!passwordChecker.validatePassword(password)) {
            throw new AuthenticationException("Password did not match");
        }
        model.addAttribute("lengthLimit", settingsStorage.getRemoteSettings().maximumLength().getSeconds());
        return "remoteSettingElements/songLengthLimit.html";
    }

    @PostMapping("/admin/settings/length")
    public String setSongsLengthLimit(Model model, @RequestHeader("password") String password, @RequestParam(value = "length") String limit) throws IOException {
        if (!passwordChecker.validatePassword(password)) {
            throw new AuthenticationException("Password did not match");
        }
        int newLimit = Integer.parseInt(limit);
        if (newLimit <= 0) {
            throw new InvalidParameterValueException("Limit must be greater than 0");
        }
        settingsStorage.changeSingleSetting("/maximumLength", "PT" + newLimit + "S", SettingsStorage.SettingType.REMOTE);
        return getSongLengthLimit(model, password);
    }

}
