<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="adultadmin.bean.order.AreaStreetBean"%>
<%@page import="adultadmin.service.impl.CityAreaService"%>

<%
	String method = request.getParameter("method");
	String parentId = request.getParameter("pranetId");
	CityAreaService cityOper = new CityAreaService();
	if(method==null) return;
	if(parentId==null || parentId.equals("")) return;
	List list =null;
	AreaStreetBean dto = new AreaStreetBean();
	dto.setAreaId(Integer.parseInt(parentId));
	
	if(method.equals("sheng")){
		list = cityOper.getProvinceList();
	}else if(method.equals("shi")){
		list = cityOper.getCityList(dto);
	}else if(method.equals("qu")){
		list = cityOper.getAreaList(dto);
	}else if(method.equals("streetId")){ 
		list = cityOper.getStreetList(dto);
	}
	
	response.setCharacterEncoding("utf-8");
	response.setContentType("text/xml");
	if(list!=null){
		StringBuffer bs = new StringBuffer();
		bs.append("<note>");
		for(int i=0;i<list.size();i++){
			AreaStreetBean cdto=(AreaStreetBean)list.get(i);
			bs.append("<name value='"+cdto.getId()+"' >"+cdto.getStreet()+"</name>");
		}
		if(method.equals("qu")){
			bs.append("<name value='0'>其他区</name>");
		}else if(method.equals("streetId")){
			bs.append("<name value='0'>其他街道</name>");
		}
		bs.append("</note>");
		out.write(bs.toString());
	}else{
		out.write("");
	}
%>

 