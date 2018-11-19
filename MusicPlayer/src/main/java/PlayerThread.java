import javazoom.jl.decoder.JavaLayerException;

import java.io.*;

class PlayerThread extends Thread {
    private MyAdvancedPlayer myAdvancedPlayer;
    private String musicFilePath;
    private volatile boolean paused;
    private Object lock;
    private static int nowMusic;

    static final String[] musicFileList = {
            System.getProperty("user.dir") + "/downloads/test1.mp3",
            System.getProperty("user.dir") + "/downloads/test2.mp3",
            System.getProperty("user.dir") + "/downloads/test3.mp3",

    };

    public PlayerThread()
            throws JavaLayerException, FileNotFoundException {
        this.nowMusic = 0;
        this.lock = new Object();
        this.paused = false;
        this.musicFilePath = musicFileList[nowMusic];
        this.myAdvancedPlayer = new MyAdvancedPlayer(getInputStream(),lock);
    }

    public static int getNowMusic(){
        return nowMusic;
    }

    public static void setNowMusic(int nowMusi){
        nowMusic = nowMusi;
    }

    private InputStream getInputStream()
            throws FileNotFoundException {
        this.musicFilePath = musicFileList[nowMusic];
        return new BufferedInputStream(new FileInputStream(new File(musicFilePath)));
    }
    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }

    @Override
    public void run(){
        paused = false;
        while (!paused){
            synchronized (lock){
                int oldPosition = myAdvancedPlayer.getNowPosition();
                try {
                    if(nowMusic > musicFileList.length - 1)
                        break;
                    myAdvancedPlayer = new MyAdvancedPlayer(getInputStream(),lock);
                    myAdvancedPlayer.setNowPosition(oldPosition);
                    new Thread(myAdvancedPlayer).start();
                    lock.wait();
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