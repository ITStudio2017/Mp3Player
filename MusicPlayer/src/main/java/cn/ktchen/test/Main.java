package cn.ktchen.test;

import cn.ktchen.http.HttpTools;
import cn.ktchen.player.PlayerThread;
import cn.ktchen.sqlite.SqliteTools;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class Main {
    public static void main(String[] args) throws Exception {

        //查询
        Vector<HashMap<String,String>> searchSet =  HttpTools.search("春夏秋冬的你",1, HttpTools.Sources.netease.toString());
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
        PlayerThread playerThread = new PlayerThread(0,searchSet, PlayerThread.Pattern.Sequence);
        playerThread.setTime(83);
        playerThread.run();
        Thread.sleep(3 * 1000);
        System.out.println(playerThread.getImagePath());
        System.out.println(playerThread.getLrcPath());
    }

}
