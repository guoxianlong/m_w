function $() {
return document.getElementById(arguments[0]) || false;
}

var DialogInstance = [];
function showDialog(ID,Width,Height,Title,URL,Content,func) {
//播放警告声音
/*var soundObj = document.getElementById("soundDiv");
if(soundObj){
	soundObj.innerHTML='<embed id="player" src="../error.wav" hidden=true autostart="true"  volume="0"/>';
}*/
if ($(ID)) { document.body.removeChild($(ID)); }
if ($(ID+"Mask")) { document.body.removeChild($(ID+"Mask")); }
 
//mask遮罩层
var Mask = document.createElement("div");
Mask.id = ID + "Mask";
Mask.style.position = "absolute";
Mask.style.zIndex = "1000";
Mask.style.width = parseInt(document.body.scrollWidth) + "px";
Mask.style.height = parseInt(document.body.scrollHeight) + "px";
Mask.style.top = "0px";
Mask.style.left = "0px";
Mask.style.background = "#33393C";
Mask.style.filter = "alpha(opacity=30)";
Mask.style.opacity = "0.30";
document.body.appendChild(Mask);
 
 //新弹出层
var arr = [];
arr.push("<table width='100%' height='100%' border='0' cellspacing='0' cellpadding='0'>");
arr.push("<tr id='_DialogTitle_"+ID+"'>");
//arr.push("<td width='13' height='33' style='color:#000000;font-size:14px;font-weight:bold' bgcolor='#9999ff'>");
//arr.push("<div style='width:13px;'></div></td>");
arr.push("<td height='33' colspan='2' style='color:#000000;font-size:14px;font-weight:bold' bgcolor='#81BEF7'>");
arr.push("<div style='float:left;font-weight:bold; color:#FFFFFF; padding:9px 0 0 4px; font-size:12px;'>");
arr.push(Title+"</div></td></tr>");
//arr.push("<div style='position: relative;cursor:pointer; float:right; margin:5px 0 0; _margin:4px 0 0;height:17px; width:28px; background:url(dialog_closebtn.gif); onMouseOver=\"style.backgroundImage='url(dialog_closebtn_over.gif)';\" onMouseOut=\"style.backgroundImage='url(dialog_closebtn.gif)';\" id='_DialogClose_"+ID+"' drag='false'></div></td>");
//arr.push("<td width='13' height='33' style='background:url(dialog_rt.png); background-repeat:no-repeat;'>");
//arr.push("<div style='width:13px;'></div></td></tr>");
//arr.push("<tr drag='false'><td width='13' style='background:url(dialog_mlm.png); background-repeat:no-repeat;'></td>");
arr.push("<tr><td align='center' valign='top' colspan='2' >");
arr.push("<table width='100%' height='100%' border='0' cellpadding='0' cellspacing='0' bgcolor='#FFFFFF'>");
arr.push("<tr><td align='center' ");
if(URL=="about:blank" || URL=="") {
 arr.push("valign='middle'>");
 arr.push(Content);
} else {
 arr.push("valign='top'>");
 arr.push("<iframe src='"+URL+"' id='_DialogFrame_"+ID+"' allowTransparency='true' width='100%' height='100%' frameborder='0' scrolling='yes' style='background-color: #transparent; border:none;'></iframe>");
}
arr.push("</td></tr>");
if(URL=="about:blank" || URL=="") {
 arr.push("<tr><td height='30' valign='top' colspan='2' style='text-align:center; padding-right:20px;'>");
 arr.push("&nbsp;<input id='_ButtonOK_"+ID+"' type='button' value='确定' onkeydown='if (event.keyCode==13)return false;'>");
 arr.push("</td></tr>");
}
//arr.push("</table></td><td width='13' style='background:url(dialog_mrm.png); background-repeat:no-repeat;'></td></tr>");
//arr.push("<tr><td width='13' height='13' style='background:url(dialog_lb.png); background-repeat:no-repeat;'></td>");
//arr.push("<td style='background-image:url(dialog_cb.png); background-repeat:repeat-x;'></td>");
//arr.push("<td width='13' height='13' style='background-image:url(dialog_rb.png); background-repeat:no-repeat;'></td>");
//arr.push("</tr></table>");

var mDialog = document.createElement("div");
mDialog.id = ID;
mDialog.style.position = "absolute";
mDialog.style.zIndex = "10000";
mDialog.style.width = Width + "px";
mDialog.style.height = Height + "px";
mDialog.style.top = (document.body.clientHeight/2 - Height/2) + "px";
mDialog.style.left = (document.body.clientWidth/2 - Width/2) + "px";
mDialog.innerHTML = arr.join('\n');
document.body.appendChild(mDialog);

//将实例放入队列中
DialogInstance.push(arguments[0]);

//如果是Alert 将焦点移到按钮上
var ButtonOK = $("_ButtonOK_"+ID);
if(ButtonOK) {
 ButtonOK.focus();
ButtonOK.onclick = function() {
 if ((typeof func)=="function") {
 document.body.removeChild($(ID));
 document.body.removeChild($(ID + "Mask"));
 func();
 } else {
 ButtonClose.onclick();
 }
}

}

function Resize() {
Mask.style.width = parseInt(document.body.scrollWidth) + "px";
Mask.style.height = parseInt(document.body.scrollHeight) + "px";
mDialog.style.top = (document.body.scrollTop + document.body.clientHeight/2 - Height/2) + "px";
mDialog.style.left = ( document.body.clientWidth/2 - Width/2) + "px";
}
if(document.all){
window.attachEvent("onresize",Resize);
} else{
window.addEventListener('resize',Resize,false);
}
 
//关闭新图层和mask遮罩层 取消事件监听

var ButtonClose = $("_DialogClose_"+ID);
ButtonClose.onclick = function () {
//var soundObj = document.getElementById("soundDiv");
//if(soundObj)soundObj.innerHTML="";
if(document.all){
 window.detachEvent("onresize",Resize);
} else {
 window.removeEventListener('resize',Resize,false);
}
document.body.removeChild($(ID));
document.body.removeChild($(ID + "Mask"));
return false;
}
}

///////////这是测试调用自己可以利用该功能覆盖window.alert方法实现自定义alert