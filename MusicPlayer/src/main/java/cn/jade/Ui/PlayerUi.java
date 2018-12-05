package cn.jade.Ui;


import cn.jade.Stylechange.JPanelBackground;
import cn.jade.Stylechange.NewButton;
import cn.jade.Tools.GaussianBlur;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class PlayerUi {
    public static JFrame frame = null;
    public JPanel panel = new JPanel(new BorderLayout());
    public JPanel paneltop = new JPanel();
    public JPanel panelmainer = new JPanel();
    public JPanel panelfooter = new JPanel();
    public JPanel menu = new JPanel();
    public JPanel panelleft = new JPanel();
    public JScrollPane scrollmenu = null;
    public DefaultTableModel defaultdetailtable = null;

    public JPanel panellefttop = new JPanel();
    public JPanel panelleftmiddle = new JPanel();
    public JPanel panelleftbottom = new JPanel(); //左边栏分为上下两块
    public JPanel othermenu = new JPanel();    //别人的清单
    public JPanel mymenu = new JPanel();       //我的歌单
    public JPanel playnow = new JPanel(); //展示现在播放状态的区域
    public JPanel playintroduce = new JPanel(); //展示推荐的区域
    public JPanel contain = new JPanel();      //歌曲内容
    public JPanel containwrap = new JPanel();  //歌单内容页，与此相对应的是详细内容
    public JPanel containwrap2 = new JPanel();  //歌词内容页
    public JPanel editlist = new JPanel(); //编辑歌单相关信息
    public JPanel containtop = new JPanel();
    public JPanel containbottom = new JPanel(); //控制区
    public JLabel mymenutitle = new JLabel("我的歌单");
    public JList mylist = new JList();   //我的歌单
    public JLabel othermenutitle = new JLabel("别人都在听");
    public JList otherlist = new JList(); //别人歌单
    public Vector<String> otherv = new Vector<String>(); //别人的歌单
    public Vector<String> myv = new Vector<String>(); //别my人的歌单
    public JPanel musicbuttons = new JPanel(); //所有的播放相关按键
    public JPanel pplaybutton = new JPanel();  //包裹播放按钮
//    private JPanel pstopbutton = new JPanel();  //包裹暂停按钮
    public JPanel plastbutton = new JPanel(); //包裹上一首按钮
    public JPanel pnextbutton = new JPanel(); //包裹下一首按钮
    public JPanel pstylebutton = new JPanel(); //包裹播放方式按钮
    public JButton playbutton = new NewButton("播放") ;//播放暂停按钮
    public JButton lastbutton = new NewButton("上"); //上一首按钮
    public JButton nextbutton = new NewButton("下"); //下一首按钮
    public JButton stylebutton = new JButton(); //播放方式切换按钮
    public JPanel listcoverwrap = new JPanel(); //
    public ImageIcon iconlist = new ImageIcon("E:/图片素材/刘看山.png");
    public JLabel listcover = new JLabel(iconlist); //歌单封面！！！！！


    public JPanel containtopright = new JPanel();
    public JLabel covertitle = new JLabel("每日歌曲更新");

    public ImageIcon iconnow = new ImageIcon("E:/图片素材/刘看山.png");
    public JLabel playnowcover = new JLabel(iconnow);


    public CardLayout layout = null; //这是为了歌单以及内容切换展示而设置的一种卡片布局
    public JButton changelist = new JButton("歌单"); //切换为展示清单
    public JButton changecontent = new JButton("歌词"); //切换为展示内容
    public int currentProgress = 0; //当前音乐播放进度
    public int maxmusicprogress = 100; //准确来说放的是歌曲的长度
    public int minmusicprogress = 0;
    public JSlider musicslider = null; //播放进度控制
    public JLabel progressnow = new JLabel("00:00");   //现在播放到的位置
    public JLabel progresstotal = new JLabel("00:00"); //一共的播放长度
    public JPanel musicsliders = new JPanel();  //包裹进度块
//    JPanel test = new JPanel(); //包裹切换方式
    public JLabel playstyle = null;  //包裹切换方式
    public int clickstyle = 1; //记录点击播放方式的次数，对应播放方式
    public ImageIcon playstyleicon = null; //放置播放方式
    public String playstylestring;  //记录播放方式，以字符串的形式
    public JLabel mymenuadd = null; //我的歌单的add按钮
    public JLabel othermenuadd = null; //别人听的add按钮
//    E:\图片素材\刘看山.png
//    System.getProperty("user.dir") + "/UIphotos/indexcover.png")
    //编辑菜单区域
    public JTextField editlistname = new JTextField();
    public JLabel editlistlabel = new JLabel("歌单名：");
    public ImageIcon coverimage = new ImageIcon("E:/图片素材/刘看山.png");
    public JLabel indexcover = new JLabel(coverimage);
    public JButton editsave = new JButton("保存");
    public JButton editcancel = new JButton("取消");
    public JButton editcover = new JButton("编辑封面");

    //歌单详情页
    public ImageIcon icondetail = new ImageIcon("E:/图片素材/刘看山.png"); //歌单详情页放
    public JLabel detailcover = new JLabel();  //歌词详情页的歌单封面！！
    public JTextArea musicwords =  new JTextArea(); //歌词！！！！

    public JTextField searchinput = new JTextField();  //搜索框
    public JButton searchbutton = new JButton("搜索");  //搜索按钮

    public JPanelBackground jPanelBackground = new JPanelBackground();  //歌词页！！！！的背景
    public GaussianBlur gaosi = new GaussianBlur();

//    public Object[][] rowData = null;

//    public Vector<Object> rowData = new Vector<Object>();
    public String[] columnNames = {"序号", "音乐标题", "歌手"};   //那个音乐列表的表格！！！
    public JLabel downloadbutton =  new JLabel(new ImageIcon(System.getProperty("user.dir")+"/UIphotos/downloadmusic.png"));
    public JButton nextpage = new JButton("下一页");  //点击之后跳转到下一页！
    public JButton lastpage = new JButton("上一页"); //
    public JPanel pagewrap = new JPanel();

    public JComboBox searchsource = new JComboBox();   //设置搜索来源！！


    public void initui(JFrame frame){
        layout();
        playerNav();
        playerMymenu();
        playerOthermenu();
        playerContain();
        playerFooter();
        playerNow();
        playercontaindetail();
        playerlistedit();
    }
    //切换内容页的方法


    //页面基本布局
    public void layout(){
        panel.setLayout(new BorderLayout(0,0));
        panel.setBackground(Color.BLUE);

        frame.add(panel);
        //以上构建了最大的container，一下添加内部组件或面板


//        panel.add(menu);
        //基础borderlayout划分
        paneltop.setPreferredSize(new Dimension(1200,65));
        paneltop.setBackground(new Color(198,47,47));
        panelmainer.setSize(1200,600);
//        panelmainer.setBackground(Color.green);
        panelleft.setPreferredSize(new Dimension(260,800));
        panelleft.setBackground(new Color(245,245,247));
        panelfooter.setPreferredSize(new Dimension(1200,65));
        panelfooter.setBackground(new Color(246,246,248));
        panel.add(panelfooter,BorderLayout.SOUTH);
        panel.add(panelleft,BorderLayout.WEST);
        panel.add(paneltop,BorderLayout.NORTH);
        panel.add(panelmainer,BorderLayout.CENTER);


        //panelleft再分为上下
//        panelleft.setLayout(null);
//
        panelleft.setLayout(new GridLayout(4,1));
;       panelleft.add(playintroduce);
        panelleft.add(panelleftmiddle);
        panelleft.add(panellefttop);
        panelleft.add(playnow);




        //左边区域的滚动条
        panellefttop.setLayout(new GridLayout(1,1));
        scrollmenu = new JScrollPane( mymenu, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        panellefttop.add(scrollmenu);
        panelleftmiddle.setLayout(new GridLayout(1,1));
        scrollmenu = new JScrollPane( othermenu, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        panelleftmiddle.add(scrollmenu);

        //右边列表页区域滚动条
//        panelmainer.add(containwrap);
//        panelmainer.setLayout(new GridLayout(1,1));
        containwrap.setLayout(new GridLayout(1,1));
        scrollmenu = new JScrollPane( contain, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        containwrap.add(scrollmenu);

        //右边区域一共有两层,一层是歌单，一层是具体内容页， 用卡片式布局实现
        layout = new CardLayout(0,0);
        panelmainer.setLayout(layout);

//        panelmainer.add(btn01, "btn01");
        panelmainer.add(containwrap, "list");
        panelmainer.add(containwrap2, "content"); // 先显示第二个
        panelmainer.add(editlist,"editlist");
//        layout.show(panelmainer, "editlist");
        layout.show(panelmainer, "content");

        //menu上下分隔
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


//        JSplitPane jsplitpane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,panellefttop,panelleftbottom);
////        jsplitpane2.setDividerLocation(400);
//        jsplitpane3.setDividerSize(1);
//        jsplitpane3.setEnabled(false);
//        panelleft.add(jsplitpane3);
//        panellefttop.setBackground(Color.red);
//        panelleftbottom.setBackground(Color.green);
//        panellefttop.setPreferredSize(new Dimension(260,500));
//        panelleftbottom.setPreferredSize(new Dimension(260,100));
//        panelleft.setLayout(new GridLayout(1,1));


//        JSplitPane jsplitpane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,othermenu,mymenu);
////        jsplitpane2.setDividerLocation(400);
//        jsplitpane2.setDividerSize(1);
//        jsplitpane2.setEnabled(false);
//        menu.add(jsplitpane2);








    }
    //实现上面的头
    public void playerNav(){

        Object[] sources = {"网易云音乐","QQ音乐","酷我音乐"};
        searchsource = new JComboBox(sources);
        searchinput.setPreferredSize(new Dimension(300,30));
//        searchbutton.setFocusable(false);
//        searchinput.setFocusable(false);
        paneltop.add(searchinput);
        paneltop.add(searchbutton);
        paneltop.add(searchsource);


    }
    //具体实现歌单内部
    public void playerMymenu(){
        mymenu.setBackground(new Color(246,246,248));






        Box box1 = Box.createHorizontalBox();
        mymenuadd = new JLabel(new ImageIcon(System.getProperty("user.dir") + "/UIphotos/listadd.png"));
        JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        temp.setOpaque(false);
        temp.add(mymenutitle);
        temp.add(mymenuadd);
        box1.add(temp);
        box1.add(Box.createHorizontalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(mylist);
        Box vbox = Box.createVerticalBox();
        vbox.add(box1);
        vbox.add(box2);
        mymenu.add(vbox);


        mylist.setFixedCellWidth(300);
//        mymenutitle.setFont(new Font("宋体",Font.BOLD,20));
        mymenutitle.setFont(new Font("微软雅黑",Font.BOLD,16));
        mymenu.setBackground(new Color(245,245,247));

//        mylist.setLayout(new GridLayout(1,1));
//        mymenu.setLayout(new GridLayout(1,1));
        mylist.setBackground(new Color(245,245,247));
        mymenutitle.setForeground(new Color(97,97,97));
        mylist.setFocusable(false);



    }
    public void playerOthermenu(){
//        othermenu.setBackground(Color.white);

        otherv.add("学号1702003100x的歌单");
        otherv.add("学号1702003100x的歌单");
        otherv.add("学号1702003100x的歌单");
        otherv.add("学号1702003100x的歌单");
        otherlist.setListData(otherv);

        Box box1 = Box.createHorizontalBox();
//        box1.add(othermenutitle);
        othermenuadd = new JLabel(new ImageIcon(System.getProperty("user.dir") + "/UIphotos/listadd.png"));
        JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        temp.setOpaque(false);
        temp.add(othermenutitle);
        temp.add(othermenuadd);
        box1.add(temp);
        box1.add(Box.createHorizontalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(otherlist);
//        box2.add(Box.createHorizontalGlue());
        Box vbox = Box.createVerticalBox();
        vbox.add(box1);
        vbox.add(box2);

        othermenu.add(vbox);


        othermenutitle.setFont(new Font("微软雅黑",Font.BOLD,16));
        otherlist.setBackground(new Color(245,245,247));
        othermenu.setBackground(new Color(245,245,247));
        othermenutitle.setForeground(new Color(97,97,97));
        otherlist.setFixedCellWidth(360);
        otherlist.setFocusable(false);

    }
    public void playerContain(){
        // 表头（列名）

        // 表格所有行数据


        Box box1 = Box.createHorizontalBox();
        box1.add(containtop);
//        box1.add(Box.createHorizontalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(containbottom);
//        box2.add(Box.createHorizontalGlue());
        Box vbox = Box.createVerticalBox();
        vbox.add(box1);
        vbox.add(box2);
        contain.add(vbox);


        //设置表格内容颜色

        //设置表头
//        detailtable.getTableHeader().setFocusTraversalKe;

        //设置行高
//        detailtable.setRowHeight(30);
//        containbottom.add(detailtable);






        contain.setBackground(new Color(249,246,247));
        contain.setLayout(new GridLayout(1,1));


        containtop.setSize(700,600);
        containtop.setBackground(new Color(248,244,245));
//        contain.add(containtop);
        containbottom.setSize(700,200);
        containbottom.setBackground(Color.green);
//        containbottom.setLayout(new GridLayout(1,1));
        containbottom.setLayout(new BorderLayout());
//        containtop.setLayout();

        lastpage.setPreferredSize(new Dimension(100,30));
        nextpage.setPreferredSize(new Dimension(100,30));




        //这是歌单具体内容的上部分

        listcover.setPreferredSize(new Dimension(140,140));
        //设置歌单封面大小
        iconlist.setImage(iconlist.getImage().getScaledInstance(140, 140,Image.SCALE_DEFAULT ));
//        listcoverwrap.setBackground(Color.PINK);


        containtop.setLayout(new FlowLayout(FlowLayout.LEFT));

        containtop.add(listcover);
        containtop.add(covertitle);
//        listcoverwrap.add(listcover);
        //
//        containwrap.add(contain);
    }
    public void playerFooter(){


        panelfooter.setBorder(BorderFactory.createLineBorder(new Color(225,225,226)));
        panelfooter.setBackground(new Color(246,246,248));
//        musicbuttons.setLayout(new GridLayout(1,3));
        //左边 按钮部分
//        musicbuttons.setLayout(new BorderLayout());
        musicbuttons.setLayout(null);
        musicbuttons.setLayout(new GridLayout(1,3));
        musicbuttons.setOpaque(false);
        panelfooter.setLayout(new BorderLayout());


        panelfooter.add(musicbuttons,BorderLayout.WEST);
        musicbuttons.add(plastbutton);
        musicbuttons.add(pplaybutton);
        musicbuttons.add(pnextbutton);


        plastbutton.setSize(42,42);
        plastbutton.add(lastbutton);
        plastbutton.setOpaque(false);
        lastbutton.setBackground(new Color(232,60,60));
        lastbutton.setFocusPainted(false);
        lastbutton.setBorder(BorderFactory.createLineBorder(new Color(232,60,60)));
//        panelfooter.add(plastbutton);

        pplaybutton.setSize(50,50);
        pplaybutton.setOpaque(false);
        pplaybutton.add(playbutton);
        playbutton.setBackground(new Color(232,60,60));
        playbutton.setFocusPainted(false);
//        panelfooter.add(playbutton);


        pnextbutton.setSize(50,50);
        pnextbutton.setOpaque(false);
        pnextbutton.add(nextbutton);
        nextbutton.setBackground(new Color(232,60,60));
        nextbutton.setFocusPainted(false);
//
//        JLabel temp = new JLabel(new ImageIcon(System.getProperty("user.dir") + "/UIphotos/play.png"));
//        playbutton.add(temp);
//        temp.setLocation(50,50);
//        panelfooter.add(nextbutton);

//        final JProgressBar musicprogressbar = new JProgressBar();
//        musicprogressbar.setMinimum(minmusicprogress);
//        musicprogressbar.setMaximum(maxmusicprogress);
//        musicprogressbar.setValue(10);
//        musicprogressbar.addChangeListener(new ChangeListener() {
//
//            public void stateChanged(ChangeEvent e) {
//              System.out.println("当前进度值："+musicprogressbar.getValue()+";"+"进度百分比:"+musicprogressbar.getPercentComplete());
//
//            }
//        });
//        panelfooter.add(musicprogressbar,BorderLayout.CENTER);
//        musicprogressbar.setStringPainted(true);

//        musicprogressbar.setBackground(Color.green);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask(){
//            public void run() {
//                currentProgress++;
//                if (currentProgress > maxmusicprogress) {
//                    currentProgress = minmusicprogress;
//                }
//                musicprogressbar.setValue(currentProgress);
//            }
//
//        },500);
        //中间 播放进度控制部分

        musicsliders.setLayout(new BorderLayout());

        musicslider = new JSlider(minmusicprogress,maxmusicprogress,currentProgress);
        musicslider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
//                System.out.println("当前值"+musicslider.getValue());
            }
        });
        musicslider.setBackground(new Color(246,246,248));
        musicsliders.setOpaque(false);

        panelfooter.add(musicsliders,BorderLayout.CENTER);
        musicsliders.add(progressnow,BorderLayout.WEST);
        musicsliders.add(musicslider,BorderLayout.CENTER);
        musicsliders.add(progresstotal,BorderLayout.EAST);
//        progressnow.setVerticalAlignment(SwingConstants.CENTER);




        //右边 切换播放方式
        playstyleicon = new ImageIcon(System.getProperty("user.dir") + "/UIphotos/xunhuan.png");
        playstyle = new JLabel(playstyleicon);
        playstyle.setPreferredSize(new Dimension(200,60));
        panelfooter.add(playstyle,BorderLayout.EAST);


//        test.add(te);
//        System.getProperty("user.dir") + "/downloads/test1.mp3"




//        test.setPreferredSize(new Dimension(20,20));
//        test.setBackground(Color.green);







    }
    //这个是初始化现在播放情况的界面
    public void playerNow() {


        playnow.setLayout(new FlowLayout(FlowLayout.LEFT));

        playnowcover.setBackground(Color.PINK);
        playnowcover.setPreferredSize(new Dimension(80,80));

        iconnow.setImage(iconnow.getImage().getScaledInstance(80, 80,Image.SCALE_DEFAULT ));
        playnow.add(playnowcover);
        playnow.setBackground(new Color(245,245,247));

        playnow.add(changecontent);
        playnow.add(changelist);
        changecontent.setFocusPainted(false);
        changelist.setFocusPainted(false);




//



    }

    //这个函数是用于歌词具体内容页
    public void playercontaindetail(){

//        jPanelBackground.width = containwrap2.getWidth();
//        jPanelBackground.height = containwrap2.getHeight();
        jPanelBackground.width = 900;
        jPanelBackground.height = 600;
//        System.out.println(containwrap2.getWidth());
        JPanel inner = new JPanel();


        jPanelBackground.url = gaosi.playmain("downloads/test.jpg");
        System.out.println(jPanelBackground.url);
        inner = jPanelBackground.getJPanelBackground(inner);
        inner.setPreferredSize(new Dimension(900,600));
        containwrap2.add(inner);



        String url = "/downloads/cover.png";
        JLabel back = new JLabel(new ImageIcon("E:/图片素材/刘看山.png"));
        back.setPreferredSize(new Dimension(containwrap2.getWidth(),containtop.getHeight()));



        inner.setLayout(new FlowLayout(FlowLayout.LEFT));
//        contain.setPreferredSize(new Dimension(0,0));
        JPanel bigmusiccover = new JPanel();
        detailcover = new JLabel(new ImageIcon(System.getProperty("user.dir") +url ));
        inner.add(bigmusiccover);
        bigmusiccover.add(detailcover);
        inner.add(musicwords);
//        BackgroundPanel bp = new BackgroundPanel();



        bigmusiccover.setPreferredSize(new Dimension(300,300));
        icondetail.setImage(icondetail.getImage().getScaledInstance(300, 300,Image.SCALE_DEFAULT ));

//        bigmusiccover.setBackground(Color.pink);
        musicwords.setPreferredSize(new Dimension(574,466));







    }

    //这个函数用于编辑歌单相关信息,也就是listedit页

    public void playerlistedit(){
        JLabel title = new JLabel("编辑歌单信息"); //标题


//        editlist.add();
//        JPanel editcontent = new JPanel();
//        JPanel editcontentleft = new JPanel();




        Box box1 = Box.createHorizontalBox();
        box1.add(title);
        box1.add(Box.createHorizontalGlue());
        Box box2 = Box.createHorizontalBox();
        box2.add(editlistlabel);
        box2.add(editlistname);
        Box box4 = Box.createHorizontalBox();
        box4.add(indexcover);
        box4.add(Box.createHorizontalGlue());
        Box box5 = Box.createHorizontalBox();
        box5.add(editcover);
        box5.add(Box.createHorizontalGlue());
        Box box3 = Box.createHorizontalBox();
        box3.add(editsave);
        box3.add(editcancel);
        Box vbox = Box.createVerticalBox();
        vbox.add(box1);
        vbox.add(box4);
        vbox.add(box5);
        vbox.add(box2);
        vbox.add(box3);
        editlist.setBorder(BorderFactory.createLineBorder(new Color(238,238,238),50));
        editlist.setOpaque(false);
        indexcover.setPreferredSize(new Dimension(240,240));
//        coverimage.setImage(coverimage.getImage().getScaledInstance(240, 240,Image.SCALE_DEFAULT ));
        vbox.setBackground(new Color(250,250,250));
        box1.setOpaque(false);
        vbox.setOpaque(false);
        title.setOpaque(false);

        editlist.setLayout(new FlowLayout(FlowLayout.LEFT));
        editlist.add(vbox);




        editcancel.setPreferredSize(new Dimension(110,40));
        editsave.setPreferredSize(new Dimension(110,40));
        editcover.setPreferredSize(new Dimension(110,30));
        editlistname.setPreferredSize(new Dimension(110,40));
        editcancel.setFocusPainted(false);
        editsave.setFocusPainted(false);
        editsave.setBackground(new Color(12,115,194));
        editsave.setForeground(Color.white);
        editcancel.setBackground(Color.white);
        editcancel.setForeground(Color.BLACK);
        editcover.setBackground(Color.white);
        editcover.setForeground(Color.BLACK);




    }






}

