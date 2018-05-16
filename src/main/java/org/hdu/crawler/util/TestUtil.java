package org.hdu.crawler.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtil {

    public static void main(String[] args) {
        String resultStats = "找到约 1,320,000 条结果";
        try {
            Matcher matcher = Pattern.compile("(?<=找到约).*(?=条结果)").matcher(resultStats);
            if(matcher.find()){
                String numText = matcher.group().trim();
                DecimalFormat df = new DecimalFormat(",###,##0");
                System.out.println(df.parse(numText).longValue());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static boolean testProxy(String ipAndPort) {
        System.out.println("正在使用代理ip：" + ipAndPort);
        BufferedReader reader = null;
        try {
            String targetUrl = "http://www.google.com";
            HttpURLConnection connection;
            URL link = new URL(targetUrl);
            String charset = "UTF-8";
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress((ipAndPort.split(":"))[0], Integer.parseInt((ipAndPort.split(":"))[1])));
            connection = (HttpURLConnection)link.openConnection(proxy);
            connection.setDoOutput(true);
            connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Charset", charset);
            //connection.setRequestProperty("Referer", "");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("Cookie", "NID=129=W5g_bwgaFIlcRxDVyzYjXa1RmGU5uAXRV52BOOIHA3ditgkbmUmvoThpdGFScisJpxRzN6-OS1-E0hmo1miJ8W5fY2u7oiqO-mHMT4tTb541Osi3CP8WwBBRfVPKLws26ohtGUtldMJkerjLs58zD0Dnyjvx2t69j3riPlMDrLIh7sF6gP4NV8OvJBgL30YdyusDlsbzKKcyKQdk9o-OxR8MKtNoDPEPd9iVoKhoN8Q7FPU; DV=g9BfJ1BW3qIfsLwFPdwP7UQnPtOgMxY; 1P_JAR=2018-05-07-10");
            connection.setRequestProperty("x-client-data", "CJa2yQEIprbJAQjBtskBCKmdygEIsp3KAQioo8oBGJKjygE=");
            connection.setUseCaches(false);
            connection.setReadTimeout(8000);
            int code = connection.getResponseCode();

            if (code != 200) {
                System.out.println("使用代理IP连接网络失败，状态码：" + connection.getResponseCode());
                return false;
            }else {
                String line;
                StringBuilder html = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
                while((line = reader.readLine()) != null){
                    html.append(line);
                }
                connection.disconnect();
                System.out.println("请求" + targetUrl + ", 得到如下信息：");
                System.out.println(html.toString());
                System.out.println("成功代理ip：" + ipAndPort);
                return true;
            }
        } catch (Exception e) {
            System.err.println("发生异常：" + e.getMessage());
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

}
