<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../taglibs.jsp"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=request.getParameter("pageTitle") %></title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs6.1.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
<script type="text/javascript">
	var LODOP;//这行语句是为了符合DTD规范
	//打印产品条码
	function onprint(){
		var reg=new RegExp("^\\d{1,3}$");
		//cssStyle = "<style>div{border:0;}body{font-size:12px;}</style>";
		LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		LODOP.PRINT_INIT("产品条码");
		LODOP.SET_PRINT_PAGESIZE(1,"152px","60px","");
		LODOP.SET_PRINT_STYLE("FontSize",5);
		var printNum = document.getElementById("printNum");
		if(printNum.value=="" || printNum.value<=0){
			alert("打印次数不能为空且必须大于0");
			printNum.focus();
			return ;
		}else if(!reg.test(printNum.value)){
			alert("打印次数只能是1-100的整数");
			printNum.focus();
			return ;
		}else if(printNum.value>100){
			alert("打印次数不能大于100");
			printNum.focus();
			return ;
		}
		var num=41;
//		var num1 = printNum.value/2;
		var barcodeWidth=34;
		var tmpbarcode = "<%=request.getParameter("barcode")%>";
		var tmpcode = "<%=request.getParameter("code")%>"+" ";
		//var tmporiname = "<%=request.getParameter("oriname")==null?"":request.getParameter("oriname").replace("\"","\"+\"\\\"\"+\"") %>";
		//if(tmpbarcode.length==13){barcodeWidth+=2.2;}
		//else if(tmpbarcode.length==14){barcodeWidth+=4.5;}
		//else if(tmpbarcode.length==15){barcodeWidth+=9;}
		//if(tmporiname.length>26){
		//	tmporiname = tmporiname.substring(0,26);
		//}
		var num1 = printNum.value;
		num1= Math.round(num1);
		LODOP.SET_PRINTER_INDEX(-1);
		if(printNum.value<=1){
			//LODOP.ADD_PRINT_TEXT("13mm","2mm","95%","5.3mm",tmpcode+tmporiname);
			//LODOP.ADD_PRINT_BARCODE("18mm","2mm",barcodeWidth+"mm","16.9mm","128A",tmpbarcode);
			LODOP.ADD_PRINT_TEXT("5px","19px","95%","10px",tmpcode);
			LODOP.SET_PRINT_STYLEA(0,"FontSize",9);
			LODOP.SET_PRINT_STYLEA(0,"Bold",1);
			LODOP.ADD_PRINT_BARCODE("5mm","3mm","36mm","9mm","128A",tmpbarcode);
			
		//	LODOP.ADD_PRINT_HTML(0,0,"44mm","30mm",cssStyle+document.getElementById("barcodeDiv").innerHTML);
		}else{
			for(var i=0;i<num1;i+=1){
				<%--	if(i==num1-1 && printNum.value%2!=0){
					//LODOP.ADD_PRINT_HTML(parseInt(num*(i))+"mm",0,"44mm","30mm",cssStyle+document.getElementById("barcodeDiv").innerHTML);
					LODOP.ADD_PRINT_TEXT(parseInt(num*(i)+15)+"mm","2mm","40mm","5.3mm","+tmpcode+tmporiname+");
					LODOP.ADD_PRINT_BARCODE(parseInt(num*(i)+20)+"mm","2mm",barcodeWidth+"mm","16.9mm","128A","+tmpbarcode+");
				}else{				
					//alert(i);
					//LODOP.ADD_PRINT_HTML(parseInt(num*(i))+"mm",0,"44mm","30mm",cssStyle+document.getElementById("barcodeDiv").innerHTML);
					//LODOP.ADD_PRINT_HTML(parseInt(num*(i))+"mm","45mm","44mm","30mm",cssStyle+document.getElementById("barcodeDiv").innerHTML);
				LODOP.ADD_PRINT_TEXT(parseInt(num*(i)+(i%2==0?15:16))+"mm","5.8mm","40mm","5.3mm","<%=request.getParameter("code")+" "%><%=request.getParameter("oriname") %>");
	//				LODOP.ADD_PRINT_BARCODE(parseInt(num*(i)+(i%2==0?20:21))+"mm","3.2mm","46.4mm","16.9mm","128A","<%=request.getParameter("barcode") %>");
		//			LODOP.ADD_PRINT_TEXT(parseInt(num*(i)+(i%2==0?15:16))+"mm","55.4mm","40mm","5.3mm","<%=request.getParameter("code")+" "%><%=request.getParameter("oriname") %>");
			//		LODOP.ADD_PRINT_BARCODE(parseInt(num*(i)+(i%2==0?20:21))+"mm","53.4mm","46.4mm","16.9mm","128A","<%=request.getParameter("barcode") %>");
					}--%>
					//LODOP.ADD_PRINT_TEXT(num*(i)+(i%15>00?13:12.9)+"mm","2mm","95%","5.3mm",tmpcode+tmporiname);
					//LODOP.ADD_PRINT_BARCODE(num*(i)+(i%15>0?18:17.9)+"mm","2mm",barcodeWidth+"mm","16.9mm","128A",tmpbarcode);
					//LODOP.ADD_PRINT_TEXT("13mm","2mm","95%","5.3mm",tmpcode+tmporiname);
					//LODOP.ADD_PRINT_BARCODE("18mm","2mm",barcodeWidth+"mm","16.9mm","128A",tmpbarcode);
					LODOP.ADD_PRINT_TEXT("5px","19px","95%","10px",tmpcode);
					LODOP.SET_PRINT_STYLEA(0,"FontSize",9);
					LODOP.SET_PRINT_STYLEA(0,"Bold",1);
					LODOP.ADD_PRINT_BARCODE("5mm","3mm","36mm","9mm","128A",tmpbarcode);
					LODOP.NEWPAGE();
					
			}
		}
		//LODOP.PRINT_DESIGN();
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
	}
	function prive(){
		onprint();
		//LODOP.PRINT_DESIGN();
		//LODOP.PREVIEWB();
	}
	
	function digs(){
		onprint();
	//	LODOP.SET_PREVIEW_WINDOW(1,0,0,"0mm","0mm","");	
		//LODOP.PRINT_DESIGN();		
	}
	
</script>
</head>
<body style="text-align: center;">
<table cellpadding="0" cellspacing="0"  align="center">
<tr><td>打印样式：</td>
<td><div id="barcodeDiv" style=" border: 1px solid #000000; width: 200px; " >
			<%=request.getParameter("code") %>
			<hr size="1">
			<img src="<%=request.getContextPath() %>/barcodeServlet?msg=<%=request.getParameter("barcode") %>&fmt=jpg&type=Code39" width="200" height="60" border="0"/>
		</div></td>
</tr>
<tr><td>打印次数：</td>
<td><input type="text" name="printNum" id="printNum" maxlength="3" size="3"/> &nbsp;注：一次最多允许打印100次</td>
</tr>
</table>

<input value="打印" type="button" onclick="onprint();"/>
<!--<input value="打印" type="button" onclick="digs();"/>-->
</body>
</html>