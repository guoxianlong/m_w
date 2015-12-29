<%@page import="cache.CatalogCache"%>
<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="ormap.*" %>
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

	String catalogIds = StringUtil.convertNull(request.getParameter("catalogIds"));
	String catalogId2s = StringUtil.convertNull(request.getParameter("catalogId2s"));
	String productLineName = StringUtil.convertNull(request.getParameter("productLineName"));
	String dateType = StringUtil.convertNull(request.getParameter("dateType"));
	String allProduct = StringUtil.convertNull(request.getParameter("allProduct"));
	String showHide = StringUtil.convertNull(request.getParameter("showHide"));
	int securityLine1 = StringUtil.StringToId(request.getParameter("securityLine"));
	int areaId = StringUtil.toInt(request.getParameter("areaId"));
	
	String isShow = StringUtil.convertNull(request.getAttribute("isShow").toString());	//是否显示数据区
	int supplierId = 0;	// 供应商id
	if(request.getAttribute("supplier") != null){
		supplierId = StringUtil.StringToId(request.getAttribute("supplier").toString());	
	}
	String suppliertext = StringUtil.convertNull(request.getParameter("suppliertext"));	//供应商名称
	String supplierIds = com.primit.cache.ProductLinePermissionCache.getProductLineSupplierIds(adminUser);
	String code = StringUtil.convertNull(request.getParameter("code"));	//商品编号
	String name = StringUtil.convertNull(request.getParameter("name"));	//商品原名称
	int minProductStock = StringUtil.StringToId(request.getParameter("minProductStock"));	//最小可发货库存
	int maxProductStock = StringUtil.StringToId(request.getParameter("maxProductStock"));	//最大可发货库存
	int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
	int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
	int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));
	int productBrandId = 0;	//品牌id
	if(request.getAttribute("productBrandId") != null){
		productBrandId = StringUtil.StringToId(request.getAttribute("productBrandId").toString());	
	}
	String brandtext = StringUtil.convertNull(request.getParameter("brandtext"));	//品牌名称
	String productBrandIds = StringUtil.convertNull((String)request.getAttribute("productBrandIds"));
	List supplierList = new ArrayList();
	if(request.getAttribute("supplierList") != null){
		supplierList = (List) request.getAttribute("supplierList");	//供货商信息
	}
	List statusList = new ArrayList();
	if(request.getAttribute("statusList") != null){
		statusList = (List) request.getAttribute("statusList");	//库存状态
	}
	String[] statuss = request.getParameterValues("productStatus");
	String status = "";
	if(statuss != null){
		for(int i=0;i<statuss.length;i++){
	status = status + statuss[i]+",";
		}
		if(status.endsWith(",")){
	status = status.substring(0,status.length() - 1);
		}
	}
	List productBrandList = new ArrayList();
	if(request.getAttribute("productBrandList") != null){
		productBrandList = (List) request.getAttribute("productBrandList");	//品牌信息
	}
%>
<html>
<head>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/brandNames2.js"></script>
<script language="JavaScript" src="js/pts.js"></script>
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
    var cellContent = objTbl.rows[r].cells[c].innerText;
    if(cellContent == null || cellContent.length==0){
        cellContent = objTbl.rows[r].cells[c].innerHTML;
        if(cellContent != null && cellContent.length > 0){
            var inputObj = objTbl.rows[r].cells[c].getElementsByTagName("input");
            if(inputObj != null && inputObj.length > 0){
            	cellContent = inputObj[0].value;
            }
        } else {
            cellContent = " ";
        }
    }
    returnText   +=   cellContent   +   "\t";   
  }
  returnText   +=   "\n";
  }
  // delete <br/>
  var reg = new RegExp("\r\n", "g");
  returnText = returnText.replace(reg, "");
  return   returnText;   
  }   
</script>
<script>
function exportList(){
	this.allForm.action="productstock.do?dateType=<%=dateType%>&allProduct=<%=allProduct%>&productLineName=<%=productLineName%>&areaId=<%=areaId%>&showHide=<%=showHide%>&catalogIds=<%=catalogIds%>&catalogId2s=<%=catalogId2s%>&flag=1&securityLine=<%=securityLine1%>";
	this.allForm.target="_self";
	this.allForm.submit();
}
function updateOneProduct(pId){
	var stockDayBjObj = document.getElementById("stockDayBj" + pId);
	var stockDayGdObj = document.getElementById("stockDayGd" + pId);
	if(stockDayBjObj != null){
		document.getElementById("stockDayBjUpdate").value = stockDayBjObj.value;
	}
	if(stockDayGdObj != null){
		document.getElementById("stockDayGdUpdate").value = stockDayGdObj.value;
	}
	document.getElementById("productIdUpdate").value = pId;
	document.forms['updateProductStockStandard'].submit();
}
function checkSelectProductIds(){
	var names = document.getElementsByName("selectProductIds");
	var i;
	var check = false;
	for(i=0; i<names.length; i++){
		if(names[i].checked){
			check = true;
		}
	}
	if(!check){
		window.alert("未选择任何产品");
		return false;
	}
	this.allForm.action='<%=request.getContextPath()+ "/admin/stock2/addBuyPlanAuto2.jsp"%>';
	this.allForm.target='_self';
	this.allForm.submit();
	return true;
}

function checkSearch(){
	 var minStock = trim(document.getElementById("minProductStock").value) * 1;
	 var maxStock = trim(document.getElementById("maxProductStock").value) * 1;
	 if(minStock != "" && maxStock != ""){
	 	var patrn=/^\d+$/;
	 	if(!patrn.exec(minStock) || !patrn.exec(maxStock)){
	 		alert("“可发货库存”只可输入大于等于零的整数！");
	 	   	return false;
	 	}
	 	if(minStock > maxStock){
	 	    alert("可发货库存输入框两个同时填写，且前一个小于等于后一个！!!!");
	 	    return false;
	 	}
	 }else if(minStock == "" && maxStock == ""){
		return true;
	 }else{
 		alert("可发货库存输入框两个同时填写，且前一个小于等于后一个！");
 		return false;
	 }
	return true;
}
window.onload = function() {
	selectOption(document.getElementById("brandId"), "<%=productBrandId %>");
	selectOption(document.getElementById("supplierId"), "<%=supplierId %>");
}
function select(){
	//此函数什么也不做，因为调用的公用类supplierNames.js中调用了此函数
}
function autoWrite(textName,selectName){
	textName.value = selectName.options[selectName.selectedIndex].text;
}
function dealOption(id) {
   	var s = document.getElementById(id).options;
   	var l = s.length;
   	for(var i = 0; i < l; i++){
	   	if(s[i].disabled && s[i].selected) {
	   		s[0].selected = "selected";
	   		return;
	   	}
   	}
   	sredirect(s.selectedIndex-1);	
}
</script>
</head>

<body>
<form name="searchForm" action="productstock.do?dateType=<%=dateType%>&allProduct=<%=allProduct%>&areaId=<%=areaId%>&showHide=<%=showHide%>&catalogIds=<%=catalogIds%>&catalogId2s=<%=catalogId2s%>&securityLine=<%=securityLine1%>" method="post" onsubmit="return checkSearch();">
	<input type="hidden" name="search" value="1"/>
	一级分类：<select name="parentId1" class="bd" style="width:120" onChange="dealOption('parentId1');">
			<option value="0"></option>
			 <%
				HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					boolean tag = false;	//标志位，为true则表明该option可选中
					voCatalog catalog = (voCatalog)iter.next();
					int id = catalog.getId();
					if(!"".equals(catalogIds) && catalogIds.length() > 0){
						String[] catalogIdArr = catalogIds.split(",");
						for(int i = 0; i < catalogIdArr.length; i++){
							if(("" + id).equals(catalogIdArr[i])){
				 %>
				 <option value="<%=id %>" ><%=catalog.getName() %></option>
				 <%
				 			tag = true;
							}
						}
					}
					if(!"".equals(catalogId2s) && catalogId2s.length() > 0){
						String[] catalogId2Arr = catalogId2s.split(",");
						HashSet set = new HashSet();
						for(int i = 0; i < catalogId2Arr.length; i++){
							voCatalog bean = CatalogCache.getParentCatalog(Integer.parseInt(catalogId2Arr[i]));
							if((!set.contains(Integer.valueOf(id)) && id == bean.getId())){ //相同父类的二级分类只取一次一级分类
				 %>
				 <option value="<%=id %>"><%=catalog.getName() %></option>
				 <%
				 			tag = true;
				 			set.add(Integer.valueOf(id));
							}
						}
					}
					if(!tag){
				%>	
				<option value="<%=id %>" style="color:#999999" disabled="disabled"><%=catalog.getName() %></option>
				<%
					}
				} 
			%>
		</select>&nbsp;&nbsp;
	二级分类：<select name="parentId2" class="bd" style="width:120" onChange="tredirect(this.options.selectedIndex-1);">
	<option value="0"></option></select>&nbsp;&nbsp;
	三级分类：<select name="parentId3" class="bd" style="width:120">
	<option value="0"></option></select>&nbsp;&nbsp;
	<script type="text/javascript">
	
	//分类联动
	var parentId1 = document.searchForm.parentId1;
	var spt = document.searchForm.parentId2;
	function sredirect(x){
	  if( x<=0){
		  document.searchForm.parentId2.options[0].selected=true;
		  document.searchForm.parentId3.length=1;
		  document.searchForm.parentId3.options[0].selected=true;
	  }
	  for (m = spt.options.length - 1; m > 0; m --)
	      spt.options[m] = null;
	  if(x < 0){
		return;
	  }
	  spt.options[0]=new Option("", "0");
	  for (i = 0; i < spts[x].length; i ++){	 
	      spt.options[i+1]=new Option(spts[x][i].text, spts[x][i].value);
	  }
	  spt.options[0].selected=true;
	  tredirect(0);
	}
	
	var tpt = document.searchForm.parentId3;
	function tredirect(y){
	  x = document.searchForm.parentId1.options.selectedIndex-1;
	  for (m = tpt.options.length - 1; m >= 0; m --)
	      tpt.options[m] = null;
      if(x < 0){
    		return;
    	  }
	  tpt.options[0]=new Option("", "0");
	  for (i = 0; i < tpts[x][y].length; i ++){
	      tpt.options[i+1]=new Option(tpts[x][y][i].text, tpts[x][y][i].value);
	  }
	  tpt.options[0].selected=true;
	}
	</script>
	<%if(parentId1 > 0){ %>
	<script>
		selectOption(document.searchForm.parentId1, '<%= parentId1 %>');
		sredirect(document.searchForm.parentId1.selectedIndex-1);
	</script>
	<%} %>
	<%if(parentId2 > 0){ %>
	<script>
		selectOption(document.searchForm.parentId2, '<%= parentId2 %>');
		tredirect(document.searchForm.parentId2.selectedIndex-1);
	</script>
	<%} %>
	<%if(parentId3 > 0){ %>
	<script>
		selectOption(document.searchForm.parentId3, '<%= parentId3 %>');
	</script>
	<%} %>
	
	品牌：
	<div id="auto2" style="position: absolute; left: 100px; top: 35px;"></div>
	<input type="text" name="brandtext" id="word2" style="width: 100px; font-size: 10pt; height: 20px;" value=<%=brandtext %>/>
	<input type="hidden" name="condition2" id="condition2" value=" and id in (<%=productBrandIds %>)">
	<span style="width:18px;border:0px solid red; margin-left:-8px; margin-bottom:-6px;">
	<select name="productBrandId" id="brandId" style="margin-left:-100px; width:118px;"
	onChange="autoWrite(document.searchForm.brandtext, document.searchForm.productBrandId);">
		<option value="0"></option>
		<%
		if (productBrandList != null && productBrandList.size() > 0) {
			for(int i = 0; i < productBrandList.size(); i++) {
				voSelect bean = (voSelect) productBrandList.get(i);
				if(bean != null){
	%>
	 			<option value="<%= bean.getId() %>" ><%= bean.getName() %></option>
	<%
				}
			} 
		}
	%>
	</select></span>&nbsp;&nbsp;
	
	供应商:
	<div id="auto" style="position: absolute; left: 100px; top: 35px;"></div>
	<input type="text" name="suppliertext" id="word" style="width: 100px; font-size: 10pt; height: 20px;" value="<%=suppliertext %>"/>
	<input type="hidden" name="condition" id="condition" value="status = 1 and id in (<%=supplierIds %>)">
	<span style="width:18px;border:0px solid red; margin-left:-8px; margin-bottom:-6px;">
	<select name="supplier" id="supplierId" style="margin-left:-100px;width:118px;"
	onChange="autoWrite(document.searchForm.suppliertext, document.searchForm.supplier);" >
		<option value="0"></option>
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
	<script type="text/javascript">selectOption(document.getElementById("supplierId"), "<%=supplierId %>");</script>
	<br/>
	商品编号：<input type=text name="code" size="20" value="<%=code%>">&nbsp;&nbsp;
	商品原名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）&nbsp;&nbsp;
	产品状态：<%
		for (int i = 0; i < statusList.size(); i++) {
			voSelect psBean = (voSelect) statusList.get(i);
			if(psBean != null){
	    %>
           <input type="checkbox" id="productStatus<%=psBean.getId() %>" name="productStatus" value="<%=psBean.getId() %>" />
           <%=psBean.getName() %>&nbsp;
		<%} }%>
		<%if(!"".equals(status) && status.length() > 0){ %>
			<script>checkboxChecked(document.getElementsByName("productStatus") ,"<%=status%>");</script>
		<%} %>
	可发货库存： 
			<input type=text name="minProductStock" id="minProductStock" size="10" value="<%=minProductStock == 0 ? "" : "" + minProductStock %>"> 
			至 
			<input type=text name="maxProductStock" id="maxProductStock" size="10" value="<%= maxProductStock == 0 ? "" : "" + maxProductStock%>">&nbsp;&nbsp;
			<input type="submit" value="查询"/>
</form>
<%if("1".equals(isShow)){ %>
<form method="post" action="" name="allForm" target="_blank">
	<input type="hidden" name="search" value="1"/>
	<!-- searchForm表单里的参数 begin -->
	<input type="hidden" name="parentId1" value="<%=parentId1 %>" />
	<input type="hidden" name="parentId2" value="<%=parentId2 %>" />
	<input type="hidden" name="parentId3" value="<%=parentId3 %>" />
	<input type="hidden" name="brandtext" value="<%=brandtext %>" />
	<input type="hidden" name="suppliertext" value="<%=suppliertext %>" />
	<input type="hidden" name="code" value="<%=code %>" />
	<input type="hidden" name="name" value="<%=name %>" />
	<input type="hidden" name="productStatus" value="<%=status %>" />
	<input type="hidden" name="minProductStock" value="<%=minProductStock %>" />
	<input type="hidden" name="maxProductStock" value="<%=maxProductStock %>" />
	<!-- end -->
	<input type="hidden" id="updateType" name="updateType" value="" />
	<input type="hidden" id="updateCatalogIds" name="catalogIds" value="<%= catalogIds %>" />
	<input type="hidden" id="updateCatalogId2s" name="catalogId2s" value="<%= catalogId2s %>" />
	<input type="hidden" id="securityLine" name="securityLine" value="<%=securityLine1 %>"/>
	<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
		<tr><td>
			批量处理:&nbsp;全部<input type="checkbox" name="allProduct" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;
			代理商:<select name="proxyId">
				<option value="0"> </option>
				<%if(request.getAttribute("proxyMap") != null){ %>
			    <logic:iterate name="proxyMap" id="proxy" >
				    <option value="<bean:write name="proxy" property="key" />"><bean:write name="proxy" property="value" /></option>
			    </logic:iterate>
			    <%} %>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			<%--北京库存天数:<input type="text" name="stockDayBj" value="" size="5" />&nbsp;&nbsp;&nbsp;&nbsp;--%>
			广东库存天数:<input type="text" name="stockDayGd" value="" size="5" />&nbsp;&nbsp;&nbsp;&nbsp;
			<button type="button" onclick="document.getElementById('updateType').value='0';this.form.action='./updateProductStockStardard.do';this.form.submit()">提交库存天数</button>&nbsp;&nbsp;&nbsp;&nbsp;
			<button type="button" onclick="document.getElementById('updateType').value='1';this.form.action='./updateProductStockStardard.do';this.form.submit()">修改库存标准</button>
			<%int securityLine = StringUtil.toInt(request.getParameter("securityLine"));
				if((securityLine == ProductLineMap.SECURITYLINE0 && group.isFlag(100))||
				(securityLine == ProductLineMap.SECURITYLINE1 && group.isFlag(101))||
				(securityLine == ProductLineMap.SECURITYLINE2 && group.isFlag(102))||
				(securityLine == ProductLineMap.SECURITYLINE3 && group.isFlag(103))||
				(securityLine == ProductLineMap.SECURITYLINE4 && group.isFlag(104))||
				(securityLine == ProductLineMap.SECURITYLINE6 && group.isFlag(132))||
				(securityLine == ProductLineMap.SECURITYLINE7 && group.isFlag(158))||
				(securityLine == ProductLineMap.SECURITYLINE8 && group.isFlag(161))||
				(securityLine == ProductLineMap.SECURITYLINE9 && group.isFlag(165))||
				(securityLine == ProductLineMap.SECURITYLINE10 && group.isFlag(176))||
				(securityLine == ProductLineMap.SECURITYLINE11 && group.isFlag(308))||
				(securityLine == ProductLineMap.SECURITYLINE12 && group.isFlag(361))||
				(securityLine == ProductLineMap.SECURITYLINE13 && group.isFlag(363))||
				(securityLine == ProductLineMap.SECURITYLINE14 && group.isFlag(367))||
				(securityLine == ProductLineMap.SECURITYLINE5 && group.isFlag(105))||
				(securityLine == ProductLineMap.SECURITYLINE16 && group.isFlag(403))||
				(securityLine == ProductLineMap.SECURITYLINE15 && group.isFlag(394))||
				(securityLine == ProductLineMap.SECURITYLINE17 && group.isFlag(486))||
				(securityLine == ProductLineMap.SECURITYLINE18 && group.isFlag(488))){ %>
			<button type="button" onclick="javascript:this.form.action='<%=request.getContextPath()+ "/admin/stock2/addBuyPlanAuto.jsp"%>';this.form.target='_self';this.form.submit()">一键生成采购计划</button>
			<button type="button" onclick="checkSelectProductIds()">对勾选产品生成采购计划单</button>
			<%} %>
		</td></tr>
	</table>
          <br />
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" id="productStockTable" class="sortable">
          <thead class="sorthead">
              <tr bgcolor="#4688D6">
              <td></td>
              <td width="40" align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
              <td width="40" align="center"><font color="#FFFFFF">批发价</font></td>
<%}%>
<%if(group.isFlag(256)){%>
			  <td width="40" align="center"><font color="#FFFFFF">最低进货税后价</font></td>
<%}%>
			  <td width="40" align="center"><font color="#FFFFFF">买卖宝价</font></td>
			  <td width="40" align="center"><font color="#FFFFFF">市场价</font></td>
              <td align="center"><font color="#FFFFFF">代理商</font></td>
              <td width="50" align="center"><font color="#FFFFFF">产品状态</font></td>
              <td width="50" align="center"><font color="#FFFFFF">日销量</font></td>
              <td width="50" align="center"><font color="#FFFFFF">上周销量</font></td>
              <td width="50" align="center"><font color="#FFFFFF">处理中<br/>的数量</font></td>
              <%-- <td width="50" align="center"><font color="#FFFFFF">待申请<br/>出库的量</font></td> --%>
              <td width="50" align="center"><font color="#FFFFFF">上月销量</font></td>
<%if(areaId == -1 || areaId == 0){%>
              <td width="50" align="center"><font color="#FFFFFF">北京待验库</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京不可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京残次品</font></td>
              <td width="30" align="center"><font color="#FFFFFF">北京<br/>库存<br/>天数</font></td>
              <td width="60" align="center"><font color="#FFFFFF">库存标准<br/>(北京)</font></td>
              <td width="50" align="center"><font color="#FFFFFF">北京预计<br/>进货数</font></td>
<%}%>
<%if(areaId == -1 || areaId == 1){%>
              <td width="50" align="center"><font color="#FFFFFF">广东待验库</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东不可发货</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东残次品</font></td>
              <td width="30" align="center"><font color="#FFFFFF">广东库存天数</font></td>
              <td width="60" align="center"><font color="#FFFFFF">库存标准<br/>(广东)</font></td>
              <td width="50" align="center"><font color="#FFFFFF">广东预计<br/>进货数</font></td>
<%}%>
			  <td><font color="#FFFFFF">采购计划数</font></td>
			  <td width="50" align="center"><font color="#FFFFFF">在途量</font></td>
              <td width="30" align="center"><font color="#FFFFFF">操作</font></td>
            </tr>
	</thead>
<logic:present name="productList" scope="request">
<%
Map lastWeekSellCount = (Map)request.getAttribute("lastWeekSellCount");
Map lastMonthSellCount = (Map)request.getAttribute("lastMonthSellCount");
//Map lastWeekOrderCount = (Map)request.getAttribute("lastWeekOrderCount");
Map dealingCount = (Map)request.getAttribute("dealingCount");
Map waitingStockOutCount = (Map)request.getAttribute("waitingStockOutCount");
Map lastDaySellGdCount = (Map)request.getAttribute("lastDaySellGdCount");
Map lastWeekSellGdCount = (Map)request.getAttribute("lastWeekSellGdCount");
Map lastMonthSellGdCount = (Map)request.getAttribute("lastMonthSellGdCount");
//Map lastWeekOrderGdCount = (Map)request.getAttribute("lastWeekOrderGdCount");
Map dealingGdCount = (Map)request.getAttribute("dealingGdCount");
Map packageMap = (Map)request.getAttribute("packageMap");
Map packageMap2 = (Map)request.getAttribute("packageMap2");

Map lastWeekSellCountPackage = (Map)request.getAttribute("lastWeekSellCountPackage");
Map lastMonthSellCountPackage = (Map)request.getAttribute("lastMonthSellCountPackage");
Map dealingCountPackage = (Map)request.getAttribute("dealingCountPackage");
Map waitingStockOutCountPackage = (Map)request.getAttribute("waitingStockOutCountPackage");
Map lastDaySellGdCountPackage = (Map)request.getAttribute("lastDaySellGdCountPackage");
Map lastWeekSellGdCountPackage = (Map)request.getAttribute("lastWeekSellGdCountPackage");
Map lastMonthSellGdCountPackage = (Map)request.getAttribute("lastMonthSellGdCountPackage");
Map dealingGdCountPackage = (Map)request.getAttribute("dealingGdCountPackage");

Map buyCountGDMap = (Map)request.getAttribute("buyCountGDMap");
Map minBuyPriceMap = (Map)request.getAttribute("minBuyPriceMap");
%> 
<form action="addk" method="post">
<%String productId = ""; %>
<logic:iterate name="productList" id="item" >
<input type="hidden" name="productId" value="<bean:write name="item" property="id" />" />
<%
Object lwsc = lastWeekSellCount.get(Integer.valueOf(((voProduct)item).getId()));
Object lmsc = lastMonthSellCount.get(Integer.valueOf(((voProduct)item).getId()));
//Object lwoc = lastWeekOrderCount.get(Integer.valueOf(((voProduct)item).getId()));
Object dc = dealingCount.get(Integer.valueOf(((voProduct)item).getId()));
Object wsoc = waitingStockOutCount.get(Integer.valueOf(((voProduct)item).getId()));
Object ldscGd = lastDaySellGdCount.get(Integer.valueOf(((voProduct)item).getId()));
Object lwscGd = lastWeekSellGdCount.get(Integer.valueOf(((voProduct)item).getId()));
Object lmscGd = lastMonthSellGdCount.get(Integer.valueOf(((voProduct)item).getId()));
//Object lwocGd = lastWeekOrderGdCount.get(Integer.valueOf(((voProduct)item).getId()));
Object dcGd = dealingGdCount.get(Integer.valueOf(((voProduct)item).getId()));

Object lwscp = lastWeekSellCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object lmscp = lastMonthSellCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object dcp = dealingCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object wsocp = waitingStockOutCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object ldscGdp = lastDaySellGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object lwscGdp = lastWeekSellGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object lmscGdp = lastMonthSellGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));
Object dcGdp = dealingGdCountPackage.get(Integer.valueOf(((voProduct)item).getId()));

Integer buyCountGD = (Integer)buyCountGDMap.get(Integer.valueOf(((voProduct)item).getId()));
String minBuyPrice = (String)minBuyPriceMap.get(String.valueOf(((voProduct)item).getId()));
if(minBuyPrice == null){
	minBuyPrice = "0.0";
}
 
adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;

	voProduct pp = (voProduct)packageMap.get(Integer.valueOf(voItem.getId()));
	voProduct pp2 = (voProduct)packageMap2.get(Integer.valueOf(voItem.getId()));
	int ppCount = 0;
	int ppCount2 = 0;
	int ppCountGd2 = 0;
%>
		<tr bgcolor='#F8F8F8'>
		<td><input type="checkbox" name="selectProductIds" value="<bean:write name="item" property="id" />"/></td>
		<td align=left><a href="fproduct.do?id=<bean:write name="item" property="id" />&simple=1" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a></td>
<%if(group.isFlag(41)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='right'><bean:write name="item" property="price3" />元</td>
<%}%>
<%if(group.isFlag(256)) /*if(isSystem || isShangpin || isYunyingzhongxin)*/{%>
		<td align='right'><%=minBuyPrice %>元</td>
<%}%>
		<td align='right'><bean:write name="item" property="price" />元</td>
		<td align='right'><bean:write name="item" property="price2" />元</td>
		<td align='center' ><%=StringUtil.convertNull(voItem.getProxyName())%></td>
		<td align='center' ><%= voItem.getStatusName() %></td>
		<logic:present parameter="areaId" scope="request">
		<logic:equal parameter="areaId" scope="request" value="0" >
		<td align='right' ><%= NumberUtil.sum(lwsc, lwscp) %></td>
		<td align='right' ><%= NumberUtil.sum(dc, dcp) %></td>
		<td align='right' ><%= NumberUtil.sum(wsoc, wsocp) %></td>
		<td align='right' ><%= NumberUtil.sum(lmsc, lmscp) %></td>
		</logic:equal>
		<logic:equal parameter="areaId" scope="request" value="1" >
		<td align='right' ><%= NumberUtil.sum(ldscGd, ldscGdp) %></td>
		<td align='right' ><%= NumberUtil.sum(lwscGd, lwscGdp) %></td>
		<td align='right' ><%= NumberUtil.sum(dcGd, dcGdp) %></td>
		<%-- <td align='right' ><%= NumberUtil.sum(wsoc, wsocp) %></td> --%>
		<td align='right' ><%= NumberUtil.sum(lmscGd, lmscGdp) %></td>
		</logic:equal>
		</logic:present>
		<logic:notPresent parameter="areaId" scope="request">
		<td align='right' ><%= NumberUtil.sum(lwsc, lwscp) %><%= (lwscGd==null && lwscGdp==null)?"":"(" + NumberUtil.sum(lwscGd, lwscGdp) + ")" %></td>
		<td align='right' ><%= NumberUtil.sum(dc, dcp) %><%= (dcGd==null && dcGdp==null)?"":"(" + NumberUtil.sum(dcGd, dcGdp) + ")" %></td>
		<td align='right' ><%= NumberUtil.sum(wsoc, wsocp) %><%= (wsoc==null && wsocp==null)?"":"(" + NumberUtil.sum(wsoc, wsocp) + ")" %></td>
		<td align='right' ><%= NumberUtil.sum(lmsc, lmscp) %><%= (lmscGd==null && lmscGdp==null)?"":"(" + NumberUtil.sum(lmscGd, lmscGdp) + ")" %></td>
		</logic:notPresent>
<%if(areaId == -1 || areaId == 0){ %>
<% if(voItem.getStockStandardBj() >= (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) * 2 && (voItem.getStockStandardBj() != 0)){ %>
		<td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %></font></td>
        <td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) %></font></td>
		<td align=right ><font color="red"><input type="text" id="stockDayBj<bean:write name="item" property="id" />" name="stockDayBj<bean:write name="item" property="id" />" value="<bean:write name="item" property="stockDayBj" />" size="3" /></font></td>
		<td align=right ><font color="red"><bean:write name="item" property="stockStandardBj" /></font></td>
		<td align=right >
			<%
				if(productId.indexOf(voItem.getId())==-1){
					productId += voItem.getId()+";";
				}
			 %>
			<input type="hidden" name="bjStockNeed<bean:write name="item" property="id" />" value="<%= voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) %>" />
			<font color="red"><%= (voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) > 0)?(voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK))):0 %></font>
		</td>
<%} else { %>
		<td align=right ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
        <td align=right ><%= voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=right ><input type="text" id="stockDayBj<bean:write name="item" property="id" />" name="stockDayBj<bean:write name="item" property="id" />" value="<bean:write name="item" property="stockDayBj" />" size="3" /></td>
		<td align=right ><bean:write name="item" property="stockStandardBj" /></td>
		<td align=right >
			<input type="hidden" name="bjStockNeed" value="<%= voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) %>" />
			<%= (voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK)) > 0)?(voItem.getStockStandardBj() - (voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_CHECK))):0 %>
		</td>
<% } %>
<%} %>
<%if(areaId == -1 || areaId == 1){ %>
<% if(voItem.getStockStandardGd() >= (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) ) * 2 && (voItem.getStockStandardGd() != 0)){ %>
		<td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) %></font></td>
        <td align=right><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) %></font></td>
        <td align=right ><font color="red"><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE)%></font></td>
		<td align=right ><font color="red"><input type="text" id="stockDayGd<bean:write name="item" property="id" />" name="stockDayGd<bean:write name="item" property="id" />" value="<bean:write name="item" property="stockDayGd" />" size="3" /></font></td>
		<td align=right ><font color="red"><bean:write name="item" property="stockStandardGd" /></font></td>
		<td align=right >
			<%
				if(productId.indexOf(String.valueOf(voItem.getId()))==-1){
					productId += voItem.getId()+";";
				}
			 %>
			<input type="hidden" name="gdStockNeed<bean:write name="item" property="id" />" value="<%= voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) ) %>" />
			<font color="red"><%= (voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) ) > 0)?(voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) )):0 %></font>
		</td>
<%} else { %>
		<td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)   + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td align=right><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)  + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) %></td>
        <td align=right ><%= voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) %></td>
		<td align=right ><input type="text" id="stockDayGd<bean:write name="item" property="id" />" name="stockDayGd<bean:write name="item" property="id" />" value="<bean:write name="item" property="stockDayGd" />" size="3" /></td>
		<td align=right ><bean:write name="item" property="stockStandardGd" /></td>
		<td align=right >
			<input type="hidden" name="gdStockNeed" value="<%= voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) ) %>" />
			<%= (voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) ) > 0)?(voItem.getStockStandardGd() - (voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + voItem.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) )):0 %>
		</td>
<% } %>
<%} %>
		<td><input type="text" size="2" name="GDBuyPlanCount<bean:write name="item" property="id" />"/></td>
		<td align=right ><%=buyCountGD == null?0:buyCountGD.intValue() %></td>
		<td align="center"><input type="button" value="修改标准" onclick="updateOneProduct(<bean:write name="item" property="id" />);" /></td>
		</tr>
</logic:iterate> 
<input type="hidden" name="productIds" value="<%=(productId.endsWith(";"))?productId.substring(0,productId.length()-1):"0"%>"/>
</form>
</logic:present> 
          </table>
</form>
<form action="./updateProductStockStardard.do" method="post" name="updateProductStockStandard" target="_blank">
	<input type="hidden" id="productIdUpdate" name="productId" value="" />
	<input type="hidden" id="stockDayGdUpdate" name="stockDayGd" value="" />
	<input type="hidden" id="stockDayBjUpdate" name="stockDayBj" value="" />
</form>
          <br />
<input type="button" value="导出警戒产品" onClick="javascript:exportList();" /><br/>
<%} %>
<script>

</script>
<pre>
规则说明:
1、时间：订单生成日期
2、范围：根据产品线和产品状态
   ①广东成人保健警戒产品/手机数码警戒产品/电脑警戒产品/服装警戒产品：普通\促销\热销\隐藏；
   ②手机数码警戒配件产品：普通\促销\热销
3、上周销量、上月销量：上周销量倒推七天，上月销量倒推30天;销量所涉及订单内容包括订单出货状态为已出货/待出货和处理中的订单；
4、库存标准=（上周销量+处理中产品数）*库存天数/7；     
5、批量处理方式：包含两种①全部：如果选中全部则不论是否选择代理商都会对所查询出来的所有内容进行操作，②代理商：所查询出来的产品
根据代理商来修改；批量操作时广东库存天数必须填有数字，未填项将默认为0；提交库存天数只对库存天数进行修改；修改库存标准不需要先修改库存天数，选择完批处理方式，接着将
库存天数都填上，最后点击修改库存标准就将所选内容进行了库存标准的修改
<font color="red">6、“一键生成采购计划”按钮对所有“广东预计进货数”大于0的产品生成采购计划单；“对勾选产品生成采购计划单”按钮对已进行勾选的产品，按所填写的“采购计划数”生成采购计划单</font></pre>
</body>
</html>