<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="java.util.*"%>
<%@page import="adultadmin.bean.cargo.CargoDeptBean"%>
<html>
<%
List deptList=new ArrayList();
if(request.getAttribute("deptList")!=null){
	deptList=(List)request.getAttribute("deptList");
}
CargoDeptBean dept=(CargoDeptBean)request.getAttribute("dept");
List detailDeptList=new ArrayList();
if(request.getAttribute("detailDeptList")!=null){
	detailDeptList=(List)request.getAttribute("detailDeptList");
}
%>
<head>
<title>组织结构管理</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script type="text/javascript">
function newDept(){
	var depts=document.getElementById("depts");
	var newRow=depts.insertRow();
	var newCell0 = newRow.insertCell();
    var newCell1 = newRow.insertCell();
    newCell0.innerHTML = "<input type='text' name='deptName'/>";
    newCell1.innerHTML = "<input type='text' name='deptCode'/>";
}
function checkDept(){
	var nameList=document.getElementsByName("deptName");
	var codeList=document.getElementsByName("deptCode");
	var reg=/^[0-9]{2}$/;
	for(var i=0;i<nameList.length;i++){
		var name=trim(nameList[i].value).length;
		var code=trim(codeList[i].value).length;
		if((name==0&&code!=0)||(name!=0&&code==0)){
			alert("部门名称和部门代码必须填写完整！");
			return false;
		}
		if(name>0&&!reg.exec(trim(codeList[i].value))){
			alert("部门代码只能是两位数字！");
			return false;
		}
	}
	<%if(detailDeptList!=null){%>
		var changed=false;
		var name="";
		var code="";
		<%for(int i=0;i<detailDeptList.size();i++){%>
			<%CargoDeptBean cd=(CargoDeptBean)detailDeptList.get(i); %>
			name=trim(nameList[<%=i%>].value);
			code=trim(codeList[<%=i%>].value);
			if(name!='<%=cd.getName()%>'){
				changed=true;
			}
			if(code!='<%=cd.getCode()%>'){
				changed=true;
			}
		<%}%>
		if(changed==true){
			var b=confirm("修改部门编号或名称会影响部门下所有员工，确定修改？");
			if(b==false){
				return false;
			}
		}
	<%}%>
	return true;
}
</script>
</head>
<body>
<table>
	<tr>
		<td colspan="2">物流中心组织结构管理</td>
	</tr>
	<tr>
		<td width="40%" valign="top">
			<table border="1">
				<tr>
					<td>维护组织结构</td>
				</tr>
				<tr>
					<td>
						<%for(int i=0;i<deptList.size();i++){ %>
							<%CargoDeptBean cd=(CargoDeptBean)deptList.get(i); %>
							<%=cd.getName() %>&nbsp;
							<%=cd.getCode() %>&nbsp;
							<a href="qualifiedStock.do?method=deptManagement&deptId=<%=cd.getId() %>">下级部门</a>&nbsp;
							<a href="qualifiedStock.do?method=deleteDept&deptId=<%=cd.getId() %>">删除部门</a><br/>
							<%List deptList2=cd.getJuniorDeptList(); %>
							<%for(int j=0;j<deptList2.size();j++){ %>
								&nbsp;&nbsp;&nbsp;
								<%CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j); %>
								<%=cd2.getName() %>&nbsp;
								<%=cd2.getCode() %>&nbsp;
								<a href="qualifiedStock.do?method=deptManagement&deptId=<%=cd2.getId() %>">下级部门</a>&nbsp;
								<a href="qualifiedStock.do?method=deleteDept&deptId=<%=cd2.getId() %>">删除部门</a><br/>
								<%List deptList3=cd2.getJuniorDeptList(); %>
								<%for(int k=0;k<deptList3.size();k++){ %>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<%CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k); %>
									<%=cd3.getName() %>&nbsp;
									<%=cd3.getCode() %>&nbsp;
									<a href="qualifiedStock.do?method=deptManagement&deptId=<%=cd3.getId() %>">下级部门</a>&nbsp;
									<a href="qualifiedStock.do?method=deleteDept&deptId=<%=cd3.getId() %>">删除部门</a><br/>
									<%List deptList4=cd3.getJuniorDeptList(); %>
									<%for(int l=0;l<deptList4.size();l++){ %>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<%CargoDeptBean cd4=(CargoDeptBean)deptList4.get(l); %>
										<%=cd4.getName() %>&nbsp;
										<%=cd4.getCode() %>&nbsp;
										<a href="qualifiedStock.do?method=deleteDept&deptId=<%=cd4.getId() %>">删除部门</a><br/>
									<%} %>
								<%} %>
							<%} %>
						<%} %>
					</td>
				</tr>
			</table>
		</td>
		<td>
			<table border="1">
				<tr>
					<td>
						<%if(dept==null||dept.getParentId0()==0){ %>一级部门设置
						<%}else if(dept.getParentId1()==0){ %>二级部门设置
						<%}else if(dept.getParentId2()==0){ %>三级部门设置<%} %>
					</td>
				</tr>
				<tr>
					<td>
						<form action="qualifiedStock.do?method=updateDept" method="post">
						<table id="depts">
							<tr>
								<td>部门名称</td>
								<td>部门代码</td>
							</tr>
							<%for(int i=0;i<detailDeptList.size();i++){ %>
								<%CargoDeptBean cd=(CargoDeptBean)detailDeptList.get(i); %>
							<tr>
								<td>
									<input type="text" name="deptName" value="<%=cd.getName() %>"/>
								</td>
								<td>
									<input type="text" name="deptCode" value="<%=cd.getCode() %>"/>
								</td>
							</tr>	
							<%} %>
						</table>
						<input type="button" onclick="newDept();" value="增设部门"/><br/>
						<input type="submit" value="保存" onclick="return checkDept();">&nbsp;&nbsp;
						<input type="button" value="取消" onclick="window.location='qualifiedStock.do?method=staffManagement';"/>
						<%if(dept!=null){ %>
							<input type="hidden" name="deptId" value='<%=dept.getId()%>'/>
						<%} %>
						</form>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
</html>