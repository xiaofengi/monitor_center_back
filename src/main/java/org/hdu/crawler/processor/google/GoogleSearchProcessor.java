package org.hdu.crawler.processor.google;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import org.hdu.crawler.crawler.DatumGenerator;
import org.hdu.crawler.processor.Processor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class GoogleSearchProcessor implements Processor{

    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchProcessor.class);

    @Resource
    private DatumGenerator datumGenerator;

    @Override
    public void process(Page page, CrawlDatums next) {
        if(page.select(".srg").isEmpty()){
            return;
        }
        int size = 0;
        Elements gs = page.select(".srg .g");
        for(Element g : gs){
            Element a = g.getElementsByTag("a").first();
            if(a == null){
                continue;
            }
            String href = a.attr("href");
            if(href!=null && href.startsWith("http")){
                next.add(datumGenerator.generateGoogleSearchRs(href, page.meta("subject"), page.meta("domain"), null));
            }else {
                logger.error("取不到链接：" + href);
            }
            size++;
        }
        logger.info("共搜索到" + size + "条数据");
    }
}
