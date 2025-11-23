import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class BgmLoop extends Thread {
    private String filePath;
    private Clip clip;

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

            // 무한 반복 설정
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
            clip.start(); // 재생 시작

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 👇 [이 부분이 없어서 오류가 났던 겁니다!] 👇
    public void stopMusic() {
        if (clip != null) {
            clip.stop(); // 재생 멈춤
            clip.close(); // 자원 해제
        }
    }
}