package cn.ktchen.cmdPlayer;

import cn.ktchen.http.HttpTools;
import cn.ktchen.sqlite.SqliteTools;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class Search {

    private static int nowPage;
    private static Vector<HashMap<String, String>> result;
    private static HttpTools.Sources source = HttpTools.Sources.netease;
    private static boolean quit = false;
    //搜索时可使用的命令
    private enum searchCmds {pre, next, play, down, quit, help, source, display ,searchAgain}
    private enum shortCmds {pr, n, p, d, q, h, s, ds, sa}


    private static void help(){
        System.out.println("Command:pre          Abbr:pr       return to previous page");
        System.out.println("Command:next         Abbr:n        go to the next page");
        System.out.println("Command:play         Abbr:p        Play online");
        System.out.println("Command:down         Abbr:d        download music");
        System.out.println("Command:source       Abbr:s        Change search source");
        System.out.println("Command:display      Abbr:ds       Print current search page results");
        System.out.println("Command:searchAgain  Abbr:sa       Search again");
        System.out.println("Command:quit         Abbr:q        quit ");
        System.out.println("Command:help         Abbr:h        View help");
    }

    private static void display() {
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
        System.out.println("Current " + nowPage + "page");
        System.out.printf("%-3s %-30s %-20s %-20s\n", "ID", "Name", "Artist", "Album");
        for (int i = 0; i < result.size(); i++) {
            HashMap<String, String> music = result.get(i);
            System.out.printf("%-3s %-30s %-20s %-20s\n", Integer.toString(i),music.get("name"), music.get("artist"), music.get("album"));
        }

    }

    //下载音乐
    public static void download(){
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
        if (result.size() == 0)
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

    private static void prePage(String keyword){
        if (nowPage == 1) {
            System.out.println("Currently is the first page");
            return;
        }
        nowPage--;
        result = HttpTools.search(keyword, nowPage, 20, source.toString());
        printResult(result);
    }

    private static void nextPage(String keyword) {
        nowPage++;
        result = HttpTools.search(keyword, nowPage, 20, source.toString());
        printResult(result);
    }

    private static void search(){
        nowPage = 1;
        quit = false;
        System.out.println("Please enter the keyword");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        String keyword;
        String cmd;
        do {
            keyword = scanner.nextLine();
        }while (keyword.equals(""));
        System.out.println("searching...");
        result = HttpTools.search(keyword,nowPage, 20, source.toString());
        printResult(result);
        while (true){
            System.out.print(">>>");
            cmd = scanner.next();

            //上一页
            if (cmd.equals(searchCmds.pre.toString()) || cmd.equals(shortCmds.pr.toString())){
                System.out.println("Returning to the previous page...");
                prePage(keyword);
                continue;
            }

            //下一页
            if (cmd.equals(searchCmds.next.toString()) || cmd.equals(shortCmds.n.toString())){
                System.out.println("Going to the next page...");
                nextPage(keyword);
                continue;
            }

            //在线播放
            if (cmd.equals(searchCmds.play.toString()) || cmd.equals(shortCmds.p.toString())) {
                play();
                continue;
            }

            //下载
            if (cmd.equals(searchCmds.down.toString()) || cmd.equals(shortCmds.d.toString())){
                download();
                continue;
            }

            //打印当前页面的搜索结果
            if (cmd.equals(searchCmds.display.toString()) || cmd.equals(shortCmds.ds.toString())){
                display();
                continue;
            }

            //更换源
            if (cmd.equals(searchCmds.source.toString()) || cmd.equals(shortCmds.s.toString())){
                changeSource();
                continue;
            }

            //重新搜索
            if (cmd.equals(searchCmds.searchAgain.toString()) || cmd.equals(shortCmds.sa.toString())){
                break;
            }

            //帮助
            if (cmd.equals(searchCmds.help.toString()) || cmd.equals(shortCmds.h.toString())) {
                help();
                continue;
            }

            //退出
            if (cmd.equals(searchCmds.quit.toString()) || cmd.equals(shortCmds.q.toString())){
                quit = true;
                System.out.println("Exit search mode");
                break;
            }
        }

    }

    public static void main(String[] args) {
        quit = false;
        while (!quit){
            search();
        }
    }
}
