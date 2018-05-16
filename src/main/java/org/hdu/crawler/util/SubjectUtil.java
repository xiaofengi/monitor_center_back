package org.hdu.crawler.util;

import org.hdu.crawler.constants.DatumConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubjectUtil {

    /**
     * 获取关键词的idf
     */
    public static void setKeywordIdf(List<List<Map<String, Object>>> subjectList) throws Exception{
        if(subjectList.isEmpty()){
            return;
        }
        long totalWebpageNum = getWebpageNum("的");
        for(List<Map<String, Object>> subject : subjectList){
            for(Map<String, Object> keywordInfo : subject){
                String keyword = keywordInfo.get("keyword").toString();
                long keywordWebpageNum = getWebpageNum(keyword);
                double idf = Math.log(totalWebpageNum/(keywordWebpageNum+1));
                System.out.println(keyword + "的idf：" + idf);
                keywordInfo.put("idf", idf);
            }
        }
    }

    /**
     * 获取关键字的网页数
     */
    public  static long getWebpageNum(String keyword) throws Exception{
        String encodeKeyword = URLEncoder.encode(keyword, "utf-8");
        String html = HttpClientUtil.doGet(String.format(DatumConstants.GOOGLE_SEARCH_URL, encodeKeyword, 0));
        Document document = Jsoup.parse(html);
        String resultStats = document.select("#resultStats").first().text();
        Matcher matcher = Pattern.compile("(?<=找到约).*(?=条结果)").matcher(resultStats);
        if(matcher.find()){
            String numText = matcher.group().trim();
            DecimalFormat df = new DecimalFormat(",###,##0");
            return df.parse(numText).longValue();
        }
        return 0;
    }

}
