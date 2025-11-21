import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BgmLoop extends Thread {

    private final String filePath;

    public BgmLoop(String filePath) {
        this.filePath = filePath;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                File audioFile = new File(filePath);

                AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();

                clip.open(ais);
                clip.start();

                long lengthMs = clip.getMicrosecondLength() / 1000;
                Thread.sleep(lengthMs);

                clip.close();
                ais.close();

            } catch (UnsupportedAudioFileException | LineUnavailableException |
                     IOException | InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
