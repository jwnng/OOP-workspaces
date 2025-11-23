import java.awt.Image;
import javax.swing.ImageIcon;

public class Girl extends Player {
    
    public Girl(MainMap m_map, OptionPane op) {
        // 👇 맨 뒤에 ', 1' 추가! (1번 = 소녀)
        // 위치는 100, 100으로 설정 (P1)
        super(m_map, 100, 100, 1); 
        
        // 이미지 설정 (소녀 이미지로 수정함)
        ImageIcon icon = new ImageIcon("Images/Girls/Girl_Idle.png"); 
        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        character.setIcon(new ImageIcon(img));
    }
}