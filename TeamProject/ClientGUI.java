import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientGUI extends JFrame {
    private Dog dog;
    private Girl girl;
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
        setFocusable(true);//키 입력을 받기 위해 창 자체가 포커스를 받을 수 있게 설정
    }

    private void createDisplayPanel() {
        // 배경 설정
        ImageIcon bgIcon = new ImageIcon("Images/Background/b_start.png"); // 파일명 확인
        backgroundLabel = new JLabel(bgIcon);
        backgroundLabel.setBounds(0, 0, 715, 738);
        backgroundLabel.setLayout(null);
        add(backgroundLabel);

        // 버튼 생성
        JButton b_dog = new JButton("개(P1) 하기");
        b_dog.setBounds(100, 300, 200, 50);
        
        JButton b_girl = new JButton("소녀(P2) 하기");
        b_girl.setBounds(400, 300, 200, 50);

        backgroundLabel.add(b_dog);
        backgroundLabel.add(b_girl);

        b_dog.addActionListener(e -> startGame("dog"));
        b_girl.addActionListener(e -> startGame("girl"));
    }

    private void startGame(String selectedUid) {
        this.uid = selectedUid;
        remove(backgroundLabel); // 시작 화면 제거

        oPane = new OptionPane(); // 더미 옵션창
        
        mainMap = new MainMap();
        
        m_map = mainMap; 

        // 2. 캐릭터 생성 (파라미터 주의!)
        dog = new Dog(mainMap, oPane);
        girl = new Girl(mainMap, oPane);

        // 나머지는 그대로...
        m_map.add(dog.getCharacter());
        m_map.add(girl.getCharacter());
        add(m_map);
        
        //화면 갱신
        revalidate();
        repaint();
        
        try {
            connectToServer(uid.equals("dog") ? girl : dog);
        } catch (Exception e) { e.printStackTrace(); }

        setupKeyListener();
        requestFocusInWindow();
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Player myChar = uid.equals("dog") ? dog : girl;
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
                Player myChar = uid.equals("dog") ? dog : girl;
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
