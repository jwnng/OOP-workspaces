import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;

public class Box {
    public int x, y;
    public int width = 32, height = 32; // 상자 크기 (캐릭터와 동일)
    public JLabel boxLabel;
    
    private double ySpeed = 0;
    private final double GRAVITY = 0.5;
    
    public Box(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        
        boxLabel = new JLabel();
        boxLabel.setBounds(x, y, width, height);
        
        // 📦 상자 이미지 설정 (Images/Tile/box.png 필요! 없으면 wallImage 등 임시 사용)
        ImageIcon icon = new ImageIcon("Images/Tile/box.png"); 
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        boxLabel.setIcon(new ImageIcon(img));
    }
    
    public void update() {
        // 1. 중력 적용 (아래로 떨어짐)
        ySpeed += GRAVITY;
        y += ySpeed;
        
        // 2. 바닥 충돌 검사
        if (Collision.isColliding(x, y, width, height)) {
            if (ySpeed > 0) { // 떨어지다가 바닥에 닿음
                y = ((y + height) / Collision.TILE_SIZE) * Collision.TILE_SIZE - height - 1;
                ySpeed = 0;
            }
        }
        
        // 3. 위치 반영
        boxLabel.setLocation(x, y);
    }
    
    // 플레이어가 밀 때 호출되는 함수
    public void push(double pushX) {
        // 일단 밀어봄
        x += pushX;
        
        // 벽에 막히는지 검사
        if (Collision.isColliding(x, y, width, height)) {
            // 벽이면 다시 원위치 (안 밀림)
            x -= pushX;
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}