import java.io.File;
import javax.sound.sampled.*;

public class BgmLoop extends Thread {
    private String filePath;
    private Clip clip;
    private float volume = 0.8f; // 0.0 ~ 1.0 기본 볼륨

    public BgmLoop(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("오류: 음악 파일을 찾을 수 없습니다 -> " + filePath);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(ais);

            // 현재 volume 값 기준으로 볼륨 적용
            setVolume(volume);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (clip == null) return;

        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // volume: 0.0 ~ 1.0 → -80dB ~ 0dB로 변환
            float dB;
            if (volume == 0) {
                dB = gain.getMinimum();   // 거의 무음 (-80dB)
            } else {
                // 사람이 들을 때 자연스럽게 들리는 로그 스케일
                dB = (float)(Math.log10(volume) * 20.0);
            }

            gain.setValue(dB);

        } catch (Exception e) {
            System.out.println("Volume control not supported");
        }
    }

    public void stopMusic() {
        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        } catch (Exception e) {}
    }
}
