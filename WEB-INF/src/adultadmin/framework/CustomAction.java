package adultadmin.framework;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import adultadmin.util.StringUtil;


public class CustomAction {
	public static String defaultResult = "failure";
	public static String defaultTip = "参数错误";
	
	protected HttpServletRequest request = null;
	protected HttpServletResponse response = null;
	
	protected HttpSession session = null;
	
	protected String tip = null;
	protected String result = null;
	
	public CustomAction(HttpServletRequest request) {
		this.request = request;
		session = request.getSession();
	}

	public CustomAction(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		session = request.getSession();
	}

	public void doTip(String result, String tip) {
		if (result == null) {
			result = defaultResult;
		}
		if (tip == null) {
			tip = defaultTip;
		}
		request.setAttribute("result", result);
		request.setAttribute("tip", tip);
	}
	
	public void tip(String result, String tip) {
		this.result = result;
		this.tip = tip;
	}
	
	public void tip(String result) {
		this.result = result;
	}
	
	public String getTip() {
		if(tip == null)
			return defaultTip;
		else
			return tip;
	}
	
	public boolean isResult(String result) {
		return this.result != null && this.result.equals(result);
	}
	
	public String getResult() {
		if(result == null)
			return defaultResult;
		else
			return result;
	}
	
	public void sendRedirect(String url, HttpServletResponse response) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(url));
	}
	
	public int getAttributeInt(String name) {
		try {
			return ((Integer)request.getAttribute(name)).intValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public float getAttributeFloat(String name) {
		try {
			return ((Float)request.getAttribute(name)).floatValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public long getAttributeLong(String name) {
		try {
			return ((Long)request.getAttribute(name)).longValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void setAttribute(String name, int value) {
		request.setAttribute(name, Integer.valueOf(value));
	}
	
	public void setAttribute(String name, long value) {
		request.setAttribute(name, Long.valueOf(value));
	}
	
	public void setAttribute(String name, float value) {
		request.setAttribute(name, Float.valueOf(value));
	}
	
	public void setAttribute(String name, Object value) {
		request.setAttribute(name, value);
	}
	
	public int getParameterInt(String name) {
		try {
			return Integer.parseInt(request.getParameter(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int getParameterIntS(String name) {
		try {
			return Integer.parseInt(request.getParameter(name));
		} catch (Exception e) {
			return -1;
		}
	}
	
	public long getParameterLong(String name) {
		try {
			return Long.parseLong(request.getParameter(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	public float getParameterFloat(String name) {
		try {
			return Float.parseFloat(request.getParameter(name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	public String getParameterString(String name) {
		return request.getParameter(name);
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 

	public Date getParameterDate(String name) {
		try {
			return sdf.parse(request.getParameter(name));
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isMethodGet() {
		return request.getMethod().equalsIgnoreCase("get");
	}
	
	/**
	 * 判断是否已经冷却
	 * @return
	 */
	public boolean isCooldown(String param, long cd) {
		param = "cd-" + param;
		try {
			Long t = (Long)session.getAttribute(param);
			long t2 = System.currentTimeMillis();;
			if(t == null || t2 - t.longValue() > cd) {
				session.setAttribute(param, Long.valueOf(t2));
				return true;
			} else {
				return false;
			}
			
		} catch(Exception e) {
			return false;
		}
	}
	
	public boolean hasParam(String param) {
		return request.getParameter(param) != null;
	}
	
	public void innerRedirect(String url, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	// 如果要使用以下两个跳转函数，那么构造的时候必须带response
	public void innerRedirect(String url) throws ServletException, IOException {
		request.getRequestDispatcher(url).forward(request, response);
	}
	public void redirect(String url) throws IOException {
		response.sendRedirect(response.encodeURL(url));
	}
	
	public int[] getParameterIntList(String name) {
		String[] ints = request.getParameterValues(name);
		if(ints == null)
			return new int[0];
		int[] is = new int[ints.length];
		for(int i = 0;i < ints.length;i++) {
			try {
				is[i] = Integer.parseInt(ints[i]);
			} catch (Exception e) {
				is[i] = 0;
			}
		}
		return is;
	}
	
	public List getParameterIntList2(String name) {
		List list = new ArrayList();
		String[] p = request.getParameterValues(name);
		if(p != null) {
			if(p.length == 1 && p[0].indexOf(';') != -1) {
				p = p[0].split(";");
				for(int i = 0;i < p.length;i++) {
					int v = StringUtil.toInt(p[i]);
					if(v >= 0)
						list.add(Integer.valueOf(v));
				}
			} else {
				for(int i = 0;i < p.length;i++) {
					int v = StringUtil.toInt(p[i]);
					if(v >= 0)
						list.add(Integer.valueOf(v));
				}
			}
		}
		return list;
	}
	
	// 根据一堆int，每个是位数，获得二进制标志的结果
	public static long getFlag(List list) {
		long flag = 0;
		for(int i = 0;i < list.size();i++) {
			flag |= ( 1l << ((Integer)list.get(i)).intValue());
		}
		return flag;
	}
	public long getParameterFlag(String name) {
		List list = getParameterIntList2(name);
		return getFlag(list);
	}
	// 每个long存储60个状态flag
	public long[] getParameterFlag(String name, int count) {
		if(count <= 0 || count > 1024)
			return null;
		List list = getParameterIntList2(name);
		return getFlag(list, count);
	}
	public static long[] getFlag(List list, int count) {
		long[] flags = new long[count];
		for(int i = 0;i < list.size();i++) {
			int isf = ((Integer)list.get(i)).intValue();
			int c = isf / 60;
			if(c >= 0 && c < count)
				flags[c] |= ( 1l << (isf % 60));
		}
		return flags;
	}
}
