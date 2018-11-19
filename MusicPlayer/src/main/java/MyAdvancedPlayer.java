import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.InputStream;

public class MyAdvancedPlayer extends AdvancedPlayer implements Runnable {
    private int nowPosition;//当前解码的帧的位置
    private volatile boolean close;//控制解码停止与否
    private boolean frameEnd;//判断解码是否完成，即播放完毕
    private Object lock;//锁

    //初始化
    MyAdvancedPlayer(InputStream in, Object lock) throws JavaLayerException {
        super(in);
        this.lock = lock;
        nowPosition = 0;
        close = true;
        frameEnd = false;
    }

    //解码
    protected boolean decodeFrame() throws JavaLayerException {
        nowPosition++;
        return super.decodeFrame();
    }

    //获取帧位置
    public int getNowPosition(){

        return nowPosition;
    }

    //设置帧位置
    public void setNowPosition(int nowPosition){

        this.nowPosition = nowPosition;
    }

    //关闭或开启解码
    public void setClosed(boolean close){
        this.close = close;
    }

    public void run(){
        this.frameEnd = false;
        this.close = false;
        for (int i = 0; i < nowPosition; i++) {
            try {
                super.skipFrame();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }
        synchronized (lock) {
            while(this.close == false && !frameEnd) {
                try {
                    frameEnd = !this.decodeFrame();
                    if (frameEnd == true){
                        this.nowPosition = 0;
                        this.close = true;
                        PlayerThread.setNowMusic(PlayerThread.getNowMusic()+1);
                        lock.notify();
                    }
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}