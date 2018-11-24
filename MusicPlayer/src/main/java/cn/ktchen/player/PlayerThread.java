package cn.ktchen.player;

import javazoom.jl.decoder.JavaLayerException;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

enum  Pattern {Sequence, Stochastic, Single}        //播放模式 顺序、随机、单曲播放
class PlayerThread extends Thread {
    private MyAdvancedPlayer myAdvancedPlayer;          //解码播放器
    private volatile boolean paused;                    //控制暂停
    private final Object lock;                          //线程锁
    private int frameSum;                               //帧总数
    private Pattern nowPattern;                         //当前播放模式

    private int musicIndex;//当前播放单曲
    private  Vector<HashMap<String,String>> musicList;

    //初始化
    public PlayerThread()
            throws JavaLayerException {
        this(0,null,Pattern.Sequence);
    }

    //有参初始化
    public PlayerThread(int musicIndex, Vector<HashMap<String, String>> musicList)
            throws JavaLayerException {
        this(musicIndex, musicList, Pattern.Sequence);

    }

    //有参初始化
    public PlayerThread(int musicIndex, Vector<HashMap<String, String>> musicList,Pattern p)
            throws JavaLayerException {
        this.musicIndex =  musicIndex;
        this.musicList = musicList;
        this.nowPattern = p;
        this.lock = new Object();
        this.paused = false;
        this.myAdvancedPlayer = new MyAdvancedPlayer(getInputStream(),lock);
    }

    //获取播放流
    private InputStream getInputStream() {
        try {
            return new BufferedInputStream(
                    new FileInputStream(
                            new File(
                                    musicList.get(musicIndex).get("filePath"))));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public int getMusicIndex(){
        return musicIndex;
    }

    public void setMusicIndex(int nowMusi){
        musicIndex = nowMusi;
    }

    public void setNowPattern(Pattern p){
        this.nowPattern = p;
    }

    //自动播放结束后返回下首歌索引，根据播放模式确定
    private int autoNextMusicIndex(){
        //判断播放模式
        switch (nowPattern){
            //顺序播放
            case Sequence:
                this.musicIndex++;
                if(musicIndex > musicList.size() - 1){
                    //如果越界
                    return 0;
                }
                return this.musicIndex;
            //随机播放
            case Stochastic:
                return new Random().nextInt(musicList.size());
            case Single:
                return this.musicIndex;
            default:
                return 0;
        }
    }

    private int getFrameSum() throws JavaLayerException {
        frameSum = 0;
        MyAdvancedPlayer myAdvancedPlayer = new MyAdvancedPlayer(
                (getInputStream()),new Object());
        while (myAdvancedPlayer.skipFrame())
            frameSum++;
        return frameSum;
    }

    //播放音乐
    private void play(){
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
                    this.musicIndex = autoNextMusicIndex();


                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取音乐播放总时长，mp3每帧26.122毫秒
    public double getMusicTime(){
        return 26.122 * frameSum / 1000;
    }

    //获取当前播放时间
    public double getNowMusicTime(){
        return this.myAdvancedPlayer.getNowFrame() * 26.122 /1000;
    }

    //暂停播放
    public void pause(){
        this.paused = true;
        myAdvancedPlayer.setClosed(true);
    }

    //下一首歌
    public void nextMusic(){
        this.pause();

        switch (nowPattern) {
            case Sequence:
            case Single:
                this.musicIndex++;
                //歌单播放完毕，重新开始
                if(musicIndex > musicList.size() - 1)
                    musicIndex = 0;
                break;
            case Stochastic:
                this.musicIndex = new Random().nextInt(musicList.size());
                break;
            default:
                this.musicIndex = 0;

        }
        this.myAdvancedPlayer.setNowFrame(0);

        //线程未开启则启动
        if(!this.isAlive())
            this.start();
    }

    //上一首歌
    public void previousMusic() {
        this.pause();
        switch (nowPattern) {
            case Sequence:
            case Single:
                this.musicIndex--;
                if (musicIndex < 0)
                    musicIndex = musicList.size() - 1;
            case Stochastic:
                this.musicIndex = new Random().nextInt(musicList.size());
            default:
                    this.musicIndex = 0;
        }
        this.myAdvancedPlayer.setNowFrame(0);
        if (!this.isAlive())
            this.start();
    }

    @Override
    public void run(){
        this.play();
    }
}