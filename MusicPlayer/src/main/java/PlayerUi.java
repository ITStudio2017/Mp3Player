import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class PlayerUi {
    private JPanel paneltop = new JPanel();
    private JPanel panelmainer = new JPanel();
    private JPanel panelfooter = new JPanel();
    private JPanel menu = new JPanel();
    private JPanel panelleft = new JPanel();
    private JScrollPane scrollmenu = null;
//            new JScrollPane( textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

    private JPanel othermenu = new JPanel();    //别人的清单
    private JPanel mymenu = new JPanel();       //我的歌单
    private JPanel contain = new JPanel();      //歌曲内容
    private JPanel containtop = new JPanel();
    private JPanel containbottom = new JPanel(); //控制区
    private JLabel mymenutitle = new JLabel("我的歌单");
    final JList mylist = new JList();   //我的歌单
    private JLabel othermenutitle = new JLabel("别人都在听");
    final JList otherlist = new JList(); //别人歌单
    public static void main(String[] args){
        PlayerUi playerui = new PlayerUi();
        JFrame frame = new JFrame("一个美丽的音乐播放器");
        frame.setSize(1200,800);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new BorderLayout(0,0));
        panel.setBackground(Color.BLUE);

        frame.add(panel);
        //以上构建了最大的container，一下添加内部组件或面板



//        placeComponents(panel);
        playerui.layout(panel,frame);
        playerui.playerMymenu(panel);
        playerui.playerOthermenu();
        playerui.playerContain();
        frame.setVisible(true);
        System.out.println("xxx");
    }
    //页面基本布局
    private void layout(JPanel panel,JFrame frame){

//        panel.add(menu);
        //基础borderlayout划分
        paneltop.setPreferredSize(new Dimension(1200,65));
        paneltop.setBackground(new Color(198,47,47));
        panelmainer.setSize(1200,600);
        panelmainer.setBackground(Color.green);
        panelleft.setPreferredSize(new Dimension(260,800));
        panelfooter.setPreferredSize(new Dimension(1200,65));
        panelfooter.setBackground(new Color(198,47,47));
        panel.add(panelfooter,BorderLayout.SOUTH);
        panel.add(panelleft,BorderLayout.WEST);
        panel.add(paneltop,BorderLayout.NORTH);
        panel.add(panelmainer,BorderLayout.CENTER);
//        menu.setSize(150,700);

        //左边区域的滚动条
        panelleft.setLayout(new GridLayout(1,1));
        scrollmenu = new JScrollPane( menu, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        panelleft.add(scrollmenu);

        //右边区域滚动条
        panelmainer.setLayout(new GridLayout(1,1));
        scrollmenu = new JScrollPane( contain, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        panelmainer.add(scrollmenu);

        //menu上下分隔
//        menu.setBackground(Color.red);
        menu.setLayout(new GridLayout(1,1));


        //设置左右分隔线
//        menu.setLayout(new BorderLayout(0,0));
//        contain.setLayout(new BorderLayout(0,0));
//        JSplitPane jsplitpane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,menu,contain);
//        jsplitpane1.setDividerLocation(300);
//        jsplitpane1.setDividerSize(1);
//        jsplitpane1.setEnabled(false);
//        frame.add(jSplitpane);

//        panelmainer.add(jsplitpane1);
        //设置上下分割线
        JSplitPane jsplitpane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,othermenu,mymenu);
//        jsplitpane2.setDividerLocation(400);
        jsplitpane2.setDividerSize(1);
        jsplitpane2.setEnabled(false);
        menu.add(jsplitpane2);




    }
    //具体实现清单内部
    private void playerMymenu(JPanel panel){
//        mymenu.setBackground(Color.white);
        Vector<String> v = new Vector<String>();
        v.add(new String("杂七杂八的民谣"));
        v.add(new String("疯狂的摇滚情结"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语cccccccccccc力sssssssssssssssssssssssssssssssssssssssssssssssssssssss练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));
        v.add(new String("英语听力练练练"));

        Box box1 = Box.createHorizontalBox();
        box1.add(mymenutitle);
        box1.add(Box.createHorizontalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(mylist);
        box2.add(Box.createHorizontalGlue());
        Box vbox = Box.createVerticalBox();
        vbox.add(box1);
        vbox.add(box2);
        mymenu.add(vbox);


        mylist.setFixedCellWidth(400);
        mymenutitle.setFont(new Font("宋体",Font.BOLD,20));
        mymenu.setBackground(new Color(245,245,247));
        mylist.setListData(v);
        mylist.setLayout(new GridLayout(1,1));
        mymenu.setLayout(new GridLayout(1,1));
        mylist.setBackground(new Color(245,245,247));





//        JButton button = new JButton("歌单管理");
//        button.setLocation(180,300);
//        button.setSize(100,30);
//        mymenu.add(button);
//        Box box3 = Box.createHorizontalBox();
//        box3.add(button);
//        Box vBox = Box.createVerticalBox();
//        vBox.add(box2);
//        vBox.add(box3);

//        othermenu.add(vBox);


        //        list.




    }
    private void playerOthermenu(){
//        othermenu.setBackground(Color.white);
        Vector<String> v = new Vector<String>();
        v.add("学号1702003100x的歌单");
        v.add("学号1702003100x的歌单");
        v.add("学号1702003100x的歌单");
        v.add("学号1702003100x的歌单");
        otherlist.setListData(v);

        Box box1 = Box.createHorizontalBox();
        box1.add(othermenutitle);
        box1.add(Box.createHorizontalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(otherlist);
        box2.add(Box.createHorizontalGlue());
        Box vbox = Box.createVerticalBox();
        vbox.add(box1);
        vbox.add(box2);

        othermenu.add(vbox);


        othermenutitle.setFont(new Font("宋体",Font.BOLD,20));
        otherlist.setBackground(new Color(245,245,247));


        othermenu.setLayout(new FlowLayout(FlowLayout.LEFT));


    }
    private void playerContain(){
        Box box1 = Box.createHorizontalBox();
        box1.add(containtop);
        box1.add(Box.createHorizontalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(containbottom);
        box2.add(Box.createHorizontalGlue());
        Box vbox = Box.createVerticalBox();
        vbox.add(box1);
        vbox.add(box2);
        contain.add(vbox);



        contain.setBackground(new Color(249,246,247));
        contain.setLayout(new GridLayout(1,1));


        containtop.setSize(700,600);
        containtop.setBackground(Color.red);
//        contain.add(containtop);
        containbottom.setSize(700,200);
        containbottom.setBackground(Color.green);
//        contain.add(containbottom);


        //
    }






}
