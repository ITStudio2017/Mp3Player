package cn.ktchen.player;

import cn.ktchen.download.DownloadFile;
import cn.ktchen.download.DownloadSingle;
import cn.ktchen.http.HttpTools;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class PlayerThread implements Runnable {
    //播放模式 顺序、随机、单曲播放
    public enum  Pattern {Sequence, Stochastic, Single}

    private MyAdvancedPlayer myAdvancedPlayer;                  //解码播放器
    private final Object playerLock;                            //线程锁
    private final Object fileDownloadLock;                      //音乐文件下载锁
    private final Object imageDownloadLock;                     //图片文件下载锁
    private Thread playThread;
    private Thread AdvancedPlayerThread;

    public Thread getPlayThread() {
        return playThread;
    }

    public void setPlayThread(Thread playThread) {
        this.playThread = playThread;
    }

    private int startPosition;                                  //起始位置
    private Pattern nowPattern;                                 //当前播放模式

    public boolean isPaused() {
        return isPaused;
    }

    private volatile boolean isPaused;                          //当前是否已暂停
    private int musicIndex;                                     //当前播放单曲

    public Vector<HashMap<String, String>> getMusicList() {
        return musicList;
    }

    public void setMusicList(Vector<HashMap<String, String>> musicList) {
        this.musicList = musicList;
    }

    private  Vector<HashMap<String,String>> musicList;

    //初始化
    public PlayerThread() {
        this(0,null,Pattern.Sequence);
    }

    //有参初始化
    public PlayerThread(int musicIndex, Vector<HashMap<String, String>> musicList) {
        this(musicIndex, musicList, Pattern.Sequence);

    }

    //有参初始化

    /**
     *
     * @param musicIndex    当前要播放的音乐在musicList的索引
     * @param musicList     音乐播放列表
     * @param p             播放模式
     */
    public PlayerThread(int musicIndex, Vector<HashMap<String, String>> musicList,Pattern p) {
        this.musicIndex =  musicIndex;
        this.musicList = musicList;
        this.nowPattern = p;
        this.isPaused = false;
        this.myAdvancedPlayer = null;
        this.playerLock = new Object();
        this.fileDownloadLock = new Object();
        this.imageDownloadLock = new Object();
    }

    //检查本地是否有音乐文件
    private boolean musicExists(HashMap<String,String> music){
        String filePath = HttpTools.filePath(music);
        String fileName =  music.get("name") + " - " + music.get("artist");
        File musicFile = new File(filePath + fileName + ".mp3");
        return musicFile.exists();
    }

    //获取播放流
    private InputStream getInputStream(){
        return getInputStream(0);
    }

    /**
     *
     * @param startPos 开始的位置
     * @return 返回流
     */
    private InputStream getInputStream(int startPos) {
        return getInputStream(startPos, true);
    }

    private InputStream getInputStream(int startPos, boolean autoDownload){
        HashMap<String, String> music = null;
        try {
            music = musicList.get(musicIndex);
        }catch (Exception e){
            return null;
        }
        String filePath = HttpTools.filePath(music);
        String fileName = HttpTools.fileName(music);
        InputStream inputStream = null;

        //查看本地音乐是否已下载
        if(musicExists(music)) {//如果本地已下载
            try{
                inputStream = new BufferedInputStream(new FileInputStream(filePath+fileName+".mp3"));
                inputStream.skip(startPos);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{//本地未下载
            try{
                String musicUrl = HttpTools.getMusicUrl(music);

                //url为空返回null
                if (musicUrl == null || musicUrl.equals(""))
                    return null;

                URL url = new URL(musicUrl);
                URLConnection conn = url.openConnection();
                DownloadFile.setHeader(conn);
                conn.setRequestProperty("RANGE","bytes=" + startPos + "-");
                inputStream = conn.getInputStream();
                if (autoDownload) {//开始下载
                    String imageUrl = HttpTools.getImageUrl(music);
                    new DownloadSingle(musicUrl, filePath + fileName + ".mp3", fileDownloadLock).start();
                    new DownloadSingle(imageUrl, filePath + fileName + ".jpg", imageDownloadLock).start();
                    HttpTools.downloadLyric(music, filePath + fileName + ".lrc");
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    public int getMusicIndex(){
        return musicIndex;
    }

    public void setMusicIndex(int musicIndex){
        this.musicIndex = musicIndex;
    }

    public Pattern getNowPattern(){
        return nowPattern;
    }

    public void setNowPattern(Pattern p){
        this.nowPattern = p;
    }

    //自动播放结束后返回下首歌索引，根据播放模式确定
    private int autoNextMusicIndex(){
        if (musicList == null)
            return 0;
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


    //播放音乐
    private void play(){
        isPaused = false;
        while (!isPaused){
            synchronized (playerLock){

                //创建播放解码线程
                try {
                    InputStream inputStream = getInputStream(startPosition);
                    if(inputStream != null){
                        myAdvancedPlayer = new MyAdvancedPlayer(inputStream, playerLock);
                        this.AdvancedPlayerThread = new Thread(myAdvancedPlayer);
                        AdvancedPlayerThread.start();
                        //等待解码完毕
                        playerLock.wait();
                    }

                    //播放下一首
                    this.startPosition = 0;
                    this.musicIndex = autoNextMusicIndex();


                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getInputStreamLength(){
        int length = 0;
        HashMap<String,String> music = this.musicList.get(musicIndex);
        if (musicExists(music)) {//本地文件
            try {
                length = new BufferedInputStream(new FileInputStream(new File(
                        HttpTools.filePath(music) + HttpTools.fileName(music) + ".mp3"
                ))).available();
            }catch (Exception e){
                e.printStackTrace();
                length = 0;
            }
        }else{//网络文件
            try{
                URL url = new URL(HttpTools.getMusicUrl(music));
                URLConnection conn = url.openConnection();
                length = conn.getContentLength();
            }catch (Exception e){
                e.printStackTrace();
                length = 0;
            }
        }
        return length;
    }

    //获取音乐播放总时长
    public double getMusicTime() {
        InputStream stream = null;
        Bitstream bitstream = null;
        Header header = null;
        int contentLength = 0;
        try{
            stream = getInputStream();

            //流为空返回0
            if (stream == null)
                return 0.0;
            contentLength = getInputStreamLength();
            bitstream = new Bitstream(stream);
            header = bitstream.readFrame();
            stream.close();
            bitstream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return header.total_ms(contentLength) / 1000;
    }

    //获取当前播放时间
    public double getNowMusicTime(){
        try{
            Header header = myAdvancedPlayer.getHeader();
            int frameSize = isPaused ? (startPosition / (header.framesize + 5)) : (startPosition / (header.framesize + 5) + myAdvancedPlayer.getNowFrame());
            return  frameSize * header.ms_per_frame() / 1000;
        }catch (NullPointerException e){
            return 0;
        }
    }

    //根据时间计算文件的起始读取点
    private int getStartPosition(double seconds) {
        int position = 0;
        InputStream stream = getInputStream(0,false);
        if (stream != null){
            try{
                Bitstream bitstream = new Bitstream(stream);
                Header header = bitstream.readFrame();
                //先求得帧数，再算出位置
                int frameNum = (int)(seconds * 1000 / header.ms_per_frame());
                int frameSize = header.framesize + 5;
                position = frameNum * frameSize;
            }catch (BitstreamException e){
                e.printStackTrace();
            }
        }
        return position;
    }

    //停止播放
    public void stop(){
        startPosition = 0;
        this.isPaused = true;
        if (myAdvancedPlayer != null){
            myAdvancedPlayer.setClosed(true);
            this.myAdvancedPlayer.setNowFrame(0);
        }
        this.musicIndex = 0;
        this.musicList = null;
        try {
            this.playThread.stop();
            this.AdvancedPlayerThread.stop();
        }catch (NullPointerException e){
        }

    }

    //暂停播放
    public void pause(){
        startPosition = getStartPosition(getNowMusicTime());
        this.isPaused = true;
        if (myAdvancedPlayer != null)
            myAdvancedPlayer.setClosed(true);
        try {
            this.playThread.stop();
            this.AdvancedPlayerThread.stop();
        }catch (NullPointerException e){
        }
    }

    public void indexMusic(int index){
        this.pause();
        this.startPosition = 0;
        this.musicIndex = index;
        if (this.myAdvancedPlayer != null)
            this.myAdvancedPlayer.setNowFrame(0);
        this.playThread = new Thread(this);
        playThread.start();
    }

    //下一首歌
    public void nextMusic(){
        this.pause();
        this.startPosition = 0;
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
        this.playThread = new Thread(this);
        playThread.start();
    }

    //上一首歌
    public void previousMusic() {
        this.pause();
        this.startPosition = 0;
        switch (nowPattern) {
            case Sequence:
            case Single:
                this.musicIndex--;
                if (musicIndex < 0)
                    musicIndex = musicList.size() - 1;
                break;
            case Stochastic:
                this.musicIndex = new Random().nextInt(musicList.size());
                break;
            default:
                    this.musicIndex = 0;
        }
        this.myAdvancedPlayer.setNowFrame(0);
        this.playThread = new Thread(this);
        playThread.start();
    }

    private void checkExist(boolean block, File file, long milliseconds){
        long startMillisecond = System.currentTimeMillis();
        while (block){
            try {
                if (file.exists())
                    break;
                Thread.sleep(100);
                long endMillisecond = System.currentTimeMillis();
                if (endMillisecond - startMillisecond >= milliseconds)
                    break;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //返回歌词路径
    public String getLrcPath(){
        return getLrcPath(false);
    }
    private String getLrcPath(boolean block){
        return getLrcPath(block,Integer.MAX_VALUE);
    }

    private String getLrcPath(boolean block, long milliseconds){
        HashMap<String, String> music = musicList.get(musicIndex);
        String LrcPath = HttpTools.filePath(music) + HttpTools.fileName(music) + ".lrc";
        File file = new File(LrcPath);
        //阻塞则循环到文件存在
        checkExist(block, file, milliseconds);

        if (file.exists())
            return LrcPath;
        else
            return null;

    }

    //获取封面路径
    public String getImagePath(){
        return getImagePath(false);
    }
    public String getImagePath(boolean block){
        return getImagePath(block,Integer.MAX_VALUE);
    }
    public String getImagePath(boolean block, long milliseconds){
        HashMap<String, String> music = musicList.get(musicIndex);
        String imagePath = HttpTools.filePath(music) + HttpTools.fileName(music) + ".jpg";
        File file = new File(imagePath);
        //阻塞则循环到文件存在
        checkExist(block, file, milliseconds);
        if (file.exists())
            return imagePath;
        else
            return null;
    }

    /**
     *
     * @param seconds 根据时间找到起始位置
     */
    public void setTime(int seconds){
        setTime(seconds, false);
    }
    public void setTime(int seconds, boolean autoPlay){
        if (seconds < 0)
            seconds = 0;
        if(myAdvancedPlayer != null){
            this.pause();
        }
        startPosition = getStartPosition(seconds);
        if (autoPlay)
            new Thread(this).start();
    }

    public void run(){
        this.play();
    }
}