package cn.ktchen.player;

import cn.ktchen.download.DownloadFile;
import cn.ktchen.http.HttpTools;
import cn.ktchen.utils.DownloadUtils;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class PlayerThread implements Runnable {
    //播放模式 顺序、随机、单曲播放
    public enum  Pattern {Sequence, Stochastic, Single}

    private MyAdvancedPlayer myAdvancedPlayer;               //解码播放器
    private volatile boolean paused;                        //控制暂停
    private volatile Object lock;                           //线程锁
    private int startPositon;                               //起始位置
    private Pattern nowPattern;                             //当前播放模式
    //

    private int musicIndex;//当前播放单曲
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
        this.lock = new Object();
        this.paused = false;
        this.myAdvancedPlayer = null;
    }

    //获取播放流
    private InputStream getInputStream(){
        return getInputStream(0);
    }

    /**
     *
     * @param startPos 开始的位置
     * @return
     */
    private InputStream getInputStream(int startPos) {
        return getInputStream(startPos, true);
    }

    private InputStream getInputStream(int startPos, boolean autoDownload){
        HashMap<String, String> music = musicList.get(musicIndex);
        String filePath = music.get("filePath");
        InputStream inputStream = null;
        if(filePath == null){//根据提供的播放列表中有无filePath判断是否网络播放
            //查看本地音乐是否已下载
            filePath = HttpTools.downloadPath +  music.get("artist") +
                    "/" + music.get("album") + "/" + music.get("name") + "/";
            String fileName =  music.get("name") + " - " + music.get("artist");
            File musicFile = new File(filePath + fileName + ".mp3");
            if(musicFile.exists()) {//如果本地已下载
                try{
                    inputStream = new BufferedInputStream(new FileInputStream(musicFile));
                    inputStream.skip(startPos);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else{//本地未下载
                try{
                    URL url = new URL(HttpTools.getMusicUrl(music));
                    URLConnection conn = url.openConnection();
                    DownloadFile.setHeader(conn);
                    conn.setRequestProperty("RANGE","bytes=" + startPos + "-");
                    inputStream = conn.getInputStream();
                    if (autoDownload) {//开始下载
                        String musicUrl = HttpTools.getMusicUrl(music);
                        String imageUrl = HttpTools.getImageUrl(music);
                        HttpTools.makeParentFolder(filePath + fileName);
                        DownloadUtils.download(musicUrl, fileName + ".mp3", filePath, 5);
                        DownloadUtils.download(imageUrl, fileName + ".jpg", filePath, 5);
                        HttpTools.downloadLyric(music, filePath + fileName + ".lrc");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{//本地音乐
            try {
                inputStream = new BufferedInputStream(
                        new FileInputStream(
                                new File(
                                        musicList.get(musicIndex).get("filePath"))));

                inputStream.skip(startPos);
                return inputStream;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputStream;

    }

    public int getMusicIndex(){
        return musicIndex;
    }

    public void setMusicIndex(int nowMusic){
        musicIndex = nowMusic;
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


    //播放音乐
    private void play(){
        paused = false;
        while (!paused){
            synchronized (lock){

                //创建播放解码线程
                try {
                    myAdvancedPlayer = new MyAdvancedPlayer(getInputStream(startPositon),lock);
                    new Thread(myAdvancedPlayer).start();

                    //等待解码完毕
                    lock.wait();

                    //播放下一首
                    this.startPositon = 0;
                    this.musicIndex = autoNextMusicIndex();


                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取音乐播放总时长，mp3每帧26.122毫秒
    public double getMusicTime() {
        InputStream stream = null;
        Bitstream bitstream = null;
        Header header = null;
        int contentLength = 0;
        try{
            stream = getInputStream();
            contentLength = stream.available();
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
        Header header = myAdvancedPlayer.getHeader();
        return (startPositon / (header.framesize + 5) + myAdvancedPlayer.getNowFrame()) * header.ms_per_frame() /1000;
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
        new Thread(this).start();
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
    }

    //返回歌词路径
    public String getLrcPath(){
        HashMap<String, String> music = musicList.get(musicIndex);
        String LrcPath =  HttpTools.downloadPath + music.get("artist") + "/" + music.get("album") +
                "/" + music.get("name") + "/" + music.get("name") + " - " + music.get("artist") + ".lrc";
        File file = new File(LrcPath);
        if(file.exists()){
            return LrcPath;
        }else {
            return null;
        }

    }

    public String getImagePath(){
        HashMap<String, String> music = musicList.get(musicIndex);
        String imagePath =  HttpTools.downloadPath + music.get("artist") + "/" + music.get("album") +
                "/" + music.get("name") + "/" + music.get("name") + " - " + music.get("artist") + ".jpg";
        File file = new File(imagePath);
        if(file.exists()){
            return imagePath;
        }else {
            return null;
        }
    }

    /**
     *
     * @param seconds 根据时间找到起始位置
     */
    public void setTime(int seconds){
        if(myAdvancedPlayer != null){
            this.pause();
        }
        try{
            InputStream stream = getInputStream(0,false);
            int lenght = stream.available();
            Bitstream bitstream = new Bitstream(stream);
            Header header = bitstream.readFrame();
            //先求得帧数，再算出位置
            int frameNum = (int)(seconds * 1000 / header.ms_per_frame());
            int frameSize = header.framesize + 5;
            startPositon = frameNum * frameSize;
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run(){
        this.play();
    }
}