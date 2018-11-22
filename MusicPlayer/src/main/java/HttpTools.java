import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.io.*;



public class HttpTools {

    //音乐源
    public enum Sources {netease,tencent,xiami,kugou}

    //API接口
    private   static String mainUrl = "https://music.itmxue.cn/api.php";

    //搜索功能
    public static Vector<HashMap<String, String>> search(
            String keyWord, int page){
        //默认网易云搜索
        return search(keyWord, page, Sources.netease.toString());
    }

    public static Vector<HashMap<String, String>> search(
            String keyWord, int page, String source){

        //构造post请求
        Vector<HashMap<String, String>> vector = new Vector<HashMap<String, String>>();
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types","search"));
        list.add(new BasicNameValuePair("count","20"));
        list.add(new BasicNameValuePair("source", source));
        list.add(new BasicNameValuePair("pages", Integer.toString(page)));
        list.add(new BasicNameValuePair("name",keyWord));

        //获取查询集
        JSONArray jsonArray = new JSONArray(doPost(mainUrl, list));
        if(jsonArray != null){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id",jsonObject.get("id").toString());
                hashMap.put("name",jsonObject.get("name").toString());
                hashMap.put("artist",jsonObject.getJSONArray("artist").get(0).toString());
                hashMap.put("url_id",jsonObject.get("url_id").toString());
                hashMap.put("lyric_id",jsonObject.get("lyric_id").toString());
                hashMap.put("source",jsonObject.get("source").toString());
                hashMap.put("album",jsonObject.get("album").toString());
                hashMap.put("pic_id",jsonObject.get("pic_id").toString());

                vector.add(hashMap);
            }
        }
        return vector;
    }

    //下载歌曲
    public static boolean downloadMusic(
            HashMap<String, String> music){
        //目标路径
        String targetPath = System.getProperty("user.dir") + "/downloads/" +
                            music.get("artist") + "/" + music.get("album") +
                            "/" + music.get("name") + "/" + music.get("name") +
                            " - " + music.get("artist");
        boolean downloadFile = downloadMusicFile(music, targetPath + ".mp3");
        boolean downloadPic = downloadPic(music, targetPath + ".jpg");
        boolean downloadLrc = downloadLyric(music, targetPath + ".lrc");

        if(downloadFile && downloadPic && downloadLrc){
            //下载成功
            return true;
        }
        return false;
    }

    //下载音乐文件
    private static boolean downloadMusicFile(
            HashMap<String, String> hashMap, String targetPath){
        //获取音乐url,此url可能为空
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "url"));
        list.add(new BasicNameValuePair("id",hashMap.get("id")));
        list.add(new BasicNameValuePair("source",hashMap.get("source")));
        String url = new JSONObject(doPost(mainUrl,list)).getString("url");
        if(url.equals("")){
            url = "https://music.163.com/song/media/outer/url?id=" + hashMap.get("id") + ".mp3";
        }

        //获取对象存储中音乐的url，需要签名，短时间访问有效
        String requestUrl = "https://music.itmxue.cn/download";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("artist", hashMap.get("artist"));
        jsonObject.put("id", hashMap.get("id"));
        jsonObject.put("name",hashMap.get("name"));
        jsonObject.put("source",hashMap.get("source"));
        jsonObject.put("url",url);
        JSONObject result = new JSONObject(doJsonPost(requestUrl,jsonObject));

        //下载
        if(result.getInt("code") == 1){
            String musicUrl = result.getJSONObject("data").getString("url");
            return getForDownload(musicUrl, targetPath);
        }
        return false;
    }

    //下载封面
    private static boolean downloadPic(
            HashMap<String,String> hashMap, String targetPath){
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types","pic"));
        list.add(new BasicNameValuePair("id",hashMap.get("pic_id")));
        list.add(new BasicNameValuePair("source",hashMap.get("source")));
        JSONObject jsonObject = new JSONObject(doPost(mainUrl, list));
        String imgUrl = jsonObject.getString("url");
        if(hashMap.get("source").toString().equals(Sources.netease.toString())){
            //网易云封面url处理,下载高清版😄😄
            imgUrl = imgUrl.split("\\?")[0];
        }
        if(hashMap.get("source").toString().equals(Sources.xiami.toString())){
            imgUrl = imgUrl.split("@")[0];
        }
        return getForDownload(imgUrl, targetPath);

    }

    //下载歌词
    private static boolean downloadLyric(
            HashMap<String, String> hashMap, String targetPath) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types","lyric"));
        list.add(new BasicNameValuePair("id", hashMap.get("id")));
        list.add(new BasicNameValuePair("source",hashMap.get("source")));
        JSONObject jsonObject = new JSONObject(doPost(mainUrl,list));
        if(jsonObject != null){
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
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //下载文件到指定目录
    private static boolean getForDownload(
            String url, String targetPath){
        makeParentFolder(targetPath);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();//响应码
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream initialStream = entity.getContent();
                OutputStream outputStream = new FileOutputStream(
                        new File(targetPath));
                int bytesRead;
                byte[] buffer = new byte[8192];
                while ((bytesRead = initialStream.read(buffer,0,8192)) != -1){
                    outputStream.write(buffer,0,bytesRead);
                }
                return true;
            }else {
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
        request.setHeader("Content-Type","application/json");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0");
        request.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200){
                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //发送post请求 form类型
    private static String doPost(
            String url, List<NameValuePair> list){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String s = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list,"UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    s = EntityUtils.toString(entity);

                }
            }finally {
                httpClient.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

    //生成目录
    private static void makeParentFolder(String Path){
        File file = new File(Path);
        File parentFile =  new File(file.getParent());
        if(parentFile != null){
            while (!parentFile.exists()){
                //批量生成全部的父文件路径
                parentFile.mkdirs();
            }
        }
    }

    public static void playMusic(String url){
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
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String musicUrl = "https://m8.music.126.net/20181122181150/c7c3fd42eda56aec593f081d14f5091a/ymusic/a1cf/1381/deaa/fa8e84b88f0db4d7bc0da00983a516bf.mp3";
        playMusic(musicUrl);
        URL urlfile = new URL(musicUrl);
        URLConnection con = urlfile.openConnection();
        int b = con.getContentLength();
        BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
        Bitstream bt = new Bitstream(bis);
        Header h = bt.readFrame();
        System.out.println(h.total_ms(b));
    }

}
