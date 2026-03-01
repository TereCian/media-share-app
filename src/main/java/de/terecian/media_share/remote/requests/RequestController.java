package de.terecian.media_share.remote.requests;

import de.terecian.media_share.remote.data.*;
import de.terecian.media_share.remote.infra.ETagGenerator;
import de.terecian.media_share.remote.pageControllers.RemoteIndexController;
import de.terecian.media_share.shared.NotChangedException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

@Controller
@SessionScope
public class RequestController {

    private final UpcomingSongsData upcomingSongsData;
    private final ETagGenerator eTagGenerator;

    public RequestController(UpcomingSongsData upcomingSongsData, ETagGenerator eTagGenerator) {
        this.upcomingSongsData = upcomingSongsData;
        this.eTagGenerator = eTagGenerator;
    }

    @GetMapping("/queuePoller")
    public String getQueuePoller() {
        return "requestListOuter.html";
    }

    @GetMapping("/queue")
    public String getRequestQueue(Model model, @RequestHeader(value = "If-None-Match", required = false) String etag,
                                  HttpServletResponse response, @CookieValue(RemoteIndexController.PERSISTENT_SESSION_COOKIE) String userId) {
        String currentEtag = eTagGenerator.generateEtag(upcomingSongsData.listOfUpcomingSongs());
        if (etag != null && etag.startsWith("w/")) {
            etag = etag.substring(2);
        }
        if (currentEtag.equals(etag)) {
            throw new NotChangedException("Not Modified");
        }
        model.addAttribute("requestQueue", upcomingSongsData.getSongQueue());
        model.addAttribute("requestingUserUUID", userId);
        response.addHeader("ETag", "w/" + currentEtag);
        return "requestsList.html";
    }

    @GetMapping("/searchForm")
    public String getRequestForm() {
        return "requestSearchForm.html";
    }

    @GetMapping("/searchRequest")
    public String lookUpRequest(@RequestParam("searchType") LookupType lookupType, @RequestParam("searchQuery") String searchQuery,
                                HttpServletResponse response, Model model,@CookieValue(RemoteIndexController.PERSISTENT_SESSION_COOKIE) String userId) {
        try {
            SongData searchResult = upcomingSongsData.cachedLookUp(new SongLookupRequest(searchQuery, lookupType), userId);
            model.addAttribute("youtubeObject", searchResult);
            return "searchResult.html";
        } catch (Exception e) {
            response.addHeader("HX-Retarget", "#searchRequestErrorAnchor");
            model.addAttribute("id", "searchRequestErrorAnchor");
            model.addAttribute("text", "❌ There was an error looking up your request!");
            return "justAGoddamnMessageSpan.html";
        }
    }

    @PostMapping("/request")
    public String submitRequest(@RequestParam("videoId") String confirmId, Model model, @CookieValue(RemoteIndexController.PERSISTENT_SESSION_COOKIE) String userId) {
        try {
            upcomingSongsData.addSongToQueue(confirmId, userId);
            model.addAttribute("resultMessage", "Song has been successfully queued!");
        } catch (InvalidParameterValueException e) {
            model.addAttribute("resultMessage", "Song could not be queued, because: " + e.getMessage());
        }
        return getRequestForm();
    }

    @DeleteMapping("/request")
    @ResponseBody
    public String removeRequest(@RequestParam("songID") String deleteId, @CookieValue(RemoteIndexController.PERSISTENT_SESSION_COOKIE) String userId) {
        upcomingSongsData.deleteSongFromQueue(deleteId, userId, false);
        return "";
    }
}
