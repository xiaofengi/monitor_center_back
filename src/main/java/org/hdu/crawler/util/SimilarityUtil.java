package org.hdu.crawler.util;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.*;
import org.hdu.crawler.crawler.HduCrawler;

public class SimilarityUtil {

    /**
     * 相关度大于阈值时爬取
     * @param html
     * @return
     */
    public static boolean matchCrawl(String html){
        double score = getWebPageScore(html);
        if(score >= HduCrawler.threshold){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 计算出网页的相关度(0到1之间）
     * @param html
     * @return
     */
    public static double getWebPageScore(String html){
        return 1;
    }

    public static void main(String[] args) {
        String str = "人工智能（Artificial Intelligence），英文缩写为AI。它是研究、开发用于模拟、延伸和扩展人的智能的理论、方法、技术及应用系统的一门新的技术科学。" ;
        StopRecognition filter = new StopRecognition();
        filter.insertStopNatures("uj"); //过滤词性
        filter.insertStopNatures("ul");
        filter.insertStopNatures("null");
        filter.insertStopRegexes("我|是|的|用于|及|和"); //过滤单词
        filter.insertStopRegexes("小.*?"); //支持正则表达式
        //System.out.println(BaseAnalysis.parse(str));
        Result result = ToAnalysis.parse(str).recognition(filter);
        System.out.println(result);
        System.out.println(DicAnalysis.parse(str));
        System.out.println(IndexAnalysis.parse(str));
        System.out.println(NlpAnalysis.parse(str));
        String keyword = "人工智能";
        int count = 0;
        for(Term term:result.getTerms()){
            if(keyword.equals(term.getName())){
                count++;
            }
        }
        System.out.println("count:" + count);
        double tf = count/result.getTerms().size();
    }
}
