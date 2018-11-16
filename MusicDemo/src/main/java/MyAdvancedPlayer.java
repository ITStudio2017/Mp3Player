import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.InputStream;

public class MyAdvancedPlayer extends AdvancedPlayer implements Runnable {
    private int nowPosition;
    private boolean closed;
    MyAdvancedPlayer(InputStream in) throws JavaLayerException {
        super(in);
        nowPosition = 0;
        closed = true;
    }

    protected boolean decodeFrame() throws JavaLayerException {
        nowPosition++;
        return super.decodeFrame();
    }
    public int getNowPosition(){
        return nowPosition;
    }
    public void setNowPosition(int nowPosition){
        this.nowPosition = nowPosition;
    }
    public void setClosed(){
        this.closed = !closed;
        super.close();
    }
    public void run(){
        boolean ret = true;
        this.closed = false;
        for (int i = 0; i < nowPosition; i++) {
            try {
                super.skipFrame();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }
        while(this.closed == false && ret) {
            try {
                ret = this.decodeFrame();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }

}