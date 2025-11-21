import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Player implements Moveable {

    // 1. 위치 및 속도
    int x, y;                    // 내 위치
    int width = 50, height = 50; // 내 크기 (이미지 크기와 같게 설정하세요)
    
    double xSpeed = 0;           // 가로 속도
    double ySpeed = 0;           // 세로 속도

    // 2. 상태 체크
    boolean left, right, up, down; // 키보드 상태
    boolean onGround = false;      // 땅에 닿았는지 여부
    boolean isDead = false;        // 게임 오버 여부

    // 3. 게임 물리 설정값
    final double GRAVITY = 0.5;    // 중력
    final double JUMP_POWER = -12; // 점프력
    final double RUN_SPEED = 5;    // 이동 속도

    public JLabel character; // 화면에 보여질 이미지 라벨
    Player otherPlayer;      // 상대방 플레이어 (만나면 클리어)

    // 생성자
    public Player(JPanel m_map, int startX, int startY) {
        this.x = startX;
        this.y = startY;
        
        character = new JLabel();
        character.setBounds(x, y, width, height);

        // ⚠️ 중요: 여기서 new MainMap()을 절대 하지 않습니다! (무한 루프 방지)
        
        startPhysicsLoop(); // 물리 엔진 가동
    }
    
    // 친구 등록
    public void setOtherPlayer(Player p) {
        this.otherPlayer = p;
    }

    // --- ⚙️ 물리 엔진 (무한 루프 스레드) ---
    private void startPhysicsLoop() {
        new Thread(() -> {
            while (!isDead) { 
                
                // 1. 좌우 이동 속도 계산
                if (left) xSpeed = -RUN_SPEED;
                else if (right) xSpeed = RUN_SPEED;
                else xSpeed = 0; 

                // 2. 점프 계산 (땅에 있을 때만)
                if (up && onGround) { 
                    ySpeed = JUMP_POWER; 
                    onGround = false;    
                }

                // 3. 중력 적용
                ySpeed = ySpeed + GRAVITY; 

                // 4. 실제 이동 및 충돌 검사 (핵심!)
                moveAndCheckCollision();

                // 5. 화면 위치 업데이트
                character.setLocation(x, y);

                try { Thread.sleep(20); } catch (Exception e) {} // 0.02초 딜레이
            }
        }).start();
    }

    // --- 🧱 충돌 검사 로직 (Collision 클래스 사용) ---
    private void moveAndCheckCollision() {
        
        // [가로 이동]
        x += xSpeed; 
        // Collision 클래스(static)에게 벽인지 물어봄
        if (Collision.isColliding(x, y, width, height)) { 
             
             if (xSpeed > 0) { // 오른쪽으로 가다 박음
                 // 내 오른쪽 끝이 벽의 왼쪽에 딱 붙게 위치 보정
                 // (현재 위치 / 32) * 32 -> 내 타일의 시작점
                 x = ((x + width) / Collision.TILE_SIZE) * Collision.TILE_SIZE - width - 1;
             } 
             else if (xSpeed < 0) { // 왼쪽으로 가다 박음
                 // 내 왼쪽 끝이 벽의 오른쪽에 딱 붙게 위치 보정
                 x = (x / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
             }
             
             xSpeed = 0; // 속도 0으로 (더 이상 못 감)
        }

        // [세로 이동]
        y += ySpeed;
        onGround = false; // 일단 공중에 있다고 가정
        
        // 바닥/천장 충돌 체크
        if (Collision.isColliding(x, y, width, height)) {
            
            if (ySpeed > 0) { // 아래로 떨어지다가 닿음 -> 바닥 착지!
                onGround = true;
                // 발바닥을 땅 높이에 딱 맞춤
                 y = ((y + height) / Collision.TILE_SIZE) * Collision.TILE_SIZE - height - 1;
            } 
            else if (ySpeed < 0) { // 점프하다 천장에 박음
                 // 머리를 천장 아래에 딱 맞춤
                 y = (y / Collision.TILE_SIZE) * Collision.TILE_SIZE + Collision.TILE_SIZE;
            }
            
            ySpeed = 0;
        }
        
        // [친구 만남 체크] (게임 클리어)
        if (otherPlayer != null) {
            Rectangle me = new Rectangle(x, y, width, height);
            Rectangle friend = new Rectangle(otherPlayer.x, otherPlayer.y, width, height);
            
            if (me.intersects(friend)) {
                System.out.println("🎉 게임 클리어! 친구를 만났습니다.");
                System.exit(0); // 게임 종료
            }
        }
        
        // [함정 체크] 
        // 나중에 Collision 클래스에 함정(숫자 2)이 추가되면 여기서 체크합니다.
    }

    // --- 🎮 키보드 컨트롤 (Moveable 인터페이스 구현) ---
    @Override public void left() { left = true; }
    @Override public void right() { right = true; }
    @Override public void up() { up = true; } 
    @Override public void down() { down = true; }

    @Override public void left_released() { left = false; }
    @Override public void right_released() { right = false; }
    @Override public void up_released() { up = false; }
    @Override public void down_released() { down = false; } // 🔥 아까 빠졌던 부분 추가 완료

    @Override
    public void dead() {
        isDead = true;
        System.out.println("💀 으악! 죽었습니다.");
        // 여기에 게임 오버 팝업 등을 띄울 수 있습니다.
    }

    // 사용하지 않지만 인터페이스 규약상 필요한 메서드들
    @Override public void idle() {}
    @Override public void initIndex() {}
}