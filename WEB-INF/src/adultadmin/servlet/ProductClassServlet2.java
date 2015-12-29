package adultadmin.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.action.vo.voCatalog;

import cache.CatalogCache;

public class ProductClassServlet2 extends HttpServlet {

	ServletContext servlet = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setCharacterEncoding("UTF-8"); 
		
		response.getWriter().println("spts_count = 100");
		response.getWriter().println("var spts = new Array(spts_count)");
		response.getWriter().println("for (i = 0; i < spts_count; i ++) {");
		response.getWriter().println("  spts[i]=new Array()");
		response.getWriter().println("}");
		response.getWriter().println("var tpts_count = 100");
		response.getWriter().println("var tpts = new Array(100)");
		response.getWriter().println("for(i = 0; i < spts_count; i ++){");
		response.getWriter().println("	tpts[i] = new Array()");
		response.getWriter().println("	for(j = 0; j < tpts_count; j ++){");
		response.getWriter().println("		tpts[i][j] = new Array()");
		response.getWriter().println("	}");
		response.getWriter().println("}");
		
		//一级
		HashMap firstMap = (HashMap)CatalogCache.catalogLevelList.get(0);
		HashMap secondMap = (HashMap)CatalogCache.catalogLevelList.get(1);
		HashMap thirdMap = (HashMap)CatalogCache.catalogLevelList.get(2);
		List firstList = (List)firstMap.get(Integer.valueOf(0));
		response.getWriter().println("spts[0][0] = new Option(\"全部\", \"0\")");
		for(int i=0;i<firstList.size();i++){
			voCatalog catalog = (voCatalog)firstList.get(i);
			
			//二级
			List secondList = (List)secondMap.get(Integer.valueOf(catalog.getId()));
			if(secondList==null){
				response.getWriter().println("spts["+(i+1)+"][0] = new Option(\"全部\", \"0\")");
			}else{
				response.getWriter().println("spts["+(i+1)+"][0] = new Option(\"全部\", \"0\")");
				for(int j=0;j<secondList.size();j++){
					catalog = (voCatalog)secondList.get(j);
					response.getWriter().println("spts["+(i+1)+"]["+(j+1)+"] = new Option(\""+catalog.getName()+"\", \""+catalog.getId()+"\")");
					
					//三级
					List thirdList = (List)thirdMap.get(Integer.valueOf(catalog.getId()));
					if(thirdList == null||thirdList.size()==0){
						response.getWriter().println("tpts["+(i+1)+"]["+(j+1)+"][0] = new Option(\"全部\", \"0\")");
					}else{
						response.getWriter().println("tpts["+(i+1)+"]["+(j+1)+"][0] = new Option(\"全部\", \"0\")");
						for(int k=0;k<thirdList.size();k++){
							catalog = (voCatalog)thirdList.get(k);
							response.getWriter().println("tpts["+(i+1)+"]["+(j+1)+"]["+(k+1)+"] = new Option(\""+catalog.getName()+"\", \""+catalog.getId()+"\")");
						}
					}
				}
			}
		}
		
		response.getWriter().close();
	}
	
	public void destroy() {
		if (servlet != null) {

		}

		super.destroy();
	}
}
