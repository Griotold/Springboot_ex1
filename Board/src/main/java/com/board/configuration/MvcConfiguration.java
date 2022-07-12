package com.board.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.board.interceptor.LoggerInterceptor;
@Configuration
public class MvcConfiguration implements WebMvcConfigurer{

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//레지스트리에 인터셉터를 등록해라
		registry.addInterceptor(new LoggerInterceptor())
		//인자로 들어온놈들의 경로 패턴은 제외한다.
		.excludePathPatterns("/css/**", "/fonts/**", "/plugin/**", "/scripts/**");
	}

}
