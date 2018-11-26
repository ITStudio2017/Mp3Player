package cn.ktchen.utils;

import cn.ktchen.sqlite.SqliteTools;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

public class GetFileMD5 implements Runnable{
    private File file;
    private HashMap<String, String> music;
    public GetFileMD5(String filePath, HashMap<String, String> music){
        this.file = new File(filePath);
        this.music = music;
    }
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public void run(){
        try {//先睡10秒
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!file.exists()) {//文件不存在则循环
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //插入数据库
        String md5 = getFileMD5(file);
        SqliteTools.updateMd5(music, md5);
    }
}
