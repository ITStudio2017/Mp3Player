package cn.ktchen.cmdPlayer;

import cn.ktchen.player.PlayerThread;

import java.util.Scanner;

public class cmdMain {
    static PlayerThread playerThread = new PlayerThread(0,null);

    private enum commands {search, manager, control, exit, help, about,}
    private enum shortCommands {s, m, c, h}

    //开始提示语
    private static void printStartwords(){
        System.out.println("ISong 1.0 (Author:Alex Lee)");
        System.out.println("Welcome to use ISong Player");
        System.out.println("Type \"help\", \"about\" for more information.");
    }

    //结束提示语
    private static void printEndwords() {
        System.out.println("Thanks for use!");
        System.out.println("Goodbye!");
    }

    private static void help() {
        System.out.println("Command:search       Abbr:s     Enter search mode");
        System.out.println("Command:manager      Abbr:m     Enter music manager mode");
        System.out.println("Command:control      Abbr:c     Enter play control mode");
        System.out.println("Command:help         Abbr:h     View help");
        System.out.println("Command:exit                    Exit the program");
        System.out.println("Command:about                   View software information");
        System.out.println();
        System.out.println("Search Mode: search, download songs");
        System.out.println("Music Manager Mode: Manage local music sheet and songs");
        System.out.println("Play Control Mode: Control to play current music");
    }

    private static void about() {
        System.out.println("ISong Player");
        System.out.println("Version:    1.0");
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

    public static void main(String[] args) {
        printStartwords();
        Scanner scanner = new Scanner(System.in);
        String cmd;
        while (true){
            System.out.print(">>>");
            cmd = scanner.next();

            //搜索
            if (cmd.equals(commands.search.toString()) || cmd.equals(shortCommands.s.toString())){
                System.out.println("Enter search mode");
                Search.main(null);
                continue;
            }

            //播放控制
            if (cmd.equals(commands.control.toString()) || cmd.equals(shortCommands.c.toString())) {
                System.out.println("Enter play control mode");
                playControl.main(null);
                continue;
            }

            //信息管理
            if (cmd.equals(commands.manager.toString()) || cmd.equals(shortCommands.m.toString())){
                System.out.println("Enter music manager mode");
                SheetManager.main(null);
            }

            //帮助
            if (cmd.equals(commands.help.toString()) || cmd.equals(shortCommands.h.toString())){
                help();
                continue;
            }

            //关于
            if (cmd.equals(commands.about.toString())) {
                about();
                continue;
            }

            //关闭
            if (cmd.equals(commands.exit.toString())){
                cmdMain.playerThread.stop();
                if (cmdMain.playerThread.getPlayThread() != null)
                    cmdMain.playerThread.getPlayThread().stop();
                printEndwords();
                break;
            }

        }
    }
}
