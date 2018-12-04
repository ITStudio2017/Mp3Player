package cn.ktchen.cmdPlayer;

import cn.ktchen.sqlite.SqliteTools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import static cn.ktchen.cmdPlayer.playControl.getInt;

public class SheetManager {

    private static boolean isquit;

    //退出
    private static void quit(){
        System.out.println("------Exit music manager mode------");
        isquit = true;
    }

    //帮助
    private static void help() {
        System.out.printf("%-15s %-10s %s\n",   "Command",  "Abbr", "Description");
        System.out.printf("%-15s %-10s %s\n",   "-------",  "----", "-----------");
        System.out.printf("%-15s %-10s %s\n",   "sheet",    "s",    "View all music sheet");
        System.out.printf("%-15s %-10s %s\n",   "music",    "m",    "View the list of music under the music sheet");
        System.out.printf("%-15s %-10s %s\n",   "create",   "c",    "Create music sheet");
        System.out.printf("%-15s %-10s %s\n",   "deleteSheet","ds", "Delete music sheet");
        System.out.printf("%-15s %-10s %s\n",   "deleteMusic","dm", "Delete the song");
        System.out.printf("%-15s %-10s %s\n",   "quit",     "q",    "Exit music manager mode");
        System.out.printf("%-15s %-10s %s\n",   "help",     "h",    "View help");
    }

    //删除音乐
    private static void deleteMusic(){
        printAllSheet();
        System.out.println("Please choose a sheet by ID");
        System.out.print(">>>");
        Vector<HashMap<String, String>> list = SqliteTools.getSheetList();
        int index = getInt(list.size());
        printMusicList(list.get(index));
        list = SqliteTools.getMusicBySheet(list.get(index));
        if (list.size() == 0){
            System.out.println("The sheet of your chosen non music!");
            return;
        }
        System.out.println("Please enter song ID to delete");
        System.out.print(">>>");
        index = getInt(list.size());
        SqliteTools.deleteMusic(list.get(index));
        System.out.println("delete successfully");
    }

    //打印全部歌单信息
    static void printAllSheet(){
        Vector<HashMap<String, String>> sheetList = SqliteTools.getSheetList();
        System.out.printf("%-3s%-13s%-30s\n", "ID", "CreateDate", "Name");
        for (int i = 0; i < sheetList.size(); ++i) {
            HashMap<String, String> sheet = sheetList.get(i);
            System.out.printf("%-3s%-13s%-30s\n", i, sheet.get("createDate"), sheet.get("name"));
        }
    }

    //打印歌单中歌曲信息
    static void printMusicList(HashMap<String, String> sheet){
        Vector<HashMap<String, String>> musicList = SqliteTools.getMusicBySheet(sheet);
        System.out.printf("%-3s %-20s %-10s %-10s\n", "ID", "Name", "Artist", "Album");
        for (int i = 0; i < musicList.size(); ++i) {
            HashMap<String, String> music = musicList.get(i);
            System.out.printf("%-3s %-20s %-10s %-10s\n", i, music.get("name"), music.get("artist"), music.get("album"));
        }
        System.out.println();
    }

    public static void music(){
        Vector<HashMap<String, String>> list;

        //打印歌单信息
        SheetManager.printAllSheet();

        //获取歌单索引
        list = SqliteTools.getSheetList();
        System.out.println("Please choose a sheet to display by type in sheet ID:");
        System.out.print(">>>");
        int sheetIndex = getInt(list.size());

        cmdMain.playerThread.setMusicList(SqliteTools.getMusicBySheet(list.get(sheetIndex)));
        //打印歌曲列表
        SheetManager.printMusicList(list.get(sheetIndex));
        list = SqliteTools.getMusicBySheet(list.get(sheetIndex));
        if (list.size() == 0){
            //歌单无歌曲
            System.out.println("The sheet of your chosen non music!");
        }

    }

    //创建歌单
    private static void createSheet(){
        System.out.println("Please enter the name of sheet:");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        try {
            SqliteTools.createSheet(name);
            System.out.println("\nCreated successfully!\n");
        }catch (Exception e){
            System.out.println("Creation failed, please check and try again!");
        }
        printAllSheet();
    }

    //删除歌单
    private static void deleteSheet(){
        int size = SqliteTools.getSheetList().size();
        if (size == 0){
            System.out.println("Non music sheet");
            return;
        }
        printAllSheet();
        System.out.println("Please enter the ID of sheet to delete:");
        System.out.print(">>>");
        int index = getInt(size);
        SqliteTools.deleteSheet(SqliteTools.getSheetList().get(index));
        System.out.println("\ndelete successfully\n");
        printAllSheet();

    }

    public static void main(String[] args) {

        //命令-函数哈希映射
        HashMap<String, String> hashCmds = new HashMap<String, String>();
        hashCmds.put("create", "createSheet");      hashCmds.put("c", "createSheet");
        hashCmds.put("deleteSheet", "deleteSheet"); hashCmds.put("ds", "deleteSheet");
        hashCmds.put("music", "music");             hashCmds.put("m", "music");
        hashCmds.put("sheet", "printAllSheet");     hashCmds.put("s", "printAllSheet");
        hashCmds.put("deleteMusic", "deleteMusic"); hashCmds.put("dm", "deleteMusic");
        hashCmds.put("help", "help");               hashCmds.put("h", "help");
        hashCmds.put("quit", "quit");               hashCmds.put("q", "quit");

        //利用反射调用函数
        Method method;
        Scanner scanner = new Scanner(System.in);
        String cmd;
        isquit = false;
        while (!isquit){
            System.out.print(">>>");
            cmd = scanner.next();
            if (hashCmds.get(cmd) != null){
                try {
                    method = SheetManager.class.getDeclaredMethod(hashCmds.get(cmd));
                    method.setAccessible(true);
                    method.invoke(SheetManager.class.newInstance());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
