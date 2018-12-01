package cn.ktchen.cmdPlayer;

import cn.ktchen.http.HttpTools;
import cn.ktchen.sqlite.SqliteTools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class Search {

    private static int nowPage = 1;
    private static Vector<HashMap<String, String>> result = null;
    private static HttpTools.Sources source = HttpTools.Sources.netease;
    private static boolean quit = false;
    private static String keyword = null;

    //退出
    private static void quit(){
        quit = true;
        System.out.println("------Exit search mode------");
    }

    private static void help(){
        System.out.printf("%-15s %-10s %s\n",   "Command",  "Abbr", "Description");
        System.out.printf("%-15s %-10s %s\n",   "-------",  "----", "-----------");
        System.out.printf("%-15s %-10s %s\n",   "pre",      "pr",   "return to previous page");
        System.out.printf("%-15s %-10s %s\n",   "next",     "n",    "go to the next page");
        System.out.printf("%-15s %-10s %s\n",   "play",     "p",    "Play online");
        System.out.printf("%-15s %-10s %s\n",   "download", "d",    "download music");
        System.out.printf("%-15s %-10s %s\n",   "changeSource","cs","Change search source");
        System.out.printf("%-15s %-10s %s\n",   "display",  "ds",   "Print current search page results");
        System.out.printf("%-15s %-10s %s\n",   "search",   "s",    "Search");
        System.out.printf("%-15s %-10s %s\n",   "quit",     "q",    "quit ");
        System.out.printf("%-15s %-10s %s\n",   "help",     "h",   "View help");
    }

    private static void display() {
        if (result == null || result.size() == 0 )
            return;
        printResult(result);
    }

    private static void changeSource(){
        nowPage = 1;
        for (int i = 0, length = HttpTools.Sources.values().length; i < length; i++) {
            System.out.println("ID  " + i + "   Play source:" + HttpTools.Sources.values()[i].toString());
        }
        System.out.println("Please choose source by enter ID");
        System.out.print(">>>");
        int index = playControl.getInt(HttpTools.Sources.values().length);
        source = HttpTools.Sources.values()[index];
        System.out.println("The search source has been replaced with " + source.toString());
    }

    //打印搜索结果
    private static void printResult(Vector<HashMap<String, String>> result){
        System.out.println("Current " + "page " + nowPage);
        System.out.printf("%-3s %-30s %-20s %-20s\n", "ID", "Name", "Artist", "Album");
        for (int i = 0; i < result.size(); i++) {
            HashMap<String, String> music = result.get(i);
            System.out.printf("%-3s %-30s %-20s %-20s\n", Integer.toString(i),music.get("name"), music.get("artist"), music.get("album"));
        }

    }

    //下载音乐
    public static void download(){
        if (result == null || result.size() == 0 )
            return;

        //检查数据库中是否存在歌单
        Vector<HashMap<String, String>> sheetList = SqliteTools.getSheetList();
        if (sheetList.size() == 0) {
            System.out.println("Non music sheet, please create a sheet first");
            return;
        }

        printResult(result);
        System.out.println("Please choose a song by ID to download");
        System.out.print(">>>");
        int musicIndex = playControl.getInt(result.size());
        SheetManager.printAllSheet();
        System.out.println("Please choose a sheet to download to it");
        System.out.print(">>>");
        int sheetIndex = playControl.getInt(sheetList.size());
        HttpTools.downloadMusic(result.get(musicIndex),sheetList.get(sheetIndex));
        System.out.println("downloading asynchronously...");
    }

    //在线播放
    private static void play(){
        if (result == null || result.size() == 0 )
            return;

        printResult(result);
        System.out.println("Please choose a song to play by ID");
        System.out.print(">>>");
        int index = playControl.getInt(result.size());
        if (playControl.validate())
            cmdMain.playerThread.pause();
        cmdMain.playerThread.setMusicList(result);
        cmdMain.playerThread.setMusicIndex(index);
        cmdMain.playerThread.setPlayThread(new Thread(cmdMain.playerThread));
        cmdMain.playerThread.getPlayThread().start();
    }

    private static void prePage(){
        if (nowPage == 1) {
            System.out.println("Currently is the first page");
            return;
        }
        System.out.println("Returning to the previous page...");
        nowPage--;
        result = HttpTools.search(keyword, nowPage, 20, source.toString());
        printResult(result);
    }

    private static void nextPage() {
        if (keyword == null)
            return;
        System.out.println("Going to the next page...");
        nowPage++;
        result = HttpTools.search(keyword, nowPage, 20, source.toString());
        printResult(result);
    }

    private static void search(){
        nowPage = 1;
        System.out.println("Please enter the keyword");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        do {
            keyword = scanner.nextLine();
        }while (keyword.equals(""));
        System.out.println("searching...");
        result = HttpTools.search(keyword,nowPage, 20, source.toString());
        printResult(result);

    }

    public static void main(String[] args){

        //命令-函数映射
        HashMap<String, String> hashCmds = new HashMap<String, String>();
        hashCmds.put("pre", "prePage");     hashCmds.put("pr", "prePage");
        hashCmds.put("next", "nextPage");   hashCmds.put("n", "nextPage");
        hashCmds.put("play", "play");       hashCmds.put("p", "play");
        hashCmds.put("download", "download");hashCmds.put("d", "download");
        hashCmds.put("quit", "quit");       hashCmds.put("q", "quit");
        hashCmds.put("help", "help");       hashCmds.put("h", "help");
        hashCmds.put("changeSource", "changeSource"); hashCmds.put("cs", "changeSource");
        hashCmds.put("display", "display"); hashCmds.put("ds", "display");
        hashCmds.put("search", "search");   hashCmds.put("s", "search");

        //反射
        Method method;
        Scanner scanner = new Scanner(System.in);
        String cmd;
        quit = false;
        while (!quit){
            System.out.print(">>>");
            cmd = scanner.next();
            if (hashCmds.get(cmd) != null){
                try {
                    method = Search.class.getDeclaredMethod(hashCmds.get(cmd));
                    method.setAccessible(true);
                    method.invoke(Search.class.newInstance());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
