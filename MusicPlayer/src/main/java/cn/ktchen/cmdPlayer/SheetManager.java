package cn.ktchen.cmdPlayer;

import cn.ktchen.sqlite.SqliteTools;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import static cn.ktchen.cmdPlayer.playControl.getInt;

public class SheetManager {

    private enum cmds {sheet, music, create, deleteSheet, deleteMusic, quit, help}
    private enum shortCmds {s, m, c, ds, dm, q, h}

    //帮助
    private static void help(){
        System.out.println("Command:sheet        Abbr:s        View all music sheet");
        System.out.println("Command:music        Abbr:m        View the list of music under the music sheet");
        System.out.println("Command:create       Abbr:c        Create music sheet");
        System.out.println("Command:deleteSheet  Abbr:ds       Delete music sheet");
        System.out.println("Command:deleteMusic  Abbr:dm       Delete the song");
        System.out.println("Command:quit         Abbr:q        Exit music manager mode");
        System.out.println("Command:help         Abbr:h        View help");
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
        Scanner scanner = new Scanner(System.in);
        String cmd;
        while (true){
            System.out.print(">>>");
            cmd = scanner.next();

            //创建歌单
            if (cmd.equals(cmds.create.toString()) || cmd.equals(shortCmds.c.toString())) {
                createSheet();
                continue;
            }

            //删除歌单
            if (cmd.equals(cmds.deleteSheet.toString()) || cmd.equals(shortCmds.ds.toString())){
                deleteSheet();
                continue;
            }

            //查看音乐列表
            if (cmd.equals(cmds.music.toString()) || cmd.equals(shortCmds.m.toString())){
                music();
                continue;
            }

            //查看歌单列表
            if (cmd.equals(cmds.sheet.toString()) || cmd.equals(shortCmds.s.toString())){
                printAllSheet();
                continue;
            }

            //删除音乐
            if (cmd.equals(cmds.deleteMusic.toString()) || cmd.equals(shortCmds.dm.toString())){
                deleteMusic();
                continue;
            }

            //帮助
            if (cmd.equals(cmds.help.toString()) || cmd.equals(shortCmds.h.toString())){
                help();
                continue;
            }

            //退出
            if (cmd.equals(cmds.quit.toString()) || cmd.equals(shortCmds.q.toString())){
                System.out.println("Exit music manager mode");
                break;
            }

        }
    }

}
