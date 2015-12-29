<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script>
	function returnVal(obj){
		window.parent.returnValue =obj;
		window.close();
	}
</script>
<center>
	尚未录入不合格品结果记录，是否确认完成？
	<br>
	<input type="button" onclick="returnVal(true)" value="是">&nbsp;&nbsp;
	<input type="button" onclick="returnVal(false)" value="否">
</center>