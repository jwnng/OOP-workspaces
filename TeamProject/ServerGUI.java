import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;

public class ServerGUI extends JFrame {
    private int port;
    private ServerSocket serverSocket = null;
    private DefaultStyledDocument document;
    private JTextPane t_display;
    private Thread acceptThread = null;
    private Vector<ClientHandler> users = new Vector<ClientHandler>();

    public ServerGUI(int port) {
        super("Server GUI");
        this.port = port;
        buildGUI();
        setSize(400, 300);
        setLocation(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildGUI() {
        add(createDisplayPanel(), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
    }

    private JPanel createDisplayPanel() {
        document = new DefaultStyledDocument();
        t_display = new JTextPane(document);
        t_display.setEditable(false);
        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(t_display)); }};
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 0));
        JButton b_connect = new JButton("서버 시작");
        
        b_connect.addActionListener(e -> {
            acceptThread = new Thread(() -> startServer());
            acceptThread.start();
            b_connect.setEnabled(false);
        });
        
        panel.add(b_connect);
        return panel;
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            printDisplay("서버 시작됨: Port " + port + "\n");
            while (acceptThread == Thread.currentThread()) {
                Socket client = serverSocket.accept();
                printDisplay("클라이언트 접속: " + client.getInetAddress() + "\n");
                ClientHandler ch = new ClientHandler(client);
                users.add(ch);
                ch.start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void printDisplay(String msg) {
        try { document.insertString(document.getLength(), msg, null); } 
        catch (Exception e) {}
    }

    class ClientHandler extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        public ClientHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try {
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                
                // 1. UID(이름) 받기 (Bear 또는 Rabbit)
                String uid = in.readUTF(); 
                printDisplay("접속 ID: " + uid + "\n");

                while (true) {
                    String msg = in.readUTF(); // 메시지 수신
                    broadcast(msg); // 모두에게 전송
                }
            } catch (IOException e) {
                users.remove(this);
                try { socket.close(); } catch(Exception ex) {}
            }
        }
        
        public void send(String msg) {
            try { out.writeUTF(msg); out.flush(); } catch (Exception e) {}
        }
    }

    public void broadcast(String msg) {
        for (ClientHandler user : users) user.send(msg);
    }

    public static void main(String[] args) {
        new ServerGUI(54321);
    }
}