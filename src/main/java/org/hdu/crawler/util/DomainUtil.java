package org.hdu.crawler.util;

import cn.edu.hfut.dmic.webcollector.model.Page;

import java.util.Map;

import org.hdu.crawler.crawler.HduCrawler;

public class DomainUtil {

    /**
     * 根据用户输入域名地址列表，判断是否限制域名
     * @param domain
     */
    public static boolean limitedDomain(String domain, Page page){
        if(HduCrawler.domainList != null){ //限制域名
            switch (HduCrawler.limitType) {
                case "current": //限当前域名
                    if(page.meta("domain").equals(domain)){
                        return false;
                    }else {
                        return true;
                    }
                case "list": //限列表
                    boolean isContains = false;
                    for(Map<String, Object> domainInfo : HduCrawler.domainList){
                        if(domainInfo.get("domain").toString().equals(domain)){
                            isContains = true;
                            break;
                        }
                    }
                    if(!isContains){
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }else {
            return false;
        }
    }

}
