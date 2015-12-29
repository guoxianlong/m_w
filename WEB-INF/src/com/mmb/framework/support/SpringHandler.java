package com.mmb.framework.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.mmb.framework.utils.AssertUtils;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时�?取出ApplicaitonContext.
 * 
 */
public class SpringHandler implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	private static Logger logger = LoggerFactory.getLogger(SpringHandler.class);

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋�?对象的类�?
	 */
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋�?对象的类�?
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		logger.debug("清除SpringContextHolder中的ApplicationContext:"
				+ applicationContext);
		applicationContext = null;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.debug("注入ApplicationContext到SpringContextHolder:"
				+ applicationContext);

		if (SpringHandler.applicationContext != null) {
			logger.warn("SpringContextHolder中的ApplicationContext被覆�? 原有ApplicationContext�?"
					+ SpringHandler.applicationContext);
		}

		SpringHandler.applicationContext = applicationContext; // NOSONAR
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变�?
	 */
	public void destroy() throws Exception {
		SpringHandler.clearHolder();
	}

	/**
	 * �?��ApplicationContext不为�?
	 */
	private static void assertContextInjected() {
		AssertUtils.state(applicationContext != null,
						"applicaitonContext属�?未注�? 请在applicationContext.xml中定义SpringContextHolder.");
	}
}
