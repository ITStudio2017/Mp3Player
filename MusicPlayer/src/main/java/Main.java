import javazoom.jl.decoder.JavaLayerException;
import java.io.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

class Myplayer{
//    static Player player;
    static final String[] musicFileList = {
            "/Users/alex/Music/iTunes/iTunes Media/Music/毛不易/明日之子 第7期/消愁.mp3",
            "/Users/alex/Music/iTunes/iTunes Media/Music/买辣椒也用券/起风了（Cover 高橋優-《ヤキモチ》）/1-01 起风了（Cover 高橋優）.mp3",
    };
}

public class Main {
    public static void main(String[] args) {
        String sql = "insert into sheet (sheetName, createDate, image) " +
                "values ('我喜欢的音乐','2018-11-11','/Users/sss/');";
        try{
           SqliteTools.executor(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
