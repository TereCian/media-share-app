package de.terecian.media_share.remote.pageControllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.HexFormat;

@Controller
public class RemoteIndexController {
    public static final String PERSISTENT_SESSION_COOKIE = "persistent-identifier-cookie";

    @GetMapping("/index")
    public String indexPage(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null && Arrays.stream(request.getCookies()).noneMatch(cookie -> cookie.getName().equals(PERSISTENT_SESSION_COOKIE))) {
            response.setHeader("Set-Cookie", PERSISTENT_SESSION_COOKIE + "=" + getIdentifierCookieValue() + "; Max-Age=7884000; Path=/");
        }
        return "remoteIndex.html";
    }

    private String getIdentifierCookieValue() {
        return HexFormat.of().toHexDigits(Math.round(Math.random() * 1000000));
    }
}
