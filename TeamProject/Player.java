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
        
        checkBoxPush(); // 상자 밀기
        if (Collision.isColliding(x, y, width, height)) {
            if (xSpeed > 0) x = ((x + width) / Collision.TILE_SIZE) * Collision.TILE_SIZE - width - 1;
            else if (xSpeed < 0) x = (x / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
            xSpeed = 0;
        }
        

        y += ySpeed;
        onGround = false;

        // 세로 이동은 발바닥까지 정확히 체크해야 하므로 원래대로 둡니다.
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

        checkBoxStand(); // 상자 밟기
        checkGimmicks(); // 스위치/함정 체크
    }

    private void checkBoxPush() {
        Box box = mainMap.getBox();
        if (box == null) return;

        Rectangle myRect = new Rectangle(x, y, width, height);
        Rectangle boxRect = box.getBounds();

        if (!myRect.intersects(boxRect)) return;

        // 오른쪽으로 밀기
        if (xSpeed > 0) {
            if (x + width <= box.x + 10) {
                box.push(xSpeed);

                // 플레이어와 박스가 겹치지 않도록 위치 보정
                if (myRect.intersects(boxRect)) {
                    x = box.x - width - 1;
                }
            }
        }
        // 왼쪽으로 밀기
        else if (xSpeed < 0) {
            if (x >= box.x + box.width - 10) {
                box.push(xSpeed);

                if (myRect.intersects(boxRect)) {
                    x = box.x + box.width + 1;
                }
            }
        }
    }

    private void checkBoxStand() {
        Box box = mainMap.getBox();
        if (box == null) return;
        // 발밑 검사 (폭을 좁게)
        Rectangle myFeet = new Rectangle(x + 5, y, width - 10, height); 
        Rectangle boxRect = box.getBounds();
        if (myFeet.intersects(boxRect)) {
            if (ySpeed > 0 && y + height <= box.y + 15) { onGround = true; y = box.y - height; ySpeed = 0; }
            else if (ySpeed < 0 && y >= box.y + box.height - 15) { y = box.y + box.height; ySpeed = 0; }
        }
    }

    private void checkGimmicks() { //기믹 작동
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int tx = centerX / Collision.TILE_SIZE;
        int ty = centerY / Collision.TILE_SIZE;

        if (ty < 0 || ty >= Collision.tileMap.length || tx < 0 || tx >= Collision.tileMap[0].length) return;

        int tile = Collision.tileMap[ty][tx]; 

        // 1. 발판(함정) 밟으면 리스폰
        if ((tile == Collision.PAD_GIRL && type == 1) || 
            (tile == Collision.PAD_DOG && type == 2)) {
            dead();
        } 
        
        // 2. 스위치 작동
        else if (tile == Collision.SWITCH_GIRL || tile == Collision.SWITCH_DOG || tile == Collision.SWITCH_GIRL1 || tile == Collision.SWITCH_DOG1) {
        	int targetDoor = 0; // 열어야 할 문 번호 저장 변수

            // 어떤 스위치인지 확인해서 짝꿍 문을 지정
            if (tile == Collision.SWITCH_GIRL) {
                targetDoor = Collision.DOOR_GIRL;
            } else if (tile == Collision.SWITCH_DOG) {
                targetDoor = Collision.DOOR_DOG;
            } else if (tile == Collision.SWITCH_GIRL1) {
                targetDoor = Collision.DOOR_GIRL1; // 짝꿍 지정
            } else if (tile == Collision.SWITCH_DOG1) {
                targetDoor = Collision.DOOR_DOG1;  // 짝꿍 지정
            }
            //스위치 눌린 모양
            int finalState = (xSpeed > 0) ? Collision.SWITCH_ON_RIGHT : Collision.SWITCH_ON_LEFT;
            //서로 상호작용하는 targetDoor를 없앰
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