<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>扫描复核帮助</title>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
		<p align="center">扫描复核说明</p>
		<div style="padding-left:  40px;">
		<P>1、如何复核产品及产品数量？</P>
		<P>答：方法一，请扫描产品外包装上或产品上贴着的可用条码。每扫描一次，相应产品的复核量加1。如果扫描失败或没有贴条码，请看方法二。</P>
		<P>&nbsp;&nbsp;&nbsp;&nbsp; 方法二，请单击产品列表中该产品所在行的最后一列‘+1’按钮。每单击一次，该产品的复核量加1。</P>
		<P>2、复核产品完成后，怎么办？</P>
		<P>答：方法一，请再扫描一次发货清单编号。</P>
		<P>&nbsp;&nbsp;&nbsp;&nbsp; 方法二，请单击‘复核完毕确认出货’按钮。</P>
		<P>3、如果同一款产品的数量非常大，有没有快捷的复核办法？</P>
		<P>答：有。扫描一次后，手动输入其复核量，然后单击‘修改复核量’按钮。（有修改复核量的权限的账号才能操作）。</P>
		<P>4、扫描了一部分产品后，想重新开始扫描复核，有快捷办法吗？</P>
		<P>答：请单击‘重新扫描’按钮，将还原到复核开始前，可以重新开始复核。</P>
		</div>
		<p align="center"><input type="button" value="关闭" onclick="window.close();"/></p>
</body>
</html>