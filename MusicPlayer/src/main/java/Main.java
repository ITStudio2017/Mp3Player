import java.util.Random;

public class Main {
    public static void main(String[] args) {
        try {
            PlayerThread playerThread = new PlayerThread(4,PlayerThread.musicFileList,Pattern.Sequence);
            playerThread.start();
            while (true){
                System.out.println("总时长：" + playerThread.getMusicTime() + "秒");
                System.out.println("当前播放时间：" + playerThread.getNowMusicTime() + "秒");
                Thread.sleep(300);

            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
