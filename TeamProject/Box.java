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
        // ★ 추가: 낙하 속도 제한 (너무 빨리 떨어지면 충돌 튐)
        if (ySpeed > 15) ySpeed = 15;

        int nextY = (int)Math.round(y + ySpeed);  // ★ 수정: 다음 y를 미리 계산

        // ★ 수정: Player와 비슷하게, 충돌 여부에 따라 보정
        if (!Collision.isColliding(x, nextY, width, height)) {
            y = nextY;
        } else {
            // 아래로 떨어지는 중 → 바닥에 닿은 경우
            if (ySpeed > 0) {
                // 1픽셀씩 내려가면서, "충돌 직전"까지 맞춰주기
                while (!Collision.isColliding(x, y + 1, width, height)) {
                    y += 1;
                }
                ySpeed = 0;
            }
            // (박스가 위로 튕길 일은 거의 없지만, 혹시 몰라 처리)
            else if (ySpeed < 0) {
                while (!Collision.isColliding(x, y - 1, width, height)) {
                    y -= 1;
                }
                ySpeed = 0;
            }
        }

        boxLabel.setLocation(x, y);
    }
    
    public void push(double pushX) {
        // ★ 수정: 먼저 "다음 x"를 계산하고, 부딪히면 이동 안 하는 방식으로 변경
        if (pushX == 0) return;

        int nextX = (int)Math.round(x + pushX);

        // 좌우 충돌만 검사하도록 y 범위를 약간 줄임
        int marginTop = 4;
        int marginBottom = 4;

        // ★ 수정: 미리 충돌 체크 → 부딪히지 않을 때만 x 갱신
        if (!Collision.isColliding(nextX, y + marginTop, width, height - marginTop - marginBottom)) {
            x = nextX;
            boxLabel.setLocation(x, y); // ★ 추가: 위치 바뀌면 라벨도 즉시 갱신
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

}
