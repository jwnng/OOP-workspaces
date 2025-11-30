import java.awt.Rectangle;
import javax.swing.JLabel;

public class Player implements Moveable {
    int x, y, startX, startY; 
    
    // ⭐ [중요] 히트박스 크기를 타일(32)보다 작게 설정하여 끼임 방지
    int width = 30, height = 30; 
    
    double xSpeed = 0, ySpeed = 0;
    boolean left, right, up, down, onGround, isDead;
    
    // 속도 조절
    final double GRAVITY = 0.5;
    final double JUMP_POWER = -11; 
    final double RUN_SPEED = 3;    
    
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
        // 1. [가로 이동]
        x += xSpeed; 
        
        // 벽 충돌 (가로)
        if (Collision.isColliding(x, y, width, height)) { 
             if (xSpeed > 0) x = ((x + width) / Collision.TILE_SIZE) * Collision.TILE_SIZE - width - 1;
             else if (xSpeed < 0) x = (x / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
             xSpeed = 0; 
        }
        
        // ⭐ [추가] 상자 밀기 (가로)
        checkBoxPush();

        // 2. [세로 이동]
        y += ySpeed;
        onGround = false; 
        
        // 벽 충돌 (세로 - 끼임 방지: 폭을 줄여서 검사)
        if (Collision.isColliding(x + 5, y, width - 10, height)) {
            if (ySpeed > 0) { 
                 onGround = true;
                 y = ((y + height) / Collision.TILE_SIZE) * Collision.TILE_SIZE - height - 1;
            } 
            else if (ySpeed < 0) { 
                 y = (y / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
            }
            ySpeed = 0;
        }
        
        // ⭐ [추가] 상자 밟기 (세로)
        checkBoxStand();

        // 3. 기믹(함정, 스위치) 체크
        checkGimmicks(); 
    }

    // 📦 상자 밀기 로직
    private void checkBoxPush() {
        Box box = mainMap.getBox(); 
        if (box == null) return;

        Rectangle myRect = new Rectangle(x, y, width, height);
        Rectangle boxRect = box.getBounds();

        if (myRect.intersects(boxRect)) {
            if (xSpeed > 0) { // 오른쪽으로 밀기
                box.push(xSpeed);
                // 상자가 벽에 막혀서 안 밀렸으면, 나도 멈춤
                if (box.x <= x + width) x = box.x - width - 1; 
            }
            else if (xSpeed < 0) { // 왼쪽으로 밀기
                box.push(xSpeed);
                if (box.x + box.width >= x) x = box.x + box.width + 1;
            }
        }
    }

    // 📦 상자 밟기 로직
    private void checkBoxStand() {
        Box box = mainMap.getBox();
        if (box == null) return;

        // 발밑 검사 (폭을 좁게 잡아서 옆면 비비기 방지)
        Rectangle myFeet = new Rectangle(x + 5, y, width - 10, height); 
        Rectangle boxRect = box.getBounds();

        if (myFeet.intersects(boxRect)) {
            // 떨어지다가 상자 윗면 밟음
            if (ySpeed > 0 && y + height <= box.y + 15) { 
                onGround = true;
                y = box.y - height;
                ySpeed = 0;
            }
            // 점프하다가 상자 아랫면 박음
            else if (ySpeed < 0 && y >= box.y + box.height - 15) {
                y = box.y + box.height;
                ySpeed = 0;
            }
        }
    }

    private void checkGimmicks() {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int tx = centerX / Collision.TILE_SIZE;
        int ty = centerY / Collision.TILE_SIZE;

        if (ty < 0 || ty >= Collision.tileMap.length || tx < 0 || tx >= Collision.tileMap[0].length) return;

        int tile = Collision.tileMap[ty][tx]; 

        // 1. 발판(함정) 체크
        if ((tile == Collision.PAD_GIRL && type == 1) || (tile == Collision.PAD_DOG && type == 2)) {
            respawn();
        } 
        
        // 2. 스위치 체크 (빨강)
        else if (tile == Collision.SWITCH_RED) { 
            if (xSpeed > 0) mainMap.operateSwitch(tx, ty, Collision.DOOR_RED, Collision.SWITCH_ON_RIGHT);
            else if (xSpeed < 0) mainMap.operateSwitch(tx, ty, Collision.DOOR_RED, Collision.SWITCH_ON_LEFT);
        }
        else if (tile == Collision.SWITCH_ON_LEFT) { 
             if (xSpeed > 0) mainMap.operateSwitch(tx, ty, Collision.DOOR_RED, Collision.SWITCH_ON_RIGHT);
        }
        else if (tile == Collision.SWITCH_ON_RIGHT) { 
             if (xSpeed < 0) mainMap.operateSwitch(tx, ty, Collision.DOOR_RED, Collision.SWITCH_ON_LEFT);
        }

        // 3. 스위치 체크 (파랑)
        else if (tile == Collision.SWITCH_BLUE) { 
            if (xSpeed > 0) mainMap.operateSwitch(tx, ty, Collision.DOOR_BLUE, Collision.SWITCH_ON_RIGHT);
            else if (xSpeed < 0) mainMap.operateSwitch(tx, ty, Collision.DOOR_BLUE, Collision.SWITCH_ON_LEFT);
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