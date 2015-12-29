<%@ page contentType="text/html;charset=utf-8" %>
<script type="text/javascript" src="/admin/js/jquery.js"></script>
<script type="text/javascript">
<!--
function test(){
	$.ajax({
		type: "GET",
		url: "cargo.do?method=xxx",
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			window.document.getElementById("test").innerHTML = msg;
		}
	});
}
//-->
</script>
<input type="button" value="ajax" onclick="test()"/>
<div id="test"></div>