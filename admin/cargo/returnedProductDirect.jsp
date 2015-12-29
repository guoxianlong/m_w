<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货上架指向管理</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" type="text/css">
<style type="text/css">

select {
	width:160px;
}

</style>
</head>
<body>
	<form id="createForm" action="" method="post">
		<fieldset style="width:1000px;">
			<legend>创建退货上架指向</legend>
			<table border="0" width="98%">
				<tr>
					<td align="right">所属仓库：</td>
					<td>
	  	 				<select class="singleSelect" id="storage"  name="storage" >
      	   					<c:choose>
      	    				<c:when test="${fn:length(storageList)>0}">
      	    					<c:forEach var="item" items="${storageList}">
      	    					<option value="${item.id}">${item.wholeCode}</option>
      	    					</c:forEach>
      	    				</c:when>
      	    				<c:otherwise>
      	    	 				<option value="">无地区权限</option>
      	    				</c:otherwise>
      	    	 			</c:choose>
       	 				</select>					
					</td>
					<td align="right">区域：</td>
					<td>
						<select class="singleSelect" id="storageArea"  name="storageArea">
						</select>
					</td>
					<td align="right">巷道：</td>
					<td>
						<select class="multiSelect" id="passage"  name="passage">
						</select>
					</td>					
					<td align="right">层数：</td>
					<td>
						<select class="multiSelect" id="floorNum"  name="floorNum">
						</select>
					</td>						
				</tr>
				<tr>
					<td align="right">一级分类：</td>
					<td>
	  	 				<select class="multiSelect" id="firstCatalog"  name="firstCatalog" >
	  	 					<c:forEach var="item" items="${firstCatalogList}">
      	    				<option value="${item.id}">${item.name}</option>
      	    				</c:forEach>
       	 				</select>					
					</td>
					<td align="right">二级分类：</td>
					<td>
						<select class="multiSelectB" id="secondCatalog"  name="secondCatalog">
						</select>
						（可选）
					</td>
					<td align="right">三级分类：</td>
					<td colspan="3">
						<select class="multiSelectB" id="thirdCatalog"  name="thirdCatalog">
						</select>
						（可选）
					</td>	
				</tr>
				<tr>
					<td align="right"><input id="configDefaultStorageArea" type="checkbox"/></td>
					<td colspan="3">
						是否设置默认区域（若所限制的区域内无任何可用货位时，指定默认区域）
					</td>
					<td align="right">默认区域：</td>
					<td>
	  	 				<select disabled class="singleSelect" id="defaultStorageArea"  name="defaultStorageArea" >
       	 				</select>					
					</td>
					<td colspan="2">
						<input type="button" value="创建退货上架指向" id="createBtn">
					</td>
				</tr>
			</table>
		</fieldset>
	</form>
	<form id="queryForm" action="" method="post"> 
		<fieldset style="width:1000px;">
			<legend>退货上架指向列表查询</legend>
			<table border="0" width="98%">	
				<tr>
					<td align="right">所属仓库：</td>
					<td>
	  	 				<select class="singleSelect" id="qryStorage"  name="qryStorage" >
	  	 					<option value="">不限</option>
      	   					<c:choose>
      	    				<c:when test="${fn:length(storageList)>0}">
      	    					<c:forEach var="item" items="${storageList}">
      	    					<option value="${item.id}">${item.wholeCode}</option>
      	    					</c:forEach>
      	    				</c:when>
      	    				<c:otherwise>
      	    	 				<option value="">无地区权限</option>
      	    				</c:otherwise>
      	    	 			</c:choose>
       	 				</select>					
					</td>
					<td align="right">区域：</td>
					<td>
						<select class="singleSelect" id="qryStorageArea"  name="qryStorageArea">
						</select>
					</td>
					<td align="right">指向状态：</td>
					<td>
						<select class="singleSelect" id="qryStatus"  name="qryStatus">
							<option value="" >不限</option>
							<option value="1" >已生效</option>
							<option value="0" >已作废</option>
						</select>
						</select>
					</td>					
					<td align="right">指向编号：</td>
					<td>
						<input type="text" class="ui-state-default" value="${directCode}" id="directCode" name="directCode">
					</td>						
				</tr>			
				<tr>				
					<td align="right">创建人：</td>
					<td>
						<input type="text" class="ui-state-default" value="${operator}" id="operator" name="operator">
					</td>
					<td align="right">创建时间：</td>
					<td>
						<input type="text" class="ui-state-default"  readonly name="createDateStart" id="createDateStart" size="10" value="${createDateStart}"
	  	   					onClick="WdatePicker({maxDate:'#F{$dp.$D(\'createDateEnd\')}'});" />
	  	   					&nbsp;-&nbsp;
						<input type="text" class="ui-state-default"  readonly name="createDateEnd" id="createDateEnd" size="10" value="${createDateEnd}"
	  	   					onClick="WdatePicker({minDate:'#F{$dp.$D(\'createDateStart\')}'});" />
					</td>
					<td colspan="4">
						<input type="button" value="查询" id="queryBtn">
					</td>										
				</tr>					
			</table>
		</fieldset>
	</form>	
	<table id="datagrid"></table> 
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-1.7.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/ui/jquery-ui-1.8.17.custom.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/multiselect/jquery.multiselect.js"></script>
<link href="${pageContext.request.contextPath}/jquery/themes/ui-lightness/jquery-ui-1.8.17.custom.css"" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/jquery/multiselect/jquery.multiselect.css"" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script language="JavaScript" src="${pageContext.request.contextPath}/admin/js/pts.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/easyui/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript">
var modifyFlag="";
function loadDatagrid(){
	var storage="";
	if($("#queryForm #qryStorage").multiselect("getChecked").length>0){
		storage=$("#queryForm #qryStorage").multiselect("getChecked")[0].value;
	}
	var storageArea="";
	if($("#queryForm #qryStorageArea").multiselect("getChecked").length>0){
		storageArea=$("#queryForm #qryStorageArea").multiselect("getChecked")[0].value;
	}
	var status="";
	if($("#queryForm #qryStatus").multiselect("getChecked").length>0){
		status=$("#queryForm #qryStatus").multiselect("getChecked")[0].value;
	}
	$('#datagrid').datagrid('load', {
		storage:storage,
		storageArea:storageArea,
		status:status,
		directCode:$("#queryForm #directCode").val(),
		operator:$("#queryForm #operator").val(),
		createDateStart:$("#queryForm #createDateStart").val(),
		createDateEnd:$("#queryForm #createDateEnd").val()
	});
}
function clearCreateForm(){
	$("#createForm #createBtn").val("创建退货上架指向");
	//仓库
	$("#createForm #storage").multiselect("uncheckAll");
	//仓库区域
	$("#createForm #storageArea").empty();
	$("#createForm #storageArea").multiselect("refresh");
	//巷道
	$("#createForm #passage").empty();
	$("#createForm #passage").multiselect("refresh");
	//层数
	$("#createForm #floorNum").empty();
	$("#createForm #floorNum").multiselect("refresh");
	//一级分类
	$("#createForm #firstCatalog").multiselect("uncheckAll");
	//二级分类
	$("#createForm #secondCatalog").empty();
	$("#createForm #secondCatalog").multiselect("refresh");	
	//三级分类
	$("#createForm #thirdCatalog").empty();
	$("#createForm #thirdCatalog").multiselect("refresh");
	//默认区域
	$("#createForm #defaultStorageArea").empty();
	$("#createForm #defaultStorageArea").multiselect("refresh");
	$("#createForm #configDefaultStorageArea").get(0).checked=false;
	$("#createForm #defaultStorageArea").attr("disabled","disabled");
	$("#createForm #defaultStorageArea").multiselect("disable");	
}
$(function($) {
	var viewLog = "${viewLog}";
	
	//多选select初始化
	$(".multiSelect").multiselect(
			{ 
				minWidth:120,
				selectedList:1
			}
	);
	$(".multiSelect").multiselect("uncheckAll");
	//多选select初始化B
	$(".multiSelectB").multiselect(
			{ 
				minWidth:120,
				noneSelectedText:'不限',
				selectedList:1
			}
	);	
	$(".multiSelectB").multiselect("uncheckAll");
	//单选select初始化
	$(".singleSelect").multiselect(
			{ 
				minWidth:120,
				selectedList:1,
				header:'请选择',
				multiple:false
			}
	);	
	$(".singleSelect").multiselect("uncheckAll");
	//获取仓库区域
	$("#createForm #storage").multiselect(
			{
				click: function(event, ui){
					if($("#createForm #storage").multiselect("getChecked")[0].value==""){
						return;
					}
					$.ajax({
						type: "get",
						url: "${pageContext.request.contextPath}/returnedProductDirect/getStockAreaList.mmx?storageId="+$("#createForm #storage").multiselect("getChecked")[0].value,
						cache: false,
						dataType: "json",
						error: function(){
							alert("网络错误!");
						},
						success: function(items){
							//仓库区域
							$("#createForm #storageArea").empty();
							for(var i=0;i<items.length;i++){
								$("#createForm #storageArea").append("<option value='"+items[i].id+"'>"+items[i].code+"-"+items[i].name+"</option>");
							}
							$("#createForm #storageArea").multiselect("refresh");
							$("#createForm #storageArea").multiselect("uncheckAll");
							//清空巷道
							$("#createForm #passage").empty();
							$("#createForm #passage").multiselect("refresh");
							//默认仓库区域
							$("#createForm #defaultStorageArea").empty();
							for(var i=0;i<items.length;i++){
								$("#createForm #defaultStorageArea").append("<option value='"+items[i].id+"'>"+items[i].code+"-"+items[i].name+"</option>");
							}
							$("#createForm #defaultStorageArea").multiselect("refresh");
							$("#createForm #defaultStorageArea").multiselect("uncheckAll");
						}
					});
				}
			}
	);
	//获取巷道
	$("#createForm #storageArea").multiselect(
			{
				click: function(event, ui){
					if($("#createForm #storageArea").multiselect("getChecked")[0].value==""){
						return;
					}
					$.ajax({
						type: "get",
						url: "${pageContext.request.contextPath}/returnedProductDirect/getPassageList.mmx?stockAreaId="+$("#createForm #storageArea").multiselect("getChecked")[0].value,
						cache: false,
						dataType: "json",
						error: function(){
							alert("网络错误!");
						},
						success: function(items){
							$("#createForm #passage").empty();
							for(var i=0;i<items.length;i++){
								$("#createForm #passage").append("<option value='"+items[i].id+"'>"+items[i].code+"</option>");
							}
							//巷道多选
							$("#createForm #passage").multiselect("refresh");
							$("#createForm #passage").multiselect("uncheckAll");
						}
					});					
				}
			}
	);
	//获取层数
	$("#createForm #passage").multiselect(
			{
				close: function(event, ui){
						$("#createForm #floorNum").empty();
						$("#createForm #floorNum").multiselect("refresh");
						var checkedItemArr = $("#createForm #passage").multiselect("getChecked");
						if(checkedItemArr.length>0){
							var passageStr = "";
							for(var i=0;i<checkedItemArr.length;i++){
								passageStr+=","+checkedItemArr[i].value;
							}
							passageStr=passageStr.substr(1);
							$.ajax({
								type: "get",
								url: "${pageContext.request.contextPath}/returnedProductDirect/getMaxFloorNum.mmx?passageId="+passageStr,
								cache: false,
								dataType: "text",
								error: function(){
									alert("网络错误!");
								},
								success: function(items){
									$("#createForm #floorNum").empty();
									for(var i=1;i<=parseInt(items);i++){
										$("#createForm #floorNum").append("<option value='"+i+"'>第"+i+"层</option>");
									}
									//层数多选
									$("#createForm #floorNum").multiselect("refresh");
									$("#createForm #floorNum").multiselect("uncheckAll");
								}
							});	
						}
				}
			}			
	);	
	
	//获取二级分类
	$("#createForm #firstCatalog").multiselect(
			{
				close: function(event, ui){
					 $("#createForm #secondCatalog").empty();
					 var checkedItemArr = $("#createForm #firstCatalog").multiselect("getChecked");
					 if(checkedItemArr.length>0){
						 var html = "";
						 for(var i=0;i<checkedItemArr.length;i++){
							 var checkItem=checkedItemArr[i];
							 var index=checkItem.id.split("-")[4];
							 html+="<optgroup label='"+checkItem.title+"'>";
							 var target = spts[index];
							 for(var j=0;j<target.length;j++){
								 if(target[j].value==0){
									 continue;
								 }
								 html+="<option value='"+index+"-"+j+"-"+target[j].value+"'>"+target[j].text+"</option>";
							 } 
							 html+="</optgroup>";
						 }
						 $("#createForm #secondCatalog").append(html);
					 }
					 $("#createForm #secondCatalog").multiselect("refresh");
			 		 $("#createForm #secondCatalog").multiselect("uncheckAll");
			 		 //三级分类清空
					 $("#createForm #thirdCatalog").empty();
					 $("#createForm #thirdCatalog").multiselect("refresh");
				}
			}			
	);		
	
	//获取三级分类
	$("#createForm #secondCatalog").multiselect(
			{
				close: function(event, ui){
					 $("#createForm #thirdCatalog").empty();
					 var checkedItemArr = $("#createForm #secondCatalog").multiselect("getChecked");
					 if(checkedItemArr.length>0){
						 var html = "";
						 for(var i=0;i<checkedItemArr.length;i++){
							 var checkItem=checkedItemArr[i];
							 var firstIndex=checkItem.value.split("-")[0];
							 var secondIndex=checkItem.value.split("-")[1];
							 html+="<optgroup label='"+checkItem.title+"'>";
							 var target = tpts[firstIndex][secondIndex];
							 for(var j=0;j<target.length;j++){
								 if(target[j].value==0){
									 continue;
								 }
								 html+="<option value='"+target[j].value+"'>"+target[j].text+"</option>";
							 }
							 html+="</optgroup>";
						 }
						 $("#createForm #thirdCatalog").append(html);
					 }
					 $("#createForm #thirdCatalog").multiselect("refresh");
			 		 $("#createForm #thirdCatalog").multiselect("uncheckAll");
				}
			}			
	);		
	//默认区域
	$("#createForm #configDefaultStorageArea").click(
		function(){
			if($(this).get(0).checked){
				$("#createForm #defaultStorageArea").removeAttr("disabled");
				$("#createForm #defaultStorageArea").multiselect("enable");
			}else{
				$("#createForm #defaultStorageArea").attr("disabled","disabled");
				$("#createForm #defaultStorageArea").multiselect("uncheckAll");
				$("#createForm #defaultStorageArea").multiselect("disable");
			}
			
		}
	);
	
	
	//创建退货指向逻辑 createBtn
	$("#createForm #createBtn").click(
		function(){
			//检测输入项
			if($("#createForm #storage").multiselect("getChecked").length==0){
				alert("请选择所属仓库！");
				return;
			}
			if($("#createForm #storageArea").multiselect("getChecked").length==0){
				alert("请选择区域！");
				return;
			}
			if($("#createForm #passage").multiselect("getChecked").length==0){
				alert("请选择巷道！");
				return;
			}
			if($("#createForm #floorNum").multiselect("getChecked").length==0){
				alert("请选择层数！");
				return;
			}			
			if($("#createForm #firstCatalog").multiselect("getChecked").length==0){
				alert("请选择一级分类！");
				return;
			}
			if($("#createForm #configDefaultStorageArea").get(0).checked){
				if($("#createForm #defaultStorageArea").multiselect("getChecked").length==0){
					alert("请选择默认区域！");
					return;
				}
			}
			//防止多次点击
			$("#createForm #createBtn").attr("disabled","disabled");
			//所属仓库
			var storage=$("#createForm #storage").multiselect("getChecked")[0].value;
			var storageName=$("#createForm #storage").multiselect("getChecked")[0].title;
			//区域
			var storageArea=$("#createForm #storageArea").multiselect("getChecked")[0].value;
			var storageAreaName=$("#createForm #storageArea").multiselect("getChecked")[0].title;
			//巷道
			var passage ="";
			var passageName="";
			var passageSel = $("#createForm #passage").multiselect("getChecked");
			for(var i=0;i<passageSel.length;i++){
				passage+=","+passageSel[i].value;
				passageName+=","+passageSel[i].title;
			}
			passage=passage.substr(1);
			passageName=passageName.substr(1);
			//层数
			var floorNum ="";
			var floorNumSel = $("#createForm #floorNum").multiselect("getChecked");
			for(var i=0;i<floorNumSel.length;i++){
				floorNum+=","+floorNumSel[i].value;
			}
			floorNum=floorNum.substr(1);
			//一级分类
			var firstCatalog ="";
			var firstCatalogName="";
			var firstCatalogSel = $("#createForm #firstCatalog").multiselect("getChecked");
			for(var i=0;i<firstCatalogSel.length;i++){
				firstCatalog+=","+firstCatalogSel[i].value;
				firstCatalogName+=","+firstCatalogSel[i].title;
			}
			firstCatalog=firstCatalog.substr(1);
			firstCatalogName=firstCatalogName.substr(1);
			//二级分类
			var secondCatalog ="";
			var secondCatalogName ="";
			if($("#createForm #secondCatalog").multiselect("getChecked").length>0){
				var secondCatalogSel = $("#createForm #secondCatalog").multiselect("getChecked");
				for(var i=0;i<secondCatalogSel.length;i++){
					secondCatalog+=","+secondCatalogSel[i].value.split("-")[2];
					secondCatalogName+=","+secondCatalogSel[i].title;
				}
				secondCatalog=secondCatalog.substr(1);
				secondCatalogName=secondCatalogName.substr(1);
			}
			//三级分类
			var thirdCatalog ="";
			var thirdCatalogName ="";
			if($("#createForm #thirdCatalog").multiselect("getChecked").length>0){
				var thirdCatalogSel = $("#createForm #thirdCatalog").multiselect("getChecked");
				for(var i=0;i<thirdCatalogSel.length;i++){
					thirdCatalog+=","+thirdCatalogSel[i].value;
					thirdCatalogName+=","+thirdCatalogSel[i].title;
				}
				thirdCatalog=thirdCatalog.substr(1);
				thirdCatalogName=thirdCatalogName.substr(1);
			}			
			//默认区域
			var defaultStorageArea="";
			var defaultStorageAreaName="";
			if($("#createForm #configDefaultStorageArea").get(0).checked){
				defaultStorageArea=$("#createForm #defaultStorageArea").multiselect("getChecked")[0].value;
				defaultStorageAreaName=$("#createForm #defaultStorageArea").multiselect("getChecked")[0].title;
			}
			var url="";
			if(modifyFlag==""){
				url="${pageContext.request.contextPath}/returnedProductDirect/create.mmx";
			}else{
				url="${pageContext.request.contextPath}/returnedProductDirect/update.mmx";
			}
			$.ajax({
				type: "post",
				url: url,
				data:{storage:storage,storageArea:storageArea,passage:passage,floorNum:floorNum,firstCatalog:firstCatalog,
						secondCatalog:secondCatalog,thirdCatalog:thirdCatalog,defaultStorageArea:defaultStorageArea,
						storageName:storageName,storageAreaName:storageAreaName,passageName:passageName,firstCatalogName:firstCatalogName,
						secondCatalogName:secondCatalogName,thirdCatalogName:thirdCatalogName,defaultStorageAreaName:defaultStorageAreaName,
						directId:modifyFlag
						},
				cache: false,
				dataType: "json",
				error: function(){
					alert("网络错误!");
					$("#createForm #createBtn").removeAttr("disabled");
				},
				success: function(items){
					$("#createForm #createBtn").removeAttr("disabled");
					if(items.success){
						alert(items.msg);
						if(modifyFlag!=""){
							modifyFlag="";
						}
						loadDatagrid();
						clearCreateForm();
					}else{
						alert(items.msg);
					}
				}
			});				
		}		
	);
	//获取仓库区域
	$("#queryForm #qryStorage").multiselect(
			{
				click: function(event, ui){
					if($("#queryForm #qryStorage").multiselect("getChecked")[0].value==""){
						return;
					}
					$.ajax({
						type: "get",
						url: "${pageContext.request.contextPath}/returnedProductDirect/getStockAreaList.mmx?storageId="+$("#queryForm #qryStorage").multiselect("getChecked")[0].value,
						cache: false,
						dataType: "json",
						error: function(){
							alert("网络错误!");
						},
						success: function(items){
							//仓库区域
							$("#queryForm #qryStorageArea").empty();
							$("#queryForm #qryStorageArea").append("<option value=''>不限</option>");
							for(var i=0;i<items.length;i++){
								$("#queryForm #qryStorageArea").append("<option value='"+items[i].id+"'>"+items[i].code+"-"+items[i].name+"</option>");
							}
							$("#queryForm #qryStorageArea").multiselect("refresh");
							$("#queryForm #qryStorageArea").multiselect("uncheckAll");
						}
					});
				}
			}
	);
	//查询结果
	$('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/returnedProductDirect/query.mmx',
		width : 1200,
		border: true,
		fitColumns : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    collapsible:true,
	    pageSize : 10,
	    pageList : [10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[]
	    ],
		queryParams: {
			status: '1'
		},
	    onLoadSuccess:function(){
	    	clearCreateForm();
	    },
	    columns:[[
	        {field:'directCode',title:'退货指向编号',width:105,align:'center'},
	        {field:'storageName',title:'仓库',width:60,align:'center'},  
	        {field:'stockAreaName',title:'区域',width:60,align:'center'}, 
	        {field:'passageNum',title:'巷道数',width:40,align:'center',
	        	formatter:function(val,rec){  
	        		return "<a target='_blank' style='color:#0000ff' href='${pageContext.request.contextPath}/admin/cargo/returnedProductDirectPassage.jsp?directId="+rec.id+"'>"+val+"</a>";
	        	}
	        }, 
	        {field:'stockType',title:'库存类型',width:60,align:'center',
	        	formatter:function(val,rec){  
	        		return rec.stockTypeName;
	        	}
	        }, 
	        {field:'floorNum',title:'已选择的层数',width:60,align:'center'}, 
	        {field:'firstCatalogName',title:'一级分类',width:80,align:'center'},
	        {field:'secondCatalogName',title:'二级分类',width:80,align:'center'},
	        {field:'thirdCatalogName',title:'三级分类',width:80,align:'center'},
	        {field:'defaultStockAreaName',title:'默认区域',width:60,align:'center'},
	        {field:'createDatetime',title:'创建时间',width:80,align:'center'},
	        {field:'status',title:'指向状态',width:60,align:'center',
	        	formatter:function(val,rec){ 
	        		if(val==1){
	        			return "已生效";
	        		}else{
	        			return "已作废";
	        		}
	        	}
	        },
	        {field:'operatorName',title:'操作人',width:60,align:'center'},
	        {field:'operation',title:'操作',width:60,align:'center',
	        	formatter:function(val,rec){
	        		if(rec.status=="1"){
		        		return "<a style='color:#0000ff' href='javascript:void(0);' onclick='cancel("+rec.id+")'>作废</a>"
    					+"&nbsp;&nbsp;<a href='javascript:void(0);' style='color:#0000ff' id='rows"+rec.id
    					+"' onclick='modify(\""
    							+rec.id+"\",\""
    							+rec.storageId+"\",\""
    							+rec.stockAreaId+"\",\""
    							+rec.passageId+"\",\""
    							+rec.floorNum+"\",\""
    							+rec.firstCatalogId+"\",\""
    							+rec.secondCatalogId+"\",\""
    							+rec.thirdCatalogId+"\",\""
    							+rec.defaultStockAreaId+"\")'>修改</a>";
	        		}else{
	        			return "";	        		}
	        	}
	        },
	        {field:'log',title:'操作记录',width:60,align:'center',
	        	formatter:function(val,rec){
	        		if(viewLog=="1"){
	        			return "<a target='_blank' style='color:#0000ff' href='${pageContext.request.contextPath}/admin/cargo/returnedProductDirectLog.jsp?directId="+rec.id+"&directCode="+rec.directCode+"'>人员操作记录</a>";
	        		}else{
	        			return "";	        		}
	        	}
	        }
	    ]]
	});
	$("#queryForm #queryBtn").click(
		function(){
			loadDatagrid();
		}
	);
});
//作废记录
function cancel(directId){
	if(confirm("确定作废此条记录？")){
		$.ajax({
			type: "get",
			url: "${pageContext.request.contextPath}/returnedProductDirect/cancel.mmx?directId="+directId,
			cache: false,
			dataType: "json",
			error: function(){
				alert("网络错误!");
			},
			success: function(items){
				if(items.success){
					alert(items.msg);
					loadDatagrid();
				}else{
					alert(items.msg);
				}
			}
		});
	}
}
//修改记录
function modify(id,storageId,stockAreaId,passageId,floorNum,firstCatalogId,secondCatalogId,thirdCatalogId,defaultStockAreaId){
	if(modifyFlag!=""){
		if(modifyFlag!=id){
			$("#rows"+modifyFlag).html("修改");
		}else{
			$("#rows"+id).html("修改");
			clearCreateForm();
			modifyFlag="";
			return;
		}
	}
	modifyFlag=id;
	$("#rows"+id).html("取消");
	$("#createForm #createBtn").val("修改提交");
	//directId
	$("#createForm #directId").val(id);
	//仓库
	$("#createForm #storage").val(storageId);
	$("#createForm #storage").multiselect("refresh");
	//仓库区域和默认仓库区域
	$.ajax({
		type: "get",
		async: false,
		url: "${pageContext.request.contextPath}/returnedProductDirect/getStockAreaList.mmx?storageId="+storageId,
		cache: false,
		dataType: "json",
		error: function(){
			alert("网络错误!");
		},
		success: function(items){
			//仓库区域
			$("#createForm #storageArea").empty();
			for(var i=0;i<items.length;i++){
				$("#createForm #storageArea").append("<option value='"+items[i].id+"'>"+items[i].code+"-"+items[i].name+"</option>");
			}
			$("#createForm #storageArea").val(stockAreaId);
			$("#createForm #storageArea").multiselect("refresh");

			//默认仓库区域
			$("#createForm #defaultStorageArea").empty();
			for(var i=0;i<items.length;i++){
				$("#createForm #defaultStorageArea").append("<option value='"+items[i].id+"'>"+items[i].code+"-"+items[i].name+"</option>");
			}
			if(defaultStockAreaId!="undefined"){
				if(!$("#createForm #configDefaultStorageArea").get(0).checked){
					$("#createForm #configDefaultStorageArea").get(0).checked=true;
					$("#createForm #defaultStorageArea").removeAttr("disabled");
					$("#createForm #defaultStorageArea").multiselect("enable");
				}
				$("#createForm #defaultStorageArea").val(defaultStockAreaId);
				$("#createForm #defaultStorageArea").multiselect("refresh");
				
			}else{
				if($("#createForm #configDefaultStorageArea").get(0).checked){
					$("#createForm #configDefaultStorageArea").get(0).checked=false;
					$("#createForm #defaultStorageArea").attr("disabled","disabled");
					$("#createForm #defaultStorageArea").multiselect("disable");
				}
				$("#createForm #defaultStorageArea").multiselect("refresh");
				$("#createForm #defaultStorageArea").multiselect("uncheckAll");
			}
		}
	});	
	//巷道
	$.ajax({
		type: "get",
		async: false,
		url: "${pageContext.request.contextPath}/returnedProductDirect/getPassageList.mmx?stockAreaId="+$("#createForm #storageArea").multiselect("getChecked")[0].value,
		cache: false,
		dataType: "json",
		error: function(){
			alert("网络错误!");
		},
		success: function(items){
			$("#createForm #passage").empty();
			for(var i=0;i<items.length;i++){
				$("#createForm #passage").append("<option value='"+items[i].id+"'>"+items[i].code+"</option>");
			}
			//巷道多选
			$("#createForm #passage").multiselect("refresh");
			$("#createForm #passage").multiselect("uncheckAll");
			var checkboxArr=$("#createForm #passage").multiselect("getAll");
			var passageArr=passageId.split(",");
			for(var i=0;i<checkboxArr.length;i++){
				for(var j=0;j<passageArr.length;j++){
					if(passageArr[j]==checkboxArr[i].value){
						checkboxArr[i].checked="checked";
					}
				}
			}
			$("#createForm #passage").multiselect("update");
		}
	});		
	//层数
	$.ajax({
		type: "get",
		url: "${pageContext.request.contextPath}/returnedProductDirect/getMaxFloorNum.mmx?passageId="+passageId,
		cache: false,
		dataType: "text",
		error: function(){
			alert("网络错误!");
		},
		success: function(items){
			$("#createForm #floorNum").empty();
			for(var i=1;i<=parseInt(items);i++){
				$("#createForm #floorNum").append("<option value='"+i+"'>第"+i+"层</option>");
			}
			//层数多选
			$("#createForm #floorNum").multiselect("refresh");
			$("#createForm #floorNum").multiselect("uncheckAll");
			var checkboxArr=$("#createForm #floorNum").multiselect("getAll");
			var floorNumArr=floorNum.split(",");
			for(var i=0;i<checkboxArr.length;i++){
				for(var j=0;j<floorNumArr.length;j++){
					if(floorNumArr[j]==checkboxArr[i].value){
						checkboxArr[i].checked="checked";
					}
				}
			}
			$("#createForm #floorNum").multiselect("update");
		}
	});	
	//一级分类
	var fcCheckboxArr=$("#createForm #firstCatalog").multiselect("getAll");
	var firstCatalogArr=firstCatalogId.split(",");
	for(var i=0;i<fcCheckboxArr.length;i++){
		for(var j=0;j<firstCatalogArr.length;j++){
			if(firstCatalogArr[j]==fcCheckboxArr[i].value){
				fcCheckboxArr[i].checked="checked";
			}
		}
	}
	$("#createForm #firstCatalog").multiselect("update");	
	//二级分类
	$("#createForm #secondCatalog").empty();
	var fcCheckedItemArr = $("#createForm #firstCatalog").multiselect("getChecked");
	if(fcCheckedItemArr.length>0){
		var html = "";
		for(var i=0;i<fcCheckedItemArr.length;i++){
			var checkItem=fcCheckedItemArr[i];
			var index=checkItem.id.split("-")[4];
			html+="<optgroup label='"+checkItem.title+"'>";
			var target = spts[index];
			for(var j=0;j<target.length;j++){
				 if(target[j].value==0){
					 continue;
				 }
				html+="<option value='"+index+"-"+j+"-"+target[j].value+"'>"+target[j].text+"</option>";
			} 
			html+="</optgroup>";
		}
		$("#createForm #secondCatalog").append(html);
	}
	$("#createForm #secondCatalog").multiselect("refresh");
	$("#createForm #secondCatalog").multiselect("uncheckAll");
	if(secondCatalogId!="undefined"){
		var scCheckboxArr=$("#createForm #secondCatalog").multiselect("getAll");
		var sCatalogArr=secondCatalogId.split(",");
		for(var i=0;i<scCheckboxArr.length;i++){
			for(var j=0;j<sCatalogArr.length;j++){
				var trueValue=scCheckboxArr[i].value.split("-")[2];
				if(sCatalogArr[j]==trueValue){
					scCheckboxArr[i].checked="checked";
				}
			}
		}
		$("#createForm #secondCatalog").multiselect("update");	
	}
	//三级分类
	$("#createForm #thirdCatalog").empty();
	var scCheckedItemArr = $("#createForm #secondCatalog").multiselect("getChecked");
	if(scCheckedItemArr.length>0){
		var html = "";
		for(var i=0;i<scCheckedItemArr.length;i++){
			var checkItem=scCheckedItemArr[i];
			var firstIndex=checkItem.value.split("-")[0];
			var secondIndex=checkItem.value.split("-")[1];
			html+="<optgroup label='"+checkItem.title+"'>";
			var target = tpts[firstIndex][secondIndex];
			for(var j=0;j<target.length;j++){
				 if(target[j].value==0){
					 continue;
				 }
				html+="<option value='"+target[j].value+"'>"+target[j].text+"</option>";
			}
			html+="</optgroup>";
		}
		$("#createForm #thirdCatalog").append(html);
	}
	$("#createForm #thirdCatalog").multiselect("refresh");
	$("#createForm #thirdCatalog").multiselect("uncheckAll");
	if(thirdCatalogId!="undefined"){
		var thCheckboxArr=$("#createForm #thirdCatalog").multiselect("getAll");
		var thCatalogArr=thirdCatalogId.split(",");
		for(var i=0;i<thCheckboxArr.length;i++){
			for(var j=0;j<thCatalogArr.length;j++){
				if(thCatalogArr[j]==thCheckboxArr[i].value){
					thCheckboxArr[i].checked="checked";
				}
			}
		}
		$("#createForm #thirdCatalog").multiselect("update");				 
	}
}

</script>    
</body>
</html>


