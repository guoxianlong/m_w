<%@page import="java.net.URLEncoder"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,adultadmin.framework.*,java.math.*" %>
<%@ page import="adultadmin.bean.stock.*,java.util.*,adultadmin.bean.buy.BuyStockinProductBean" %>
<%@ page import="adultadmin.util.*,java.net.*,adultadmin.bean.order.OrderStockProductBean" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement,java.text.*" %>
<%@page import="cache.*"%>
<%
	String fileName = "分库库存查询导出_"+DateUtil.getNowDateStr()+".xls";
	response.setContentType("application/msexcel"); 
	response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes("GBK"), "iso8859-1"));
%> 
<%
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();
	
	boolean isSystem = (adminUser.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (adminUser.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (adminUser.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (adminUser.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (adminUser.getPermission() == 7);	//销售部
	boolean isShangpin = (adminUser.getPermission() == 6);	//商品部
	boolean isTuiguang = (adminUser.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (adminUser.getPermission() == 4);	//运营中心
	boolean isKefu = (adminUser.getPermission() == 3);	//客服部	
	
	int type=-1,area=-1;
	
	int proxy = StringUtil.toInt(request.getParameter("proxy"));
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	if(request.getAttribute("stockType")!=null){
		type=((Integer)request.getAttribute("stockType")).intValue();
		//System.out.println("type-->"+type);
	}
	if(request.getAttribute("stockArea")!=null){
		area=((Integer)request.getAttribute("stockArea")).intValue();
	}
	
	int count = StringUtil.StringToId((String)request.getAttribute("count"));
%>

<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
 <thead>
  	<tr bgcolor="#4688D6">              
		<td align="center" onclick="sortTable('listTable', 0, 'int')" style="cursor:pointer"><font color="#FFFFFF">产品编号</font></td>
		<td align="center"><font color="#FFFFFF">小店名称</font></td>
		<td align="center"><font color="#FFFFFF">产品原名称</font></td>
		
  	<%if(type==1){//待验库%>
  		<%if(area==0||area==-1){%>
        	<td align="center"><font color="#FFFFFF">北京</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(北库)</font></td><%}}%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}%>
        <%if(area==3||area==-1){%>
        	<td align="center"><font color="#FFFFFF">增城</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(增城)</font></td><%}}%>
        <%if(area==4||area==-1){%>
        	<td align="center"><font color="#FFFFFF">无锡</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(无锡)</font></td><%}}}%>
    <%if(type==0){//合格库%>
    	<%if(area==0||area==-1){%>  
        	<td align="center"><font color="#FFFFFF">北京</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(北库)</font></td><%}}%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}%>
        <%if(area==2||area==-1){%>      
        	<td align="center"><font color="#FFFFFF">广速</font></td>
        	<%if(group.isFlag(95)) {%> 
        	<td align="center"><font color="#FFFFFF">金额(广速)</font></td><%}}%>    
        <%if(area==3||area==-1){%>      
        	<td align="center"><font color="#FFFFFF">增城</font></td>
        	<%if(group.isFlag(95)) {%> 
        	<td align="center"><font color="#FFFFFF">金额(增城)</font></td><%}}%> 
        <%if(area==4||area==-1){%>      
        	<td align="center"><font color="#FFFFFF">无锡</font></td>
        	<%if(group.isFlag(95)) {%> 
        	<td align="center"><font color="#FFFFFF">金额(无锡)</font></td><%}}}%>    
    <%if(type==6){//样品库%>
  		<%if(area==0||area==-1){%>
        	<td align="center"><font color="#FFFFFF">北京</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(北库)</font></td><%}}%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}%>   
        <%if(area==3||area==-1){%>
        	<td align="center"><font color="#FFFFFF">增城</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(增城)</font></td><%}}%>   
        <%if(area==4||area==-1){%>
        	<td align="center"><font color="#FFFFFF">无锡</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(无锡)</font></td><%}}}%>   
    <%if(type==4){//退货库%>
    	<%if(area==0||area==-1){%>  
        	<td align="center"><font color="#FFFFFF">北京</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(北库)</font></td><%}}%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}%>
        <%if(area==2||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">广速</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(广速)</font></td><%}}%>
        <%if(area==3||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">增城</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(增城)</font></td><%}}%>
        <%if(area==4||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">无锡</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(无锡)</font></td><%}}}%>
    <%if(type==3){//返厂库%>    
    	<%if(area==0||area==-1){%>
        	<td align="center"><font color="#FFFFFF">北京</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(北库)</font></td><%}}%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}%>
        <%if(area==3||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">增城</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(增城)</font></td><%}}%>
        <%if(area==4||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">无锡</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(无锡)</font></td><%}}}%>
    <%if(type==2){//维修库%> 
    	<%if(area==0||area==-1){%>
        	<td align="center"><font color="#FFFFFF">北京</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(北库)</font></td><%}}%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}%>
        <%if(area==3||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">增城</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(增城)</font></td><%}}}%>
    <%if(type==5){//残次品库%>
    	<%if(area==0||area==-1){%>  
        	<td align="center"><font color="#FFFFFF">北京</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(北库)</font></td><%}}%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}%>
        <%if(area==2||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">广速</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(广速)</font></td><%}}%>
        <%if(area==3||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">增城</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(增城)</font></td><%}}%>
        <%if(area==4||area==-1){%>       
        	<td align="center"><font color="#FFFFFF">无锡</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(无锡)</font></td><%}}}%>
    <%if(type==9){//售后库%>
        <%if(area==1||area==-1){%>
        	<td align="center"><font color="#FFFFFF">芳村</font></td>
        	<%if(group.isFlag(95)) {%>
        	<td align="center"><font color="#FFFFFF">金额(芳村)</font></td><%}}}%>
        	
        <td align="center" ><font color="#FFFFFF">库存总数</font></td> 
        <%if(group.isFlag(95)) {%> 	
        <td align="center"  ><font color="#FFFFFF">总金额</font></td> <%} %>      
        <td align="center" ><font color="#FFFFFF">状态</font></td>
		<td align="center"><font color="#FFFFFF">一级分类</font></td>
		<td align="center" ><font color="#FFFFFF">二级分类</font></td>
		<td align="center" ><font color="#FFFFFF">三级分类</font></td>
  	</tr>
 </thead>
<tbody>
<%if(count != 0){ %>
<%float total=0; int c=0;%>
<logic:present name="productList" scope="request">
<logic:iterate name="productList" id="item" type="adultadmin.action.vo.voProduct" > 
<%
Hashtable hk=new Hashtable();
int t=item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0);//合格库的数量
int a1=item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1) + item.getStock(3,1) + item.getLockCount(3,1) + item.getStock(4,1) + item.getLockCount(4,1);
int a2=item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0);
int a3=item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4);
int a4=item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3) + item.getStock(3,3) + item.getLockCount(3,3) + item.getStock(4,3) + item.getLockCount(4,3);
int a5=item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2);
int a6=item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5)+item.getStock(4,5) + item.getLockCount(4,5);
int a7=item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6)+ item.getStock(4,6) + item.getLockCount(4,6);//样品库总数
int a9=item.getStock(1,9) + item.getLockCount(1,9);//售后库总数
if(type==1||area==-1){//待检库
	int k1=item.getStock(0,1) + item.getLockCount(0,1);//北京
	int k2=item.getStock(1,1) + item.getLockCount(1,1);//芳村
	int k3=item.getStock(3,1) + item.getLockCount(3,1);//增城
	int k4=item.getStock(4,1) + item.getLockCount(4,1);//无锡
	hk.put("0",""+k1);hk.put("1",""+k2);hk.put("3",""+k3);hk.put("4",""+k4);
}
if(type==0||area==-1){//合格库
	int k1=item.getStock(0,0) + item.getLockCount(0,0);//北京
	int k2=item.getStock(1,0) + item.getLockCount(1,0);//芳村
	int k3=item.getStock(2,0) + item.getLockCount(2,0);//广速
	int k4=item.getStock(3,0) + item.getLockCount(3,0);//增城
	int k5=item.getStock(4,0) + item.getLockCount(4,0);//无锡
	hk.put("0",""+k1);hk.put("1",""+k2);hk.put("2",""+k3);hk.put("3",""+k4);hk.put("4",""+k5);
}
if(type==6||area==-1){//样品库
	int k1=item.getStock(0,6) + item.getLockCount(0,6);//北京
	int k2=item.getStock(1,6) + item.getLockCount(1,6);//芳村
	int k3=item.getStock(3,6) + item.getLockCount(3,6);//增城
	int k4=item.getStock(4,6) + item.getLockCount(4,6);//增城
	hk.put("0",""+k1);hk.put("1",""+k2);hk.put("3",""+k3);hk.put("4",""+k4);
}
if(type==4||area==-1){//退货库
	int k1=item.getStock(0,4) + item.getLockCount(0,4);//北京
	int k2=item.getStock(1,4) + item.getLockCount(1,4);//芳村
	int k3=item.getStock(2,4) + item.getLockCount(2,4);//广速
	int k4=item.getStock(3,4) + item.getLockCount(3,4);//增城
	int k5=item.getStock(4,4) + item.getLockCount(4,4);//无锡
	hk.put("0",""+k1);hk.put("1",""+k2);hk.put("2",""+k3);hk.put("3",""+k4);hk.put("4",""+k5);
}
if(type==3||area==-1){//返厂库
	int k1=item.getStock(0,3) + item.getLockCount(0,3);//北京
	int k2=item.getStock(1,3) + item.getLockCount(1,3);//芳村
	int k3=item.getStock(2,3) + item.getLockCount(2,3);//广速
	int k4=item.getStock(3,3) + item.getLockCount(3,3);//增城
	int k5=item.getStock(4,3) + item.getLockCount(4,3);//无锡
	hk.put("0",""+k1);hk.put("1",""+k2);hk.put("2",""+k3);hk.put("3",""+k4);hk.put("4",""+k5);
}
if(type==2||area==-1){//维修库
	int k1=item.getStock(0,2) + item.getLockCount(0,2);//北京
	int k2=item.getStock(1,2) + item.getLockCount(1,2);//芳村
	int k3=item.getStock(3,2) + item.getLockCount(3,2);//增城
	hk.put("0",""+k1);hk.put("1",""+k2);hk.put("3",""+k3);
}
if(type==5||area==-1){//残次品
	int k1=item.getStock(0,5) + item.getLockCount(0,5);//北京
	int k2=item.getStock(1,5) + item.getLockCount(1,5);//芳村
	int k3=item.getStock(2,5) + item.getLockCount(2,5);//广速
	int k4=item.getStock(3,5) + item.getLockCount(3,5);//增城
	int k5=item.getStock(4,5) + item.getLockCount(4,5);//无锡
	hk.put("0",""+k1);hk.put("1",""+k2);hk.put("2",""+k3);hk.put("3",""+k4);hk.put("4",""+k5);
}
if(type==9||area==-1){//售后库
	int k1=item.getStock(1,9) + item.getLockCount(1,9);//芳村
	hk.put("1",""+k1);
}
Hashtable ha=new Hashtable();//key-地区 value-库存数
ha.put("1",""+a1);ha.put("0",""+a2);ha.put("4",""+a3);ha.put("3",""+a4);ha.put("2",""+a5);ha.put("5",""+a6);ha.put("6",""+a7);ha.put("9",""+a9);

//System.out.println(type+"  "+area);
//根据所传的地域和类型得出所有数值,并判断其是否该显示
int haha=Integer.valueOf((String)ha.get(""+type)).intValue();//显示所选类型有数据的行
int kaka=0;//表示所选地域是否有数据
if(area>=0){
//System.out.println("area-->"+area);
kaka=Integer.valueOf((String)hk.get(""+area)).intValue();//显示所选地域 有数据的行
//System.out.println("area-->"+kaka);
}if(area==-1){kaka=1;}//如果选地域为全部,显示全部
int b=0;
//if(ht.get(""+item.getId())!=null){
//	b=((OrderStockProductBean)ht.get(""+item.getId())).getStockoutCount();	
//}
if(haha>0&&kaka>0){	//当所选区域库存数不为0 或者 缺货时显示
//	if(area == -1){
//		total=total+(item.getStockAllType(type) + item.getLockCountAllType(type))*item.getPrice5();
//	}else{
//		total=total+(item.getStock(area,type) + item.getLockCount(area,type))*item.getPrice5();
//	}
%>
	<tr bgcolor='#F8F8F8'>	
		<td align="left"><bean:write name="item" property="code" /></td>
		<td align='center' width="150"><bean:write name="item" property="name" /></td>
		<td align='center' width="150"><bean:write name="item" property="oriname" /></td>
	
		<%if(type==1){//待验库%>
		<%
		if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1) + item.getStock(3,1) + item.getLockCount(3,1) + item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,1) + item.getLockCount(area,1))*item.getPrice5();
			}
		}
		%>
	  		<%if(area==0||area==-1){%>
	        	<td align="center"><%= item.getStock(0,1) + item.getLockCount(0,1) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(0,1) + item.getLockCount(0,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()   %><%} %></td><%}%>
	        <%if(area==1||area==-1){%>
	        	<td align='center'><%= item.getStock(1,1) + item.getLockCount(1,1)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal( (item.getStock(1,1) + item.getLockCount(1,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==3||area==-1){%>
	        	<td align='center'><%= item.getStock(3,1) + item.getLockCount(3,1)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal( (item.getStock(3,1) + item.getLockCount(3,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==4||area==-1){%>
	        	<td align='center'><%= item.getStock(4,1) + item.getLockCount(4,1)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal( (item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <td align='center'><%= item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1)+ item.getStock(3,1) + item.getLockCount(3,1)+ item.getStock(4,1) + item.getLockCount(4,1)%></td>	        		        	
	        <td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1)+ item.getStock(3,1) + item.getLockCount(3,1)+ item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>		
		<%if(type==0){//合格库%>				
		<%
		if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(0,0) + item.getLockCount(0,0) + item.getStock(1,0) + item.getLockCount(1,0) + item.getStock(2,0) + item.getLockCount(2,0) + item.getStock(3,0) + item.getLockCount(3,0) + item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,0) + item.getLockCount(area,0))*item.getPrice5();
			}
		}
		%>
	    	<%if(area==0||area==-1){%>  
	        	<td align='center'><%= item.getStock(0,0) + item.getLockCount(0,0) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,0) + item.getLockCount(0,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==1||area==-1){%>
	        	<td align='center'><%= item.getStock(1,0) + item.getLockCount(1,0) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(1,0) + item.getLockCount(1,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==2||area==-1){%>       
	        	<td align='center'><%= item.getStock(2,0) + item.getLockCount(2,0) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(2,0) + item.getLockCount(2,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==3||area==-1){%>
	        	<td align='center'><%= item.getStock(3,0) + item.getLockCount(3,0) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(3,0) + item.getLockCount(3,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
        	<%if(area==4||area==-1){%>
	        	<td align='center'><%= item.getStock(4,0) + item.getLockCount(4,0) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <td align='center'><%= item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0) %></td>	        	
	        <td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	         
        <%if(type==6){//样品库%>
		<%
		if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,6) + item.getLockCount(area,6))*item.getPrice5();
			}
		}
		%>
	  		<%if(area==0||area==-1){%>
	        	<td align="center"><%= item.getStock(0,6) + item.getLockCount(0,6) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(0,6) + item.getLockCount(0,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()   %><%} %></td><%}%>
	        <%if(area==1||area==-1){%>
	        	<td align='center'><%= item.getStock(1,6) + item.getLockCount(1,6)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal( (item.getStock(1,6) + item.getLockCount(1,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==3||area==-1){%>
	        	<td align='center'><%= item.getStock(3,6) + item.getLockCount(3,6)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal( (item.getStock(3,6) + item.getLockCount(3,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==4||area==-1){%>
	        	<td align='center'><%= item.getStock(4,6) + item.getLockCount(4,6)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal( (item.getStock(4,6) + item.getLockCount(4,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        	
	        <td align='center'><%= item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6)+ item.getStock(4,6) + item.getLockCount(4,6)%></td>	        		        	
	        <td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6)+ item.getStock(4,6) + item.getLockCount(4,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>	
        <%if(type==4){//退货库%>        		
        <%
        if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4) + item.getStock(2,4) + item.getLockCount(2,4) + item.getStock(3,4) + item.getLockCount(3,4) + item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,4) + item.getLockCount(area,4))*item.getPrice5();
			}
        }
		%>
	    	<%if(area==0||area==-1){%>  
	        	<td align='center'><%= item.getStock(0,4) + item.getLockCount(0,4) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,4) + item.getLockCount(0,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==1||area==-1){%>
	        	<td align='center'><%= item.getStock(1,4) + item.getLockCount(1,4) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(1,4) + item.getLockCount(1,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==2||area==-1){%>       
	        	<td align='center'><%= item.getStock(2,4) + item.getLockCount(2,4) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(2,4) + item.getLockCount(2,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==3||area==-1){%>       
	        	<td align='center'><%= item.getStock(3,4) + item.getLockCount(3,4) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(3,4) + item.getLockCount(3,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==4||area==-1){%>       
	        	<td align='center'><%= item.getStock(4,4) + item.getLockCount(4,4) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <td align='center'><%= item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4) %></td>        	
	        <td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>	
		<%if(type==3){//返厂库%>				
		<%
		if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3) + item.getStock(3,3) + item.getLockCount(3,3) + item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,3) + item.getLockCount(area,3))*item.getPrice5();
			}
		}
		%>
	  		<%if(area==0||area==-1){%>
	        	<td align="center"><%= item.getStock(0,3) + item.getLockCount(0,3) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,3) + item.getLockCount(0,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==1||area==-1){%>
	        	<td align='center'><%= item.getStock(1,3) + item.getLockCount(1,3)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(1,3) + item.getLockCount(1,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==3||area==-1){%>
	        	<td align='center'><%= item.getStock(3,3) + item.getLockCount(3,3)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(3,3) + item.getLockCount(3,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==4||area==-1){%>
	        	<td align='center'><%= item.getStock(4,3) + item.getLockCount(4,3)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>	
	        <td align='center'><%= item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3) %></td>
	        <td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3)+ item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>       		
		<%if(type==2){//维修库%>				
		<%
		if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,2) + item.getLockCount(area,2))*item.getPrice5();
			}
		}
		%>
	  		<%if(area==0||area==-1){%>
	        	<td align="center"><%= item.getStock(0,2) + item.getLockCount(0,2) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,2) + item.getLockCount(0,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==1||area==-1){%>
	        	<td align='center'><%= item.getStock(1,2) + item.getLockCount(1,2)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(1,2) + item.getLockCount(1,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        	
	        <%if(area==3||area==-1){%>
	        	<td align='center'><%= item.getStock(3,2) + item.getLockCount(3,2)%></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(3,2) + item.getLockCount(3,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        	
	        <td align='center'><%= item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2) %></td>
	        <td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%> 
        <%if(type==5){//残次品库%>        		
        <%
        if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5) + item.getStock(2,5) + item.getLockCount(2,5) + item.getStock(3,5) + item.getLockCount(3,5) + item.getStock(4,5) + item.getLockCount(4,5))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,5) + item.getLockCount(area,5))*item.getPrice5();
			}
        }
		%>
	    	<%if(area==0||area==-1){%>  
	        	<td align='center'><%= item.getStock(0,5) + item.getLockCount(0,5) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(0,5) + item.getLockCount(0,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==1||area==-1){%>
	        	<td align='center'><%= item.getStock(1,5) + item.getLockCount(1,5) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(1,5) + item.getLockCount(1,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        <%if(area==2||area==-1){%>       
	        	<td align='center'><%= item.getStock(2,5) + item.getLockCount(2,5) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(2,5) + item.getLockCount(2,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==3||area==-1){%>       
	        	<td align='center'><%= item.getStock(3,5) + item.getLockCount(3,5) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(3,5) + item.getLockCount(3,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        <%if(area==4||area==-1){%>       
	        	<td align='center'><%= item.getStock(4,5) + item.getLockCount(4,5) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(4,5) + item.getLockCount(4,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()  %><%} %></td><%}%>
	        	
	        <td align='center'><%= item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5)+item.getStock(4,5) + item.getLockCount(4,5) %></td>
	        <td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5)+item.getStock(4,5) + item.getLockCount(4,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>		
	    <%if(type==9){//售后库%>				
		<%
		if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
			if(area == -1){
				total=total+(item.getStock(1,9) + item.getLockCount(1,9))*item.getPrice5();
			}else{
				total=total+(item.getStock(area,9) + item.getLockCount(area,9))*item.getPrice5();
			}
		}
		%>
	  		<%if(area==1||area==-1){%>
	        	<td align="center"><%= item.getStock(1,9) + item.getLockCount(1,9) %></td>
	        	<td align="center"><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%= new  BigDecimal((item.getStock(1,9) + item.getLockCount(1,9))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        	<td align='center'><%= item.getStock(1,9) + item.getLockCount(1,9) %></td>
	        	<td align='center'><%if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {%><%=new  BigDecimal((item.getStock(1,9) + item.getLockCount(1,9))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %><%} %></td><%}%>
	        		
		<td align="right"><%=item.getStatusName() %></td>
		<td align='center' width="50"><%=(item.getParentId1()==0  ? "无" : item.getParent1().getName())%></td>
		<td align='center' width="50"><%=(item.getParentId2()==0  ? "无" : item.getParent2().getName())%></td>
		<td align='center' width="50"><%=(item.getParentId3()==0  ? "无" : item.getParent3().getName())%></td>
	</tr>
	<%c++;} %>
</logic:iterate> 
</logic:present> 
共有:<%=c %> 种产品
<%if(group.isFlag(95)) {%>
总金额: <%=new  BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() %>  <%} %>  
<%} %>
</tbody>
          </table>       