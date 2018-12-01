package cn.ktchen.cmdPlayer;

import cn.ktchen.player.PlayerThread;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;

public class cmdMain {
    static PlayerThread playerThread = new PlayerThread(0,null);
    private static boolean exit = false;

    //版本号
    private static String Version = "1.2";

    //开始提示语
    private static void printStartwords(){
        System.out.println("ISong "+ Version +" (Author:Alex Lee)");
        System.out.println("Welcome to use ISong Player");
        System.out.println("Type \"help\", \"about\" for more information.");
    }

    //结束提示语
    private static void printEndwords() {
        System.out.println("Thanks for use!");
        System.out.println("Goodbye!");
    }

    private static void help() {
        System.out.printf("%-12s %-10s %s\n",   "Command",  "Abbr", "Description");
        System.out.printf("%-12s %-10s %s\n",   "-------",  "----", "-----------");
        System.out.printf("%-12s %-10s %s\n",  "search",       "s",    "Enter search mode");
        System.out.printf("%-12s %-10s %s\n",  "manager",      "m" ,   "Enter music manager mode");
        System.out.printf("%-12s %-10s %s\n",  "control",      "c",    "Enter play control mode");
        System.out.printf("%-12s %-10s %s\n",  "help",         "h",    "View help");
        System.out.printf("%-12s %-10s %s\n",  "exit",         "",          "Exit the program");
        System.out.printf("%-12s %-10s %s\n",  "about",        "",          "View software information");
        System.out.println();
        System.out.println("Search Mode: search, download songs");
        System.out.println("Music Manager Mode: Manage local music sheet and songs");
        System.out.println("Play Control Mode: Control to play current music");
    }

    private static void about() {
        System.out.println("ISong Player");
        System.out.println("Version:    " + Version);
        System.out.println("Author:     Alex Lee");
        System.out.println("Email:      chenktmail@gmail.com");
        System.out.println("Blog:       https://www.ktchen.cn");
        System.out.println("The program uses the APIs as follows:");
        System.out.println("1、https://music.itmxue.cn/api.php");
        System.out.println("2、https://music_api.dns.24mz.cn/index.php");
        System.out.println("3、https://api.bzqll.com/");
        System.out.println("Please inform if any infringement");
        System.out.println( "ISong Player is a Java-based terminal music player.\n" +
                            "With music search, play, download and other functions.\n" +
                            "This program is only for practicing Java. If you found \n" +
                            "any shortcomings, please inform."
        );
    }

    private static void enterSearch(){
        System.out.println("------Enter search mode------");
        Search.main(null);
    }

    private static void enterControl(){
        System.out.println("------Enter play control mode------");
        playControl.main(null);
    }

    private static void enterManager(){
        System.out.println("------Enter music manager mode------");
        SheetManager.main(null);
    }

    private static void exit(){
        cmdMain.playerThread.stop();
        printEndwords();
        exit = true;
    }
    public static void main(String[] args) {
        HashMap<String, String> hashCmds = new HashMap<String, String>();
        hashCmds.put("search", "enterSearch"); hashCmds.put("s", "enterSearch");
        hashCmds.put("control", "enterControl"); hashCmds.put("c", "enterControl");
        hashCmds.put("manager", "enterManager"); hashCmds.put("m", "enterManager");
        hashCmds.put("help", "help");           hashCmds.put("h", "help");
        hashCmds.put("about", "about");
        hashCmds.put("exit", "exit");
        printStartwords();
        //利用反射调用函数
        Method method;
        Scanner scanner = new Scanner(System.in);
        String cmd;
        exit = false;
        while (!exit){
            System.out.print(">>>");
            cmd = scanner.next();
            if (hashCmds.get(cmd) != null){
                try {
                    method = cmdMain.class.getDeclaredMethod(hashCmds.get(cmd));
                    method.setAccessible(true);
                    method.invoke(cmdMain.class.newInstance());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
