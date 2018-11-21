import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.io.*;

public class HttpTools {

    static String mainUrl = "https://music.itmxue.cn/api.php";

    //搜索功能
    public static Vector<HashMap<String, String>> search(String keyWord, int page){
        Vector<HashMap<String, String>> vector = new Vector<HashMap<String, String>>();
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types","search"));
        list.add(new BasicNameValuePair("count","20"));
        list.add(new BasicNameValuePair("source", "netease"));
        list.add(new BasicNameValuePair("pages", Integer.toString(page)));
        list.add(new BasicNameValuePair("name",keyWord));
        JSONArray jsonArray = new JSONArray(doPost(mainUrl, list));
        if(jsonArray != null){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id",Integer.toString(jsonObject.getInt("id")));
                hashMap.put("name",jsonObject.getString("name"));
                hashMap.put("artist",jsonObject.getJSONArray("artist").getString(0));
                hashMap.put("url_id",Integer.toString(jsonObject.getInt("url_id")));
                hashMap.put("lyric_id",Integer.toString(jsonObject.getInt("lyric_id")));
                hashMap.put("source",jsonObject.getString("source"));
                hashMap.put("album",jsonObject.getString("album"));
                try {
                    //无封面pic_id = 0
                    hashMap.put("pic_id",jsonObject.getString("pic_id"));
                }catch (JSONException e){
                    hashMap.put("pic_id","0");
                }
                vector.add(hashMap);
            }
        }
        return vector;
    }

    //下载歌曲
    public static boolean downloadMusic(HashMap<String, String> music){
        String targetPath = System.getProperty("user.dir") + "/downloads/" +
                music.get("artist") + "/" + music.get("album") +
                "/" + music.get("name") + "/" + music.get("name") + " - " + music.get("artist");
        String fileName = music.get("name") + " - " + music.get("artist");
        boolean downloadFile = downloadMusicFile(music, targetPath + ".mp3");
        boolean downloadPic = downloadPic(music, targetPath + ".jpg");
        boolean downloadLrc = downloadLyric(music, targetPath + ".lrc");

        if(downloadFile && downloadPic && downloadLrc){
            return true;
        }
        return false;
    }

    //下载音乐文件
    public static boolean downloadMusicFile(HashMap<String, String> hashMap, String targetPath){
        //获取音乐url,此url短时间可访问下载，但我们的目的还是用于得到签名构造新url
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types", "url"));
        list.add(new BasicNameValuePair("id",hashMap.get("id")));
        list.add(new BasicNameValuePair("source",hashMap.get("source")));
        String url = new JSONObject(doPost(mainUrl,list)).getString("url");
        return getForDownload(url, targetPath);

//        //获取对象存储中音乐的url，需要签名，短时间访问有效
//        String requestUrl = "https://music.itmxue.cn/download";
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("artist", hashMap.get("artist"));
//        jsonObject.put("id", Integer.parseInt(hashMap.get("id")));
//        jsonObject.put("name",hashMap.get("name"));
//        jsonObject.put("source",hashMap.get("source"));
//        jsonObject.put("url",url);
//        JSONObject result = new JSONObject(doJsonPost(requestUrl,jsonObject));
//
//        //下载
//        if(result.getInt("code") == 1){
//            String musicUrl = result.getJSONObject("data").getString("url");
//            return getForDownload(musicUrl, targetPath);
//        }
//        return false;
    }

    //下载封面
    public static boolean downloadPic(HashMap<String,String> hashMap, String targetPath){
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("types","pic"));
        list.add(new BasicNameValuePair("id",hashMap.get("pic_id")));
        list.add(new BasicNameValuePair("source",hashMap.get("netease")));
        JSONObject jsonObject = new JSONObject(doPost(mainUrl, list));
        String imgUrl = jsonObject.getString("url");
        imgUrl = imgUrl.substring(0,imgUrl.length() - 14);
        return getForDownload(imgUrl, targetPath);

    }

    //下载歌词
    public static boolean downloadLyric(HashMap<String, String> hashMap, String targetPath) {
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
    public static boolean getForDownload(String url, String targetPath){
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
    public static String doJsonPost(String url, JSONObject jsonObject){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        System.out.println(jsonObject);
        String s = null;
        try {
            StringEntity entity = new StringEntity(jsonObject.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            s = EntityUtils.toString(response.getEntity());
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    //发送post请求 form类型
    public static String doPost(String url, List<NameValuePair> list){
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

    public static void makeParentFolder(String Path){
        File file = new File(Path);
        File parentFile =  new File(file.getParent());
        if(parentFile != null){
            while (!parentFile.exists()){
                //批量生成全部的父文件路径
                parentFile.mkdirs();
            }
        }
    }

    public static void main(String[] args) {
        Vector<HashMap<String, String>> music = search("龙舌兰",1);
        downloadMusic(music.get(0));
    }
}
