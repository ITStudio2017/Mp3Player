import java.util.HashMap;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        Vector<HashMap<String, String>> musicList =
                HttpTools.search("可能否",1,
                        HttpTools.Sources.netease.toString());

        if(HttpTools.downloadMusic(musicList.get(0))){
            System.out.println("下载成功");
        }


    }

}
