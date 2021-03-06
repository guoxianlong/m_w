<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>
<%String code =request.getParameter("code"); %>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/ckeditor/ckeditor.js"></script>
<script type="text/javascript" charset="UTF-8">
	var datagrid;
	$(function() {
		datagrid = $('#datagrid').datagrid(
						{	url : '${pageContext.request.contextPath}/fittingController/buyStockinListconfirm.mmx?code=<%=code%>',
							toolbar : '#tb',
							idField : 'id',
							width : 1200,
							height : 100,
							fit : true,
							fitColumns : true,
							striped : true,
							nowrap : false,
							loadMsg : '正在努力为您加载..',
							pagination : true,
							rownumbers : true,
							singleSelect : true,
							pageSize : 10,
							pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
							frozenColumns : [ [ {
								field : 'id',
								title : 'ID',
								width : 20,
								hidden : true
							}, ] ],
							columns : [ [ {
								field : 'productCode',
								title : '配件编号',
								width : 10,
								align : 'center',
								sortable : true
							}, {
								field : 'oriname',
								title : '配件名称',
								width : 30,
								align : 'center',
								sortable : true
							}, {
								field : 'productCount',
								title : '配件数量',
								width : 10,
								align : 'center',
								sortable : true
							} ] ],
							onLoadSuccess : function(data){							
								$('#code').html(data.rows[0].code);
						    	$('#area').html(data.rows[0].area);
						    	$('#createDatetime').html((data.rows[0].createDatetime).substring(0,19));
						    	$('#status').html(data.rows[0].status);
						    	
						    	var count = 0;
					    		$.each(data.rows, function(index,data){
					    			count += parseInt(data.count);
					    		});
					    		$('#allCount').html(count);
						    }
						})
	});
	
	function confirm(value){
		if(value==1){
			$.ajax({
				   type: "POST",
				   dataType : 'json',
				   url: '${pageContext.request.contextPath}/fittingController/updateStockinListconfirm.mmx',
				   data: "code="+$('#code').html()+"&remark="+$("#remark").val()+"&operation=confirm",
				   success: function(msg){
					   console.info(msg.success);
					   if(msg.success == true){
						   window.location.href = "${pageContext.request.contextPath}/admin/fitting/fittingstockin.jsp";
					   } else {
						   $.messager.show({
								msg : msg.msg,
								title : '提示'
							});
					   }
				   }
				});
		}
		if(value==0){
			$.ajax({
				   type: "POST",
				   dataType : 'json',
				   url: '${pageContext.request.contextPath}/fittingController/updateStockinListconfirm.mmx',
				   data: "code="+$('#code').html()+"&remark="+$("#remark").val()+"&operation=UNconfirm",
				   success: function(msg){
					   if(msg.success == true){
						   window.location.href = "${pageContext.request.contextPath}/admin/fitting/fittingstockin.jsp";
					   } else {
						   $.messager.show({
								msg : msg.msg,
								title : '提示'
							});
					   }
				   }
				});
		}
	}

</script>

</head>
<body>
	<table id="datagrid"></table>

<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>配件入库确认</legend>
			<table class="tableForm"  border="0">
				<tr align="center" >
					<th >入库单编号：<span id="code"></span>&nbsp&nbsp&nbsp&nbsp</th>
					<td align="left"></td>
					<th >入库库区：<span id="area"></span>&nbsp&nbsp&nbsp&nbsp</th>
					<td align="left"></td>
					<th >配件总量：<span id="allCount"></span>&nbsp&nbsp&nbsp&nbsp</th>
					<td align="left"></td>
					<th >生成时间：<span id="createDatetime"></span>&nbsp&nbsp&nbsp&nbsp</th>
					<td align="left"></td>	
					<th >状态：<span id="status"></span>&nbsp&nbsp&nbsp&nbsp</th>
					<td align="left"></td>				
				</tr>
				<tr align="left" >
					<th align="left">处理意见：</th>
					<td align="left" colspan="8">
						<input id="remark" name="remark" size="50" maxlength="50"/>
						<input type="submit" name="Submit" value="确认入库" onclick="confirm(1)"/>
						<input type="submit" name="Submit" value="确认不通过" onclick="confirm(0)" />
					</td>
				</tr>
			</table>
		</fieldset>
	</div>	
	<!--  <div id="mainPanel" style="height:100px;float:lfet;" align="center" class="easyui-panel" data-options="title:'入库配件'">
	</div>-->
</body>
</html>