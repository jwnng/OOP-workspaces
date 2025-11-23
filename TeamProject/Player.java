import java.awt.Rectangle;
import javax.swing.JLabel;

public class Player implements Moveable {
    int x, y, startX, startY; 
    int width = 50, height = 50;
    double xSpeed = 0, ySpeed = 0;
    boolean left, right, up, down, onGround, isDead;
    
    // 👇 [수정] 속도를 5 -> 3으로 낮춤 (점프력도 살짝 조정)
    final double GRAVITY = 0.5;
    final double JUMP_POWER = -11; // 점프도 살짝 낮춰서 균형 맞춤
    final double RUN_SPEED = 3;    // 걷는 속도 줄임
    
    public JLabel character;
    Player otherPlayer;
    MainMap mainMap; 
    int type; 

    public JLabel getCharacter() { return character; }

    public Player(MainMap map, int startX, int startY, int type) {
        this.mainMap = map; 
        this.x = startX; 
        this.y = startY;
        this.startX = startX; 
        this.startY = startY;
        this.type = type;
        
        character = new JLabel();
        character.setBounds(x, y, width, height);
    }
    
    public void setOtherPlayer(Player p) { this.otherPlayer = p; }
    
    public void update() {
        if (isDead) return; 
        if (left) xSpeed = -RUN_SPEED;
        else if (right) xSpeed = RUN_SPEED;
        else xSpeed = 0;

        if (up && onGround) { ySpeed = JUMP_POWER; onGround = false; }
        ySpeed += GRAVITY;

        moveAndCheckCollision();
        character.setLocation(x, y);
    }

    private void moveAndCheckCollision() {
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

        checkGimmicks(); 
    }

    private void checkGimmicks() {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int tx = centerX / Collision.TILE_SIZE;
        int ty = centerY / Collision.TILE_SIZE;

        if (ty < 0 || ty >= Collision.tileMap.length || tx < 0 || tx >= Collision.tileMap[0].length) return;

        int tile = Collision.tileMap[ty][tx]; 

        // 1. 발판(함정) 밟으면 리스폰
        if ((tile == Collision.PAD_GIRL && type == 1) || 
            (tile == Collision.PAD_DOG && type == 2)) {
            respawn(); 
        } 
        
        // 2. 스위치 작동
        else if (tile == Collision.SWITCH_RED || tile == Collision.SWITCH_BLUE) {
            int targetDoor = (tile == Collision.SWITCH_RED) ? Collision.DOOR_RED : Collision.DOOR_BLUE;
            int finalState = (xSpeed > 0) ? Collision.SWITCH_ON_RIGHT : Collision.SWITCH_ON_LEFT;
            mainMap.operateSwitch(tx, ty, targetDoor, finalState);
        }
    }

    public void respawn() {
        x = startX;
        y = startY;
        xSpeed = 0;
        ySpeed = 0;
    }

    @Override public void left() { left = true; }
    @Override public void right() { right = true; }
    @Override public void up() { up = true; }
    @Override public void down() { down = true; }
    @Override public void left_released() { left = false; }
    @Override public void right_released() { right = false; }
    @Override public void up_released() { up = false; }
    @Override public void down_released() { down = false; }
    @Override public void dead() { isDead = true; mainMap.gameOver("으악! 죽었습니다."); }
    @Override public void idle() {}
    @Override public void initIndex() {}
}