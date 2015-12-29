var highlightindex = -1;
var timeoutId;
$(document).ready(function () {
	$("#bankName").mouseover(function() {
		var appUser = $('#appUser').val();
		var bankId = $("#supplierBankId").val();
		if (bankId*1 > 0) {
			getBankFullName(bankId, appUser);
		}
	});
	$("#bankAccount").mouseover(function() {
		var appUser = $('#appUser').val();
		var bankId = $("#supplierBankId").val();
		var bankName = $("#bankName").val();
		if (bankName.length == 0) {
			bankName = '';
		}
		if (bankId*1 > 0) {
			getBankAccount(bankId, bankName, appUser);
		}
	});
	$("#bankName").mouseout(function() {
		$("#bankNames").hide();
	});
	$("#bankNames").mouseover(function() {
		$("#bankNames").show();
	});
	$("#bankNames").mouseout(function() {
		$("#bankNames").hide();
	});
	
	$("#bankAccount").mouseout(function() {
		$("#bankAccounts").hide();
	});
	$("#bankAccounts").mouseover(function() {
		$("#bankAccounts").show();
	});
	$("#bankAccounts").mouseout(function() {
		$("#bankAccounts").hide();
	});
})

function getBankFullName(bankId, appUser) {
	var bankNameInput = $("#bankName");
	var bankNameInputOffset = bankNameInput.offset();
	$("#bankNames").hide().css("border","1px black solid").css("left",bankNameInputOffset.left+"px").width(bankNameInput.width()+7+"px").css("background-color","white").css("z-index","10");
	clearTimeout(timeoutId);
	timeoutId  = setTimeout(function() {
		$.post("/adult-admin/admin/supplier/imprestBankInfo.jsp", {bankId:bankId,appUser:appUser}, function (date)
		 {
			var org = $(date);
			var wordnodes = org.find("name");
			var autonode = $("#bankNames");
			autonode.html("");
			wordnodes.each(function (i)
			{
				var wordnode = $(this);
				var newDivNode = $("<div>").attr("id",i);
				newDivNode.html(wordnode.text()).appendTo(autonode);
				newDivNode.mouseover(function ()
				{
					if(highlightindex!=-1)
					{
						$("#bankNames").children("div").eq(highlightindex).css("background-color","white");
					}
					highlightindex = $(this).attr("id");
					$(this).css("background-color","red");
				});
				newDivNode.mouseout(function ()
				{$(this).css("background-color","white");
				});
				newDivNode.click(function()
				{
					var textInputText =$(this).text();
					highlightindex = -1;
					$("#bankName").val(textInputText);
					$("#bankNames").hide();
				});
			});
			autonode.append('<iframe src="JavaScript:false" style="position:absolute; visibility:inherit; top:0px; left:0px; width:100px; height:200px; z-index:-1; filter=progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0);"></iframe>');
			if (wordnodes.length > 0) {
				autonode.show();
			} else {
			    autonode.hide();
			    highlightindex=-1;
			}
		},"xml");
	},100);
}

function getBankAccount(bankId, bankName, appUser) {
	var bankAccountInput = $("#bankAccount");
	var bankAccountInputOffset = bankAccountInput.offset();
	$("#bankAccounts").hide().css("border","1px black solid").css("left",bankAccountInputOffset.left+"px").width(bankAccountInput.width()+7+"px").css("background-color","white").css("z-index","10");
	clearTimeout(timeoutId);
	timeoutId  = setTimeout(function() {
		$.post("/adult-admin/admin/supplier/imprestBankInfo.jsp", {bankId:bankId, bankName:bankName, appUser:appUser}, function (date)
		 {
			var org = $(date);
			var wordnodes = org.find("account");
			var autonode = $("#bankAccounts");
			autonode.html("");
			wordnodes.each(function (i)
			{
				var wordnode = $(this);
				var newDivNode = $("<div>").attr("id",i);
				newDivNode.html(wordnode.text()).appendTo(autonode);
				newDivNode.mouseover(function ()
				{
					if(highlightindex!=-1)
					{
						$("#bankAccounts").children("div").eq(highlightindex).css("background-color","white");
					}
					highlightindex = $(this).attr("id");
					$(this).css("background-color","red");
				});
				newDivNode.mouseout(function ()
				{$(this).css("background-color","white");
				});
				newDivNode.click(function()
				{
					var textInputText =$(this).text();
					highlightindex = -1;
					$("#bankAccount").val(textInputText);
					$("#bankAccounts").hide();
				});
			});
			autonode.append('<iframe src="JavaScript:false" style="position:absolute; visibility:inherit; top:0px; left:0px; width:100px; height:200px; z-index:-1; filter=progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0);"></iframe>');
			if (wordnodes.length > 0) {
				autonode.show();
			} else {
			    autonode.hide();
			    highlightindex=-1;
			}
		},"xml");
	},100);
}