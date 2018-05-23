package org.hdu.crawler.util;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.StopLibrary;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.*;
import org.hdu.crawler.crawler.HduCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class SimilarityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SimilarityUtil.class);

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
        double[] keywordCounts = new double[subject.size()];

        //分词

        StopRecognition filter = StopLibrary.get();
        filter.insertStopNatures("e", "m", "w"); //去掉叹词、数词、标点符号
        filter.insertStopWords("\n");
        Result result = ToAnalysis.parse(data).recognition(filter);

        //统计词频
        for(Term term : result.getTerms()){
            //System.out.println(term.getRealName());
            for(int i=0; i<subject.size(); i++) {
                Map<String, Object> keywordInfo = subject.get(i);
                if (term.getName().equals(keywordInfo.get("keyword"))) {
                    keywordCounts[i] = ++keywordCounts[i];
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
            logger.info(keywordInfo.get("keyword") + "的tf_idf：" + tf*idf);
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
        double score = denominator==0 ? 0:numerator/denominator;

        //返回相似度
        logger.info("score: " + score);
        return score;
    }

    public static void main(String[] args) {
        String data = "打砸抢活动和动作都是以实现预定目的为特征的，但是动作受单一目的的制约。而活动则受一种完整的目的和动机系统的制约。活动是由一系列动作构成的系统。\n" +
                "活动总要指向一定的对象。对象有两种 :①制约着活动的客观事物；②调节活动的客观事物的心理映象。离开对象的活动是不存在的。活动总是由需要来推动的,人通过活动改变客体使其满足自身的需要。人对客观现实的积极反映、主体与客体的关系都是通过活动而实现的。在活动过程中主客体之间发生相互转化，通过活动客体转化为主观映象，而主观映象也是通过活动才转化为客观产物的。内省心理学脱离活动研究意识、行为主义心理学脱离意识去研究行为，都不能得出科学的结论。\n" +
                "人的心理、意识是在活动中形成和发展起来的。通过活动，人认识周围世界，形成人的各种个性品质；反过来，活动本身又受人的心理、意识的调节。这种调节具有不同的水平。肌肉的强度、运动的节律是在感觉和知觉水平上进行的调节，而解决思维课题的活动则是在概念水平上进行的调节。\n" +
                "活动可以分为外部活动和内部活动。从发生的观点来看，外部活动是原初的,内部活动起源于外部活动,是外部活动内化的结果。内部活动又通过外部活动而外化。这两种活动具有共同的结构，可以相互过渡。\n" +
                "人的活动的基本形式有3种:游戏、学习和劳动。这3种形式的活动在人们不同发展阶段起着不同的作用,其中有一种起着主导作用。例如在学龄前，儿童的主导活动是游戏；到了学龄期，游戏活动便逐步为学习活动所取代；到了成人期，劳动便成为人的主导活动。活动和动作都是以实现预定目的为特征的，但是动作受单一目的的制约。而活动则受一种完整的目的和动机系统的制约。活动是由一系列动作构成的系统。\n" +
                "活动总要指向一定的对象。对象有两种:①制约着活动的客观事物；②调节活动的客观事物的心理映象。离开对象的活动是不存在的。活动总是由需要来推动的,人通过活动改变客体使其满足自身的需要。人对客观现实的积极反映、主体与客体的关系都是通过活动而实现的。在活动过程中主客体之间发生相互转化，通过活动客体转化为主观映象，而主观映象也是通过活动才转化为客观产物的。内省心理学脱离活动研究意识、行为主义心理学脱离意识去研究行为，都不能得出科学的结论。\n" +
                "人的心理、意识是在活动中形成和发展起来的。通过活动，人认识周围世界，形成人的各种个性品质；反过来，活动本身又受人的心理、意识的调节。这种调节具有不同的水平。肌肉的强度、运动的节律是在感觉和知觉水平上进行的调节，而解决思维课题的活动则是在概念水平上进行的调节。\n" +
                "活动可以分为外部活动和内部活动。从发生的观点来看，外部活动是原初的,内部活动起源于外部活动,是外部活动内化的结果。内部活动又通过外部活动而外化。这两种活动具有共同的结构，可以相互过渡。\n" +
                "人的活动的基本形式有3种:游戏、学习和劳动。这3种形式的活动在人们不同发展阶段起着不同的作用,其中有一种起着主导作用。例如在学龄前，儿童的主导活动是游戏；到了学龄期，游戏活动便逐步为学习活动所取代；到了成人期，劳动便成为人的主导活动。活动和动作都是以实现预定目的为特征的，但是动作受单一目的的制约。而活动则受一种完整的目的和动机系统的制约。活动是由一系列动作构成的系统。\n" +
                "活动总要指向一定的对象。对象有两种:①制约着活动的客观事物；②调节活动的客观事物的心理映象。离开对象的活动是不存在的。活动总是由需要来推动的,人通过活动改变客体使其满足自身的需要。人对客观现实的积极反映、主体与客体的关系都是通过活动而实现的。在活动过程中主客体之间发生相互转化，通过活动客体转化为主观映象，而主观映象也是通过活动才转化为客观产物的。内省心理学脱离活动研究意识、行为主义心理学脱离意识去研究行为，都不能得出科学的结论。\n" +
                "人的心理、意识是在活动中形成和发展起来的。通过活动，人认识周围世界，形成人的各种个性品质；反过来，活动本身又受人的心理、意识的调节。这种调节具有不同的水平。肌肉的强度、运动的节律是在感觉和知觉水平上进行的调节，而解决思维课题的活动则是在概念水平上进行的调节。\n" +
                "活动可以分为外部活动和内部活动。从发生的观点来看，外部活动是原初的,内部活动起源于外部活动,是外部活动内化的结果。内部活动又通过外部活动而外化。这两种活动具有共同的结构，可以相互过渡。\n" +
                "人的活动的基本形式有3种:游戏、学习和劳动。地震预测这3种形式地震预测的活动在地震预测人们不同发展阶段起着不同的作用,其中有一种起着主导作用。例如在学龄前，儿童的主导活动是游戏；到了学龄期，游戏活动便逐步为学习活动所取代；到了成人期，劳动便成为人的主导活动。活动是由共同目的联合起来并完成一定社会职能的动作的总和。活动由目的、动机和动作构成，具有完整的结构系统。苏联心理学家从20年代起就对活动进行了一系列研究。其中Α.Н.列昂节夫的活动理论对苏联心理学的发展影响很大，成为现代苏联心" +
                "发生打砸抢，预测汶川预测很难的预测GOOD MORNING预测";
        Map<String, Object> keywordInfo1 = new HashMap();
        keywordInfo1.put("keyword", "地震预测");
        keywordInfo1.put("weight", "8");
        keywordInfo1.put("idf", "6.22156152");
        Map<String, Object> keywordInfo2 = new HashMap();
        keywordInfo2.put("keyword", "预测");
        keywordInfo2.put("weight", "2");
        keywordInfo2.put("idf", "7.125854");
        List<Map<String, Object>> subject = new ArrayList<>();
        subject.add(keywordInfo1);
        subject.add(keywordInfo2);
        getWebPageScore(data, subject);
    }

/*    public static void main(String[] args) throws Exception {
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
        *//*filter.insertStopNatures("Ag", "ad", "an", "Bg", "b", "c", "Dg", "e", "f", "h", "i", "j", "k", "l", "Mg", "m", "Ng",
                "nr", "o", "p", "q", "Rg", "r", "s", "Tg", "u", "w", "x", "Yg", "y"); //过滤词性*//*
        filter.insertStopRegexes("我|是|的|用于|及|和"); //过滤单词
        //System.out.println(BaseAnalysis.parse(str));
        Result result = ToAnalysis.parse(contentData.toString()).recognition(filter);
        //System.out.println(result);
*//*        System.out.println(DicAnalysis.parse(str));
        System.out.println(IndexAnalysis.parse(str));
        System.out.println(NlpAnalysis.parse(str));*//*
        //String keyword = "非法";
        //int count = 0;
        StringBuilder corpusData = new StringBuilder();
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\user\\Desktop\\corpusData.txt"));
        for(Term term : result.getTerms()){
            corpusData.append(term.getName());
            corpusData.append(" ");
        }
        fos.write(corpusData.toString().getBytes()) ;
    }*/
}
