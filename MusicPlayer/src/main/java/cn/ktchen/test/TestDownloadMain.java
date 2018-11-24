package cn.ktchen.test;

import cn.ktchen.download.DownloadSingle;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

public class TestDownloadMain {

    public static void main(String[] args) throws Exception {
        //多线程下载
//        String url = "https://m7.music.126.net/20181124150056/4aa337bde5fbf49303ddf279842fb419/ymusic/931b/c2a2/9662/df3b7e6cbff7acaf30a32bfb9df824c2.mp3";
//        String filePath = System.getProperty("user.dir") + "/test/test2.mp3";
//        DownloadSingle downloadSingle = new DownloadSingle(url, filePath, 100001,200000,1);
//        downloadSingle.start();
//        DownloadUtils.download(url,"test.mp3",System.getProperty("user.dir") + "/test/", 5);
        Vector<InputStream> vector = new Vector<InputStream>();
        InputStream inputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/test/test1.mp3"));
        InputStream inputStream1 = new FileInputStream(new File(System.getProperty("user.dir") + "/test/test2.mp3"));
        Enumeration<InputStream> e = vector.elements();
        SequenceInputStream sequenceInputStream = new SequenceInputStream(e);
        new AdvancedPlayer(inputStream).play();
        new AdvancedPlayer(inputStream1).play();


    }
}