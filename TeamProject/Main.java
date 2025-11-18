// --- 필요한 라이브러리/클래스들을 가져옵니다. ---

import javax.swing.JFrame; 
// JFrame 클래스를 가져옵니다. 
// Java Swing GUI에서 최상위 창(Window) 역할을 하며, 게임 화면 전체를 담는 컨테이너입니다.

public class Main { // 프로그램의 시작점(Entry Point) 클래스
	
	// --- 메인 메서드: 프로그램이 실행될 때 가장 먼저 호출되는 메서드 ---
	public static void main(String[] args) {
		
		// 1. 게임 창 (프레임) 생성
		JFrame frame = new JFrame("미로 탐색 게임"); // 새로운 JFrame 객체를 생성하고 창 제목을 설정합니다.
		
		// 2. 게임 패널 생성 및 추가
		GamePanel gamePanel = new GamePanel(); // 게임 로직과 화면을 담당하는 GamePanel 객체를 생성합니다.
		
		frame.add(gamePanel); // 생성된 GamePanel을 JFrame에 추가하여 화면에 표시되도록 합니다.
		
		// 3. 창 크기 계산 및 설정
		
		// 창 크기 설정: GamePanel의 Getter 메서드를 사용하여 미로 크기에 맞게 계산
		int width = gamePanel.getMapWidth() * gamePanel.getTileSize(); // 미로의 가로 타일 수 * 타일 크기로 가로 폭 계산
		int height = gamePanel.getMapHeight() * gamePanel.getTileSize() + 25; // 미로의 세로 타일 수 * 타일 크기에 상단바 높이(약 25픽셀)를 더하여 세로 폭 계산
		
		frame.setSize(width, height); // 계산된 크기로 JFrame의 크기를 설정합니다.
		
		// 4. 창 속성 설정
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 사용자가 창의 'X' 버튼을 누르면 프로그램이 완전히 종료되도록 설정합니다.
		frame.setLocationRelativeTo(null); // 창이 화면의 중앙에 위치하도록 설정합니다.
		frame.setResizable(false); // 사용자가 마우스로 창 크기를 조절할 수 없도록 설정합니다.
		frame.setVisible(true); // 창을 화면에 보이도록 설정합니다. (모든 설정 후 마지막에 호출)
		
	}
}
