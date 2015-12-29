<%@page import="adultadmin.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoOperationProcessBean"%>
<%@page import="adultadmin.bean.cargo.CargoDeptBean"%>
<%@page import="adultadmin.bean.cargo.CargoInfoStorageBean"%><html>

<%

List processListS=(List)request.getAttribute("processListS");
List processListX=(List)request.getAttribute("processListX");
List processListB=(List)request.getAttribute("processListB");
List processListD=(List)request.getAttribute("processListD");
List storageList=(List)request.getAttribute("storageList");
List deptList0=(List)request.getAttribute("deptList0");
List deptList1=(List)request.getAttribute("deptList1");
int operationType = StringUtil.StringToId(request.getParameter("operationType"));
int useStatusCountS=Integer.parseInt(request.getAttribute("useStatusCountS").toString());
int useStatusCountX=Integer.parseInt(request.getAttribute("useStatusCountX").toString());
int useStatusCountB=Integer.parseInt(request.getAttribute("useStatusCountB").toString());
int useStatusCountD=Integer.parseInt(request.getAttribute("useStatusCountD").toString());
%>

<head>
<title>合格库作业操作及时效设置</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
function changeOperationType(){
	var operationType=document.getElementById("operationType").selectedIndex;
	var operationType1=document.getElementById("operationType1");
	var operationType2=document.getElementById("operationType2");
	var operationType3=document.getElementById("operationType3");
	var operationType4=document.getElementById("operationType4");
	if(operationType==1){
		operationType1.style.display="block";
		operationType2.style.display="none";
		operationType3.style.display="none";
		operationType4.style.display="none";
	}else if(operationType==2){
		operationType1.style.display="none";
		operationType2.style.display="block";
		operationType3.style.display="none";
		operationType4.style.display="none";
	}else if(operationType==3){
		operationType1.style.display="none";
		operationType2.style.display="none";
		operationType3.style.display="block";
		operationType4.style.display="none";
	}else if(operationType==4){
		operationType1.style.display="none";
		operationType2.style.display="none";
		operationType3.style.display="none";
		operationType4.style.display="block";
	}else if(operationType==0){
		operationType1.style.display="none";
		operationType2.style.display="none";
		operationType3.style.display="none";
		operationType4.style.display="none";
	}
}
function changeProcessCount(str){
	var changeProcessCount=document.getElementById("changeProcessCount"+str);
	var selectedIndex=changeProcessCount.selectedIndex;
	var process1=document.getElementById("process1"+str);
	var process2=document.getElementById("process2"+str);
	var process3=document.getElementById("process3"+str);
	var process4=document.getElementById("process4"+str);
	if(selectedIndex==0){
		process1.style.display="block";
		process2.style.display="none";
		process3.style.display="none";
		process4.style.display="none";
	}else if(selectedIndex==1){
		process1.style.display="block";
		process2.style.display="block";
		process3.style.display="none";
		process4.style.display="none";
	}else if(selectedIndex==2){
		process1.style.display="block";
		process2.style.display="block";
		process3.style.display="block";
		process4.style.display="none";
	}else if(selectedIndex==3){
		process1.style.display="block";
		process2.style.display="block";
		process3.style.display="block";
		process4.style.display="block";
	}
}
function checkSubmit1(form){
	var reg=/^[0-9]*$/;
	var effectTime1=trim(form.effectTime1.value);
	var effectTime2=trim(form.effectTime2.value);
	if(effectTime1==""||effectTime2==""){
		alert("请输入时效状态！");
		return false;
	}
	if(effectTime1!=""&&!reg.exec(effectTime1)){
		alert("时效状态只能输入数字！");
		return false;
	}
	if(effectTime2!=""&&!reg.exec(effectTime2)){
		alert("时效状态只能输入数字！");
		return false;
	}
	if(effectTime1.length>10||effectTime2.length>10){
		alert("时效状态最多输入10位！");
		return false;
	}
	return false;
}
function checkSubmit2(form){
	var processCount=form.processCount.selectedIndex+1;
	var reg=/^[0-9]*$/;
	if(processCount>0){
		var operName1=trim(form.operName1.value);
		if(operName1==""){
			alert("请输入人员操作名称！");
			return false;
		}
		var statusName1=trim(form.statusName1.value);
		if(statusName1==""){
			alert("请输入作业状态名称！");
			return false;
		}
		var effectTime3=trim(form.effectTime3.value);
		if(effectTime3==""){
			alert("请输入时效状态！");
			return false;
		}
		if(effectTime3!=""&&!reg.exec(effectTime3)){
			alert("时效状态只能输入数字！");
			return false;
		}
		if(effectTime3.length>10){
			alert("时效状态最多输入10位！");
			return false;
		}
		var deptId1=form.deptId1.options[form.deptId1.selectedIndex].value;
		if(deptId1==""){
			alert("请选择一级部门！");
			return false;
		}
		var deptId2=form.deptId2.options[form.deptId2.selectedIndex].value;
		if(deptId2==""){
			alert("请选择二级部门！");
			return false;
		}
		var storageId1=form.storageId1.options[form.storageId1.selectedIndex].value;
		if(storageId1==""){
			alert("请选择所属仓库！");
			return false;
		}
	}
	if(processCount>1){
		var operName2=trim(form.operName2.value);
		if(operName2==""){
			alert("请输入人员操作名称！");
			return false;
		}
		var statusName2=trim(form.statusName2.value);
		if(statusName2==""){
			alert("请输入作业状态名称！");
			return false;
		}
		var effectTime4=trim(form.effectTime4.value);
		if(effectTime4==""){
			alert("请输入时效状态！");
			return false;
		}
		if(effectTime4!=""&&!reg.exec(effectTime4)){
			alert("时效状态只能输入数字！");
			return false;
		}
		if(effectTime4.length>10){
			alert("时效状态最多输入10位！");
			return false;
		}
		var deptId3=form.deptId3.options[form.deptId3.selectedIndex].value;
		if(deptId3==""){
			alert("请选择一级部门！");
			return false;
		}
		var deptId4=form.deptId4.options[form.deptId4.selectedIndex].value;
		if(deptId4==""){
			alert("请选择二级部门！");
			return false;
		}
		var storageId2=form.storageId2.options[form.storageId2.selectedIndex].value;
		if(storageId2==""){
			alert("请选择所属仓库！");
			return false;
		}
	}
	if(processCount>2){
		var operName3=trim(form.operName3.value);
		if(operName3==""){
			alert("请输入人员操作名称！");
			return false;
		}
		var statusName3=trim(form.statusName3.value);
		if(statusName3==""){
			alert("请输入作业状态名称！");
			return false;
		}
		var effectTime5=trim(form.effectTime5.value);
		if(effectTime5==""){
			alert("请输入时效状态！");
			return false;
		}
		if(effectTime5!=""&&!reg.exec(effectTime5)){
			alert("时效状态只能输入数字！");
			return false;
		}
		if(effectTime5.length>10){
			alert("时效状态最多输入10位！");
			return false;
		}
		var deptId5=form.deptId5.options[form.deptId5.selectedIndex].value;
		if(deptId5==""){
			alert("请选择一级部门！");
			return false;
		}
		var deptId6=form.deptId6.options[form.deptId6.selectedIndex].value;
		if(deptId6==""){
			alert("请选择二级部门！");
			return false;
		}
		var storageId3=form.storageId3.options[form.storageId3.selectedIndex].value;
		if(storageId3==""){
			alert("请选择所属仓库！");
			return false;
		}
	}
	if(processCount>3){
		var operName4=trim(form.operName4.value);
		if(operName4==""){
			alert("请输入人员操作名称！");
			return false;
		}
		var statusName4=trim(form.statusName4.value);
		if(statusName4==""){
			alert("请输入作业状态名称！");
			return false;
		}
		var effectTime6=trim(form.effectTime6.value);
		if(effectTime6==""){
			alert("请输入时效状态！");
			return false;
		}
		if(effectTime6!=""&&!reg.exec(effectTime6)){
			alert("时效状态只能输入数字！");
			return false;
		}
		if(effectTime6.length>10){
			alert("时效状态最多输入10位！");
			return false;
		}
		var deptId7=form.deptId7.options[form.deptId7.selectedIndex].value;
		if(deptId7==""){
			alert("请选择一级部门！");
			return false;
		}
		var deptId8=form.deptId8.options[form.deptId8.selectedIndex].value;
		if(deptId8==""){
			alert("请选择二级部门！");
			return false;
		}
		var storageId4=form.storageId4.options[form.storageId4.selectedIndex].value;
		if(storageId4==""){
			alert("请选择所属仓库！");
			return false;
		}
	}
	return true;
}
function checkSubmit3(form){
	var effectTime7=trim(form.effectTime7.value);
	var reg=/^[0-9]*$/;
	if(effectTime7==""){
		alert("请输入时效状态！");
		return false;
	}
	if(effectTime7!=""&&!reg.exec(effectTime7)){
		alert("时效状态只能输入数字！");
		return false;
	}
	if(effectTime7.length>10){
		alert("时效状态最多输入10位！");
		return false;
	}
	return true;
}
function process(){
	var changeProcessCountS=document.getElementById("changeProcessCountS");
	changeProcessCountS.options[<%=useStatusCountS-1%>].selected=true;
	changeProcessCount("S");
	var changeProcessCountX=document.getElementById("changeProcessCountX");
	changeProcessCountX.options[<%=useStatusCountX-1%>].selected=true;
	changeProcessCount("X");
	var changeProcessCountB=document.getElementById("changeProcessCountB");
	changeProcessCountB.options[<%=useStatusCountB-1%>].selected=true;
	changeProcessCount("B");
	var changeProcessCountD=document.getElementById("changeProcessCountD");
	changeProcessCountD.options[<%=useStatusCountD-1%>].selected=true;
	changeProcessCount("D");
}
function changeDept0(id0,name,id3,dept1,dept2){
	var deptId0=document.getElementById(id0).options[document.getElementById(id0).selectedIndex].value;
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=3&deptId0="+deptId0+"&name="+name+"&dept1="+dept1,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById(id3).innerHTML = msg;
		}
	});
}


function changeDept2(id1,name,id4,dept2){
	var deptId=document.getElementById(id1).options[document.getElementById(id1).selectedIndex].value;
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=3&deptId="+deptId+"&name="+name+"&dept2="+dept2,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById(id4).innerHTML = msg;
		}
	});
}

function changeDept1(id1,name,id3,dept2){
	var deptId=document.getElementById(id1).options[document.getElementById(id1).selectedIndex].value;
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=3&deptId="+deptId+"&name="+name+"&dept2="+dept2,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById(id3).innerHTML = msg;
		}
	});
}
</script>
</head>
<body onload="process();">
<h3>合格库作业操作及时效设置</h3>
<div>
	作业类型设置：
	<select id="operationType" onchange="changeOperationType();">
		<option value="0">请选择</option>
		<option value="1">上架作业</option>
		<option value="2">下架作业</option>
		<option value="3">补货作业</option>
		<option value="4">调拨作业</option>
	</select>
	
</div>
<br/>
<div id="operationType1" style="display: none;">
	作业单操作流程设置：上架单<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit1(this)">
		作业生成阶段：<input type="submit" value="保存设置" />
		<div>
			人员操作：<%=((CargoOperationProcessBean)processListS.get(0)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListS.get(0)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime1" value="<%=((CargoOperationProcessBean)processListS.get(0)).getEffectTime() %>"/>分钟
			<br/>
			人员操作：<%=((CargoOperationProcessBean)processListS.get(1)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListS.get(1)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime2" value="<%=((CargoOperationProcessBean)processListS.get(1)).getEffectTime() %>"/>分钟
			<br/>
		</div>
		<input type="hidden" name="formIndex" value="1"/>
		<input type="hidden" name="operationType" value="1"/>
	</form>
	<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit2(this)">
		作业交接阶段：
		<select id="changeProcessCountS" name="processCount" onchange="changeProcessCount('S');">
			<option value="1">一阶流程</option>
			<option value="2">二阶流程</option>
			<option value="3">三阶流程</option>
			<option value="4">四阶流程</option>
		</select>
		<input type="submit" value="保存设置"/>
		<div id="processS_1" style="display:block;">
			作业交接阶段一<br/>
			人员操作：<input type="text" name="operName1" value="<%=((CargoOperationProcessBean)processListS.get(2)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName1" value="<%=((CargoOperationProcessBean)processListS.get(2)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime3" value="<%=((CargoOperationProcessBean)processListS.get(2)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId0S" name="deptId0" onchange="changeNextOption0('deptId0S', 'deptId1S','deptId2S', 2);">
				<option value="-1">选择地区部门</option>
				<%for(int i=0;i<deptList0.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList0.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(2)).getDeptId0()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<select id="deptId1S" name="deptId1" onchange="">
				<OPTION selected value="-1">一级部门</OPTION>
			</select>
			<select id="deptId2S" name="deptId2">
				<option value="-1">二级部门</option>
			</select>
			<br/>
			操作方式：
			<input type="radio" name="handleType1" value="0" <%if(((CargoOperationProcessBean)processListS.get(2)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType1" value="1" <%if(((CargoOperationProcessBean)processListS.get(2)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId1">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(2)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType1" value="1" <%if(((CargoOperationProcessBean)processListS.get(2)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="2" <%if(((CargoOperationProcessBean)processListS.get(2)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="0" <%if(((CargoOperationProcessBean)processListS.get(2)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId1S','deptId2','deptId2S',<%=((CargoOperationProcessBean)processListS.get(2)).getDeptId2()%>);</script>
		</div>
		<div id="process2S" style="display:none;">
			作业交接阶段二<br/>
			人员操作：<input type="text" name="operName2" value="<%=((CargoOperationProcessBean)processListS.get(3)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName2" value="<%=((CargoOperationProcessBean)processListS.get(3)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime4" value="<%=((CargoOperationProcessBean)processListS.get(3)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId3S" name="deptId3" onchange="changeDept('deptId3S','deptId4','deptId4S',<%=((CargoOperationProcessBean)processListS.get(3)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(3)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId4S">
			<select name="deptId4">
				<option value="4">二级部门</option>
			</select>
			</span>
			
			<select id="deptId3S" name="deptId3" onchange="changeDept('deptId3S','deptId4','deptId4S',<%=((CargoOperationProcessBean)processListS.get(3)).getDeptId1()%>);">
				<option value="">选择地区部门</option>
				<%for(int i=0;i<deptList0.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList0.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(2)).getDeptId0()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId4S">
			<select id="deptId4S" name="deptId4" onchange="">
				<OPTION selected value="">一级部门</OPTION>
			</select>
			</span>
			<span id="deptId5S">
			<select name="deptId5">
				<option value="4">二级部门</option>
			</select>
			</span>
			
			<br/>
			操作方式：
			<input type="radio" name="handleType2" value="0" <%if(((CargoOperationProcessBean)processListS.get(3)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType2" value="1" <%if(((CargoOperationProcessBean)processListS.get(3)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId2">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(3)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType2" value="1" <%if(((CargoOperationProcessBean)processListS.get(3)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="2" <%if(((CargoOperationProcessBean)processListS.get(3)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="0" <%if(((CargoOperationProcessBean)processListS.get(3)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId3S','deptId4','deptId4S',<%=((CargoOperationProcessBean)processListS.get(3)).getDeptId2()%>);</script>
		</div>
		<div id="process3S" style="display:none;">
			作业交接阶段三<br/>
			人员操作：<input type="text" name="operName3" value="<%=((CargoOperationProcessBean)processListS.get(4)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName3" value="<%=((CargoOperationProcessBean)processListS.get(4)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime5" value="<%=((CargoOperationProcessBean)processListS.get(4)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId5S" name="deptId5" onchange="changeDept('deptId5S','deptId6','deptId6S',<%=((CargoOperationProcessBean)processListS.get(4)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(4)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId6S">
			<select name="deptId6">
				<option value="4">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType3" value="0" <%if(((CargoOperationProcessBean)processListS.get(4)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType3" value="1" <%if(((CargoOperationProcessBean)processListS.get(4)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId3">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(4)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType3" value="1" <%if(((CargoOperationProcessBean)processListS.get(4)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="2" <%if(((CargoOperationProcessBean)processListS.get(4)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="0" <%if(((CargoOperationProcessBean)processListS.get(4)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId5S','deptId6','deptId6S',<%=((CargoOperationProcessBean)processListS.get(4)).getDeptId2()%>);</script>
		</div>
		<div id="process4S" style="display:none;">
			作业交接阶段四<br/>
			人员操作：<input type="text" name="operName4" value="<%=((CargoOperationProcessBean)processListS.get(5)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName4" value="<%=((CargoOperationProcessBean)processListS.get(5)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime6" value="<%=((CargoOperationProcessBean)processListS.get(5)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId7S" name="deptId7" onchange="changeDept('deptId7S','deptId8','deptId8S',<%=((CargoOperationProcessBean)processListS.get(5)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(5)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId8S">
			<select name="deptId8">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType4" value="0" <%if(((CargoOperationProcessBean)processListS.get(5)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType4" value="1" <%if(((CargoOperationProcessBean)processListS.get(5)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId4">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListS.get(5)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType4" value="1" <%if(((CargoOperationProcessBean)processListS.get(5)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="2" <%if(((CargoOperationProcessBean)processListS.get(5)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="0" <%if(((CargoOperationProcessBean)processListS.get(5)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId7S','deptId8','deptId8S',<%=((CargoOperationProcessBean)processListS.get(5)).getDeptId2()%>);</script>
		</div>
		<input type="hidden" name="formIndex" value="2"/>
		<input type="hidden" name="operationType" value="1"/>
	</form>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit3(this)">
		作业完成阶段：<input type="submit" value="保存设置"/>
		<div>
			人员操作：作业完成&nbsp;&nbsp;作业状态：作业结束&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime7" value="<%=((CargoOperationProcessBean)processListS.get(6)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
		</div>
		<input type="hidden" name="formIndex" value="3"/>
		<input type="hidden" name="operationType" value="1"/>
	</form>
</div>
<div id="operationType2" style="display: none;">
	作业单操作流程设置：下架单<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit1(this)">
		作业生成阶段：<input type="submit" value="保存设置" />
		<div>
			人员操作：<%=((CargoOperationProcessBean)processListX.get(0)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListX.get(0)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime1" value="<%=((CargoOperationProcessBean)processListX.get(0)).getEffectTime() %>"/>分钟
			<br/>
			人员操作：<%=((CargoOperationProcessBean)processListX.get(1)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListX.get(1)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime2" value="<%=((CargoOperationProcessBean)processListX.get(1)).getEffectTime() %>"/>分钟
			<br/>
		</div>
		<input type="hidden" name="formIndex" value="1"/>
		<input type="hidden" name="operationType" value="2"/>
	</form>
	<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit2(this)">
		作业交接阶段：
		<select id="changeProcessCountX" name="processCount" onchange="changeProcessCount('X');">
			<option value="1">一阶流程</option>
			<option value="2">二阶流程</option>
			<option value="3">三阶流程</option>
			<option value="4">四阶流程</option>
		</select>
		<input type="submit" value="保存设置"/>
		<div id="process1X" style="display:block;">
			作业交接阶段一<br/>
			人员操作：<input type="text" name="operName1" value="<%=((CargoOperationProcessBean)processListX.get(2)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName1" value="<%=((CargoOperationProcessBean)processListX.get(2)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime3" value="<%=((CargoOperationProcessBean)processListX.get(2)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId1X" name="deptId1" onchange="changeDept('deptId1X','deptId2','deptId2X',<%=((CargoOperationProcessBean)processListX.get(2)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(2)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId2X">
			<select name="deptId2">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType1" value="0" <%if(((CargoOperationProcessBean)processListX.get(2)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType1" value="1" <%if(((CargoOperationProcessBean)processListX.get(2)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId1">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(2)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType1" value="1" <%if(((CargoOperationProcessBean)processListX.get(2)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="2" <%if(((CargoOperationProcessBean)processListX.get(2)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="0" <%if(((CargoOperationProcessBean)processListX.get(2)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId1X','deptId2','deptId2X',<%=((CargoOperationProcessBean)processListX.get(2)).getDeptId2()%>);</script>
		</div>
		<div id="process2X" style="display:none;">
			作业交接阶段二<br/>
			人员操作：<input type="text" name="operName2" value="<%=((CargoOperationProcessBean)processListX.get(3)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName2" value="<%=((CargoOperationProcessBean)processListX.get(3)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime4" value="<%=((CargoOperationProcessBean)processListX.get(3)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId3X" name="deptId3" onchange="changeDept('deptId3X','deptId4','deptId4X',<%=((CargoOperationProcessBean)processListX.get(3)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(3)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId4X">
			<select name="deptId4">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType2" value="0" <%if(((CargoOperationProcessBean)processListX.get(3)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType2" value="1" <%if(((CargoOperationProcessBean)processListX.get(3)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId2">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(3)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType2" value="1" <%if(((CargoOperationProcessBean)processListX.get(3)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="2" <%if(((CargoOperationProcessBean)processListX.get(3)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="0" <%if(((CargoOperationProcessBean)processListX.get(3)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId3X','deptId4','deptId4X',<%=((CargoOperationProcessBean)processListX.get(3)).getDeptId2()%>);</script>
		</div>
		<div id="process3X" style="display:none;">
			作业交接阶段三<br/>
			人员操作：<input type="text" name="operName3" value="<%=((CargoOperationProcessBean)processListX.get(4)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName3" value="<%=((CargoOperationProcessBean)processListX.get(4)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime5" value="<%=((CargoOperationProcessBean)processListX.get(4)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId5X" name="deptId5" onchange="changeDept('deptId5X','deptId6','deptId6X',<%=((CargoOperationProcessBean)processListX.get(4)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(4)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId6X">
			<select name="deptId6">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType3" value="0" <%if(((CargoOperationProcessBean)processListX.get(4)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType3" value="1" <%if(((CargoOperationProcessBean)processListX.get(4)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId3">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(4)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType3" value="1" <%if(((CargoOperationProcessBean)processListX.get(4)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="2" <%if(((CargoOperationProcessBean)processListX.get(4)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="0" <%if(((CargoOperationProcessBean)processListX.get(4)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId5X','deptId6','deptId6X',<%=((CargoOperationProcessBean)processListX.get(4)).getDeptId2()%>);</script>
		</div>
		<div id="process4X" style="display:none;">
			作业交接阶段四<br/>
			人员操作：<input type="text" name="operName4" value="<%=((CargoOperationProcessBean)processListX.get(5)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName4" value="<%=((CargoOperationProcessBean)processListX.get(5)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime6" value="<%=((CargoOperationProcessBean)processListX.get(5)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId7X" name="deptId7" onchange="changeDept('deptId7X','deptId8','deptId8X',<%=((CargoOperationProcessBean)processListX.get(5)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(5)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId8X">
			<select name="deptId8">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType4" value="0" <%if(((CargoOperationProcessBean)processListX.get(5)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType4" value="1" <%if(((CargoOperationProcessBean)processListX.get(5)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId4">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListX.get(5)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType4" value="1" <%if(((CargoOperationProcessBean)processListX.get(5)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="2" <%if(((CargoOperationProcessBean)processListX.get(5)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="0" <%if(((CargoOperationProcessBean)processListX.get(5)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId7X','deptId8','deptId8X',<%=((CargoOperationProcessBean)processListX.get(5)).getDeptId2()%>);</script>
		</div>
		<input type="hidden" name="formIndex" value="2"/>
		<input type="hidden" name="operationType" value="2"/>
	</form>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit3(this)">
		作业完成阶段：<input type="submit" value="保存设置"/>
		<div>
			人员操作：作业完成&nbsp;&nbsp;作业状态：作业结束&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime7" value="<%=((CargoOperationProcessBean)processListX.get(6)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
		</div>
		<input type="hidden" name="formIndex" value="3"/>
		<input type="hidden" name="operationType" value="2"/>
	</form>
</div>
<div id="operationType3" style="display: none;">
	作业单操作流程设置：补货单<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit1(this)">
		作业生成阶段：<input type="submit" value="保存设置" />
		<div>
			人员操作：<%=((CargoOperationProcessBean)processListB.get(0)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListB.get(0)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime1" value="<%=((CargoOperationProcessBean)processListB.get(0)).getEffectTime() %>"/>分钟
			<br/>
			人员操作：<%=((CargoOperationProcessBean)processListB.get(1)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListB.get(1)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime2" value="<%=((CargoOperationProcessBean)processListB.get(1)).getEffectTime() %>"/>分钟
			<br/>
		</div>
		<input type="hidden" name="formIndex" value="1"/>
		<input type="hidden" name="operationType" value="3"/>
	</form>
	<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit2(this)">
		作业交接阶段：
		<select id="changeProcessCountB" name="processCount" onchange="changeProcessCount('B');">
			<option value="1">一阶流程</option>
			<option value="2">二阶流程</option>
			<option value="3">三阶流程</option>
			<option value="4">四阶流程</option>
		</select>
		<input type="submit" value="保存设置"/>
		<div id="process1B" style="display:block;">
			作业交接阶段一<br/>
			人员操作：<input type="text" name="operName1" value="<%=((CargoOperationProcessBean)processListB.get(2)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName1" value="<%=((CargoOperationProcessBean)processListB.get(2)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime3" value="<%=((CargoOperationProcessBean)processListB.get(2)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId1B" name="deptId1" onchange="changeDept('deptId1B','deptId2','deptId2B',<%=((CargoOperationProcessBean)processListB.get(2)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(2)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId2B">
			<select name="deptId2">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType1" value="0" <%if(((CargoOperationProcessBean)processListB.get(2)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType1" value="1" <%if(((CargoOperationProcessBean)processListB.get(2)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId1">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(2)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType1" value="1" <%if(((CargoOperationProcessBean)processListB.get(2)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="2" <%if(((CargoOperationProcessBean)processListB.get(2)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="0" <%if(((CargoOperationProcessBean)processListB.get(2)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId1B','deptId2','deptId2B',<%=((CargoOperationProcessBean)processListB.get(2)).getDeptId2()%>);</script>
		</div>
		<div id="process2B" style="display:none;">
			作业交接阶段二<br/>
			人员操作：<input type="text" name="operName2" value="<%=((CargoOperationProcessBean)processListB.get(3)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName2" value="<%=((CargoOperationProcessBean)processListB.get(3)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime4" value="<%=((CargoOperationProcessBean)processListB.get(3)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId3B" name="deptId3" onchange="changeDept('deptId3B','deptId4','deptId4B',<%=((CargoOperationProcessBean)processListB.get(3)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(3)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId4B">
			<select name="deptId4">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType2" value="0" <%if(((CargoOperationProcessBean)processListB.get(3)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType2" value="1" <%if(((CargoOperationProcessBean)processListB.get(3)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId2">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(3)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType2" value="1" <%if(((CargoOperationProcessBean)processListB.get(3)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="2" <%if(((CargoOperationProcessBean)processListB.get(3)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="0" <%if(((CargoOperationProcessBean)processListB.get(3)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId3B','deptId4','deptId4B',<%=((CargoOperationProcessBean)processListB.get(3)).getDeptId2()%>);</script>
		</div>
		<div id="process3B" style="display:none;">
			作业交接阶段三<br/>
			人员操作：<input type="text" name="operName3" value="<%=((CargoOperationProcessBean)processListB.get(4)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName3" value="<%=((CargoOperationProcessBean)processListB.get(4)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime5" value="<%=((CargoOperationProcessBean)processListB.get(4)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId5B" name="deptId5" onchange="changeDept('deptId5B','deptId6','deptId6B',<%=((CargoOperationProcessBean)processListB.get(4)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(4)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId6B">
			<select name="deptId6">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType3" value="0" <%if(((CargoOperationProcessBean)processListB.get(4)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType3" value="1" <%if(((CargoOperationProcessBean)processListB.get(4)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId3">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(4)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType3" value="1" <%if(((CargoOperationProcessBean)processListB.get(4)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="2" <%if(((CargoOperationProcessBean)processListB.get(4)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="0" <%if(((CargoOperationProcessBean)processListB.get(4)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId5B','deptId6','deptId6B',<%=((CargoOperationProcessBean)processListB.get(4)).getDeptId2()%>);</script>
		</div>
		<div id="process4B" style="display:none;">
			作业交接阶段四<br/>
			人员操作：<input type="text" name="operName4" value="<%=((CargoOperationProcessBean)processListB.get(5)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName4" value="<%=((CargoOperationProcessBean)processListB.get(5)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime6" value="<%=((CargoOperationProcessBean)processListB.get(5)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId7B" name="deptId7" onchange="changeDept('deptId7B','deptId8','deptId8B',<%=((CargoOperationProcessBean)processListB.get(5)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(5)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId8B">
			<select name="deptId8">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType4" value="0" <%if(((CargoOperationProcessBean)processListB.get(5)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType4" value="1" <%if(((CargoOperationProcessBean)processListB.get(5)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId4">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListB.get(5)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType4" value="1" <%if(((CargoOperationProcessBean)processListB.get(5)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="2" <%if(((CargoOperationProcessBean)processListB.get(5)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="0" <%if(((CargoOperationProcessBean)processListB.get(5)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId7B','deptId8','deptId8B',<%=((CargoOperationProcessBean)processListB.get(5)).getDeptId2()%>);</script>
		</div>
		<input type="hidden" name="formIndex" value="2"/>
		<input type="hidden" name="operationType" value="3"/>
	</form>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit3(this)">
		作业完成阶段：<input type="submit" value="保存设置"/>
		<div>
			人员操作：作业完成&nbsp;&nbsp;作业状态：作业结束&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime7" name="processCount" value="<%=((CargoOperationProcessBean)processListB.get(6)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
		</div>
		<input type="hidden" name="formIndex" value="3"/>
		<input type="hidden" name="operationType" value="3"/>
	</form>
</div>
<div id="operationType4" style="display: none;">
	作业单操作流程设置：调拨单<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit1(this)">
		作业生成阶段：<input type="submit" value="保存设置" />
		<div>
			人员操作：<%=((CargoOperationProcessBean)processListD.get(0)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListD.get(0)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime1" value="<%=((CargoOperationProcessBean)processListD.get(0)).getEffectTime() %>"/>分钟
			<br/>
			人员操作：<%=((CargoOperationProcessBean)processListS.get(1)).getOperName() %>&nbsp;&nbsp;
			作业状态：<%=((CargoOperationProcessBean)processListS.get(1)).getStatusName() %>&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime2" value="<%=((CargoOperationProcessBean)processListD.get(1)).getEffectTime() %>"/>分钟
			<br/>
		</div>
		<input type="hidden" name="formIndex" value="1"/>
		<input type="hidden" name="operationType" value="4"/>
	</form>
	<br/>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit2(this)">
		作业交接阶段：
		<select id="changeProcessCountD" name="processCount" onchange="changeProcessCount('D');">
			<option value="1">一阶流程</option>
			<option value="2">二阶流程</option>
			<option value="3">三阶流程</option>
			<option value="4">四阶流程</option>
		</select>
		<input type="submit" value="保存设置"/>
		<div id="process1D" style="display:block;">
			作业交接阶段一<br/>
			人员操作：<input type="text" name="operName1" value="<%=((CargoOperationProcessBean)processListD.get(2)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName1" value="<%=((CargoOperationProcessBean)processListD.get(2)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime3" value="<%=((CargoOperationProcessBean)processListD.get(2)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId1D" name="deptId1" onchange="changeDept('deptId1D','deptId2','deptId2D',<%=((CargoOperationProcessBean)processListD.get(2)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(2)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId2D">
			<select name="deptId2">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType1" value="0" <%if(((CargoOperationProcessBean)processListD.get(2)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType1" value="1" <%if(((CargoOperationProcessBean)processListD.get(2)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId1">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(2)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType1" value="1" <%if(((CargoOperationProcessBean)processListD.get(2)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="2" <%if(((CargoOperationProcessBean)processListD.get(2)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType1" value="0" <%if(((CargoOperationProcessBean)processListD.get(2)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId1D','deptId2','deptId2D',<%=((CargoOperationProcessBean)processListD.get(2)).getDeptId2()%>);</script>
		</div>
		<div id="process2D" style="display:none;">
			作业交接阶段二<br/>
			人员操作：<input type="text" name="operName2" value="<%=((CargoOperationProcessBean)processListD.get(3)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName2" value="<%=((CargoOperationProcessBean)processListD.get(3)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime4" value="<%=((CargoOperationProcessBean)processListD.get(3)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId3D" name="deptId3" onchange="changeDept('deptId3D','deptId4','deptId4D',<%=((CargoOperationProcessBean)processListD.get(3)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(3)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId4D">
			<select name="deptId4">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType2" value="0" <%if(((CargoOperationProcessBean)processListD.get(3)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType2" value="1" <%if(((CargoOperationProcessBean)processListD.get(3)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId2">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(3)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType2" value="1" <%if(((CargoOperationProcessBean)processListD.get(3)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="2" <%if(((CargoOperationProcessBean)processListD.get(3)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType2" value="0" <%if(((CargoOperationProcessBean)processListD.get(3)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId3D','deptId4','deptId4D',<%=((CargoOperationProcessBean)processListD.get(3)).getDeptId2()%>);</script>
		</div>
		<div id="process3D" style="display:none;">
			作业交接阶段三<br/>
			人员操作：<input type="text" name="operName3" value="<%=((CargoOperationProcessBean)processListD.get(4)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName3" value="<%=((CargoOperationProcessBean)processListD.get(4)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime5" value="<%=((CargoOperationProcessBean)processListD.get(4)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId5D" name="deptId5" onchange="changeDept('deptId5D','deptId6','deptId6D',<%=((CargoOperationProcessBean)processListD.get(4)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(4)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId6D">
			<select name="deptId6">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType3" value="0" <%if(((CargoOperationProcessBean)processListD.get(4)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType3" value="1" <%if(((CargoOperationProcessBean)processListD.get(4)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId3">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(4)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType3" value="1" <%if(((CargoOperationProcessBean)processListD.get(4)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="2" <%if(((CargoOperationProcessBean)processListD.get(4)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType3" value="0" <%if(((CargoOperationProcessBean)processListD.get(4)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId5D','deptId6','deptId6D',<%=((CargoOperationProcessBean)processListD.get(4)).getDeptId2()%>);</script>
		</div>
		<div id="process4D" style="display:none;">
			作业交接阶段四<br/>
			人员操作：<input type="text" name="operName4" value="<%=((CargoOperationProcessBean)processListD.get(5)).getOperName() %>" />&nbsp;&nbsp;
			作业状态：<input type="text" name="statusName4" value="<%=((CargoOperationProcessBean)processListD.get(5)).getStatusName() %>" />&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime6" value="<%=((CargoOperationProcessBean)processListD.get(5)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
			职能归属：
			<select id="deptId7D" name="deptId7" onchange="changeDept('deptId7D','deptId8','deptId8D',<%=((CargoOperationProcessBean)processListD.get(5)).getDeptId2()%>);">
				<option value="">一级部门</option>
				<%for(int i=0;i<deptList1.size();i++){ %>
					<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
					<option value="<%=dept.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(5)).getDeptId1()==dept.getId()){ %>selected=selected<%} %>><%=dept.getName() %></option>
				<%} %>
			</select>
			<span id="deptId8D">
			<select name="deptId8">
				<option value="">二级部门</option>
			</select>
			</span>
			<br/>
			操作方式：
			<input type="radio" name="handleType4" value="0" <%if(((CargoOperationProcessBean)processListD.get(5)).getHandleType()==0){ %>checked=checked<%} %>/>人工确认&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="handleType4" value="1" <%if(((CargoOperationProcessBean)processListD.get(5)).getHandleType()==1){ %>checked=checked<%} %>/>设备确认&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			所属仓库：
			<select name="storageId4">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
					<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storage.getId() %>" <%if(((CargoOperationProcessBean)processListD.get(5)).getStorageId()==storage.getId()){ %>selected=selected<%} %>><%=storage.getWholeCode() %></option>
				<%} %>
			</select><br/>
			作业判断：
			<input type="radio" name="confirmType4" value="1" <%if(((CargoOperationProcessBean)processListD.get(5)).getConfirmType()==1){ %>checked=checked<%} %>/>源货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="2" <%if(((CargoOperationProcessBean)processListD.get(5)).getConfirmType()==2){ %>checked=checked<%} %>/>目的货位&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" name="confirmType4" value="0" <%if(((CargoOperationProcessBean)processListD.get(5)).getConfirmType()==0){ %>checked=checked<%} %>/>不做判断&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<script type="text/javascript">changeDept('deptId7D','deptId8','deptId8D',<%=((CargoOperationProcessBean)processListD.get(5)).getDeptId2()%>);</script>
		</div>
		<input type="hidden" name="formIndex" value="2"/>
		<input type="hidden" name="operationType" value="4"/>
	</form>
	<form action="qualifiedStock.do?method=updateProcess" method="post" onsubmit="return checkSubmit3(this)">
		作业完成阶段：<input type="submit" value="保存设置"/>
		<div>
			人员操作：作业完成&nbsp;&nbsp;作业状态：作业结束&nbsp;&nbsp;
			时效状态：<input type="text" name="effectTime7" value="<%=((CargoOperationProcessBean)processListD.get(6)).getEffectTime() %>"/>分钟&nbsp;&nbsp;
		</div>
		<input type="hidden" name="formIndex" value="3"/>
		<input type="hidden" name="operationType" value="4"/>
	</form>
</div>
<script>selectOption(document.getElementById('operationType'), '<%=operationType%>');changeOperationType();</script>

<select id="abc" name="abc" >
	<option value="1">111</option>
</select>
<input type="button" value="delete" onclick="deleteSome();"/>
<input type="button" value="add" onclick="addSome();"/>
<script type="text/javascript" >
	function addSome() {
		
		for( var i = 0; i < 5; i++ ) {
			document.getElementById("abc").options.add(new Option("sss"+i,i));
		}
		var selectEle = document.getElementById("abc");
			var count = selectEle.options.length;
		for( var j = 0 ; j < count; j ++ ) {
			if( selectEle.options[j].value==2 ) {
			selectEle.options[j].selected='selected';
			}
		}
	}
	
	function deleteSome() {
		var selectEle = document.getElementById("abc");
		var count = selectEle.options.length;
		selectEle.options.length = 0;
	}
	
	function changeNextOption0 (cDeptEleId0, cDeptEleId1, cDeptEleId2, needSelect1) {
		var Ele0 = document.getElementById(cDeptEleId0);
		var Ele1 = document.getElementById(cDeptEleId1);
		var Ele2 = document.getElementById(cDeptEleId2);
		
		if( Ele0.value == "-1" ) {
			//清除下面两级的所有选项
			Ele1.options.length = 0;
			Ele2.options.length = 0;
			Ele1.options.add(new Option("一级部门", "-1"));
			Ele2.options.add(new Option("二级部门", "-1"));
		} else {
			var pDeptId = Ele0.value;
			$.ajax({
				type: "GET",
				url: "qualifiedStock.do?method=getOptionByPDept&level=1&pDeptId="+pDeptId,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					var json = eval('(' + msg + ')');
					if ( json['status'] == "fail" ) {
						Ele1.options.length = 0;
						Ele1.options.add(new Option("一级部门", "-1"));
						Ele2.options.add(new Option("二级部门", "-1"));
						Ele2.options.length = 0;
					} else if ( json['status'] == "success" ) {
						Ele1.options.length = 0;
						Ele1.options.add(new Option("一级部门", "-1"));
						var pas = json['options'];
						var x= pas.length;
						for( var i = 0; i < x; i ++ ) {
							Ele1.options.add(new Option(pas[i]['name'], pas[i]['id']));
						}
						
					}
				}
			});
		}
		
		
		
		
	}
	
	function changeNextOption1 (cDeptEleId1, cDeptEleId2, needSelect2) {
	
	}
</script>


</body>
</html>