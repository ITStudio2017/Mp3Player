package cn.ktchen.test;

import cn.ktchen.http.HttpTools;
import cn.ktchen.player.PlayerThread;
import cn.ktchen.sqlite.SqliteTools;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    public static void main(String[] args) throws Exception {

        //查询
        Vector<HashMap<String,String>> searchSet =  HttpTools.search("你一生的故事",1, HttpTools.Sources.netease.toString());
        for (HashMap music:searchSet
             ) {
            System.out.println(music);
        }

        //创建歌单
//        SqliteTools.createSheet("纯民谣");

        //查询歌单
//        Vector<HashMap<String,String>> sheetList = SqliteTools.getSheetList();
//        for (HashMap<String,String> sheet: sheetList
//             ) {
//            System.out.println(sheet);
//        }
//        HttpTools.downloadMusic(searchSet.get(0), sheetList.get(0));
//        Vector<HashMap<String,String>> musicList = SqliteTools.getMusicBySheet(
//                sheetList.get(0));
        final PlayerThread playerThread = new PlayerThread(0,searchSet, PlayerThread.Pattern.Sequence);
        new Thread(playerThread).start();
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    System.out.println("当前时间:" + playerThread.getNowMusicTime());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        while (true){
            int second = scanner.nextInt();
            playerThread.setTime(second);
        }

    }

}
