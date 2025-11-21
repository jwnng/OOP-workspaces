// 필요한 도구들을 서랍에서 꺼내요.
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

// 서버 GUI(화면)를 만드는 설계도예요.
public class ServerGUI extends JFrame {
    private int port; // 서버의 문패 번호(포트 번호)예요.
    private ServerSocket serverSocket = null; // 손님을 기다리는 '대문'이에요.
    private DefaultStyledDocument document; // 화면에 글씨를 쓸 종이에요.
    private JTextPane t_display; // 글씨가 보여지는 화면이에요.
    private JButton b_connect, b_disconnect, b_exit; // 시작, 종료, 나가기 버튼들이에요.
    
    private Thread acceptThread = null; // 손님을 계속 기다리는 '문지기'예요.
    
    private Vector<ClientHandler> users = new Vector<ClientHandler>(); // 접속한 친구들 목록이에요.
    private Map<String, ClientHandler> clientMap = new HashMap<>();  // 친구들의 이름(Rabbit, Bear)과 연락처를 적어둔 '주소록'이에요.
    
    // 1. 서버 창 만들기 (생성자)
    public ServerGUI(int port) {
        super("Server GUI"); // 창의 제목을 붙여요.
        
        this.port = port; // 문패 번호를 기억해요.
        users = new Vector<>(); // 목록을 만들 준비를 해요.
        
        buildGUI(); // 화면을 예쁘게 꾸며요.
        
        setSize(400, 300); // 창 크기 설정
        setLocation(500,300); // 창 위치 설정
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫으면 프로그램 종료
        setVisible(true); // 창을 보여줘요!
    }
    
    // 화면의 버튼과 글상자를 배치하는 함수예요.
    private void buildGUI() {
        add(createDisplayPanel(), BorderLayout.CENTER); // 가운데에 글상자 패널 배치
        add(createControlPanel(), BorderLayout.SOUTH); // 아래쪽에 버튼 패널 배치
    }
    
    // 글씨가 나오는 화면을 만드는 함수
    private JPanel createDisplayPanel() {
        document = new DefaultStyledDocument();
        t_display = new JTextPane(document);
        t_display.setEditable(false); // 서버 관리자만 볼 수 있게 수정은 못 하게 막아요.
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(t_display), BorderLayout.CENTER); // 내용이 많아지면 스크롤바가 생겨요.
        return panel;
    }
    
    // 버튼들을 만드는 함수
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 0)); // 버튼을 한 줄로 나란히 놓아요.
        
        // [서버 시작] 버튼
        b_connect = new JButton("서버 시작");
        b_connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 버튼을 누르면 문지기(Thread)를 고용해서 일을 시켜요.
                acceptThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer(); // "손님 받기 시작해!"
                    }
                });
                acceptThread.start(); // 문지기 출동!
                
                b_connect.setEnabled(false); // 시작 버튼은 비활성화 (또 누르면 안 되니까)
                b_disconnect.setEnabled(true); // 종료 버튼 활성화
                b_exit.setEnabled(false); 
            }
        });
        
        // [서버 종료] 버튼
        b_disconnect = new JButton("서버 종료");
        b_disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect(); // 연결을 다 끊어요.
                b_connect.setEnabled(true);
                b_disconnect.setEnabled(false);
                b_exit.setEnabled(true);
            }
        });
        
        // [종료] 버튼
        b_exit = new JButton("종료");
        b_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 프로그램 완전 종료
            }
        });
        
        panel.add(b_exit); // 버튼 순서대로 붙이기
        panel.add(b_connect);
        panel.add(b_disconnect);
        panel.add(b_exit);
        
        // 처음 버튼 상태 설정
        b_connect.setEnabled(true);
        b_disconnect.setEnabled(false);
        b_exit.setEnabled(true);
        
        return panel;
    }
    
    // 내 컴퓨터의 IP 주소를 알아내는 함수 (주소 확인용)
    private String getLocalAddr() {
        String address = "";
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            address = localAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }
    
    // 2. 진짜 서버를 시작하는 곳 (핵심!)
    private void startServer() {
        Socket clientSocket = null;
        try { 
            serverSocket = new ServerSocket(port); // 대문을 열었어요!
            printDisplay("서버가 시작되었습니다: " + getLocalAddr()); // 화면에 알림 출력
            
            // 문지기가 계속 서 있으면서 손님이 올 때마다 환영해줘요.
            while (acceptThread == Thread.currentThread()) {
                clientSocket = serverSocket.accept(); // "똑똑! 누구 없나요?" 하고 손님이 오면 문을 열어줘요.
                
                String cAddr = clientSocket.getInetAddress().getHostAddress();
                printDisplay("클라이언트가 연결되었습니다: " + cAddr + "\n");
                
                // 손님 한 명당 전담 매니저(ClientHandler)를 한 명씩 붙여줘요.
                ClientHandler cHandler = new ClientHandler(clientSocket);
                users.add(cHandler); // 손님 목록에 추가
                cHandler.start(); // 매니저 일 시작!
            }
        } catch (SocketException e) {
            printDisplay("서버 소켓 종료" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 마무리 청소
            try {
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 서버 끄는 함수
    private void disconnect() {
        try {
            acceptThread = null; // 문지기 퇴근
            serverSocket.close(); // 대문 잠그기
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    // 화면에 글씨를 띄워주는 함수 (스레드 안전하게)
    private void printDisplay(String msg) {
        int len = t_display.getDocument().getLength();
        try {
            document.insertString(len, msg + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        t_display.setCaretPosition(len); // 스크롤을 항상 맨 아래로
    }
    
    // 3. 손님 전담 매니저 (내부 클래스)
    // 이 매니저는 손님 1명과 1:1로 대화하며 메시지를 전달해줘요.
    private class ClientHandler extends Thread {
        private Socket clientSocket; // 연결된 손님의 전화기
        private DataOutputStream