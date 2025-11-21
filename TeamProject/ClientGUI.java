ClientGUI    package project;

// 필요한 도구(라이브러리)들을 가져오는 부분이야.
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

// JFrame을 상속받아서 윈도우 창(틀)을 만드는 클래스야.
public class ClientGUI extends JFrame {
    // --- 게임 등장인물과 맵 ---
    private Bear bear;      // 곰돌이 캐릭터
    private Rabbit rabbit;  // 토끼 캐릭터
    private MainMap mainMap;// 전체 맵 관리자
    private JPanel m_map;   // 실제로 그림이 그려질 맵 패널
    private OptionPane oPane; // 옵션 창(체력바 같은 것)
    
    // --- 화면 꾸미기용 그림들 ---
    private JLabel backgroundLabel, back_cake, back_choice; // 배경, 케이크 그림, 캐릭터 선택 배경
    private JTextPane t_display; // 글자를 보여줄 판 (채팅창 같은 용도인데 여기선 로그용)
    private ImageIcon i_cake, i_choice, i_startB; // 이미지 파일들
    
    // --- 버튼들 ---
    private JButton b_gameStart, b_sound; // 게임 시작 버튼, 소리 버튼
    private DefaultStyledDocument document; // 텍스트 판의 내용물 관리자
    
    // --- 서버 연결 정보 ---
    private String serverAddress; // 서버 주소 (IP)
    private int serverPort;       // 서버 포트 번호 (문 번호)
    
    // --- 네트워크 통신 도구 ---
    private Socket socket;        // 서버와 연결되는 전화기
    private DataOutputStream out; // 서버로 데이터를 보내는 확성기
    private Thread receiveThread = null; // 서버의 말을 계속 듣는 귀 (스레드)
    private String uid;           // 내가 곰인지 토끼인지 정하는 ID


    // 맵 크기 설정
    int panelWidth = 715; 
    int mapWidth = 1800;
    int screenCenterX = panelWidth / 2;
    int mapX = 0; // 맵의 X 좌표
    
    // --- 생성자: 게임 창을 처음 만들 때 실행됨 ---
    public ClientGUI(String serverAddress, int serverPort) {
        super("Client GUI"); // 창의 제목 설정
        this.serverAddress = serverAddress; 
        this.serverPort = serverPort;

        setLayout(null); // 내 마음대로 위치를 잡기 위해 레이아웃 매니저 끔
        buildGUI(); // 화면 꾸미기 시작!
        
        setSize(715, 738); // 창 크기 설정
        setLocation(500,150); // 창이 모니터 어디에 뜰지 설정

        // X 버튼 누르면 프로그램 완전히 종료
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // "창아, 이제 보여라! 얍!"
    }

    // GUI 조립 공장
    private void buildGUI() {
        createDisplayPanel(); // 배경화면 만들기
        createControlPanel(); // 버튼 기능 만들기
    }
    
    // (주석 처리된 updateMap 메서드는 생략 - 사용하지 않음)

    // --- 첫 시작 화면(배경, 버튼 등) 만들기 ---
    private void createDisplayPanel() {
        // 배경 이미지 불러오기
        i_startB = new ImageIcon("images/background/b_start.png");
        backgroundLabel = new JLabel(i_startB);
        backgroundLabel.setBounds(0, 0, i_startB.getIconWidth(), i_startB.getIconHeight());
        backgroundLabel.setLayout(null); // 배경 위에 버튼을 올리기 위해 레이아웃 끔
        add(backgroundLabel); // 창에 배경 붙이기
        
        // 케이크 그림 장식 추가
        i_cake = new ImageIcon("images/background/b_cake.png");
        back_cake = new JLabel(i_cake);
        back_cake.setBounds(20, 20, i_cake.getIconWidth(), i_cake.getIconHeight());
        backgroundLabel.add(back_cake);
        
        // 버튼 이미지 불러오기
        ImageIcon i_play = new ImageIcon("images/button/playButton.png");
        ImageIcon i_sound = new ImageIcon("images/button/soundButton.png");
        
        // 게임 시작 버튼 만들기
        b_gameStart = new JButton(i_play);
        b_gameStart.setBounds(260, 300, i_play.getIconWidth(), i_play.getIconHeight());
        b_gameStart.setBorderPainted(false);  // 버튼 테두리 없애기 (이쁘게)
        b_gameStart.setContentAreaFilled(false);  // 버튼 배경색 없애기 (투명하게)

        // 사운드 버튼 만들기
        b_sound = new JButton(i_sound);
        b_sound.setBounds(520, 20, i_play.getIconWidth(), 100);
        b_sound.setBorderPainted(false); 
        b_sound.setContentAreaFilled(false); 
        b_sound.setFocusPainted(false); // 클릭했을 때 생기는 점선 테두리 제거
        
        // 배경 위에 버튼들 붙이기
        backgroundLabel.add(b_gameStart);
        backgroundLabel.add(b_sound); 
        
        setVisible(true); // 화면 갱신
    }
    
    // 화면에 메시지를 띄워주는 함수 (로그 출력용)
    private void printDisplay(String msg) {
        int len = t_display.getDocument().getLength();
        try {
            document.insertString(len, msg + "\n", null); // 글자 추가
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        t_display.setCaretPosition(len); // 스크롤을 항상 맨 아래로
    }
    
    // --- 버튼들이 눌렸을 때 할 일을 정하는 곳 ---
    private void createControlPanel() {
        // '게임 시작' 버튼을 눌렀을 때!
        b_gameStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 캐릭터 선택 화면으로 전환 준비
                ImageIcon i_bearButton = new ImageIcon("images/button/b_bear.png");
                ImageIcon i_rabbitButton = new ImageIcon("images/button/b_rabbit.png");
                i_choice = new ImageIcon("images/background/b_choice.png");
                
                back_choice = new JLabel(i_choice);
                JButton b_bear = new JButton(i_bearButton);   // 곰 선택 버튼
                JButton b_rabbit = new JButton(i_rabbitButton); // 토끼 선택 버튼
                
                // 위치 잡기
                back_choice.setBounds(230, 20, i_choice.getIconWidth(), i_choice.getIconHeight());
                b_bear.setBounds(50, 240, i_bearButton.getIconWidth(), i_bearButton.getIconHeight());
                b_rabbit.setBounds(500, 240, i_rabbitButton.getIconWidth(), i_rabbitButton.getIconHeight());
                
                // 버튼 꾸미기 (투명 배경)
                b_bear.setBorderPainted(false); 
                b_bear.setContentAreaFilled(false); 
                b_rabbit.setBorderPainted(false); 
                b_rabbit.setContentAreaFilled(false); 
                
                // 화면 구성 요소 교체 (기존 시작 버튼 지우고 선택 버튼 추가)
                backgroundLabel.add(b_bear);
                backgroundLabel.add(b_rabbit);
                backgroundLabel.add(back_choice);
                backgroundLabel.remove(b_gameStart); // 시작 버튼 삭제
                backgroundLabel.remove(back_cake);   // 케이크 삭제
                
                backgroundLabel.revalidate(); // 화면 새로고침
                backgroundLabel.repaint();    // 다시 그리기
                
                // --- 곰 버튼을 눌렀을 때! ---
                b_bear.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        uid = "Bear"; // 나는 이제부터 곰이다!
                        
                        // 게임 화면 설정 (옵션창, 맵 생성)
                        // GlassPane은 화면 맨 위에 투명한 유리를 깐 것과 같아. 여기에 옵션창을 띄움.
                        oPane = new OptionPane();
                        oPane.setMainFrame(ClientGUI.this); 
                        JComponent glassPane = (JComponent) getGlassPane(); 
                        glassPane.setLayout(null); 
                        glassPane.setBounds(0, 0, 1100, 738); 
                        
                        oPane.getPane().setBounds(0, 0, 1100, 738); 
                        
                        glassPane.add(oPane.getPane()); 
                        glassPane.setVisible(true); 
                        
                        add(oPane.getPane());
                        
                        // 시작 화면(배경) 지우기
                        remove(backgroundLabel);
                        
                        // 진짜 게임 맵 생성!
                        mainMap = new MainMap();
                        m_map = mainMap.getMainMap();
                        
                        // 캐릭터 생성
                        bear = new Bear(m_map, oPane);
                        rabbit = new Rabbit(m_map, oPane);
                        
                        // 맵에 캐릭터들 올리기
                        m_map.add(bear.getCharacter());
                        m_map.add(rabbit.getCharacter());
                        add(m_map);

                        setSize(1100, 738); // 게임 화면 크기로 변경
                        revalidate();
                        repaint();                      
                        
                        // 서버 연결 시도! (중요: 나는 곰이니까, 서버에서 오는 메시지는 '토끼'를 움직여야 해!)
                        try {
                            connectToServer(rabbit); // 반대 캐릭터(토끼)를 제어할 권한을 줌
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        
                        // --- 키보드 입력 처리 (내가 누르는 키) ---
                        addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                if (bear.isDead)return; // 죽었으면 움직이지 마
                                int keyCode = e.getKeyCode();
                                bear.idle = false; // 움직이니까 멈춤 상태 해제
                                    switch (keyCode) {
                                        case KeyEvent.VK_LEFT : // 왼쪽 화살표
                                            send(KeyMsg.KEY_LEFT); // 서버에 "나 왼쪽 가요!" 전송
                                            bear.left();           // 내 화면에서도 왼쪽으로 이동
                                            break;
                                        case KeyEvent.VK_RIGHT : // 오른쪽 화살표
                                            send(KeyMsg.KEY_RIGHT); // 서버에 "나 오른쪽 가요!" 전송
                                            bear.right();           // 내 화면에서도 오른쪽으로 이동
                                            break;
                                        case KeyEvent.VK_SPACE: // 스페이스바
                                            send(KeyMsg.KEY_SPACE); // 서버에 "점프!" 전송
                                            bear.up();              // 점프
                                            break;
                                        case KeyEvent.VK_A: // A키
                                            send(KeyMsg.KEY_A);     // 서버에 "으악 죽음!" 전송 (테스트용인듯)
                                            bear.dead();            // 죽는 모션
                                            break;
                                    }
                                    m_map.repaint(); // 화면 갱신
                            }
                            @Override
                            public void keyReleased(KeyEvent e) {
                                // 키보드에서 손을 뗐을 때
                                int keyCode = e.getKeyCode();
                                bear.initIndex(); // 애니메이션 초기화
                                if (keyCode == KeyEvent.VK_LEFT) {
                                    send(KeyMsg.KEY_LEFT_RELEASED); // "왼쪽 멈춤" 전송
                                    bear.left_released();
                                    bear.idle(); // 가만히 있는 상태로
                                } else if (keyCode == KeyEvent.VK_RIGHT) {
                                    send(KeyMsg.KEY_RIGHT_RELEASED); // "오른쪽 멈춤" 전송
                                    bear.right_released();
                                    bear.idle();
                                } 
                                m_map.repaint();
                            }
                        });

                        // 창이 키보드 입력을 받을 수 있게 포커스 요청
                        requestFocusInWindow();
                    }
                });

                // --- 토끼 버튼을 눌렀을 때! (곰과 로직은 똑같고 캐릭터만 반대) ---
                b_rabbit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        uid = "Rabbit"; // 나는 토끼다!
                        System.out.println("UID set to: " + uid); 

                        remove(backgroundLabel); // 배경 지우기
                        
                        // 옵션창 설정 (위와 동일)
                        oPane = new OptionPane();
                        JComponent glassPane = (JComponent) getGlassPane(); 
                        glassPane.setLayout(null); 
                        glassPane.setBounds(0, 0, 1100, 738); 
                        
                        oPane.getPane().setBounds(0, 0, 1100, 738); 
                        glassPane.add(oPane.getPane()); 
                        glassPane.setVisible(true); 
                        add(oPane.getPane());

                        // 맵과 캐릭터 생성
                        mainMap = new MainMap();
                        m_map = mainMap.getMainMap();
                        rabbit = new Rabbit(m_map, oPane);
                        bear = new Bear(m_map, oPane);
                        m_map.add(rabbit.getCharacter());
                        m_map.add(bear.getCharacter());
                        add(m_map);
                        
                        setSize(1100, 738);                     
                        revalidate();
                        repaint();
                        
                        // 서버 연결! (나는 토끼니까, 서버 메시지로 '곰'을 움직여야 해!)
                        try {
                            connectToServer(bear); // 반대 캐릭터(곰)를 넘겨줌
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        // 키보드 입력 (나는 토끼를 조종)
                        addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                if (rabbit.isDead)return; 
                                int keyCode = e.getKeyCode();
                                rabbit.idle = false;
                                    switch (keyCode) {
                                        case KeyEvent.VK_LEFT :
                                            send(KeyMsg.KEY_LEFT);
                                            rabbit.left();
                                            break;
                                        case KeyEvent.VK_RIGHT :
                                            send(KeyMsg.KEY_RIGHT);
                                            rabbit.right();
                                            break;
                                        case KeyEvent.VK_SPACE:
                                            send(KeyMsg.KEY_SPACE);
                                            rabbit.up();
                                            break;
                                        case KeyEvent.VK_A:
                                            send(KeyMsg.KEY_A);
                                            rabbit.dead();
                                            break;
                                    }
                                    m_map.repaint();
                            }
                            @Override
                            public void keyReleased(KeyEvent e) {
                                int keyCode = e.getKeyCode();
                                rabbit.initIndex();
                                if (keyCode == KeyEvent.VK_LEFT) {
                                    send(KeyMsg.KEY_LEFT_RELEASED);
                                    rabbit.left_released();
                                    rabbit.idle();
                                } else if (keyCode == KeyEvent.VK_RIGHT) {
                                    send(KeyMsg.KEY_RIGHT_RELEASED);
                                    rabbit.right_released();
                                    rabbit.idle();
                                } 
                                m_map.repaint();
                            }
                        });
                        requestFocusInWindow();
                    }
                });
            }
        });

        // 사운드 버튼 클릭 시
        b_sound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("사운드 설정을 변경합니다!");
            }
        });
    }
    
    // --- 서버와 연결하는 핵심 함수 ---
    private void connectToServer(Moveable character) throws UnknownHostException, IOException {
        try {
            socket = new Socket(); // 전화기 장만
            SocketAddress sa = new InetSocketAddress(serverAddress, serverPort); // 주소록에 적기
            socket.connect(sa, 3000); // 3초 안에 연결 시도!
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream())); // 말하기(전송) 도구 준비

            System.out.println("Sending UID: " + uid); 
            out.writeUTF(uid); // 서버에 "저는 곰(또는 토끼)입니다!" 하고 신분증 제출
            out.flush();
            
            // 서버에서 오는 말을 듣는 귀 (스레드) 생성
            // 여기서 'character'는 상대방 캐릭터를 의미해.
            receiveThread = new Thread(new Runnable() {
                private DataInputStream in;
                String inMsg;

                private void receiveMessage() {
                    try {
                        inMsg = in.readUTF(); // 서버가 보낸 메시지 읽기
                        inMsg = inMsg.trim().toUpperCase(); // 대문자로 변환
                        KeyMsg key = KeyMsg.valueOf(inMsg); // 무슨 명령인지 해석

                        // 서버가 시키는 대로 '상대방 캐릭터'를 움직임
                        switch (key) {
                            case KEY_LEFT:
                                character.left(); // 상대방이 왼쪽으로 갔대!
                                break;
                            case KEY_RIGHT:
                                character.right(); // 상대방이 오른쪽으로 갔대!
                                break;
                            case KEY_SPACE:
                                character.up(); // 상대방이 점프했대!
                                break;
                            case KEY_A:
                                character.dead(); // 상대방이 죽었대!
                                break;
                            case KEY_LEFT_RELEASED:
                                character.left_released(); // 상대방이 멈췄대!
                                character.idle();
                                character.initIndex();
                                break;
                            case KEY_RIGHT_RELEASED:
                                character.right_released();
                                character.idle();
                                character.initIndex();
                                break;
                            default:
                                System.err.println("Unhandled KeyMsg: " + key);
                                break;
                        }
                        m_map.repaint(); // 화면 다시 그리기
                    } catch (IOException e) {
                        System.err.println("수신 오류> " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.err.println("잘못된 메시지: " + inMsg);
                    }
                }

                @Override
                public void run() {
                    try {
                        in = new DataInputStream(new BufferedInputStream(socket.getInputStream())); // 듣기 도구 준비
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 연결되어 있는 동안 계속 메시지 듣기
                    while (receiveThread == Thread.currentThread()) {
                        receiveMessage();
                    }
                }
            });
            receiveThread.start(); // 듣기 시작!
        } catch (Exception e) {
            printDisplay("서버 연결 오류");
            e.printStackTrace();
        }
    }

    // 연결 끊기 (프로그램 종료 시 등)
    private void disconnect() {
        try {
            receiveThread = null; // 듣는 귀 닫기
            socket.close(); // 전화 끊기
        } catch (IOException e) {
            System.exit(-1);
        }
    }
    
    // 서버로 메시지 보내기 (내가 키보드 누를 때 사용)
    private void send(KeyMsg msg) {
        if (msg == null) return;
        try {
            out.writeUTF(msg.name()); // 메시지 전송!
            out.flush();
        } catch (IOException e) {
            System.err.println("전송 오류> " + e.getMessage());
        }
    }
    
    // 메인 함수: 프로그램의 시작점
    public static void main(String[] args) {
        String serverAddress = "localhost"; // 내 컴퓨터 주소
        int serverPort = 54321; // 약속된 문 번호
        new ClientGUI(serverAddress, serverPort); // 게임 창 생성!
    }
}