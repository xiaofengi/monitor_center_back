package org.hdu.crawler.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

public class TestUtil {

    public static void main(String[] args) {
        String urlInfo = "itag=22\\u0026type=video%2Fmp4%3B+codecs%3D%22avc1.64001F%2C+mp4a.40.2%22\\u0026quality=hd720\\u0026url=https%3A%2F%2Fr5---sn-ab5l6nzk.googlevideo.com%2Fvideoplayback%3Fc%3DWEB%26id%3Do-AHWG9t2v8o7lku_A0w12t0Xa_0_8E36zBm0cO2NZxH0L%26dur%3D89.443%26signature%3DC601CEA1E4F69075DA59214C7B6C18BA7F672994.CBE7A540A8D5AD26428F84776594281964E81775%26mm%3D31%252C29%26mn%3Dsn-ab5l6nzk%252Csn-ab5sznl7%26expire%3D1525978450%26fvip%3D5%26ms%3Dau%252Crdu%26source%3Dyoutube%26mv%3Dm%26ip%3D207.246.90.158%26key%3Dyt6%26lmt%3D1518007403184966%26ipbits%3D0%26ei%3D8UD0WpuBMZmk8wTm2p-IBQ%26sparams%3Ddur%252Cei%252Cid%252Cip%252Cipbits%252Citag%252Clmt%252Cmime%252Cmm%252Cmn%252Cms%252Cmv%252Cpl%252Cratebypass%252Crequiressl%252Csource%252Cexpire%26ratebypass%3Dyes%26pl%3D25%26mime%3Dvideo%252Fmp4%26mt%3D1525956725%26itag%3D22%26requiressl%3Dyes,itag=43\\u0026type=video%2Fwebm%3B+codecs%3D%22vp8.0%2C+vorbis%22\\u0026quality=medium\\u0026url=https%3A%2F%2Fr5---sn-ab5l6nzk.googlevideo.com%2Fvideoplayback%3Fc%3DWEB%26mime%3Dvideo%252Fwebm%26signature%3DC248EA7A8A3E882C3643FA8A3E540F0E8794AD80.3923CA6AEFC9F76CCDFCA6CAEA509003E3D16F48%26ipbits%3D0%26itag%3D43%26sparams%3Dclen%252Cdur%252Cei%252Cgir%252Cid%252Cip%252Cipbits%252Citag%252Clmt%252Cmime%252Cmm%252Cmn%252Cms%252Cmv%252Cpl%252Cratebypass%252Crequiressl%252Csource%252Cexpire%26ratebypass%3Dyes%26requiressl%3Dyes%26id%3Do-AHWG9t2v8o7lku_A0w12t0Xa_0_8E36zBm0cO2NZxH0L%26dur%3D0.000%26mm%3D31%252C29%26mn%3Dsn-ab5l6nzk%252Csn-ab5sznl7%26fvip%3D5%26ms%3Dau%252Crdu%26source%3Dyoutube%26mv%3Dm%26ip%3D207.246.90.158%26key%3Dyt6%26lmt%3D1518008461453955%26ei%3D8UD0WpuBMZmk8wTm2p-IBQ%26pl%3D25%26gir%3Dyes%26mt%3D1525956725%26expire%3D1525978450%26clen%3D8182509,itag=18\\u0026type=video%2Fmp4%3B+codecs%3D%22avc1.42001E%2C+mp4a.40.2%22\\u0026quality=medium\\u0026url=https%3A%2F%2Fr5---sn-ab5l6nzk.googlevideo.com%2Fvideoplayback%3Fc%3DWEB%26mime%3Dvideo%252Fmp4%26signature%3D8B5B3B632CCADBC7A41848569F7D9925EEAD6071.2D0821AADA5D95AE0DA405AFBC260DF8DA65886E%26ipbits%3D0%26itag%3D18%26sparams%3Dclen%252Cdur%252Cei%252Cgir%252Cid%252Cip%252Cipbits%252Citag%252Clmt%252Cmime%252Cmm%252Cmn%252Cms%252Cmv%252Cpl%252Cratebypass%252Crequiressl%252Csource%252Cexpire%26ratebypass%3Dyes%26requiressl%3Dyes%26id%3Do-AHWG9t2v8o7lku_A0w12t0Xa_0_8E36zBm0cO2NZxH0L%26dur%3D89.443%26mm%3D31%252C29%26mn%3Dsn-ab5l6nzk%252Csn-ab5sznl7%26fvip%3D5%26ms%3Dau%252Crdu%26source%3Dyoutube%26mv%3Dm%26ip%3D207.246.90.158%26key%3Dyt6%26lmt%3D1518006883882291%26ei%3D8UD0WpuBMZmk8wTm2p-IBQ%26pl%3D25%26gir%3Dyes%26mt%3D1525956725%26expire%3D1525978450%26clen%3D7830794,itag=36\\u0026type=video%2F3gpp%3B+codecs%3D%22mp4v.20.3%2C+mp4a.40.2%22\\u0026quality=small\\u0026url=https%3A%2F%2Fr5---sn-ab5l6nzk.googlevideo.com%2Fvideoplayback%3Fc%3DWEB%26id%3Do-AHWG9t2v8o7lku_A0w12t0Xa_0_8E36zBm0cO2NZxH0L%26dur%3D89.489%26signature%3D08CD720F3C025661E1C596DE593D30DCD14F12AB.60937B35D4976EDDF348D3EC25836946D5A42113%26mm%3D31%252C29%26mn%3Dsn-ab5l6nzk%252Csn-ab5sznl7%26fvip%3D5%26ms%3Dau%252Crdu%26source%3Dyoutube%26mv%3Dm%26itag%3D36%26ip%3D207.246.90.158%26key%3Dyt6%26lmt%3D1518006884480831%26ipbits%3D0%26ei%3D8UD0WpuBMZmk8wTm2p-IBQ%26sparams%3Dclen%252Cdur%252Cei%252Cgir%252Cid%252Cip%252Cipbits%252Citag%252Clmt%252Cmime%252Cmm%252Cmn%252Cms%252Cmv%252Cpl%252Crequiressl%252Csource%252Cexpire%26pl%3D25%26gir%3Dyes%26mime%3Dvideo%252F3gpp%26mt%3D1525956725%26expire%3D1525978450%26clen%3D2502323%26requiressl%3Dyes,itag=17\\u0026type=video%2F3gpp%3B+codecs%3D%22mp4v.20.3%2C+mp4a.40.2%22\\u0026quality=small\\u0026url=https%3A%2F%2Fr5---sn-ab5l6nzk.googlevideo.com%2Fvideoplayback%3Fc%3DWEB%26id%3Do-AHWG9t2v8o7lku_A0w12t0Xa_0_8E36zBm0cO2NZxH0L%26dur%3D89.489%26signature%3DC76B71530247EAD20F1875E01E28FF8919C99F36.C5E534EF92EDA92590D2387ABA69932AB6110CBE%26mm%3D31%252C29%26mn%3Dsn-ab5l6nzk%252Csn-ab5sznl7%26fvip%3D5%26ms%3Dau%252Crdu%26source%3Dyoutube%26mv%3Dm%26itag%3D17%26ip%3D207.246.90.158%26key%3Dyt6%26lmt%3D1518006887786111%26ipbits%3D0%26ei%3D8UD0WpuBMZmk8wTm2p-IBQ%26sparams%3Dclen%252Cdur%252Cei%252Cgir%252Cid%252Cip%252Cipbits%252Citag%252Clmt%252Cmime%252Cmm%252Cmn%252Cms%252Cmv%252Cpl%252Crequiressl%252Csource%252Cexpire%26pl%3D25%26gir%3Dyes%26mime%3Dvideo%252F3gpp%26mt%3D1525956725%26expire%3D1525978450%26clen%3D929744%26requiressl%3Dyes";
        String[] videoUrlInfos = urlInfo.split(",");
        for(String videoUrlInfo : videoUrlInfos) {
            /*try {
                videoUrlInfo = URLDecoder.decode(videoUrlInfo, "utf-8");
                System.out.println(videoUrlInfo);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
            String[] videoUrlParams = videoUrlInfo.split("\\\\u0026");
            for(String videoUrlParam : videoUrlParams){
                String[] paramKeyValue = videoUrlParam.split("=");
                if(paramKeyValue[0].equals("url")) {
                    try {
                        String videoUrl = URLDecoder.decode(paramKeyValue[1], "utf-8");
                        System.out.println(videoUrl);
/*                        String[] params = videoUrl.split("&");
                        for(String param : params){
                            System.out.println(param);
                        }*/
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
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
