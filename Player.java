// Player.java (수정된 코드)

import java.awt.Image; 
import java.awt.event.KeyEvent; // KeyCode를 사용하지 않더라도 import는 유지합니다.

public class Player { 
    
    private int x, y; 
    
    // 이 필드들은 GamePanel의 updateGame() 메서드가 이동을 처리하므로 private final로 유지해도 됩니다.
    private final int SIZE = 32;        
    private final int MOVE_SPEED = 4;   // (참고용으로 유지)
    
    private Image playerImage;
    
    /**
     * Player 클래스의 생성자
     */
    public Player(int startX, int startY, Image img) {
        this.x = startX;
        this.y = startY;
        this.playerImage = img;
    }
    
    // --- Getter 메서드는 기존과 동일하게 유지 ---
    public int getX() { return x; } 
    public int getY() { return y; } 
    public int getSize() { return SIZE; } 
    public Image getImage() { return playerImage; } 
    
    // ------------------------------------------------------------------
    // ⭐ 필수 추가: GamePanel에서 위치를 직접 설정하기 위한 Setter 메서드
    // ------------------------------------------------------------------
    /**
     * GamePanel에서 계산된 새 X 좌표를 주인공에게 적용
     */
    public void setX(int newX) { 
        this.x = newX; 
    }
    
    /**
     * GamePanel에서 계산된 새 Y 좌표를 주인공에게 적용
     */
    public void setY(int newY) { 
        this.y = newY; 
    }
    
    // ------------------------------------------------------------------
    // ⚠️ 변경: move() 메서드는 더 이상 사용하지 않음 (Timer 루프가 처리)
    // ------------------------------------------------------------------
    // 이 메서드는 Timer 기반 2인 플레이어 시스템에서는 사용되지 않습니다.
    // 주석 처리하거나 제거하는 것을 권장합니다.
    /*
    public void move(int KeyCode) {
        switch(KeyCode) {   
        case KeyEvent.VK_UP:
            y -= MOVE_SPEED;
            break;
        // ... (나머지 로직)
        }
    }
    */
}