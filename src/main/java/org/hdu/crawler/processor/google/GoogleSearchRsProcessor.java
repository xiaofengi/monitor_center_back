package org.hdu.crawler.processor.google;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hdu.back.mapper.WebPageDetailMapper;
import org.hdu.back.mapper.WebPageRelationMapper;
import org.hdu.back.mapper.WebPageResourceMapper;
import org.hdu.back.model.WebPageDetail;
import org.hdu.back.model.WebPageRelation;
import org.hdu.back.model.WebPageResource;
import org.hdu.back.util.AlgorithmUtil;
import org.hdu.crawler.constants.DatumConstants;
import org.hdu.crawler.crawler.DatumGenerator;
import org.hdu.crawler.monitor.MonitorExecute;
import org.hdu.crawler.processor.Processor;
import org.hdu.crawler.util.DomainUtil;
import org.hdu.crawler.util.SimilarityUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GoogleSearchRsProcessor implements Processor{

    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchRsProcessor.class);

    @Resource
    private DatumGenerator datumGenerator;
    @Resource
    private WebPageDetailMapper webPageDetailMapper;
    @Resource
    private WebPageResourceMapper webPageResourceMapper;
    @Resource
    private WebPageRelationMapper webPageRelationMapper;

    @Override
    public void process(Page page, CrawlDatums next) {
        List<Map<String, Object>> subject = new Gson().fromJson(page.meta("subject"), new TypeToken<List<Map<String, Object>>>(){}.getType());

        MonitorExecute.counter.getAndIncrement(); //下载页面数
        String realUrl = page.getResponse().getRealUrl().toString();
        if(realUrl.contains("www.google.com/search")){ //暂时不处理再次链接到谷歌搜索的网页
            return;
        }
        if(page.getHtml()==null || page.select("title").isEmpty()){ //下载失败的数据
            return;
        }

        String content = null; //内容
        Element contentElement = null;
        if(!page.select(".F11").isEmpty()) { //博讯
            contentElement = page.select(".F11").first();
            String f11Text = contentElement.text();
            if (!page.select(".F11 center").isEmpty()) { //移去文章标题部分
                String centerText = page.select(".F11 center").first().text();
                content = f11Text.substring(centerText.length());
            }else {
                content = f11Text;
            }
        }else if(!page.select("article").isEmpty()){
            contentElement = page.select("article").first();
            content = contentElement.text();
        }else if(!page.select(".article_content, .topic-content, .main-content, .article-content-wrap, .sec_article, .post_text, .article, .Cnt-Main-Article-QQ, .yc_con_txt, .theme-content").isEmpty()){
            contentElement = page.select(".article_content, .topic-content, .main-content, .article-content-wrap, .sec_article, .post_text, .article, .Cnt-Main-Article-QQ, .yc_con_txt, .theme-content").first();
            content = contentElement.text();
        }
        boolean contentMatch = false;
        if(content != null){
            contentMatch = SimilarityUtil.matchCrawl(content, subject); //根据过滤与关键字不相关的网页
        }

        //标题
        String title = page.select("title").first().text();
        if (title.equals("YouTube")) { //youtube标题需要额外获取
            Matcher matcher = Pattern.compile("(?<=document.title = \").*(?=\";)").matcher(page.getHtml());
            if (matcher.find()) {
                title = matcher.group();
            }
        }
        if(!contentMatch) {
            boolean titleMatch = false;
            for (Map<String, Object> keywordInfo : subject) { //根据标题过滤与关键字不相关的网页
                if (title.contains(keywordInfo.get("keyword").toString())) {
                    titleMatch = true;
                    break;
                }
            }
            if (!titleMatch) {
                return;
            }
        }

        StringBuilder keywords = new StringBuilder();
        boolean isFirst = true;
        for(Map<String, Object> keywordInfo : subject){
            if(isFirst){
                isFirst = false;
            }else {
                keywords.append(",");
            }
            keywords.append(keywordInfo.get("keyword").toString());
        }
        List<WebPageResource> resourceLs = new LinkedList<>();
        long urlDetailId = parseWebPageDetailAndImg(page, title, content, contentElement, resourceLs, keywords.toString());
        MonitorExecute.saveCounter.getAndIncrement(); //保存页面数
        parseVideoSource(page, urlDetailId, resourceLs);
        parseHref(page, next);
    }

    /**
     * 解析网页详情
     * @param page
     * @param next
     */
    private long parseWebPageDetailAndImg(Page page, String title, String content, Element contentElement, List<WebPageResource> resourceLs, String keywords){
        WebPageDetail webPageDetail = new WebPageDetail();

        //网页地址
        String realUrl  = page.getResponse().getRealUrl().toString();
        webPageDetail.setUrl(realUrl);
        //网页地址md5，作为索引
        String realUrlMd5 = AlgorithmUtil.toMD5(realUrl);
        webPageDetail.setUrlMd5(realUrlMd5);
        //域名
        String domain = page.getResponse().getRealUrl().getHost();
        webPageDetail.setDomain(domain);
        webPageDetail.setTitle(title);
        //文章来源
        String src = null;
        if(!page.select("#ne_article_source, .fabiao, .a_source, .source").isEmpty()){
            src = page.select("#ne_article_source, .fabiao, .a_source, .source").first().text();
        }else if(!page.select(".F11").isEmpty()){ //博讯
            String f11Text = page.select(".F11").first().text();
            if(f11Text.contains("来源：")){
                String tmp = f11Text.substring(f11Text.indexOf("来源："));
                int index = tmp.indexOf(" ")!=-1 ? tmp.indexOf(" "):7;
                src = tmp.substring(3, index);
            }
        }
        if(src != null){
            webPageDetail.setSrc(src);
        }
        //创建时间
        String createTimeStr = null;
        if(page.getHtml().indexOf("发布\"}") != -1){
            int index = page.getHtml().indexOf("发布\"}");
            String tmp = page.getHtml().substring(index-50<0?0:index-50, index);
            createTimeStr = tmp.substring(tmp.lastIndexOf("simpleText\":\"") + "simpleText\":\"".length());
        }else if(!page.select(".F11 center font small").isEmpty()){ //博讯
            String tmp = page.select(".F11 center font small").first().text();
            if(tmp.indexOf("年")!=-1 && tmp.indexOf("日")!=-1){
                createTimeStr = tmp.substring(tmp.indexOf("年")-4, tmp.indexOf("日")+1);
            }
        }else if(!page.select(".time, .utime, .time, .date, .a_time").isEmpty()){
            createTimeStr = page.select(".time, .utime, .time, .date, .a_time").first().text();
        }else if(!page.select(".post_time_source").isEmpty()) {
            createTimeStr = page.select(".post_time_source").first().text();
            if(createTimeStr.indexOf("来源") != -1) {
                createTimeStr = createTimeStr.substring(0, createTimeStr.indexOf("来源")).trim();
            }
        }
        if(createTimeStr != null){
            webPageDetail.setCreateTime(createTimeStr);
        }
        //作者
        String author = null;
        if(page.getHtml().indexOf("\"author\":\"") != -1){ //youtube
            int index = page.getHtml().indexOf("\"author\":\"");
            String tmp = page.getHtml().substring(index+"\"author\":\"".length(), index+"\"author\":\"".length()+50);
            author = tmp.substring(0, tmp.indexOf("\""));
        }else if(!page.select(".author, .author-name, .from, .ep-editor, .show_author, .qq_editor").isEmpty()){
            author = page.select(".author, .author-name, .from, .ep-editor, .show_author, .qq_editor").first().text();
        }else if(!page.select(".user_info").isEmpty()){
            Element userInfo = page.select(".user_info").first();
            if(userInfo.getElementById("uid") != null) {
                author = userInfo.getElementById("uid").text();
            }
        }
        if(author != null){
            webPageDetail.setAuthor(author);
        }
        //关键字
        webPageDetail.setKeyword(keywords);
        //标签
        String tags = null;
        if(!page.select("[target=tags]").isEmpty()){
            tags = page.select("[target=tags]").first().attr("content");
        }else if(!page.select(".article_tags a").isEmpty()){
            Elements ts = page.select(".article_tags a");
            for(Element tag : ts){
                tags = tags + tag.text() + ",";
            }
            tags = tags.substring(0, tags.length()-1);
        }
        if(tags != null) {
            webPageDetail.setTags(tags);
        }
        if(content != null){
            webPageDetail.setContent(content);
        }
        //html
        webPageDetail.setHtml(page.getHtml());
        //浏览数
        Integer viewNum = null;
        if(page.getHtml().indexOf("次观看") != -1){ //youtube
            int index = page.getHtml().indexOf("次观看");
            String tmp = page.getHtml().substring(index-50<0?0:index-50, index);
            int index2 = tmp.lastIndexOf("simpleText\":\"");
            if(index2 != -1){
                viewNum = 0;
                String viewNumStr = tmp.substring(index2+"simpleText\":\"".length());
                String[] nums = viewNumStr.split(",");
                for(int i=0; i<nums.length; i++){
                    String num = nums[i];
                    viewNum += (int)(Integer.parseInt(num)*Math.pow(1000, num.length()-1-i));
                }
            }
        }else if(!page.select(".art_click_count, .js-tiejoincount, .num, .cmtNum, .w-num").isEmpty()){
            String txt = page.select(".art_click_count, .js-tiejoincount, .num, .cmtNum, .w-num").first().text();
            if(!StringUtils.isEmpty(txt)){
                try {
                    viewNum = Integer.parseInt(txt);
                }catch (Exception e){
                }
            }
        }else if(!page.select(".icon-read").isEmpty()){
            String txt = page.select(".icon-read").first().nextElementSibling().text();
            if(!StringUtils.isEmpty(txt)) {
                try {
                    viewNum = Integer.parseInt(txt);
                }catch (Exception e){
                }
            }
        }
        if(viewNum != null){
            webPageDetail.setViewNum(viewNum);
        }
        //评论数
        Integer commentNum = null;
        if(!page.select(".cmtNum, .linkComment, .js_cmtNum, .comment-num, .js-tiecount, .comment-count").isEmpty()){
            String txt = page.select(".cmtNum, .linkComment, .js_cmtNum, .comment-num, .js-tiecount, .comment-count").first().text();
            if(!StringUtils.isEmpty(txt)){
                try {
                    commentNum = Integer.parseInt(txt);
                }catch (Exception e){
                }
            }
        }else {
            if (!page.select(".article-pl").isEmpty()) {
                String txt = page.select(".article-pl").first().text().replace("评论", "").trim();
                if (!StringUtils.isEmpty(txt)) {
                    try {
                        commentNum = Integer.parseInt(txt);
                    }catch (Exception e){
                    }
                }
            }
        }
        if(commentNum != null){
            webPageDetail.setCommentNum(commentNum);
        }
        //爬取时间
        webPageDetail.setCrawlTime(new Date());
        logger.info("保存关键字：" + webPageDetail.getKeyword());
        webPageDetailMapper.insertSelective(webPageDetail);
        long urlDetailId = webPageDetail.getId();

        //内容图片
        if(contentElement != null){
            Elements imgs = contentElement.select("img");
            if(!imgs.isEmpty()) {
                String path = page.getResponse().getRealUrl().getPath();
                for (Element img : imgs) {
                    //String src = img.attr("abs:src"); 获取绝对路径，暂时不用，可能存在bug
                    String picUrl = img.attr("src");
                    if(picUrl==null || picUrl.equals("") || picUrl.contains("{")){ //js动态加载、vue的图片暂时取不到
                        continue;
                    }
                    if(picUrl.startsWith("http") || picUrl.startsWith("//")){ //绝对路径

                    }else if(picUrl.startsWith("/")){ //以域名为基的相对路径
                        picUrl = domain+picUrl;
                    }else if(picUrl.startsWith("./")){ //当前目录下
                        picUrl = realUrl + picUrl.substring(1);
                    }else if(picUrl.startsWith("../")){ //上一目录下
                        picUrl = realUrl.substring(0, realUrl.lastIndexOf("/")) + picUrl.substring(2);
                    } else { //图片文件目录和当前文件在同一目录下的相对路径
                        picUrl = domain + path.substring(0, path.lastIndexOf("/")+1) + picUrl;
                    }
                    WebPageResource resource = new WebPageResource(urlDetailId, realUrl, picUrl, DatumConstants.RESOURCE_TYPE_PIC, new Date());
                    resourceLs.add(resource);
                }
            }
        }

        return urlDetailId;
    }


    /**
     * 解析网页的视频资源
     * @param page
     * @param next
     */
    private void parseVideoSource(Page page, long urlDetailId, List<WebPageResource> resourceLs){
        String url = page.getResponse().getRealUrl().toString();

        //解析视频地址
        if(url.startsWith("https://www.youtube.com/watch?v=")){ //youtube播放地址
            int index = page.getHtml().indexOf("url_encoded_fmt_stream_map\":\"");
            if(index != -1) {
                String tmp = page.getHtml().substring(index+"url_encoded_fmt_stream_map\":\"".length());
                String urlMapInfo = tmp.substring(0, tmp.indexOf("\","));
                if(urlMapInfo == null){
                    return;
                }
                String[] videoUrlInfos = urlMapInfo.split(",");
                for(String videoUrlInfo : videoUrlInfos){
                    String[] videoUrlParams = videoUrlInfo.split("\\\\u0026");
                    for(String videoUrlParam : videoUrlParams) {
                        String[] paramKeyValue = videoUrlParam.split("=");
                        if (paramKeyValue[0].equals("url")) {
                            try {
                                String videoUrl = URLDecoder.decode(paramKeyValue[1], "utf-8");
                                WebPageResource resource = new WebPageResource(urlDetailId, url, videoUrl, DatumConstants.RESOURCE_TYPE_VIDEO, new Date());
                                resourceLs.add(resource);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        }

        //插入数据库
        if(!resourceLs.isEmpty()) {
            webPageResourceMapper.batchInsert(resourceLs);
        }
    }

    /**
     * 解析网页下面的超链接
     * @param page
     * @param next
     */
    private void parseHref(Page page, CrawlDatums next){
        Elements as = page.select("a");
        if(as.isEmpty()){
            return;
        }
        List<WebPageRelation> relationLs = new ArrayList<>();
        for (Element a : as) {
            if (a.hasAttr("href")) {
                String href = a.attr("abs:href");//获取超链接
                String title = a.text();
                if(href.startsWith("http") /*&& title.contains(page.meta("keyword"))*/) { //过滤与关键字无关的超链接
                    try {
                        if(DomainUtil.limitedDomain(new URL(href).getHost(), page)){ //过滤域名
                            continue;
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    String srcUrl = page.getResponse().getRealUrl().toString();
                    WebPageRelation webPageRelation = new WebPageRelation(href, srcUrl, new Date());
                    relationLs.add(webPageRelation);
                    next.add(datumGenerator.generateGoogleSearchRs(href, page.meta("subject"), page.meta("domain"), srcUrl));
                }
            }
        }
        //插入数据库
        if(!relationLs.isEmpty()){
            webPageRelationMapper.batchInsert(relationLs);
        }
    }
}
