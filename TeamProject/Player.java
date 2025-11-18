// --- 필요한 라이브러리/클래스들을 가져옵니다. ---
import java.awt.Image; 
// Image 클래스를 가져옵니다. 
// 주인공 캐릭터의 이미지(사진 에셋) 데이터를 저장하고 다루기 위해 필요합니다.

import java.awt.event.KeyEvent;
// KeyEvent 클래스를 가져옵니다. 
// 키보드 입력(예: 방향키)을 감지하고, 해당 키보드 입력의 고유한 정수 코드(KeyCode)를 사용하기 위해 필요합니다.

public class Player { // 주인공(플레이어) 객체의 상태와 행동을 정의하는 클래스
	
	private int x, y; // 주인공의 현재 위치 좌표 (픽셀 기준)
	
	// 주인공 객체의 고정 속성 (final로 선언하여 값이 변하지 않음)
	private final int SIZE = 32;        // 주인공을 화면에 그릴 때의 크기 (32x32 픽셀)
	private final int MOVE_SPEED = 4;   // 한 번의 키 입력으로 이동하는 거리 (속도)
	
	// 주인공이 화면에 표시될 때 사용할 이미지 데이터
	private Image playerImage;
	
	/**
	 * Player 클래스의 생성자
	 * @param startX 주인공의 초기 X 좌표
	 * @param startY 주인공의 초기 Y 좌표
	 * @param img 로드된 주인공 이미지 객체
	 */
	public Player(int startX, int startY, Image img) {
		this.x = startX; // 인자로 받은 초기 X 좌표를 객체 변수에 할당
		this.y = startY; // 인자로 받은 초기 Y 좌표를 객체 변수에 할당
		this.playerImage = img; // 로드된 이미지 객체를 할당
	}
	
	// --- Getter 메서드: 외부(GamePanel)에서 주인공의 상태를 읽기 위한 메서드 ---
	public int getX() { return x; } // 현재 X 좌표 반환
	public int getY() { return y; } // 현재 Y 좌표 반환
	public int getSize() { return SIZE; } // 주인공의 크기 반환
	public Image getImage() { return playerImage; } // 주인공의 이미지 객체 반환
	
	/**
	 * 이동 메서드: 키 코드에 따라 주인공의 위치 좌표를 변경
	 * @param KeyCode 눌린 키에 해당하는 정수 값 (KeyEvent.VK_UP, VK_DOWN 등)
	 */
	public void move(int KeyCode) {
	    // GamePanel에서 충돌 체크를 진행하기 전에, 임시로 위치를 변경
		int nextX = x, nextY = y;
		switch(KeyCode) {	
		// 미로 충돌 체크는 GamePanel에서 player.move() 호출 후 checkCollision()으로 확인
		case KeyEvent.VK_UP: // ↑ (위) 키가 눌렸을 경우 (KeyEvent 클래스의 상수 사용)
			nextY = y - MOVE_SPEED; // Y 좌표를 MOVE_SPEED만큼 감소 (화면 상단으로 이동)
			break;
		case KeyEvent.VK_DOWN: // ↓ (아래) 키가 눌렸을 경우
			nextY = y + MOVE_SPEED; // Y 좌표를 MOVE_SPEED만큼 증가 (화면 하단으로 이동)
			break;
		case KeyEvent.VK_LEFT: // ← (왼쪽) 키가 눌렸을 경우
			nextX = x - MOVE_SPEED; // X 좌표를 MOVE_SPEED만큼 감소 (왼쪽으로 이동)
			break;
		case KeyEvent.VK_RIGHT: // → (오른쪽) 키가 눌렸을 경우
			nextX = x + MOVE_SPEED; // X 좌표를 MOVE_SPEED만큼 증가 (오른쪽으로 이동)
			break;
		// 다른 키 입력은 무시 (별도 동작 없음)
		}
		// 벽 충돌 검사
        if (!Collision.checkWallCollision(nextX, nextY)) {
            x = nextX;
            y = nextY;
		}
		// else: 벽이면 움직이지 않음
	}
}
