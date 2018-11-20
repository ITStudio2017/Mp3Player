import java.util.HashMap;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        Vector<HashMap<String,String>> musicList = SqliteTools.getMusicBySheet(4);
        for (HashMap<String,String> music: musicList
             ) {
            System.out.println("id:" + music.get("id") + " filePath:" + music.get("filePath"));
        }
    }

}
