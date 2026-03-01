package de.terecian.media_share.local.administration;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class LocalIndexController {

    @EventListener(ApplicationReadyEvent.class)
    public void openAdminPageAfterStartup() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://localhost:8080/index"));
        } else {
            System.out.println("Could not open page in standard browser, open it yourself: http://localhost:8080/index");
        }
    }

    @GetMapping("/index")
    public String initialLoad() {
        return "localIndex.html";
    }
}
