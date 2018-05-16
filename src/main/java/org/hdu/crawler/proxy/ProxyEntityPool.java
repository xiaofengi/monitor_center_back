package org.hdu.crawler.proxy;

import java.util.Date;
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
	
	private Vector<ProxyEntity> proxyEntities = null;
	
	private boolean isDynAdd = false;

	@Resource
	private ProxyEntityMapper proxyEntityMapper;
	
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
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Date lastMaxCreateTime = proxyEntities.isEmpty() ? new Date(0):proxyEntities.lastElement().getCreateTime();
					List<ProxyEntity> proxyEntityLs = proxyEntityMapper.getNewEnables(lastMaxCreateTime);
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
			if(proxyEntities.get(i).getEnable() && !proxyEntities.get(i).getUsing()){ //代理ip有效且没被使用
				proxyEntities.get(i).setUsing(true);
				return proxyEntities.get(i);
			}
		}
		return null;
	}

	public void failProxyEntity(ProxyEntity entity, Exception e) {
		if(entity != null) {
			logger.info("error proxy: " + entity.getHost() + ":" + entity.getPort());
			if(e.getMessage()!=null &&
					(e.getMessage().contains("Server returned HTTP response code: 503") || e.getMessage().contains("connect timed out") || e.getMessage().contains("Connection refused: connect"))) { //被反爬或连接超时则丢弃该ip
				entity.setUsing(false);
				entity.setEnable(false);
				proxyEntityMapper.updateByPrimaryKeySelective(entity);
			}else {
				entity.setUsing(false);
			}
		}
	}

	public void successProxyEntity(ProxyEntity entity) {
		if(entity != null) { //释放
			entity.setUsing(false);
		}		
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
