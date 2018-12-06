package cn.ktchen.http;

import cn.ktchen.download.DownloadSingle;
import cn.ktchen.sqlite.SqliteTools;
import cn.ktchen.utils.GetFileMD5;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

import cn.ktchen.player.*;


public class HttpTools {

    //音乐源
    public enum Sources {
        netease, tencent, xiami, kugou
    }

    //API接口
    private static final String mliAPI = "https://music_api.dns.24mz.cn/index.php";
    private static final String bzqllAPI = "https://api.bzqll.com/music/netease/hotSongList";
    private static final String itmxueAPI = "https://music.itmxue.cn/api.php";


    public static Vector<HashMap<String,String>> getInternetPlaylist(int page, int count){
        //首页固定了四个歌单
        if (page == 1)
            count -= 4;

        //构造参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("key","579621905"));
        list.add(new BasicNameValuePair("cat","全部"));
        list.add(new BasicNameValuePair("limit",Integer.toString(count)));
        list.add(new BasicNameValuePair("offset",Integer.toString( count * (page-1) - 2)));
        JSONObject jsonObject = new JSONObject(doGet(bzqllAPI,list));

        Vector<HashMap<String,String>> playList = (page == 1)
                ? RawPlaylist.getRawPlaylist() : new Vector<HashMap<String, String>>();

        try{
            String code = jsonObject.get("code").toString();
            if (code.equals("200")){
                JSONArray jsonList = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonList.length(); i++) {
                    JSONObject jsonSheet = jsonList.getJSONObject(i);
                    HashMap<String, String> sheet = new HashMap<String, String>();
                    sheet.put("id", jsonSheet.get("id").toString());
                    sheet.put("title", jsonSheet.get("title").toString());
                    sheet.put("description", jsonSheet.get("description").toString());
                    sheet.put("coverImgUrl", jsonSheet.get("coverImgUrl").toString());
                    playList.add(sheet);
                }
                return playList;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Vector<HashMap<String,String>> getMusicListByInternetPlaylist(
            HashMap<String, String> playlist){

        //构造参数，获取音乐列表
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types","playlist"));
        list.add(new BasicNameValuePair("id", playlist.get("id")));
        JSONObject result = new JSONObject(doPost(itmxueAPI, list));
        String code = result.get("code").toString();
        if (!code.equals("200"))
            return null;
        JSONArray tracks = result.getJSONObject("playlist").getJSONArray("tracks");

        Vector<HashMap<String, String>> musicList = new Vector<HashMap<String, String>>();
        for (int i = 0; i < tracks.length(); i++) {
            HashMap<String, String> music = new HashMap<String, String>();
            JSONObject jsonMusic = tracks.getJSONObject(i);
            music.put("id", jsonMusic.get("id").toString());
            music.put("name", jsonMusic.get("name").toString());
            music.put("artist", jsonMusic.getJSONArray("ar").getJSONObject(0).get("name").toString());
            music.put("url_id", jsonMusic.get("id").toString());
            music.put("lyric_id", jsonMusic.get("id").toString());
            music.put("source", Sources.netease.toString());
            music.put("album", jsonMusic.getJSONObject("al").get("name").toString());
            music.put("pic_id", jsonMusic.getJSONObject("al").get("pic").toString());
            musicList.add(music);
        }
        return musicList;
    }

    //下载位置
    private static String downloadPath = System.getProperty("user.dir") + "/downloads/";

    //默认文件路径
    public static String filePath(HashMap<String,String> music){
        return downloadPath + music.get("artist") + "/" + music.get("album") +
                "/" + music.get("name") + "/";
    }

    //默认文件名
    public static String fileName(HashMap<String,String> music){
        return music.get("name") + " - " + music.get("artist");
    }

    //搜索功能
    public static Vector<HashMap<String, String>> search(
            String keyWord, int page, int count) {
        //默认网易云搜索
        return search(keyWord, page, count, Sources.netease.toString());
    }

    public static Vector<HashMap<String, String>> search(
            String keyWord, int page, int count, String source) {

        //构造请求
        Vector<HashMap<String, String>> vector = new Vector<HashMap<String, String>>();
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "search"));
        list.add(new BasicNameValuePair("count", Integer.toString(count)));
        list.add(new BasicNameValuePair("source", source));
        list.add(new BasicNameValuePair("pages", Integer.toString(page)));
        list.add(new BasicNameValuePair("name", keyWord));

        //获取查询集
        String result = doPost(itmxueAPI, list);
        if (result == null)
            return null;
        JSONArray jsonArray = new JSONArray(result);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("id", jsonObject.get("id").toString());
            hashMap.put("name", jsonObject.get("name").toString());
            hashMap.put("artist", jsonObject.getJSONArray("artist").get(0).toString());
            hashMap.put("url_id", jsonObject.get("url_id").toString());
            hashMap.put("lyric_id", jsonObject.get("lyric_id").toString());
            hashMap.put("source", jsonObject.get("source").toString());
            hashMap.put("album", jsonObject.get("album").toString());
            hashMap.put("pic_id", jsonObject.get("pic_id").toString());

            vector.add(hashMap);
        }
        return vector;
    }

    public static void downloadMusic(
            HashMap<String, String> music, HashMap<String,String> sheet) {
        //目标路径
        String filePath = filePath(music);
        String fileName = fileName(music);

        //检查是否缓存
        File musicFile = new File(filePath + fileName + ".mp3");
        if (musicFile.exists()) {//已下载
            //数据库有记录
            if(SqliteTools.musicExist(music)) {
                System.out.println("当前歌曲已下载");
                return;
            }
            new Thread(new GetFileMD5(filePath + fileName + ".mp3", music)).start();
            SqliteTools.createMusic(music.get("name"), filePath + fileName  + ".mp3",
                    Integer.parseInt(sheet.get("id")), filePath + fileName  + ".jpg",
                    filePath + fileName  + ".lrc", music.get("artist"),music.get("album"));

            System.out.println("下载完成");
        }else{//开始下载
            String musicUrl = getMusicUrl(music);
            String imageUrl = getImageUrl(music);
            try {
                new DownloadSingle(musicUrl, filePath + fileName + ".mp3", new Object()).start();
                new DownloadSingle(imageUrl, filePath + fileName + ".jpg", new Object()).start();
            }catch (Exception e) {
                e.printStackTrace();
                return;
            }
            boolean downloadLrc = downloadLyric(music, filePath + fileName + ".lrc");
            if (downloadLrc) {//下载成功
                new Thread(new GetFileMD5(filePath + fileName + ".mp3", music)).start();
                //写入数据库
                SqliteTools.createMusic(music.get("name"), filePath + fileName  + ".mp3",
                        Integer.parseInt(sheet.get("id")), filePath + fileName  + ".jpg",
                        filePath + fileName  + ".lrc", music.get("artist"),music.get("album"));
            }
        }
    }

    //获取音乐文件url
    public static String getMusicUrl(HashMap<String, String> hashMap) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "url"));
        list.add(new BasicNameValuePair("id", hashMap.get("id")));
        list.add(new BasicNameValuePair("source", hashMap.get("source")));
        return new JSONObject(doPost(itmxueAPI, list)).getString("url");
    }

    //下载音乐文件
    private static boolean downloadMusicFile(
            HashMap<String, String> hashMap, String targetPath) {
        //获取音乐url
        String url = getMusicUrl(hashMap);
        //下载
        return getForDownload(url, targetPath);

    }

    //获取封面url
    public static String getImageUrl(HashMap<String, String> hashMap) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "pic"));
        list.add(new BasicNameValuePair("id", hashMap.get("pic_id")));
        list.add(new BasicNameValuePair("source", hashMap.get("source")));
        JSONObject jsonObject = new JSONObject(doPost(itmxueAPI, list));
        String imgUrl = jsonObject.getString("url");
        if (hashMap.get("source").equals(Sources.netease.toString())) {
            //网易云封面url处理,下载高清版😄😄
            imgUrl = imgUrl.split("\\?")[0];
        }
        if (hashMap.get("source").equals(Sources.xiami.toString())) {
            imgUrl = imgUrl.split("@")[0];
        }
        return imgUrl;
    }

    //下载封面
    private static boolean downloadPic(
            HashMap<String, String> hashMap, String targetPath) {
        String imgUrl = getImageUrl(hashMap);
        return getForDownload(imgUrl, targetPath);

    }

    //下载歌词
    public static boolean downloadLyric(
            HashMap<String, String> hashMap, String targetPath) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "lyric"));
        list.add(new BasicNameValuePair("id", hashMap.get("id")));
        list.add(new BasicNameValuePair("source", hashMap.get("source")));
        JSONObject jsonObject = new JSONObject(doPost(itmxueAPI, list));
        String lyric = jsonObject.getString("lyric");
        makeParentFolder(targetPath);
        try {
            OutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(
                            new File(targetPath)));
            byte[] buffer = lyric.getBytes();
            outputStream.write(buffer);
            outputStream.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //下载文件到指定目录
    private static boolean getForDownload(
            String url, String targetPath) {
        makeParentFolder(targetPath);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();//响应码
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream initialStream = entity.getContent();
                OutputStream outputStream = new FileOutputStream(
                        new File(targetPath));
                int bytesRead;
                byte[] buffer = new byte[8192];
                while ((bytesRead = initialStream.read(buffer, 0, 8192)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return true;
            } else {
                return false;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //发送post请求 json类型
    private static String doJsonPost(
            String url, JSONObject jsonObject) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/json");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0");
        request.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            System.out.println("请求失败，请检查网络连接");
        }
        return null;
    }

    //发送post请求 form类型
    private static String doPost(
            String url, List<NameValuePair> list) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String s = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    s = EntityUtils.toString(entity);
                }
            } finally {
                httpClient.close();
            }
        } catch (Exception e) {
            System.out.println("请求失败，请检查网络连接");
        }
        return s;
    }

    private static String doGet(String url, List<NameValuePair> list) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String s = null;
        HttpGet httpGet = new HttpGet(url + "?" + URLEncodedUtils.format(list,"utf-8"));
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        try{
            HttpResponse response = httpClient.execute(httpGet);
            s = EntityUtils.toString(response.getEntity());
        }catch (Exception e){
            System.out.println("请求失败，请检查网络连接");
        }
        return s;
    }


    //生成目录
    public static void makeParentFolder(String Path) {
        File file = new File(Path);
        File parentFile = new File(file.getParent());
        while (!parentFile.exists()) {
            //批量生成全部的父文件路径
            parentFile.mkdirs();
        }

    }

    public static void playMusic(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();//响应码
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                try {
                    MyAdvancedPlayer myAdvancedPlayer = new
                            MyAdvancedPlayer(entity.getContent(), new Object());
                    new Thread(myAdvancedPlayer).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Vector<HashMap<String, String>> result = getInternetPlaylist(1,20);

    }
}