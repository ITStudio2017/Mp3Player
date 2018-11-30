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
    public static void main(String[] args){
        Vector<HashMap<String, String>> vector = HttpTools.getInternetPlaylist(20,1);
        Vector<HashMap<String, String>> musicList = HttpTools.getMusicListByInternetPlaylist(vector.elementAt(1));
        Vector<HashMap<String, String>> sheetList = SqliteTools.getSheetList();
        HttpTools.downloadMusic(musicList.elementAt(0), sheetList.elementAt(0));
    }

}
