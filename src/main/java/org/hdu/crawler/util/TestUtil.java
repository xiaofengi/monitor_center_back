package org.hdu.crawler.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.Executor;

public class TestUtil {

    public static void main(String[] args) {
        System.out.println("To enable your free eval account and get "
                +"CUSTOMER, YOURZONE and YOURPASS, please contact "
                +"sales@luminati.io");
       /* HttpHost proxy = new HttpHost("zproxy.lum-superproxy.io", 22225);
        String res = Executor.newInstance()
                .auth(proxy, "lum-customer-hl_e2d00ce2-zone-static-country-us", "qebyi23uouhr")
                .execute(Request.Get("http://lumtest.com/myip.json").viaProxy(proxy))
                .returnContent().asString();
        System.out.println(res);*/
//        String proxyList="[{'HTTP': '178.57.115.124:8080'}, {'HTTP': '5.45.105.223:80'}, {'HTTP': '204.13.164.179:80'}, {'HTTP': '189.100.57.164:3128'}, {'HTTP': '5.45.102.29:80'}, {'HTTP': '49.140.44.88:8998'}, {'HTTP': '122.139.49.102:8888'}, {'HTTP': '117.135.250.89:80'}, {'HTTP': '222.129.110.205:81'}, {'HTTP': '51.254.106.64:80'}, {'HTTP': '175.139.62.163:80'}, {'HTTP': '60.13.74.143:80'}, {'HTTP': '101.201.235.141:8000'}, {'HTTP': '122.13.163.109:81'}, {'HTTP': '111.222.227.254:8118'}, {'HTTP': '192.200.106.156:8080'}, {'HTTP': '190.198.191.136:8080'}, {'HTTP': '52.10.30.100:8083'}, {'HTTP': '158.69.237.1:3128'}, {'HTTP': '124.243.210.13:8888'}, {'HTTP': '177.87.241.226:8080'}, {'HTTP': '43.224.234.86:80'}, {'HTTP': '58.208.16.141:808'}, {'HTTP': '124.133.240.88:8080'}, {'HTTP': '123.97.26.20:8888'}, {'HTTP': '94.229.91.51:80'}, {'HTTP': '78.36.1.169:3128'}, {'HTTP': '111.126.245.230:8888'}, {'HTTP': '139.162.36.131:8080'}, {'HTTP': '104.40.159.62:8118'}, {'HTTP': '84.28.221.68:80'}, {'HTTP': '42.123.89.120:8888'}, {'HTTP': '61.62.160.137:8080'}, {'HTTP': '115.249.2.192:8080'}, {'HTTP': '218.92.224.131:80'}, {'HTTP': '51.254.106.67:80'}, {'HTTP': '200.105.202.195:3128'}, {'HTTP': '178.62.125.124:8118'}, {'HTTP': '139.59.226.60:8000'}, {'HTTP': '39.80.207.94:81'}, {'HTTP': '198.98.122.83:80'}, {'HTTP': '45.40.143.57:80'}, {'HTTP': '52.90.38.120:8083'}, {'HTTP': '54.82.188.49:3128'}, {'HTTP': '195.182.141.2:3128'}, {'HTTP': '169.50.87.249:80'}, {'HTTP': '149.202.95.110:80'}, {'HTTP': '54.201.175.33:8083'}, {'HTTP': '95.220.33.222:80'}, {'HTTP': '198.23.74.170:81'}, {'HTTP': '198.50.211.54:80'}, {'HTTP': '51.254.106.74:80'}, {'HTTP': '47.88.102.97:8000'}, {'HTTP': '52.88.198.107:8083'}, {'HTTP': '51.254.106.79:80'}, {'HTTP': '176.31.165.141:3128'}, {'HTTP': '51.254.106.65:80'}, {'HTTP': '64.62.233.67:80'}, {'HTTP': '52.42.236.235:80'}, {'HTTP': '24.108.220.150:80'}, {'HTTP': '52.34.194.84:8083'}, {'HTTP': '87.98.155.63:80'}, {'HTTP': '94.23.17.157:80'}, {'HTTP': '178.238.229.236:80'}, {'HTTP': '149.56.35.226:80'}, {'HTTP': '128.123.226.59:8080'}, {'HTTP': '188.165.121.240:80'}, {'HTTP': '149.202.195.236:80'}, {'HTTP': '85.143.24.70:80'}, {'HTTP': '69.70.183.110:80'}, {'HTTP': '190.63.130.252:80'}, {'HTTP': '51.254.132.238:80'}, {'HTTP': '117.36.197.152:3128'}, {'HTTP': '47.89.53.92:3128'}, {'HTTP': '51.254.106.76:80'}, {'HTTP': '183.35.139.223:81'}, {'HTTP': '93.63.142.144:80'}, {'HTTP': '175.17.211.170:8888'}, {'HTTP': '183.131.151.208:80'}, {'HTTP': '45.62.246.212:8080'}, {'HTTP': '112.255.179.92:8888'}, {'HTTP': '58.71.17.34:80'}, {'HTTP': '91.134.230.98:80'}, {'HTTP': '108.59.10.129:55555'}, {'HTTP': '218.8.79.165:8888'}, {'HTTP': '183.238.80.236:8118'}, {'HTTP': '101.50.3.222:80'}, {'HTTP': '222.133.31.130:2226'}, {'HTTP': '112.85.115.89:10000'}, {'HTTP': '175.180.96.188:8080'}, {'HTTP': '81.134.199.38:3128'}, {'HTTP': '37.187.115.112:80'}, {'HTTP': '95.240.172.208:8080'}, {'HTTP': '169.45.213.73:80'}, {'HTTP': '116.26.222.20:81'}, {'HTTP': '210.245.87.175:80'}, {'HTTP': '93.236.99.19:80'}, {'HTTP': '1.52.162.20:8888'}, {'SOCKS5': '131.161.6.229:43803'}, {'HTTP': '181.113.29.26:3128'}, {'SOCKS5': '138.186.77.146:43803'}, {'SOCKS5': '95.129.250.224:48111'}, {'SOCKS5': '167.250.26.200:43803'}, {'SOCKS5': '138.186.92.201:43803'}, {'SOCKS5': '138.118.205.182:43803'}, {'HTTP': '51.255.161.222:80'}, {'HTTP': '221.4.169.82:8080'}, {'HTTP': '94.23.29.110:80'}, {'HTTP': '178.62.31.120:8118'}, {'HTTP': '213.57.90.253:18000'}, {'HTTP': '50.81.91.68:8888'}, {'HTTP': '84.39.48.187:80'}, {'HTTP': '101.201.80.68:808'}, {'HTTP': '167.114.195.242:3128'}, {'HTTP': '149.202.249.206:3128'}, {'HTTP': '201.241.88.63:80'}, {'HTTP': '164.132.222.249:80'}, {'HTTP': '180.169.8.51:3128'}, {'HTTP': '173.68.185.170:80'}, {'HTTP': '70.89.14.22:8118'}, {'HTTP': '54.200.99.208:8083'}, {'HTTP': '195.4.133.227:8080'}, {'HTTP': '173.161.0.227:80'}, {'HTTP': '184.173.139.10:80'}, {'HTTP': '101.67.3.119:81'}, {'HTTP': '169.50.87.252:80'}, {'HTTP': '74.208.146.112:80'}, {'HTTP': '5.2.205.177:8080'}, {'HTTP': '124.42.7.103:80'}, {'HTTP': '1.182.108.188:8888'}, {'HTTP': '52.34.78.75:80'}, {'HTTP': '152.26.91.86:8080'}, {'HTTP': '114.39.32.209:8998'}, {'HTTP': '106.75.17.129:808'}, {'HTTP': '220.225.245.109:8000'}, {'HTTP': '52.67.10.231:3128'}, {'HTTP': '27.8.61.173:8888'}, {'HTTP': '58.247.30.222:8080'}, {'HTTP': '210.57.208.14:80'}, {'HTTP': '123.132.44.222:8888'}, {'HTTP': '178.62.54.134:8118'}, {'HTTP': '52.53.237.11:8083'}, {'HTTP': '123.241.211.161:8998'}, {'HTTP': '81.46.212.102:80'}, {'HTTP': '123.57.190.51:7777'}, {'HTTP': '117.169.17.242:80'}, {'HTTP': '191.242.63.187:8080'}, {'HTTP': '213.197.20.136:80'}, {'HTTP': '163.158.216.152:80'}, {'HTTP': '106.75.128.90:80'}, {'HTTP': '223.12.40.143:8888'}, {'HTTP': '117.218.159.232:8000'}, {'HTTP': '122.195.181.46:8888'}, {'HTTP': '211.143.45.216:3128'}, {'HTTP': '84.39.40.62:80'}, {'HTTP': '89.250.207.195:80'}, {'HTTP': '36.7.172.18:82'}, {'HTTP': '182.38.100.56:81'}, {'HTTP': '123.120.205.70:81'}, {'HTTP': '124.133.230.254:80'}, {'HTTP': '52.29.95.106:80'}, {'HTTP': '112.226.254.225:8888'}, {'HTTP': '112.196.5.76:3128'}, {'HTTP': '37.188.72.122:80'}, {'HTTP': '123.136.104.34:80'}, {'HTTP': '36.22.96.128:8888'}, {'HTTP': '23.24.89.194:7004'}, {'HTTP': '88.191.174.188:80'}, {'HTTP': '89.98.24.196:80'}, {'HTTP': '220.101.93.3:3128'}, {'HTTP': '36.228.110.165:8998'}, {'HTTP': '111.13.136.46:80'}, {'HTTP': '222.82.222.242:9999'}, {'HTTP': '180.103.131.65:808'}, {'HTTP': '112.244.186.46:8888'}, {'HTTP': '101.100.201.203:80'}, {'HTTP': '219.117.214.131:80'}, {'HTTP': '207.188.73.155:80'}, {'HTTP': '119.167.80.51:8080'}, {'HTTP': '52.34.218.183:8083'}, {'HTTP': '200.255.220.211:8080'}, {'HTTP': '190.201.102.101:8080'}, {'HTTP': '71.92.222.98:3128'}, {'HTTP': '187.86.249.173:80'}, {'HTTP': '5.141.9.86:8080'}, {'HTTP': '175.168.167.39:8888'}, {'HTTP': '45.55.206.119:80'}, {'HTTP': '112.81.19.71:8888'}, {'HTTP': '104.233.80.24:80'}, {'HTTP': '202.111.9.106:23'}, {'HTTP': '220.130.196.155:8080'}, {'HTTP': '120.90.6.92:8080'}, {'HTTP': '111.28.178.94:8888'}, {'HTTP': '193.124.184.171:80'}, {'HTTP': '212.58.204.190:3128'}, {'HTTP': '139.59.11.87:80'}, {'HTTP': '91.134.230.97:80'}, {'HTTP': '39.76.187.36:81'}, {'HTTP': '223.68.228.85:8888'}, {'HTTP': '87.120.162.222:3128'}]";
//        JSONArray jsonArray = new JSONArray(proxyList);
//       /* for(int i=0; i<jsonArray.length(); i++){
//            JSONObject object = jsonArray.getJSONObject(i);
//            if(object.has("HTTP")) {
//                boolean success = testProxy(object.getString("HTTP"));
//                if(success){
//                    break;
//                }
//            }
//        }*/
//        String ipAndPort = "153.149.168.28" + ":" + "3128";
//        testProxy(ipAndPort);
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
