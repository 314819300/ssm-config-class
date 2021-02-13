package com.db.common.web;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.db.common.exception.ServiceException;

public class AccessInterceptor extends HandlerInterceptorAdapter{
	  /**
	   * 控制层方法执行之前执行
	   * @return 结果代表请求是继续向下执行还是到此为止。
	   */
	  @Override
	  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		  System.out.println("==preHanle==");
		  long time=System.currentTimeMillis();
		  //设置访问时间
		  Calendar c1=Calendar.getInstance();
		  c1.set(Calendar.HOUR_OF_DAY,8);
		  c1.set(Calendar.MINUTE,25);
		  c1.set(Calendar.SECOND,0);
		  long startTime=c1.getTimeInMillis();
		  c1.set(Calendar.HOUR_OF_DAY,20);
		  long endTime=c1.getTimeInMillis();
		  if(time<startTime||time>endTime){
			  throw new ServiceException("8:25~17:25 允许登录");
		  }
		  return true;//true为放行，false表示请求到此为止。
	  }
}






