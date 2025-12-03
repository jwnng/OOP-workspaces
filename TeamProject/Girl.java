import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.Timer;

public class Girl extends Player {

    private ImageIcon idleIcon;
    private ImageIcon[] rightRun;
    private ImageIcon[] leftRun;
    private int frameIndex = 0;
    private Timer animationTimer;

    // MainMap에서 쓰는 생성자
    public Girl(MainMap m_map, int startX, int startY, OptionPane op) {
        super(m_map, startX, startY, 1); // type = 1 (소녀)

        // ----- 이미지 로드 -----
        idleIcon = loadIcon("Images/Girls/Girl_Idle.png");

        rightRun = new ImageIcon[] {
            loadIcon("Images/Girls/Girl_rightRun.png"),
            loadIcon("Images/Girls/Girl_rightRun1.png")
        };

        leftRun = new ImageIcon[] {
            loadIcon("Images/Girls/Girl_leftRun.png"),
            loadIcon("Images/Girls/Girl_leftRun1.png")
        };

        // 처음에는 Idle 상태
        character.setIcon(idleIcon);
        character.setSize(width, height);

        // ----- 애니메이션 타이머 -----
        animationTimer = new Timer(120, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (left) {
                    frameIndex = (frameIndex + 1) % leftRun.length;
                    character.setIcon(leftRun[frameIndex]);
                } else if (right) {
                    frameIndex = (frameIndex + 1) % rightRun.length;
                    character.setIcon(rightRun[frameIndex]);
                } else {
                    character.setIcon(idleIcon); // 멈추면 Idle
                    frameIndex = 0;
                }
            }
        });
        animationTimer.start();
    }

    // ClientGUI 같은 데서 쓸 수 있는 보조 생성자
    public Girl(MainMap m_map, OptionPane op) {
        this(m_map, 100, 100, op);  // 기본 위치 아무 데나
    }

    private ImageIcon loadIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
