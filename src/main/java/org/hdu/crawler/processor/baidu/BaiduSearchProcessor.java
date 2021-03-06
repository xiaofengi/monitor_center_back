package org.hdu.crawler.processor.baidu;

import org.hdu.crawler.crawler.DatumGenerator;
import org.hdu.crawler.processor.Processor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.ParseException;

@Component
public class BaiduSearchProcessor implements Processor{

	Logger logger = LoggerFactory.getLogger(BaiduSearchProcessor.class);

	@Resource
	private DatumGenerator datumGenerator;

	@Override
	public void process(Page page, CrawlDatums next) {
		//解析结果列表页
		int size = 0;
		//System.out.println(page.getHtml());
		Element contentLeft = page.select("#content_left").first();
		if(contentLeft == null){
			return;
		}
		Elements divs = contentLeft.getElementsByClass("c-container");
		for(Element element:divs){
			Elements tmp = element.getElementsByAttributeValue("target", "_blank");
			if(tmp != null){
				Element aTag = tmp.first();
				String resultTitle = aTag.text();//搜索结果展示标题
				String href = aTag.attr("href");//搜索结果链接
				//System.out.println(href);
				//百度搜索结果url过滤
				if(href!=null && href.startsWith("http")){
					next.add(datumGenerator.generateBaiduSearchRs(href, page.meta("keyword"), page.meta("domain"), null));
				}else {
					logger.error("取不到链接：" + href);
				}
				size++;
			}
		}
		logger.info("共搜索到" + size + "条数据");
//		//第一页的话往下抓取
//		int pn = Integer.parseInt(page.meta("pn"));
//		if(pn == 0){
//			Element numsElement  = page.select(".nums").first();
//			String numContent = numsElement.text();
//			System.out.println(numContent);
//			DecimalFormat df = new DecimalFormat(",###,##0"); //没有小数
//			int num = 0;
//			try {
//				num = df.parse(numContent.substring(numContent.indexOf("约")+1, numContent.indexOf("个"))).intValue();
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			for(int i=50; i<=750; i+=50){ //抓取下一页，目前百度只能搜索到760条结果
//				next.add(datumGenerator.generateBaiduSearchList(page.meta("keyword"), i));
//			}
//		}
	}

}
