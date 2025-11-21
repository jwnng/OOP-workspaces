import java.awt.Rectangle;
import javax.swing.JLabel;

public class Player implements Moveable {
    // ... (기존 변수들 유지) ...
    int x, y;
    int width = 50, height = 50;
    double xSpeed = 0, ySpeed = 0;
    boolean left, right, up, down, onGround, isDead;
    final double GRAVITY = 0.5, JUMP_POWER = -12, RUN_SPEED = 5;
    
    public JLabel character;
    Player otherPlayer;
    MainMap mainMap; // MainMap 참조 추가

    public JLabel getCharacter() {
        return character;
    }

    // 생성자 수정
    public Player(MainMap map, int startX, int startY) {
        this.mainMap = map; // 맵 기억하기
        this.x = startX;
        this.y = startY;
        
        character = new JLabel();
        character.setBounds(x, y, width, height);
        
        startPhysicsLoop();
    }
    
    // ... (setOtherPlayer, startPhysicsLoop 등은 그대로 유지) ...
    public void setOtherPlayer(Player p) { this.otherPlayer = p; }
    
    private void startPhysicsLoop() {
        new Thread(() -> {
            while (!isDead) {
                // ... (물리 연산 코드 그대로) ...
                if (left) xSpeed = -RUN_SPEED;
                else if (right) xSpeed = RUN_SPEED;
                else xSpeed = 0;

                if (up && onGround) { ySpeed = JUMP_POWER; onGround = false; }
                ySpeed += GRAVITY;

                moveAndCheckCollision();
                character.setLocation(x, y);
                try { Thread.sleep(20); } catch (Exception e) {}
            }
        }).start();
    }

    private void moveAndCheckCollision() {
        // ... (충돌 코드 그대로 유지) ...
        x += xSpeed;
        if (Collision.isColliding(x, y, width, height)) {
            if (xSpeed > 0) x = ((x + width) / Collision.TILE_SIZE) * Collision.TILE_SIZE - width - 1;
            else if (xSpeed < 0) x = (x / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
            xSpeed = 0;
        }
        y += ySpeed;
        onGround = false;
        if (Collision.isColliding(x, y, width, height)) {
            if (ySpeed > 0) { onGround = true; y = ((y + height) / Collision.TILE_SIZE) * Collision.TILE_SIZE - height - 1; }
            else if (ySpeed < 0) { y = (y / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE; }
            ySpeed = 0;
        }

        // 친구 만남 체크 -> 게임 오버 호출!
        if (otherPlayer != null) {
            Rectangle me = new Rectangle(x, y, width, height);
            Rectangle friend = new Rectangle(otherPlayer.x, otherPlayer.y, width, height);
            if (me.intersects(friend)) {
                // MainMap을 통해 게임 오버 화면으로 전환
                mainMap.gameOver("친구를 만났습니다! 클리어!");
                isDead = true; // 스레드 종료
            }
        }
    }

    // Moveable 인터페이스 구현
    @Override public void left() { left = true; }
    @Override public void right() { right = true; }
    @Override public void up() { up = true; }
    @Override public void down() { down = true; }
    @Override public void left_released() { left = false; }
    @Override public void right_released() { right = false; }
    @Override public void up_released() { up = false; }
    @Override public void down_released() { down = false; }
    
    @Override public void dead() { 
        isDead = true; 
        mainMap.gameOver("으악! 죽었습니다."); 
    }
    @Override public void idle() {}
    @Override public void initIndex() {}
}