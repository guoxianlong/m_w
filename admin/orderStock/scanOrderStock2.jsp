<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.barcode.ProductBatchBarcodeBean" %>

<%
String soundSrcSJYC = Constants.WARE_SOUND_SJYC;
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

OrderStockAction action = new OrderStockAction();
action.orderStock(request, response);

//List productList = (List) request.getAttribute("productList");
List outList = (List) request.getAttribute("outList");
List productBatchBarcodeList = (List) request.getAttribute("productBatchBarcodeList");

OrderStockBean bean = (OrderStockBean) request.getAttribute("bean");
String gfExchange = (String) request.getAttribute("gfExchange");
String scanBack = request.getParameter("scanback");
scanBack=scanBack==null?"scanCheckOrderStock.jsp":scanBack;
int i;
voProduct product = null;
OrderStockProductBean sh = null;
Iterator itr = null;

int stockStatus = StringUtil.StringToId((String) request.getAttribute("stockStatus"));

boolean changeArea = false;
if(bean.getStatus() == OrderStockBean.STATUS1){
	changeArea = true;
} else {
	if(group.isFlag(33)){ //isShangpin){
		changeArea = true;
	}
}
%>
<html>
<head>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath() %>/admin/js/myalert.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/admin/js/autoHideAlert.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript">
$.ajax({
 	type: "GET",
 	url: "<%=request.getContextPath()%>/admin/scanOrderStock.do?selectIndex=11",
 	cache: false,
 	dataType: "html",
 	data: {type: "1"},
 	success: function(msg, reqStatus){
 		$("#performance").empty();
 		$("#performance").html(msg);
 	}
 });
//如果是已复核就跳转到原页面orderStock.jsp
<%if(bean.getStatus()==OrderStockBean.STATUS3){%>
document.location.href = "orderStock.jsp?id=<%=bean.getId()%>";
<%}%>
var productBatchCode = new Array();
var scanProductLog = new Array();
var productCodeArr = new Array();
var oldCode="";
function check(){
	if(confirm("请先详细核对一遍！")){
		return true;
	}
	return false;
}

function errorPlay() {
	Play('<%= soundSrcSJYC%>');
}

function  Play(sound) {
	 if(navigator.appName == "Microsoft Internet Explorer")
 	{
  		 var snd = document.createElement("bgsound");
        document.getElementsByTagName("body")[0].appendChild(snd);
        snd.src = sound;
	}
	else
	{
       var obj = document.createElement("object");
       obj.width="0px";
       obj.height="0px";
       obj.type = "audio/x-wav";
       obj.data = sound;            
       var body = document.getElementsByTagName("body")[0];
       body.appendChild(obj);
	}
}

function complete(){
	//alert(productBatchBarcodeToString(scanProductLog,1));
//权限验证
<%if(!group.isFlag(294)){%>
	alert("对不起，您没有复核调拨出库权限，无法操作！");
	return false;
<%}%>
	if(isValdatorCheckNum()){
		if(productBatchCode.length==0 && productCodeArr.length==0){
			customAlert("扫描数据异常，这可能是你在扫描过程中离开此页造成的，请重新扫描。");
			return ;
		}
		//if(confirm("确认？")){
			document.getElementById("productBatchCodeTA").value=productBatchBarcodeToString(productBatchCode,0);
			document.getElementById("scanProductBatchCodeTA").value=productBatchBarcodeToString(scanProductLog,1);
			$('#loadingDiv').show();
			document.getElementById("completeOrderStockForm").submit();
			return false;
		//}
	} else {
		onLoadMe();
	}
		// 移入焦点
	return false;
}

function confirmCodeValue() {
	if (document.getElementById("confirmCode").value == 'CONFIRM') {
		document.getElementById("confirmCode").value = '';
		resetCheck();
		closewin();
		return;
	} else {
		document.getElementById("confirmCode").value = '';
		document.getElementById("confirmCode").focus();
		return ;
	}
}
function productBatchBarcodeToString(codeArr,flag){
	var numLen = codeArr.length;
	var tmpArr = new Array();
	for(var i=0;i<numLen;i+=1){
		var tmpObj = codeArr[i];
		if(flag==0)
			tmpArr[i]=tmpObj.batchBarcode+","+tmpObj.batchCount;
		else{
			if(typeof(tmpObj)=="string")
				tmpArr[i]=tmpObj;
			else
				tmpArr[i]=tmpObj.pcode+","+tmpObj.scanStr+","+tmpObj.updateStr;
		}
	}
	return tmpArr.join("\n");
}
function confirm1(f, a){
	if(a == -1){
		f.submit();
	}
	else {
		if(a != f.area.value){
	        if(confirm("确认修改地区？库存将被更改！")){		
		        f.submit();
			}
	    }
		else {
			f.submit();
		}
	}
}

function trim(str){   
	return str.toString().replace(/^\s+|\s+$/g,"");   
}

/**
 * 点击+1按钮时白相应产品复核量加1
 */
function addOneClickHandler(pcode){
	document.getElementById("productCode").value=pcode;
	checkIMEI('');
}

function setTrBgcolor(colorStr){
	colorStr=colorStr==null?"#FFFFFF":colorStr;
	var tableObj = document.getElementById("mainTable");
	var rows = tableObj.rows;
	var rownum = rows.length;
	for(var i=1;i<rownum;i+=1){
		rows[i].bgColor=colorStr;
	}
}
function cleanIMEI(){
	$('#imeiId').val('');
}
/**
 * 验证IMEI码
 */
function checkIMEI(productCode,addCount){
	if (typeof addCount != "number") {
		addCount = 1;	
	}
	//如果没有未该方法传入productCode商品编号 则去取值	
	if(productCode == null || productCode == ''){
		productCode = document.getElementById("productCode").value;
	}
	if(trim(productCode).length==0){
		customAlert("请扫描商品条码或输入产品编号！");
		return false;
	}
	//var reg=new RegExp("[a-zA-Z]");
	if("<%= bean.getOrderCode() %>"==trim(productCode)){
		complete();
		return false;
	}else if("<%= bean.getCode() %>"==trim(productCode)){
		complete();
		return false;
	}
	var flag = false;
	$.ajax({
	 	type: "post",
	 	url: '<%=request.getContextPath()%>/admin/orderStockAction.do?method=checkIMEI',
	 	async: false,
	 	dataType: "json",
	 	data: {code: productCode},
	 	success: function(r){
	 		if(r.success){
	 			checkProduct(r.obj.productCode,r.obj.imeiId,addCount);
		 		flag = true;
	 		} else {
	 			customAlert(r.msg);
	 			flag = false;
	 		}
	 	}
	 });
	return flag;
}
/**
 * 验证已记录的IMEI码是否重复
 */
function checkRecordIMEI(imeiId){
	//记录IMEI码
	var imeiIds = $('#imeiId').val();
	var ids = [];
	if(imeiIds != null && imeiIds != ''){
		ids = imeiIds.split(',');
		for(var i=0;i < ids.length;i++){
			if(ids[i] == imeiId){
				customAlert("该IMEI码已经扫过啦!");
				return false;
			}
		}
	}
	return true;
}
/**
 * 扫描复核产品
 */

function checkProduct(productCodeValue,imeiId,addCount){
	//如果扫描了已经扫过的imei码则直接返回
	if(!checkRecordIMEI(imeiId)){
		return;	
	}
	var productBatchBarcdoeArr = document.getElementById("batchBarcodeTextArea").value.split("\n");
	var num = productBatchBarcdoeArr.length-1;
	setTrBgcolor(null);
	for(var step=0;step<num;step+=1){
		var tmpStr = productBatchBarcdoeArr[step];
		var tmpProBarcode = tmpStr.substring(0,tmpStr.lastIndexOf("."));
		var productId =parseInt(tmpStr.substring(tmpStr.lastIndexOf(".")+1));
		if(isProduct(tmpProBarcode,productCodeValue)){
			var trProduct = document.getElementById("tr"+productId);
			var checkNum = document.getElementById("productCheckNum"+productId);
			if(checkNum){
				var sumNum = parseInt(checkNum.value);
				checkNum.value=sumNum+=addCount;
				var vale = getBatchBarcodeCount(tmpProBarcode);
				vale+=addCount;
				//判断批次条码是否扫描
				if(!isProductBatchBarcode(productCodeValue,vale)){
					var pbb = new ProductBatchBarcode(productCodeValue,vale); 
					productBatchCode.push(pbb);
				}
				//扫描日志判断
				if(oldCode!=productCodeValue){
					var scanLog = new ProductScanLog(productCodeValue,vale-1+"-"+vale,"");
					scanProductLog.push(scanLog);
				}else{
					updateProductScanLog(productCodeValue,vale,0,true);
				}
				oldCode=productCodeValue;
				var stockOutCount = document.getElementById("stockOutCount"+productId).value;
				var oriname = document.getElementById("oriname"+productId).innerText;
				if(sumNum>stockOutCount){
					customAlert(oriname+" 的扫描量<font color='red'>"+sumNum+"</font>超出实际应复核量<font color='red'>"+stockOutCount+"</font>，请重新核实！");
					return;
				}else{
					// 移入焦点
					onLoadMe();
				}
				var str='<div style="background-color: #81BEF7;">当前扫描产品：'+oriname+'<br/>批次号：'+tmpProBarcode+'当前已复核数量：&nbsp;<input type="text" size="3" maxlength="7" id="enterNum" value="'+vale+'" <%=group.isFlag(402)?"":"readonly=readonly"%>/>&nbsp;<input type="button" value="修改复核量" <%=group.isFlag(402)?"":"disabled=\"disabled\""%>';
				str+=" onclick=\"updateCheckNum('productCheckNum"+productId+"','"+tmpProBarcode+"');\"/></div>";
				//alert(str);
				document.getElementById("scanProdDiv").innerHTML=str;
				trProduct.bgColor="#00FF00";
				return ;
			}			
		}else if(step==num-1){
			//document.getElementById("scanProdDiv").innerHTML="";
			//showDialog('newDiv1',240,130,'系统提示','',"没有找到对应的产品，请重新扫描！",onLoadMe);
		}
	}
	var flag = false;
	<%
	OrderStockProductBean tmpsh2 = null;
	int num2 = outList.size();
	for(int step1=0;step1<num2;step1+=1){
			tmpsh2 = (OrderStockProductBean)outList.get(step1);
	%>
	var tempCode = "<%=tmpsh2.getProductCode()%>";
	var productId =<%=tmpsh2.getProductId()%>;
	if(isProduct(tempCode,productCodeValue)){
		var checkNum = document.getElementById("productCheckNum"+productId);
		var trProduct = document.getElementById("tr"+productId);
		if(checkNum){
			var sumNum = parseInt(checkNum.value);
			checkNum.value=sumNum+=addCount;
			var vale = getProductCodeCount(tempCode);
			vale+=addCount;
			//判断批次条码是否扫描
			if(!isProductCode(tempCode,vale)){
				var pbb = new Object();
				pbb.code=tempCode;
				pbb.count=vale; 
				productCodeArr.push(pbb);
			}
			//扫描日志判断
			if(oldCode!=productCodeValue){
				var scanLog = new ProductScanLog(productCodeValue,vale-1+"-"+vale,"");
				scanProductLog.push(scanLog);
			}else{
				updateProductScanLog(productCodeValue,vale,0,true);
			}
			oldCode=productCodeValue;
			if(sumNum><%=tmpsh2.getStockoutCount()%>){
				customAlert("<%=tmpsh2.getProduct().getOriname().replace("\r","").replace("\n", "").replace("\"","“").replace("\'","‘")%> 的扫描量<font color='red'>"+sumNum+"</font>超出实际应复核量<font color='red'><%=tmpsh2.getStockoutCount()%></font>，请重新核实！");
				return false;
			}else{
				var imeiIds = $('#imeiId').val();
				if(imeiId != ''){
					if(imeiIds != ''){
						imeiIds = imeiIds + ',' + imeiId;
						$('#imeiId').val(imeiIds);
					}else{
						$('#imeiId').val(imeiId);
					}
				}
				// 移入焦点
				onLoadMe();
			}
			var str='<div style="background-color: #81BEF7;">当前扫描商品：<%=tmpsh2.getProduct().getOriname().replace("\r","").replace("\n", "").replace("\"","“").replace("\'","‘")%>&nbsp;&nbsp;当前已复核数量：<input type="text" size="3" maxlength="7" id="enterNum" value="'+vale+'" <%=group.isFlag(402)?"":"readonly=readonly"%>/><input type="button" value="修改复核量" <%=group.isFlag(402)?"":"disabled=\"disabled\""%> onclick="updateCheckNum(\'productCheckNum'+productId+'\',\''+tempCode+'\');"/></div>';
			document.getElementById("scanProdDiv").innerHTML=str;
			//checkNum.style.borderColor="#0000FF #00FF00";
			trProduct.bgColor="#00FF00";
			flag = true;
			return ;
		}
	} 
 <%}%>
	if (!flag){
		document.getElementById("scanProdDiv").innerHTML="";
		//showAlert("没有找到对应的产品，请重新扫描！");
		customAlert("没有找到对应的产品!");
		return ;
	}
}
/**
 * 修改产品复核量
 */
function updateCheckNum(procNumId,tmpBatchBarcode){
	var procNumObj = document.getElementById(procNumId);
	var enterNumObj = document.getElementById("enterNum");
	if(enterNumObj){
		var reg=new RegExp("^\\d{1,7}$");
		if(enterNumObj.value.length==0 || !reg.test(enterNumObj.value)){
			alert("只允许输入正整数或0！");
			enterNumObj.focus();
			return ;
		}else{
			var tempNum =0;			
			tempNum = enterNumObj.value - getProductCodeCount(tmpBatchBarcode);
			checkIMEI(tmpBatchBarcode,tempNum);
		}
	}
}
/**
 * 扫描日志修改复核量
 */
function updateProductScanLog(codeStr,count,updateCount,flag){
	var numLen = scanProductLog.length;
	for(var i=numLen-1;i>=0;i-=1){
		var tmpObj =  scanProductLog[i];
		if(tmpObj.pcode==codeStr){
			if(updateCount==0 && flag){
				var tmpCount = tmpObj.scanStr.substring(0,tmpObj.scanStr.indexOf("-"));
				tmpObj.scanStr=tmpCount+"-"+count;break;
			}else{
				var tmpCount ="";
				if(tmpObj.updateStr.length>0){var num = tmpObj.updateStr.indexOf("-");tmpCount = tmpObj.updateStr.substring(0,num);}
				else{var num2 = tmpObj.scanStr.lastIndexOf("-"); tmpCount = tmpObj.scanStr.substring(num2+1);}
				if(tmpCount!=""){
					tmpObj.updateStr=tmpCount+"-"+updateCount;
				}
				else
					tmpObj.updateStr=count+"-"+updateCount;
				break;
			}
		}
	}
}
/**
 * 根具产品条码或者产品编号判断是否存在
 */
function isProduct(target,productCode){
	if(target=="null")return false;
	if(target==productCode){
		return true;
	}
	return false;
}
/**
 * 产品批次条对象
 */
function ProductBatchBarcode(batchBarcode,batchCount){
	this.batchBarcode=batchBarcode;
	this.batchCount=batchCount;
}

/**
 * 产品扫描日照对象
 */
function ProductScanLog(pcode,scanStr,updateStr){
	this.pcode=pcode;
	this.scanStr=scanStr;
	this.updateStr=updateStr;
}

/**
 * 查找所有扫描的批次条码如果有修改相应的扫描量
 */
function isProductBatchBarcode(batchBarcode,batchCount){
	var numLen = productBatchCode.length;
	for(var i=0;i<numLen;i+=1){
		var tmpObj =  productBatchCode[i];
		if(tmpObj.batchBarcode==batchBarcode){
			tmpObj.batchCount=batchCount;
			return true;
		}
	}
	return false;
} 

/**
 * 查找所有扫描的产品编号如果有修改相应的扫描量
 */
function isProductCode(pcode,count){
	var numLen = productCodeArr.length;
	for(var i=0;i<numLen;i+=1){
		var tmpObj =  productCodeArr[i];
		if(tmpObj.code==pcode){
			tmpObj.count=count;
			return true;
		}
	}
	return false;
} 

/**
 * 查找相应产品编号的扫描量
 */
function getProductCodeCount(pcode){
	var numLen = productCodeArr.length;
	for(var i=0;i<numLen;i+=1){
		var tmpObj =  productCodeArr[i];
		if(tmpObj.code==pcode){
			return parseInt(tmpObj.count); 
		}
	}
	return 0;
} 
/**
* 得到产品总的扫描量
*/
function getBatchBarcodeSumCount(){
	  var numLen = productBatchCode.length;
	  var result=0;
		for(var i=0;i<numLen;i+=1){
			var tmpObj =  productBatchCode[i];
			result+=parseInt(tmpObj.batchCount);
		}
		return result;
}
var bindthePress=function(e) {
	e=e||window.event;
    var key = window.event ? e.keyCode : e.which;
    if (key.toString() == "13") {
    	confirmCodeValue();
        return false;
    }
}
/**
/**
 * 重新扫描复核商品
 */
function resetCheck(){
	cleanIMEI();//清除已记录imei码
	var productCNArr = document.getElementsByName("productCheckNum");
	if(productCNArr!=null &&  productCNArr.length>0){
		var step = productCNArr.length;
		for(var i=0;i<step;i+=1){
			productCNArr[i].value=0;
		}
	}
	document.getElementById("scanProdDiv").innerHTML="";
	productBatchCode = new Array();
	productCodeArr = new Array();
	scanProductLog.push("reset");
	oldCode="";
	setTrBgcolor(null);
	// 移入焦点
	onLoadMe();
}

/**
 * 判断复核量是否和出库量相等 
 */
 function isValdatorCheckNum(){
	 var productCNArr = document.getElementsByName("productCheckNum");
	 var productorinameArr = document.getElementsByName("productoriname");
	 // 出库量
	 var stockOutArr = document.getElementsByName("stockOutCount");
		if(productCNArr!=null &&  productCNArr.length>0){
			var step = productCNArr.length;
			for(var i=0;i<step;i+=1){
				if(productCNArr[i].value!=stockOutArr[i].value){
					customAlert(productorinameArr[i].value+" 的扫描量<font color='red'>"+productCNArr[i].value+"</font>与应复核量<font color='red'>"+stockOutArr[i].value+"</font>不一致，<br\>请重新核实！");
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
}

/**
 * 键盘按下回车事件
 */
 function onKeyDown(e){
	if(e.keyCode==13){
		checkIMEI('');
	}
}
</script>

<body  onpaste="return false">
<div id="performance" align="center"></div>
<p align="center">订单出货操作</p>
<form method="post" action="editOrderStock.jsp">
<a href="../order.do?id=<%= bean.getOrder().getId() %>&split=1">订单:<%= bean.getOrderCode() %></a><br/>
操作名称：<input type="text" name="name" size="40" value="<%=bean.getName()%>" />&nbsp;&nbsp;编号：<%= bean.getCode() %>&nbsp;&nbsp;状态：
<%if(bean.getStatus() == OrderStockBean.STATUS1){%>
<font color="red"><%=bean.getStatusName()%></font> 
<%if(stockStatus == 0){%><%= OrderStockBean.getStockStatusName(stockStatus) %><%} else {%><font color="red"><%= OrderStockBean.getStockStatusName(stockStatus) %></font><%}%>
<%--
<input type="button" name="btnStockout" value="确认申请出货" onclick="if(confirm('确定？')){this.disabled=true;window.location.href='completeOrderStock.jsp?operId=<%=bean.getId()%>&act=stockout';}" />
<a href="completeOrderStock.jsp?operId=<%=bean.getId()%>&act=stockout" onclick="return complete();">确认申请出货</a>
--%>
<%
}
//复核状态
else if(bean.getStatus() == OrderStockBean.STATUS6){%>
<font color="blue"><%=bean.getStatusName()%></font> 
<%if(group.isFlag(133)) /*if(!isXiaoshou && !isKefu)*/{ %>
<a href="javascript:void(0);" onclick="return complete();">复核完毕确认出货</a>
<%} %>
<%
}else{%><%=bean.getStatusName()%><%}%>&nbsp;<br/>
备注：<textarea name="remark" cols="30" rows="5" ><%=bean.getRemark()%></textarea>地区：<select name="area" <%= (!changeArea && (bean.getStatus() == OrderStockBean.STATUS1 || bean.getStatus() == OrderStockBean.STATUS2))?"disabled=\"disabled\"":"" %> ><option value="1" <%if(bean.getStockArea() == 1){%>selected<%}%>>广分</option><option value="2" <%if(bean.getStockArea() == 2){%>selected<%}%>>广速</option></select><input type="button" value="修改" <%if(bean.getStatus() == OrderStockBean.STATUS2){%>onclick="javascript:confirm1(document.forms[0], <%=bean.getStockArea()%>)"<%} else {%>onclick="javascript:confirm1(document.forms[0], -1)"<%}%>>注：在已完成状态下地区将不被更改。&nbsp;&nbsp;
<%if(gfExchange != null){ %><a href="../productStock/createStockExchange.jsp?type=2&srcId=<%= bean.getId() %>&stockInArea=1&stockInType=0&stockOutArea=2&stockOutType=0&forward=auto" target="_blank" >生成调拨单(广速-广分)</a><%}%><br/>
<p align="right">订单地址:<%= bean.getOrder().getAddress() %>&nbsp;&nbsp;&nbsp;&nbsp;</p>
<%if(!changeArea){ %>
<input type="hidden" name="area" value="<%=bean.getStockArea()%>"/>
<%} %>
<input type="hidden" name="id" value="<%=bean.getId()%>"/>
<input type="hidden" name="back" value="orderStock.jsp"/>
</form>

<fieldset>
	<legend>扫描复核区</legend>
		扫描产品条码或输入产品编号：<input type="text" maxlength="30" name="productCode" id="productCode" size="25"  onkeydown="onKeyDown(event);"> 
		<input type="button" onclick="checkIMEI('');" value="复核确认"> &nbsp;<input type="button" value="重新扫描" onclick="resetCheck();">
		&nbsp;<a href="<%=request.getContextPath() %>/admin/barcodeManager/ScanOrderHelp.jsp" target="_blank">扫描帮助</a>
		<div id="scanProdDiv" style="padding-top: 10px;"></div>
</fieldset>
<script type="text/javascript">
function onLoadMe(){
	var productCode = document.getElementById("productCode");
	productCode.value="";
	productCode.focus();
}
onLoadMe();
function onLoadMeNoClear(){
	var productCode = document.getElementById("productCode");
	productCode.focus();
}
</script>
<br/>
<form method="post" action="editOrderStock.jsp">
<table width="95%" border="1" cellpadding="0" cellspacing="0" id="mainTable" >
<tr>
  <td >序号</td>
  <td>产品名称</td>
  <td>产品原名称</td>
  <td>产品编号</td>
  <td>产品条码</td>
  <td>出库量</td>
  <td>复核量</td>
  <td>当前北库库存</td>
  <td>当前广分库存</td>
  <td>当前广速库存</td>
  <td>查进销存</td>
  <td>操作</td>
</tr>
<%

int stockOutCount = 0;
OrderStockProductBean outSh = null;
itr = outList.iterator();
i = 0;
while(itr.hasNext()){
	i++;
	sh = (OrderStockProductBean) itr.next();
	stockOutCount = sh.getStockoutCount();
	outSh = sh;
%>
<tr id="tr<%=sh.getProductId() %>">
  <td><%= i %></td>
  <td><%=sh.getProduct().getName()%></td>
  <td id="oriname<%=sh.getProduct().getId() %>"><a onmouseover="displaydata(event,'<%=sh.getProduct().getId()%>')"  onmouseout="disappear();" href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getOriname().replace("\r","").replace("\n","").replace("\"","”").replace("\'","‘")%></a></td>
  <td><%=sh.getProduct().getCode()%></td>
  <td><%=(sh.getProductBarcodeVO()==null ||sh.getProductBarcodeVO().getBarcode()==null)?"":sh.getProductBarcodeVO().getBarcode()%></td>
  <td><input type="text" name="stockOutCount<%=sh.getProduct().getId()%>" size="5" value="<%=stockOutCount%>" readonly/>
  	<input type="hidden" name="stockOutCount" value="<%=stockOutCount%>" id="stockOutCount<%=sh.getProduct().getId()%>"/>
  </td>
  <td><input type="text" size="5" value="0" readonly id="productCheckNum<%=sh.getProduct().getId()%>"  name="productCheckNum"/><input type="hidden" id="productoriname<%=sh.getProduct().getId()%>"  name="productoriname" value="<%=sh.getProduct().getOriname()%>"/></td>
  <td><%=sh.getProduct().getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><%=sh.getProduct().getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>
  <td><%=sh.getProduct().getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)%></td>  
  <td><a href="../productStock/stockCardList.jsp?productCode=<%= sh.getProduct().getCode() %>" target="_blank">查</a></td>
  <td><%if(group.isFlag(402)){ %><input type="button" value="+1" onclick="addOneClickHandler('<%=sh.getProductCode()%>');"/><%} %></td>
</tr>
<%
}
%>
</table>
<input type="hidden" name="operId" value="<%=bean.getId()%>" />
</form>
<p align="center"><a href="stockAdminHistory.jsp?operId=<%=bean.getId()%>&logType=10" target="_blank">人员操作记录</a>|<a href="orderStockList.jsp">返回订单出货操作记录列表</a>|<a href="orderStockPrint.jsp?id=<%=bean.getId()%>" target="_blank">导出列表</a></p>
<textarea id="batchBarcodeTextArea" style="display: none;">
<%if(productBatchBarcodeList!=null && productBatchBarcodeList.size()>0){
	System.out.println(productBatchBarcodeList.size());
	for(int step=0;step<productBatchBarcodeList.size();step+=1){
		ProductBatchBarcodeBean tmpBean = (ProductBatchBarcodeBean)productBatchBarcodeList.get(step);
		out.println(tmpBean.getBatchBarcode()+"."+tmpBean.getProductId());
	}}%></textarea>
<form action="completeOrderStock.jsp" id="completeOrderStockForm" style="display: none;" method="post">
<textarea id="productBatchCodeTA" name="productBatchCodeName"></textarea>
<textarea id="scanProductBatchCodeTA" name="scanProductBatcodeName"></textarea>
<input type="hidden" name="operId" value="<%=bean.getId()%>">
<input type="hidden" name="act" value="confirm">
<input type="hidden" name="back" value="<%=scanBack %>">
<input type="hidden" name="action" value="completeOrderStock2">
<input type="hidden" name="orderCode" value="<%=bean.getOrderCode() %>">
<input type="hidden" id="imeiId" name="imeiId" value="">
</form>	
 <script type="text/javascript" language="javascript" >
  function displaydata(event,pid){
	 var boxObj = document.getElementById("divTip1");
	 var productBatchBarcdoeArr = document.getElementById("batchBarcodeTextArea").value.split("\n");
	 var num = productBatchBarcdoeArr.length-1;
	 var htmlstr="<table><tr><td width='150'>产品批次条码</td> <td width='50'>复核量</td> </tr>";
	 for(var i=0;i<num;i+=1){
		 var tmpBarcode = productBatchBarcdoeArr[i];
		 if(tmpBarcode.lastIndexOf(pid)!=-1){
			 var tb = tmpBarcode.substring(0,tmpBarcode.lastIndexOf(".")); 
			 var tbcount = getBatchBarcodeCount(tb);
			 htmlstr+="<tr><td>"+tb+"</td><td>"+tbcount+"</td></tr>";
		 }
	 }
	 htmlstr+='</table>';
	 //var addRow = boxObj.insertRow(obj.rowIndex+1);
	 //var addCell = addRow.insertCell(0);
	 //addRow.bgColor="#F0F0F0";
	 //addCell.colSpan=10;
	 //addCell.align="center";
	 //addCell.innerHTML=htmlstr;
 	 boxObj.style.left=event.clientX+document.body.scrollLeft;
 	 boxObj.style.top=event.clientY+document.body.scrollTop;
	 boxObj.innerHTML=htmlstr;
	 boxObj.style.visibility="visible"; 
  }
  
  function display(){
	document.getElementById("divTip1").style.visibility="visible"; 
  }
  function disappear(){
	 document.getElementById("divTip1").style.visibility="hidden"; 
	// var boxObj = document.getElementById("contentTable");
	//  boxObj.deleteRow(obj.rowIndex+1);
  }
  
  function getBatchBarcodeCount(batchCode){
	  var numLen = productBatchCode.length;
		for(var i=0;i<numLen;i+=1){
			var tmpObj =  productBatchCode[i];
			if(tmpObj.batchBarcode==batchCode){
				return tmpObj.batchCount;
			}
		}
	return 0;
  }
 </script>
<div id="divTip1" style="background-color:Yellow; position:absolute; visibility:hidden; padding:5px; width: 200;" onmouseover="display()" onmouseout="disappear()"></div>
<div id="soundDiv" ></div>
<div id="loadingDiv" style="z-index:9999;background-color: #FFF;opacity:0.5;filter:Alpha(Opacity=50);width:100%;top:0;left:0; height:100%;display:none;position:absolute;" align="center">
	<img style="" src="<%=request.getContextPath()%>/images/loading.gif" />
</div>
</body>
</html>