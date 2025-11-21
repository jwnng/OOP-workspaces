import javax.swing.JPanel;
import javax.swing.JFrame;

public class OptionPane {
    private JPanel pane = new JPanel();
    public void setMainFrame(JFrame f) {}
    public JPanel getPane() { 
        pane.setOpaque(false); // 투명하게
        return pane; 
    }
}