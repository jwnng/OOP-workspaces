import java.awt.Image;
import javax.swing.ImageIcon;

public class Girl extends Player {
    
    // 👇 여기를 JPanel -> MainMap 으로 바꿔야 합니다!
    public Girl(MainMap m_map, OptionPane op) {
        super(m_map, 200, 100); // 시작 위치
        
        // 이미지 설정
        ImageIcon icon = new ImageIcon("Images/Dog/Dog_Idle.png"); 
        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        character.setIcon(new ImageIcon(img));
    }
}