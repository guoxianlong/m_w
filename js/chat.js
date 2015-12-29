var URL_BASE = "";
var CHAT_POST_URL = "../PostChatMessage.do";
var CHAT_GET_URL = "";
var CHAT_MESSAGES_URL = "../ChatMessages.do";
var ADMIN_ONLINE_URL = "../GetChatUserList.do?method=list";
var CHAT_ADMIN_LOGIN_URL = "../ChatAdminLogin.do";
var QUICK_ANSWER_URL = "../QuickAnswers.do";
var RECOMMEND_PRODUCT_URL = "../RecommendProducts.do";
var DELETE_CHAT_MESSGE_URL = "../DChatMessage.do";
var GET_BAN_USERS_URL = "../GetChatUserList.do?method=getBanUsers";
//var GET_CHAT_USER_IP_INFO = "../GetChatUserList.do?method=getChatUserIpInfo";
var adminOnline = false;
var allChatMaxId = 0;
var adminUserId = 0;
var userIdTarget = 0;
var targetChatMaxId = 0;
var _hTargetChat = null;

var admin = new Object();
admin.adminUserId = 0;
var initTargetChatLock = new Object();
initTargetChatLock.lock = false;
var getChatMessageLock = new Object();
getChatMessageLock.lock = false;
getChatMessageLock.scrollLock=false;

function initAdminOnline(){
	refreshAdmin();
}
function refreshAdmin(){
	$.ajax({
		type: "GET",
		url: ADMIN_ONLINE_URL,
		cache: false,
		dataType: "xml",
		data: 'type=1',
		success: function(msg, reqStatus){
			var $msg = $(msg);
			$("#adminOnlineDiv").empty();
			$(msg).find("chatUser").each(function(){
				var nick = decodeURIComponent($(this).find("nick").text());
				var userId = parseInt($(this).find("userId").text());
				var $chatAdmin = $("<span>" + nick + "</span>");
				$chatAdmin.data("userId", userId);
				$("#adminOnlineDiv").append($chatAdmin);
			});
			setTimeout("refreshAdmin();", 3000);
		}
	});
}

function getAllChatMessage(){
	$.ajax({
		type: "GET",
		url: CHAT_MESSAGES_URL,
		cache: false,
		dataType: "xml",
		data: {type: "0", maxId: allChatMaxId},
		success: function(msg, reqStatus){
		    $("#allChatMessage").empty();
			var $msg = $(msg);
			$(msg).find("chatMessage").each(function(){
				var id = parseInt($(this).find("id").text());
				var nick = decodeURIComponent($(this).find("nick").text());
				var content = decodeURIComponent($(this).find("content").text());
				var title = decodeURIComponent($(this).find("title").text())
				var userId = parseInt($(this).find("userId").text());
				var userIdTarget = parseInt($(this).find("userIdTarget").text());
				var status = parseInt($(this).find("status").text());
				var phone = $(this).find("phone").text();
				var createDatetime = $(this).find("createDatetime").text();
				var readSpan = "<span class=\"unRead\">" ;
				if(status ==1)
					readSpan = "<span class=\"read\">";
				var cMsg = "<span id=\"" + id + "\"><span class=\"chatUserNick\">" + nick + "</span>(" + phone + ")说:" + readSpan+content + "</span></span><br/>";
				var $chatMessage = $(cMsg);
				$chatMessage.data("userId", userId);
				$chatMessage.bind("click", function(){
					$(this).css("color", "red");
					window.parent.frames["productFrame"].window.initTargetChat($(this).data("userId"));
					var iframe = window.parent.frames["productFrame"].window.document.getElementById("historyList");
					if(iframe != null){
						iframe.src = "http://www.mmb.cn/wap/www/history.jsp?userId=" + $(this).data("userId");
					}
				});
				if(id > allChatMaxId){
					allChatMaxId = id;
				}
				//$("#allChatMessage").append($chatMessage);
				$("#allChatMessage").prepend($chatMessage);

				/*document.all[document.all.length-1].scrollIntoView();
				window.scroll(0,document.body.scrollHeight);
				*/
			});
			//setTimeout("getAllChatMessage();", 3000);
		}
	});
}
function getChatMessages(){
	if(getChatMessageLock.lock == true){
		return;
	} else {
		getChatMessageLock.lock = true
	}
	$.ajax({
		type: "GET",
		url: CHAT_MESSAGES_URL,
		cache: false,
		dataType: "xml",
		data: {type: "1", maxId: targetChatMaxId, adminUserId: adminUserId, userId: userIdTarget},
		success: function(msg, reqStatus){
			var $msg = $(msg);
			$(msg).find("chatMessage").each(function(){
				var id = parseInt($(this).find("id").text());
				var nick = decodeURIComponent($(this).find("nick").text());
				var content = decodeURIComponent($(this).find("content").text());
				var title = decodeURIComponent($(this).find("title").text());
				var userId = parseInt($(this).find("userId").text());
				var userIdTarget = parseInt($(this).find("userIdTarget").text());
				var isAdmin = parseInt($(this).find("isAdmin").text());
				var phone = $(this).find("phone").text();
				var createDatetime = $(this).find("createDatetime").text();
				var assortId = $(this).find("assortId").text();
				var className = "" ;
				if(isAdmin == 1)
					className= " class=\"admin\"";
				var $chatMessage = $("<span"+className+" id='"+id+"' >" + nick + ":" + title + "</span><br/>");
				$chatMessage.data("messageId", userId);
				if(id > targetChatMaxId){
					targetChatMaxId = id;
				}
				var htmlInner='';
				var deleteHtml="<span style=\"cursor:pointer;\" onclick=\"if(confirm('确认要删除该信息?'))deleteMessage(this, '"+ id + "');\">[删除]";
				htmlInner+="<span class='chatUserMessage' >" + nick + ":&nbsp;&nbsp;&nbsp;("+ createDatetime+")</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
				if(isAdmin==1){
					htmlInner+=deleteHtml;
				}
				htmlInner+="</span><br/><span "+className+" style='margin-left:15px' >"+title+"</span><br/>";
				window.parent.frames["targetFrame"].window.document.getElementById("targetChatMessage").innerHTML =
				  window.parent.frames["targetFrame"].window.document.getElementById("targetChatMessage").innerHTML+htmlInner; 
				  $(window.parent.frames["adminChatFrame"].window.document.getElementById("assortId")).val(assortId);
				//$(window.parent.frames["targetFrame"].window.document.getElementById("targetChatMessage")).append($chatMessage);
				//$("#targetChatMessage").append($chatMessage);
			});
			//_hTargetChat = setTimeout("getChatMessages()", 10000);
			getChatMessageLock.lock = false;
			if(getChatMessageLock.scrollLock==true){
				window.parent.frames["targetFrame"].window.document.body.scrollTop = window.parent.frames["targetFrame"].window.document.body.scrollHeight;
				getChatMessageLock.scrollLock=false;
			}
		}
	});
}
function deleteMessage(spanObj, msgId){
	$.ajax({
		type: "GET",
		url: DELETE_CHAT_MESSGE_URL,
		cache: false,
		data: {id: msgId},
		success: function(msg, reqStatus){
			spanObj.onclick=function(){return false;};
			$(spanObj).css("cursor", "none");
			$(spanObj).text("[已删除]");
		}
	});
}
function initTargetChat(tui){
	//userIdTarget = tui;
	if(tui == userIdTarget){
		//return;
	}
	if(initTargetChatLock.lock == true){
		return;
	} else {
		initTargetChatLock.lock = true
	}
	window.parent.frames["productFrame"].window.clearTimeout(window.parent.frames["productFrame"].window._hTargetChat);
	userIdTarget = tui;
	targetChatMaxId = 0;
	window.parent.frames["targetFrame"].window.document.getElementById("targetChatMessage").innerHTML = "";
	$.ajax({
		type: "GET",
		url: ADMIN_ONLINE_URL,
		cache: false,
		dataType: "xml",
		data: 'type=2'+'&userId='+userIdTarget,
		success: function(msg, reqStatus){
			var $msg = $(msg);
			if($(msg).find("chatUser").size() == 0){
				alert("该用户暂时不在线");
			} else {
				$(msg).find("chatUser").each(function(){
					var id = parseInt($(this).find("id").text());
					var nick = decodeURIComponent($(this).find("nick").text());
					var userId = parseInt($(this).find("userId").text());
					var $chatAdmin = $("<span>" + nick + "</span>");
					var fr = parseInt($(this).find("fr").text());
					var frType = parseInt($(this).find("frType").text());
					var phone = $(this).find("phone").text();
					var columnsId =$(this).find("columnsId").text();
					var productId = parseInt($(this).find("product > productId").text());
					var productName = decodeURIComponent($(this).find("product > productName").text());
					/*
					$("#targetUserNick").text(nick);
					$("#targetUserPhone").text(phone);
					$("#targetUserFr").text(fr);
					*/
					$(window.parent.frames["targetFrame"].window.document.getElementById("targetUserId")).val(id);
					$(window.parent.frames["targetFrame"].window.document.getElementById("targetUserNick")).html(nick);
					$(window.parent.frames["targetFrame"].window.document.getElementById("userID")).html(userId);
					$(window.parent.frames["targetFrame"].window.document.getElementById("targetUserPhone")).text(phone);
					$(window.parent.frames["targetFrame"].window.document.getElementById("targetUserFr")).text(fr);
					$(window.parent.frames["targetFrame"].window.document.getElementById("targetUserProductId")).text(productName);
					window.parent.frames["targetFrame"].window.document.getElementById("targetUserProductLink").href = "http://mmb.cn/wap/shop/product.do?id=" + productId;
					
					$(window.parent.frames["adminChatFrame"].window.document.getElementById("chatTarget")).html(nick);
					$(window.parent.frames["adminChatFrame"].window.document.getElementById("adminChatTarget")).val(userId);
					window.parent.frames["adminChatFrame"].window.document.getElementById("sendShortMessage").href="../sms/send.jsp?dst="+phone+"&status=0"
				});
				getChatMessageLock.scrollLock=true;
				window.parent.frames["productFrame"].window.getChatMessages();
			}
			initTargetChatLock.lock = false;
		}
	});
}

function initAdminLogin(){
	$.ajax({
		type: "GET",
		url: CHAT_ADMIN_LOGIN_URL,
		cache: false,
		dataType: "xml",
		data: {type: "0"},
		success: function(msg, reqStatus){
			var $msg = $(msg);
			var result = $msg.find("message > result").text();
			if(result == "true"){
				adminOnline = true;
				admin.adminUserId = parseInt($msg.find("message > adminUser > id").text());
				$("#logout").bind("click", function(){
					chatAdminLogout();
				});
				$("#logout").addClass("onlineButton");
				$("#login").unbind("click");
				$("#login").removeClass("onlineButton");
			} else {
				adminOnline = false;
				$("#login").bind("click", function(){
					chatAdminLogin();
				});
				$("#login").addClass("onlineButton");
				$("#logout").unbind("click");
				$("#logout").removeClass("onlineButton");
			}
			
//			if(adminOnline){
//				$("#logout").bind("click", function(){
//					chatAdminLogout();
//				});
//				$("#logout").addClass("onlineButton");
//			} else {
//				$("#login").bind("click", function(){
//					chatAdminLogin();
//				});
//				$("#login").addClass("onlineButton");
//			}
			//getChatUserList();
			getQuickAnswerList();
		}
	});
}
function chatAdminLogin(){
	$.ajax({
		type: "GET",
		url: CHAT_ADMIN_LOGIN_URL,
		cache: false,
		dataType: "xml",
		data: {type: "1"},
		success: function(msg, reqStatus){
			var $msg = $(msg);
			var result = $msg.find("message > result").text();
			if(result == "true"){
				admin.adminUserId = parseInt($msg.find("message > adminUser > id").text());
				adminOnline = true;
				$("#logout").bind("click", function(){
					chatAdminLogout();
				});
				$("#logout").addClass("onlineButton");
				$("#login").unbind("click");
				$("#login").removeClass("onlineButton");
			}
		}
	});
}
function chatAdminLogout(){
	$.ajax({
		type: "GET",
		url: CHAT_ADMIN_LOGIN_URL,
		cache: false,
		dataType: "xml",
		data: {type: "2"},
		success: function(msg, reqStatus){
			var $msg = $(msg);
			var result = $msg.find("message > result").text();
			if(result == "true"){
				adminOnline = false;
				admin.adminUserId = 0;
				$("#login").bind("click", function(){
					chatAdminLogin();
				});
				$("#login").addClass("onlineButton");
				$("#logout").unbind("click");
				$("#logout").removeClass("onlineButton");
			}
		}
	});
}

function clearChatUser(){
	$.ajax({
		type: "GET",
		url: ADMIN_ONLINE_URL,
		cache: false,
		dataType: "xml",
		data: 'type=3',
		success: function(msg, reqStatus){
			alert("已经清空在线聊天用户列表");
		}
	});
}
function getChatUserList(){
	$.ajax({
		type: "GET",
		url: ADMIN_ONLINE_URL,
		cache: false,
		dataType: "xml",
		data: 'type=0'+'&adminUserId='+adminUserId,
		success: function(msg, reqStatus){
			var $msg = $(msg);
			$("#chatUserList").empty();
			$(msg).find("chatUser").each(function(){
				var id = parseInt($(this).find("id").text());
				var nick = decodeURIComponent($(this).find("nick").text());
				var userId = parseInt($(this).find("userId").text());
				var userIdTarget = parseInt($(this).find("userIdTarget").text());
				var phone = $(this).find("phone").text();
				var fr = $(this).find("fr").text();
				var status = $(this).find("status").text();
				var cuMsg = "<span id='" + userId + "'></span>";
				var $chatUser = $(cuMsg);
				if(status==0){
					$chatUser.addClass("chatUserUnRead");
				}else if(status==1){
					$chatUser.addClass("chatUserNick");
				}
				 
				if($('#selectUserId').val()==userId){ //选中的用户  如果是有未读数据。则改版颜色  
					$chatUser.removeClass();
					$chatUser.addClass("chatUserRead");
					if(status==0){
						$chatUser.append("<img id='imgStyle' src='/adult-admin/images/msg_prompt.gif' />");
					}
				}
				$chatUser.append(nick+"<br/>");
				$chatUser.data("userDate", {id:userId,userStatus:status});
				$chatUser.bind("click", function(){
					window.parent.frames["productFrame"].window.initTargetChat($(this).data("userDate").id);
					//window.parent.frames["allFrame"].window.getChatUserIpInfo($(this).data("userDate").id);
					window.parent.frames["allFrame"].window.getChatUserWeekMessages($(this).data("userDate").id);
					$(this).addClass("chatUserRead");
					var tempSelectUserId= $('#selectUserId').val();
					$('#selectUserId').val(userId);
					if(tempSelectUserId!="" && userId!=tempSelectUserId){
						$('#'+tempSelectUserId).removeClass();
						$('#'+tempSelectUserId).addClass('chatUserNick');
					}
					if($(this).data("userDate").userStatus==0){
						var parms = 'userId='+userId;
						$.ajax({
							type: "post",
							url: "../GetChatUserList.do?method=updateUserStatus",
							cache: false,
							dataType: "text",
							data:parms,
							success: function(text){
								targetChatMaxId=0;
								$('#imgStyle').hide();
							},
							error:function(XMLHttpRequest, textStatus, errorThrown){
						   		alert(textStatus);
						    }
						});	
							
					}
				});
				$("#chatUserList").append($chatUser);
				//$("#chatUserList").prepend($chatUser);
			});
			//document.all[document.all.length-1].scrollIntoView();
			//window.scroll(0,document.body.scrollHeight);
			setTimeout("getChatUserList();", 3000);
		}
	});
}
function getQuickAnswerList(){
	$.ajax({
		type: "GET",
		url: QUICK_ANSWER_URL,
		cache: false,
		dataType: "xml",
		data: {type: "0"},
		success: function(msg, reqStatus){
			var $msg = $(msg);
			$("#quickAnswerDiv").empty();
			$("#quickAnswerDiv").append("<span style=\"width:100%; height:20px;\">快捷回复：</span><br/>");
			$(msg).find("quickAnswer").each(function(){
				var id = parseInt($(this).find("id").text());
				var content = decodeURIComponent($(this).find("content").text());
				var title = decodeURIComponent($(this).find("title").text());
				var $quickAnswer = $("<span id=\"" + id + "\" class=\"quickAnswer\">" + title + "</span><br/>");
				$quickAnswer.data("content", content);
				$quickAnswer.bind("click", function(){
					var taObj = window.parent.frames["adminChatFrame"].window.document.getElementById("chatMessage");
					var message = "";
					message = $(this).data("content");
					insertAtCursor(taObj, message);
					return false;
				});
				$("#quickAnswerDiv").append($quickAnswer);
				//document.all[document.all.length-1].scrollIntoView();
				//window.scroll(0,document.body.scrollHeight);
			});
			setTimeout("getQuickAnswerList();", 3600 * 1000);
		}
	});
}

function getFrameElement(frameName){
	
}

function insertAtCursor(myField, myValue){   
	if(document.selection){   
		myField.focus();
		sel = document.selection.createRange();
		sel.text = myValue;
	} else if (myField.selectionStart || myField.selectionStart == "0"){
		sel = document.selection.createRange();
		var t = txb.createTextRange();
		t.collapse(true);//将光标移到头
		t.select();
		var startPos = myField.selectionStart;
		var endPos = myField.selectionEnd;
		myField.value = myField.value.substring(0, startPos) + myValue + myField.value.substring(endPos, myField.value.length);
		sel.collapse(false);
		sel.select();
	} else {
		myField.value += myValue;   
	}
}

function jInsertAtCursor($myField, myValue){   
	if(document.selection){   
		$myField.focus();
		sel = document.selection.createRange();
		sel.text = myValue;
	} else if (myField.selectionStart || myField.selectionStart == "0"){
		var startPos = myField.selectionStart;
		var endPos = myField.selectionEnd;
		$myField.val(myField.val().substring(0, startPos) + myValue + myField.val().substring(endPos, myField.val().length));
	} else {
		$myField.val(myValue);
	}
}

function postChatMessage(userIdTarget, adminUserId, chatContent,assortId){
	if(chatContent== null || chatContent.length == 0){
		alert("不能发送空信息");
		return;
	}
	$.ajax({
		type: "POST",
		url: CHAT_POST_URL,
		cache: false,
		dataType: "xml",
		data: {adminUserId: adminUserId, userId: userIdTarget, content: chatContent,assortId:assortId},
		success: function(msg, reqStatus){
			var $msg = $(msg);
			var result = $msg.find("result").text();
			if(result == "false"){
				alert("对不起，发送失败，请重试.");
			} else if(result == "login"){
				alert("对不起，请上线后再发送消息.");
			}else{
				window.parent.frames["productFrame"].window.getChatMessages();
			}
		}
	});
}

function getRecommendProducts($productType, $productDiv){
	var catalogs = $productType.data("catalogs");
	var poolId = $productType.data("poolId");
	$.ajax({
		type: "GET",
		url: RECOMMEND_PRODUCT_URL,
		cache: false,
		dataType: "xml",
		data: {type: "0", catalogs: catalogs, poolId: poolId},
		success: function(msg, reqStatus){
			$productDiv.empty();
			var $msg = $(msg);
			$(msg).find("product").each(function(){
				var id = parseInt($(this).find("id").text());
				var name = decodeURIComponent($(this).find("name").text());
				var price = $(this).find("price").text();
				var $product = $("<span id=\"" + id + "\" class=\"productItem\">" + name + "-" + price + "元</span><br/>");
				$product.data("id", id).data("name", name);
				$product.bind("click", function(){
					var taObj = window.parent.frames["adminChatFrame"].window.document.getElementById("chatMessage");
					var message = "";
					message = "TAGSTARTa href=\"http://wap.mmb.cn/adult/shop/product.do?id=" + $(this).data("id") + "\"TAGEND" + $(this).data("name") + "TAGSTART/aTAGEND-" + price + "元";
					insertAtCursor(taObj, message);
					return false;
				});
				$productDiv.append($product);
			});
		}
	});
}
//新增初始化banUser.jsp的Ajax,获取所有被禁的用户
function getBanUsers(){
	$.ajax({
		type: "GET",
		url: GET_BAN_USERS_URL,
		cache: false,
		dataType: "xml",
		data: {type: "0"},
		success: function(msg, reqStatus){
			var banUsersDiv = window.parent.frames["banUserFrame"].window.document.getElementById("banUsersDiv");
			var $banUsersDiv =  $(banUsersDiv);
			$banUsersDiv.empty();
			var banUsersCheckbox = "";
			$(msg).find("chatUser").each(function(){
				var nick = decodeURIComponent($(this).find("nick").text());;
				var userId = decodeURIComponent($(this).find("userId").text());;
				banUsersCheckbox = banUsersCheckbox + "<input type='checkbox' id='banUser' name='banUser' value="+userId+" class='banUsers''>" + nick+"</input><br>";
			});
			if($(msg).find("chatUser").length>0){
				banUsersCheckbox = banUsersCheckbox + "<input type='checkbox' name='banUsers' id='checkAll' >全选</input>";
			}
			
			banUsersDiv.innerHTML = banUsersCheckbox;
			$(".banUsers").each(function(){
					$(this).bind("click",function(){
						$("#checkAll").attr("checked","true");
						$(".banUsers").each(function(){
							if(!$(this).attr("checked")){
								$("#checkAll").removeAttr("checked");
							}
						});
					});
			});
			$("#checkAll").bind("click",function(){
				if($(this).attr("checked")){
					$(".banUsers").attr("checked","true");
				}else{
					$(".banUsers").removeAttr("checked");
				}
			});
		}
	});
}
//新增初始化聊天用户ip及所在地的Ajax
function getChatUserIpInfo(tui){
	userIdTarget = tui;
	$.ajax({
		type: "GET",
		url: GET_CHAT_USER_IP_INFO,
		cache: false,
		dataType: "xml",
		data: {userId: userIdTarget},
		success: function(msg, reqStatus){
			var chatUserIpInfoDiv = window.parent.frames["allFrame"].window.document.getElementById("chatUserIpInfo");
			var $chatUserIpInfoDiv =  $(chatUserIpInfoDiv);
			$chatUserIpInfoDiv.empty();
			var chatUserIpInfo = "";
			$(msg).find("chatUserIpInfo").each(function(){
				var province = decodeURIComponent($(this).find("province").text());
				var city = decodeURIComponent($(this).find("city").text());
				var userId = decodeURIComponent($(this).find("userId").text());
				var ip = decodeURIComponent($(this).find("ip").text());
				var chatAssortName = decodeURIComponent($(this).find("chatAssortName").text());
				var catalogName = decodeURIComponent($(this).find("catalogName").text());
				chatUserIpInfo = chatUserIpInfo + "<span>用户IP:</span></br>";
				chatUserIpInfo = chatUserIpInfo + "<span><font color='brown'>"+ip+"</font></span></br></br>";
				chatUserIpInfo = chatUserIpInfo + "<span>用户所在城市:</span></br>";
				chatUserIpInfo = chatUserIpInfo + "<span><font color='brown'>"+city+"</font></span></br></br>";
				chatUserIpInfo = chatUserIpInfo + "<span>聊天分类:</span></br>";
				chatUserIpInfo = chatUserIpInfo + "<span><font color='brown'>"+chatAssortName+"</font></span></br></br>";
				chatUserIpInfo = chatUserIpInfo + "<span>产品类别:</span></br>";
				chatUserIpInfo = chatUserIpInfo + "<span><font color='brown'>"+catalogName+"</font></span></br></br>";
			});
			chatUserIpInfoDiv.innerHTML = chatUserIpInfo;
		}
	});
	
}
//获取聊天用户一周所有的聊天信息的Ajax
function getChatUserWeekMessages(tui){
	userIdTarget = tui;
	$.ajax({
		type: "GET",
		url: "../ChatMessagesManage.do?method=getChatMessages",
		cache: false,
		dataType: "xml",
		data: {customerUserId: userIdTarget},
		success: function(msg, reqStatus){
			var chatUserWeekMessagesDiv = window.parent.frames["allFrame"].window.document.getElementById("chatUserWeekMessages");
			var $chatUserWeekMessagesDiv =  $(chatUserWeekMessagesDiv);
			$chatUserWeekMessagesDiv.empty();
			var chatUserWeekMessages = "";
			$(msg).find("chatMessage").each(function(){
				var id = parseInt($(this).find("id").text());
				var nick = decodeURIComponent($(this).find("nick").text());
				var content = decodeURIComponent($(this).find("content").text());
				var userId = parseInt($(this).find("userId").text());
				var userIdTarget = parseInt($(this).find("userIdTarget").text());
				var isAdmin = parseInt($(this).find("isAdmin").text());
				
				var productId = parseInt($(this).find("product > productId").text());
				var productName = decodeURIComponent($(this).find("product > productName").text());
				
				if(productName!=null&&productName!=""){
					productName = "<span ><font color='purple'>[产品]:</font><a   target='_blank' href='http://mmb.cn/wap/shop/product.do?id=" + productId+"'> <font color='blue'>" + productName+"</font></a></span></br>";
				}else{
					productName = "";
				}
				
				var className = "" ;
				if(isAdmin == 1)
					className= " class=\"admin\"";
				var createDatetime = $(this).find("createDatetime").text();
				var cMsg = "<span><span class='chatUserMessage' >" + nick + ":&nbsp;&nbsp;&nbsp;("+ createDatetime+")</span>";
				cMsg+="<br/><span "+className+" style='margin-left:15px' >"+content+"</span><br/>";
				cMsg+=productName;
				var $chatMessage = $(cMsg);
				$chatUserWeekMessagesDiv.append($chatMessage);
			});
		}
	});
	
}