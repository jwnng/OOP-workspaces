import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // 1. 윈도우 창(Frame) 만들기
        JFrame window = new JFrame("2인용 포레스트 템플 게임");
        
        // 2. 닫기 버튼 누르면 프로그램 종료되게 설정
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 3. 우리가 만든 게임 화면(MainMap)을 가져와서 창에 넣기
        MainMap gamePanel = new MainMap();
        window.add(gamePanel);
        
        // 4. 창 크기 설정 (넉넉하게 1000x800 정도로 설정)
        window.setSize(1000, 800); 
        
        // 5. 창을 화면 가운데에 띄우기
        window.setLocationRelativeTo(null); 
        
        // 6. 창 보이게 하기! (이게 있어야 눈에 보임)
        window.setVisible(true);
    }
}