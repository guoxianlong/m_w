package com.mmb.framework.support;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	public static String MASTER = "master";
	public static String SLAVE = "slave";
	private static ThreadLocal<String> local = new ThreadLocal<String>();
	

	@Override
	protected Object determineCurrentLookupKey() {
		String dString = local.get() == null ? MASTER : local.get();
        setRoute(DynamicDataSource.MASTER);
        return dString;
	}

	public static void setRoute(String route) {
		if (route == null || route.equals("")) {
			route = MASTER;
		}
		local.set(route);
	}

	public static void remoteRoute() {
		local.remove();
	}

	public static Object getRoute() {
		return local.get();
	}
}
