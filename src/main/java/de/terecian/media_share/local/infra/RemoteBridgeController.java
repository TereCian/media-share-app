package de.terecian.media_share.local.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.terecian.media_share.shared.SettingsStorage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Controller
public class RemoteBridgeController {

    private final SettingsStorage settings;
    private final ObjectMapper objectMapper;

    public RemoteBridgeController(SettingsStorage settings, ObjectMapper objectMapper) {
        this.settings = settings;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/bridge/**")
    public ResponseEntity<String> passPostToRemote(HttpServletRequest request) {
        String requestUrl = settings.getSettings().remoteUrl() + request.getRequestURI().split("bridge")[1];
        Map<String, String[]> params = request.getParameterMap();
        var remoteRequest = RestClient.create(requestUrl).post();
        remoteRequest.header("password", settings.getSettings().remotePass());
        remoteRequest.body(valueBody(params)).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        return remoteRequest.retrieve().toEntity(String.class);
    }

    @DeleteMapping("/bridge/**")
    public ResponseEntity<String> passDeleteToRemote(HttpServletRequest request) {
        String requestUrl = settings.getSettings().remoteUrl() + request.getRequestURI().split("bridge")[1];
        Map<String, String[]> params = request.getParameterMap();
        var remoteRequest = RestClient.create(requestUrl).delete();
        remoteRequest.header("password", settings.getSettings().remotePass());
        for (var parameter : params.entrySet()) {
            remoteRequest.attribute(parameter.getKey(), parameter.getValue()[0]);
        }
        return remoteRequest.retrieve().toEntity(String.class);
    }

    @GetMapping("/bridge/**")
    public ResponseEntity<String> passGetToRemote(HttpServletRequest request) {
        String requestUrl = settings.getSettings().remoteUrl() + request.getRequestURI().split("bridge")[1];
        Map<String, String[]> params = request.getParameterMap();
        var remoteRequest = RestClient.create(requestUrl).get();
        remoteRequest.header("password", settings.getSettings().remotePass());
        for (var parameter : params.entrySet()) {
            remoteRequest.attribute(parameter.getKey(), parameter.getValue()[0]);
        }
        return remoteRequest.retrieve().toEntity(String.class);
    }

    private String valueBody(Map<String, String[]> params) {
        StringBuilder body = new StringBuilder();
        for (var param : params.entrySet()) {
            body.append(param.getKey()).append("=").append(param.getValue()[0]).append("&");
        }
        return body.deleteCharAt(body.length() - 1).toString();
    }
}
