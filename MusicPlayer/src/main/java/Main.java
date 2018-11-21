import javazoom.jl.decoder.JavaLayerException;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, JavaLayerException, InterruptedException {
        PlayerThread playerThread = new PlayerThread(4,PlayerThread.musicFileList,Pattern.Sequence);
        playerThread.start();
        while (true){
            System.out.println("音乐名：" + playerThread.musicFileList[playerThread.getNowMusic()]);
            System.out.println("总时长：" + playerThread.getMusicTime());
            System.out.println("当前播放时间：" + playerThread.getNowMusicTime());
            Thread.sleep(400);
        }

    }

}
