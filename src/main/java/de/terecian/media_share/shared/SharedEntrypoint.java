package de.terecian.media_share.shared;

import de.terecian.media_share.local.display.OuterContainer;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"de.terecian.media_share.shared"})
@EnableScheduling
public class SharedEntrypoint {

    static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.OFF)
                .headless(false)
                .main(SharedEntrypoint.class)
                .sources(SharedEntrypoint.class)
                .run(args);
        OuterContainer.setContext(context);
    }
}
