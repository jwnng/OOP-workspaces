import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientGUI extends JFrame {
    private Bear bear;
    private Rabbit rabbit;
    private MainMap mainMap;
    private JPanel m_map;
    private OptionPane oPane;
    
    private JLabel backgroundLabel;
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private DataOutputStream out;
    private Thread receiveThread = null;
    private String uid;

    public ClientGUI(String serverAddress, int serverPort) {
        super("Client GUI");
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        setLayout(null);
        createDisplayPanel();
        setSize(715, 738);
        setLocation(500,150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createDisplayPanel() {
        // 배경 설정
        ImageIcon bgIcon = new ImageIcon("Images/Background/b_start.png"); // 파일명 확인
        backgroundLabel = new JLabel(bgIcon);
        backgroundLabel.setBounds(0, 0, 715, 738);
        backgroundLabel.setLayout(null);
        add(backgroundLabel);

        // 버튼 생성
        JButton b_bear = new JButton("곰(P1) 하기");
        b_bear.setBounds(100, 300, 200, 50);
        
        JButton b_rabbit = new JButton("토끼(P2) 하기");
        b_rabbit.setBounds(400, 300, 200, 50);

        backgroundLabel.add(b_bear);
        backgroundLabel.add(b_rabbit);

        b_bear.addActionListener(e -> startGame("Bear"));
        b_rabbit.addActionListener(e -> startGame("Rabbit"));
    }

    private void startGame(String selectedUid) {
        this.uid = selectedUid;
        remove(backgroundLabel); // 시작 화면 제거

        oPane = new OptionPane(); // 더미 옵션창
        
        mainMap = new MainMap();
        
        m_map = mainMap; 

        // 2. 캐릭터 생성 (파라미터 주의!)
        bear = new Bear(mainMap, oPane);
        rabbit = new Rabbit(mainMap, oPane);

        // 나머지는 그대로...
        m_map.add(bear.getCharacter());
        m_map.add(rabbit.getCharacter());
        add(m_map);

        try {
            // 내가 곰이면 토끼를 조종할 수 있는 권한(상대방 정보)을 연결, 반대면 곰을 연결
            connectToServer(uid.equals("Bear") ? rabbit : bear);
        } catch (Exception e) { e.printStackTrace(); }

        setupKeyListener();
        requestFocusInWindow();
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Player myChar = uid.equals("Bear") ? bear : rabbit;
                int key = e.getKeyCode();
                
                switch (key) {
                    case KeyEvent.VK_LEFT: 
                        send(KeyMsg.KEY_LEFT); myChar.left(); break;
                    case KeyEvent.VK_RIGHT: 
                        send(KeyMsg.KEY_RIGHT); myChar.right(); break;
                    case KeyEvent.VK_SPACE: 
                        send(KeyMsg.KEY_SPACE); myChar.up(); break;
                }
                m_map.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Player myChar = uid.equals("Bear") ? bear : rabbit;
                int key = e.getKeyCode();
                
                if (key == KeyEvent.VK_LEFT) {
                    send(KeyMsg.KEY_LEFT_RELEASED); myChar.left_released();
                } else if (key == KeyEvent.VK_RIGHT) {
                    send(KeyMsg.KEY_RIGHT_RELEASED); myChar.right_released();
                }
                m_map.repaint();
            }
        });
    }

    private void connectToServer(Moveable otherChar) throws Exception {
        socket = new Socket(serverAddress, serverPort);
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.writeUTF(uid);
        out.flush();

        receiveThread = new Thread(() -> {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                while (true) {
                    String msg = in.readUTF();
                    // 내가 보낸 메시지는 무시 (에코 방지) 할 수도 있지만 일단 다 받음
                    // 여기서 중요한 건 '상대방 캐릭터(otherChar)'를 움직이는 것
                    try {
                        KeyMsg key = KeyMsg.valueOf(msg);
                        switch (key) {
                            case KEY_LEFT: otherChar.left(); break;
                            case KEY_RIGHT: otherChar.right(); break;
                            case KEY_SPACE: otherChar.up(); break;
                            case KEY_LEFT_RELEASED: otherChar.left_released(); break;
                            case KEY_RIGHT_RELEASED: otherChar.right_released(); break;
                        }
                        m_map.repaint();
                    } catch (Exception ex) { }
                }
            } catch (IOException e) { e.printStackTrace(); }
        });
        receiveThread.start();
    }

    private void send(KeyMsg msg) {
        try { out.writeUTF(msg.name()); out.flush(); } catch (Exception e) {}
    }

    public static void main(String[] args) {
        new ClientGUI("localhost", 54321);
    }
}