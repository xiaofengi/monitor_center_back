package org.hdu.crawler.proxy;

import java.util.List;
import java.util.Vector;
import javax.annotation.Resource;
import org.hdu.back.mapper.ProxyEntityMapper;
import org.hdu.back.model.ProxyEntity;
import org.hdu.crawler.listener.CrawlerBeginListener;
import org.hdu.crawler.listener.CrawlerEndListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProxyEntityPool implements CrawlerBeginListener, CrawlerEndListener{
	
	private static final Logger logger = LoggerFactory.getLogger(ProxyEntityPool.class);
	
	@Resource
	private ProxyEntityMapper proxyEntityMapper;
	
	private Vector<ProxyEntity> proxyEntities = null; 
	
	private boolean isDynAdd = false;
	
	/**
	 * 创建代理实体池
	 */
	private synchronized void createPool(){
		if(proxyEntities != null){
			return;
		}else{
			proxyEntities = new Vector<ProxyEntity>(); 
			List<ProxyEntity> proxyEntityLs = proxyEntityMapper.getAllEnables();
			proxyEntities.addAll(proxyEntityLs);
		}
	}
	
	/**
	 * 动态增加代理实体
	 */
	private synchronized void dynamicAdd(){
		new Thread(new Runnable() {		
			@Override
			public void run() {
				while(isDynAdd){
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					List<ProxyEntity> proxyEntityLs = proxyEntityMapper.getNewEnables(proxyEntities.lastElement().getCreateTime());
					proxyEntities.addAll(proxyEntityLs);
				}
			}
		}).start();
	}
	
	/**
	 * 获取一个代理实体
	 */
	public synchronized ProxyEntity getOne(){
		for(int i=proxyEntities.size()-1; i>=0; i--){
			if(proxyEntities.get(i).getEnable()){
				//proxyEntities.get(i).setEnable(false);
				return proxyEntities.get(i);
			}
		}
		return null;
	}

	@Override
	public void crawlerBegin() {
		createPool();
		isDynAdd = true;
		dynamicAdd();
		logger.info("proxy entity pool add start");
	}
	
	@Override
	public void crawlerEnd() {
		isDynAdd = false;
		logger.info("proxy entity pool add end");
	}
}
