package cn.ktchen.download;

import cn.ktchen.http.HttpTools;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadSingle extends Thread {
    private String url;
    private String filePath;
    private long startPos;
    private long endPos;

    private int threadId;
    private boolean isDownloadOver = false;


    public DownloadSingle(String url, String filePath, long startPos,
                        long endPos, int threadId) {
        super();
        this.url = url;
        this.filePath = filePath;
        this.startPos = startPos;
        this.endPos = endPos;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        if(endPos > startPos && !isDownloadOver) {
            try{
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 设置连接超时时间为10000ms
                conn.setConnectTimeout(10 * 1000);
                // 设置读取数据超时时间为10000ms
                conn.setReadTimeout(10 * 1000);

                DownloadFile.setHeader(conn);

                String property = "bytes=" + startPos + "-" + endPos;
                conn.setRequestProperty("RANGE", property);
                InputStream inputStream = conn.getInputStream();
                //生成目录
                HttpTools.makeParentFolder(filePath);
                OutputStream outputStream = new FileOutputStream(
                        new File(filePath));
                int bytesRead;
                byte[] buffer = new byte[8192];
                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                isDownloadOver = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
