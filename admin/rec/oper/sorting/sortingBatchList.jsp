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
voUser user = (voUser) request.getSession().getAttribute("userView");
List areaList = (List) request.getAttribute("areaList");
UserGroupBean group = user.getGroup();
%>
<!DOCTYPE html>
<%
int hour =24;
int m =5;
%>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
	var datagrid;
	var addBrand;
	var userForm;
	var passwordInput;
	var userRoleDialog;
	var userRoleForm;
	var datagrid;
	var editDiv;
	var editForm;
	$(function() {
		    $('#startDay').datebox({ 
			required:true 
			}); 
			datagrid = $('#datagrid').datagrid({
			rownumbers:'true',
			striped:'true',
			url : '${pageContext.request.contextPath}/SortingController/sortingBatchList.mmx',
			toolbar : '#toolbar',
			title : '分拣批次列表',
			iconCls : '',
			pagination : true,
			pageSize : 20,
			pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
			fit : true,
			fitColumns : true,
			nowrap : false,
			border : true,
			idField : 'id',//记住翻页之后已选中相
			fixRowHeight: function(index) {   
				selectRow(index).height=50;
			},
			rowStyler: function(index,row){
				row.createDatetime = row.createDatetime.substring(0,row.createDatetime.length-2);
				if(row.completeDatetime!=''&& row.completeDatetime!=null){
					row.completeDatetime = row.completeDatetime.substring(0,row.completeDatetime.length-2)
				}
			},
			frozenColumns : [ [ {
				field : 'code',
				title : '分拣批次号',
				align :'center',
				sortable : true,
				width:'180',
				formatter : function(value, row, index) {
					if(row.statusName=="无需处理" || row.statusName=="未处理" || row.statusName=="处理中"){
						return row.code;
					}else{
					    return '<a href="javascript:void(0);" class="editbutton" onclick="groupList('+row.id+')">'+row.code+'</a>';
					}
				}
			} ] ],
			columns : [ [ {
				field : 'storageName',
				title : '作业仓',
				align :'center',
					width:'90'
			},{
				field : 'createDatetime',
				align :'center',
				title : '生成日期',
				sortable : true,
				width:'180'
			},{
				field : 'sortingBatchGroupCount',
				title : '分拣波次数',
				align :'center',
				width:'80',
				formatter : function(value, row, index) {
					if(row.sortingBatchGroupCount=="0"){
						return "--";
					}else{
					    return row.sortingBatchGroupCount;
					}
				}
			},{
				field : 'orderCount',
				title : '订单总数',
				align :'center',
				width:'80',
				formatter : function(value, row, index) {
					if(value==0||row.statusName=="分拣中" || row.statusName=="已完成"){
						return value;
					}else{
					    return <%if(group.isFlag(593)){ %>'<a href="javascript:void(0);" class="editbutton" onclick="orderList('+row.id+')">'+<%}%>value<%if(group.isFlag(593)){ %>+'</a>'<%}%>;
					}
				}
			},{
				field : 'skuCount',
				title : 'SKU数',
				align :'center',
				width:'80'
			},{
				field : 'completeDatetime',
				title : '完成时间',
				align :'center',
				width:'180'
			},{
				field : 'statusName',
				title : '状态',
				align :'center',
				width:'90'
				
			},{
				field : 'action',
				title : '操作',
				align :'center',
				width : 100,
				formatter : function(value, row, index) {
					if(row.statusName!="生成中..." && row.orderCount!="0"){
						<%if(group.isFlag(594)){ %>
						if(row.statusName!="分拣中" && row.statusName!="已完成"){
							return '<a href="javascript:void(0);" class="editbutton" onclick="printOrder('+row.id+')">导出全部订单</a>| <a href="javascript:void(0);" class="editbutton" onclick="orderList('+row.id+')">分类/物流|</a> ';
						}else{
							return '<a href="javascript:void(0);" class="editbutton" onclick="printOrder('+row.id+')">导出全部订单</a>';
						}
						<%}%>
					}else{
						return "";
					}
				}
			}] ],
			onLoadSuccess : function(data) {
				try {
					if (data.footer[0].completeBatchOrderCount != 'undefined' && data.footer[0].completeBatchOrderCount !=null && data.footer[0].completeBatchOrderCount != "") {
						$("#completeBatchOrderCount").html("<font color='blue'>今日已完成的分拣批次:"+data.footer[0].completeBatchOrderCount+"</font>");
					} else {
						$("#completeBatchOrderCount").html("");
					}
					if (data.footer[0].noCompleteBatchCount != 'undefined' && data.footer[0].noCompleteBatchCount !=null && data.footer[0].noCompleteBatchCount != "") {
						$("#noCompleteBatchCount").html("<font color='red'>今日未完成的分拣批次:"+data.footer[0].noCompleteBatchCount+"</font>");
					} else {
						$("#noCompleteBatchCount").html("");
					}
					if (data.footer[0].historyNoCompleteCount != 'undefined' && data.footer[0].historyNoCompleteCount !=null && data.footer[0].historyNoCompleteCount != "") {
						$("#historyNoCompleteCount").html("<font color='red'>历史未完成的分拣批次:"+data.footer[0].historyNoCompleteCount+"</font>");
					} else {
						$("#historyNoCompleteCount").html("");
					}
					if (data.footer[0].ordercount != 'undefined' && data.footer[0].ordercount !=null && data.footer[0].ordercount != "") {
						$("#ordercount").html("<font color='red'>至今未处理的订单:"+data.footer[0].ordercount+"</font>");
					} else {
						$("#ordercount").html("");
					}
					if (data.footer[0].lastTime != 'undefined' && data.footer[0].lastTime !=null && data.footer[0].lastTime != "") {
						$("#lastTime").html("上次生成时间:"+data.footer[0].lastTime);
					} else {
						$("#lastTime").html("");
					}
					if (data.footer[0].nextTime != 'undefined' && data.footer[0].nextTime !=null && data.footer[0].nextTime != "") {
						$("#nextTime").html("下次自动生成时间:"+data.footer[0].nextTime);
					} else {
						$("#nextTime").html("");
					}
				} catch(e) {
					$.messager.alert("提示", "错误" ,"info");
				}
			},
			onRowContextMenu : function(e, rowIndex, rowData) {
				e.preventDefault();
				$(this).datagrid('unselectAll');
				$(this).datagrid('selectRow', rowIndex);
				$('#menu').menu('show', {
					left : e.pageX,
					top : e.pageY
				});
			  }
		  });
			$('#storage').combobox({   
				url:'<%=request.getContextPath()%>/SortingController/getStorageList.mmx',
				valueField : 'id',   
				textField : 'storageName',
				panelHeight:'auto'
			}); 

	});
	function searchBrand() {
		datagrid.datagrid('load', {
			storage : $('#searchForm').find('[name=storage]').val(),
			status : $('#searchForm').find('[name=status]').val(),
			select : $('#searchForm').find('[name=select]').val(),
			text : $('#searchForm').find('[name=text]').val(),
			startDay : $('#searchForm').find('[name=startDay]').val(),
			startHour : $('#searchForm').find('[name=startHour]').val(),
			startM : $('#searchForm').find('[name=startM]').val(),
			endHour : $('#searchForm').find('[name=endHour]').val(),
			endM : $('#searchForm').find('[name=endM]').val()
		});
	};
	function searchFlag(a) {
		datagrid.datagrid('load', {
			flag : a
		});
	};
	function append() {
		editDiv.dialog('open');
		editForm.form('clear');
	};
	function showSearchCondition(){
		if($('#select').val()=='0'){
			$('#showSearchDiv1').hide();	
			$('#showSearchDiv').show();
		}else{
			$('#showSearchDiv').hide();	
			$('#showSearchDiv1').show();
		}
	}
	function doClear(){
		var txt1=document.getElementById("text");  
		txt1.value="";  
		txt1.focus();
	}
	function zengcheng(){ 
		location.href = '${pageContext.request.contextPath}/SortingController/createSortingBatch.mmx?sortingType=zengcheng';
	}
	function wuxi(){
		location.href = '${pageContext.request.contextPath}/SortingController/createSortingBatch.mmx?sortingType=wuxi';
	}
	function orderList(id){ 
			window.location.href = '${pageContext.request.contextPath}/admin/rec/oper/sorting/sortingOrderList.jsp?batchId='+id;
	}
	function printOrder(id){ 
		location.href = '${pageContext.request.contextPath}/SortingController/sortingOrderStockPrint.mmx?batchId='+id+'&deliver=all';
    }
	function groupList(id){ 
		
		window.location.href = '${pageContext.request.contextPath}/admin/rec/oper/sorting/sortingBatchGroupList.jsp?batchId='+id;
    }
</script>
</head>
<body class="easyui-layout" fit="true">
	<div region="center" border="false" style="overflow: hidden;">
		<div id="toolbar" class="datagrid-toolbar"  style="height: auto;display: none;">
		<form id="form1" name="form1" method="post">
			<table  id="datas" width="99%" border="0" cellspacing="0">
			  <tr>
			    <td style="color: #000"><h4 id="lastTime"></h4></td>
			    <td style="color: #000"><h4 id="nextTime"></h4></td>
			    <td style="color: #000"><h4>自动生成间隔:30分钟</h4></td>
			    <td style="color: #000"><strong>
			<%if(group.isFlag(596)){ %>
			     <input type="button" name="button3" id="button3" onclick="zengcheng();"  value="增城手动生成分拣批次" />
			     <input type="button" name="button4" id="button4" onclick="wuxi();"  value="无锡手动生成分拣批次" />
			     <%} %>
			    </strong></td>
			  </tr>
			  <tr>
			    <td width="25%"><a href='#' onclick='searchFlag(0)'><h3 id="noCompleteBatchCount" name="noCompleteBatchCount"></h3></a> </td>
			    <td width="25%"><a href='#' onclick='searchFlag(1)'><h3 id="historyNoCompleteCount" name="historyNoCompleteCount"></h3></a></td>
			    <td width="25%"><a href='#' onclick='searchFlag(2)'><h3 id="completeBatchOrderCount" ></h3></a></td>
			    <td width="25%"><h3 id="ordercount"></h3></td>
			  </tr>
			</table>
        </form>
		<form id="searchForm">
				<table class="tableForm">
					
					<tr>
				        <td>
				        	<select name="storage" id="storage" class="easyui-combobox" panelHeight="auto" style="width:100px"></select>
				        </td>
				        <td>
				        	<div align="left">
				        		<select name="status" id="status" class="easyui-combobox" panelHeight="auto" style="width:100px">
							         <option value="">全部状态</option>
							         <option value="0">未处理</option>
							         <option value="1">处理中</option>
							         <option value="2">未分拣</option>
							         <option value="3">分拣中</option>
							         <option value="4">已完成</option>
				        		</select>
				        	</div>
				        </td>
				        <td >
					        <div >
	       						<select name="select" id="select" onChange="showSearchCondition()">
							          <option value="0">订单号/批次号/波次号</option>
							          <option value="1">分拣批次生成时间</option>
	        					</select>
	      					</div>
	      				</td>
				        <td>
				        	<div align="left" id="showSearchDiv">
      	                         <input type="text" name="text" id="text" value="订单号/批次号/波次号" onclick='doClear()'>
                            </div>
				        	<div id="showSearchDiv1" style="display: none">
      	  						<input type=text name="startDay" id="startDay" size="10" value="" />
								<select name="startHour" id="startHour">
									<%for(int i=0;i<hour ;i++){ %>
										<option value="<%=i%>"><%=i %></option><%} %>
        						</select>
        						<select name="startM" id="startM">
							        <%for(int i=1;i<=m ;i++){ %>
							        	<option value="<%=i*10%>"><%=i*10%>分</option>
							        <%} %>
        						</select>至
       							<select name="endHour" id="endHour">
							        <%for(int i=0;i<hour ;i++){ %>
										<option value="<%=i%>"><%=i %></option>
									<%} %>
        						</select>
        						<select name="endM" id="endM">
							        <%for(int i=1;i<=m ;i++){ %>
							        	<option value="<%=i*10%>"><%=i*10%>分</option>
							        <%} %>
        						</select>
      						</div>
				        </td>
						<td colspan="2">
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchBrand();" href="javascript:void(0);">查找</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
		<table id="datagrid" ></table>
	</div>
</body>
</html>