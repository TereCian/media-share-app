package de.terecian.media_share.shared;

import de.terecian.media_share.local.display.OuterContainer;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.*;
import java.util.Properties;

@SpringBootApplication(scanBasePackages = {"de.terecian.media_share.shared"})
@EnableScheduling
public class SharedEntrypoint {

    static void main(String[] args) {
        String additionalProfile = "";
        if (!System.getenv().containsKey("spring.profiles.active")
                && (System.getProperty("spring.profiles.active") == null
                    || System.getProperty("spring.profiles.active").isEmpty())) {
            if (args.length > 0) {
                additionalProfile = args[0];
            } else {
                additionalProfile = promptAdditionalProfile();
            }
        }
        SpringApplicationBuilder builder = new SpringApplicationBuilder()
                .bannerMode(Banner.Mode.OFF)
                .headless(false)
                .main(SharedEntrypoint.class)
                .sources(SharedEntrypoint.class);
        if (!additionalProfile.isBlank()) {
            builder.profiles(additionalProfile);
        }
        ApplicationContext context = builder.run(args);
        OuterContainer.setContext(context);
    }

    private static String promptAdditionalProfile() {
        String additionalProfile = "";
        while(additionalProfile.isBlank()) {
            try (Writer terminalWriter = new OutputStreamWriter(System.out)){
                terminalWriter.write("No mode of operation detected. Please choose one [local|remote]");
                terminalWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (BufferedReader terminalReader = new BufferedReader(new InputStreamReader(System.in))) {
                additionalProfile = terminalReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return additionalProfile;
    }
}
