<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="mmb.stock.stat.*"%>
<%
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
%>
<html>
  <head>
    <title>添加收货暂存号</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../js/jquery.js"></script>
	<link href="../css/global.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
	jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
		
		function goSubmit() {
			jQuery.messager.progress();	// display the progress bar
			$('#ff').form('submit', {
				url: '<%= request.getContextPath()%>/admin/addTemporaryNum2.mmx',
				onSubmit: function(){
					//$.messager.alert("1", "2");1
					var isValid = $(this).form('validate');
					if (!isValid){
						jQuery.messager.progress('close');	// hide progress bar while the form is invalid
					}
					var tepNum = $("#tempNum").val();
					var vwareArea = $("#wareArea").val();
					jQuery.post("<%= request.getContextPath()%>/admin/addTemporaryNum2.mmx",{tempNum:tepNum,wareArea:vwareArea},function(result){
							if(result=="添加成功！"){
								jQuery.messager.alert("提示","添加成功！");
								window.location.href="<%= request.getContextPath()%>/admin/toQueryTemporaryNum2.mmx";
							}else{
								jQuery.messager.alert("提示",result);
							}
					});
					jQuery.messager.progress('close');
					return false;	// return false will stop the form submission
				},
				success: function(){
					jQuery.messager.progress('close');	// hide progress bar while submit successfully
				}
			});
		}
		
		function goSdand() {
			return false;
		}
	</script>
</head>
<body>
<center>
<h5 style="margin-left: 10px;">添加收货暂存号</h5>
<div style="width:350px;height:200px;padding:10px;" class="easyui-panel" title="添加收货暂存号">
	<center>
	<form id="ff" method="get" onsubmit="return goSdand();">  
    <div>  
    	<br/>
    	<br/>
        <label for="name">收货暂存号:</label>  
        <input class="easyui-validatebox" type="text" name="tempNum" id="tempNum" data-options="required:true"  value=""/>  
    	<br/>
    	<br/>
    	<label for="name">地区：</label>
    	<%= wareAreaSelectLable %>
    	<br/>
    	<br/>
		<input type="hidden" name="id" id="temporaryNumberId" value="$!{tempNumBean.id}" />
    </div>  
	</form>
    <a href="javascript:goSubmit();" class="easyui-linkbutton" iconCls="icon-ok" >添加</a>
    </center>
</div>
</center>
</body>
</html>
