package cn.ktchen.cmdPlayer;

import cn.ktchen.player.PlayerThread;
import cn.ktchen.sqlite.SqliteTools;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class playControl {

    private enum commands {play, pause, next, pre, time, info, pattern, quit, help, list, index}
    private enum shortCommands {p, pu, n, pr, t, i, pa, q, h, l, in}

    private static void help(){
        System.out.println("Command:play     Abbr:p    Play music");
        System.out.println("Command:pause    Abbr:pu   Pause play");
        System.out.println("Command:next     Abbr:n    Play the next song");
        System.out.println("Command:pre      Abbr:pr   Play the previous song");
        System.out.println("Command:time     Abbr:t    Set the current music play time");
        System.out.println("Command:info     Abbr:i    View current music information");
        System.out.println("Command:pattern  Abbr:pa   Set play mode");
        System.out.println("Command:list     Abbr:l    list the music sheet");
        System.out.println("Command:index    Abbr:in   play the music of index");
        System.out.println("Command:quit     Abbr:q    Exit play control mode");
        System.out.println("Command:help     Abbr:h    View help");
    }

    private static void index(){
        if (cmdMain.playerThread.getMusicList() == null)
            return;
        listMusic();
        System.out.println("Please enter the index");
        System.out.print(">>>");
        int index = getInt(cmdMain.playerThread.getMusicList().size());
        cmdMain.playerThread.indexMusic(index);
    }

    private static void listMusic(){
        Vector<HashMap<String, String>> musicList = cmdMain.playerThread.getMusicList();
        if (musicList == null)
            return;
        System.out.printf("%-3s %-20s %-10s %-10s\n", "ID", "Name", "Artist", "Album");
        for (int i = 0; i < musicList.size(); ++i) {
            HashMap<String, String> music = musicList.get(i);
            System.out.printf("%-3s %-20s %-10s %-10s\n", i, music.get("name"), music.get("artist"), music.get("album"));
        }
        System.out.println();
    }
    private static void setPattern(){
        if (!validate())
            return;
        System.out.println("Play mode 0:Sequence");
        System.out.println("Play mode 1:Stochastic");
        System.out.println("Play mode 2:Single");
        System.out.println("Please enter a number to select a play mode:");
        System.out.print(">>>");
        int index = getInt(3);
        cmdMain.playerThread.setNowPattern(PlayerThread.Pattern.values()[index]);
        printMusicInfo();
    }

    //暂停
    private static void pause(){
        if (!validate())
            return;
        if (!cmdMain.playerThread.isPaused())
        cmdMain.playerThread.pause();
    }

    //设置时间
    private static void setTime(){
        if (!validate())
            return;
        printMusicInfo();
        System.out.println("Please enter the time of the music:");
        System.out.print(">>>");
        int time = getInt((int)cmdMain.playerThread.getMusicTime());
        cmdMain.playerThread.setTime(time,true);
    }

    //打印当前播放音乐信息
    private static void printMusicInfo(){
        if (!validate())
            return;
        Vector<HashMap<String, String>> musicList = cmdMain.playerThread.getMusicList();
        if (musicList != null){
            HashMap<String, String> music = musicList.get(cmdMain.playerThread.getMusicIndex());
            System.out.println("Current music information as follows:");
            System.out.println("Music:" + music.get("name"));
            System.out.println("Artist:" + music.get("artist"));
            System.out.println("Album:" + music.get("album"));
            System.out.printf("Total time: %.2fs\n", cmdMain.playerThread.getMusicTime());
            System.out.printf("Now:%.2fs\n",cmdMain.playerThread.getNowMusicTime());
            System.out.println("Play Mode:" + cmdMain.playerThread.getNowPattern().toString());

        }
    }

    static boolean validate(){
        return !(cmdMain.playerThread.getMusicList() == null);
    }

    //下一首歌
    private static void next(){
        if (!validate())
            return;
        cmdMain.playerThread.nextMusic();
        printMusicInfo();
    }

    //上一首歌
    private static void pre(){
        if (!validate())
            return;
        cmdMain.playerThread.previousMusic();
        printMusicInfo();
    }

    //根据范围读取一个int类型的数
    static int getInt(int range){
        Scanner scanner = new Scanner(System.in);
        int index;
        do {
            try {
                index = Integer.parseInt(scanner.next());
                if (index < 0 || index > range - 1){
                    System.out.println("Out range of index, please type in again:");
                    System.out.print(">>>");
                }else {
                    return index;
                }
            }catch (Exception e){
                System.out.println("Syntax Error, please type in again:");
                System.out.print(">>>");
            }
        }while (true);
    }

    private static void play(){
        //如果还没初始化
        if (cmdMain.playerThread.getMusicList() == null)
            init();

        //如果已暂停
        if (cmdMain.playerThread.isPaused())
        new Thread(cmdMain.playerThread).start();
    }

    private static void init(){
        Vector<HashMap<String, String>> list;
        int sheetIndex;

        //打印歌单信息
        SheetManager.printAllSheet();

        //获取歌单索引
        list = SqliteTools.getSheetList();
        System.out.println("Please choose a sheet to play by type in sheet ID:");
        System.out.print(">>>");
        sheetIndex = getInt(list.size());

        //打印歌曲列表
        SheetManager.printMusicList(list.get(sheetIndex));
        list = SqliteTools.getMusicBySheet(list.get(sheetIndex));
        if (list.size() == 0){
            //歌单无歌曲
            System.out.println("The sheet of your chosen non music!");
            return;
        }

        System.out.println("Please choose a music to play by type in music ID:");
        System.out.print(">>>");
        int musicIndex = getInt(list.size());
        cmdMain.playerThread.setMusicList(SqliteTools.getMusicBySheet(SqliteTools.getSheetList().get(sheetIndex)));
        cmdMain.playerThread.setMusicIndex(musicIndex);
        cmdMain.playerThread.pause();
        new Thread(cmdMain.playerThread).start();
        printMusicInfo();
        System.out.println("Playing...");
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String cmd;
        while (true){
            System.out.print(">>>");
            cmd = scanner.next();

            //当前音乐信息
            if (cmd.equals(commands.info.toString()) || cmd.equals(shortCommands.i.toString())) {
                printMusicInfo();
                continue;
            }
            //下一首歌
            if (cmd.equals(commands.next.toString()) || cmd.equals(shortCommands.n.toString())) {
                next();
                continue;
            }
            //上一首歌
            if (cmd.equals(commands.pre.toString()) || cmd.equals(shortCommands.pr.toString())) {
                pre();
                continue;
            }
            //控制播放进度
            if (cmd.equals(commands.time.toString()) || cmd.equals(shortCommands.t.toString())) {
                setTime();
                continue;
            }
            //播放
            if (cmd.equals(commands.play.toString()) || cmd.equals(shortCommands.p.toString())) {
                play();
                continue;
            }
            //暂停
            if (cmd.equals(commands.pause.toString()) || cmd.equals(shortCommands.pu.toString())) {
                pause();
                continue;
            }
            //打印音乐列表
            if (cmd.equals(commands.list.toString()) ||cmd.equals(shortCommands.l.toString())) {
                listMusic();
                continue;
            }
            //通过index播放
            if (cmd.equals(commands.index.toString()) || cmd.equals(shortCommands.in.toString())){
                index();
                continue;
            }
            //帮助
            if (cmd.equals(commands.help.toString()) || cmd.equals(shortCommands.h.toString())){
                help();
                continue;
            }
            //模式
            if (cmd.equals(commands.pattern.toString()) || cmd.equals(shortCommands.pa.toString())){
                setPattern();
                continue;
            }
            //退出
            if (cmd.equals(commands.quit.toString()) || cmd.equals(shortCommands.q.toString())){
                System.out.println("Exit play control mode");
                break;
            }
        }
    }
}
