import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HttpTools {
    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost("https://music.itmxue.cn/api.php");
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("types","search"));
            list.add(new BasicNameValuePair("count","20"));
            list.add(new BasicNameValuePair("source","netease"));
            list.add(new BasicNameValuePair("pages","1"));
            list.add(new BasicNameValuePair("name","啦啦啦"));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list,"UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if(entity != null){
                    String s = EntityUtils.toString(entity);
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.print("歌曲名： " + jsonObject.getString("name"));
                        System.out.println("  歌手：" + jsonObject.getJSONArray("artist").getString(0));
                    }

                }
            }finally {
                httpClient.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
