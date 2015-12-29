function deleteItem(id,path){
			$("#tip").dialog({
				modal:true,
				bgiframe:true,
				width:540,
				title:"删除成员",
				buttons:{
					"确定":function(){
						window.location.href = "ChatGroup.do?method=delete&id=" + id;
					},
					"取消":function(){
						$(this).dialog('close');
					}
				},
				dialogClass: 'alert'
		});
}

function addItem(){
	   $("#groupForm")[0].reset();
	   $("#groupName").attr("readonly",false);
		$("#group").dialog({
			modal:true,
			bgiframe:true,
			width:540,
			height:300,
			title:"新增组别",
			buttons:{
				"确定":function(){
					if( $.trim($('#groupName').val())===''){
		       			alert('请输入组别名称!');
		       			return;
		       		}
			       	if($('#groupName').val().length>155){
			       		alert('输入的字数不能超过25个!');
			       		return;
			       	}
			       	if($("form input:checkbox").length==0){
			       		alert("请先添加聊天分类!");
			       		return;
			       	}
			       	var input = $("form input:checkbox:checked");
			       		if(input==null || input.length==0){
			       			alert("请选择聊天分类!");
			       			return;
			       		}
		               $(this).dialog('close');
		               $("#groupForm").submit();
					},
					"取消":function(){
						$(this).dialog('close');
					}
				},
			dialogClass: 'alert'
		});	
	}
	
	function assortSet(id,operate){
		getGroup(id,operate);
		$("#groupId").val(id);
		$("#group").dialog({
			modal:true,
			bgiframe:true,
			width:540,
			height:300,
			title:"分类设置",
			buttons:{
				"确定":function(){					
			       	if($("form input:checkbox").length==0){
			       		alert("请先添加聊天分类!");
			       		return;
			       	}
			       	var input = $("form input:checkbox:checked");
			       		if(input==null || input.length==0){
			       			alert("请选择聊天分类!");
			       			return;
			       		}
		               $(this).dialog('close');
		               $("#groupForm").attr("action","ChatGroup.do?method=assortSet").submit();
					},
					"取消":function(){
						$(this).dialog('close');
					}
				},
			dialogClass: 'alert'
		});	
	}

	function getGroup(id,operate){
		$.ajax({
			type:"post",
			url:"ChatGroup.do?method=getGroupInfo",
			dataType: "json",  
            data: {"id":id},  
            success: function(json){   
                if(json!=null){
                	var bean = json.groupBean;
                	if(bean!=null){
                		if(operate=="edit"){
                			var names = bean.userNames;
                			if(names!=null && names.length>0){
                				$("#members").val(names);
                			}
                		}else if(operate=="set"){
							$("#groupName").val(bean.name).attr("readonly","true");
                			var input = $("form input:checkbox");
	                		var ids = bean.assortIds.split(",");
	                		if(ids.length>0){
	                			for(var i=0;i<ids.length;i++){
	                				for(var j=0;j<input.length;j++){
	                					if(ids[i]==input[j].value){
	                						input[j].checked = true;
	                					}
	                				}
	                			}
	                		}                			
                		}
                	}
                }	
            },
            error:function(XMLHttpRequest, textStatus, errorThrown){
	   			alert('显示内容异常!');
	    	}
		});
	}

function editMembers(id,operate){
	getGroup(id,operate);
	$("#id").val(id);
	$("#memberForm").dialog({
		modal:true,
		bgiframe:true,
		width:540,
		title:"编辑成员",
		buttons:{
			"确定":function(){
				if( $.trim($('#members').val())===''){
			       	alert('请输入员工账号!');
			       	return;
			     }			      
			    $(this).dialog('close');
			    $("#memberForm").submit();
			},
			"取消":function(){
				$(this).dialog('close');
			}
		},
		dialogClass: 'alert'
	});	
}