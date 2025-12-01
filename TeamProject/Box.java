import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Box {
    public int x, y;
    public int width = 32, height = 32; // 타일 크기에 맞춤 (32x32)
    public JLabel boxLabel;
    
    private double ySpeed = 0;
    private final double GRAVITY = 0.5;
    
    public Box(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        
        boxLabel = new JLabel();
        boxLabel.setBounds(x, y, width, height);
        
        // 상자 이미지 (없으면 갈색 배경)
        ImageIcon icon = new ImageIcon("Images/Tile/box.png"); 
        if (icon.getIconWidth() == -1) {
             boxLabel.setOpaque(true);
             boxLabel.setBackground(new java.awt.Color(139, 69, 19));
        } else {
             Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
             boxLabel.setIcon(new ImageIcon(img));
        }
    }
    
    public void update() {
        // 중력 적용
        ySpeed += GRAVITY;
        y += ySpeed;
        
        // 바닥 충돌
        if (Collision.isColliding(x, y, width, height)) {
            if (ySpeed > 0) {
                y = ((y + height) / Collision.TILE_SIZE) * Collision.TILE_SIZE - height;
                ySpeed = 0;
            }
        }
        boxLabel.setLocation(x, y);
    }
    
    public void push(double pushX) {
    x += pushX;

    // 좌우 충돌만 검사하도록 y 범위를 약간 줄임
    int marginTop = 4;
    int marginBottom = 4;

    if (Collision.isColliding(x, y + marginTop, width, height - marginTop - marginBottom)) {
        x -= pushX;  // 벽과 부딪힐 때만 되돌림
    }
}
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}