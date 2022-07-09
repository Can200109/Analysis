package com.example.analysis.utill;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AUtill {
    /**
     *
     * 获取重定向后的URL地址
     *
     * @param httpURL
     *            原URL
     * @return 重定向后的URL
     */
    public static String getRedirectsURL(String httpURL) {
        String result = null;
        HttpURLConnection conn = null;
        try {
            // 配置请求头
            conn = (HttpURLConnection) new URL(httpURL).openConnection();
            // 禁止重定向
            conn.setInstanceFollowRedirects(false);
            // 获取重定向后的URL
            result = conn.getHeaderField("Location");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 清理资源
            conn.disconnect();
        }
        return result;
    }

    /**
     *
     * 以GET的方式请求JSON数据
     *
     * @param httpURL
     *            请求的URL
     * @return 请求到的JSON数据
     */
    public static String getURLData(String httpURL) throws Exception {
        //创建URL对象  参数设置需要爬取的网址 也就是我们方法传过来的参数
        URL url = new URL(httpURL);

        //得到一个HttpURLConnection 对象
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        //防止出现Server returned HTTP response code: 403 for URL 的错误
        //也就是服务器的安全设置不接受Java程序作为客户端访问  所以我们进行安全设置
        huc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //通过HttpURLConnection获得输入流对象
        InputStream is = huc.getInputStream();

        //使用缓冲字符输入流获取源码  设置编码
        BufferedReader r = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        StringBuffer sbf = new StringBuffer();
        String line;

        //写入文件中
        String temp = null;
        while ((temp = r.readLine()) != null) {
            sbf.append(temp);
        }

        //关闭资源
        r.close();
        is.close();
        return sbf.toString();
    }
    /**
     * 小红书请求头设置
     */
    public static Map<String, String> getHeaderMap() {

        String cookie = openFile("http://www.pangguo.top:5555/cookies/cookie.txt");
        System.out.println(cookie);
        Map<String, String> map = new HashMap<>(new LinkedHashMap<>());

//        if (StringUtils.isNotEmpty(ip) && !ip.contains("127.0.0.1") && !ip.contains("192.168")) {
//            map.put("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
//            map.put("Accept-Encoding", "gzip, deflate");
//            map.put("x-forwarded-for", ip);
//            map.put("Proxy-Client-IP", ip);
//            map.put("WL-Proxy-Client-IP", ip);
//            map.put("HTTP_CLIENT_IP", ip);
//            map.put("HTTP_X_FORWARDED_FOR", ip);
//        }
        map.put("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        map.put("accept-encoding", "gzip, deflate, br");
        map.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        map.put("cache-control", "max-age=0");
        map.put("sec-ch-ua-platform","Android");
        map.put("cookie", cookie);
        map.put("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Mobile Safari/537.36 Edg/103.0.1264.49");
        return map;
    }

    private static String openFile(String filePath) {
        int HttpResult; // 服务器返回的状态
        String ee = new String();
        try
        {
            URL url =new URL(filePath); // 创建URL
            URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
            urlconn.connect();
            HttpURLConnection httpconn =(HttpURLConnection)urlconn;
            HttpResult = httpconn.getResponseCode();
            if(HttpResult != HttpURLConnection.HTTP_OK) {
                System.out.print("无法连接到");
            } else {
                int filesize = urlconn.getContentLength(); // 取数据长度
                InputStreamReader isReader = new InputStreamReader(urlconn.getInputStream(),"UTF-8");
                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer buffer = new StringBuffer();
                String line; // 用来保存每行读取的内容
                line = reader.readLine(); // 读取第一行
                while (line != null) { // 如果 line 为空说明读完了
                    buffer.append(line); // 将读到的内容添加到 buffer 中
                    buffer.append(" "); // 添加换行符
                    line = reader.readLine(); // 读取下一行
                }
                System.out.print(buffer.toString());
                ee = buffer.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  ee;
    }
}
