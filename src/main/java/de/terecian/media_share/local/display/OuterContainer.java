package de.terecian.media_share.local.display;

import de.terecian.media_share.local.settings.Settings;
import de.terecian.media_share.shared.SettingsStorage;
import org.springframework.context.ApplicationContext;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class OuterContainer {

    private static OuterContainer INSTANCE = null;
    private static ApplicationContext context;
    private final JFrame frame;
    private final EmbeddedMediaPlayerComponent mediaPlayer;

    private OuterContainer(int height, int width) {
        frame = new JFrame("Video Output");
        frame.setBounds(100, 100, width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayer.release();
                INSTANCE = null;
            }
        });
        mediaPlayer = new EmbeddedMediaPlayerComponent();
        frame.setContentPane(mediaPlayer);
        frame.setVisible(true);
    }

    public static OuterContainer getINSTANCE() {
        if (INSTANCE == null) {
            Settings settings = context.getBean(SettingsStorage.class).getSettings();
            INSTANCE = new OuterContainer(settings.resolution().getWidthInNumber(), settings.resolution().getHeightInNumber());
        }
        return INSTANCE;
    }

    public static void setContext(ApplicationContext context) {
        OuterContainer.context = context;
    }

    public EmbeddedMediaPlayerComponent getMediaPlayer() {
        return mediaPlayer;
    }

    public JFrame getFrame() {
        return frame;
    }
}
