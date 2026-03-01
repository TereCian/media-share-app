package de.terecian.media_share.shared;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;

@Controller
public class StaticResourceController {

    @GetMapping("/static/{file}")
    @ResponseBody
    public String getStaticResource(@PathVariable("file") String filename, HttpServletResponse response) throws IOException {
        response.addHeader("Cache-Control", "public, max-age=604800, immutable");
        if (filename.endsWith(".js")) {
            response.setContentType("text/javascript");
        }
        return Files.readString(ResourceUtils.getFile("classpath:static/" + filename).toPath());
    }
}
