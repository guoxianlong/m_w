<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page isELIgnored="false" %>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.buy.*, adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.stock.*,mmb.stock.IMEI.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%@ page import="mmb.stock.cargo.*"%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部	
%>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.PageUtil" %>
<%
BuyStockinAction action = new BuyStockinAction();
action.buyStockin(request, response);

List productList = (List) request.getAttribute("productList");
List buyStockinProductList = (List) request.getAttribute("bsipList");
BuyStockinBean bean = (BuyStockinBean)request.getAttribute("bean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
List proxyList = (List) request.getAttribute("proxyList");
ArrayList buyReturnList = (ArrayList)request.getAttribute("buyReturnList");
ArrayList errorProductList = (ArrayList)request.getAttribute("errorProductList");
String isMMBMobileS = (String) request.getAttribute("isMMBMobile");
boolean isMMBMobile = false;
Integer productId = new Integer(0);
List<IMEIBuyStockinBean> imeiList = null;
if( isMMBMobileS != null && isMMBMobileS.equals("YES") ) {
	isMMBMobile = true;
	imeiList = (ArrayList<IMEIBuyStockinBean>) request.getAttribute("imeiList");
	productId = (Integer) request.getAttribute("productId");
}

int i, count;
voProduct product = null;
BuyStockinProductBean bsip = null;
Iterator iter = null;
voSelect proxy = null;

boolean check = false;
if(request.getParameter("check") != null){
	check = true;
}

boolean shenhe = group.isFlag(116);
boolean zhuanhuan = group.isFlag(114);
boolean complete = group.isFlag(115);
boolean bianji = group.isFlag(169);

String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
document.location = "buyStockinList.jsp?isBTwoC=${isBTwoC}";
</script>
<%
	return;
} else { 

Boolean hasDifObj = (Boolean) request.getAttribute("hasDif");
boolean hasDif = false;
if(hasDifObj != null){
	hasDif = hasDifObj.booleanValue();
}

Map difMap = (Map) request.getAttribute("difMap");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../../js/jquery.js"></script>
<script language="JavaScript" src="../js/count2.js"></script>
<script>
function check(){
	if(confirm("请先详细核对一遍！")){
		return true;
	}
	return false;
}

	function confirmComplete() {
		var tt = <%=bean.getId()%>;
		window.location.href="confirmBuyStockin.jsp?&isBTwoC=${isBTwoC}&buyStockinId="+tt+"&mark=1&completeFlag=0";
		document.getElementById("askComplete").style.display="none";
		return true;
	}
	
	function confirmNotComplete() {
		var tt = <%=bean.getId()%>;
		window.location.href="confirmBuyStockin.jsp?&isBTwoC=${isBTwoC}&buyStockinId="+tt+"&mark=1&completeFlag=1";
		document.getElementById("askComplete").style.display="none";
		return true;
	}
	
	function openConfirm() {
		confirmComplete();
	}


function shenhe(a){
	if(a==1){
		if(confirm("审核通过？确认后不能撤销。")){
			var tt = <%=bean.getId()%>;
			jQuery.post("../../admin/checkStockinMissionAction.do?method=judgeComOrderByStockinId&buyStockinId="+tt,
			function(result){
				if(result=="0"){
					openConfirm();
				}else if(result=="1"){
					window.location.href="confirmBuyStockin.jsp?isBTwoC=${isBTwoC }&buyStockinId="+tt+"&mark=1&completeFlag=1";
				}else if(result!=""){
					alert(result);
				}
				
			})
			return true;
		}
	}else if(a==0){
		if(confirm("审核不通过？确认后不能撤销。")){
			return true;
		}
	}
	return false;
}
function complete(){
	if(confirm("确认入库？确认后不能撤销。")){
		return true;
	}
	return false;
}

function addproduct(code,pid){
	document.addBuyStockinForm.productCode.value=code;
	var value = 1;
	value = prompt('将添加产品编号为'+code+'的产品，请输入数量，并确认', 1);
	if(value != null && value > 0){
		document.addBuyStockinForm.stockinCount.value = value;
		document.addBuyStockinForm.submit();
	}
}
</script>
<p align="center">采购入库操作</p>
<form method="post" action="editBuyStockin2.jsp">
编号：<%= bean.getCode() %>&nbsp;&nbsp;状态：
<%if(bean.getStatus() != BuyStockinBean.STATUS4){%>
<font color="red"><%=bean.getStatusName()%></font> 
<%if(bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1 || bean.getStatus() == BuyStockinBean.STATUS5){ %>
<%--<%if(check){%>--%>
<a href="completeBuyStockin.jsp?isBTwoC=${isBTwoC }&buyStockinId=<%=bean.getId()%>" onclick="return complete();">确认入库</a>
<%--<%}else{%>
<a href="buyStockin.jsp?id=<%=bean.getId()%>&check=1" onclick="return check();">确认入库</a>
<%}%>--%>
<%}else if(bean.getStatus() == BuyStockinBean.STATUS3 && shenhe){%>
<a onclick="shenhe(1)">通过审核</a>&nbsp;&nbsp;&nbsp;
<a href="confirmBuyStockin.jsp?isBTwoC=${isBTwoC }&buyStockinId=<%=bean.getId()%>&mark=0" onclick="return shenhe(0)">未通过审核</a>
<%}%>
<%
}else{%><%=bean.getStatusName()%><%}%><br/>
<%if(group.isFlag(184)){ %>
来源于：<a href="buyStock.jsp?isBTwoC=${isBTwoC }&stockId=<%=request.getAttribute("stockId")%>"><%=request.getAttribute("buyStockCode")==null?"":request.getAttribute("buyStockCode")%></a>&nbsp;&nbsp;&nbsp;
<a href="buyOrder.jsp?isBTwoC=${isBTwoC }&orderId=<%=request.getAttribute("orderId")%>"><%=request.getAttribute("buyOrderCode")==null?"":request.getAttribute("buyOrderCode")%></a>&nbsp;&nbsp;&nbsp;
<a href="buyPlan.jsp?planId=<%=request.getAttribute("planId")%>"><%=request.getAttribute("buyPlanCode")==null?"":request.getAttribute("buyPlanCode")%></a>
<%} // group.isFlag(184)%>
<br/>
备注：<input type="text" name="remark" size=50 value="<%=bean.getRemark()%>"/>&nbsp;&nbsp;&nbsp;
<input type="submit" value="修改"/>
<input type="hidden" name="buyStockinId" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="buyStockin.jsp"/>
</form>
<fieldset>
   <legend>操作</legend>
	<p align="center"><%if(group.isFlag(183)){ %><a href="buyAdminHistory.jsp?logId=<%=bean.getId()%>&logType=<%= BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN %>" target="_blank">人员操作记录</a>|<%} %><a href="buyStockinList.jsp?isBTwoC=${isBTwoC }">返回采购入库操作记录列表</a><%if(group.isFlag(31)){ %>|<a href="buyStockinPrint.jsp?stockinId=<%=bean.getId()%>" target="_blank">导出列表</a><%} %></p>
</fieldset>
<form method="post" action="editBuyStockinItem.jsp">
<style type=text/css>   
  .redfont{color:red}
</style>
<input type="hidden" name="isBTwoC" value="${isBTwoC }" />
<div id="auto" style="position:absolute; left:100px; top:222px;"></div>
<table width="95%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<td colspan="8" align="left">
		统一使用代理商：
		<script>document.all.proxytext.value=document.getElementById('proxy').options[document.getElementById('proxy').selectedIndex].text;</script>
		<%-- <input type="text" name="proxytext" onClick="select()"  id="word" style="width:100px;height:21px;font-size:10pt;"<%if(bean.getStatus() == BuyStockinBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS3){%> readonly<%}%>>
		<span style="width:18px;border:0px solid red;"><select name="proxy" onchange="javascript:document.all.proxytext.value=document.getElementById('proxy').options[document.getElementById('proxy').selectedIndex].text" id="proxy"<%if(bean.getStatus() == BuyStockinBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS6 || check){%> disabled="disabled"<%}%> style="margin-left:-100px;width:118px;"> --%>
		<input type="text" name="proxytext" onClick="select()"  id="word" style="width:100px;height:20px;font-size:10pt;" readonly />
		<span style="width:18px;border:0px solid red;margin-left:-8px;margin-bottom:-6px;"><select name="proxy" onchange="javascript:document.all.proxytext.value=document.getElementById('proxy').options[document.getElementById('proxy').selectedIndex].text" id="proxy" disabled="disabled" style="margin-left:-100px;width:118px;">
			<option value="0"></option>
			<%
				iter = proxyList.listIterator();
				while(iter.hasNext()){
					proxy = (voSelect)iter.next();
			%>
  			<option value="<%= proxy.getId() %>"><%= proxy.getName() %></option>
			<%} %>
		</select>
		</span>
		<%if(request.getAttribute("proxyId")!=null){%>
			<script>selectOption(document.getElementById('proxy'), '<%=request.getAttribute("proxyId")%>');</script>
		<%}%>
		&nbsp;&nbsp;
		<%-- <%if(bean.getStatus() != BuyStockinBean.STATUS4 && bean.getStatus() != BuyStockinBean.STATUS6 &&!check){%><input type="submit" value="修改"/>&nbsp;&nbsp;&nbsp;<%}%> 
		地区：<%if(bean.getStockArea()==ProductStockBean.AREA_BJ){%>北京<%}else if(bean.getStockArea()==ProductStockBean.AREA_GF){ %>芳村<%}else if(bean.getStockArea()==ProductStockBean.AREA_ZC){ %>增城<%} %>--%>
		地区：
		<% List areaList2 = CargoDeptAreaService.getCargoDeptAreaList(request); 
		%>
		<select name="stockArea" disabled="disabled">
			<%
				for(int j = 0;j<areaList2.size();j++){
					ProductStockBean psBean = new ProductStockBean();
					int areaId = Integer.parseInt(areaList2.get(j).toString());
			%>
			<option value="<%=areaList2.get(j)%>" <%if(areaId==bean.getStockArea()){%>selected<%}%>><%=psBean.getAreaName(areaId) %></option>
			<%
				}
			%>
			<option value="0" <%if(bean.getStockArea()==ProductStockBean.AREA_BJ){ %>selected=selected<%} %>>请选择</option>
			<option value="1" <%if(bean.getStockArea()==ProductStockBean.AREA_GF){ %>selected=selected<%} %>>芳村</option>
			<option value="3" <%if(bean.getStockArea()==ProductStockBean.AREA_ZC){ %>selected=selected<%} %>>增城</option>
			<option value="5" <%if(bean.getStockArea()==ProductStockBean.AREA_JD){ %>selected=selected<%} %>>京东</option>
		</select>
	</td>
<tr>
  <td>序号</td>
  <td>产品线</td>
  <td>产品编号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>预计入库量</td>
  <td>入库前库存</td>
  <td>状态及操作</td>
  <td>查进销存</td>
</tr>
<%
count = buyStockinProductList.size();
int planCount = 0;
for(i = 0; i < count; i ++){
	bsip = (BuyStockinProductBean) buyStockinProductList.get(i);
	product = bsip.getProduct();
%>
<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
  <td<%if(errorProductList!=null){if(errorProductList.contains(Integer.valueOf(bsip.getProductId()))){%> class=redfont<%}}%>><%=i+1%></td>
  <td><%=bsip.getProductLineName()%></td>
  <td><a href="../fproduct.do?id=<%=bsip.getProduct().getId()%>" target="_blank"><%=bsip.getProduct().getCode()%></a></td>
  <td><a href="../fproduct.do?id=<%=bsip.getProduct().getId()%>" target="_blank"><%=bsip.getProduct().getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=bsip.getProduct().getId()%>" target="_blank"><%=bsip.getProduct().getOriname()%></a></td>
  <td>
  <input type="text" name="stockinCount<%=bsip.getProduct().getId()%>" id="stockinCount" size="5" value="<%=bsip.getStockInCount()%>" <%if(bean.getStatus() == BuyStockBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS6 || check || isMMBMobile){%>readonly<%}%>/>
  </td>
  <td>
  	<%if(bean.getStockArea()==ProductStockBean.AREA_BJ){%>
  	<%=bsip.getProduct().getStock(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)+bsip.getProduct().getLockCount(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)%>
  	<%}else if(bean.getStockArea()==ProductStockBean.AREA_GF){%>
  	<%=bsip.getProduct().getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED)+bsip.getProduct().getLockCount(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED)+
  	   bsip.getProduct().getStock(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED)+bsip.getProduct().getLockCount(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED)%>
  	<%}%>
  </td>
  <td>
  <%if((bean.getStatus() == BuyStockinBean.STATUS3&&(bianji&&shenhe))||((bean.getStatus()==BuyStockinBean.STATUS0||bean.getStatus()==BuyStockinBean.STATUS1||bean.getStatus()==BuyStockinBean.STATUS5)&&(bianji||bean.getCreateUserId()==user.getId()))){%>
  <% if( isMMBMobile ) { %>
  
  <% } else { %>
  <a href="deleteBuyStockinItem.jsp?isBTwoC=${isBTwoC }&buyStockinId=<%=bean.getId()%>&productId=<%=bsip.getProduct().getId()%>" onclick="return confirm('确认删除？');">删除</a>
  <% }%>
  <%}%>
  </td>
  <td><a href="../productStock/stockCardList.jsp?productCode=<%= bsip.getProductCode() %>" target="_blank">查</a></td>
</tr>
<%
}
%>
</table>
<%if((bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1 || bean.getStatus() == BuyStockinBean.STATUS2 || bean.getStatus() == BuyStockinBean.STATUS3 || bean.getStatus() == BuyStockinBean.STATUS5)&& (bianji||bean.getCreateUserId()==user.getId()) && !check){%>
	<% if( !isMMBMobile ) {%>
		<p align="center"><input type="submit" value="修改"/></p>
	<% } %>
<%} else {
	if( isMMBMobile ) {
%>
	<br/>
	<br/>
	<br/>
	<div  >
	<input type="hidden" name="productId" id="productId" value="<%= productId%>" />
	关联的IMEI码:
	<div id="IMEICheckDiv" style="margin-left:100px;width:200px;height:300px;overflow:scroll;">
		<%if( imeiList != null && imeiList.size() > 0 ) {
		
			for( int im = 0 ; im < imeiList.size(); im ++ ) {
				IMEIBuyStockinBean ibsBean = imeiList.get(im);
		%>
			<span name='IMEI_span' id='IMEI_<%= ibsBean.getIMEI()%>'><%= ibsBean.getIMEI()%></span><br/>
		<%
			}
		}
		%>
	</div>
<%
	}
}%>
<input type="hidden" name="buyStockinId" id="buyStockinId" value="<%=bean.getId()%>"/>
<input type="hidden" name="pageIndex" value="<%=paging.getCurrentPageIndex() %>"/>
</form>
<%if((bean.getStatus() == BuyStockinBean.STATUS0 || bean.getStatus() == BuyStockinBean.STATUS1 || bean.getStatus() == BuyStockinBean.STATUS2 || bean.getStatus() == BuyStockinBean.STATUS3 || bean.getStatus() == BuyStockinBean.STATUS5)&& (bianji||bean.getCreateUserId()==user.getId()) && !check){%>
	<% if( isMMBMobile ) { %>
	<br/>
	<br/>
	<br/>
	<div  >
	<input type="hidden" name="productId" id="productId" value="<%= productId%>" />
	<form method="post" onsubmit="return addIMEI();">
	请逐一扫描IMEI码计数：<input type="text" name="IMEI" id="IMEI"  />
	
	</form>
	<%if(group.isFlag(949)){ %>
	<form method="post" >
		批量添加IMEI码：<textarea cols="50" rows="4" id="multiIMEI" name="multiIMEI"></textarea>
		<input type="button" onclick="addMultiIMEI();" value="添加">
	</form>
	<%} %>
		<div style="margin-left:320px;">
			<input type="button" value="删除选中项" onclick="deleteIMEICode();" /> 
		</div>
	<div id="IMEICheckDiv" style="margin-left:100px;width:200px;height:300px;overflow:scroll;">
		<input type="checkbox" id="checkAll" onclick="checkAll();"/>全选<br/>
		<%if( imeiList != null && imeiList.size() > 0 ) {
		
			for( int im = 0 ; im < imeiList.size(); im ++ ) {
				IMEIBuyStockinBean ibsBean = imeiList.get(im);
		%>
			<span name='IMEI_span' id='IMEI_<%= ibsBean.getIMEI()%>'><input type='checkbox' name='IMEI_code' value='<%= ibsBean.getIMEI()%>' /><%= ibsBean.getIMEI()%><br/></span>
		<%
			}
		}
		%>
	</div>
	<input type="hidden" name="IMEI_count" id="IMEI_count" value="0"/>
</div>
	<script type="text/javascript" >
String.prototype.trim= function(){return this.replace(/(^\s*)|(\s*$)/g, "");}
	function  addIMEI() {
		var pattern = /^[0-9]{1,22}$/ ;
		var IMEI = $("#IMEI").val();
		IMEI = IMEI.trim();
		if( IMEI == null || IMEI == "" ) {
			alert("IMEI码不可以为空！");
			$("#IMEI").focus();
			return false;
		}
		if( !pattern.exec(IMEI) ) {
			alert("IMEI码只能是数字！");
			$("#IMEI").val("");
			$("#IMEI").focus();
			return false;
		}
		var temp =  document.getElementById("IMEI_" + IMEI);
		if( temp != null ) {
			alert("IMEI码已经存在！");
			$("#IMEI").val("");
			$("#IMEI").focus();
			return false;
		}
		
		var buyStockinId = $("#buyStockinId").val();
		var productId = $("#productId").val();
		$.post("<%= request.getContextPath()%>/admin/IMEI/addIMEI.mmx",
			{
				IMEICode:IMEI,
				productId:productId,
				buyStockinId:buyStockinId
			},
			function (data) {
				if( data['status'] == "success" ) {
					var ap = "<span name='IMEI_span' id='IMEI_"+IMEI+"'><input type='checkbox' name='IMEI_code' value='"+IMEI+"' />"+IMEI+"<br/></span>";
					var ht = $("#IMEICheckDiv").html();
					ht += ap;
					$("#IMEICheckDiv").html(ht);
					var countS = $("#stockinCount").val();
					var count  = parseInt(countS, 10);
					count ++;
					$("#stockinCount").val(count);
					$("#IMEI").val("");
					$("#IMEI").focus();
				} else {
					//alert(data);
					alert(data['tip']);
					$("#IMEI").val("");
					$("#IMEI").focus();
				}
			}
			,
			"json"
		);
		return false;
	}
	
	function  addMultiIMEI() {
		var pattern = /^[0-9]{1,22}$/ ;
		var multiIMEI = $("#multiIMEI").val();
		multiIMEI = multiIMEI.trim();
		if( multiIMEI == null || multiIMEI == "" ) {
			alert("IMEI码不可以为空！");
			$("#multiIMEI").focus();
			return false;
		}
		var str = multiIMEI.split("\n");
		var numlen = str.length;
		var count=0;
		for(var i=0;i<numlen;i+=1){
			if( !pattern.exec(trim(str[i])) ) {
				alert("IMEI码只能是数字！" + trim(str[i]) + "不符合！");
				$("#multiIMEI").val("");
				$("#multiIMEI").focus();
				return false;
			}
		}
		
		var buyStockinId = $("#buyStockinId").val();
		var productId = $("#productId").val();
		$.post("<%= request.getContextPath()%>/admin/IMEI/addMultiIMEI.mmx",
			{
				multiIMEICode:multiIMEI,
				productId:productId,
				buyStockinId:buyStockinId
			},
			function (data) {
				if( data['status'] == "success" ) {
					var ht = $("#IMEICheckDiv").html();
					var imeis = data['imeis'].split(",");
					var len = imeis.length;
					for (var i = 0; i < len; i ++) {
						ht = ht + "<span name='IMEI_span' id='IMEI_"+imeis[i]+"'><input type='checkbox' name='IMEI_code' value='"+imeis[i]+"' />"+imeis[i]+"<br/></span>";
					}
					$("#IMEICheckDiv").html(ht);
					var countS = $("#stockinCount").val();
					var count  = parseInt(countS, 10);
					count += len;
					$("#stockinCount").val(count);
					$("#multiIMEI").val("");
					$("#multiIMEI").focus();
				} else {
					alert(data['tip']);
					$("#multiIMEI").val("");
					$("#multiIMEI").focus();
				}
			}
			,
			"json"
		);
		return false;
	}
	
	//全选的操作
	function checkAll() {
		//alert("checkAll");
		var isCheckAll = $("#checkAll").attr("checked");
		//alert(isCheckAll);
		var cbxs = document.getElementsByName("IMEI_code");
		if( isCheckAll ) {
			for (var i  = 0 ; i < cbxs.length; i++ ) {
				var cbx = cbxs[i];
				//alert(cbx);
				cbx.checked="true";
			}
		} else {
			for (var i  = 0 ; i < cbxs.length; i++ ) {
				var cbx = cbxs[i];
				//alert(cbx);
				cbx.checked="";
			}
		}
	}
	//进行循环删除IMEI码的操作
	function deleteIMEICode() {
		var buyStockinId = $("#buyStockinId").val();
		var productId = $("#productId").val();
		var cbxs = document.getElementsByName("IMEI_code");
		var l = cbxs.length;
		var m = new Array();
		var j = 0;
		var params = "";
		for( var x = 0 ; x < l; x++ ) {
			var cbx = cbxs[x];
			var check = cbx.checked;
			if(  check ) {
				if( params.length == 0 ) {
					params += "?codes=" + cbx.value;
				} else {
					params += "&codes="+cbx.value;
				}
				m[j] = "IMEI_" + cbx.value;
				j++;
			} 
		}
		if( params.length == 0 ) {
			alert("没有要删除的项！");
			return;
		}
		//alert(params);
		$.post("<%= request.getContextPath()%>/admin/IMEI/deleteIMEIBuyStockin.mmx"+params,
			{
				productId:productId,
				buyStockinId:buyStockinId
			},
			function (data) {
				if( data['status'] == "success" ) {
					for( var i = 0 ; i < m.length; i++ ) {
						var f = document.getElementById(m[i]).parentNode;
						f.removeChild(document.getElementById(m[i]));
					}
					//更新数量
					var mCount = parseInt(data['count'], 10);
					var countS = $("#stockinCount").val();
					var count  = parseInt(countS, 10);
					count = count - mCount;
					$("#stockinCount").val(count)
				} else {
					alert(data['tip']);
				}
			}
			,
			"json"
		);
	}
</script>
	<% } %>
<%}%>

<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%}%>
<div style="left:270px;top:220px;width:280px;height:150px;position:absolute;background-color:#d0d0d0;border-style:solid;border-width:1px;border-color:#000000;padding:3px;display:none;" id="askComplete">
	<div style="width:272px;height:142px;background-color:#FFFFFF;;border-style:solid;border-width:1px;border-color:#000000;padding:3px;">
	<div align="center" style="margin-top:40px;"><b>请确认是否存在其他未完成的入库单！</b></div>
	
	<div style="margin-top:30px;margin-left:70px;">
	<button onclick="confirmNotComplete();">&nbsp;有&nbsp;</button>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<button onclick="confirmComplete();">&nbsp;无&nbsp;</button>
	</div>
	</div>
</div>