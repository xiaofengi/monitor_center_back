package org.hdu.crawler.crawler;

import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.edu.hfut.dmic.webcollector.crawldb.Generator;
import cn.edu.hfut.dmic.webcollector.fetcher.Fetcher;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BerkeleyDBManager;
import cn.edu.hfut.dmic.webcollector.util.Config;

import org.hdu.crawler.constants.DatumConstants;
import org.hdu.crawler.listener.CrawlerBeginListener;
import org.hdu.crawler.listener.CrawlerEndListener;
import org.hdu.crawler.monitor.MonitorExecute;
import org.hdu.crawler.processor.manager.ProcessorManager;
import org.hdu.crawler.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.net.Requester;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

@Component
public class HduCrawler extends BreadthCrawler implements ApplicationContextAware{
	private static final Logger logger = LoggerFactory.getLogger(HduCrawler.class);
	
	/*@Value("${crawler.webcollector.depth}")
	private int depth;*/
	
	@Value("${crawler.webcollector.threads}")
	private int threads; 
	
	@Value("${crawler.webcollector.resumable}")
	private boolean resumable;
	
	@Value("${crawler.webcollector.topN}")
	private int topN;
	
	@Value("${crawler.webcollector.executeInterval}")
	private int executeInterval;

	private String crawlPath;
	
	@Autowired
	private Requester hduRequester;
	
	@Autowired
	private SeedGenerator seedGenerator;
	
	@Autowired
	private ProcessorManager processorManager;
	
	private Map<String, CrawlerEndListener> crawlerEndListenerMap;
	private Map<String, CrawlerBeginListener> crawlerBeginListenerMap;
	/** 最大抓取总量 */
	public static int count = DatumConstants.DEFAULT_COUNT;
	/** 最大深度 */
	private int depth = DatumConstants.DEFAULT_DEPTH;
	/** 域名列表 */
	public static List<Map<String, Object>> domainList = null;
	/** 限制域名类型 */
	public static String limitType = null;
	/** 是否已启动爬虫 */
	public static boolean isStart = false;
	/** 分值阈值,大于该阈值则爬取网页 **/
	public static double threshold = 0.3;
	/** 当前层数 */
	public static int nowDepth = -1;

	private Random random = new Random();
	
	public HduCrawler(@Value("${crawler.webcollector.crawlPath}") String crawlPath, @Value("${crawler.webcollector.autoParse}") boolean autoParse) {
		super(crawlPath, autoParse);
		this.crawlPath = crawlPath;
	}

	/* (non-Javadoc)
	 * @see cn.edu.hfut.dmic.webcollector.fetcher.Visitor#visit(cn.edu.hfut.dmic.webcollector.model.Page, cn.edu.hfut.dmic.webcollector.model.CrawlDatums)
	 */
	public void visit(Page page, CrawlDatums next) {
		processorManager.process(page, next);
	}

	@Override
	public void execute(CrawlDatum datum, CrawlDatums next) throws Exception {
		if(MonitorExecute.saveCounter.get() >= HduCrawler.count){ //数量达到上限则停止爬虫
        	stop();
        }
		if(datum.getUrl().contains("www.google.com")){ //爬取谷歌网站设置fetcher的间隔为8-10秒
			int googleInterval = random.nextInt(2000) + 8000;
			fetcher.setExecuteInterval(googleInterval);
		}else {
			fetcher.setExecuteInterval(executeInterval);
		}
		HttpResponse response = requester.getResponse(datum);
        Page page = new Page(datum, response);
        visitor.visit(page, next);
	}
	
	public void start(List<List<Map<String, Object>>> subjectList, Integer depth, Integer count, List<Map<String, Object>> domainList, String limitType) {
		long startTime = System.currentTimeMillis();
		if(depth != null){
			this.depth = depth;
		}
		if(count != null){
			HduCrawler.count = count;
		}
		if(!domainList.isEmpty()){
			HduCrawler.domainList = domainList;
			HduCrawler.limitType = limitType;
		}
		if(!resumable){
			this.seeds.clear(); //清空种子列表
		}
		this.setRequester(hduRequester);
		logger.info("crawler start");
		notifyBeginCrawler();
		logger.info("request start");
		HduCrawler.isStart = true; //标记爬虫已启动

		try {
			SubjectUtil.setKeywordIdf(subjectList);
			seedGenerator.addSeed(this, subjectList, domainList);
			setResumable(resumable);
			setTopN(topN);
			setThreads(threads);
        	setExecuteInterval(executeInterval);
			start(this.depth);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		logger.info("爬取总量：" + MonitorExecute.counter.get());
		logger.info("入库总量：" + MonitorExecute.saveCounter.get());
		HduCrawler.isStart = false; //标记爬虫已停止
		logger.info("request end");
        notifyEndCrawler();
        logger.info("crawler end" );
		logger.info("爬取总时间:" + (System.currentTimeMillis()-startTime)/1000 + "秒");
		//重置变量
		HduCrawler.count = DatumConstants.DEFAULT_COUNT;
		this.depth = DatumConstants.DEFAULT_DEPTH;
		HduCrawler.domainList = null;
		HduCrawler.limitType = null;
		HduCrawler.threshold = 1;
		//HduCrawler.nowDepth = -1;
	}

	@Override
	public void start(int depth) throws Exception {
		if (!resumable) {
			if (dbManager.isDBExists()) {
				dbManager.clear();
				LOG.info("clear database file success");
			}

			if (seeds.isEmpty() && forcedSeeds.isEmpty()) {
				LOG.info("error:Please add at least one seed");
				return;
			}
		}
		dbManager.open();

		if(!seeds.isEmpty()){
			inject();
		}

		if (!forcedSeeds.isEmpty()) {
			injectForcedSeeds();
		}

		Generator generator = dbManager.getGenerator();
		if (maxExecuteCount >= 0) {
			generator.setMaxExecuteCount(maxExecuteCount);
		} else {
			generator.setMaxExecuteCount(Config.MAX_EXECUTE_COUNT);
		}
		generator.setTopN(topN);
		status = RUNNING;
		for (int i = 0; i < depth; i++) {
			if (status == STOPED) {
				break;
			}
			nowDepth = i+1; //获取当前层数
			LOG.info("start depth " + (i + 1));
			long startTime = System.currentTimeMillis();
			fetcher = new Fetcher();
			fetcher.setDBManager(dbManager);
			fetcher.setExecutor(executor);
			fetcher.setThreads(threads);
			fetcher.setExecuteInterval(executeInterval);
			fetcher.fetchAll(generator);
			long endTime = System.currentTimeMillis();
			long costTime = (endTime - startTime) / 1000;
			int totalGenerate = generator.getTotalGenerate();

			LOG.info("depth " + (i + 1) + " finish: \n\ttotal urls:\t" + totalGenerate + "\n\ttotal time:\t" + costTime + " seconds");
			if (totalGenerate == 0) {
				break;
			}

		}
		if(!resumable){ //如果不断点爬取则新建数据库管理器
			dbManager.close(); //关闭数据库管理器
			setDBManager(new BerkeleyDBManager(crawlPath)); //设置数据库管理器
		}
	}

	@Override
	public void stop() {
		super.stop();

	}

	private void notifyBeginCrawler() {
		if(crawlerBeginListenerMap == null || crawlerBeginListenerMap.isEmpty()) {
			return;
		}
		for(CrawlerBeginListener listener: crawlerBeginListenerMap.values()) {
			listener.crawlerBegin();
		}
 	}

	private void notifyEndCrawler() {
		if(crawlerEndListenerMap == null || crawlerEndListenerMap.isEmpty()) {
			return;
		}
		
		if(crawlerEndListenerMap.containsKey("pipeline")) {
			crawlerEndListenerMap.get("pipeline").crawlerEnd();
		}
		
		for(CrawlerEndListener listener: crawlerEndListenerMap.values()) {
			listener.crawlerEnd();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		crawlerBeginListenerMap = applicationContext.getBeansOfType(CrawlerBeginListener.class);
		crawlerEndListenerMap = applicationContext.getBeansOfType(CrawlerEndListener.class);
	}

}