package adultadmin.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voUser;
import adultadmin.util.StringUtil;

import cache.CatalogCache;
import cache.ProductLinePermissionCache;

public class ProductClassServlet extends HttpServlet {

	ServletContext servlet = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		
		boolean permission = StringUtil.toBoolean(request.getParameter("permission"));
		
		response.setCharacterEncoding("UTF-8"); 
		
		response.getWriter().println("spts_count = 100");
		response.getWriter().println("var spts = new Array(spts_count)");
		response.getWriter().println("for (i = 0; i < spts_count; i ++) {");
		response.getWriter().println("  spts[i]=new Array()");
		response.getWriter().println("}");
		response.getWriter().println("var tpts_count = 40");
		response.getWriter().println("var tpts = new Array(40)");
		response.getWriter().println("for(i = 0; i < spts_count; i ++){");
		response.getWriter().println("	tpts[i] = new Array()");
		response.getWriter().println("	for(j = 0; j < tpts_count; j ++){");
		response.getWriter().println("		tpts[i][j] = new Array()");
		response.getWriter().println("	}");
		response.getWriter().println("}");
		
		//产品线权限
		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(user);
		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(user);
		String catalogIdsTemp = "";
		if(!catalogIds2.equals("")){
			String[] splits = catalogIds2.split(",");
			for(int i=0;i<splits.length;i++){
				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
				}
			}
			catalogIds1 = catalogIdsTemp + catalogIds1;
			if(catalogIds1.endsWith(",")){
				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
			}
		}
		
		//一级
		HashMap firstMap = CatalogCache.getFirstMap();
		HashMap secondMap = CatalogCache.getSecondMap();
		HashMap thirdMap = CatalogCache.getThirdMap();
		List firstList = (List)firstMap.get(Integer.valueOf(0));
		int indexFirst = 0;
		int level = 0;
		for(int i=0;i<firstList.size();i++){
			voCatalog catalog = (voCatalog)firstList.get(i);
			
			if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(catalog.getId()))){
				level = 2;
			}else{
				level = 1;
			}
			if(permission && !StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
				continue;
			}
			
			//二级
			List secondList = (List)secondMap.get(Integer.valueOf(catalog.getId()));
			if(secondList==null){
				response.getWriter().println("spts["+indexFirst+"][0] = new Option(\"不限\", \"0\")");
			}else{
				int indexSecond = 0;
				for(int j=0;j<secondList.size();j++){
					catalog = (voCatalog)secondList.get(j);
					if(permission && level == 2 && !StringUtil.hasStrArray(catalogIds2.split(","),String.valueOf(catalog.getId()))){
						continue;
					}
					
					response.getWriter().println("spts["+indexFirst+"]["+indexSecond+"] = new Option(\""+catalog.getName()+"\", \""+catalog.getId()+"\")");
					
					//三级
					List thirdList = (List)thirdMap.get(Integer.valueOf(catalog.getId()));
					if(thirdList == null||thirdList.size()==0){
						response.getWriter().println("tpts["+indexFirst+"]["+indexSecond+"][0] = new Option(\"不限\", \"0\")");
					}else{
						for(int k=0;k<thirdList.size();k++){
							catalog = (voCatalog)thirdList.get(k);
							response.getWriter().println("tpts["+indexFirst+"]["+indexSecond+"]["+k+"] = new Option(\""+catalog.getName()+"\", \""+catalog.getId()+"\")");
						}
					}
					indexSecond++;
				}
			}
			indexFirst++;
		}
		
		response.getWriter().close();
	}
	
	public void destroy() {
		if (servlet != null) {

		}

		super.destroy();
	}
}
