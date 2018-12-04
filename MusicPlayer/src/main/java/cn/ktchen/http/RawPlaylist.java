package cn.ktchen.http;


import java.util.HashMap;
import java.util.Vector;

/**
 *
 */
class RawPlaylist {
    static Vector<HashMap<String, String>> getRawPlaylist(){
        Vector<HashMap<String, String>> rawPlaylist = new Vector<HashMap<String, String>>();
        HashMap<String, String> hotPlaylist = new HashMap<String, String>();        // 云音乐热歌榜
        HashMap<String, String> upPlaylist = new HashMap<String, String>();         // 云音乐飙升榜
        HashMap<String, String> originalPlaylist = new HashMap<String, String>();   // 网易原创歌曲榜
        HashMap<String, String> newPlaylist = new HashMap<String, String>();        // 云音乐新歌榜

        //put id
        hotPlaylist.put("id", "3778678");
        upPlaylist.put("id","19723756");
        originalPlaylist.put("id", "2884035");
        newPlaylist.put("id", "3779629");

        //put title
        hotPlaylist.put("title", "云音乐热歌榜");
        upPlaylist.put("title", "云音乐飙升榜");
        originalPlaylist.put("title", "网易原创歌曲榜");
        newPlaylist.put("title", "云音乐新歌榜");

        //put description
        hotPlaylist.put("description", "云音乐热歌榜：云音乐用户一周内收听所有线上歌曲 官方TOP排行榜，每周四更新。");
        newPlaylist.put("description", "云音乐新歌榜：云音乐用户一周内收听所有新歌（一月内最新发行） 官方TOP排行榜，每天更新。");
        upPlaylist.put("description", "云音乐中每天热度上升最快的100首单曲，每日更新。");
        originalPlaylist.put("description", "云音乐独立原创音乐人作品官方榜单，以推荐优秀原创作品为目的。每周四网易云音乐首发。");

        //put coverImageUrl

        hotPlaylist.put("coverImgUrl","https://p2.music.126.net/GhhuF6Ep5Tq9IEvLsyCN7w==/18708190348409091.jpg");
        newPlaylist.put("coverImgUrl", "https://p1.music.126.net/N2HO5xfYEqyQ8q6oxCw8IQ==/18713687906568048.jpg");
        upPlaylist.put("coverImgUrl", "https://p1.music.126.net/DrRIg6CrgDfVLEph9SNh7w==/18696095720518497.jpg");
        originalPlaylist.put("coverImgUrl", "https://p2.music.126.net/sBzD11nforcuh1jdLSgX7g==/18740076185638788.jpg");

        rawPlaylist.add(hotPlaylist);
        rawPlaylist.add(newPlaylist);
        rawPlaylist.add(upPlaylist);
        rawPlaylist.add(originalPlaylist);

        return rawPlaylist;
    }
}
