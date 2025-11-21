import java.awt.Image;
import javax.swing.ImageIcon;

public class Bear extends Player {
    
    public Bear(MainMap m_map, OptionPane op) {
        super(m_map, 100, 100); // 시작 위치
    
        // 이미지 설정
        ImageIcon icon = new ImageIcon("Images/Girls/Girl_Idle.png"); 
        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        character.setIcon(new ImageIcon(img));
    }
}