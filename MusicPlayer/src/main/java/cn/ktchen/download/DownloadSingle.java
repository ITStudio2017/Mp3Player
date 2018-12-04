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
    private final Object lock;

    public DownloadSingle(String url, String filePath, Object lock) {
        super();
        this.url = url;
        this.filePath = filePath;
        this.lock = lock;
    }

    @Override
    public void run() {
        //url为空则直接返回
        if (url.equals("") || url == null){
            return;
        }
        try{
            synchronized (lock){
                //防止重复下载
                File file = new File(filePath);
                if (file.exists())
                    return;

                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 设置连接超时时间为10000ms
                conn.setConnectTimeout(10 * 1000);
                // 设置读取数据超时时间为10000ms
                conn.setReadTimeout(10 * 1000);
                // 设置http头部
                DownloadFile.setHeader(conn);

                int contentLength = conn.getContentLength();

                InputStream inputStream = conn.getInputStream();
                //生成目录
                HttpTools.makeParentFolder(filePath);
                byte[] byteContent = new byte[contentLength];
                int bytesRead;
                byte[] buffer = new byte[8192];
                int index = 0;
                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    for (int i = 0; i < bytesRead; i++) {
                        byteContent[index] = buffer[i];
                        index++;
                    }
                }
                OutputStream outputStream = new FileOutputStream(
                        new File(filePath));
                outputStream.write(byteContent, 0, index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


