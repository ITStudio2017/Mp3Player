package cn.ktchen.sqlite;

import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SqliteTools {
    static Connection conn = null;
    static Statement stmt = null;
    static String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/SQLite3/MyMusic.db";

    //创建表
    public static void createTables() {
        String createMusicTable = "CREATE TABLE \"music\" (\n" +
                "  \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"name\" TEXT(100) NOT NULL,\n" +
                "  \"hash\" TEXT(100),\n" +
                "  \"filePath\" TEXT NOT NULL,\n" +
                "  \"sheetID\" INTEGER NOT NULL,\n" +
                "  \"musicImage\" TEXT(200),\n" +
                "  \"lyric\" TEXT(1000),\n" +
                "  \"artist\" TEXT(30),\n" +
                "  \"album\" TEXT(50),\n" +
                "  CONSTRAINT \"sheetID\" FOREIGN KEY (\"sheetID\") REFERENCES \"sheet\" (\"id\") ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                ")";
        String createSheetTable = "CREATE TABLE \"sheet\" (\n" +
                "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"name\" text(100) NOT NULL,\n" +
                "  \"createDate\" text(20) NOT NULL,\n" +
                "  \"owner\" TEXT(11) NOT NULL DEFAULT 17020031002,\n" +
                "  \"imagePath\" text(200) NOT NULL\n" +
                ")";
        try {
            executor(createSheetTable);
            executor(createMusicTable);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建表失败");
        }

    }

    //创建歌单
    public static void createSheet(String name) {
        createSheet(name, null);
    }

    public static void createSheet(String name, String imagePath) {
        //默认是我自己😄
        createSheet(name, imagePath, "17020031002");
    }

    public static void createSheet(String name, String imagePath, String owner) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", name);
        hashMap.put("createDate", dateString);
        hashMap.put("owner", owner);
        hashMap.put("imagePath", imagePath);
        insert("sheet", hashMap);
    }

    //获取歌单
    public static Vector<HashMap<String, String>> getSheetList() {
        Vector<HashMap<String, String>> sheetList = new Vector<HashMap<String, String>>();
        String sql = "select * from sheet";
        try {
            connect();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", rs.getObject("id").toString());
                hashMap.put("name", rs.getObject("name").toString());
                hashMap.put("createDate", rs.getObject("createDate").toString());
                hashMap.put("owner", rs.getObject("owner").toString());
                hashMap.put("imagePath", rs.getObject("imagePath").toString());
                sheetList.add(hashMap);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return sheetList;
        }

    }

    //修改歌单名
    public static void updateSheetName(int id, String name) {
        String sql = "update sheet set name = \"" + name + "\" where id = " + id;
        try {
            executor(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("修改歌单名失败");
        }
    }

    //修改歌单封面
    public static void updateSheetImage(int id, String imagePage) {
        String sql = "update sheet set imagePath = \"" + imagePage + "\" where id = " + id;
        try {
            executor(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("更新歌单封面失败");
        }
    }

    //删除歌单
    public static void deleteSheet(int id) {
        String sql = "delete from sheet where id = " + id;
        try {
            executor(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除歌单失败");
        }
    }

    //根据歌单查找音乐
    public static Vector<HashMap<String, String>> getMusicBySheet(HashMap<String, String> sheet) {
        Vector<HashMap<String, String>> musicList = new Vector<HashMap<String, String>>();
        String sql = "select * from music where sheetID = " +
                sheet.get("id") + " order by id desc ";
        try {
            connect();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                HashMap<String, String> music = new HashMap<String, String>();
                music.put("id", rs.getObject("id").toString());
                music.put("name", rs.getObject("name").toString());
                music.put("hash", rs.getObject("hash").toString());
                music.put("filePath", rs.getObject("filePath").toString());
                music.put("sheetID", rs.getObject("sheetID").toString());
                music.put("musicImage", rs.getObject("musicImage").toString());
                music.put("lyric", rs.getObject("lyric").toString());
                music.put("artist", rs.getObject("artist").toString());
                music.put("album", rs.getObject("album").toString());
                musicList.add(music);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicList;
    }

    //创建音乐
    public static void createMusic(String name, String filePath,
                                   int sheetID, String musicImage,
                                   String lyric, String artist, String album) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", name);
        hashMap.put("filePath", filePath);
        hashMap.put("sheetID", Integer.toString(sheetID));
        hashMap.put("musicImage", musicImage);
        hashMap.put("lyric", lyric);
        hashMap.put("artist", artist);
        hashMap.put("album", album);
        insert("music", hashMap);

    }

    //删除音乐
    public static void deleteMusic(HashMap<String, String> music) {
        String sql = "delete from music where id = " + music.get("id");
        try {
            executor(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除音乐失败");
        }

    }

    //检查音乐是否在本地,判断标准为音乐名、歌手、专辑是否相同
    public static boolean musicExist(HashMap<String, String> music) {
        String sql = "select * from music where name = \"" +
                music.get("name") + "\" and artist = \"" + music.get("artist") +
                "\" and album = \"" + music.get("album") + "\"";
        try {
            connect();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                rs.close();
                stmt.close();
                conn.close();
                return true;
            }else {
                rs.close();
                stmt.close();
                conn.close();
                return false;
            }
        }catch (Exception e) {
           e.printStackTrace();
       }
       return false;

    }

    public static void insert(String tableName, HashMap<String,String> hashMap){
        String column = "(";
        String values = "(";
        for (Map.Entry<String,String> entry: hashMap.entrySet()
             ) {
            column += (entry.getKey() + ",");
            values += ("\"" + entry.getValue() + "\",");
        }
        column = column.substring(0, column.length() - 1) + ")";
        values = values.substring(0, values.length() - 1) + ")";
        String sql = "insert into " + tableName + " " + column + " values " + values;
        try {
            executor(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void checkDB(){
        String DBpath = System.getProperty("user.dir") + "/SQLite3/MyMusic.db";
        File file = new File(DBpath);
        if(!file.exists()){
            //数据库不存在则创建表
            createTables();
        }
    }

    //连接数据库
    public static void connect() {
        try{
            checkDB();//检查数据库文件
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("database connected error");
        }

    }

    //执行增加、删除、更新sql语句
    public static void executor(String sql) {
        connect();
        try{
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("sql statement error");
        }
    }

}
