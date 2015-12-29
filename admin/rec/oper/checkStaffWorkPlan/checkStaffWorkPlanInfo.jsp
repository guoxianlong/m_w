<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	String monthDate = StringUtil.convertNull(request.getParameter("monthDate"));
	int areaId = StringUtil.toInt(StringUtil.convertNull(request.getParameter("areaId")));
	List list = (List) request.getAttribute("dateList");
%>
<html>
  <head>
    
    <title>质检排班计划</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
    <style type="text/css">
	body {
		padding: 0px;
	}
	form {
		padding: 0px;
		margin-left: 0px;
		margin-right:0px;
	}
</style>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
		function updateAreaId(){
			var areaId = document.getElementById("areaId").value;
			document.getElementById("areaIdHidden").value = areaId;
		}
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	$.messager.alert("提示","请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			$.messager.alert("提示","请填入整数！！");
	   			}
   			}
   		}	
   		
   		function synMonthDate() {
   			var val = document.getElementById("monthDate").value;
   			$.messager.alert("提示",val);
   			document.getElementById("monthDate2").value=val;
   			document.getElementById("monthDate3").value=val;
   		}

   		function checkChangeMonth() {
   			var val = document.getElementById("monthDate").value;
   			var count = document.getElementById("mStaffCount").value;
   			if( count == null || count == "" ) {
   				$.messager.alert("提示","请填写月在编人数再提交！");
   				return;
   			}
   			if( val == null || val == "") {
   				return;
   			} else {
   				document.getElementById("monthDate2").value=val;
   				document.form2.submit();
   			}
   		}
   		
   		function addToSubmit(obj, tail) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    			document.getElementById("dStaffCount_" + tail).value = obj.value;
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	$.messager.alert("提示","请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			$.messager.alert("提示","请填入整数！！");
	   			}
   			}
   		}	
	</script>

  </head>
  <body>
<div id="tb">
  	<div align="center">
  		<table align="center" width="100%" border="0" cellspacing="1px" bgcolor="#99CCFF" cellpadding="1px">
  			<tr align="center">
  				<td bgcolor="#FFFFFF" width="50%" align="center" valign="middle">
  				<h2>
  				<a href="<%= request.getContextPath()%>/admin/toCheckEffectInfo.mmx">商品质检分类与效率
  				</a>
  				</h2>
  				</td>
  				<td bgcolor="#CCFFFF" align="center" valign="middle">
  				<h2>
  					<a href="<%= request.getContextPath()%>/admin/getCheckStaffWorkPlanInfo.mmx">质检排班计划</a>
  				</h2>
  				</td>
  			</tr>
  		</table>
  	</div>
	<fieldset>
	<legend>日在编人数查询</legend>
  	<form action="<%= request.getContextPath() %>/admin/getCheckStaffWorkPlanInfo.mmx" method="post" name="form1">
  	地区:
  	<% List areaList = CargoDeptAreaService.getCargoDeptAreaList(request); 
		%>
  	<select id="areaId" name="areaId" onchange="updateAreaId()">
			<%
				for(int i = 0;i<areaList.size();i++){
					ProductStockBean psBean = new ProductStockBean();
					int area = Integer.parseInt(areaList.get(i).toString());
			%>
			<option value="<%=areaList.get(i)%>" <%if(areaId == area){ %> selected="selected" <%} %>><%=psBean.getAreaName(area) %></option>
			<%
				}
			%>
  	</select>
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月份:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	<input type="text" id="monthDate" name="monthDate" onfocus="WdatePicker({dateFmt:'yyyy-MM'})" class="Wdate" value="<%= monthDate %>" />
  		<a href="javascript:document.form1.submit();" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查看在编人数</a><br/>
	</form>
	<form action="<%= request.getContextPath() %>/admin/addMonthStaffCount.mmx" method="post" name="form2">
	当月在编:<input type="text" id="mStaffCount" name="mStaffCount" onblur="checkNumber(this);" />
			<input type="hidden" name="monthDate2" id="monthDate2" value="<%= monthDate %>"/>
			<input type="hidden" name="areaIdHidden" id="areaIdHidden" value="<%= areaId %>"/>
	<a href="javascript:checkChangeMonth();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">确认提交</a>
	<br/>
	</form>
	</fieldset>
	</div>
	每日在岗人数：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/>
	<div>
	<div id= "menu" class= "easyui-menu" style= "width:120px;display: none;" >
             <div onclick= "javascript:document.form3.submit();" iconCls= "icon-ok" >确认编辑日在编人数</div>
    </div>
	
	<span id="numberTip"></span>
	<form action="<%= request.getContextPath() %>/admin/editDayStaffCount.mmx" method="post" onsubmit="" name="form3">
		<input type="hidden" name="monthDate3" id="monthDate3" value="<%= monthDate %>"/>
		<input type="hidden" name="area" id="area" value="<%= areaId %>"/>
	<table align="center" id="info_table" class="easyui-datagrid" data-options="fit:true,striped: true,collapsible:true,rownumbers:true,fitColumns:true,border:true,toolbar:[{iconCls: 'icon-edit',text:'确认日在编人数编辑',handler: function(){document.form3.submit();}}],toolbar:'#tb'">
		<thead>  
        <tr>  
            <th data-options="field:'date',width:250,align:'center'">日期</th>  
            <th data-options="field:'staffCount',width:200,align:'center'">在编人数</th>  
        </tr>  
    </thead>
		<% for (int i = 0; i < list.size(); i ++ )  {
			CheckStaffBean csb = (CheckStaffBean)list.get(i);
			
		%>
		<tr bgcolor="#FFFFFF">
			<td align="center">
			<%= csb.getNikeName() %>
			</td>
			<td align="center">
				<input type="text" size="9" value="<%= csb.getDayCount()%>" onchange="addToSubmit(this,'<%= csb.getNikeName() %>');" />
				<input type="hidden" name="dStaffCount_<%= csb.getNikeName() %>" id="dStaffCount_<%= csb.getNikeName() %>" value="-1" />
			</td>
		</tr>
		<% 
		} 
		%>
	</table>
	</form>
	</div>
	<center><a href="javascript:document.form3.submit();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">确认</a></center>
  	<script type="text/javascript">
  		$(function () {
  			$( '#info_table' ).datagrid({
                   onRowContextMenu : function (e, rowIndex, rowData) {
                              e.preventDefault();
                              $( this ).datagrid('unselectAll' );
                              $( this ).datagrid('selectRow' , rowIndex);
                              $( '#menu' ).menu('show' , {
                                    left : e.pageX,
                                    top : e.pageY
                              });
                        }
                  });
  		});
  	</script>
  </body>
  
</html>
