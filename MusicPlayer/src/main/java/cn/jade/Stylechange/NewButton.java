package cn.jade.Stylechange;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;


public class NewButton extends JButton {
/**
 * this function is use to paint a button in the style of circle,p1 is the word,p2 is the color
 * */
    public NewButton(String label){
        super(label);
//        label.setBorder(BorderFactory.createLineBorder(Color.red));
        // 这些声明把按钮扩展为一个圆而不是一个椭圆。
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width,
                size.height);
        setPreferredSize(size);
        //这个调用使JButton不画背景，而允许我们画一个圆的背景。
        setContentAreaFilled(false);
        this.setBackground(Color.GRAY);
    }
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            // 你可以选一个高亮的颜色作为圆形按钮类的属性
            g.setColor(new Color(211,48,48));
        }
        else {
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getSize().width - 1,
                getSize().height - 1);
        //这个调用会画一个标签和焦点矩形。
        super.paintComponent(g);
    }

    // 用简单的弧画按钮的边界。
    protected void paintBorder(Graphics g) {
//        g.setColor(getForeground());
        g.setColor(new Color(232,60,60));
        g.drawOval(0, 0,
                getSize().width - 1,
                getSize().height - 1);
    }
    // 侦测点击事件
    Shape shape;
    public boolean contains(int x, int y) {
        // 如果按钮改变大小，产生一个新的形状对象。
        if (shape == null ||
                !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(0, 0,
                    getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }
    public static void main(String[] args){
        JButton button = new NewButton("Jackpot");
        ImageIcon ic = new ImageIcon("E://clientForMssql//Icons//item_group.gif");
        JButton button2 = new JButton(ic);
        button.setBackground(Color.GRAY);
        // 产生一个框架以显示这个按钮。
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().setBackground(Color.GRAY);
//        frame.add(button);
//        frame.getContentPane().add(button2);
//        frame.getContentPane().setLayout(new FlowLayout());
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(30,30));
        panel.add(button);
        frame.add(panel);
        frame.setSize(700, 200);
        frame.setVisible(true);
    }
}
