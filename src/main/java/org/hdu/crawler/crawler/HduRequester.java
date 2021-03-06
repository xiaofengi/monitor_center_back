package org.hdu.crawler.crawler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import javax.annotation.Resource;
import org.hdu.back.model.ProxyEntity;
import org.hdu.crawler.constants.DatumConstants;
import org.hdu.crawler.constants.ProcessorType;
import org.hdu.crawler.listener.CrawlerBeginListener;
import org.hdu.crawler.listener.CrawlerEndListener;
import org.hdu.crawler.proxy.ProxyEntityPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.net.Requester;

@Component
public class HduRequester implements Requester, CrawlerBeginListener, CrawlerEndListener{
	private static final Logger logger = LoggerFactory.getLogger(HduRequester.class);
	
	@Value("${crawler.proxy.enable}")
	private boolean proxyEnable;
	
	@Resource
	private ProxyEntityPool proxyEntityPool;
	
	private String googleCookie_SIDCC = "AEfoLebltNyVi1Y6LMsy0zEqbwxgo1UcKEV4EWBSLivlPZUjrUskom86qjGxeJTVMN9K56NecdA";
	private String googleCookie_1P_JAR = "2018-5-16-9";
	private String googleCookie_GOOGLE_ABUSE_EXEMPTION = "ID=44d35cbd808c35db:TM=1525582439:C=r:IP=207.246.90.158-:S=APGng0v5A9maN9Jbjk-2QQpa-cd9NqY0jA";
	private String googleCookie_NID = "130=IyEdpcu3TePv0_3m8cFyb_uCbOHD-Ejd69RU1zxJtfeBw4HoRzcbltYPkNB1UlhEnnie0FNyhuPtCLDWZVpfBuVryIDcV0jGURBXjWDX1VyYaevOuNJm0fdkjiZSVXE25U-9a4qZlBn0r0vRCXBS1Mmzd0mQMaWu6lm8lh9yR7uzbXAJ5Bfyl1Gf8PydEhMqIXDXMQbWaL9aeg";

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		HttpResponse res;
		HttpRequest request;
		ProxyEntity proxyEntity = null;
		try{
			request = new HttpRequest(crawlDatum);
			request.setMAX_REDIRECT(4);
			setHeader(crawlDatum, request);
			if(proxyEnable && crawlDatum.meta("proxyEnable")!=null){ //设置代理
				proxyEntity = proxyEntityPool.getOne();
				if(proxyEntity != null){
					setProxy(request, proxyEntity);
					logger.info(crawlDatum.getUrl()+" 使用代理 "+proxyEntity.getHost()+":"+proxyEntity.getPort());
				}else {
					logger.error("获取不到有效的代理实体");
				}
			}
			res = request.getResponse();
			//获取返回的google cookies信息
			List<String> cookies = res.getHeader("Set-Cookie");
			if(cookies!=null && !cookies.isEmpty()){
				for(String cookie : cookies){
					if(cookie.startsWith("SIDCC=")){
						googleCookie_SIDCC = cookie.substring("SIDCC=".length(), cookie.indexOf(";"));
					}else if (cookie.startsWith("1P_JAR=")) {
						googleCookie_1P_JAR = cookie.substring("1P_JAR=".length(), cookie.indexOf(";"));
					}else if(cookie.startsWith("NID=")){
						googleCookie_NID = cookie.substring("NID".length(), cookie.indexOf(";"));
					}
				}
			}
		}catch (Exception e) {
			proxyEntityPool.failProxyEntity(proxyEntity, e);
			throw e;
		}
		proxyEntityPool.successProxyEntity(proxyEntity);
		return res;
	}
	
	private void setProxy(HttpRequest request, ProxyEntity proxyEntity){
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyEntity.getHost(), proxyEntity.getPort()));
		request.setProxy(proxy);
	}

	private void setHeader(CrawlDatum crawlDatum, HttpRequest request) {
		request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		if(crawlDatum.meta("referer") != null) {
			request.setHeader("referer", crawlDatum.meta("referer"));
		}
		switch (crawlDatum.meta(ProcessorType.PROCESSOR_TYPE)) {
			case ProcessorType.PROCESSOR_TYPE_GOOGLE_SEARCH:
				String cookies = String.format(DatumConstants.GOOGLE_HK_COOKIES, googleCookie_NID, googleCookie_1P_JAR);
				logger.info("正在使用google cookies： " + cookies);
				request.setCookie(cookies);
				request.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
				request.setHeader("accept-encoding", "utf-8");
				request.setHeader("accept-language", "zh-CN,zh;q=0.9");
				request.setHeader("upgrade-insecure-requests", "1");
				request.setHeader("x-client-data", "CJa2yQEIprbJAQjBtskBCKmdygEIsp3KAQioo8oBGJKjygE=");
				break;
			case ProcessorType.PROCESSOR_TYPE_BAIDU_VIDEO_SEARCH:
				request.setHeader("Host", "v.baidu.com");
				request.setHeader("Referer", "http://v.baidu.com/v");
				request.setCookie("BIDUPSID=165A3B8D1F93219D14C0B4A8138AEF6A; PSTM=1503732400; __cfduid=db0db3f162e9aee15f3eb36abaa675d1f1508568007; BAIDUID=17956543C27BB1FA4E9D7A458BC3F00C:FG=1; MCITY=-%3A; d_ad_beforeplay_today_num=6; H_PS_PSSID=1460_19033_21122; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; BDRCVFR[X7WRLt7HYof]=mk3SLVN4HKm; PSINO=5; bdv_right_ad_poster=1; BUBBLESDEl=1");
				break;
			case ProcessorType.PROCESSOR_TYPE_YOUTUBE_LIST:
				request.setCookie("VISITOR_INFO1_LIVE=EzJV7E3gjr0; SID=NAVMifB-wvRZg_XV7HICBT67GT1tKvBQUCq2xskgMTVQhC6LKJaR3apM_949e0lBDSBPtw.; HSID=A_6HcDLRBttV-fEHi; SSID=AteEO8zoelTmY0_4b; APISID=qMYD0zY0RxQ3EqPO/AxuY5BI-_YHQrrbIj; SAPISID=y35kNgi8LP2Nt-yZ/AUY00eUQzGh8jj00y; CONSENT=YES+CN.zh-CN+20170903-09-0; LOGIN_INFO=ACn9GHowRAIgBQH-LEMv1Nuq0klFnwOnmBCHn37kMJb9FnVHiNunlC4CIGVb_ifU0HQK_oJXMSS75NjmyTjp7cD-BB_IAA1E5fMJ:QUxJMndvR2lObFpQdEhQUktfY2hyZ25mOHN2LTZHS0JFWjQzWDNEdXl6dUpMSXFJX24tZDZyVUVrSmt1RnpUUXVERVF6bFZ1S09KOE1yQUkxMVNBVjc0NXN6N1EyTmtDTmVaZi1zUEpyUV9DcDEzQTFySEEta1V2TE5xZTl3dUpsT1d0RVRac1FtLUJ0UkZyVkVwZ3NOMnZZLVVZLXNDbFc0djZ2V3FFUklfVGJ4WHZrYk5pMXBn; PREF=f1=50000000&al=zh-CN; YSC=kaqiTayd01Q; ST-1i14npa=oq=%E4%BA%BA%E5%B7%A5%E6%99%BA%E8%83%BD&gs_l=youtube.3..0i12k1l10.51203.55934.0.56253.20.17.2.0.0.0.730.7059.3-11j1j2j2.16.0....0...1ac.4.64.youtube..5.15.6012...0.0.GAHewAixluE&feature=web-masthead-search&itct=CCoQ7VAiEwimh7_A4JHaAhUHx8EKHZmgDX8ojh4%3D&csn=ofm8WuayG4eOhwaZwbb4Bw");
				request.setHeader("referer", " https://www.youtube.com/");
				request.addHeader("x-client-data", "CIS2yQEIpbbJAQjEtskBCKmdygEIqKPKAQ==");
				request.addHeader("x-spf-previous", "https://www.youtube.com/");
				request.addHeader("x-spf-referer", "https://www.youtube.com/");
				request.addHeader("x-youtube-client-name", "1");
				request.addHeader("x-youtube-client-version", "2.20180328");
				request.addHeader("x-youtube-identity-token", "QUFFLUhqbWtiMHNfdC1UZE96eEFERFlFbUdaRUJfOW8yZ3w=");
				request.addHeader("x-youtube-page-cl", "190715957");
				request.addHeader("x-youtube-page-label", "youtube.ytfe.desktop_20180327_5_RC0");
				request.addHeader("x-youtube-sts", "17616");
				request.addHeader("x-youtube-variants-checksum", "49f92a48c02b74aaca23e1c7b1fd5f9c");
				break;
			case ProcessorType.PROCESSOR_TYPE_FACEBOOK_FRIENDS_LIST:
				//facebook账号cookie
				request.setCookie("datr=6B3CWoBQ0_uvkPBIWSDMiO1l; sb=7R3CWhsjE_MJSIs2bPwriFb6; c_user=100025261115546; xs=35%3AIIrVA_3JwGiPFA%3A2%3A1522671085%3A-1%3A-1; fr=0wiotCZ6lTlDopX0T.AWW9qs8IncTTc5vWSt3nWOHcnoQ.Bawh3o.xf.AAA.0.0.Bawh3t.AWWEOFBl; pl=n; act=1522671572435%2F1; wd=1920x302; presence=EDvF3EtimeF1522671954EuserFA21B25261115546A2EstateFDutF1522671954526CEchFDp_5f1B25261115546F4CC");
				request.setMethod("POST");
				request.setHeader("origin", "https://m.facebook.com");
				request.setHeader("referer", "https://m.facebook.com/thomas.okoto.9/friends");
				request.setHeader("Content-Type", "application/x-www-form-urlencoded");
				request.setHeader("x-requested-with", "XMLHttpRequest");
				request.setHeader("x-response-format", "JSONStream");
				try {
					request.setOutputData(crawlDatum.meta("fbFriendLsParam").getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
		default:
			break;
		}	
	}

	@Override
	public void crawlerBegin() {
	}


	@Override
	public void crawlerEnd() {
	}

	/*private HttpResponse getResponseViaProxy(HttpRequest requester) throws Exception{
		URL url = new URL(requester.getCrawlDatum().getUrl());
		HttpResponse response = new HttpResponse(url);
		Client client = new Client("ie");
		try {
			String content = client.request("https://www.bing.com");
			response.setContent(content.getBytes("utf-8"));
		} finally {
			client.close();
		}
		return response;
	}*/
}
