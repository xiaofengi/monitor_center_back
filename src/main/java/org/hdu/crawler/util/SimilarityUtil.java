package org.hdu.crawler.util;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.*;
import org.hdu.crawler.crawler.HduCrawler;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimilarityUtil {

    /**
     * 相关度大于阈值时爬取
     * @param data
     * @return
     */
    public static boolean matchCrawl(String data, List<Map<String, Object>> subject){
        double score = getWebPageScore(data, subject);
        if(score >= HduCrawler.threshold){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 计算出网页的相关度(0到1之间）
     * @param data
     * @return
     */
    public static double getWebPageScore(String data, List<Map<String, Object>> subject){
        int[] keywordCounts = new int[subject.size()];

        //分词
        StopRecognition filter = new StopRecognition();
        filter.insertStopRegexes("我|是|的|用于|及|和"); //过滤单词
        Result result = ToAnalysis.parse(data).recognition(filter);

        //统计词频
        for(Term term : result.getTerms()){
            for(int i=0; i<subject.size(); i++) {
                Map<String, Object> keywordInfo = subject.get(i);
                if (term.getName().equals(keywordInfo.get("keyword"))) {
                    keywordCounts[i]++;
                    break;
                }
            }
        }

        //权重向量
        Vector<Double> userWeight = new Vector<>();
        Vector<Double> webpageWeight = new Vector<>();
        for(int i=0; i<subject.size(); i++) {
            Map<String, Object> keywordInfo = subject.get(i);
            userWeight.add(Double.parseDouble(keywordInfo.get("weight").toString()));
            double tf = keywordCounts[i]/result.getTerms().size();
            double idf = Double.parseDouble(keywordInfo.get("idf").toString());
            System.out.println(keywordInfo.get("keyword") + "的tf_idf：" + tf*idf);
            webpageWeight.add(tf*idf);
        }

        //余弦相似度
        double numerator = 0;
        double userSquare = 0;
        double webpageSquare = 0;
        for(int i=0; i<userWeight.size(); i++){
            numerator += userWeight.get(i)*webpageWeight.get(i);
            userSquare += Math.pow(userWeight.get(i), 2);
            webpageSquare += Math.pow(webpageWeight.get(i), 2);
        }
        double denominator = Math.sqrt(userSquare)*Math.sqrt(webpageSquare);
        double score = numerator/denominator;

        //返回相似度
        System.out.println("score: " + score);
        return score;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\user\\Desktop\\news_tensite_xml.dat");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "gbk"));
        StringBuilder newsData = new StringBuilder();
        String line;
        while((line=reader.readLine()) != null){
            newsData.append(line);
        }
        //System.out.println(newsData);
        String[] docList = newsData.toString().split("</doc>");
        System.out.println("doc size：" + docList.length);
        StringBuilder titleData = new StringBuilder();
        StringBuilder contentData = new StringBuilder();
        Pattern titlePattern = Pattern.compile("(?<=<contenttitle>).*(?=</contenttitle>)");
        Pattern commentPattern = Pattern.compile("(?<=<content>).*(?=</content>)");
        for(int i=0; i<docList.length/4; i++){
            String doc = docList[i];
            Matcher matcher = titlePattern.matcher(doc);
            if(matcher.find()) {
                titleData.append(matcher.group());
                titleData.append(";");
            }
            matcher = commentPattern.matcher(doc);
            if(matcher.find()){
                contentData.append(matcher.group());
                contentData.append(";");
            }
        }
        //System.out.println(titleData);
        //System.out.println(contentData);
        StopRecognition filter = new StopRecognition();
        /*filter.insertStopNatures("Ag", "ad", "an", "Bg", "b", "c", "Dg", "e", "f", "h", "i", "j", "k", "l", "Mg", "m", "Ng",
                "nr", "o", "p", "q", "Rg", "r", "s", "Tg", "u", "w", "x", "Yg", "y"); //过滤词性*/
        filter.insertStopRegexes("我|是|的|用于|及|和"); //过滤单词
        //System.out.println(BaseAnalysis.parse(str));
        Result result = ToAnalysis.parse(contentData.toString()).recognition(filter);
        //System.out.println(result);
/*        System.out.println(DicAnalysis.parse(str));
        System.out.println(IndexAnalysis.parse(str));
        System.out.println(NlpAnalysis.parse(str));*/
        //String keyword = "非法";
        //int count = 0;
        StringBuilder corpusData = new StringBuilder();
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\corpusData.txt"));
        for(Term term : result.getTerms()){
            corpusData.append(term.getName());
            corpusData.append(" ");
        }
        fos.write(corpusData.toString().getBytes()) ;
    }
}
