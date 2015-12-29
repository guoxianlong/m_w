<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<html>
<head>
<title>批量添加货位</title>
<%List wholeStorageList=(List)request.getAttribute("wholeStorageList"); %>
<%List cargoCountList=new ArrayList();
List productLineList=null;
String shelfId="";
if(request.getAttribute("cargoCountList")!=null){ 
	cargoCountList=(List)request.getAttribute("cargoCountList");
}
if(request.getAttribute("productLineList")!=null){ 
	productLineList=(List)request.getAttribute("productLineList");
}
if(request.getAttribute("shelfId")!=null){ 
	shelfId=request.getParameter("shelfId");
}
%>

<%List shelfList=(List)request.getAttribute("shelfList"); %>
<%List wholeCityList=(List)request.getAttribute("wholeCityList"); %>
<%List stockAreaCodeList=(List)request.getAttribute("stockAreaCodeList"); %>
<%if(request.getAttribute("deleted")!=null){ %>
<script type="text/javascript">alert("删除成功！");</script>
<%} %>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
</head>
<body>
<script type="text/javascript">
function selectcity(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&cityId="+document.getElementById("cityId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("area").innerHTML = msg;
		}
	});
}
function selectarea(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&areaId="+document.getElementById("areaId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("storage").innerHTML = msg;
		}
	});
}
function selectstorage(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("stockArea").innerHTML = msg;
		}
	});
}
function selectstockarea(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&stockAreaId="+document.getElementById("stockAreaId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("passage").innerHTML = msg;
		}
	});
}
function selectpassage(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&passageId="+document.getElementById("passageId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("shelf").innerHTML = msg;
		}
	});
}
function checkAll1(){
	var checkAll=document.getElementById("checkAll");
	if(checkAll.checked==true){
		for(var i=1;i<=<%=cargoCountList.size()%>;i++){
			document.getElementById('check'+i).checked=!checkAll.checked;
		}
	}
}
function changeCheckAll(){
	document.getElementById("checkAll").checked=false;
}
function checkSubmit(){
	if(document.getElementById("checkAll").checked==true){//统一添加
		if(document.getElementById("cargoCount").value.trim()==""){
			alert("请输入最后一行的货位个数！");
			return false;
		}else if(/^[0-9]{1,2}$/.exec(document.getElementById("cargoCount").value)==null){
			alert("最后一行的货位个数，输入不正确，请输入大于0的整数！");
			return false;
		}else if(/^[0]+$/.exec(document.getElementById("cargoCount").value)!=null){
			alert("最后一行的货位个数，输入不正确，请输入大于0的整数！");
			return false;
		}
		<%for(int i=1;i<=cargoCountList.size();i++){%>
			if(new Number(document.getElementsByName("count")[<%=cargoCountList.size()-i%>].value)+new Number(document.getElementById("cargoCount").value)>99){
				alert("第<%=i%>层货位数将大于99，不能添加！");
				return false;
			}
		<%}%>
		
		
		if(/^[0-9]*$/.exec(document.getElementById("length").value)==null){
			alert("最后一行的货长宽高，输入不正确，请输入大于0的整数！");
			return false;
		}
		if(/^[0-9]*$/.exec(document.getElementById("width").value)==null){
			alert("最后一行的货长宽高，输入不正确，请输入大于0的整数！");
			return false;
		}
		if(/^[0-9]*$/.exec(document.getElementById("high").value)==null){
			alert("最后一行的货长宽高，输入不正确，请输入大于0的整数！");
			return false;
		}
	}else{//分别添加
		var check=document.getElementsByName("check");
		for(var i=0;i<check.length;i++){
			if(check[i].checked==true){i=check.length}
			if(i==check.length-1){
				alert("请至少选中货架的某一层！");
				return false;
			}
		}
		for(var i=check.length;i>0;i--){
			if(check[check.length-i].checked==true){
				if(document.getElementById("cargoCount"+i).value.trim()==""){
					alert("请输入第"+i+"层的货位个数！");
					return false;
				}else if(/^[0-9]{1,2}$/.exec(document.getElementById("cargoCount"+i).value)==null){
					alert("第"+i+"层的货位个数，输入不正确，请输入大于0的整数！");
					return false;
				}else if(/^[0]+$/.exec(document.getElementById("cargoCount"+i).value)!=null){
					alert("第"+i+"层的货位个数，输入不正确，请输入大于0的整数！");
					return false;
				}
				if(new Number(document.getElementsByName("count")[check.length-i].value)+new Number(document.getElementById("cargoCount"+i).value)>99){
					alert("第"+i+"层货位数将大于99，不能添加！");
					return false;
				}
				
				
				if(/^[0-9]*$/.exec(document.getElementById("length"+i).value)==null){
					alert("第"+i+"行的货长宽高，输入不正确，请输入大于0的整数！");
					return false;
				}
				if(/^[0-9]*$/.exec(document.getElementById("width"+i).value)==null){
					alert("第"+i+"行的货长宽高，输入不正确，请输入大于0的整数！");
					return false;
				}
				if(/^[0-9]*$/.exec(document.getElementById("high"+i).value)==null){
					alert("第"+i+"行的货长宽高，输入不正确，请输入大于0的整数！");
					return false;
				}
			}
		}
	}
	
	return confirm("如果确认提交，请单击‘确定’，反之，请单击‘取消’！");
}
</script>
<%if(request.getAttribute("cargoCountList")==null){ %>
<p>批量添加货位A</p><br/>
<form action="../admin/cargoInfo.do?method=addCargoList" method="post">
<fieldset style="width:750px;"><legend>如果添加货位，请先选择货架</legend>
所属仓库<font color="red">*</font>：
<select id="storageId" name="storageId" onchange="selectstorage();">
	<%for(int i=0;i<wholeStorageList.size();i++){ 
		CargoInfoStorageBean storageBean=(CargoInfoStorageBean)wholeStorageList.get(i);%>
		<option value="<%=storageBean.getId() %>" <%if(request.getParameter("storageId")!=null&&(!request.getParameter("storageId").equals(""))&&Integer.parseInt(request.getParameter("storageId"))==storageBean.getId()){ %>selected=selected<%} %>>
			<%=storageBean.getWholeCode() %>
		</option>
	<%} %>
</select>
<script type="text/javascript">selectstorage()</script>
所属区域<font color="red">*</font>：
<span id='stockArea'>
	<select>
		<option value="">请选择</option>
	</select>
</span>
所属巷道<font color="red">*</font>：
<span id='passage'>
	<select>
		<option value="">请选择</option>
	</select>
</span>
货架代号<font color="red">*</font>：
<span id='shelf'>
	<select>
		<option value="">请选择</option>
	</select>
</span>
<input type="submit" value="下一步"/>
</fieldset>
</form>
<%}else{ %>
	<p>批量添加货位B</p>
	<p><b><%=request.getAttribute("wholeShelfCode") %></b><%=request.getAttribute("shelfAddress") %></p>
	<form action="../admin/cargoInfo.do?method=cargoInfoAddCargo&shelfId=<%=shelfId %>&areaId=<%=request.getAttribute("areaId") %>" method="post">
	<table cellpadding="3" cellspacing="1" border=1>
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">选</font></td>
			<td><font color="#FFFFFF">货架层</font></td>
			<td><font color="#FFFFFF">当前货位数</font></td>
			<td><font color="#FFFFFF">本次添加货位数</font></td>
			<td><font color="#FFFFFF">存放类型</font></td>
			<td><font color="#FFFFFF">货位产品线</font></td>
			<td><font color="#FFFFFF">货位类型</font></td>
			<td><font color="#FFFFFF">长、宽、高/cm</font></td>
		</tr>
		<%for(int i=cargoCountList.size();i>0;i--){ %>
			<tr>
				<td><input id="check<%=i %>" type="checkbox" name="check" value=<%=i %> onclick="changeCheckAll();"/></td>
				<td>第<%=i %>层<%if(i==cargoCountList.size()&&cargoCountList.size()!=1){ %>（最顶层）<%} %><%if(i==1){ %>（最底层）<%} %></td>
				<td><%=cargoCountList.get(i-1) %></td>
				<input type="hidden" name="count" value="<%=cargoCountList.get(i-1) %>"/>
				<td><input id="cargoCount<%=i %>" name="cargoCount<%=i %>" type="text" size=3 maxlength=2 /></td>
				<td>
					<select id="storeType<%=i %>" name="storeType<%=i %>">
						<option value="0">散件区</option>
						<option value="1">整件区</option>
						<option value="2">缓存区</option>
						<option value="4">混合区</option>
						<option value="5">作业区</option>
						<option value="6">包裹区</option>
						<option value="7">下架区</option>
					</select>
				</td>
				<td>
					<select id="productLineId<%=i %>" name="productLineId<%=i %>">
						<option value="">请选择</option>
						<%for(int j=0;j<productLineList.size();j++){ %>
							<option value="<%=((voProductLine)(productLineList.get(j))).getId()%>"><%=((voProductLine)(productLineList.get(j))).getName() %></option>
						<%} %>
					</select>
				</td>
				<td>
					<select id="type<%=i %>" name="type<%=i %>">
						<option value="0">普通</option>
						<option value="1">热销</option>
						<option value="2">滞销</option>
						<option value="3">完好</option>
						<option value="4">保修机残次</option>
						<option value="5">非保修机残次</option>
					</select>
				</td>
				<td>
					长<input id="length<%=i %>" type="text" size=3 name="length<%=i %>"/>
					宽<input id="width<%=i %>" type="text" size=3 name="width<%=i %>"/>
					高<input id="high<%=i %>" type="text" size=3 name="high<%=i %>"/>
				</td>
			</tr>
		<%} %>
		<tr>
				<td><input type="checkbox" id="checkAll" name="checkAll" value="1" onclick="checkAll1();"/></td>
				<td>所有层统一设定</td>
				<td>-</td>
				<td><input id="cargoCount" name="cargoCount" type="text" size=3 maxlength=2 /></td>
				<td>
					<select id="storeType" name="storeType">
						<option value="0">散件区</option>
						<option value="1">整件区</option>
						<option value="2">缓存区</option>
						<option value="4">混合区</option>
						<option value="5">作业区</option>
						<option value="6">包裹区</option>
						<option value="7">下架区</option>
					</select>
				</td>
				<td>
					<select id="productLineId" name="productLineId">
						<option value="">请选择</option>
						<%for(int j=0;j<productLineList.size();j++){ %>
							<option value="<%=((voProductLine)(productLineList.get(j))).getId()%>"><%=((voProductLine)(productLineList.get(j))).getName() %></option>
						<%} %>
					</select>
				</td>
				<td>
					<select id="type" name="type">
						<option value="0">普通</option>
						<option value="1">热销</option>
						<option value="2">滞销</option>
						<option value="3">完好</option>
						<option value="4">残次</option>
						<option value="4">保修机残次</option>
						<option value="5">非保修机残次</option>
					</select>
				</td>
				<td>
					长<input id="length" type="text" size=3 name="length"/>
					宽<input id="width" type="text" size=3 name="width"/>
					高<input id="high" type="text" size=3 name="high"/>
				</td>
		</tr>
	</table>
	以上货位是否直接开通：<input type="radio" name="status" value="1" checked=checked/>开通 <input type="radio" name="status" value="2"/>暂不开通
	<input type="submit" onClick="return checkSubmit();" value="提交"/> <input type="reset" value="重置"/>
	</form>
	注：如果添加某一层的货位，请勾选该行第一列的复选框后再单击"提交"。
<%} %>
</body>
</html>