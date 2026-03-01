package de.terecian.media_share.remote.api;

import de.terecian.media_share.remote.data.AuthenticationException;
import de.terecian.media_share.remote.data.UpcomingSongsData;
import de.terecian.media_share.remote.infra.ETagGenerator;
import de.terecian.media_share.shared.NotChangedException;
import de.terecian.media_share.remote.users.PasswordLookup;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class UpcomingSongsListController {

    private final ETagGenerator eTagGenerator;
    private final UpcomingSongsData upcomingSongsData;
    private final PasswordLookup passwordLookup;

    public UpcomingSongsListController(ETagGenerator eTagGenerator, UpcomingSongsData upcomingSongsData, PasswordLookup passwordLookup) {
        this.eTagGenerator = eTagGenerator;
        this.upcomingSongsData = upcomingSongsData;
        this.passwordLookup = passwordLookup;
    }

    @GetMapping("/api/upcoming")
    @ResponseBody
    public ResponseEntity<UpcomingSongsList> upcomingSongsList(@RequestHeader("If-None-Match") String matchEtag, HttpServletResponse response) {
        Collection<String> upcomingSongs = upcomingSongsData.listOfUpcomingSongs();
        String generatedEtag = eTagGenerator.generateEtag(upcomingSongs);
        if (matchEtag.startsWith("w/")) {
            matchEtag = matchEtag.substring(2);
        }
        if (generatedEtag.equalsIgnoreCase(matchEtag)) {
            return ResponseEntity.status(304).build();
        }
        ResponseEntity<UpcomingSongsList> result = ResponseEntity.ok(new UpcomingSongsList(upcomingSongs));
        response.setHeader("ETag", "w/" + generatedEtag);
        return result;
    }

    @DeleteMapping("/api/delete")
    @ResponseBody
    public ResponseEntity<String> deleteEntry(@RequestHeader("id") String idToDelete, @RequestHeader("password") String authString) {
        if (!passwordLookup.validatePassword(authString)) {
            throw new AuthenticationException("Password did not match");
        }
        upcomingSongsData.deleteSongFromQueue(idToDelete, null, true);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/admin/upcomingWrapper")
    // does not password check because only static data is returned
    public String getUpcomingPollingElement() {
        return "adminPoller.html";
    }

    @GetMapping("/admin/upcoming")
    public String upcomingSongsDataWithExtraControls(@RequestHeader("password") String password, Model model, @RequestHeader(value = "If-None-Match", required = false) String matchEtag,
                                                     HttpServletResponse response) {
        if (!passwordLookup.validatePassword(password)) {
            throw new AuthenticationException("Passwords did not match!");
        }
        Collection<String> upcomingSongs = upcomingSongsData.listOfUpcomingSongs();
        String generatedEtag = eTagGenerator.generateEtag(upcomingSongs);
        if (matchEtag != null && matchEtag.startsWith("w/")) {
            matchEtag = matchEtag.substring(2);
        }
        if (generatedEtag.equalsIgnoreCase(matchEtag)) {
            throw new NotChangedException("");
        }
        response.setHeader("ETag", "w/" + generatedEtag);
        model.addAttribute("songData", upcomingSongsData.getSongQueue());
        return "adminUpcomingResponse.html";
    }

    @PostMapping("/admin/upcoming/moveUpOne/{id}")
    @ResponseBody
    public String moveEntryUpOne(@RequestHeader("password") String password, @PathVariable("id") String songId) {
        if (!passwordLookup.validatePassword(password)) {
            throw new AuthenticationException("Passwords did not match!");
        }
        upcomingSongsData.move(songId, UpcomingSongsData.MoveMode.UP_ONE);
        return "";
    }

    @PostMapping("/admin/upcoming/moveDownOne/{id}")
    @ResponseBody
    public String moveEntryDownOne(@RequestHeader("password") String password, @PathVariable("id") String songId) {
        if (!passwordLookup.validatePassword(password)) {
            throw new AuthenticationException("Passwords did not match!");
        }
        upcomingSongsData.move(songId, UpcomingSongsData.MoveMode.DOWN_ONE);
        return "";
    }

    @PostMapping("/admin/upcoming/moveUpAll/{id}")
    @ResponseBody
    public String moveEntryUp(@RequestHeader("password") String password, @PathVariable("id") String songId) {
        if (!passwordLookup.validatePassword(password)) {
            throw new AuthenticationException("Passwords did not match!");
        }
        upcomingSongsData.move(songId, UpcomingSongsData.MoveMode.UP_ALL);
        return "";
    }

    @PostMapping("/admin/upcoming/moveDownAll/{id}")
    @ResponseBody
    public String moveEntryDown(@RequestHeader("password") String password, @PathVariable("id") String songId) {
        if (!passwordLookup.validatePassword(password)) {
            throw new AuthenticationException("Passwords did not match!");
        }
        upcomingSongsData.move(songId, UpcomingSongsData.MoveMode.DOWN_ALL);
        return "";
    }

    @DeleteMapping("/admin/upcoming/remove/{id}")
    @ResponseBody
    public String adminDeleteEntry(@RequestHeader("password") String password, @PathVariable(value = "id", required = false) String songId) {
        if (!passwordLookup.validatePassword(password)) {
            throw new AuthenticationException("Passwords did not match!");
        }
        upcomingSongsData.deleteSongFromQueue(songId, null, true);
        return "";
    }

    public record UpcomingSongsList(Collection<String> upcomingSongs) {}
}
