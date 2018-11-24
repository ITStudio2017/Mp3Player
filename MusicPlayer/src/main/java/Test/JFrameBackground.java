package Test;



import javax.swing.*;
import java.awt.*;

//为窗口添加背景图片
public class JFrameBackground {

    private JFrame frame = new JFrame("带背景图片的JFrame");
    private JPanel imagePanel;
    private ImageIcon backgroundimg;

    public JFrameBackground() {

        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
//                URL location = this.getClass().getResource("grapes.gif");
//                URL location = "E:/图片素材/刘看山.png";
                backgroundimg = new ImageIcon("E:/图片素材/刘看山.png");
                Image img = backgroundimg.getImage();
                g.drawImage(img, 0, 0, backgroundimg.getIconWidth(),
                        backgroundimg.getIconHeight(),
                        backgroundimg.getImageObserver());
                frame.setSize(backgroundimg.getIconWidth(),
                        backgroundimg.getIconHeight());
            }

        };

        frame.add(imagePanel);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new JFrameBackground();
    }
}
