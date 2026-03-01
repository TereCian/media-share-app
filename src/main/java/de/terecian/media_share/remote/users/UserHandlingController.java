package de.terecian.media_share.remote.users;

import de.terecian.media_share.remote.pageControllers.RemoteIndexController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class UserHandlingController {

    private final UserRepository userRepository;

    public UserHandlingController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public String getUserArea(Model model, @CookieValue(RemoteIndexController.PERSISTENT_SESSION_COOKIE) String userId) throws IOException {
        model.addAttribute("userUUID", userId);
        model.addAttribute("username", userRepository.lookUpUsername(userId));
        model.addAttribute("wasChanged", false);
        return "userArea.html";
    }

    @PostMapping("/user/updateUsername")
    public String updateUsername(Model model, @RequestParam("userUUID") String userUUID, @RequestParam("username") String username, @CookieValue(RemoteIndexController.PERSISTENT_SESSION_COOKIE) String userId) throws IOException {
        if (userId.equalsIgnoreCase(userUUID)) {
            userRepository.updateEntry(userUUID, username);
            model.addAttribute("wasChanged", true);
        } else {
            model.addAttribute("wasChanged", false);
        }
        model.addAttribute("username", username);
        model.addAttribute("userUUID", userUUID);
        return "userArea.html";
    }
}
