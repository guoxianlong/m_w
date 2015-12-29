<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script>
	function returnVal(obj){
		window.parent.returnValue =obj;
		window.close();
	}
</script>
<center>
	是否还有未完成的任务单?
	<br>
	<input type="button" onclick="returnVal(true)" value="有">&nbsp;&nbsp;
	<input type="button" onclick="returnVal(false)" value="无">
</center>