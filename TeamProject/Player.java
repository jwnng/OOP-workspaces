import java.awt.Rectangle;
import javax.swing.JLabel;

public class Player implements Moveable {
    int x, y, startX, startY; 
    int width = 30, height = 30; // 끼임 방지
    double xSpeed = 0, ySpeed = 0;
    boolean left, right, up, down, onGround, isDead;
    
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
        // [가로 이동]
        x += xSpeed; 
        if (Collision.isColliding(x, y, width, height)) { 
             if (xSpeed > 0) x = ((x + width) / Collision.TILE_SIZE) * Collision.TILE_SIZE - width - 1;
             else if (xSpeed < 0) x = (x / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
             xSpeed = 0; 
        }
        
        // 상자 밀기
        checkBoxPush();

        // [세로 이동]
        y += ySpeed;
        onGround = false; 
        
        // 세로 충돌 (끼임 방지)
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
        
        // 상자 밟기
        checkBoxStand();

        // 기믹 체크
        checkGimmicks(); 
    }

    private void checkBoxPush() {
        Box box = mainMap.getBox(); 
        if (box == null) return;
        Rectangle myRect = new Rectangle(x, y, width, height);
        Rectangle boxRect = box.getBounds();
        if (myRect.intersects(boxRect)) {
            if (xSpeed > 0) { box.push(xSpeed); if (box.x <= x + width) x = box.x - width - 1; }
            else if (xSpeed < 0) { box.push(xSpeed); if (box.x + box.width >= x) x = box.x + box.width + 1; }
        }
    }

    private void checkBoxStand() {
        Box box = mainMap.getBox();
        if (box == null) return;
        Rectangle myFeet = new Rectangle(x + 5, y, width - 10, height); 
        Rectangle boxRect = box.getBounds();
        if (myFeet.intersects(boxRect)) {
            if (ySpeed > 0 && y + height <= box.y + 15) { onGround = true; y = box.y - height; ySpeed = 0; }
            else if (ySpeed < 0 && y >= box.y + box.height - 15) { y = box.y + box.height; ySpeed = 0; }
        }
    }

    // ⭐ 기믹 체크 (변수명 수정)
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
        
        // 2. 일반 스위치 (GIRL / DOG)
        else if (tile == Collision.SWITCH_GIRL || tile == Collision.SWITCH_DOG) {
            
            // ⭐ 소녀 스위치면 소녀 문 열고, 강아지 스위치면 강아지 문 염
            int targetDoor = (tile == Collision.SWITCH_GIRL) ? Collision.DOOR_GIRL : Collision.DOOR_DOG;
            
            int finalState;
            if (xSpeed > 0) finalState = Collision.SWITCH_ON_RIGHT;
            else finalState = Collision.SWITCH_ON_LEFT;

            mainMap.operateSwitch(tx, ty, targetDoor, finalState);
        }
        
        // 3. 초록 스위치
        else if (tile == Collision.SWITCH_GREEN) {
            mainMap.operateSwitch(tx, ty, Collision.WALL_BREAKABLE, Collision.SWITCH_GREEN_ON);
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