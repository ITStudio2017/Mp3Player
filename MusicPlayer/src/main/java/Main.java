import javazoom.jl.decoder.JavaLayerException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) {
        try {
            PlayerThread playerThread = new PlayerThread();
            while (true) {
                try{
                    new Thread(playerThread).start();
                    Thread.sleep(4000);
                    System.out.println("暂停");
                    playerThread.pause();
                    Thread.sleep(1000);
                    System.out.println("继续播放");
                }catch (Exception e){
                    break;
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
