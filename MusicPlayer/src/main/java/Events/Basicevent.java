package Events;

import Tools.LrcAnalyze;
import Ui.PlayerUi;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class Basicevent extends PlayerUi {

    private static PlayerUi playerUi = new PlayerUi();
    public static void main(String[] args){
        Basicevent playerui = new Basicevent(playerUi);
        frame = new JFrame("一个美丽的音乐播放器");
        frame.setSize(1200,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);





//        placeComponents(panel);

        playerui.initui(frame);
        frame.setVisible(true);
        System.out.println("xxx");
    }
    Basicevent(PlayerUi playerUi){
        this.playerUi = playerUi;
    }
    @Override
    public void playerFooter() {
        super.playerFooter();
        //点击按钮，切换播放方式
        playstyle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                clickstyle++;

                if(clickstyle == 4){
                    clickstyle =1;
                }
//                System.out.println(clickstyle);
//                playstyle.setVisible(false);
                switch (clickstyle){
                    case 1:
                        playstyleicon = new ImageIcon(System.getProperty("user.dir") + "/UIphotos/xunhuan.png");
                        playstyle.setToolTipText("列表循环");
                        playstyle.setIcon(playstyleicon);
                        break;
                    case 2:
                        playstyleicon = new ImageIcon(System.getProperty("user.dir") + "/UIphotos/danqu.png");
                        playstyle.setToolTipText("单曲循环");
                        playstyle.setIcon(playstyleicon);
                        break;
                    case 3:
                        playstyleicon = new ImageIcon(System.getProperty("user.dir") + "/UIphotos/suiji.png");
                        playstyle.setToolTipText("随机播放");
                        playstyle.setIcon(playstyleicon);
                        break;

                }


//



            }
        });
    }

    @Override
    public void playerMymenu() {
        super.playerMymenu();
        //输入新建歌单的名字
        mymenuadd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // 显示输入对话框, 返回输入的内容

                String inputContent = JOptionPane.showInputDialog(frame, "输入歌单名称:", "歌单名称");
                System.out.println("输入的内容: " + inputContent);
            }


        });

        //鼠标右键出现对歌单的删除和编辑
        JMenuItem edit = new JMenuItem("编辑");
        JMenuItem del = new JMenuItem("删除");
        final JPopupMenu pop = new JPopupMenu();
        pop.add(edit);
        pop.add(del);
        mylist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                mylist.setSelectionForeground(new Color(11,11,11));

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                showpop(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                mylist.setSelectedIndex(mylist.locationToIndex(e.getPoint()));  //获取鼠标点击的项
                showpop(e);

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                mylist.setSelectionBackground(new Color(230,231,234));
                showpop(e);
            }
            //弹出菜单
            private void showpop(MouseEvent e){
                if(e.isPopupTrigger()&&mylist.getSelectedIndex()!=-1){
                    //获取选择项的值
                    Object selected = mylist.getModel().getElementAt(mylist.getSelectedIndex());
                    System.out.println(selected);
                    pop.show(e.getComponent(),e.getX(),e.getY());
                }
            }
        });
        //接下来为删除和编辑做对应的操作，添加对应的事件
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                layout.show(panelmainer, "editlist");

            }
        });
        del.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "确认删除？",
                        "提示",
                        JOptionPane.YES_NO_CANCEL_OPTION

                );
                System.out.println("选择结果:"+result);
            }
        });



    }


    @Override
    public void playerOthermenu() {
        super.playerOthermenu();
        othermenuadd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // 显示输入对话框, 返回输入的内容
                //输入新建歌单的名字
                String inputContent = JOptionPane.showInputDialog(frame, "输入歌单名称:", "歌单名称");
                System.out.println("输入的内容: " + inputContent);
            }


        });

    }

    @Override
    public void playercontaindetail() {
        super.playercontaindetail();
        LrcAnalyze l = new LrcAnalyze(System.getProperty("user.dir") + "/downloads/test.lrc");
        List<LrcAnalyze.LrcData> list = l.LrcGetList();
        String s ="";
        for(LrcAnalyze.LrcData o:list){
            System.out.println(o.Time);
            musicwords.append(o.LrcLine+"\n");

        }
//        musicwords.setText(s);
    }


    @Override
    public void playerlistedit() {
        super.playerlistedit();
        editcover.addActionListener(new ActionListener(){
//            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();             //设置选择器
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg, *.png)", "jpg", "png"));
                chooser.showDialog(new JLabel(), "选择");
                int result = chooser.showOpenDialog(editlist);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // 如果点击了"确定", 则获取选择的文件路径
                    File file = chooser.getSelectedFile();

//                    msgTextArea.append("打开文件: " + file.getAbsolutePath() + "\n\n"); }

//                File file = chooser.getSelectedFile();
                    if (file.isDirectory()) {
                        System.out.println("文件夹:" + file.getAbsolutePath());
                    } else if (file.isFile()) {
                        System.out.println("文件:" + file.getAbsolutePath());
                    }
                    System.out.println(chooser.getSelectedFile().getName());

                    //接下来修改选中封面的默认照片
                    //但是首先要替换一下路径！！！
                    String url = file.getAbsolutePath();
//                    String url = "E:\\mp3player3\\Mp3Player\\MusicPlayer\\UIphotos\\indexcover.png";
//                    String url2 = url.replace("\\","/");
//                    System.out.println(url2);
                    indexcover.setIcon(new ImageIcon(url));

                }


            }
    });
    }
}
