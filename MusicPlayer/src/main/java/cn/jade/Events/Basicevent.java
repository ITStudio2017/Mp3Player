package cn.jade.Events;

import cn.jade.Tools.LrcAnalyze;
import cn.jade.Tools.Time;
import cn.jade.Ui.PlayerUi;
import cn.ktchen.http.HttpTools;
import cn.ktchen.player.PlayerThread;
import cn.ktchen.sqlite.SqliteTools;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

//import javax.swing.filechooser.FileNameExtensionFilter;

//import javax.swing.filechooser.FileNameExtensionFilter;

public class Basicevent extends PlayerUi {
    //    public static PlayerThread playerThread = null;
    private static PlayerUi playerUi = new PlayerUi();
    public static SqliteTools sqliteTools = new SqliteTools();
    public static HttpTools httpTools = new HttpTools();
    public Vector<HashMap<String,String>> musiclist = new  Vector<HashMap<String,String>>();    //我的所有歌单
    public Vector<HashMap<String,String>> searchmusicsheet = new  Vector<HashMap<String,String>>();   //我的网络搜索下的所有歌曲
    public Vector<HashMap<String,String>> mymusicsheet = new  Vector<HashMap<String,String>>();   //我的某一个歌单下的所有歌曲
    public Vector<HashMap<String,String>> nowplaysheet = new Vector<HashMap<String,String>>(); //现在正在播放的歌单

    public Vector<HashMap<String,String>> hostslist = new Vector<HashMap<String, String>>();  //存放热门榜单相关
    public Vector<Double> songtime = new Vector<Double>();  //存放 所有的歌词的时间
    public Vector<String> songword = new Vector<String>(); //存放所有的歌词对应的一段一段

    public PlayerThread playerThread = new PlayerThread(0,mymusicsheet);
    public PlayerThread playerThreadnet = null;  //网络歌单那边的
    public int tableflag = 0;//判断此时有没有列表
    public int focusrowindex = -1;  //歌单内音乐当前选中项
    public int focusmymenuindex = 0;
    public int focussearchindex = -1;
    public Thread thread = null;   //这个是播放音乐的线程
    public Thread threadscroll = null; //这个是滚动条的线程
    public int playflag = 0;//判断有没有歌正在播放
    public double stoptime = 0; // 记录现在停止时的时间
    public int nowpage = 0; //记录当前所在页
    public int playnowclick = 0; //点击当前播放歌曲

    String url=""; //保存修改过后的歌单路径
    public static void main(String[] args){
        Basicevent playerui = new Basicevent(playerUi);
        frame = new JFrame("一个美丽的音乐播放器");
        frame.setSize(1200,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




        playerui.initui(frame);


        frame.setVisible(true);

    }
    Basicevent(PlayerUi playerUi){
        this.playerUi = playerUi;
    }


//初始化我的歌单 内容


    //获取某歌单下的所有歌曲,传入的参数为歌单的索引
    public void getmusicsheet(int index){
        //        public static Vector<HashMap<String, String>> getMusicBySheet(HashMap<String, String> sheet)


        //初始化歌单名称！！！




        //初始化歌单封面！！！;

        containbottom.removeAll();

        if(musiclist.size()!=0){
            mymusicsheet = sqliteTools.getMusicBySheet(musiclist.get(index));
            covertitle.setText(musiclist.get(index).get("name"));

        }

        if(mymusicsheet.size()!=0){
            String image = musiclist.get(index).get("imagePath");
            if(image == "null" ||image.equals("null")){
                image = mymusicsheet.get(0).get("musicImage");
            }
//            String image = playerThread.getImagePath();
            System.out.println(image);
//            String image = playerThread.getImagePath();

            iconlist = new ImageIcon(image);
            iconlist.setImage(iconlist.getImage().getScaledInstance(140, 140,Image.SCALE_DEFAULT ));
            listcover.setIcon(iconlist);
        }else{

            iconlist = new ImageIcon(System.getProperty("user.dir")+"/UIphotos/刘看山.png");
            iconlist.setImage(iconlist.getImage().getScaledInstance(140, 140,Image.SCALE_DEFAULT ));
            listcover.setIcon(iconlist);
        }




//
        Object[][] rowData = new Object[mymusicsheet.size()][];
        for(int i=0;i<mymusicsheet.size();i++) {

            Object[] temp = new Object[4];
            temp[0] = new Integer(i + 1);
            temp[1] = new String(mymusicsheet.get(i).get("name"));
            temp[2] = new String(mymusicsheet.get(i).get("artist"));
            rowData[i] = temp;
        }
//        detailtable = new JTable(rowData,columnNames);
        defaultdetailtable = new DefaultTableModel(rowData,columnNames);
        JTable detailtable = new JTable(defaultdetailtable){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
        detailtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        setmytable(detailtable);
        layout.show(panelmainer, "list");

    }



    //获取网络搜索得到的歌单
//    public static Vector<HashMap<String, String>> search(String keyWord, int page, int count)
    public void getsearchmusicsheet(final String keyWord,final int page ,final int count){


        containbottom.removeAll();
        //看看它选了什么音乐
        int choose = searchsource.getSelectedIndex();
        System.out.println("choose"+choose);
        String source = "";
        switch (choose){
            case 0:
                source = "netease";break;
            case 1:
                source = "tencent";break;
            case 2:
                source = "xiami";break;
            case 3:
                source = "kugou";break;

        }
        searchmusicsheet = httpTools.search(keyWord,page,count,source);



        iconlist = new ImageIcon(System.getProperty("user.dir")+"/UIphotos/刘看山.png");
        covertitle.setText("搜索结果");
        iconlist.setImage(iconlist.getImage().getScaledInstance(140, 140,Image.SCALE_DEFAULT ));
        listcover.setIcon(iconlist);

        final Object[][] rowData = new Object[count][];
        for(int i=0;i<count;i++) {
////            temp[0]
            Object[] temp = new Object[4];
            temp[0] = new Integer(i + 1);
            temp[1] = new String(searchmusicsheet.get(i).get("name"));
            temp[2] = new String(searchmusicsheet.get(i).get("artist"));
//                temp[3] = new Label("下载");
            rowData[i] = temp;
        }
        defaultdetailtable = new DefaultTableModel(rowData,columnNames);
        JTable detailtable = new JTable(defaultdetailtable){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
        detailtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        settable(detailtable);
        pagewrap.setLayout(new BorderLayout());
        pagewrap.add(lastpage,BorderLayout.WEST);
        pagewrap.add(nextpage,BorderLayout.EAST);
        containbottom.add(pagewrap,BorderLayout.SOUTH);

        layout.show(panelmainer, "list");


    }
//获取我的热门列表

    public void gethotmusicsheet(int index){


        containbottom.removeAll();
        //public static Vector<HashMap<String,String>> getMusicListByInternetPlaylist(
        //      HashMap<String, String> playlist)
        //获取歌单
        searchmusicsheet = httpTools.getMusicListByInternetPlaylist(hostslist.get(index));

        try{
            iconlist = new ImageIcon(new URL(hostslist.get(index).get("coverImgUrl")));
        }catch (Exception e){
            System.out.println("网络繁忙，图片下载失败");
            iconlist = new ImageIcon(System.getProperty("user.dir")+"/UIphotos/刘看山.png");

        }

        covertitle.setText(hostslist.get(index).get("title"));
        iconlist.setImage(iconlist.getImage().getScaledInstance(140, 140,Image.SCALE_DEFAULT ));
        listcover.setIcon(iconlist);
        int count = 10;
        final Object[][] rowData = new Object[count][];
        for(int i=0;i<count;i++) {
////            temp[0]
            Object[] temp = new Object[4];
            temp[0] = new Integer(i + 1);
            temp[1] = new String(searchmusicsheet.get(i).get("name"));
            temp[2] = new String(searchmusicsheet.get(i).get("artist"));
//                temp[3] = new Label("下载");
            rowData[i] = temp;
        }
        defaultdetailtable = new DefaultTableModel(rowData,columnNames);
        JTable detailtable = new JTable(defaultdetailtable){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
        detailtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        settable(detailtable);
        pagewrap.setLayout(new BorderLayout());
//        pagewrap.add(lastpage,BorderLayout.WEST);
//        pagewrap.add(nextpage,BorderLayout.EAST);
        containbottom.add(pagewrap,BorderLayout.SOUTH);

        layout.show(panelmainer, "list");


    }
    //获取我的歌单列表
    public void getmymusiclist(){
        mylist.removeAll();
        musiclist = sqliteTools.getSheetList();
        //清空vector
        myv.removeAllElements();

        for(int i =0 ;i<musiclist.size();i++)
        {
            myv.add(musiclist.get(i).get("name"));
        }
        mylist.setListData(myv);
    }




    @Override
    public void playerNav() {
        super.playerNav();
        searchbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String search = searchinput.getText();

                nowpage = 1;
                getsearchmusicsheet(search,nowpage,10);

            }
        });
    }

    @Override
    public void playerFooter() {
//        settime();
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
                        playerThread.setNowPattern(PlayerThread.Pattern.Sequence);
                        break;
                    case 3:
                        playstyleicon = new ImageIcon(System.getProperty("user.dir") + "/UIphotos/danqu.png");
                        playstyle.setToolTipText("单曲循环");
                        playstyle.setIcon(playstyleicon);
                        playerThread.setNowPattern(PlayerThread.Pattern.Single);
                        break;
                    case 2:
                        playstyleicon = new ImageIcon(System.getProperty("user.dir") + "/UIphotos/suiji.png");
                        playstyle.setToolTipText("随机播放");
                        playstyle.setIcon(playstyleicon);
                        playerThread.setNowPattern(PlayerThread.Pattern.Stochastic);
                        break;

                }


//



            }
        });
        //我是不是应该记录一下当前歌曲，找一个变量

        //暂停播放的切换
        playbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {


                if(playbutton.getText() == "播放"){
//                    playbutton.setText("暂停");
                    if(playflag == 0 && focusrowindex == -1){
                        //初次播放的时候设置默认值
                        nowplaysheet = mymusicsheet ;
                        playerThread.setMusicIndex(0);
                        playerThread.setMusicList(nowplaysheet);
                        System.out.println("我运行啦");
//                        focusrowindex = playerThread.getMusicIndex();
                    }


                    playflag = 1;
                    playerThread.setPlayThread(new Thread(playerThread));
                    playerThread.getPlayThread().start();
                    settime();
                    setnew();

                    //播放按钮变成暂停
                    playbutton.setText("暂停");
//                    System.out.println("现在播放到的时间"+playerThread.getNowMusicTime());


                }else{

                    //暂停拖动条的线程，isINterrupt不会清除，而
                    threadscroll.interrupt();

                    //记录下这时候的时间，以便下一次开始'
                    stoptime = playerThread.getNowMusicTime();
                    //改变按钮
                    playbutton.setText("播放");
                    playerThread.pause();
                    playflag = 0;
                }
            }
        });

        //下一首上一首的切换
        nextbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playbutton.setText("暂停");
                playerThread.nextMusic();

                stoptime = 0;
                threadscroll.interrupt();

                settime();
                setnew();
            }
        });
        lastbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playbutton.setText("暂停");
                playerThread.previousMusic();

                stoptime = 0;
                threadscroll.interrupt();
                settime();
                setnew();
            }
        });

        //初始化 设置总时长
//        System.out


        //然后我需要知道，呸，我要拖动滑动条

//        musicslider.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                //首先 很明显我应该先把线程给停了
//
//
//
//
//            }
//        });

        musicslider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.println("你按下了我");
                threadscroll.interrupt();
                playerThread.pause();
                //当然拖的过程中当然要显示现在的时间啦
                //现在时间的计算方式有点诡异
                musicslider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        musicslider.setMaximum((int)playerThread.getMusicTime());
                        int temp = musicslider.getValue();

                        Time t = new Time(temp);
                        progressnow.setText(t.getTime());

                    }
                });


            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);



                //然后拖哇拖，知道获得了vale
                int temp = musicslider.getValue();

                //获得value之后再重新设置

                // public boolean setTime(int seconds)
                playerThread.setTime(temp,true);
//                playerThread.getPlayThread().start();
                //再开一个新的
                settime();
                setnew();

            }


        });



    }

    public void settime(){
        Time t = new Time(playerThread.getMusicTime());
        progresstotal.setText(t.getTime());
        setotherfooter();
//        setnew();
    }
    //切换新歌的一些东东
    public void setnew(){
        //!!严格获取当前歌曲和当前索引
        nowplaysheet = playerThread.getMusicList();
        focusrowindex = playerThread.getMusicIndex();
        //设置playnow的封面
        //首先获取当前播放歌曲的相关信息
        //如果是网络来源那么应该把图片换成url，如果没有图片 应该是默认图片，如果如果

        String image ="";
        if(nowplaysheet == searchmusicsheet){
            image = playerThread.getImagePath(true);

        }else{
            image = nowplaysheet.get(focusrowindex).get("musicImage");
        }


        iconnow = new ImageIcon(image);


        iconnow.setImage(iconnow.getImage().getScaledInstance(80, 80,Image.SCALE_DEFAULT ));
        playnowcover.setIcon(iconnow);

        //设置歌单页的封面
        icondetail = new ImageIcon(image);
        icondetail.setImage(icondetail.getImage().getScaledInstance(300, 300,Image.SCALE_DEFAULT ));
        detailcover.setIcon(icondetail);

        //设置歌手和歌名信息
        songtitle.setText(nowplaysheet.get(focusrowindex).get("name"));
        songsinger.setText(nowplaysheet.get(focusrowindex).get("artist"));
        //设置背景
//        String str = "downloaddd";
//        Pattern p = Pattern.compile("\\download.*");
//        Matcher m = p.matcher(str);
//        System.out.println(m.group(1));
//        jPanelBackground.url = gaosi.playmain(image);

        //设置歌词
        musicwords.setText("");
        String word = playerThread.getLrcPath();


        LrcAnalyze l = new LrcAnalyze(word);
        List<LrcAnalyze.LrcData> list = l.LrcGetList();
        Time t2 = null;
        songtime.removeAllElements();
        songword.removeAllElements();

        for(LrcAnalyze.LrcData o:list){
            try{

            songtime.add(new Time(o.Time).getSeconds());
            songword.add(o.LrcLine);
            musicwords.append(o.LrcLine+"\n");
                }catch (Exception e){
                System.out.println("歌词解析错误");
            }

        }



        playbutton.setText("暂停");
    }
    public void setotherfooter(){
        //设置拖动条的速度
        final double pinjun = playerThread.getMusicTime()/100;
//        System.out.println("pinjun"+pinjun);
//        System.out.println("totaltime"+playerThread.getMusicTime());
        threadscroll = new Thread(){
            public void run(){
                try{


                    double m = playerThread.getMusicTime();
                    musicslider.setMaximum((int)m);
                    int now = (int)(playerThread.getNowMusicTime());
                    for(double i = now;i < m+30; ){
//                        System.out.println("现在播放到的时间"+playerThread.getNowMusicTime());
                        //因为文件解码需要一些时间，所以前面一段时间或许要等待一下


                        if(this.interrupted()){
                            System.out.println("线程已经终止， for循环不再执行");
                            throw new InterruptedException();
                        }
//                        System.out.println("i="+(i+1));
                        musicslider.setValue((int)i);
                        Thread.sleep(500);
//                        musicslider.setValue((int)(i/pinjun));

                        i =  playerThread.getNowMusicTime();
                        Time t = new Time(i);

                        progressnow.setText(t.getTime());

                        //现在一个重大的任务就是，歌词！！换 给我换，首先，要有歌词
                        int size = songtime.size();
                        if(size != 0){
                            for(int j = 0;j < size;j++ ){
                                try{
                                    if(j < size -1){
                                        if(i>songtime.get(j) && i<songtime.get(j+1))
                                        {
//
                                            songnowword.setText(songword.get(j));
                                            break;
                                        }
                                    }else{
                                        songnowword.setText(songword.get(j));
                                    }

                                }catch (Exception e){
                                    System.out.println("系统繁忙请稍后再试");
                                }


                            }
                        }else{
                            songnowword.setText("该歌曲暂无歌词");
                        }

                        if(Math.ceil(i) == (int)m){

                            System.out.println("切换啊冲啊！！！");

//                            playerThread.nextMusic();

                            threadscroll.interrupt();
//                            Thread.sleep(200);
//                            focusrowindex = playerThread.getMusicIndex();
                            stoptime = 0;
                            settime();
                            setnew();



                        }
                    }

                    //假如播完了怎么办?现在正常停止了并且应该重新开始,那么就应该根据播放模式进行切换




                }catch (InterruptedException e){
                    System.out.println("本次拖动条线程已暂停");
//                            e.printStackTrace();
                }


            }
        };
        threadscroll.start();
    }
    //设置表格相关参数,因为着急就先这样了
    public void setmytable(final JTable detailtable){
        //设置表格内容颜色

        detailtable.setForeground(Color.BLACK);
        detailtable.setBackground(new Color(250,250,250));
        detailtable.setSelectionBackground(new Color(236,236,237));
//        detailtable
        //添加表头和表格内容
        containbottom.add(detailtable.getTableHeader(),BorderLayout.NORTH);
        containbottom.add(detailtable,BorderLayout.CENTER);


        //设置表头
//        detailtable.getTableHeader().setFocusTraversalKe;

        //设置行高
        detailtable.setRowHeight(30);

        final JMenuItem del = new JMenuItem("删除");
        final JMenuItem play = new JMenuItem("播放");
//        JMenuItem stop = new JMenuItem("暂停");
        final JPopupMenu sheetpop = new JPopupMenu();


        sheetpop.add(play);
        sheetpop.add(del);

        detailtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e ) {
                super.mouseClicked(e);
                focusrowindex = detailtable.rowAtPoint(e.getPoint());
                if (focusrowindex == -1) {
                    return;
                }
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {


                    play.doClick();

                }
                else if(e.getButton() == MouseEvent.BUTTON3) {

                    detailtable.setRowSelectionInterval(focusrowindex, focusrowindex);
                    //弹出菜单
                    sheetpop.show(detailtable, e.getX(), e.getY());
                }


            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }
        });
        //接下来为删除和编辑做对应的操作，添加对应的事件



        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {





                //只有不同歌曲了或者不同歌单了才stop，所以首先就要判断是不是刚刚那首歌，也就是歌单相同然后索引也相同，而且之前是停止的状态
                //如果还是同一首歌 无视
                if((playerThread.getMusicList() == mymusicsheet) && (focusrowindex == playerThread.getMusicIndex()) && playflag == 0){


                }else if((playerThread.getMusicList() == mymusicsheet) && (focusrowindex == playerThread.getMusicIndex()) && playflag == 1){
                    return;
                }else{
                    playerThread.stop();
                }

                playbutton.setText("暂停");
                //那么在每一次播放之前，都要把之前的线程给关掉，
                if(playflag == 1 ) {
                    threadscroll.interrupt();
                    stoptime = 0;
//                    playerThread.pause();

                }else{

                }

                //点击右键的话，毫无疑问现在是什么歌单就放什么歌
                nowplaysheet = mymusicsheet;
                System.out.println("多重走");
                playerThread.setMusicIndex(focusrowindex);
                playerThread.setMusicList(nowplaysheet);
                playerThread.setPlayThread(new Thread(playerThread));
                playerThread.getPlayThread().start();
                settime();
                setnew();
                playflag = 1;

                //播放按钮变成暂停
                playbutton.setText("暂停");

            }
        });
//        stop.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(focusrowindex != playerThread.getMusicIndex()){
//                    return ;
//                }
//
//                //暂停拖动条的线程，isINterrupt不会清除，而
//                threadscroll.interrupt();
//
//                //记录下这时候的时间，以便下一次开始'
//                stoptime = playerThread.getNowMusicTime();
//                playbutton.setText("播放");
//                playerThread.pause();
//                playflag = 0;
//            }
//        });
        //删除我的歌单中的歌曲
        del.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "确认删除？",
                        "提示",
                        JOptionPane.YES_NO_CANCEL_OPTION

                );
                System.out.println("选择结果:"+result);
                //deleteSheet(HashMap<String, String> sheet)
                if(result==0){
                    //调用数据库方法
                    sqliteTools.deleteMusic(mymusicsheet.get(focusrowindex));
                    //重新绘制
                    getmusicsheet(focusmymenuindex);
                }

            }


        });
        //如果有上一页下一页按钮的话
        lastpage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(nowpage != 1){
                    nowpage-=1;
                    getsearchmusicsheet(searchinput.getText(),nowpage,10);
                }
            }
        });
        nextpage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nowpage+=1;
                getsearchmusicsheet(searchinput.getText(),nowpage,10);
            }
        });

    }



    @Override
    public void playerMymenu() {
        super.playerMymenu();
        getmymusiclist();



        //鼠标右键出现对歌单的删除和编辑
        JMenuItem edit = new JMenuItem("编辑");
        JMenuItem del = new JMenuItem("删除");
        final JMenuItem open = new JMenuItem("打开");
        final JPopupMenu pop = new JPopupMenu();
        pop.add(edit);
        pop.add(del);
        pop.add(open);
        mylist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                mylist.setSelectionForeground(new Color(11,11,11));

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                focusmymenuindex = mylist.locationToIndex(e.getPoint());
                showpop(e);
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {

                    if (focusmymenuindex == -1) {
                        return;
                    }
                    open.doClick();

                }else{
//                    showpop(e);
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

//                focusmymenuindex = mylist.locationToIndex(e.getPoint());
//                mylist.setSelectedIndex(focusmymenuindex);  //获取鼠标点击的项
                focusmymenuindex = mylist.locationToIndex(e.getPoint());
                mylist.setSelectedIndex(focusmymenuindex);  //获取鼠标点击的项
                showpop(e);
//

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                mylist.setSelectionBackground(new Color(230,231,234));
                showpop(e);
//           ;
            }
            //弹出菜单
            private void showpop(MouseEvent e){
                if(e.isPopupTrigger()&&mylist.getSelectedIndex()!=-1 && e.getButton() == MouseEvent.BUTTON3){
//                    if(e.getClickCount() == 2){
//                        getmusicsheet(mylist.getSelectedIndex());
//                        System.out.println("打开歌词内容");
//                    }else if(e.getClickCount() == 1) {
                    //获取选择项的值
                    Object selected = mylist.getModel().getElementAt(mylist.getSelectedIndex());
                    System.out.println(selected);
                    pop.show(e.getComponent(), e.getX(), e.getY());
                    }
                }

        });
        //接下来为删除和编辑做对应的操作，添加对应的事件
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //将相片替换成当前的相片
                coverimage = new ImageIcon(musiclist.get(focusmymenuindex).get("imagePath"));
                System.out.println("coverimage"+coverimage);
                coverimage.setImage(coverimage.getImage().getScaledInstance(240, 240,Image.SCALE_DEFAULT ));
                indexcover.setIcon(coverimage);
                //名字替换成当前歌单名
                editlistname.setText(musiclist.get(focusmymenuindex).get("name"));


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
                if(result == 0){
                    //调用数据库方法
                    // public static void deleteSheet(HashMap<String, String> sheet)
                    sqliteTools.deleteSheet(musiclist.get(focusmymenuindex));
                    getmymusiclist();

                }
            }
        });
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                getmusicsheet(mylist.getSelectedIndex());
                getmusicsheet(focusmymenuindex);
                System.out.println("打开歌词内容");
            }
        });

        //输入新建歌单的名字
        mymenuadd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // 显示输入对话框, 返回输入的内容

                String inputContent = JOptionPane.showInputDialog(
                        frame,
                        "输入歌单名称:",
                        "歌单名称",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if(inputContent == null){
                    return;
                }
                //调用数据库方法创建歌单
//                public static void createSheet(String name)
                sqliteTools.createSheet(inputContent);
                getmymusiclist();
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
//        System.out.println(playerThread.getLrcPath());

//        LrcAnalyze l = new LrcAnalyze(System.getProperty("user.dir") + "/downloads/test.lrc");
//        List<LrcAnalyze.LrcData> list = l.LrcGetList();
//        String s ="";
//        for(LrcAnalyze.LrcData o:list){
////            System.out.println(o.Time);
//            musicwords.append(o.LrcLine+"\n");
//
//        }




    }

    //编辑歌单信息
    @Override
    public void playerlistedit() {
        super.playerlistedit();
        url = musiclist.get(focusmymenuindex).get("imagePath");
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
                    url = file.getAbsolutePath();
//                    String url = "E:\\mp3player3\\Mp3Player\\MusicPlayer\\UIphotos\\indexcover.png";
//                    String url2 = url.replace("\\","/");
//                    System.out.println(url2);
                    indexcover.setIcon(new ImageIcon(url));
                    //修改歌单封面
                    //public static void updateSheetImage(HashMap<String,String> sheet, String imagePath)

                }


            }
        });
        editsave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //先把修改结果存入数据库
                sqliteTools.updateSheetImage(musiclist.get(focusmymenuindex),url);
                // public static void updateSheetName(HashMap<String,String> sheet, String name)
                sqliteTools.updateSheetName(musiclist.get(focusmymenuindex),editlistname.getText());
                //然后刷新一波
                getmymusiclist();


                // 消息对话框无返回, 仅做通知作用
                JOptionPane.showMessageDialog(
                        frame,
                        "保存成功",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                getmusicsheet(focusmymenuindex);
//                layout.show(panelmainer, "list");



            }
        });
        editcancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                layout.show(panelmainer,"list");
            }
        });
    }



    @Override
    public void playerNow() {
        //切换歌单的封面，这时候
        super.playerNow();
        playnowcover.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                playnowclick++;
                if(playnowclick == 2){
                    layout.show(panelmainer, "content");
                    playnowclick = 0;
                }else if(playnowclick == 1){
                    layout.show(panelmainer, "list");
                }


            }
        });
        changecontent.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {



            }
        });
        changelist.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {



            }
        });
    }

    @Override
    public void playerContain() {
        super.playerContain();
        getmusicsheet(0);
    }

    //设置表格相关参数,因为着急就先这样了
    public void settable(final JTable detailtable){

        //歌单封面大小

        iconlist.setImage(iconlist.getImage().getScaledInstance(140, 140,Image.SCALE_DEFAULT ));
        //设置表格内容颜色

        detailtable.setForeground(Color.BLACK);
        detailtable.setBackground(new Color(250,250,250));
        detailtable.setSelectionBackground(new Color(236,236,237));
//        detailtable
        //添加表头和表格内容
        containbottom.add(detailtable.getTableHeader(),BorderLayout.NORTH);
        containbottom.add(detailtable,BorderLayout.CENTER);

        //禁止编辑和单选
        detailtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //去掉蓝色选中
//        detailtable.setFocusable(false);




        //设置表头
//        detailtable.getTableHeader().setFocusTraversalKe;

        //设置行高
        detailtable.setRowHeight(30);
        JMenuItem download = new JMenuItem("下载");
        final JMenuItem play = new JMenuItem("播放");
        final JPopupMenu sheetpop = new JPopupMenu();
        sheetpop.add(download);
        sheetpop.add(play);

        detailtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e ) {
                super.mouseClicked(e);
                focussearchindex = detailtable.rowAtPoint(e.getPoint());
                if (focussearchindex == -1) {
                    return;
                }
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {


                    play.doClick();

                }
                else if(e.getButton() == MouseEvent.BUTTON3) {

                    detailtable.setRowSelectionInterval(focussearchindex, focussearchindex);
                    //弹出菜单
                    sheetpop.show(detailtable, e.getX(), e.getY());
                }



            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }
        });
        //接下来为删除和编辑做对应的操作，添加对应的事件
        download.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                layout.show(panelmainer, "editlist");.
                //现在应该选择下载到哪一个歌单 默认选则我喜欢的歌单
                //public static void downloadMusic(HashMap<String, String> music, HashMap<String,String> sheet)
                //!!!!!

                //先弹出框选择想下载到的歌单

                Object[] selectionValues = null;
                selectionValues = myv .toArray( new Object[myv .size()]);// vector ->array


                // 显示输入对话框, 返回选择的内容, 点击取消或关闭, 则返回null
                musiclist = sqliteTools.getSheetList();
                //清空vector
                Object inputContent = JOptionPane.showInputDialog(
                        frame,
                        "下载到歌单: ",
                        "选择项",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        selectionValues,
                        selectionValues[0]
//
                );
                System.out.println("输入的内容: " + inputContent);
                //再去匹配看是歌单哪一个
                for(int i=0;i<myv.size();i++)
                {
                    if(inputContent == myv.get(i).toString()){
                        httpTools.downloadMusic(searchmusicsheet.get(focusrowindex),musiclist.get(i));
                        System.out.println("下载完成");
                        return;
                    }
                }
            }

        });

//                httpTools.downloadMusic(searchmusicsheet.get(focusrowindex),musiclist.get(0));
        //卧槽在线播放
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {




                //只有不同歌曲了或者不同歌单了才stop，所以首先就要判断是不是刚刚那首歌，也就是歌单相同然后索引也相同，而且之前是停止的状态
                //如果还是同一首歌 无视
                if((playerThread.getMusicList() == searchmusicsheet) && (focussearchindex == playerThread.getMusicIndex()) && playflag == 0){

                }else if((playerThread.getMusicList() == searchmusicsheet) && (focussearchindex == playerThread.getMusicIndex()) && playflag == 1){
                    return;
                }else{
                    playerThread.stop();
                }

                playbutton.setText("暂停");
                //那么在每一次播放之前，都要把之前的线程给关掉，

                if(focusrowindex != -1){

                }
                if(playflag == 1 ) {
                    threadscroll.interrupt();
                    stoptime = 0;
//                    playerThread.pause();

                }else{

                }

                //点击右键的话，毫无疑问现在是什么歌单就放什么歌
                nowplaysheet = searchmusicsheet;
                System.out.println("多重走");
                playerThread.setMusicIndex(focussearchindex);
                playerThread.setMusicList(nowplaysheet);
                playerThread.setPlayThread(new Thread(playerThread));
                playerThread.getPlayThread().start();
                settime();
                setnew();
                playflag = 1;

                //播放按钮变成暂停
                playbutton.setText("暂停");




            }
        });





    }
    //关于榜单的部分，一点点
    @Override
    public void playerhotlist() {
        super.playerhotlist();

      new Thread(){
            @Override
            public void run() {
                super.run();

        //public static Vector<HashMap<String,String>> getInternetPlaylist(int page, int count)
        try {


            hostslist = httpTools.getInternetPlaylist(1, 6);
//         for(int i = 0;i<6;i++) {
//             String temp = "hotlabelwrap"+i;

            hotlabelword1.setText(hostslist.get(0).get("title"));
            hotlabelword2.setText(hostslist.get(1).get("title"));
            hotlabelword3.setText(hostslist.get(2).get("title"));
            hotlabelword4.setText(hostslist.get(3).get("title"));
            hotlabelword5.setText(hostslist.get(4).get("title"));
            hotlabelword6.setText(hostslist.get(5).get("title"));
            hotlabelintro1.setText(hostslist.get(0).get("description"));
            hotlabelintro2.setText(hostslist.get(1).get("description"));
            hotlabelintro3.setText(hostslist.get(2).get("description"));
            hotlabelintro4.setText(hostslist.get(3).get("description"));
            hotlabelintro5.setText(hostslist.get(4).get("description"));
            hotlabelintro6.setText(hostslist.get(5).get("description"));
            try {
                hotcover1 = new ImageIcon(new URL(hostslist.get(0).get("coverImgUrl")));
                hotcover2 = new ImageIcon(new URL(hostslist.get(1).get("coverImgUrl")));
                hotcover3 = new ImageIcon(new URL(hostslist.get(2).get("coverImgUrl")));
                hotcover4 = new ImageIcon(new URL(hostslist.get(3).get("coverImgUrl")));
                hotcover5 = new ImageIcon(new URL(hostslist.get(4).get("coverImgUrl")));
                hotcover6 = new ImageIcon(new URL(hostslist.get(5).get("coverImgUrl")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            hotcover1.setImage(hotcover1.getImage().getScaledInstance(240, 240, Image.SCALE_DEFAULT));
            hotcover2.setImage(hotcover2.getImage().getScaledInstance(240, 240, Image.SCALE_DEFAULT));
            hotcover3.setImage(hotcover3.getImage().getScaledInstance(240, 240, Image.SCALE_DEFAULT));
            hotcover4.setImage(hotcover4.getImage().getScaledInstance(240, 240, Image.SCALE_DEFAULT));
            hotcover5.setImage(hotcover5.getImage().getScaledInstance(240, 240, Image.SCALE_DEFAULT));
            hotcover6.setImage(hotcover6.getImage().getScaledInstance(240, 240, Image.SCALE_DEFAULT));
            hotlabel1.setIcon(hotcover1);
            hotlabel2.setIcon(hotcover2);
            hotlabel3.setIcon(hotcover3);
            hotlabel4.setIcon(hotcover4);
            hotlabel5.setIcon(hotcover5);
            hotlabel6.setIcon(hotcover6);


//         }
            hotmenutitle.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
//                    playerhotlist();
                    layout.show(panelmainer, "hotslist");

                }
            });
            hotlabelwrap1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    gethotmusicsheet(0);
                }
            });

            hotlabelwrap2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    gethotmusicsheet(1);
                }
            });
            hotlabelwrap4.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    gethotmusicsheet(3);
                }
            });
            hotlabelwrap5.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    gethotmusicsheet(4);
                }
            });
            hotlabelwrap6.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    gethotmusicsheet(5);
                }
            });


        } catch (Exception e) {
            System.out.println("网络繁忙请稍后再试");
        }
            }
        }.start();
    }
}
