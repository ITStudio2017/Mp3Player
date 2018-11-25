package cn.ktchen.player;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.InputStream;

public class MyAdvancedPlayer extends AdvancedPlayer implements Runnable {
    private volatile int nowFrame;                  //当前解码的帧的位置
    private volatile boolean close;                 //控制解码停止与否
    private boolean isEnd;                          //判断解码是否完成，即播放是否完毕
    private volatile Object lock;                   //锁

    private Header header = null;

    public Header getHeader() {
        return header;
    }

    //初始化
    public MyAdvancedPlayer(InputStream in, Object lock) throws JavaLayerException {
        super(in);
        Bitstream bitstream = new Bitstream(in);
        header = bitstream.readFrame();
        this.lock = lock;
        nowFrame = 0;
        close = true;
        isEnd = false;
    }

    @Override
    public boolean skipFrame() throws JavaLayerException {
        return super.skipFrame();
    }

    //解码
    protected boolean decodeFrame() throws JavaLayerException {
        nowFrame++;
        return super.decodeFrame();
    }

    //获取帧位置
    public int getNowFrame(){
        return nowFrame;
    }

    //设置帧位置
    public void setNowFrame(int nowFrame){
        this.nowFrame = nowFrame;
    }

    //关闭或开启解码
    public void setClosed(boolean close){
        this.close = close;
        super.close();
    }

    public void run(){
        this.isEnd = false;
        this.close = false;

        synchronized (lock) {
            while(!close && !isEnd) {
                try {
                    isEnd = !this.decodeFrame();
                    if (isEnd){
                        //播放完毕，停止解码、设置帧位置为0，通知控制线程，释放锁
                        this.nowFrame = 0;
                        this.close = true;
                        lock.notify();
                    }
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("结束了😂");
        }
    }

}