
<%@page import="adultadmin.bean.stat.NoDpproductStatBean"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%><%@ include file="../../taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ page isELIgnored="false" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="java.util.*,adultadmin.action.stat.SearecNoDpproductAction" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="cache.CatalogCache"%>
<%@ page import="adultadmin.action.vo.*"%>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
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
	
	try{
%>
<%

	SearecNoDpproductAction snda =new SearecNoDpproductAction();
	snda.searchNoDpproductStat(request,response);
	
	List proxyList = (List) request.getAttribute("proxyList");
	voSelect proxy = null;	
	
	int proxyId = StringUtil.toInt(request.getParameter("proxy"));
	int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
	int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
	String lastOutDaysAgo = request.getParameter("lastOutDaysAgo");
	String lastDaysAgo = request.getParameter("lastDaysAgo");
	String stockDateLong = request.getParameter("stockDateLong");
	int stockType = StringUtil.toInt(request.getParameter("stockType"));
%>
<html>
  <head>
    <title>滞销统计</title> 
<%
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert('<%=tip%>');
history.back(-1);
</script>
<%
	return;
}
else if("success".equals(result)){
%>
<script>
self.close();
window.opener.location.reload();
</script>
<%
	return;
}
%>
    
  </head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="../js/pts.js"></script>
<script language="JavaScript" src="../js/pub.js"></script>
<SCRIPT src="../js/sorttable.js" type="text/javascript"></SCRIPT>  
<script>
//指定页面区域内容导入Excel 注意:。
function AllAreaExcel() 
{
try{ 
  var oXL = new ActiveXObject("Excel.Application"); 
  var oWB = oXL.Workbooks.Add(); 
  var oSheet = oWB.ActiveSheet;   
   }catch(e){ 
    alert('导出失败!可能您没有安装Excel软件,或者没有将浏览器工具->安全->Internet自定义级别中“对没有标记为可安全执行脚本的Activex控件执行脚本”选择启用'); 
	return false; 
  } 
  var sel=document.body.createTextRange(); 
  sel.moveToElementText(listTable); 
  sel.select(); 
  sel.execCommand("Copy"); 
  oSheet.Paste(); 
  oXL.Visible = true; }
  
function setValue(totalCount,total){
	document.getElementById('totalCount').innerText = "共有:"+totalCount+"种产品";
	document.getElementById('total').innerText = "总金额:"+total;
}
</script>
<body>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="sForm" method=post action="nodpproduct.jsp">&nbsp; 
 <input type="hidden" name="search" value="true"/>
一级分类：<select  name="parentId1" class="bd" style="width:140" onChange="sredirect(this.options.selectedIndex - 1);">
	<option value="0">全部</option>
			<%
				HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					voCatalog catalog = (voCatalog)iter.next();
			 %>
			 <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
			<%} %>
			<option value="145">内衣</option>
			<option value="151">护肤品</option>
			<option value="163">新奇特商品</option>
		</select>
    二级分类：<select name="parentId2" class="bd" style="width:140">
	<option value="0">全部</option>
</select>
<script>
var spt = document.forms[0].parentId2
function sredirect(x){
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null;
  if(x < 0){
	return;
  }
  spt.options[0]=new Option("全部", "0");
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i + 1]=new Option(spts[x][i].text, spts[x][i].value)
  }
  spt.options[0].selected=true
}

selectOption(document.forms[0].parentId1, '<%= parentId1 %>');
sredirect(document.forms[0].parentId1.selectedIndex - 1);
selectOption(document.forms[0].parentId2, '<%= parentId2 %>');
</script>
代理商：<select name="proxy" style="width:130" >
		    <option value="0">所有</option>
		<%
	iter = proxyList.listIterator();
	while(iter.hasNext()){
		proxy = (voSelect)iter.next();
%>
  		<option value="<%= proxy.getId() %>"><%= proxy.getName() %></option>
<%} %>
		</select>
		<%if(proxyId>0){%>
			<script>selectOption(document.getElementById('proxy'), '<%=proxyId%>');</script>
		<%}%>
状态：<select name="status">
		<option value="0">全部</option>
		<option value="5" <%if("5".equals(request.getParameter("status"))){%>selected<%}%>>促销</option>
		<option value="10" <%if("10".equals(request.getParameter("status"))){%>selected<%}%>>热销</option>
		<option value="50" <%if("50".equals(request.getParameter("status"))){%>selected<%}%>>普通</option>
		<option value="100" <%if("100".equals(request.getParameter("status"))){%>selected<%}%>>下架</option>
		<option value="120" <%if("120".equals(request.getParameter("status"))){%>selected<%}%>>隐藏</option>
	</select>	
		<br/><br/>
距上次出货时长：<input type=text name="lastOutDaysAgo" size="5" <%if(lastOutDaysAgo!=null){ %>value="<%=lastOutDaysAgo %>"<%} %>> 天以上&nbsp;&nbsp;
近期<input type=text name="lastDaysAgo" size="5" <%if(lastDaysAgo!=null){ %>value="<%=lastDaysAgo %>"<%} %>> 天成交订单销量&nbsp;&nbsp;
库存类型：<select name="stockType">
			<option value="-1">全部</option>
			<option value="0" <%if(stockType == 0){%>selected<%}%>>合格库</option>
			<option value="1" <%if(stockType == 1){%>selected<%}%>>待验库</option>
			<option value="2" <%if(stockType == 2){%>selected<%}%>>维修库</option>
			<option value="3" <%if(stockType == 3){%>selected<%}%>>返厂库</option>
			<option value="4" <%if(stockType == 4){%>selected<%}%>>退货库</option>
		</select>
库存天数大于：<input type=text name="stockDateLong" size="5" <%if(stockDateLong!=null){ %>value="<%=stockDateLong %>"<%} %>> 天以上&nbsp;&nbsp;
<br/><br/>
<input type=submit value="查询">
</form>
</td></tr>
</table>
</td></tr></table><br/>

<table align="center" width="90%" border="0" style="border-collapse:collapse;">
<tr>
<td align="left">
库存天数Y = 当前库存*n天/n天发货量，其中‘当前库存’指的是查询条件中所选择的‘库存类型’，‘n’是在‘近期__天成交订单销量’里设置的天数<br/>
近期销量 = 近期发货量 - 近期退回量
<br/><br/>
</td>
</tr>
<tr>
<td align="left">
<div id="totalCount"></div>
<%if(group.isFlag(127)){ %>
<div id="total"></div>
<%} %>
</td>
</tr>
</table>
<!-- 
<input type="button" onclick="AllAreaExcel()"   value="导出列表"/> -->
<table align="center" width="90%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5" id="listTable" class="sortable">
<thead class="sorthead">
    <tr bgcolor="#4688D6">
    	 <td align="center"  width="3%"><font color="#FFFFFF">序</font></td>   
	     <td align="center"  width="7%"><font color="#FFFFFF">编号</font></td>
	     <td align="center"  width="8%"><font color="#FFFFFF">一级分类</font></td>
	     <td align="center"  width="9%"><font color="#FFFFFF">二级分类</font></td>
	     <td align="center"  width="15%"><font color="#FFFFFF">原名称</font></td>
	     <td align="center"  width="8%"><font color="#FFFFFF">最近一次入库时间</font></td>
	     <%--<td align="center"  width="4%"><font color="#FFFFFF">历史总采购量</font></td>          
	     <td align="center"  width="4%"><font color="#FFFFFF">历史总销量</font></td> --%>
	     <td align="center"  width="4%"><font color="#FFFFFF"><%if(stockType == -1){ %>总<%}else{ %><%=ProductStockBean.getStockTypeName(stockType) %><%} %>库存数量</font></td>
	     <td align="center"  width="6%"><font color="#FFFFFF"><%if(stockType == -1){ %>总<%}else{ %><%=ProductStockBean.getStockTypeName(stockType) %><%} %>库存金额</font></td>
	     <td align="center"  width="3%"><font color="#FFFFFF">近期销量</font></td>
	     <td align="center"  width="4%"><font color="#FFFFFF">最近一次出货时长</font></td>
	     <td align="center"  width="3%"><font color="#FFFFFF">库存天数</font></td>
	     <td align="center"  width="5%"><font color="#FFFFFF">近期发货量</font></td>
	     <td align="center"  width="5%"><font color="#FFFFFF">近期退回量</font></td>
    </tr>
</thead>
	<%  int row=0;//行号 
		float total = 0;
		int totalCount = 0;
		List dpproductList = (List)request.getAttribute("dpproductList");
		if(dpproductList!=null){
		Iterator iterDpp = dpproductList.listIterator();
		while(iterDpp.hasNext()){
		NoDpproductStatBean dpproduct = (NoDpproductStatBean)iterDpp.next();
		voProduct product = dpproduct.getProduct();
		String soonOutCount = "-";
		String dayOfInStock = "9999";
		String soonSellCount = "-";
		String soonReturnCount = "-";
		if(!lastDaysAgo.equals("")){
			soonOutCount = String.valueOf(dpproduct.getSoonOutCount());
			soonReturnCount = String.valueOf(dpproduct.getSoonReturnCount());
			soonSellCount = String.valueOf(dpproduct.getSoonOutCount()-dpproduct.getSoonReturnCount());
			if(dpproduct.getSoonOutCount()>0){
				if(stockType == -1){
					dayOfInStock = String.valueOf(product.getStockAll()*Integer.parseInt(lastDaysAgo)/dpproduct.getSoonOutCount());
				}else{
					dayOfInStock = String.valueOf(product.getStockAllType(stockType)*Integer.parseInt(lastDaysAgo)/dpproduct.getSoonOutCount());
				}
			}
		}
		if(!stockDateLong.equals("")){
			if(Integer.parseInt(dayOfInStock)<=Integer.parseInt(stockDateLong)){
				continue;
			}
		}
		total = total + dpproduct.getStockAmount();
		totalCount++;
	 %> 
  	<tr <%if(row%2==0){%> bgcolor="#EEE9D9"<%}%>>
  		 <td align="center"  width="3%"><%=row+1 %></td>
		 <td align="center"  width="7%"><a href="<%=request.getContextPath()%>/admin/fproduct.do?id=<%=dpproduct.getProductId()%>" ><%=dpproduct.getCode()%></a> </td>
		 <td align="center"  width="8%"><%=dpproduct.getLevelOneName()%></td>
		 <td align="center"  width="9%"><%=dpproduct.getLevelTwoName()==null?"-":dpproduct.getLevelTwoName()%></td>
	     <td align="center"  width="15%"><a href="<%=request.getContextPath()%>/admin/fproduct.do?id=<%=dpproduct.getProductId()%>" ><%=dpproduct.getOriName()%></a></td>
	     <td align="center"  width="8%"><%=dpproduct.getLastInTime()==null?"-":dpproduct.getLastInTime().substring(0,10) %></td>
	     <%--<td align="center"  width="4%"><%=dpproduct.getHistoryInCount()%></td>
	     <td align="center"  width="4%"><%=dpproduct.getHistoryOutCount()%></td> --%>
	     <td align="center"  width="4%">
	     	<%
	     		int totalStock = 0;
	     		if(stockType == -1){
	     			totalStock = product.getStockAll(); 
	     	 %>
	     	<%=totalStock %>
	     	<%
	     		}else{
	     			totalStock = product.getStockAllType(stockType);
	     	 %>
	     	<%=totalStock %>
	     	<%
	     		}
	     		total = total + totalStock*product.getPrice5();
	     	 %>
	     </td>
	     <td align="center"  width="6%"><%=df.format(totalStock*product.getPrice5())%></td>
	     <td align="center"  width="3%"><%=StringUtil.StringToId(soonSellCount)<0?0:soonSellCount%></td>
	     <td align="center"  width="5%"><%=dpproduct.getDaysOfOut()>=0?dpproduct.getDaysOfOut():"-"%></td>
	     <td align="center"  width="3%"><%=dayOfInStock%></td>
	     <td align="center"  width="3%"><%=soonOutCount%></td>
	     <td align="center"  width="3%"><%=soonReturnCount%></td>
	</tr>
<% 
	row++;
	}
	}
 %>	
 <script>setValue(<%=totalCount%>,<%=df.format(total) %>);</script>
</table>
<script>
function tableRowStyle(){
	for(var i=1;i<=document.getElementById('listTable').rows.length;i++){
		if(i%2==0){
			document.getElementById('listTable').rows[i].style.background ='#FFFFFF';
		}else{
		 document.getElementById('listTable').rows[i].style.background ='#EEE9D9';
		}
	}
}
</script>
</body>
</html>
<%
} catch(Exception e){
	e.printStackTrace();
} finally {
}
%>
