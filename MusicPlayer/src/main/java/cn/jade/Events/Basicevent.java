package cn.jade.Events;

import cn.jade.Tools.LrcAnalyze;
import cn.jade.Tools.Time;
import cn.jade.Ui.PlayerUi;
import cn.ktchen.http.HttpTools;
import cn.ktchen.player.PlayerThread;
import cn.ktchen.sqlite.SqliteTools;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
    public PlayerThread playerThread = new PlayerThread(0,mymusicsheet);
    public int tableflag = 0;//判断此时有没有列表
    public int focusrowindex = -1;  //歌单内音乐当前选中项
    public int focusmymenuindex = 0;
    public Thread thread = null;   //这个是播放音乐的线程
    public Thread threadscroll = null; //这个是滚动条的线程
    public int playflag = 0;//判断有没有歌正在播放
    public double stoptime = 0; // 记录现在停止时的时间
    public int nowpage = 0; //记录当前所在页
    public static void main(String[] args){
        Basicevent playerui = new Basicevent(playerUi);
        frame = new JFrame("一个美丽的音乐播放器");
        frame.setSize(1200,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);





//        placeComponents(panel);

        playerui.initui(frame);
//        playerui.setmytable(detailtable);

//        settime();

        frame.setVisible(true);
        System.out.println("xxx");
    }
    Basicevent(PlayerUi playerUi){
        this.playerUi = playerUi;
    }


//初始化我的歌单 内容


    //获取某歌单下的所有歌曲,传入的参数为歌单的索引
    public void getmusicsheet(int index){
        //        public static Vector<HashMap<String, String>> getMusicBySheet(HashMap<String, String> sheet)

//    System.out.println(image);



        //初始化歌单封面！！！;

        containbottom.removeAll();
        layout.show(panelmainer, "list");
        if(musiclist.size()!=0){
            mymusicsheet = sqliteTools.getMusicBySheet(musiclist.get(index));
            String image = playerThread.getImagePath();
            listcover.setIcon(new ImageIcon(image));
        }



//
        Object[][] rowData = new Object[mymusicsheet.size()][];
        for(int i=0;i<mymusicsheet.size();i++) {

            Object[] temp = new Object[4];
            temp[0] = new Integer(i + 1);
            temp[1] = new String(mymusicsheet.get(i).get("name"));
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

    }



    //获取网络搜索得到的歌单
//    public static Vector<HashMap<String, String>> search(String keyWord, int page, int count)
    public void getsearchmusicsheet(final String keyWord,final int page ,final int count){


        containbottom.removeAll();
        searchmusicsheet = httpTools.search(keyWord,page,count);

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
                        playerThread.setNowPattern(PlayerThread.Pattern.Stochastic);
                        break;
                    case 2:
                        playstyleicon = new ImageIcon(System.getProperty("user.dir") + "/UIphotos/suiji.png");
                        playstyle.setToolTipText("随机播放");
                        playstyle.setIcon(playstyleicon);
                        playerThread.setNowPattern(PlayerThread.Pattern.Single);
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
                    playbutton.setText("暂停");



                    if(playflag == 1) {
                        playerThread.pause();
//                        //暂停拖动条的线程，isINterrupt不会清除，而
//                        threadscroll.interrupt();
//                        System.out.println("stop11"+threadscroll.isInterrupted());

                    }else if(playflag == 0 && focusrowindex == -1){
                        //初次播放的时候设置默认值
                        playerThread.setMusicIndex(0);
                        playerThread.setMusicList(mymusicsheet);
                        System.out.println("我运行啦");
                    }
//
                    settime();
                    playflag = 1;
                    thread = new Thread(playerThread);
                    thread.start();

                    //播放按钮变成暂停
                    playbutton.setText("暂停");
                    System.out.println("现在播放到的时间"+playerThread.getNowMusicTime());

                }else{

                    //暂停拖动条的线程，isINterrupt不会清除，而
                    threadscroll.interrupt();

                    //记录下这时候的时间，以便下一次开始'
                    stoptime = playerThread.getNowMusicTime();
//                    System.out.println("stop22"+threadscroll.interrupted());

                    playbutton.setText("播放");
                    playerThread.pause();
                    playflag = 0;
                }
            }
        });

        //下一首上一首的切换
        nextbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                playerThread.nextMusic();
                if(focusrowindex!=mymusicsheet.size()){
                    focusrowindex++;
                }
                threadscroll.interrupt();
                stoptime = 0;
                settime();
            }
        });
        lastbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerThread.previousMusic();
                if(focusrowindex!=0){
                    focusrowindex--;
                }
                stoptime = 0;
                threadscroll.interrupt();
                settime();
            }
        });

        //初始化 设置总时长
//        System.out





    }
    public void setotherfooter(){
        //设置拖动条的速度
        final double pinjun = playerThread.getMusicTime()/100;
//        if(playflag == 1){
//            threadscroll.currentThread().interrupt();
//            System.out.println("stop1"+thread.interrupted());
//            System.out.println("stop2"+thread.interrupted());
//        }
        threadscroll = new Thread(){
            public void run(){
//                System.out.println();
                try{
                        int now =(int)(stoptime/pinjun);
                        System.out.println("now"+now);

                        for(int i = now;i < 100; i++){

                            if(this.interrupted()){
                                System.out.println("线程已经终止， for循环不再执行");
                                throw new InterruptedException();
                            }
                            System.out.println("i="+(i+1));



                                Thread.sleep((long) pinjun*1000);
                                musicslider.setValue(i);
                                int temp = i;
                                Time t = new Time(i);
                                progressnow.setText(t.getTime());
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
    public void settime(){
        Time t = new Time(playerThread.getMusicTime());

        progresstotal.setText(t.getTime());
        setotherfooter();
    }
    @Override
    public void playerMymenu() {
        super.playerMymenu();
        getmymusiclist();


        //鼠标右键出现对歌单的删除和编辑
        JMenuItem edit = new JMenuItem("编辑");
        JMenuItem del = new JMenuItem("删除");
        JMenuItem open = new JMenuItem("打开");
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
                showpop(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                focusmymenuindex = mylist.locationToIndex(e.getPoint());
                mylist.setSelectedIndex(focusmymenuindex);  //获取鼠标点击的项
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
//                    if(e.getClickCount() == 2){
//                        getmusicsheet(mylist.getSelectedIndex());
//                        System.out.println("打开歌词内容");
//                    }else if(e.getClickCount() == 1) {
                        //获取选择项的值
                        Object selected = mylist.getModel().getElementAt(mylist.getSelectedIndex());
                        System.out.println(selected);
                        pop.show(e.getComponent(), e.getX(), e.getY());
//                    }
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
                        "歌单名称"

                );
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

        LrcAnalyze l = new LrcAnalyze(System.getProperty("user.dir") + "/downloads/test.lrc");
        List<LrcAnalyze.LrcData> list = l.LrcGetList();
        String s ="";
        for(LrcAnalyze.LrcData o:list){
            System.out.println(o.Time);
            musicwords.append(o.LrcLine+"\n");

        }




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
//        JMenuItem download = new JMenuItem("下载");
        final JMenuItem del = new JMenuItem("删除");
        JMenuItem play = new JMenuItem("播放");
        JMenuItem stop = new JMenuItem("暂停");
        final JPopupMenu sheetpop = new JPopupMenu();
//        sheetpop.add(download);

        sheetpop.add(play);
        sheetpop.add(stop);
        sheetpop.add(del);

        detailtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e ) {
                super.mouseClicked(e);
                if(e.getButton() == MouseEvent.BUTTON3) {
                    focusrowindex = detailtable.rowAtPoint(e.getPoint());
                    if (focusrowindex == -1) {
                        return;
                    }
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
//        download.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
////                layout.show(panelmainer, "editlist");.
//                //现在应该选择下载到哪一个歌单 默认选则我喜欢的歌单
//                //public static void downloadMusic(HashMap<String, String> music, HashMap<String,String> sheet)
//                httpTools.downloadMusic(mymusicsheet.get(focusrowindex),musiclist.get(0));
//                System.out.println("下载完成");
//
//            }
//        });


        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                if(playflag == 1) {
//                    playerThread.pause();
//
//                }
//                playerThread.setMusicIndex(focusrowindex);
//                playerThread.setMusicList(mymusicsheet);
//                playflag = 1;
//                thread = new Thread(playerThread);
//                thread.start();
//
//                //播放按钮变成暂停
//                playbutton.setText("暂停");
//                settime();
                playbutton.setText("暂停");



                if(playflag == 1) {
                    threadscroll.interrupt();
                    stoptime = 0;
                    playerThread.pause();
//                        //暂停拖动条的线程，isINterrupt不会清除，而
//                        threadscroll.interrupt();
//                        System.out.println("stop11"+threadscroll.isInterrupted());

                }
                 playerThread.setMusicIndex(focusrowindex);
                playerThread.setMusicList(mymusicsheet);
                settime();

                playflag = 1;
                thread = new Thread(playerThread);
                thread.start();

                //播放按钮变成暂停
                playbutton.setText("暂停");
                System.out.println("现在播放到的时间"+playerThread.getNowMusicTime());
            }
        });
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                playflag = 0;
//                playerThread.pause();
//                playbutton.setText("播放");

                //暂停拖动条的线程，isINterrupt不会清除，而
                threadscroll.interrupt();

                //记录下这时候的时间，以便下一次开始'
                stoptime = playerThread.getNowMusicTime();
//                    System.out.println("stop22"+threadscroll.interrupted());

                playbutton.setText("播放");
                playerThread.pause();
                playflag = 0;
            }
        });
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
    public void playerContain() {
        super.playerContain();
        getmusicsheet(0);
    }

    //设置表格相关参数,因为着急就先这样了
    public void settable(final JTable detailtable){
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
                if(e.getButton() == MouseEvent.BUTTON3) {
                    focusrowindex = detailtable.rowAtPoint(e.getPoint());
                    if (focusrowindex == -1) {
                        return;
                    }
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





    }





}
