package cn.ktchen.http;

import cn.ktchen.sqlite.SqliteTools;
import javazoom.jl.player.advanced.AdvancedPlayer;
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
    private static String mainUrl = "https://music_api.dns.24mz.cn/index.php";

    //搜索功能
    public static Vector<HashMap<String, String>> search(
            String keyWord, int page) {
        //默认网易云搜索
        return search(keyWord, page, Sources.netease.toString());
    }

    public static Vector<HashMap<String, String>> search(
            String keyWord, int page, String source) {

        //构造post请求
        Vector<HashMap<String, String>> vector = new Vector<HashMap<String, String>>();
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "search"));
        list.add(new BasicNameValuePair("count", "20"));
        list.add(new BasicNameValuePair("source", source));
        list.add(new BasicNameValuePair("pages", Integer.toString(page)));
        list.add(new BasicNameValuePair("name", keyWord));

        //获取查询集
        JSONArray jsonArray = new JSONArray(doGet(mainUrl, list));
        if (jsonArray != null) {
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
        }
        return vector;
    }

    //下载歌曲
    public static boolean downloadMusic(
            HashMap<String, String> music, HashMap<String,String> sheet) {

        //检查本地是否下载
        if(SqliteTools.musicExist(music))
            return true;

        //目标路径
        String targetPath = System.getProperty("user.dir") + "/downloads/" +
                music.get("artist") + "/" + music.get("album") +
                "/" + music.get("name") + "/" + music.get("name") +
                " - " + music.get("artist");
        boolean downloadFile = downloadMusicFile(music, targetPath + ".mp3");
        boolean downloadPic = downloadPic(music, targetPath + ".jpg");
        boolean downloadLrc = downloadLyric(music, targetPath + ".lrc");

        if (downloadFile && downloadPic && downloadLrc) {//下载成功

            //写入数据库
            SqliteTools.createMusic(music.get("name"), targetPath + ".mp3",
                    Integer.parseInt(sheet.get("id")), targetPath + ".jpg",
                    targetPath + ".lrc", music.get("artist"),music.get("album"));
            return true;
        }
        return false;
    }

    //下载音乐文件
    private static boolean downloadMusicFile(
            HashMap<String, String> hashMap, String targetPath) {
        //获取音乐url,此url可能为空
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "url"));
        list.add(new BasicNameValuePair("id", hashMap.get("id")));
        list.add(new BasicNameValuePair("source", hashMap.get("source")));
        String url = new JSONObject(doGet(mainUrl, list)).getString("url");

        //下载
        return getForDownload(url, targetPath);

    }

    //下载封面
    private static boolean downloadPic(
            HashMap<String, String> hashMap, String targetPath) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "pic"));
        list.add(new BasicNameValuePair("id", hashMap.get("pic_id")));
        list.add(new BasicNameValuePair("source", hashMap.get("source")));
        JSONObject jsonObject = new JSONObject(doGet(mainUrl, list));
        String imgUrl = jsonObject.getString("url");
        if (hashMap.get("source").toString().equals(Sources.netease.toString())) {
            //网易云封面url处理,下载高清版😄😄
            imgUrl = imgUrl.split("\\?")[0];
        }
        if (hashMap.get("source").toString().equals(Sources.xiami.toString())) {
            imgUrl = imgUrl.split("@")[0];
        }
        return getForDownload(imgUrl, targetPath);

    }

    //下载歌词
    private static boolean downloadLyric(
            HashMap<String, String> hashMap, String targetPath) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "lyric"));
        list.add(new BasicNameValuePair("id", hashMap.get("id")));
        list.add(new BasicNameValuePair("source", hashMap.get("source")));
        JSONObject jsonObject = new JSONObject(doGet(mainUrl, list));
        if (jsonObject != null) {
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }finally {
            System.out.println(s);
            return s;
        }

    }


    //生成目录
    public static void makeParentFolder(String Path) {
        File file = new File(Path);
        File parentFile = new File(file.getParent());
        if (parentFile != null) {
            while (!parentFile.exists()) {
                //批量生成全部的父文件路径
                parentFile.mkdirs();
            }
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

    public static void main(String[] args) throws Exception {
//        String musicUrl = "https://m7.music.126.net/20181124123018/49d0ecf1aca3cfece55a0f96c95c28ce/ymusic/931b/c2a2/9662/df3b7e6cbff7acaf30a32bfb9df824c2.mp3";
//        URL urlfile = new URL(musicUrl);
//        URLConnection conn = urlfile.openConnection();
//        // 设置连接超时时间为10000ms
//        conn.setConnectTimeout(10 * 1000);
//        // 设置读取数据超时时间为10000ms
//        conn.setReadTimeout(10 * 1000);
//        int b = conn.getContentLength();
//        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
//        JavaSoundAudioDevice javaSoundAudioDevice = new JavaSoundAudioDevice();
//        Bitstream bitstream = new Bitstream(bis);
//        Header h = bitstream.readFrame();
//        InputStream stream = conn.getInputStream();
//        int Size = 800 * (h.framesize + 5);
//        int index = 0;
//        byte[] bts = new byte[b];
//        while (true) {
//            byte[] bt = new byte[1024];
//            if (stream.read(bt) != -1) {
//                for (int i = 0; i < 1024; i++) {
//                    bts[index] = bt[i];
//                    index++;
//                }
//                continue;
//            }
//            break;
//        }
//        System.out.println(b /Size);
//        for (int i = 0; i < b / Size - 2; i++) {
//            OutputStream outputStream = new FileOutputStream(
//                            new File(System.getProperty("user.dir") + "/test/" + i + ".mp3"));
//            outputStream.write(bts,Size * i,Size);
//
//        }
        Vector<InputStream> vector = new Vector<InputStream>();
        for (int i = 0; i < 6; i++) {
            InputStream inputStream = new FileInputStream(
                    new File(System.getProperty("user.dir") + "/test/" + i + ".mp3"));

            vector.add(inputStream);
        }
        Enumeration<InputStream> e = vector.elements();
        SequenceInputStream sis = new SequenceInputStream(e);
        new AdvancedPlayer(sis).play();
    }
}