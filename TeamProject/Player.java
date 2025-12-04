import java.awt.Rectangle;
import javax.swing.JLabel;

public class Player implements Moveable {
    int x, y, startX, startY; 
    int width = 50, height = 50;
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
        
        // ★ 수정: 좌우 동시에 눌리면 멈추도록
        if (left && !right) xSpeed = -RUN_SPEED;
        else if (right && !left) xSpeed = RUN_SPEED;
        else xSpeed = 0;

        // 점프: 지면에서만 가능 (이 로직은 원래 그대로 유지)
        if (up && onGround) { 
            ySpeed = JUMP_POWER; 
            onGround = false; 
        }

        // 중력
        ySpeed += GRAVITY;

        // ★ 수정: 너무 빨리 떨어져서 튀는 것 방지
        if (ySpeed > 15) ySpeed = 15;
        
        moveAndCheckCollision();
        character.setLocation(x, y);
    }

    private void moveAndCheckCollision() {
        // =========================
        // 1) 가로 이동 (원래 로직 거의 그대로)
        // =========================
        x += xSpeed;
        
        checkBoxPush(); // 상자 밀기

        if (Collision.isColliding(x, y, width, height)) {
            if (xSpeed > 0) {
                x = ((x + width) / Collision.TILE_SIZE) * Collision.TILE_SIZE - width - 1;
            } else if (xSpeed < 0) {
                x = (x / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
            }
            xSpeed = 0;
        }
        
        // =========================
        // 2) 세로 이동 (여기만 핵심 수정)
        // =========================
        y += ySpeed;
        onGround = false;

        // 발밑/머리 쪽 정확히 보려고 폭 좁혀서 체크 (원래 있던 구조)
        if (Collision.isColliding(x + 5, y, width - 10, height)) {

            if (ySpeed > 0) { 
                // ↓ 떨어지는 중에 바닥에 박힌 상태
                // ★ 수정: 바닥에 끼지 않도록 한 픽셀씩 위로 빼기
                while (Collision.isColliding(x + 5, y, width - 10, height)) {
                    y -= 1;
                }
                onGround = true;
            } 
            else if (ySpeed < 0) { 
                // ↑ 점프 중에 천장에 머리 박힌 상태
                // ★ 수정: 천장에 끼지 않도록 한 픽셀씩 아래로 빼기
                while (Collision.isColliding(x + 5, y, width - 10, height)) {
                    y += 1;
                }
            }

            ySpeed = 0;
        }

        // ★ 추가: 바닥 바로 위에 떠 있는 상태도 onGround로 인식
        //   (점프가 안 먹는 주요 원인: onGround가 false로 계속 유지될 때)
        if (!onGround) {
            // 현재 위치는 안 부딪치는데, 1픽셀 아래는 부딪치면 "발밑에 바닥 있음"
            if (!Collision.isColliding(x + 5, y, width - 10, height) &&
                 Collision.isColliding(x + 5, y + 1, width - 10, height)) {
                onGround = true;
            }
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
            if (ySpeed > 0 && y + height <= box.y + 15) { 
                onGround = true; 
                y = box.y - height; 
                ySpeed = 0; 
            }
            else if (ySpeed < 0 && y >= box.y + box.height - 15) { 
                y = box.y + box.height; 
                ySpeed = 0; 
            }
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
        else if (tile == Collision.SWITCH_GIRL || tile == Collision.SWITCH_DOG 
              || tile == Collision.SWITCH_GIRL1 || tile == Collision.SWITCH_DOG1) {
            
            int targetDoor = 0; // 열어야 할 문 번호 저장 변수

            // 어떤 스위치인지 확인해서 짝꿍 문을 지정
            if (tile == Collision.SWITCH_GIRL) {
                targetDoor = Collision.DOOR_GIRL;
            } else if (tile == Collision.SWITCH_DOG) {
                targetDoor = Collision.DOOR_DOG;
            } else if (tile == Collision.SWITCH_GIRL1) {
                targetDoor = Collision.DOOR_GIRL1; 
            } else if (tile == Collision.SWITCH_DOG1) {
                targetDoor = Collision.DOOR_DOG1;
            }

            // 스위치 눌린 모양
            int finalState = (xSpeed > 0) ? Collision.SWITCH_ON_RIGHT : Collision.SWITCH_ON_LEFT;

            // 서로 상호작용하는 targetDoor를 없앰
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
