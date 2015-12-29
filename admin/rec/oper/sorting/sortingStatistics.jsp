<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%
List areaList = (List) request.getAttribute("areaList");
String startTime = StringUtil.convertNull((String)request.getAttribute("startTime"));
String endTime = StringUtil.convertNull((String)request.getAttribute("endTime"));
String staffCode = StringUtil.convertNull((String)request.getAttribute("staffCode"));
String staffName = StringUtil.convertNull((String)request.getAttribute("staffName"));
SortingBatchGroupBean totalBean = (SortingBatchGroupBean)request.getAttribute("totalBean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
List staffList = (List)request.getAttribute("staffList");
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
<!DOCTYPE html>
<%
int hour =24;
int m =5;
%>
<html>
<head>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
	var datagrid;
	var addBrand;
	var userForm;
	var passwordInput;
	var userRoleDialog;
	var userRoleForm;
	var editDiv;
	var editForm;
	var staffName;
	$(function() {
		    $('#startTime').datebox({ 
				required:true,
				editable:false,
				onSelect:function(date){ 
					var start = $('#startTime').datebox("getValue");
					var end =$('#endTime').datebox("getValue");
					$('#staffName').combobox({   
						url : '${pageContext.request.contextPath}/SortingController/getStaffList.mmx?startTime='+start+'&endTime='+end,
						valueField : 'staffCode',   
						textField : 'staffName',
						panelHeight:'auto'
					}); 
					$('#staffName').combobox({
					    onChange:function(newValue,oldValue){
					        document.getElementById("staffCode").value=newValue;
					    }
					});
			    }
			}); 
		    $('#endTime').datebox({ 
		    	required:true,
		    	editable:false,
				onSelect:function(date){ 
					var start = $('#startTime').datebox("getValue");
					var end =$('#endTime').datebox("getValue");
					$('#staffName').combobox({   
						url : '${pageContext.request.contextPath}/SortingController/getStaffList.mmx?startTime='+start+'&endTime='+end,
						valueField : 'staffCode',   
						textField : 'staffName',
						panelHeight:'auto'
					}); 
					$('#staffName').combobox({
					    onChange:function(newValue,oldValue){
					        document.getElementById("staffCode").value=newValue;
					    }
					});
			    }
			}); 
			datagrid = $('#datagrid').datagrid({
			url : '${pageContext.request.contextPath}/SortingController/sortingStatisticsList.mmx',
			toolbar : '#toolbar',
			title : '分拣监控',
			striped:'true',
			rownumbers:'true',
			showFooter:'true',
			columns : [ [ {
				field : 'staffName',
				title : '姓名',
				align :'center',
					width:'90'
			},{
				field : 'staffCode',
				align :'center',
				title : '员工号',
				sortable : true,
				width:'180'
			},{
				field : 'attendanceCount',
				title : '出勤天数',
				align :'center',
				width:'80'
			},{
				field : 'groupCount',
				title : '波次数',
				align :'center',
				width:'80'
			},{
				field : 'orderCount',
				title : '订单数',
				align :'center',
				width:'80'
			},{
				field : 'skuCount',
				title : 'SKU数',
				align :'center',
				width:'80'
			},{
				field : 'productCount',
				title : '商品个数',
				align :'center',
				width:'180'
			},{
				field : 'passageCount',
				title : '巷道数',
				align :'center',
				width:'90'
				
			},{
				field : 'cancelOrderCount',
				title : '撤单数',
				align :'center',
				width:'90'
				
			},{
				field : 'action',
				title : '操作',
				align :'center',
				width : 100,
				formatter : function(value, row, index) {
						a = ""+row.staffCode;
					if(row.staffName!='总数：')
					    return '<a href="javascript:void(0);" onclick="xiangxi(\''+a+'\')">详细</a>';
				}
		      }
			] ]		  
		});
			$('#staffName').combobox({   
				url : '${pageContext.request.contextPath}/SortingController/getStaffList.mmx',
				valueField : 'staffCode',   
				textField : 'staffName',
				panelHeight:'auto'
			}); 
	});
	function setSucDate(){
		var date = new Date()
		if(date.getMonth()<=8){
			var d = date.getFullYear() + '-0' + (date.getMonth()+1) + '-' + date.getDate()
		}else{
			var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
		}
		$('#startTime').datebox('setValue',d);
		$('#endTime').datebox('setValue',d);
	}
	function searchBrand() {
		if(checksubmit()){
			datagrid.datagrid('load', {
				startTime : $('#searchForm').find('[name=startTime]').val(),
				endTime : $('#searchForm').find('[name=endTime]').val(),
				staffName : $('#searchForm').find('[name=staffName]').val(),
				staffCode : $('#searchForm').find('[name=staffCode]').val()
			});
		}
	}
	function xiangxi(code){
		var start = $('#startTime').datebox("getValue");
		var end =$('#endTime').datebox("getValue");
		location.href = '${pageContext.request.contextPath}/SortingController/sortingStatisticalDetailed.mmx?staffCode='+code+'&startTime='+start+'&endTime='+end;
	}
	function excel(){
		if(checksubmit()){
			
		var start = $('#startTime').datebox("getValue");
		var end =$('#endTime').datebox("getValue");
		var staffCode =document.getElementById("staffCode").value;
		location.href = '${pageContext.request.contextPath}/SortingController/sortingStatisticsListExcel.mmx?staffCode='+staffCode+'&startTime='+start+'&endTime='+end;
		}
	}
	function checksubmit(){
		var startTime = $('#startTime').datebox("getValue");
		var endTime = $('#endTime').datebox("getValue");
		var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
		if(startTime.length!=0&&endTime.length==0 ){
			alert("请输入起始时间")
			return false;
		}
		if(startTime.length==0&&endTime.length!=0 ){
		    alert("请输入截止时间")
			return false;
		}
		var nDay_ms=24*60*60*1000;
		var reg=new RegExp("-","g");
		var startDay=new Date(startTime.replace(reg,'/'));
		var endDay=new Date(endTime.replace(reg,'/'));
		var nDifTime=endDay.getTime()- startDay.getTime();
		if(nDifTime < 0){
			alert("起始日期不能大于或等于结束日期！");
	    	return false;
		}
	    var nDifDay=Math.floor(nDifTime/nDay_ms);
	    if(nDifDay > 30){
	    	alert("日期间隔不能大于31天！");
	    	return false;
	    }
		return true;
	}
</script>
</head>
<body class="easyui-layout" fit="true" <%if(startTime==""&&endTime==""){ %>onload="setSucDate()"<%} %>>
	<div region="center" border="false" style="overflow: hidden;">
		<div id="toolbar" class="datagrid-toolbar"  style="height: auto;display: none;">
		<form id="searchForm">
					
					



<input type=text  id='startTime' name='startTime' size="10" value="" />至
		<input type=text  id='endTime' name='endTime'  size="10" value="" />&nbsp;&nbsp;&nbsp;&nbsp;
		
		姓名:
		<select name="staffName" id="staffName" class="easyui-combobox" panelHeight="auto" style="width:100px">
		</select> 
		员工号:<input type='text'id="staffCode"  name='staffCode'/>&nbsp;&nbsp;
		<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchBrand();" href="javascript:void(0);">查找</a>





			</form>
		</div>
		<table id="datagrid" ></table>
		<input type='button' onclick ='excel()'value='导出'/>
	</div>
</body>
</html>