<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/imei/iMEIProductLog.css" /> 
<script type="text/javascript" charset="UTF-8">

function upLoadData(){
	$("#upLoad").attr("disabled",true);
	document.searchForm.submit();
}

function query(){
	var url = '${pageContext.request.contextPath}/admin/IMEI/queryIMEIProductLog.mmx';
	window.location.href = url;
}

function downloadTemplate(){
	var url = '${pageContext.request.contextPath}/admin/IMEI/downloadTemplate.mmx';
	window.location.href = url;
}

function reLoadData(page,pageUrl){     
    window.location.href=pageUrl;
} 

$(function(){
	var tip = '${tip}';
	if(tip != null&&tip !=""){
		alert(tip);
	}
	$("#upLoad").attr("disabled",false);
});
</script>
</head>
<body>
	<div>
		<fieldset>
		<legend></legend>
			<form id="searchForm" name="searchForm" action="upLoadData.mmx" method="post" enctype="multipart/form-data">
			
				<div class="file-box">文件
					<input type='text' id='textfield' name='path' class='txt' /> 
					<input type='button' class='btn' value='选择文件' />
	 				<input type="file" id="fileField" name="attendance" class="file" size="28" onchange="document.getElementById('textfield').value=this.value" />
 				</div>
			<div align="right">
		  		<input type="button" id="upLoad" value="提交" style="width: 100px;height: 30px" onclick="upLoadData()" />
		  		<input type="button" value="查询" style="width: 100px;height: 30px" onclick="query()"/>
		  	</div>
			</form>
		</fieldset>
	</div>
	<div><hr>操作信息：<a href="javascript:;" onclick="downloadTemplate()">下载模版</a><hr></div>
	<div id="dataListDiv">
		<c:if test="${not empty list}">
		<table border="1" class="dataTable">
			<tr class="title">
				<td>序号</td>
				<td>产品ID</td>
				<td>产品编号</td>
				<td>小店名称</td>
			</tr>
			<c:forEach items="${list}" var="iMEIProductLog" varStatus="in">
				<tr>
				<td>${in.index+1}</td>
				<td><c:choose><c:when test="${iMEIProductLog.productId == 0}"></c:when><c:otherwise>${iMEIProductLog.productId}</c:otherwise></c:choose></td>
				<td>${iMEIProductLog.productCode}</td>
				<td>${iMEIProductLog.storeName}</td>
				</tr>
			</c:forEach>
		</table>
		</c:if>
	</div>
	
	<div align="center">
		<pg:pager url="${pageContext.request.contextPath}/admin/IMEI/queryIMEIProductLog.mmx"   
		    items="${totalCount}"  export="currentPageNumber=pageNumber" maxPageItems="20">  
		<pg:index>  
		    <pg:first unless="current">    
		        <a href="javascript:;" onclick="reLoadData(${pageNumber }, '${pageUrl}');">首页</a>    
		    </pg:first>    
		    <pg:prev>    
		        <a href="javascript:;" onclick="reLoadData(${pageNumber }, '${pageUrl}');">前页</a>    
		    </pg:prev>    
		    <pg:pages>    
		        <c:choose>    
		            <c:when test="${currentPageNumber eq pageNumber}">    
		                <font color="red">${pageNumber }</font>    
		            </c:when>    
		            <c:otherwise>     
		                <a href="javascript:;" onclick="reLoadData(${pageNumber }, '${pageUrl}');">${pageNumber }</a>    
		            </c:otherwise>    
		        </c:choose>    
		    </pg:pages>    
		    <pg:next>    
		        <a href="javascript:;" onclick="reLoadData(${pageNumber }, '${pageUrl}');">后页</a>    
		    </pg:next>    
		    <pg:last unless="current">    
		        <a href="javascript:;" onclick="reLoadData(${pageNumber }, '${pageUrl}');">尾页</a>    
		    </pg:last>  
		</pg:index>  
		</pg:pager>   
	</div>  
</body>
</html>