<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@page import="adultadmin.action.vo.voOrder"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@ page import="cache.*"%>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*"%>
<%
PagingBean paging = (PagingBean) request.getAttribute("paging");
List orderList = (List) request.getAttribute("orderList");
String batchCode = (String)request.getAttribute("batchCode");
String batchId = (String)request.getAttribute("batchId");
int flag = StringUtil.parstInt(request.getParameter("flag"));
int temp = StringUtil.parstInt(request.getParameter("temp"));
int noAssignTypeOrderCount = StringUtil.parstInt(request.getAttribute("noAssignTypeOrderCount")+"");
int noAssignDeliverOrderCount = StringUtil.parstInt(request.getAttribute("noAssignDeliverOrderCount")+"");
int orderCount = StringUtil.parstInt(request.getAttribute("orderCount")+"");
Map deliverMap = (Map)request.getAttribute("deliverMap");
String SKUCount=(String)request.getAttribute("SKUCount");
String groupCount=(String)request.getAttribute("groupCount");
String batchCount=(String)request.getAttribute("batchCount");
String productCount=(String)request.getAttribute("productCount");
String parentId1 =request.getParameter("parentId1");
String parentId1s =request.getParameter("parentId1s");
String deliver =request.getParameter("deliver");
String delivers =request.getParameter("delivers");
String parentId2 =request.getParameter("parentId2");
String deliver1 =request.getParameter("deliver1");
String code =request.getParameter("code");
String tip=(String)request.getAttribute("tip");
String result = (String)request.getAttribute("result");
String failure = (String)request.getAttribute("failure");
String shengwaiCount = request.getParameter("shengwaiCount");
String shengneiCount = request.getParameter("shengneiCount");
HashMap productTypeMap =(HashMap)request.getAttribute("productTypeMap");
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
	if(tip!=null && tip.length()>0){
 		%><script>alert('<%= tip %>');</script><%
 	}
	voUser adminUser = (voUser)session.getAttribute("userView");
	String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
	String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
	int productLine = StringUtil.StringToId(request.getParameter("productLine"));
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
	 String towns = StringUtil.convertNull(request.getParameter("towns"));
%>
<html>
<head>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/admin/js/postCodeAutoSupplier.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/jquery.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script  language="JavaScript" src="<%=request.getContextPath() %>/admin/js/supplierProductLine.js" > </script>

<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<title>分拣批次管理</title>
<style type="text/css">
ul li {float:left; width:50px  }
body,td,th {
	color: #FFF;
}

.divStyle{
	border:1px solid black;border-radius:5px; float:left;width:120px;height:24px;text-align:center;padding-top:6px;margin-left:5px;
	cursor:hand;
}
.cools{
	background-color:#669966;
}
</style>
<script type="text/javascript">
	function change(show,hide){
		$('#'+show).show();
		$('#'+hide).hide();
		$(this).addClass('cools');
		if(show=='div1'){
			$('#dsDiv2').removeClass('cools');
		}else
			$('#dsDiv1').removeClass('cools');
	}
	function addGroup(id){
		window.location="<%=request.getContextPath()%>/admin/sortingAction.do?method=makeSortingBatchGroup3&batchId="+id+"&toPage=1";
	}
	function setProvice(){
		//var sheng = document.getElementById("sheng");
		$('#province').val($("#sheng").find("option:selected").text());
		$('#city').val($("#shi").find("option:selected").text());
		return true;
	}
	function changeType(value){
		var checkbox=document.getElementsByName("checkbox"); 
	    for(var i=0;i<checkbox.length;i++){
	         if(checkbox[i].value==value){
	        	 checkbox[i].checked = true;
	       }
	    }     

	}
	function modifyPtypes(){
		
		
		var selected='' ;
		var checkbox = document.getElementsByName("checkbox");
		for(var i=0;i<checkbox.length;i++){
			if(checkbox[i].checked == true){
				selected+=document.getElementById("parentId3"+i).value;
				if(i!=checkbox.length-1){
					selected=selected+",";
				}
			}
			
		}
		<%if(temp!=1){%>
		document.form4.action='<%=request.getContextPath()%>/admin/sortingAction.do?method=modifyProductTypeOrder&temp=<%=temp%>&batchId=<%=batchId%>&selected='+selected;
		<%}else{%>
		document.form4.action='<%=request.getContextPath()%>/admin/sortingAction.do?method=noChulimodifyProductTypeOrder&temp=<%=temp%>&batchId=<%=batchId%>&selected='+selected;
		<%}%>
		document.form4.method="post";
	    document.form4.submit();
		//window.location="<%=request.getContextPath()%>/admin/sortingAction.do?method=modifyOrder&temp=<%=temp%>";
	}
</script>
</head>
<body bgcolor="#ffcc00">
<%if(temp!=1){%>
	<form id="form1" name="form1" method="post" action="">
	<table width="99%" border="0" cellspacing="0">
 		<tr>
    		<td style="color: #000">所属分拣批次:<%=batchCode %></td>
    		<td style="color: #000"><strong>未定义产品分类的:<%=noAssignTypeOrderCount %></strong></td>
   			<td style="color: #000">未分配归属物流的:<%=noAssignDeliverOrderCount%></td>
    		<td width="24%" rowspan="2"  style="color: #000"><strong>
    		
    		<%if((groupCount!=null&&groupCount.equals("0"))) {
    			  if(group.isFlag(590)){%>
      				<input type="button" value="生成分拣波次" onclick="addGroup(<%=batchId%>)">
      			<%}%>
      		<%}%>
    		</strong></td>
  </tr>
  <tr>
    <td width="25%" style="color: #F00">订单数(<%=orderCount %>)</td>
    <td width="25%" style="color: #F00">SKU数(<%=SKUCount %>)</td>
    <td width="25%" style="color: #00F">商品个数(<%=productCount %>)</td>
    </tr>
</table>
</form>
<%} %>
<hr/>
  <div id="TabbedPanels1" class="TabbedPanels">
     <div id="dsDiv1" onclick="change('div1','div2')" class="divStyle" ><font size="2" style="color: #333">查询</font></div>
     <div id="dsDiv2" onclick="change('div2','div1')" class="divStyle" ><font size="2" style="color: #333">批量修改归属物流</font></div> 
     <div style="clear:both"></div>
     	<div id="div1" style='display:none;width:850px;height:100px'>
       <form id="form2" name="form2" method="post" action="">
      <table width="99%" border="0">
        <tr>
          <td width="328"><input type="text" name="code" id="code" size="30" <%if(code!=null&&code.length()>0) {%>value="<%=code %>"<%}else{%>value="订单编号/分拣波次号/分拣批次号"<%} %> onfocus="if(this.value=='订单编号/分拣波次号/分拣批次号'){this.value=''}"></td>
        </tr>
        <tr style="color: #000">
       快递公司：<select name="deliver">
		<option value="-1">全部</option>
		<option value="0">未分配</option>
		<option value="9">广速省外</option>
		<option value="10">广州宅急送</option>
		<option value="11">广东省速递局</option>
		<option value="12">广州顺丰</option>
		<option value="13">深圳自建</option>
		<option value="14">路通速递</option>
		<option value="16">如风达</option>
		<option value="17">赛澳递江苏</option>
		<option value="18">赛澳递上海</option>
	</select>
	 产品分类:<select name="parentId1" class="bd">
	<option value="-1">全部</option>
	<option value="0">未分配</option>
	<logic:iterate id="productType" name="productTypeMap">
	<option value="<bean:write name="productType" property="key" />"><bean:write name="productType" property="value" /></option>
	</logic:iterate>
</select>
       <input name="submit" type="submit" onClick="return setProvice();" value="查询" ></td>
	<td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
          </table>
  </form>
    </div>
    <div  id="div2" style="width:850px;height:100px" >
     <form id="form3" name="form3" method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=modifyOrderDeliver&temp=<%=temp%>">
    	<table width="99%" border="0">
    	<tr >
	      <td rowspan="2">
	      	<textarea name="ordersDeliver" id="ordersDeliver" cols="45" rows="5"></textarea>
	      	<input type="hidden" name="batchId" value="<%=batchId%>">
	      </td>
	      <td><font color='red'>输入格式：(可从excel中复制两列，粘贴至下面输入框中)<br>
	       QD090101249181&nbsp;广州宅急送<br>
	        D090101240066&nbsp;广东省外<br></font>
	      </td>
	    </tr>
	    <tr>
	      <td><input type="submit" name="button" id="button" value="提交"></td>
	    </tr>
	  </table>
	<% if((StringUtil.StringToId(result)+StringUtil.StringToId(failure))>0){%>
	    <font color='red'>批量修改归属物流成功<%=result %>个，失败<%=failure %>个<%} %></font>
	  </form>
	  <form method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=modifyOrderDeliverToEMS&batchId=<%=batchId%>">
	   	<input type="submit" name="button" id="button" value="分配剩余订单给EMS">
	   	<%if((shengwaiCount!=null&&shengwaiCount.length()>0)||(shengneiCount!=null&&shengneiCount.length()>0)) {%>
	   	<font color="black">分配省内订单 <%=shengneiCount %> 个，省外 <%=shengwaiCount %>个</font>
	   	<%} %>
	  </form>
	 </div>
    </div>
  <hr/>
 <form id="form4" name="form4" method="post"  <%if(temp!=1) {%>action="<%=request.getContextPath()%>/admin/sortingAction.do?method=modifyOrder&batchId=<%=batchId%>&temp=<%=temp%>"<%}else{ %>action="<%=request.getContextPath()%>/admin/sortingAction.do?method=noChulimodifyOrder"<%} %>>
 <input type='hidden' name='delivers' value='<%=deliver%>'/>
  <input type='hidden' name='parentId1s' value='<%=parentId1%>'/>
   <% if(group.isFlag(664)){%>
 <table  width="99%" border="0" cellspacing="0" >
  <%--<tr>
  <td colspan="9" style="color: #333">     &nbsp;&nbsp;&nbsp;快递公司：<select name="deliver1" id="deliver1">
		<option value="0">全部</option>
		<option value="9">广速省外</option>
		<option value="11">广东省速递局</option>
		<option value="12">广州顺丰</option>
	</select>&nbsp;&nbsp;&nbsp;产品分类:<select name="parentId2" id="parentId2" class="bd">
	<option value="0">全部</option>
	<logic:iterate id="productType" name="productTypeMap">
	<option value="<bean:write name="productType" property="key" />"><bean:write name="productType" property="value" /></option>
	</logic:iterate>
</select><input type="submit" name="button" id="button" value="修改所选" <%if((groupCount!=null&&!groupCount.equals("0"))&& (batchCount!=null&&batchCount.equals("0"))) {%>onclick="return confirm('此操作可能会影响分拣波次');"<%} %>></td>
</tr> --%>
             </table><%} %>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
 <tr bgcolor="#00ccff">
  <td width="5%" ><div align="center"><strong>全选</strong>
      <input name="" type="checkbox" value="" onclick="setAllCheck(document.form4, 'checkbox', this.checked);"></div></td>
    <td width="8%"><div align="center"><strong><font color="#00000">订单编号</font></strong></div></td>
      <td width="8%"><div align="center"><strong><font color="#00000">出库单编号</font></strong></div></td>
    <td width="6%"><div align="center"><strong><font color="#00000">申请出库时间</font></strong></div></td>
    <td width="6%"><div align="center"><strong><font color="#00000">快递公司</font></strong></div></td>
    <td width="8%"><div align="center"><strong><font color="#00000">订单地址</font></strong></div></td>
    <td width="9%"><div align="center"><strong><font color="#00000">商品名称</font></strong></div></td>
    <td width="8%"><div align="center"><strong><font color="#00000">产品分类</font></strong></div></td>
    <td width="8%"><div align="center"><strong><font color="#00000">应收款</font></strong></div></td>
    <td width="6%"><div align="center"><strong><font color="#00000">客户姓名</font></strong></div></td>
    <td width="8%"><div align="center"><strong><font color="#00000">电话</font></strong></div></td>
    </tr>
 <%if(orderList!=null){
 for(int i=0;i<orderList.size();i++){
   SortingBatchOrderBean sbBean = (SortingBatchOrderBean)orderList.get(i);
%>
 <tr bgcolor="#FFFFCC">
    <td style="color: #000"><div align="center"><%if(sbBean.status!=3){ %><input type="checkbox" name="checkbox"  value=<%= sbBean.getoOrder().getId()%>><%} %>&nbsp;<%=paging.getCurrentPageIndex()*paging.getCountPerPage()+ i+1%></div></td>
    <td style="color: #000"><a href="order.do?id=<%=sbBean.getoOrder().getId()%>" target="_blank"><%=sbBean.getoOrder().getCode()%></a></td>
     <td style="color: #000"><%=sbBean.getOrderStockCode() %></td>
    <td style="color: #000"><%=StringUtil.cutString(sbBean.getCkTime(),19) %></td>
    <td style="color: #000"><%if(sbBean.getoOrder().getDeliver()==0){%>未分配<%}else{%><%=voOrder.deliverMapAll.get(sbBean.getoOrder().getDeliver()+"")%><%} %></td>
    
    <td style="color: #000"><%=sbBean.getoOrder().getAddress()%></td>
    	<td><table border=0 width="100%" height="100%"><%for(int j=0,leng = sbBean.getProductList().size();j<leng;j++){
    	voOrderProduct bean = (voOrderProduct)sbBean.getProductList().get(j);%>
						<tr >
							<td style="color: #000;<%=j!=leng-1?"border-bottom:1px solid black;":""%>"><%=bean.getName()%></td>
						</tr><%} %>
					</table></td>
    <td style="color: #000">
    <%if(sbBean.status!=3){ %>
    <select name="parentId3" id="parentId3<%=i%>" class="bd" onchange='changeType(<%=sbBean.getoOrder().getId()%>)'>
		<%if(sbBean.getOrderType()==0){ %>
				<option value="0" selected="selected">未分类</option>
        <%} %>
		<% 
		Iterator iter = productTypeMap.entrySet().iterator();
		while (iter.hasNext()) {
			java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
			Object key = entry.getKey();
		%>
		<option value="<%=key%>" <%if(sbBean.getOrderType()==Integer.parseInt(String.valueOf(key))){ %>selected="selected"<%} %>>
			<%=entry.getValue() %>
		</option><%} %>
    </select><%}else{ %>&nbsp;<%=productTypeMap.get(sbBean.getOrderType()+"")%><%} %>
    
    </td>	
    <td style="color: #000"><%=sbBean.getoOrder().getDprice() %></td>
    <td style="color: #000"><%=sbBean.getoOrder().getName()%></td>
    <td style="color: #000"><% if(sbBean.getoOrder().getPhone().length()>7){%><%=sbBean.getoOrder().getPhone().substring(0,3)%>****<%=sbBean.getoOrder().getPhone().substring(7)%><%}else{ %><%=sbBean.getoOrder().getPhone() %><%} %></td>
    </tr><%}} %>
</table><br>
<input type="button" name="modifyPtype" id="modifyPtype" value="修改所选订单的商品种类" onclick='modifyPtypes()'>
</form>
<script type="text/javascript">
var TabbedPanels1 = new Spry.Widget.TabbedPanels("TabbedPanels1");
</script>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</body>
</html>
<script type="text/javascript">
function load(){
	 selectOption(document.getElementById("parentId1") ,"<%=parentId1%>");
	 selectOption(document.getElementById("deliver") ,"<%=deliver%>");
	 selectOption(document.getElementById("parentId2") ,"<%=parentId2%>");
	 selectOption(document.getElementById("deliver1") ,"<%=deliver1%>");
}
load();


</script>


