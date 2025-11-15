// --- 필요한 라이브러리/클래스들을 가져옵니다. ---

import javax.swing.*;       // Swing 라이브러리: GUI 컴포넌트(JFrame, JPanel 등)를 사용하기 위해 필요합니다.
import javax.imageio.ImageIO; // ImageIO 클래스: 외부 이미지 파일(에셋)을 읽어 메모리로 로드하기 위해 필요합니다.
import java.awt.*;          // AWT 라이브러리: 기본 그래픽 요소(Image, Color, Graphics 등)를 사용하기 위해 필요합니다.
import java.awt.event.KeyEvent; // KeyEvent 클래스: 키보드 이벤트(키 눌림, 떼어짐)를 처리하기 위해 필요합니다.
import java.awt.event.KeyListener; // KeyListener 인터페이스: 키보드 입력을 처리하는 메서드를 구현하기 위해 필요합니다.
import java.io.File;        // File 클래스: 이미지 파일의 경로를 지정하고 파일을 다루기 위해 필요합니다.
import java.io.IOException;   // IOException 클래스: 파일 입출력 과정(이미지 로드)에서 발생할 수 있는 예외 처리를 위해 필요합니다.
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements KeyListener { // 게임의 메인 화면이자 로직을 담당하는 패널 클래스
	
	// --- 필드(멤버 변수) ---
	private final int TILE_SIZE = 32; // 미로의 한 칸(타일) 크기 (픽셀)
	
	// 미로 맵 데이터: 2차원 배열로 정의. (0 = 길, 1 = 벽)
	private final int[][] MAP = {
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
			{1, 0, 1, 0, 0, 1, 0, 1, 0, 1},
			{1, 0, 1, 0, 1, 1, 0, 1, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
	};
	private Player player; // 주인공 Player 객체를 저장할 변수 선언
	private BufferedImage background; // ✅ 배경 이미지 필드 추가
	
	// --- 생성자 ---
	public GamePanel() {
		this.setFocusable(true); // 이 패널이 키 입력을 받을 수 있도록 포커스를 설정
		this.addKeyListener(this); // 이 패널에 키 입력 리스너(자신)를 등록
		loadAssets(); // 게임에 필요한 이미지 에셋을 로드하는 메서드 호출
		loadBackground(); // ✅ 배경 로드 메서드 호출
	}
	// --- 배경 이미지 로드 ---
	private void loadBackground() {
		try {
			background = ImageIO.read(new File("assets/mansion_background1.png"));
		} catch (IOException e) {
			System.err.println("배경 이미지를 로드할 수 없습니다. 경로를 확인하세요.");
			e.printStackTrace();
		}
	}
	
	// --- Getter 메서드: Main 클래스가 창 크기 계산을 위해 사용 ---
	public int getMapHeight() {
		return MAP.length; // 맵의 세로 길이 (행의 개수) 반환
	}
	public int getMapWidth() {
		return MAP[0].length; // 맵의 가로 길이 (열의 개수) 반환
	}
	public int getTileSize() {
		return TILE_SIZE; // 타일의 크기 반환
	}
	
	// --- 이미지 로드 메서드 ---
	private void loadAssets() {
		try {
			// ImageIO를 사용하여 지정된 경로의 이미지 파일을 로드
			Image img = ImageIO.read(new File("C:\\Users\\user\\eclipse-workspace\\teample_java2\\images\\여자 주인공.png"));
			// 로드된 이미지와 함께 Player 객체를 생성하고 초기 위치 설정 (1행 1열)
			player = new Player(TILE_SIZE * 1, TILE_SIZE * 1, img);
		} catch (IOException e) {
			System.err.println("캐릭터 이미지를 로드할 수 없습니다. 경로를 다시 확인하세요");
			e.printStackTrace(); // 예외 발생 시 오류 메시지 출력
		}
	}
	
	// --- 드로잉 메서드 (화면을 그리는 핵심 메서드) ---
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // 부모 클래스의 paintComponent를 호출하여 화면 지우기
		// ✅ 배경 먼저 그림
		if (background != null) {
			g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		}
		drawMaze(g); // 미로 맵을 그림
		drawPlayer(g); // 플레이어 객체를 그림
		Toolkit.getDefaultToolkit().sync(); // 화면 갱신 동기화 (화면 깜빡임 방지)
	}
	
	// 미로 그리기 (벽과 길을 사각형으로 표현)
	private void drawMaze(Graphics g) {
		for (int row = 0; row < MAP.length; row++) {
			for (int col = 0; col < MAP[0].length; col++) {
				if (MAP[row][col] == 1) { // 맵 데이터가 1이면 벽
					g.setColor(new Color(50, 50, 50, 180)); // 벽의 색상 설정
					g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE); // 벽 사각형 그리기
				} else { // 맵 데이터가 0이면 길
					g.setColor(Color.LIGHT_GRAY); // 길의 색상 설정
					g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE); // 길 사각형 그리기
				}
			}
		}
	}
	
	// 플레이어 그리기 (로드된 이미지 사용)
	private void drawPlayer(Graphics g) {
		if (player != null && player.getImage() != null) { // Player 객체와 이미지가 정상적으로 로드되었는지 확인
			g.drawImage(
					player.getImage(), 
					player.getX(), 
					player.getY(), // 이미지 객체를 주인공의 위치와 크기에 맞게 화면에 그림
					player.getSize(), 
					player.getSize(), 
					this
			);
		}
	}
	
	// --- 키 입력 및 충돌 처리 메서드 (KeyListener 인터페이스 구현) ---
	@Override
	public void keyPressed(KeyEvent e) {
		// 이동하기 전의 좌표를 저장 (벽에 막혔을 때 되돌릴 용도)
		int oldX = player.getX();
		int oldY = player.getY();
		
		// Player 객체의 move 메서드를 호출하여 임시로 위치 변경
		player.move(e.getKeyCode());
		
		// 충돌 검사
		if (checkCollision()) {
			// 충돌이 발생하면, 주인공 객체를 이전 좌표로 새로 생성 (혹은 setX, setY로 되돌림)
			player = new Player(oldX, oldY, player.getImage());
		}
		
		repaint(); // 위치가 변경되었으니 화면을 다시 그림 (paintComponent가 다시 호출됨)
	}
	
	private boolean checkCollision() {
		// 주인공이 현재 위치한 맵 타일의 인덱스 계산 (주인공 객체의 중앙점 기준)
		int playerTileCol = (player.getX() + player.getSize() / 2) / TILE_SIZE;
		int playerTileRow = (player.getY() + player.getSize() / 2) / TILE_SIZE;
		
		// 맵 범위를 벗어났는지 확인 (미로 경계를 넘어갔는지)
		if (playerTileRow < 0 || playerTileRow >= MAP.length ||
				playerTileCol < 0 || playerTileCol >= MAP[0].length) {
			return true; // 맵 밖은 충돌로 간주
		}
		
		// 현재 주인공 위치의 맵 값이 1(벽)인지 확인
		return MAP[playerTileRow][playerTileCol] == 1;
	}
	
	// KeyListener 인터페이스의 나머지 두 메서드는 사용하지 않더라도 반드시 구현해야 함
	@Override
	public void keyTyped(KeyEvent e) {} // 키가 입력되었을 때 (눌렀다 뗄 때까지의 과정)
	
	@Override
	public void keyReleased(KeyEvent e) {} // 키에서 손을 떼었을 때
	
}
