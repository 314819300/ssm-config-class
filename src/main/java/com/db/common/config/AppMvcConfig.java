package com.db.common.config;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.db.common.web.AccessInterceptor;
/**
 * 在此配置类中实现spring mvc 资源对象的整合
 * @author ta
 */
@ComponentScan(
    value="com.db",
    useDefaultFilters=false,//取消默认过滤器
    includeFilters={//只加载有指定注解修饰的类
      @Filter(type=FilterType.ANNOTATION,
              classes={Controller.class,
                       ControllerAdvice.class})})
@EnableWebMvc //启用mvc默认配置(内置很多类型转换器bean对象)
public class AppMvcConfig extends WebMvcConfigurerAdapter {
	@Override
	public void configureViewResolvers(
			ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/pages/",".html");
	}
	/**
	 * 注册拦截器
	 */
	@Override
	public void addInterceptors(
			InterceptorRegistry registry) {
		//构建拦截器对象
		AccessInterceptor interceptor=
		new AccessInterceptor();
		//注册拦截器对象
		registry.addInterceptor(interceptor)
		//配置要拦截的url
		.addPathPatterns("/user/doLogin.do");
	}
	
}






