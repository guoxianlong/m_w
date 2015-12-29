<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	voUser loginUser = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = loginUser.getGroup();
%>
<!DOCTYPE html>
<html>
<head>
<title>产品查询</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	initAllcombobox();
	initProductDataGrid ();
});

function initProductDataGrid () {
	$('#productDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SearchProductController/searchProductList.mmx',
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		toolbar : '#tb',
//暂时不添加toolbar，原因是看不到导出可修改的页面，也不能修改等级
// 		toolbar : '#tb',
		columns : [ [ {
			width : 40,
			checkbox: true
		}, {
			field : 'id',
			title : 'ID',
			width : 50,
			align : 'center'
		}, {
			field : 'code',
			title : '编号',
			width : 80,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'&simple=1" >'+value+'</a>'; 
			}
		}, {
			field : 'rank',
			title : '等级',
			width : 40,
			align : 'center'
		}, {
			field : 'proxyName',
			title : '供应商',
			width : 30,
			align : 'center'
		}, {
			field : 'name',
			title : '小店名称',
			width : 100,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'" >'+value+'</a>'; 
			}
		}, {
			field : 'oriname',
			title : '原名称',
			width : 100,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.id+'" >'+value+'</a><font color="red">标准化信息</font>'; 
			}
		}, {
			field : 'price',
			title : '价格',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return value+'元'; 
			}
		},{
			field : 'groupBuyPrice',
			title : '团购价格',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return value+'元'; 
			}
		}, {
			field : 'price2',
			title : '市场价',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return value+'元'; 
			}
		}, {
			field : 'price5',
			title : '库存价格',
			width : 40,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				<%  
				//库存价格 ---延用权限ID：41（批发价）。无权限的人员查看时，列表字段库存价格用“--”显示
				//要记得还有一个限制
				if(group.isFlag(41)){%>
					if (row.flag) {
						return value+'元';
					} else {
						return "--";
					}
				<%} else {%>
					return "--";
				<%}%>
			}
		}, {
			field : 'statusName',
			title : '状态',
			width : 60,
			align : 'center'
		}, {
			field : 'commentCount',
			title : '查看评论',
			width : 60,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/comments.do?productId='+row.id+'" >查看('+value+')</a>'; 
			}
		}, {
			field : 'chakan',
			title : '多图',
			width : 50,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				return '<a href="<%=request.getContextPath() %>/admin/productpics.do?productId='+row.id+'" >查看</a>'; 
			}
		}, {
			field : 'pic2',
			title : '大图',
			width : 50,
			align : 'center',
			formatter : function(value, row, rowIndex) {
				if (row.pic2 == null || row.pic2 == 'undefined' || row.pic2.length < 3 ){
					return "无";
				} else {
					return "<a href='"+row.fullPic2+"' ><image border=0 src='"+row.fullPic2+"' width=20 height=20></a>"; 
				}
			}
		}, {
			field : 'canBeShipStock',
			title : '可发货总数',
			width : 50,
			align : 'center'
		}, {
			field : 'allStockCount',
			title : '库存总数',
			width : 50,
			align : 'center'
		}, {
			field : 'action',
			title : '操作',
			width : 50,
			align : 'center',
			formatter : function(value, row, index) {
				var action = "";
				<%if(group.isFlag(51)){ %>
					action = action + '<a href="<%=request.getContextPath() %>/admin/fProductMark.do?id='+row.id+'">编辑坏货/已返厂</a>';
				<%}%>
				<%if(group.isFlag(293)){ %> 
					if (row.productBarcodeVO != null && row.productBarcodeVO.barcode != null) {
						action = action + '|<a href="javascript:void(0);" onclick="onPrintSubmit(\''+row.code+'\',\''+row.oriname+'\',\''+row.productBarcodeVO.barcode+'\'); return false;">打印条码</a>';
					}
				<%} %>
				if (row.parentId1 == 111) {
					action = action + '|<a href="<%=request.getContextPath()%>/admin/productFaq.do?method=selFAQ&productId='+row.id+'&flag=1" target="_blank">FAQ</a>';
				}
				return action;
			}
		} ] ]
	});
};

function initAllcombobox() {
	var parentId1 = $('#parentId1').combobox({
		url : '${pageContext.request.contextPath}/SearchProductController/getparentId1.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		onSelect : function(record){
			$('#parentId2').combobox({
				url : '${pageContext.request.contextPath}/SearchProductController/getparentId2.mmx?parentId1=' + record.id,
				valueField : 'id',
				textField : 'text',
				editable : false,
				onSelect : function(record){
					$('#parentId3').combobox({
						url : '${pageContext.request.contextPath}/SearchProductController/getparentId3.mmx?parentId2='+record.id,
						valueField : 'id',
						textField : 'text',
						editable : false
					}).combobox('clear');
				}
			}).combobox('clear');
			$('#parentId3').combobox({
				url : '${pageContext.request.contextPath}/SearchProductController/getparentId3.mmx?parentId2=-1',
				valueField : 'id',
				textField : 'text',
				editable : false
			}).combobox('clear');
		}
	});
	var parentId2 = $('#parentId2').combobox({
		url : '${pageContext.request.contextPath}/SearchProductController/getparentId2.mmx?parentId1=-1',
		valueField : 'id',
		textField : 'text',
		editable : false,
		onSelect : function(record){
			$('#parentId3').combobox({
				url : '${pageContext.request.contextPath}/SearchProductController/getparentId3.mmx?parentId2='+record.id,
				valueField : 'id',
				textField : 'text',
				editable : false
			}).combobox('clear');
		}
	});
	
	var parentId3 = $('#parentId3').combobox({
		url : '${pageContext.request.contextPath}/SearchProductController/getparentId3.mmx?parentId2=-1',
		valueField : 'id',
		textField : 'text',
		editable : false
	});
	
	var proxyList = $('#proxyList').combobox({
		url : '${pageContext.request.contextPath}/SearchProductController/getProxyList.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false
	});
	
	$.getJSON("${pageContext.request.contextPath}/SearchProductController/getProductStatus.mmx",null,function(response){ 
		  var listHtml=''; 
		//循环取json中的数据,并呈现在列表中 
		    $.each(response,function(i){ 
			    listHtml += " <input type='checkbox'"; 
			    listHtml += " name=product_status"; 
			    listHtml += " id=product_status"+response[i].id; 
			    listHtml += " value='"+response[i].id+"'/>"; 
			    listHtml += response[i].text + "&nbsp;&nbsp;"; 
		    }); 
		    $("#product_status").html(listHtml); 
		} 
	); 
};

function productGridSearch() {
	if (checksubmit()) {
		$("#productDataGrid").datagrid("load",{
			code : $('#searchProductForm').find('[name=code]').val(),
			barcode : $('#searchProductForm').find('[name=barcode]').val(),
			name : $('#searchProductForm').find('[name=name]').val(),
			minPrice : $('#searchProductForm').find('[name=minPrice]').val(),
			maxPrice : $('#searchProductForm').find('[name=maxPrice]').val(),
			startTime : $('#searchProductForm').find('[id=startTime]').datebox("getValue"),
			endTime : $('#searchProductForm').find('[id=endTime]').datebox("getValue"),
			minPrice5 : $('#searchProductForm').find('[name=minPrice5]').val(),
			maxPrice5 : $('#searchProductForm').find('[name=maxPrice5]').val(),
			productId : $('#searchProductForm').find('[id=productId]').numberbox("getValue"),
			parentId1 : $('#searchProductForm').find('[id=parentId1]').combobox("getValue"),
			parentId2 : $('#searchProductForm').find('[id=parentId2]').combobox("getValue"),
			parentId3 : $('#searchProductForm').find('[id=parentId3]').combobox("getValue"),
			proxyList : $('#searchProductForm').find('[id=proxyList]').combobox("getValue"),
			products : $("#searchProductForm [name='products'][type='radio'][checked]").val(),
			product_status : getCheckBoxValue(),
			minProductStock : $('#searchProductForm').find('[id=minProductStock]').numberbox("getValue"),
			maxProductStock : $('#searchProductForm').find('[id=maxProductStock]').numberbox("getValue")
		});
	}
};

//获取checkbox值
function getCheckBoxValue() {
    var str="";
    $("#searchProductForm [name='product_status']:checkbox").each(function(){ 
        if($(this).attr("checked")){
            str += $(this).val()+","
        }
    })
    return str.substring(0, str.length-1);
};
function getDateFromString(strDate){
    var arrYmd   =  strDate.split("-");
    var numYear  =  parseInt(arrYmd[0],10);
    var numMonth =  parseInt(arrYmd[1],10)-1;
    var numDay   =  parseInt(arrYmd[2],10);
    var leavetime=new Date(numYear,  numMonth,  numDay);
    return leavetime;

}
function checksubmit(){

	var startTime = $('#searchProductForm').find('[id=startTime]').datebox("getValue");
	var endTime = $('#searchProductForm').find('[id=endTime]').datebox("getValue");
	
	var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	if(startTime.length!=0 && endTime.length!=0){
		if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
			$.messager.alert("提示", "添加时间，请输入正确的格式！如：2011-08-10", "info");
			return false;
		}	
	
		if((endTime.length!=0 && endTime.length!=10) || !r.test(endTime)){
			$.messager.alert("提示", "添加时间，请输入正确的格式！如：2011-08-10", "info")
			return false;
		}
		var day = (getDateFromString(endTime)-getDateFromString(startTime))/(1000*60*60*24);
		if(day<0){
			$.messager.alert("提示", "添加时间，起始日期不能大于截止日期。\n请重新输入！ ", "info");
			return false;
		}
		if(day>30){
			$.messager.alert("提示", "添加时间，两个日期之差不得大于31。\n请重新输入！", "info");
			return false;
		}
	}
	if(startTime.length!=0&&endTime.length==0 ){
		$.messager.alert("提示", "添加时间，请输入截止日期！", "info");
		return false;
	}
    if(startTime.length==0&&endTime.length!=0 ){
    	$.messager.alert("提示", "添加时间，请输入起始日期！", "info")
		return false;
	}
	var minStock=$('#searchProductForm').find('[id=minProductStock]').numberbox("getValue");
	var maxStock=$('#searchProductForm').find('[id=maxProductStock]').numberbox("getValue");
	if(minStock!=""&&maxStock!=""){
	 	minStock=eval("("+minStock+")");
	 	maxStock=eval("("+maxStock+")");
	 	if(minStock>maxStock){
	 		$.messager.alert("提示", "可发货总数:第二个输入框大于等于第一输入框！", "info");
	 	    return false;
	 	}
	 }else{
	 	if(""==minStock&&""!=maxStock){
	 		$.messager.alert("提示", "两个框都要输入值，请输入可发货总数！", "info");
	 		return false;
	 	}
	 	if(""!=minStock&&""==maxStock){
	 		$.messager.alert("提示", "两个框都要输入值，请输入可发货总数！", "info");
		 	return false;
	 	}
	 }
	return true;
}

//去掉前后空格
function trim(str) {
	return str.replace(/(^\s*)|(\s*$)/g, "");
}

//提交表单打印条码
function onPrintSubmit(code,oriname,barcode){
	$("#codeId").attr("value", code);
	$("#orinameId").attr("value", oriname);
	$("#barcodeId").attr("value", barcode);
	$("#printBarocdeFormId").submit();
}
</script>
</head>
<body>
		<div id="tb" style="padding:3px;height: auto;">
			<form id="searchProductForm">
				<fieldset>
					<legend>筛选</legend>
					<span>产品编号：</span>
					<input name='code' id='code' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>商品条码：</span>
					<input name='barcode' id='barcode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>产品名称：&nbsp;&nbsp;</span>
					<input name='name' id='name' style="width:150px;border:1px solid #ccc"/><span>（模糊）</span>&nbsp;&nbsp;
					<br>
					<span>产品价格：</span>
					<input name='minPrice' id='minPrice' style="width:150px;border:1px solid #ccc"/>&nbsp;到&nbsp;<input name='maxPrice' id='maxPrice' style="width:150px;border:1px solid #ccc"/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<span>添加时间：&nbsp;&nbsp;</span>
					<input class="easyui-datebox" name='startTime' id='startTime' style="width:152px;border:1px solid #ccc"/>&nbsp;到&nbsp;<input class="easyui-datebox" name='endTime' id='endTime' style="width:152px;border:1px solid #ccc"/><br>
					<%if(group.isFlag(41)){%>
						<span>库存价格：</span>
						<input name='minPrice5' id='minPrice5' style="width:150px;border:1px solid #ccc"/>&nbsp;到&nbsp;<input name='maxPrice5' id='maxPrice5' style="width:150px;border:1px solid #ccc"/><br>
					<%} %>
					<span>产品状态：</span>
					<span id="product_status"></span>
					<br>
					<span>产品ID：&nbsp;&nbsp;</span>
					<input name='productId' id='productId' class="easyui-numberbox" data-options="min:1" style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<br>
					<span>一级分类：</span>
					<input id="parentId1" name="parentId1" style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>二级分类：</span>
					<input id="parentId2" name="parentId2" style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
					<span>三级分类：&nbsp;&nbsp;</span>
					<input id="parentId3" name="parentId3" style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;<br>
					<span>供应商：&nbsp;&nbsp;</span>
					<input id="proxyList" name="proxyList"  style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;<br>
					<%if(group.isFlag(19)){  %>
						<span>主子商品：</span>
						<input type="radio" name="products" value="0" checked="checked" /> 全部
						<input type="radio" name="products" value="1"/> 主商品
						<input type="radio" name="products" value="2"/> 子商品
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span>可发货总数：</span>
						从 
						<input class="easyui-numberbox" name="minProductStock" id="minProductStock" data-options="min:1"  style="width:150px;border:1px solid #ccc"/> 
						&nbsp;到&nbsp; 
						<input class="easyui-numberbox" name="maxProductStock" id="maxProductStock"  data-options="min:1"  style="width:150px;border:1px solid #ccc"/> 
					<% } %>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="productGridSearch();">查询产品</a>
				</fieldset>
			</form>
		</div>
	<table id="productDataGrid"></table>
	<div id="tb" style="display:none;">
		<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="excel_product();">导出已选项</a>
		<% if(group.isFlag(19)){ %> 
		<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="excel_allProduct();">导出全部</a>
		<%} %>
		<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" onclick="mProductRank();">修改商品等级</a>
	</div>
	<form name="printBarocdeForm" id="printBarocdeFormId" action="${pageContext.request.contextPath}/admin/barcodeManager/printProcBarcode.jsp" method="post"  target="_blank">
   		<input id="codeId" type="hidden"  name="code"/>
   		<input id="orinameId" type="hidden"  name="oriname"/>
   		<input id="barcodeId" type="hidden"  name="barcode"/>
   		<input type="hidden" name="pageTitle" value="打印产品条码">
   </form>
</body>
</html>