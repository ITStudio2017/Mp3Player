import javazoom.jl.decoder.JavaLayerException;
import java.io.*;
import java.util.concurrent.TimeUnit;

class Myplayer implements Runnable{
//    static Player player;
    static MyAdvancedPlayer player;
    static final String[] musicFileList = {
            "/Users/alex/Music/iTunes/iTunes Media/Music/毛不易/明日之子 第7期/消愁.mp3",
            "/Users/alex/Music/iTunes/iTunes Media/Music/买辣椒也用券/起风了（Cover 高橋優-《ヤキモチ》）/1-01 起风了（Cover 高橋優）.mp3",
    };
    Myplayer(String musicFilePath) throws FileNotFoundException, JavaLayerException {
        player = new MyAdvancedPlayer(new BufferedInputStream(new FileInputStream(new File(musicFilePath))));
    }
    public void play() {
        try {
            player.play();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        player.close();
    }

    public void run() {

        play();
    }
}

public class Main {
    public static void main(String[] args) throws IOException, JavaLayerException, InterruptedException {
        String in = Myplayer.musicFileList[0];
        PlayerThread playerThread = new PlayerThread(in);
        for (int i = 0; i < 5; i++) {
            System.out.println("播放中...");
            playerThread.startMusic();
            TimeUnit.SECONDS.sleep(5);
            playerThread.pause();
            System.out.println("暂停中...");
            TimeUnit.SECONDS.sleep(2);
        }
        playerThread.restart();
    }

}
