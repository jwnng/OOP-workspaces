import java.awt.Image;
import javax.swing.ImageIcon;

public class Dog extends Player {
    
    public Dog(MainMap m_map, OptionPane op) {
        // 👇 맨 뒤에 ', 2' 추가! (2번 = 강아지)
        // 위치는 200, 100으로 설정 (P2, 소녀와 안 겹치게)
        super(m_map, 200, 100, 2); 
    
        // 이미지 설정 (강아지 이미지로 수정함)
        ImageIcon icon = new ImageIcon("Images/Dog/Dog_Idle.png"); 
        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        character.setIcon(new ImageIcon(img));
    }
}