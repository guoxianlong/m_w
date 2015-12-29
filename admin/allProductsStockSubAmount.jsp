<%@page import="adultadmin.action.vo.voSelect"%>
<%@page import="java.net.URLEncoder"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ page isELIgnored="false" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.*,adultadmin.framework.*,java.math.*" %>
<%@ page import="adultadmin.bean.stock.*,java.util.*,adultadmin.bean.buy.BuyStockinProductBean" %>
<%@ page import="adultadmin.util.*,java.net.*,adultadmin.bean.order.OrderStockProductBean" %>
<%@ page import="adultadmin.util.db.DbUtil" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@page import="cache.*"%>
<%@page import="adultadmin.action.vo.voCatalog"%>
<%@ taglib uri="http://www.maimaibao.com/core" prefix="mmb" %>
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

/*	Connection conn = DbUtil.getConnection("adult");
	Statement st = conn.createStatement();

	ResultSet rs = null;*/
	int type=-1,area=-1;
//	Hashtable ht=null,ht2=null;
	int proxy = StringUtil.toInt(request.getParameter("proxy"));
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	if(request.getAttribute("stockType")!=null){
		type=((Integer)request.getAttribute("stockType")).intValue();
		//System.out.println("type-->"+type);
	}
	request.setAttribute("stockType", type);
	if(request.getAttribute("stockArea")!=null){
		area=((Integer)request.getAttribute("stockArea")).intValue();
	}
	request.setAttribute("stockArea", area);
	//if(request.getAttribute("ht")!=null){
	//	ht=(Hashtable)request.getAttribute("ht");
	//}
	//if(request.getAttribute("ht2")!=null){
//		ht2=(Hashtable)request.getAttribute("ht2");
	//}
	
	
	int supplierId = 0;	// 供应商id
	if(request.getAttribute("proxy") != null){
		supplierId = StringUtil.StringToId(request.getAttribute("proxy").toString());	
	}
	String suppliertext = StringUtil.convertNull(request.getParameter("suppliertext"));	//供应商名称
	String supplierIds = cache.ProductLinePermissionCache.getProductLineSupplierIds(adminUser);	//供应商id串
	List supplierList = new ArrayList();
	if(request.getAttribute("supplierList") != null){
		supplierList = (List) request.getAttribute("supplierList");	//供应商信息
	}
%>
<html>
<title>买卖宝后台</title>
<head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pts.js"></script>
<script language="JavaScript" src="js/soft.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock2.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
<script type="text/javascript">
	function select(){
		//此函数什么也不做，因为调用的公用类supplierNames.js中调用了此函数
	}
	function autoWrite(textName,selectName){
		textName.value = selectName.options[selectName.selectedIndex].text;
	}
	$(function(){
		loadArea($("#type").val(),<%=area%>);
		$("#type").change(function(){
			$("#tarArea").val(-1);
			loadArea($("#type").val(),-1);
		});
	});
	//动态加载库地区
	function loadArea(stockTypeId,tag){
		$.ajax({
			url:'${pageContext.request.contextPath}/CargoController/querySelectStockAreaAccess.mmx?stockType='+stockTypeId,
			cache:false,
			dataType:'text',
			type:'post',
			success:function(dd){
				$("#tarArea").empty();
				var arr = eval('('+dd+')');
				for(var i=0;i<arr.length;i++){
					var opt = $("<option>").text(arr[i].text).val(arr[i].id);
					$("#tarArea").append(opt);
				}
				if(tag!=-1){
					//查询后库地区默认选中
					$("#tarArea").val(tag);
				}
			}
		});
	}
</script>
</head>
<body onload="sortTable('listTable', 2, 'int');">
<%@include file="../header.jsp"%>
<form method=post name="searchForm" action="allProductsStockSubAmount.do">
<table cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" ><tr><td>
<table cellpadding="3" cellspacing="1" bgcolor="#F8F8F8"align="left" >

<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String name = (String)request.getParameter("name");
name = Encoder.decrypt(name);//对提交参数进行中文解码
if(name==null){//解码失败,表示已经为中文,则返回默认
	name =(String)request.getParameter("name");//名称
}
if(name == null)name="";
int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));

%>
	<tr>
		<td>产品编号：<input type=text name="code" size="20" value="<%=code %>" /></td>
		<td>产品名称：<input type=text name="name" size="20" value="<%=name %>" />（模糊）</td>
		<td>状态：<select name="status">
			<option value="0">全部</option>
		<option value="5" <%if("5".equals(request.getParameter("status"))){%>selected<%}%>>促销</option>
		<option value="10" <%if("10".equals(request.getParameter("status"))){%>selected<%}%>>热销</option>
		<option value="50" <%if("50".equals(request.getParameter("status"))){%>selected<%}%>>普通</option>
		<option value="100" <%if("100".equals(request.getParameter("status"))){%>selected<%}%>>下架</option>
		<option value="120" <%if("120".equals(request.getParameter("status"))){%>selected<%}%>>隐藏</option>
		<option value="130" <%if("130".equals(request.getParameter("status"))){%>selected<%}%>>缺货</option>
		</select></td>
	</tr>
	<tr>
		<td>一级分类：<select  name="parentId1" class="bd" style="width:140" onChange="sredirect(this.options.selectedIndex - 1);">
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
				</select></td>
		<td>二级分类：<select name="parentId2" class="bd" style="width:140" onChange="tredirect(this.options.selectedIndex-1);">
			<option value="0">全部</option>
		</select></td>
		<td>三级分类：<select name="parentId3" class="bd" style="width:140">
			<option value="0">全部</option>
		</select></td>
	<%if(group.isFlag(94)){ %>
		<td>
		供应商:
		<div id="auto" style="position: absolute; left: 100px; top: 72px;"></div>
		<input type="text" name="suppliertext" id="word" style="width: 100px; font-size: 10pt; height: 20px;" value="<%=suppliertext %>"/>
		<input type="hidden" name="condition" id="condition" value="status = 1 and id in (<%=supplierIds %>)">
		<span style="width:18px;border:0px solid red; margin-left:-8px; margin-bottom:-6px;">
			<select name="proxy" id="supplierId" style="margin-left:-100px;width:118px;"
			onChange="autoWrite(document.searchForm.suppliertext, document.searchForm.proxy);" >
				<option value="0">所有</option>
			<%
				if (supplierList != null && supplierList.size() > 0) {
					for(int i = 0; i < supplierList.size(); i++) {
						voSelect ssInfoBean = (voSelect) supplierList.get(i);
						if(ssInfoBean != null){
			%>
			 			<option value="<%= ssInfoBean.getId() %>" ><%= ssInfoBean.getName() %></option>
			<%
						}
					} 
				}
			%>
			</select>
		</span>
		<script>selectOption(document.getElementById("supplierId"), "<%=supplierId %>");</script>
		</td>
	<%} %>
	</tr>
	<tr><td>
	库类型：<select name="stockType" id="type">
<%
	HashMap stockMap = ProductStockBean.stockTypeMap;
	Iterator stockKeyIter = stockMap.keySet().iterator();
	stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER)
			continue;
%>
			<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
<%} %>
		  </select>&nbsp;</td><td>
		   <script>selectOption2(document.getElementById('type'), '<%=type %>');//保存上次查询选中状态
		   function selectOption2(select,value){
			//alert(select.length+'---'+value);
			for(var i = 0; i < select.length; i++){
				if(select.options[i].value == value){
				//alert(select.options[i].text);
				select.options[i].selected = true;
				return i;
			}
		}
		return 0;
	}
 </script>
    库区域：<select name="stockArea" id="tarArea">
          </select></td>
	</tr>
<script>
var spt = document.forms[0].parentId2
var parentId1 = document.forms[0].parentId1
function sredirect(x){
	 if(x<=0){
		  document.forms[0].parentId2.options[0].selected=true;
		  document.forms[0].parentId3.length=1;
		  document.forms[0].parentId3.options[0].selected=true;
	  }
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
  tredirect(0);
}
var tpt = document.forms[0].parentId3
function tredirect(y){
  x = document.forms[0].parentId1.options.selectedIndex-1;
  for (m = tpt.options.length - 1; m >= 0; m --)
      tpt.options[m] = null
  tpt.options[0]=new Option("全部", "0");
  for (i =0; i < tpts[x][y].length; i ++){
      tpt.options[i+1]=new Option(tpts[x][y][i].text, tpts[x][y][i].value)
  }
  tpt.options[0].selected=true;
}
function export1(){
	document.searchForm.toExcel.value = 'to';
	document.searchForm.submit();
}
function export2(){
	document.searchForm.toExcel.value = 'go';
}

selectOption(document.forms[0].parentId1, '<%= parentId1 %>');
sredirect(document.forms[0].parentId1.selectedIndex - 1);
selectOption(document.forms[0].parentId2, '<%= parentId2 %>');
tredirect(document.forms[0].parentId2.selectedIndex-1);
selectOption(document.forms[0].parentId3, '<%= parentId3 %>');
</script>
	<tr></tr>
	<tr>
		<td><input type=submit value="查询" onClick="export2()">(默认查询为全部)</td>
<!-- 		<td><input type="button" value="查询全部" onclick="location.href='allProductsStockSubAmount.do?stockType=0'" /> </td> -->
		<td><input type="button" onclick="export1()"   value="导出"/></td>
	</tr>
	
</table>
</td></tr></table>
<input type="hidden" name="toExcel" value=""/>
</form>
<!-- ============================= 数据显示 ============================= -->
<%String ul= "allProductsStockSubAmount.do?toExcel=to&"+ request.getAttribute("date")+"&stockType="+type+"&stockArea="+area; %>

<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="listTable">
 <thead>
  	<tr bgcolor="#4688D6">              
		<td align="center" onclick="sortTable('listTable', 0, 'int')" style="cursor:pointer"><font color="#FFFFFF">产品编号</font></td>
		<td align="center"><font color="#FFFFFF">小店名称</font></td>
		<td align="center"><font color="#FFFFFF">产品原名称</font></td>
		
		<c:if test="${requestScope.header!=null }">
			<c:forEach var="stockArea" items="${requestScope.header }">
				<td align="center" ><font color="#FFFFFF">${stockArea.name }</font></td>
				<%if(group.isFlag(95)) {%> 
				<td align="center" ><font color="#FFFFFF">金额(${stockArea.name })</font></td>
				<%} %>
			</c:forEach>
		</c:if>
        
        <td align="center" ><font color="#FFFFFF">库存总数</font></td> 
        <%if(group.isFlag(95)) {%> 	
        <td align="center"  ><font color="#FFFFFF">总金额</font></td> <%} %>      
        <td align="center" ><font color="#FFFFFF">状态</font></td>
		<td align="center"><font color="#FFFFFF">一级分类</font></td>
		<td align="center" ><font color="#FFFFFF">二级分类</font></td>
		<td align="center" ><font color="#FFFFFF">三级分类</font></td>
		<td align="center" ><font color="#FFFFFF">库存记录</font></td>
  	</tr>
 </thead>
<tbody>
<c:if test="${count!=0}">
<%double total=0; int c=0;%>
	<c:forEach var="item" items="${requestScope.productList }">
		<tr bgcolor='#F8F8F8'>	
			<td align="center"><a href="fproduct.do?id=${item.id }" >${item.code }</a></td>
			<td align='center' width="150"><a href="fproduct.do?id=${item.id }" >${item.name }</a></td>
			<td align='center' width="150"><a href="fproduct.do?id=${item.id }" >${item.oriname }</a></td>
			
			<c:forEach var="item2" items="${item.stockCountAndPriceList }">
				<td align='center'>${item2 }</td>
			</c:forEach>
		
			<td align="center">${item.statusName }</td>
			<td align='center' width="50">
				<c:choose>
					<c:when test="${item.parentId1==0 }">无</c:when>
					<c:otherwise>${item.parent1.name}</c:otherwise>
				</c:choose>
			</td>
			<td align='center' width="50">
				<c:choose>
					<c:when test="${item.parentId2==0 }">无</c:when>
					<c:otherwise>${item.parent2.name}</c:otherwise>
				</c:choose>
			</td>
			<td align='center' width="50">
				<c:choose>
					<c:when test="${item.parentId3==0 }">无</c:when>
					<c:otherwise>${item.parent3.name}</c:otherwise>
				</c:choose>
			</td>
			<td align="center"><a href="productStock/stockCardList.jsp?productCode=${item.code}" target="_blank">查</a></td>
		</tr>
	</c:forEach>


共有:${count } 种产品
<c:if test="${count!=null&&count!='' }">
</c:if>
<mmb:permit value="95">
	总金额: ${totalPrice }
</mmb:permit>
</c:if>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</tbody>
          </table>       
<br/>
<script type="text/javascript">
sortTable('listTable', 2, 'int');
</script>
</body>
</html>
