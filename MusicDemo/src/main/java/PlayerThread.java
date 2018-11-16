import javazoom.jl.decoder.JavaLayerException;

import java.io.*;

class PlayerThread extends Thread {
    private MyAdvancedPlayer myAdvancedPlayer;
    private String musicFilePath;
    private InputStream getInputStream() throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(new File(musicFilePath)));
    }
    public PlayerThread(String musicFilePath) throws JavaLayerException, FileNotFoundException {
        this.musicFilePath = musicFilePath;
        this.myAdvancedPlayer = new MyAdvancedPlayer(getInputStream());
    }
    public void startMusic() throws JavaLayerException, FileNotFoundException {
        int oldPosition = myAdvancedPlayer.getNowPosition();
        myAdvancedPlayer = new MyAdvancedPlayer(getInputStream());
        myAdvancedPlayer.setNowPosition(oldPosition);
        new Thread(myAdvancedPlayer).start();
    }
    public void pause(){
        myAdvancedPlayer.setClosed();
    }
    public void restart() throws JavaLayerException, FileNotFoundException {
        this.myAdvancedPlayer = new MyAdvancedPlayer(getInputStream());
        new Thread(myAdvancedPlayer).start();
    }
}