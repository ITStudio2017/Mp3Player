package cn.ktchen.test;

import cn.ktchen.http.HttpTools;
import cn.ktchen.sqlite.SqliteTools;

import java.util.HashMap;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {

        //查询
        Vector<HashMap<String,String>> searchSet =  HttpTools.search("云烟成雨",1, HttpTools.Sources.tencent.toString());
        for (HashMap music:searchSet
             ) {
            System.out.println(music);
        }

        //创建歌单
//        SqliteTools.createSheet("纯民谣");

        //查询歌单
        Vector<HashMap<String,String>> sheetList = SqliteTools.getSheetList();
        for (HashMap<String,String> sheet: sheetList
             ) {
            System.out.println(sheet);
        }
        HttpTools.downloadMusic(searchSet.get(0), sheetList.get(0));
    }

}
