/**
 * 根据type申请单类型得到action的名称
 */
function selectTypeAction(type) {
	var action = "";
	if (type*1 == 0 || type*1 == 1 || type*1 == 6) {
		action = "depositFinance";
	}
	if (type*1 == 2 || type*1 == 3) {
		action = "subscriptionFinance";
	}
	if (type*1 == 4 || type*1 == 5) {
		action = "payMoneyFinance";
	}
	return action;
}

function addUrl(type,step) {
	var url = "";
	if (type*1 == 0 || type*1 == 1) {
		url = selectTypeAction(type)+".do?method=AddApplication";
	}
	if (type*1 == 6) {
		url = selectTypeAction(type)+".do?method=AddImprestCostApplication";
	}
	if (type*1 == 2 || type*1 == 3 
			|| type*1 == 4 || type*1 == 5) {
		if (step*1 == 1) {
			url = selectTypeAction(type)+".do?method=stockOrderList";
		} else if (step*1 == 2) {
			url = selectTypeAction(type)+".do?method=AddApplication";
		}
	}
	return url;
}

function editUrl(type,step) {
	var url = "";
	if (type*1 == 0 || type*1 == 1) {
		url = selectTypeAction(type)+".do?method=updateApplication";
	}
	if (type*1 == 6) {
		url = selectTypeAction(type)+".do?method=updateImprestCostApplication";
	}
	if (type*1 == 2 || type*1 == 3 
			|| type*1 == 4 || type*1 == 5) {
		if (step*1 == 1) {
			url = selectTypeAction(type)+".do?method=stockOrderList";
		} else if (step*1 == 2) {
			url = selectTypeAction(type)+".do?method=updateApplication";
		}
	}
	return url;
}
/**
 * 日期格式验证
 */
function checkDate(theDate){
  var reg = /^\d{4}-((0{0,1}[1-9]{1})|(1[0-2]{1}))-((0{0,1}[1-9]{1})|([1-2]{1}[0-9]{1})|(3[0-1]{1}))$/;  
  var result=true;
  if(!reg.test(theDate))
    result = false;
  else{
    var arr_hd=theDate.split("-");
    var dateTmp;
    dateTmp= new Date(arr_hd[0],parseFloat(arr_hd[1])-1,parseFloat(arr_hd[2]));
    if(dateTmp.getFullYear()!=parseFloat(arr_hd[0])
       || dateTmp.getMonth()!=parseFloat(arr_hd[1]) -1 
        || dateTmp.getDate()!=parseFloat(arr_hd[2])){
        result = false
    }
  }
  return result;
}
/**
 * 根据申请单类型，返回款项进出字样
 */
function payName(type) {
	var name = "";
	if(type*1 == 0 || type*1 == 2 || type*1 == 4) {
		name = "打款";
	}
	if(type*1 == 1 || type*1 == 3 || type*1 == 5) {
		name = "收款";
	}
	return name;
}
/**
 * 根据申请单类型，返回收付款字样
 */
function moneyModeName(type) {
	var name = "";
	if(type*1 == 0 || type*1 == 2 || type*1 == 4) {
		name = "付款";
	}
	if(type*1 == 1 || type*1 == 3 || type*1 == 5) {
		name = "收款";
	}
	return name;
}
/**
 * 申请单废弃连接
 */
function discardApp(appid,type,page) {
	var action = selectTypeAction(type);
	var url = action+".do?method=discardApplication&type="+type+"&appId="+appid+page;
	if(confirm("如果确认废弃，请单击‘确定’，反之，请单击‘取消’！")) {
		window.location.href = url;
	}
}
/**
 *	checkAll：全选框的名称
 *  checkName：复选框列表的名称
 */
function selectCheckBoxAll(checkAll,checkName) {
	var check = document.getElementsByName(checkAll);
	var name = document.getElementsByName(checkName);
	for(var i=0;i<name.length;i++) {
		if(check[0].checked == true) {
			name[i].checked = true;
		} else {
			name[i].checked = false;
		}
	}
}
/**
 * 验证字数字串,是否符合条件
 * str:要验证的数字串
 * length:数字串允许最长位数
 * dec:小数位的位数
 */
function checkNum(str,length,dec) {
	//总长不超过7位，小数位不超过2位的正则表达式 
	//var reg = /^(?=.{0,7}$)((\d*)|(([1-9][0-9]*|0{1})\.\d{1,2}))$/;
	//if(!reg.test(str)) return false;
	var integer = parseInt(str);//取整数位
	var intLeng = length - dec;//整数位的最长长度
	var flt = str - integer;//取得小数部分
	var fltln = (str.toString()).length - (integer.toString()).length - 1;//小数部分的长度
	if(integer < 0){
		fltln = (str.toString()).length - (integer.toString()).length - 2;
	}
	if((integer.toString()).length > intLeng) return false;
	if(fltln > dec) return false;
	return true;
}

function checkDecimals(value,length){  
    var decallowed = length;  
    var revValue = value;   
    if(revValue.indexOf('.') == -1){  
        revValue += ".";  
    }   
    var dectext = revValue.substring(revValue.indexOf('.')+1, revValue.length);  
    if(dectext.length > decallowed){  
        return false;  
    }else{  
        return true;  
    }  
}
//alert(110);
/**
 * 重写toFixed方法
 */
Number.prototype.toFixed = function(fractionDigits)
{
	return (parseInt(this*Math.pow(10, fractionDigits) + 0.5)/Math.pow(10, fractionDigits)).toString();
}
/**
 * 两个浮点数相加
 */
function addValue(value1,value2) {
    var temp, temp1, temp2;
    try { temp1 = (value1.toString().split(".")[1]).length; } catch (e) { temp1 = 2; }
    try { temp2 = (value2.toString().split(".")[1]).length; } catch (e) { temp2 = 2; }
    temp = Math.pow(10,Math.max(temp1, temp2));
    return (mulValue(value1,temp) + mulValue(value2,temp))/temp;
}
/**
 * 两个浮点数相减
 */
function subValue(value1,value2) {
	return addValue(value1, -value2);
}
/**
 * 两个浮点数相乘
 */
function mulValue(value1,value2) {
    var temp = 0;
    var temp1 = value1.toString();
    var temp2 = value2.toString();
    try { temp += (temp1.split(".")[1]).length; } catch (e) { leng1 = 2; }
    try { temp += (temp2.split(".")[1]).length; } catch (e) { leng2 = 2; }
    return Number(temp1.replace(".","")) * Number(temp2.replace(".",""))/Math.pow(10,temp);
}
/**
 * 两个浮点数相除
 */
function divValue(arg1,arg2) {
    return mulValue(arg1,1/arg2);
}
/**
 * 两个日期之间相差的天数
 */
function DateDiff(sDate1, sDate2) {//sDate1和sDate2是2006-12-18格式  
	var aDate, oDate1, oDate2, iDays;  
	aDate = sDate1.split("-")  
	oDate1 = new  Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);//转换为12-18-2006格式  
	aDate = sDate2.split("-");
	oDate2 = new  Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);
	iDays = parseInt((oDate1 - oDate2) / 1000 / 60 / 60 / 24);//把相差的毫秒数转换为天数  
	return iDays;  
}