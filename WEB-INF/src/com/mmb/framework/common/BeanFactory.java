package com.mmb.framework.common;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BeanFactory {

	public static Object getBean(String key, ServletContext sc){
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
		return ctx.getBean(key);
	}
}
