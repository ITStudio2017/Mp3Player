package cn.ktchen.cmdPlayer;

import cn.ktchen.player.PlayerThread;
import cn.ktchen.sqlite.SqliteTools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class playControl {

    private static boolean isQuit;

    private static void quit(){
        isQuit = true;
        System.out.println("------Exit play control mode------");
    }
    private static void help(){
        System.out.printf("%-12s %-10s %s\n",   "Command",  "Abbr", "Description");
        System.out.printf("%-12s %-10s %s\n",   "-------",  "----", "-----------");
        System.out.printf("%-12s %-10s %s\n",   "play ",    "p    ","Play music");
        System.out.printf("%-12s %-10s %s\n",   "pause",    "pu   ","Pause play");
        System.out.printf("%-12s %-10s %s\n",   "next",     "n    ","Play the next song");
        System.out.printf("%-12s %-10s %s\n",   "pre",      "pr   ","Play the previous song");
        System.out.printf("%-12s %-10s %s\n",   "time",     "t    ","Set the current music play time");
        System.out.printf("%-12s %-10s %s\n",   "info",     "i    ","View current music information");
        System.out.printf("%-12s %-10s %s\n",   "pattern",  "pa   ","Set play mode");
        System.out.printf("%-12s %-10s %s\n",   "list",     "l    ","list the music sheet");
        System.out.printf("%-12s %-10s %s\n",   "index",    "in   ","play the music of index");
        System.out.printf("%-12s %-10s %s\n",   "stop",     "s    ","stop the play");
        System.out.printf("%-12s %-10s %s\n",   "quit",     "q    ","Exit play control mode");
        System.out.printf("%-12s %-10s %s\n",   "help",     "h    ","View help");
    }

    private static void stop(){
        cmdMain.playerThread.stop();
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
        if (cmdMain.playerThread.isPaused()){
            cmdMain.playerThread.setPlayThread(new Thread(cmdMain.playerThread));
            cmdMain.playerThread.getPlayThread().start();
        }

    }

    private static void init(){
        Vector<HashMap<String, String>> list;
        int sheetIndex;

        //打印歌单信息
        SheetManager.printAllSheet();

        //获取歌单索引
        list = SqliteTools.getSheetList();
        if (list.size() == 0){
            //歌单无歌曲
            System.out.println("No music sheet!");
            return;
        }
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

        cmdMain.playerThread.setPlayThread(new Thread(cmdMain.playerThread));
        cmdMain.playerThread.getPlayThread().start();
        printMusicInfo();
        System.out.println("Playing...");
    }

    public static void main(String[] args){

        //命令-函数哈希映射
        HashMap<String, String> hashCmds = new HashMap<String, String>();
        hashCmds.put("play", "play");           hashCmds.put("p", "play");
        hashCmds.put("pause","pause");          hashCmds.put("pu","pause");
        hashCmds.put("next", "next");           hashCmds.put("n", "next");
        hashCmds.put("pre",  "pre");            hashCmds.put("pr","pre");
        hashCmds.put("time", "setTime");        hashCmds.put("t", "setTime");
        hashCmds.put("info", "printMusicInfo"); hashCmds.put("i", "printMusicInfo");
        hashCmds.put("pattern", "setPattern");  hashCmds.put("pa", "setPattern");
        hashCmds.put("quit", "quit");           hashCmds.put("q", "quit");
        hashCmds.put("help","help");            hashCmds.put("h", "help");
        hashCmds.put("list", "listMusic");      hashCmds.put("l", "listMusic");
        hashCmds.put("index", "index");         hashCmds.put("in","index");
        hashCmds.put("stop", "stop");           hashCmds.put("s", "stop");

        Method method;
        Scanner scanner = new Scanner(System.in);
        String cmd;
        isQuit = false;
        while (!isQuit){
            System.out.print(">>>");
            cmd = scanner.next();
            if (hashCmds.get(cmd) != null){
                try {
                    method = playControl.class.getDeclaredMethod(hashCmds.get(cmd));
                    method.setAccessible(true);
                    method.invoke(playControl.class.newInstance());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


        }
    }
}
