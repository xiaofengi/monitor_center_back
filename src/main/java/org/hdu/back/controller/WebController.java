package org.hdu.back.controller;

import org.hdu.back.controller.base.BaseController;
import org.hdu.back.mapper.JobDailyMapper;
import org.hdu.back.mapper.JobInfoMapper;
import org.hdu.back.mapper.JobMsgMapper;
import org.hdu.back.mapper.WebPageDetailMapper;
import org.hdu.back.model.JobDaily;
import org.hdu.back.model.JobInfo;
import org.hdu.back.model.PageCountBean;
import org.hdu.back.util.CmdUtil;
import org.hdu.back.util.FileUtil;
import org.hdu.crawler.crawler.HduCrawler;
import org.hdu.crawler.monitor.MonitorExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/web")
public class WebController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Resource
    private JobInfoMapper jobInfoMapper;
    @Resource
    private JobDailyMapper jobDailyMapper;
    @Resource
    private JobMsgMapper jobMsgMapper;
    @Resource
    private WebPageDetailMapper webPageDetailMapper;
    @Resource
    private HduCrawler hduCrawler;

    /**
     * 根据前端输入的关键字，启动爬虫任务
     * @return
     */
    @RequestMapping("/startCrawl")
    public Map startCrawl(String keyword){
    	logger.info("收到关键字爬虫请求");
    	return buildResult(CODE_BUSINESS_ERROR, "该接口暂时关闭");
        /*if(StringUtils.isEmpty(keyword)){
            return buildResult(CODE_BUSINESS_ERROR, "关键字不能为空");
        }
        logger.info("关键字：" + keyword);
        String result = CmdUtil.execCmd("java -jar SearchCrawler-1.0-SNAPSHOT.jar", new File("./crawler"));
        if(result.contains("成功")){
            return buildResult(CODE_SUCCESS, result);
        }else {
            return buildResult(CODE_BUSINESS_ERROR, result);
        }*/
    }
    
    /**
     * 根据前端输入的主题信息，启动爬虫任务
     * @param subFile 主题文件
     * @param depth 层数
     * @param count 总量
     * @param domainFile 域名文件
     * @param limitType current限当前域名 list限制列表域名 all不限
     * @return
     */
    @RequestMapping("/startSubCrawl")
    public Map startSubCrawl(MultipartFile subFile, final Integer depth, final Integer count, MultipartFile domainFile, final String limitType){
    	logger.info("收到主题爬虫请求");
        if(HduCrawler.isStart){
        	return buildResult(CODE_BUSINESS_ERROR, "爬虫已启动，不能再次启动，请耐心等待爬虫完成");
        }
    	if(StringUtils.isEmpty(subFile)){
    		return buildResult(CODE_BUSINESS_ERROR, "请选择主题文件");
    	}
    	if((depth!=null&&depth<1) || (count!=null&&count<=1)){
    		return buildResult(CODE_BUSINESS_ERROR, "深度或数量不能小于1");
    	}
    	//解析主题和域名文件
    	final List<List<Map<String, Object>>> subjectList = new ArrayList<>();
    	final List<Map<String, Object>> domainList = new ArrayList<>();
    	try {
    		String subFileEncoding = FileUtil.getEncoding(subFile.getInputStream());
    		logger.info("主题文件编码格式: " + subFileEncoding); 
			BufferedReader subReader = new BufferedReader(new InputStreamReader(subFile.getInputStream(), subFileEncoding));
			String subjectLine;
			while((subjectLine=subReader.readLine()) != null){
				subjectLine = subjectLine.trim();
				logger.info("输入主题：" + subjectLine);
				if(!subjectLine.equals("")){
					List<Map<String, Object>> subjectInfo = new ArrayList<>();
					String[] keywordLines = subjectLine.split(",");
					for(String keywordLine : keywordLines){
						String[] keywordAndWeight = keywordLine.split(" ");
						if(keywordAndWeight.length != 2){
							return buildResult(CODE_BUSINESS_ERROR, "主题文件格式不正确，格式如(机器学习 4,人工智能 6");
						}
						Map<String, Object> keywordInfo = new HashMap<>();
						keywordInfo.put("keyword", keywordAndWeight[0]);
						keywordInfo.put("weight", keywordAndWeight[1]);
						subjectInfo.add(keywordInfo);
					}
					subjectList.add(subjectInfo);
				}
			}
			if(domainFile != null){
				String domainFileEncoding = FileUtil.getEncoding(domainFile.getInputStream());
	    		logger.info("域名文件编码格式: " + domainFileEncoding); 
				BufferedReader domainReader = new BufferedReader(new InputStreamReader(domainFile.getInputStream(), domainFileEncoding));
				String domainLine;
				while((domainLine=domainReader.readLine()) != null){
					domainLine = domainLine.trim();
					logger.info("输入域名：" + domainLine);
					if(!domainLine.equals("")){
						String[] domainAndLocation = domainLine.split(" ");
						if(domainAndLocation.length != 2){
							return buildResult(CODE_BUSINESS_ERROR, "域名文件格式不正确，格式如(www.baidu.com 国内)");
						}
						Map<String, Object> domainInfo = new HashMap<>();
						domainInfo.put("domain", domainAndLocation[0]);
						domainInfo.put("location", domainAndLocation[1]);
						domainList.add(domainInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if(subjectList.isEmpty()){
    		return buildResult(CODE_BUSINESS_ERROR, "主题文件中内容不可为空");
    	}
    	//在子线程中运行爬虫
    	new Thread(new Runnable() {		
			@Override
			public void run() {
				hduCrawler.start(subjectList, depth, count, domainList, limitType);
			}
		}).start();
    	return buildResult(CODE_SUCCESS, "启动爬虫成功");
    }
    
    /**
     * 停止爬虫
     * @return
     */
    @RequestMapping("/stopSubCrawl")
    public Map stopSubCrawl(){
    	logger.info("收到爬虫结束指令");
    	if(HduCrawler.isStart){
    		hduCrawler.stop();
			return buildResult(CODE_SUCCESS, "停止爬虫成功");
    	}else {
			return buildResult(CODE_BUSINESS_ERROR, "爬虫未启动，无法停止");
		}
    }

    /**
     * 向前端返回监控状态信息
     * @param jobId 任务id,前端暂时写成1
     * @return status(0未完成1已完成) nowDepth(当前层数) tableData(监控信息)
     */
    @RequestMapping("/getMsg")
    public Map getMsg(Integer jobId){
    	logger.info("向前端返回监控状态信息");
        if(jobId == null){
            return buildResult(CODE_BUSINESS_ERROR, "任务id不能为空");
        }
        List<Map> jobMsgLs = jobMsgMapper.getJobMsg(jobId);
        if(jobMsgLs.isEmpty()){
        	return buildResult(CODE_BUSINESS_ERROR, "爬虫启动失败，数据库监控状态信息为空");
        }else {
            Map<String, Object> data = new HashMap<>();
            JobDaily jobDaily = jobDailyMapper.getLastDailyInfo(jobId);
            if(jobDaily.getEndTime() == null){
            	data.put("status", 0);
            }else{
            	data.put("status", 1);
            }
            data.put("nowDepth", HduCrawler.nowDepth);
            data.put("tableData", jobMsgLs);
            return buildResult(data);
		}
    }
    
    /**
     * 向前端返回查询结果
     * @param input1 输入1
     * @param select1 选择类型1
     * @param relation1 关系1
     * @param input2 输入2
     * @param select2 选择类型2
     * @param relation2 关系2
     * @param input3 输入3
     * @param select3 选择类型3
     * @return
     */
    @RequestMapping("/getResult")
    public Map getResult(String input1, String select1, String relation1, String input2, String select2, 
    							String relation2, String input3, String select3, Integer pageIndex, Integer pageSize){
    	logger.info("向前端返回查询结果");
    	if(StringUtils.isEmpty(input1) && StringUtils.isEmpty(input2) && StringUtils.isEmpty(input3)){
    		return buildResult(CODE_BUSINESS_ERROR, "搜索条件请至少输入一个");
    	}
		if(pageIndex==null || pageSize==null){
			return buildResult(CODE_BUSINESS_ERROR, "页数或条数不能为空");
		}
    	if(pageIndex<1 || pageSize<10){
			return buildResult(CODE_BUSINESS_ERROR, "页数或条数不可小于1");
		}
		PageCountBean pageCountBean = new PageCountBean(pageIndex, pageSize);
    	List<Map> resultList = webPageDetailMapper.getResult(input1, select1, relation1, input2, select2, 
    																relation2, input3, select3, pageCountBean);
    	return buildResult("resultList", resultList, pageCountBean);
    }
}
