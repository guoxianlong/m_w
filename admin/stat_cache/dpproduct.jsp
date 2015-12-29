<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ page isELIgnored="false" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,adultadmin.framework.*,java.math.*" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*,java.net.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="cache.CatalogCache"%>
<%@ page import="adultadmin.action.vo.voCatalog"%>
<%@ page import="java.util.*,adultadmin.action.stat.SearecDpproductAction" %>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

	voUser adminUser = (voUser)session.getAttribute("userView");

	//数据库大查询锁，等待3秒
	if (!DbLock.slaveServerQueryLocked(100)) {
		response.sendRedirect(request.getContextPath()+"/tip.jsp");
		return;
	}
	Connection conn = null;
try{
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

	DbLock.slaveServerOperator = adminUser.getUsername() + "_动碰商品统计_" + DateUtil.getNow();
	conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);

	Statement st = conn.createStatement();
	ResultSet rs = null;
	SearecDpproductAction sda =new SearecDpproductAction();
	sda.SearecDpproductStat(request,response);
	int proxy = StringUtil.toInt(request.getParameter("proxy"));
	int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
    int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
	
%>
<html>
  <head>
    <title>动碰商品统计</title> 
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
}%>
    
  </head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="../js/pts.js"></script>
<script language="JavaScript" src="../js/pub.js"></script>
<SCRIPT src="../js/sorttable.js" type="text/javascript"></SCRIPT>  

<script   language="JavaScript">   
  function   getTblText(objTbl)   
  {   
  if(!objTbl)   return   "";   
  if(objTbl.tagName   !=   "TABLE")   return   "";   
  var   returnText   =   "";   
  for(var   r=0;   r<objTbl.rows.length;   r++)   
  {   
  for(var   c=0;   c<objTbl.rows[r].cells.length;   c++)   
  {   
  returnText   +=   objTbl.rows[r].cells[c].innerText   +   "\t";   
  }   
  returnText   +=   "\n";   
  }   
  return   returnText;   
  }   
</script>
<script>
function exportList(){
	clipboardData.setData('text',getTblText(listTable));
	alert("列表内容已复制到剪贴板，粘贴到excel文件中即可。");
}
</script>

<body>
<osCache:cache scope="application" time="900">
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="sForm" method=post action="dpproduct.jsp">

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
<script type="text/javascript"></script>
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
		<%rs = st.executeQuery("select id, name from supplier_standard_info where status=1 order by id");
		  while(rs.next()){
		%><option value="<%=rs.getInt(1)%>" <%if(rs.getInt(1) == proxy){%>selected<%}%>><%=rs.getString(2)%></option>
		<%}
		    rs.close();%>
		</select><br/><br/>
时间范围：<input type=text name="startDate" size="20" <%if(startDate!=null) {%> value="<%=startDate %>"<%} %> onclick="SelectDate(this,'yyyy-MM-dd');">到<input type=text name="endDate" size="20" <%if(endDate!=null) {%> value="<%=endDate %>"<%} %> onclick="SelectDate(this,'yyyy-MM-dd');"><br/><br/>
<input type=submit value="查询">
</form>
</td></tr>
</table>
</td></tr></table><br/>

<%float total=0;//库存产品的总金额
int totalCount=0;//总数%>
<logic:present name="dpproductList" scope="request">
<logic:iterate name="dpproductList" id="item" type="adultadmin.bean.stat.DpproductStatBean" > 
<%if((item.getStockAll()+item.getLockCountAll())>0 ){ totalCount++;} %>	
<%total=total+(item.getStockAll() + item.getLockCountAll())*item.getPrice5();%>
</logic:iterate> 
</logic:present> 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
共有:<%=totalCount %>种产品 &nbsp;&nbsp;&nbsp;
<%if(group.isFlag(127)){ %>
总金额:<%=total %>
<%} %>&nbsp;&nbsp;<!-- 
<input type="button" onclick="javascript:exportList();" value="导出列表"/>  -->

<table align="center" width="90%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5" id="listTable" class="sortable">
<thead class="sorthead">
    <tr bgcolor="#4688D6">
	     <td align="center"  ><font color="#FFFFFF">编号</font></td>
	     <td align="center"  ><font color="#FFFFFF">名称</font></td>
	     <td align="center"  ><font color="#FFFFFF">采购入库量</font></td>
	     <td align="center"  ><font color="#FFFFFF">销量</font></td> 
	     <td align="center"  ><font color="#FFFFFF">销售退货量</font></td>
	     <td align="center"  ><font color="#FFFFFF">动碰次数</font></td>
	     <td align="center"  ><font color="#FFFFFF">待验库</font></td>
	     <td align="center"  ><font color="#FFFFFF">合格库</font></td>
	     <td align="center"  ><font color="#FFFFFF">退货库</font></td>
	     <td align="center"  ><font color="#FFFFFF">返厂库</font></td>
	     <td align="center"  ><font color="#FFFFFF">维修库</font></td>
	     <td align="center"  ><font color="#FFFFFF">残次品库</font></td>
	     <td align="center"  ><font color="#FFFFFF">样品库</font></td> 
	    <!--  <td align="center" ><font color="#FFFFFF">最近出货时间</font></td> -->          
    </tr>
</thead>
<%int row=0;//行号 %>
<logic:present name="dpproductList" scope="request">
<logic:iterate name="dpproductList" id="dpproduct" indexId="number" type="adultadmin.bean.stat.DpproductStatBean" > 
<%if((dpproduct.getStockAll()+dpproduct.getLockCountAll())>0 ){%>
  	<tr <%if(row%2==0){%> bgcolor="#EEE9D9"<%}%>>
		 <td align="center"><a href="<%=request.getContextPath()%>/admin/fproduct.do?id=<bean:write name="dpproduct" property="productId" />" ><%=dpproduct.getCode()%></a> </td>
	     <td align="center"><a href="<%=request.getContextPath()%>/admin/fproduct.do?id=<bean:write name="dpproduct" property="productId" />" ><%=dpproduct.getName()%></a></td>
	     <td align="center"><%=dpproduct.getStockinCount()%></td>
	     <td align="center"><%=dpproduct.getOutCount()%></td>
	     <td align="center"><%=dpproduct.getOutReturnCount()%></td>
	     <td align="center"><%=dpproduct.getFrequencyCount()%></td>
	     <td align="center"><%=dpproduct.getStock(0,1) + dpproduct.getLockCount(0,1) + dpproduct.getStock(1,1) + dpproduct.getLockCount(1,1)+ dpproduct.getStock(3,1) + dpproduct.getLockCount(3,1) %></td>
	     <td align="center"><%=dpproduct.getStock(0,0) + dpproduct.getLockCount(0,0) +dpproduct.getStock(1,0) + dpproduct.getLockCount(1,0)+dpproduct.getStock(2,0) + dpproduct.getLockCount(2,0)+dpproduct.getStock(3,0) + dpproduct.getLockCount(3,0) %></td>
	     <td align="center"><%=dpproduct.getStock(0,4) + dpproduct.getLockCount(0,4) + dpproduct.getStock(1,4) + dpproduct.getLockCount(1,4)+dpproduct.getStock(2,4) + dpproduct.getLockCount(2,4)+dpproduct.getStock(3,4) + dpproduct.getLockCount(3,4) %></td>
	     <td align="center"><%=dpproduct.getStock(0,3) + dpproduct.getLockCount(0,3) + dpproduct.getStock(1,3) + dpproduct.getLockCount(1,3)+ dpproduct.getStock(3,3) + dpproduct.getLockCount(3,3) %></td>
	     <td align="center"><%=dpproduct.getStock(0,2) + dpproduct.getLockCount(0,2) + dpproduct.getStock(1,2) + dpproduct.getLockCount(1,2)+ dpproduct.getStock(3,2) + dpproduct.getLockCount(3,2) %></td>
	     <td align="center"><%=dpproduct.getStock(0,5) + dpproduct.getLockCount(0,5) + dpproduct.getStock(1,5) + dpproduct.getLockCount(1,5)+dpproduct.getStock(2,5) + dpproduct.getLockCount(2,5)+dpproduct.getStock(3,5) + dpproduct.getLockCount(3,5) %></td>
	     <td align="center"><%=dpproduct.getStock(0,6) + dpproduct.getLockCount(0,6) + dpproduct.getStock(1,6) + dpproduct.getLockCount(1,6)+ dpproduct.getStock(3,6) + dpproduct.getLockCount(3,6)%></td>
	</tr>
<% row++;} %>	
</logic:iterate> 
</logic:present>
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
</osCache:cache>  
</body>
</html>

<%
	st.close();
} catch(Exception e){
	e.printStackTrace();
} finally {
	DbLock.slaveServerQueryLock.unlock();

	if(conn != null)
		conn.close();
}
%>