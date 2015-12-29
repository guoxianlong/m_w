<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.bean.*,java.lang.*" %>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="cache.*"%>
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
	
%>
<html>
<title>小店后台管理 - 成交订单产品查询</title>
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
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>
<script language="JavaScript" src="js/JS_functions.js"></script>
<body>
<%@include file="../header.jsp"%>
<script>
function exportList(){
	clipboardData.setData('text',getTblText(listTable));
	alert("列表内容已复制到剪贴板，粘贴到excel文件中即可。");
}
function checkSubmit(){
	var startTime = document.sForm.startDate.value;
	var endTime = document.sForm.endDate.value;
	if(!validateSubDate(endTime,startTime)){
		return false;
	}
	return true;
}

$(function(){
	var value=$('#hejiValue').html();
	var values = value.split(',');
	for(var i=0;i<values.length;i++){
		$('#moneryS'+(i+1)).html(values[i]);
	}	
});
</script>
<p>
<pre>
<b><font size=5>成交订单产品查询</font></b>
  规则说明:
1、某一时间段内生成且已成交的订单中各产品成交量的查询统计
2、时间:按订单生成日期计算
3、成交量=待发货（已到款）+待查款+已发货+已结算+已妥投+待退回
4、发货量=已发货+已妥投+已退回+待退回+已结算
5、成交金额=产品订购时销售价（忽略各种优惠）× 订购量
</pre>
<p></p>
<table  cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table  cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form name="sForm" method=post action="searchCjOrder.do" onSubmit="return checkSubmit();">
<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String oriname = (String)request.getParameter("oriname");
if(oriname == null)oriname="";
String name = (String)request.getParameter("name");
if(name == null)name="";
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate=DateUtil.getFirstDayOfMonth();
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate=DateUtil.getNow().substring(0, 10);
String parentId1 = StringUtil.convertNull(request.getParameter("parentId1"));
HashMap catalogMap = (HashMap)request.getAttribute("catalogMap");
HashMap discountPrice = (HashMap)request.getAttribute("discountPrice");
String[] parentId1s=(String[])request.getAttribute("parentId1s");
int areaGroup = StringUtil.StringToId(request.getParameter("areaGroup"));
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
String ss="";
if(parentId1s!=null){
	StringBuffer sb=new StringBuffer();
	for(int s=0;s<parentId1s.length;s++){
	sb.append(parentId1s[s]+",");
	}
	ss=sb.toString();
}

String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
if(!catalogIds2.equals("")){
	String[] splits = catalogIds2.split(",");
	for(int i=0;i<splits.length;i++){
		voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
		if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
			catalogIds1 = catalog.getId() + "," + catalogIds1;
		}
	}
	if(catalogIds1.endsWith(",")){
		catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
	}
}
int typeView = StringUtil.parstInt(request.getParameter("typeView"));
if(typeView==0)typeView=1;
HashMap map = (HashMap)request.getAttribute("map");
%>
产品编号：<input type=text name="code" size="10" value="<%=code%>">
原名称：<input type=text name="oriname" size="20" value="<%=oriname%>">（模糊）
小店名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br/><br/>
产品状态：<select name="status">
	<option value="0">全部</option>
<option value="2" <%if("2".equals(request.getParameter("status"))){%>selected<%}%>>在架上</option><option value="1" <%if("1".equals(request.getParameter("status"))){%>selected<%}%>>已下架</option></select>
时间：<input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');"><br/><br/>
产品分类：<select name="parentId1"  multiple="15" size="15" style="width:180">
	<option value="0">全部</option>
			<%
				HashMap map1 = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map1.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					voCatalog catalog = (voCatalog)iter.next();
					if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
						continue;
					}
			 %>
			 <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
			<%} %>
</select>(按住Ctrl多选)<br/>
<%if(parentId1!=null&&!parentId1.equals("")){ %>
<script> //保存上次多选状态
	selectOptionsp(sForm.parentId1, '<%=ss %>');
	function selectOptionsp(select,value){
	var ps=value.split(","); 
		for(var i = 0; i < select.length; i++){
			for(var t = 0; t < ps.length; t++){
				if(select.options[i].value == ps[t]){
					select.options[i].selected = true;
				}					
			}
		}
	return 0;
}
</script>
<%} %>
<br/>
<br>
    <label><input type="radio" name="areaGroup" value="0" <%=areaGroup==0?"checked":"" %>>全部</label>
    <label><input type="radio" name="areaGroup" value="1" <%=areaGroup==1?"checked":"" %>>非无锡单</label>
    <label><input type="radio" name="areaGroup" value="2" <%=areaGroup==2?"checked":"" %>>无锡单</label><br>
<br/>
产品线:
 	<%List productLineList = (List)request.getAttribute("productLineList") ;
 	%>
 	 <select name="productLine" id="productLine">
 		<option value="">请选择</option>	
 		<%if(productLineList!=null && productLineList.size()>0){
 			for(int i=0;i<productLineList.size();i++){
				voProductLine voProudct = (voProductLine)productLineList.get(i);
 				%>
 		<option value="<%=voProudct.getId()%>" <%=productLine==voProudct.getId()?"selected='selected'":"" %>><%=voProudct.getName()%></option>		
 				<%
 			}
 		 }%>
 	 </select>&nbsp;&nbsp;
 	 查看方式&nbsp;:<input type="radio" value="2" name="typeView" <%=typeView==2?"checked='checked'":""%>>按主商品汇总
 	        &nbsp;:<input type="radio" value="1" name="typeView"<%=typeView==1?"checked='checked'":""%>>按子商品汇总
 	 <br/>
<input type=submit value="查询产品成交量">
</form>
</td></tr>
</table>
</td></tr></table>
          <br />
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
          <br><form method=post action="" name="productForm">
          <table width="99%" cellpadding="3" cellspacing="1" id="listTable">
              <tr bgcolor="#4688D6">
              <td align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">一级分类</font></td>
              <td align="center"><font color="#FFFFFF">二级分类</font></td>
              <td align="center"><font color="#FFFFFF">三级分类</font></td>
              <td align="center"><font color="#FFFFFF">品牌</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
              <td align="center"><font color="#FFFFFF">供应商</font></td>
              <td align="center"><font color="#FFFFFF">价格</font></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td align="center"><font color="#FFFFFF">批发价</font></td>
<%}%>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td align="center"><font color="#FFFFFF">库存价格</font></td>
<%}%>
              <td align="center"><font color="#FFFFFF">成交量</font></td>
              <td align="center"><font color="#FFFFFF">成交金额</font></td>
              <td align="center"><font color="#FFFFFF">发货量</font></td>
              <td align="center"><font color="#FFFFFF">发货金额</font></td>
              <td align="center"><font color="#FFFFFF">合格库可发货量</font></td>
              <td align="center"><font color="#FFFFFF">待验库库存量</font></td>
			  <td align="center"><font color="#FFFFFF">时间段</font></td>    
            </tr>
<%int row=0;//行号 
 int dealSum=0,shippingSum=0,stockCount=0,stockCount1=0;
 double dealMonery=0,shippingMonery=0;
%>
<logic:present name="productList" scope="request">
	<tr>
		<td>合计</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><%if(group.isFlag(41)){%><td></td><td></td><%}%>
		<td align='center' id="moneryS1">4</td><td align='center' id="moneryS2">3</td><td align='center' id="moneryS3">2</td><td align='center' id="moneryS4">1</td><td align='center' id="moneryS5"></td><td align='center' id="moneryS6"></td><td></td>
	</tr>
<logic:iterate name="productList" id="item" > 
<%
	adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;
	dealSum+=voItem.getBuyCount();shippingSum+=voItem.getFhuoCount();
	dealMonery+=voItem.getPrice4();shippingMonery+=voItem.getDealMoney();
	stockCount+=voItem.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED);
	stockCount1+=voItem.getStockAllType(ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCountAllType(ProductStockBean.STOCKTYPE_CHECK);
	//float discountP = ((Float)discountPrice.get(new Integer(voItem.getId()))).floatValue(); 
	voCatalog p1 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId1()));
	voCatalog p2 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId2()));
	voCatalog p3 = (voCatalog)catalogMap.get(Integer.valueOf(voItem.getParentId3()));
	if(!(voItem.getBuyCount() == 0 && voItem.getBjBuyCount() == 0 && voItem.getGdBuyCount() == 0)){
%>
		<tr <%if(row%2==0){%> bgcolor="#EEE9D9"<%}%>>		
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><%= (p1!=null)?StringUtil.toWml(p1.getName()):"" %></td>
		<td align='center'><%= (p2!=null)?StringUtil.toWml(p2.getName()):"" %></td>
		<td align='center'><%= (p3!=null)?StringUtil.toWml(p3.getName()):"" %></td>
		<td align='center'><bean:write name="item" property="brandName" /></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" filter="true" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" filter="true" /></a></td>
		<td align='center'><bean:write name="item" property="supplierName" /></td>
		<td align='center'><bean:write name="item" property="price" format="#.00" /></td>
<%if(group.isFlag(41)){%>
		<td align='center'><bean:write name="item" property="price3" format="#.00" /></td>
<%}%>
<%if(group.isFlag(41)){%>
		<td align='center'><%if(voItem.getPrice5()>0){%><bean:write name="item" property="price5" format="#.00" /><%}else{ %>0.00<%} %></td>
<%}%>
		<td align='center'><bean:write name="item" property="buyCount" /></td>
		<td align='center'><%=NumberUtil.priceOrder(voItem.getPrice4())%></td>
		<td align='center'><bean:write name="item" property="fhuoCount" /></td>
		<td align='center'><%=NumberUtil.priceOrder(voItem.getDealMoney()) %></td>
		<td align='center'><%= voItem.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
		<td align='center'><%= voItem.getStockAllType(ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCountAllType(ProductStockBean.STOCKTYPE_CHECK) %></td>
		<td align='center'><%=startDate%>至<%=endDate%></td>
		</tr>
<%row++;} %>
</logic:iterate>
</logic:present>
          </table>
          <br>
          <div style="display:none" id="hejiValue"><%=dealSum%>,<%=NumberUtil.priceOrder(dealMonery)%>,<%=shippingSum%>,<%=NumberUtil.priceOrder(shippingMonery)%>,<%=stockCount%>,<%=stockCount1%></div>
<input type="button" onclick="javascript:exportList();" value="导出列表"/>
</body>
</html>