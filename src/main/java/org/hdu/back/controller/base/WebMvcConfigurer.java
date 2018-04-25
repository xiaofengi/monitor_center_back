package org.hdu.back.controller.base;

import org.hdu.back.interceptor.RequestLoggerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

	@Resource
	private RequestLoggerInterceptor loggerinter;

	// 增加拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggerinter) // 指定拦截器类
				.addPathPatterns("/**").excludePathPatterns("/static/**").excludePathPatterns("/content/**")
				.excludePathPatterns("/**.html"); // 指定该类拦截的url
		super.addInterceptors(registry);
	}
}