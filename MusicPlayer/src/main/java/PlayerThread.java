import javazoom.jl.decoder.JavaLayerException;

import java.io.*;
import java.util.Random;

enum  Pattern {Sequence, Stochastic, Single};        //播放模式 顺序、随机、单曲播放
class PlayerThread extends Thread {
    private MyAdvancedPlayer myAdvancedPlayer;          //解码播放器
    private volatile boolean paused;                    //控制暂停
    private Object lock;                                //线程锁
    private int frameSum;                               //帧总数
    Pattern nowPattern;                                 //当前播放模式

    private int nowMusic = Integer.MIN_VALUE;//当前播放单曲
    public static String[] musicFileList = {
            System.getProperty("user.dir") + "/downloads/test1.mp3",    //春夏秋冬的您
            System.getProperty("user.dir") + "/downloads/test2.mp3",    //可能否
            System.getProperty("user.dir") + "/downloads/test3.mp3",    //山外小楼夜听雨
            System.getProperty("user.dir") + "/downloads/周杰伦/周杰伦的床边故事/告白气球/告白气球 - 周杰伦.mp3",    //告白气球
            System.getProperty("user.dir") + "/downloads/Alan Walker/Darkside/Darkside/Darkside - Alan Walker.mp3",    //Darkside

    };

    //初始化
    public PlayerThread()
            throws JavaLayerException, FileNotFoundException {
        if(nowMusic == Integer.MIN_VALUE){
            //设置当前音乐默认值
            nowMusic = 0;
        }
        if(nowPattern == null){
            //默认随机播放
            nowPattern = Pattern.Sequence;
        }
        this.lock = new Object();
        this.paused = false;
        this.myAdvancedPlayer = new MyAdvancedPlayer(getInputStream(),lock);
    }

    //有参初始化
    public PlayerThread(int nowMusic, String[] musicFileList)
            throws FileNotFoundException, JavaLayerException {
        this();
        this.nowMusic = nowMusic;
        this.musicFileList = musicFileList;
    }

    //有参初始化
    public PlayerThread(int nowMusic, String[] musicFileList,Pattern p)
            throws FileNotFoundException, JavaLayerException {
        this(nowMusic,musicFileList);
        nowPattern = p;
    }


    public int getNowMusic(){
        return nowMusic;
    }

    public void setNowMusic(int nowMusi){
        nowMusic = nowMusi;
    }

    public void setNowPattern(Pattern p){
        this.nowPattern = p;
    }

    public void nextMusic(){
        //判断播放模式
        switch (nowPattern){
            //顺序播放
            case Sequence:nowMusic++;
                if(nowMusic > musicFileList.length - 1){
                    //如果越界
                    nowMusic = 0;
                }
                break;
            //随机播放
            case Stochastic:
                nowMusic = new Random().nextInt(musicFileList.length);
                break;
            case Single:
                break;
            default:
                nowMusic = 0;
        }
    }

    private int getFrameSum() throws JavaLayerException, FileNotFoundException {
        frameSum = 0;
        MyAdvancedPlayer myAdvancedPlayer = new MyAdvancedPlayer(
                (getInputStream()),new Object());
        while (myAdvancedPlayer.skipFrame())
            frameSum++;
        return frameSum;
    }

    //获取音乐播放总时长，mp3每帧26.122毫秒
    public double getMusicTime(){
        return 26.122 * frameSum / 1000;
    }

    //获取当前播放时间
    public double getNowMusicTime(){
        return this.myAdvancedPlayer.getNowFrame() * 26.122 /1000;
    }


    private InputStream getInputStream()
            throws FileNotFoundException {
        return new BufferedInputStream(
                new FileInputStream(
                        new File(
                                musicFileList[nowMusic])));
    }

    @Override
    public void run(){
        paused = false;
        while (!paused){
            synchronized (lock){

                //创建播放解码线程
                int oldPosition = myAdvancedPlayer.getNowFrame();
                try {
                    myAdvancedPlayer = new MyAdvancedPlayer(getInputStream(),lock);
                    myAdvancedPlayer.setNowFrame(oldPosition);
                    this.frameSum = getFrameSum();
                    new Thread(myAdvancedPlayer).start();

                    //等待解码完毕
                    lock.wait();

                    //播放下一首
                    this.nextMusic();


                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pause(){
        this.paused = true;
        myAdvancedPlayer.setClosed(true);
    }
}