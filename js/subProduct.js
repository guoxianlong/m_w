/**
 * Some algrithms
 * Author: Huodongyun <huody00@gmail.com>
 * Date:　2012-01-12
 */
//var subProductsTypes ={"numOfTypes":2,"types":[{"attribute":{"displayName":"颜色","id":1},"attributeOptions":[{"attributeId":1,"displayValue":"红","id":1},{"attributeId":1,"displayValue":"黄","id":2},{"attributeId":1,"displayValue":"蓝","id":3},{"attributeId":1,"displayValue":"绿","id":4},{"attributeId":1,"displayValue":"option-4","id":5},{"attributeId":1,"displayValue":"option-5","id":6},{"attributeId":1,"displayValue":"option-6","id":7},{"attributeId":1,"displayValue":"option-7","id":8},{"attributeId":1,"displayValue":"option-8","id":9}]},{"attribute":{"displayName":"尺码","id":2},"attributeOptions":[{"attributeId":2,"displayValue":"M","id":10},{"attributeId":2,"displayValue":"L","id":11},{"attributeId":2,"displayValue":"X","id":12},{"attributeId":2,"displayValue":"XL","id":13},{"attributeId":2,"displayValue":"option-4","id":14},{"attributeId":2,"displayValue":"option-5","id":15},{"attributeId":2,"displayValue":"option-6","id":16},{"attributeId":2,"displayValue":"option-7","id":17},{"attributeId":2,"displayValue":"option-8","id":18}]}]};

//var subProductPageContentHtml = '<script>showAllAttributes(subProductsTypes);</script>';
//var subProductsTypes = null;   ///保存所有的属性以及其对应选项

//document.write("<script language='javascript' src='utils.js'></script>");

function showAllAttributes(){
	var all = subProductsTypes;
	if(all.numOfTypes<1) {
		$("#subProductAttributesDiv").empty();
        $("#operabutt").css("display","none");
		return;
	}
	$("#subProductAttributesDiv").html("");
    for(var i=0;i<all.numOfTypes;i++){
		showAttribute(all.types[i]);
	}
}

function showAttribute(attr){
	var text = '<div id="showAttributeDiv">'+
						'<span style="font-weight:bold;padding-right:5px;display:inline-block;margin-bottom:6px;">'+attr.attribute.displayName+':</span>';
	for(var i=0;i<attr.attributeOptions.length;i++){
		text += '<span style="border:1px solid #555555;padding-right:2px;margin-bottom:5px;display:inline-block;"><input type="checkbox" name="attr_'+attr.attribute.id+'" id="attr_'+
		attr.attribute.id+'_'+attr.attributeOptions[i].id+'" value="'+attr.attributeOptions[i].id+'"/>&nbsp;'+
		attr.attributeOptions[i].displayValue+'</span>';
	}
	text += '</div>';
	var div = $("#subProductAttributesDiv");
	div.append(text);
	//document.write(text);
}

////兼容原有的点击添加子商品以及兼容新增的根据子商品数量初始化若干行
function addSubProduct(){
	if(arguments.length==0){
		addNewSubProduct();
		return;
	}
	if(arguments.length==1){
		addEditSubProduct(arguments[0]);
		return;
	}
	alert("Parameter error!")
}

/////初始化numOfSubProducts行需要编辑的子商品，兼容属性为0.
function addEditSubProduct(numOfSubProducts){
	var all = subProductsTypes;
	if(all.numOfTypes<1) return;
	///添加属性的title和填写框
	///考虑到可能多次点击按钮，因此需要保存数据，并清除代码，待创建完后再加载数据
	saveData();
	renewExtraAttribute(all);
	recoverData();
	////根据选中的不同属性的数量，计算出需要创建的子商品数量，并将表格的第一行数据准备好
	var subCount = numOfSubProducts;
	if(subCount==0) return;
	var row = $(".subProductFirstTr");
	for(var i=1;i<subCount;i++){
		var clone = row.clone();
		////将条码选项的默认值填充
		clone.find(".originalValue").each(function(){
			var name = $(this).attr("name");
			if(name.indexOf("barcodeType")==0){
				$(this).val(1);
				return;
			}
			if(name.indexOf("barcode")==0){
				$(this).attr("value","");
				return;
			}
		});

		clone.addClass("extraData");
		row.after(clone);
	}
}

function addNewSubProduct(){
	var all = subProductsTypes;
	if(all.numOfTypes<1) return;

	var selectedTypesNum = getSelectedAttributeTypes();
	if(all.numOfTypes!=selectedTypesNum){
		alert("所有决定子商品的属性都必须选择，才可以添加子商品！");
		return;
	}

	////删除所有的.removeWhenNew的列
	$(".removeWhenNew").remove();

	///添加属性的title和填写框
	///考虑到可能多次点击按钮，因此需要保存数据，并清除代码，待创建完后再加载数据
	saveData();
	renewExtraAttribute(all);
	recoverData();
	////根据选中的不同属性的数量，计算出需要创建的子商品数量，并将表格的第一行数据准备好
	var subCount = getSubProductCount(all);
	if(subCount==0) return;
	var row = $(".subProductFirstTr");
	for(var i=1;i<subCount;i++){
		var clone = row.clone();

		////将条码选项的默认值填充
		clone.find(".originalValue").each(function(){
			var name = $(this).attr("name");
			if(name.indexOf("barcodeType")==0){
				$(this).val(1);
				return;
			}
			if(name.indexOf("barcode")==0){
				$(this).attr("value","");
				return;
			}
		});

		clone.addClass("extraData");
		row.after(clone);
	}
	////对所有行的属性ID、名称，以及显示的值进行处理
	var allRows = $(".subProductFirstTr");
	if(allRows.length!=subCount) {
		alert("Script error!")
		return;
	}
	//////////////////////////////////////
	///////////////////////////////////
	/////////////////////////////////
	///需要构造出索引！！
	var selectedTypes = getSelectedTypes(all);
	var selectedOptionsArray = new Array();      ///二维数组，存放不同属性 被选中的值

	var countArray = new Array();                  ///存放不同属性被选中值的个数，用于构造索引矩阵
	for(var i=0;i<selectedTypes.length;i++){
		var type = selectedTypes[i];
		var selectedOptions = getSelectedOptions(type);
		selectedOptionsArray.push(selectedOptions);
		countArray.push(selectedOptions.length);
	}
	var indexMatrix = new Array();
	var tree = build(countArray);
	var leafs = getAllLeafs(tree);
	var indexMatrix = getAllPath(leafs);

	//printTwoDimArray(indexMatrix);

	var currentRow =$(".subProductFirstTr").first();
	initRow(indexMatrix,selectedTypes,selectedOptionsArray,currentRow,0,subCount);
	for(var i=1;i<subCount;i++){
		////初始化具体的一行
		currentRow = currentRow.next(".subProductFirstTr");
		initRow(indexMatrix,selectedTypes,selectedOptionsArray,currentRow,i,subCount);
	}
	clearAttributeSelected();
}
////初始化某一行的属性ID、名称，以及显示的值
/*参数：
 *
indexMatrix
types: 有值被选中的属性
selectedOptionsArray
row: dom对象，是一行tr
index : 第几个子商品， 0开始的索引
total：总共子商品数量
*/
function initRow(indexMatrix,types,selectedOptionsArray,row,index,total){
	/*
	var typeSelectedOptions = new Array();
	for(var i=0;i<types.length;i++){
		var type = types[i];
		typeSelectedOptions.push(getSelectedOptions(type));
	}
	*/
	var count = 0;
	row.find(".optionValue").each(function(){
		var type = types[count];
		var options = selectedOptionsArray[count];
		var currentOption = options[indexMatrix[index][count]];
		var name='opt_'+type.attribute.id+'_'+currentOption.id+'_'+index;
		var value = currentOption.displayValue;
		$(this).attr("name",name);
		$(this).attr("id",name);
		$(this).attr("value",value);

		count++;
	});

	row.find(".originalValue").each(function(){
		var tag = $(this);
		var name = tag.attr("name");
		var temp = name.split("_");   /////去掉之前的那些标记
		name = temp[0]+"_"+index;
		tag.attr("name",name);
		tag.attr("id",name);
	});
}
////返回一个选择了多少个属性
function getSelectedAttributeTypes(){
	var all = subProductsTypes;
	var total = 0;
	for(var i=0;i<all.numOfTypes;i++){
		var type = all.types[i];
		var checkboxName = "attr_"+type.attribute.id;
		var temp = "input[name='"+checkboxName+"']";
		var hasSelected = false;
		$(temp).each(function(){
			var flag = $(this).attr("checked");
			if(flag=="checked" || flag == true){
				hasSelected = true;
			}
		} );
		if(hasSelected) total++;
	}
	return total;
}

////返回用户选择的属性会产生多少个子商品
function getSubProductCount(){
	var all = subProductsTypes;
	var total = 1;
	var hasSub = false;
	for(var i=0;i<all.numOfTypes;i++){
		var count = 0;
		var type = all.types[i];
		var checkboxName = "attr_"+type.attribute.id;
		var temp = "input[name='"+checkboxName+"']";
		$(temp).each(function(){
			var flag = $(this).attr("checked");
			if(flag=="checked" || flag==true){
				count++;
				hasSub = true;
			}
		} );
		if(count>0) total *= count;
	}
	return hasSub?total:0;
}
////保存数据，待恢复使用
function saveData(){

}

////创建完后，尽量恢复数据
function recoverData(){

}
////返回那些属性已经被选择了，每个元素是一个对象，包含attribute以及队形的options...
function getSelectedTypes(){
	var all = subProductsTypes;
	var array = new Array();
	for(var i=0;i<all.numOfTypes;i++){
		var type = all.types[i];
		var checkboxName = "attr_"+type.attribute.id;
		var temp = "input[name='"+checkboxName+"']";
		var flag = false;
		$(temp).each(function(){
			var isChecked = $(this).attr("checked");
			if(isChecked){
				flag = true;
			}
		} );
		if(flag){
			array.push(type);
		}
	}
	return array;
}
////找出一个属性对应的选项中被选中的
function getSelectedOptions(type){
	var array = new Array();
	var checkboxName = "attr_"+type.attribute.id;
	for(var i=0;i<type.attributeOptions.length;i++){
		var option = type.attributeOptions[i];
		var optionCheckId = '#attr_'+type.attribute.id+'_'+option.id
		var isChecked = $(optionCheckId).attr("checked");
		if(isChecked){
			array.push(option);
		}
	}
	return array;
}
////为子商品属性的表格添加需要的title和填写框
function renewExtraAttribute(){
	var all = subProductsTypes;
	///预先清除元素
	$(".extraData").remove();
	for(var i=all.numOfTypes-1;i>=0;i--){
		var type = all.types[i];
		///添加属性对应的title
		var title = '<td class="extraData">'+type.attribute.displayName+'</td>';
		$(".subProductTitleTr").prepend(title);
		////第一行先添加为匹配属性选择的默认行
		var value = '<td class="extraData"><input type="text" size="6" class="optionValue"/></td>';
		$(".subProductFirstTr").prepend(value);
	}
}
///点击删除按钮后，删除一个子商品
function deleteOneSub(tag){
	///首先需要判断是不是最后一行，如果是，不允许删除
	////通过兄弟节点进行判断
	////如果是删除默认行的话，需要将剩下的第一行作为默认行，需要去掉其 extraData属性
	var deleteTarget = $(tag).parent().parent();

////如果是已经存在于数据库中的子商品，则不能删除
	var existingSubProduct = false;
	deleteTarget.find(".hiddenSubProductClass").each(function(){
		if($(this).val()!="0"){
			existingSubProduct = true;
		}
	});
	if(existingSubProduct){
		alert("已经存在的子商品不能删除！");
		return;
	}

	var allSiblings = deleteTarget.siblings(".subProductFirstTr");
	if(allSiblings==null || allSiblings==undefined || allSiblings=='' || allSiblings.length==0){
		alert("最后一个商品的属性设置项不能删除！");
		return;
	}
	///如果删除的行是第一行，那么将其下一行的extraData标记去掉，从而晋升为第一行
	if(!deleteTarget.hasClass("extraData")){
		var next = deleteTarget.next(".extraData");
		next.removeClass("extraData");
	}
	deleteTarget.remove();
}

///将所有的动态框的名称保存下来，作为隐含域dynamicNames的value来提交
function collectAllInputNames(){
	var dynamicNames = new Array();
	$(".subProductFirstTr").each(function(){
		var row = $(this);
		var rowNames = new Array();
		row.find(".optionValue").each(function(){
			var name = $(this).attr("name",name);
			rowNames.push(name);
		});
		row.find(".originalValue").each(function(){
			var name = $(this).attr("name",name);
			rowNames.push(name);
		});
		dynamicNames.push(rowNames.join(","));
	});
	var namesText = dynamicNames.join(":");
	//alert(namesText);
	$("#dynamicNames").attr("value",namesText);
}
////将第一行的.originalValue标记的值 复制到其他行子商品的.originalValue对应的值中
function copyFirstRowValue2OtherRows(){
	var count = 0;
	var first = null;
	var valueArray = new Array();
	$(".subProductFirstTr").each(function(){
		if(count==0){
			first = $(this);
			$(this).find(".originalValue").each(function(){
				valueArray.push($(this).attr("value"));
			});
			count++;
			return;
		}
		var i=0;
		$(this).find(".originalValue").each(function(){
			var name = $(this).attr("name");
			if(name.indexOf("oriname")==0){   ///内部名称不复制
				i++;
				return;
			}
			if(name.indexOf("barcode")==0){  ///条码不复制
				i++;
				return;
			}
			if(name.indexOf("subProductId")==0){  ///隐含域的id不复制
				i++;
				return;
			}
			if(name.indexOf("code")==0){  ///隐含域的id不复制
				i++;
				return;
			}

			$(this).attr("value",valueArray[i++]);
		});
		count ++;
	});
}
/////检查对输入有要求的字段是否都符合条件进行了输入
function checkInputValid(){
	var result = true;
	$("input[valid]").each(function(){
		if(!result) return;
		var value = $(this).attr("value");
		var msg = $(this).attr("msg");
		var valid = $(this).attr("valid");

		if(!validate(value,msg,valid)){
			result = false;
			return;
		}
	});
	return result;
}
function clickForVender(tag){
	var dialog = $( "#forVenderDialogDiv" );
	dialog.dialog( "open" );
	var confirmButton = $( "#confirmVenderButton" );
	confirmButton.unbind("click");
	confirmButton.bind("click",function(){
		var name = $("#venderNameSelector");
		var allVenders=$("#allVenders").val().split(",");
		var flag=false;
	    for(var i=0;i<allVenders.length;i++){
		    if(name.val()==allVenders[i]){
		       flag=true;
		       break;
		    }
	    }
	    if(!flag){
	       	   alert("供应商不存在，请重新选择！");
	       	   return;
	    }
		$(tag).attr("value",name.val());
		name.val("");
		dialog.dialog( "close" );
	});
}

function clickForBarcode(tag){
	////首先判断是那种情况，如果是选择了商品自带条码，则将焦点放在输入框即可，否则弹出对话框
	var codeDom = $(tag);
	var value = 1;  ///默认是商品自带, 1表示选择
	value = codeDom.parent().find("select").val();
	////如果是选择的商品自带
	if(value==1){
		codeDom.focus();
		return;
	}
	/////系统生成条码，则调用以前的方式
	var para='';
	var parentObj = document.getElementById("oldcatalog-1");
	var parentObj2 = document.getElementById("oldcatalog-2");
	if(parentObj==undefined || parentObj==null){
		alert("请先选择第一层旧的产品类别，再生成条形码！");
		return;
	}
	var value1 = parentObj.value;
	var value2 = "";
	if(parentObj2!=undefined && parentObj2!=null){
		value2 = parentObj2.value;
	}
	if(value1==""){
		alert("请先选择第一层产品类别，再生成条形码！");
		parentObj.focus();
		return;
	}else if(value2==""){
		if(!confirm("第二层产品类别还没选择，是否继续生成条形码？"))
			return ;
	}
	var name="",name2="";
    name=parentObj.options[parentObj.selectedIndex].text;
	para="?catalogId="+value1;
	if(value2!=""){
	    name2=parentObj2.options[parentObj2.selectedIndex].text;
	    para+="&catalogId2="+value2;
	}
	var paramArr= new Array();
	paramArr[0]=name;
	paramArr[1]=name2;
	result = window.showModalDialog("barcodeManager/createBarcodeInfo.jsp"+para,paramArr,"location=no");
	if(result){
		codeDom.attr("value",result);
	}
}
/////设置默认的选择状态，对于包含有defaultValue属性的radio
function setDefaultRadioChecked(){
	$("input[defaultValue='true']").each(function(){
		$(this).attr("checked","checked");
	});
}
/////检查是否存在系统生成的条码，如果是的话，每次改变旧的分类时，都会提示！
function hasAnySysGenBarcode(){
	var flag = false;
	$("select[class='originalValue']").each(function(){
		if($(this).val()==2){
			flag = true;
			return;
		}
	});
	return flag;
}

function clearAttributeSelected(){
	 $("#subProductAttributesDiv").find("input[type='checkbox']").each(function(){
		 $(this).removeAttr("checked");
	 });
}

///将内部名称和型号串起来，放到每个子商品下面
function setSubProductNames(){
	var joinedChar = "*";
	var name = $("#name").val();
	var model = $("#model").val();
	$ (".subProductFirstTr").each(function(){
			var optionValues = new Array();
			$(this).find(".optionValue").each(function(){
				optionValues.push($(this).val());
			});
			var oriName = name+(model==""?"":joinedChar+model);
			if(optionValues.length>0){
				oriName += ( joinedChar+optionValues.join(joinedChar));
			}
			$(this).find(".originalValue").each(function(){
				var inputName = $(this).attr("name");
				if(inputName.indexOf("oriname")==0){
					$(this).val(oriName);
					return;
				}
			});
	});
}

//var subProductsTypes ={"numOfTypes":2,"types":[{"attribute":{"displayName":"颜色","id":1},"attributeOptions":[{"attributeId":1,"displayValue":"红","id":1},{"attributeId":1,"displayValue":"黄","id":2},{"attributeId":1,"displayValue":"蓝","id":3},{"attributeId":1,"displayValue":"绿","id":4},{"attributeId":1,"displayValue":"option-4","id":5},{"attributeId":1,"displayValue":"option-5","id":6},{"attributeId":1,"displayValue":"option-6","id":7},{"attributeId":1,"displayValue":"option-7","id":8},{"attributeId":1,"displayValue":"option-8","id":9}]},{"attribute":{"displayName":"尺码","id":2},"attributeOptions":[{"attributeId":2,"displayValue":"M","id":10},{"attributeId":2,"displayValue":"L","id":11},{"attributeId":2,"displayValue":"X","id":12},{"attributeId":2,"displayValue":"XL","id":13},{"attributeId":2,"displayValue":"option-4","id":14},{"attributeId":2,"displayValue":"option-5","id":15},{"attributeId":2,"displayValue":"option-6","id":16},{"attributeId":2,"displayValue":"option-7","id":17},{"attributeId":2,"displayValue":"option-8","id":18}]}]};
////获取一个属性的所有的选项
function getOptionsByAttributeId(id){
	var all = subProductsTypes;
	if(all.numOfTypes<1){
		alert("Script error!");
		return null;
	}
	for(var i=0;i<all.numOfTypes;i++){
		if(all.types[i].attribute.id==id){
			return all.types[i].attributeOptions;
		}
	}
	return null;
}
////用下拉框将属性的选项显示出来，val代表默认的选项值
function showAttributeOptionsSelected(val,attributeOptions){
	var html = '<select>';
	for(var i=0;i<attributeOptions.length;i++){
		html += "<option"
	}
	html += '</select>';
	document.write(text);
}


/**
 * 递交表单
 */

function doSubmit(){
	if(!checkInputValid()) return;
	collectAllInputNames();
	//alert($("#dynamicNames").attr("value"));
}