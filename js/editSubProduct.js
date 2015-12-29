/**
 * Some algrithms
 * Author: Huodongyun <huody00@gmail.com>
 * Date:　2012-01-16
 */
//var subProductOptons = {"numOfSubProducts":4,"subProductOptions":[{"attributeOptions":[{"attributeId":1,"displayValue":"option-1","optionId":1},{"attributeId":2,"displayValue":"option-10","optionId":10}],"normalAttributes":{"marketPrice":"100"},"subProductId":1},{"attributeOptions":[{"attributeId":1,"displayValue":"option-2","optionId":2},{"attributeId":2,"displayValue":"option-10","optionId":10}],"normalAttributes":{"marketPrice":"100"},"subProductId":2},{"attributeOptions":[{"attributeId":1,"displayValue":"option-3","optionId":3},{"attributeId":2,"displayValue":"option-10","optionId":10}],"normalAttributes":{"marketPrice":"100"},"subProductId":3},{"attributeOptions":[{"attributeId":1,"displayValue":"option-4","optionId":4},{"attributeId":2,"displayValue":"option-10","optionId":10}],"normalAttributes":{"marketPrice":"100"},"subProductId":4}]};

/////这是一个需要进行一定修改的方法，要把normalAttributes中的属性和值放到map中，做成key-value的形式
function buildNormalAttributesMap(normalAttributesMap){
	var map = new JsMap();
	map.put("code", normalAttributesMap.code);
	///Todo:需要添加其他的内容！！！！！！
	map.put("oriname", normalAttributesMap.oriname);
	map.put("price3", normalAttributesMap.price3);
	map.put("price2", normalAttributesMap.price2);
	map.put("price", normalAttributesMap.price);
	map.put("barcode", normalAttributesMap.barcode);
	map.put("barcodeType", normalAttributesMap.barcodeType);
	map.put("stockStandardGd", normalAttributesMap.stockStandardGd);
	map.put("stockLineGd", normalAttributesMap.stockLineGd);
	map.put("gdStockin", normalAttributesMap.gdStockin);
	map.put("qcbs", normalAttributesMap.qcbs);
	map.put("vender", normalAttributesMap.vender);
	map.put("stockStatus", normalAttributesMap.stockStatus);
	map.put("lishixiaoliang", normalAttributesMap.lishixiaoliang);
	return map;
}
function EditSubProduct(initValue,originalValue){
	////初始化参数，是一个JSON对象
	this.initObj = initValue;                   ////已经添加的子商品信息
	this.originalObj = originalValue;      ///选项以及属性信息

	var _this = this;
	/////初始化每一行的数据
	this.initEachRow = function(){
		var subIndex = 0;
		$(".subProductFirstTr").each(function(){
			var spo = _this.initObj.subProductOptions[subIndex];
			////设置固定项的名称以及其默认值
			var map = buildNormalAttributesMap(spo.normalAttributes);
			$(this).find(".originalValue").each(function(){
				var name = $(this).attr("name");
				$(this).val(map.get(name));
				name = name+"_"+subIndex;
				$(this).attr("name",name);
				$(this).attr("id",name);
			});
			////设置条码不能修改！！！
			$(this).find("select").each(function(){
				var barcodeType = map.get("barcodeType");
				$(this).val(barcodeType);
				$(this).attr("disabled","disabled");
				///设置条码框不能编辑
				$(this).prev("input").attr("disabled","disabled");
			});

			////设置可选项属性的name属性以及id属性
			var optionCount = 0;
			$(this).find(".optionValue").each(function(){
				var ao = spo.attributeOptions[optionCount];
				var name = "opt_"+ao.attributeId+"_"+ao.optionId+"_"+subIndex;
				var value = ao.displayValue;
				$(this).attr("name",name);
				$(this).attr("id",name);
				$(this).attr("value",value);

				////同时添加一个下拉框，让用户选择
				var temp = "opt_as_"+ao.attributeId+"_"+subIndex;
				var htmlText = '<select class="onlyInEdit" name="'+temp+'" id="'+temp+'">';
				htmlText += '<option value="0">未指定</option>';
				var aos = _this.originalObj.types[optionCount].attributeOptions;
				for(var i=0;i<aos.length;i++){
					var o = aos[i];
					htmlText += ( '<option value="'+o.id+'">'+o.displayValue+'</option>');
				}
				htmlText += '</select>';
				$(this).after(htmlText);
				$(this).parent().find(".onlyInEdit").val(ao.optionId);   ////设定选中的值

				optionCount++;
			});
			////设置其他一些特定功能的限制，比如不能删除；条码不能编辑，编号不能编辑
			////否则，由于存在的子商品不允许删除，因此把删除链接设置为无效
			///// 同时，条码不支持修改！！！！！

			////同时，设置隐含域，用于记录子商品ID
			var hiddenTag = $(this).find(".hiddenSubProductClass");
			hiddenTag.attr("name",hiddenTag.attr("name")+"_"+spo.subProductId);
			hiddenTag.attr("value",spo.subProductId);
			///下一行数据处理
			subIndex ++ ;
		});
	}

	this.initNewSubProduct = function(){
		$(".subProductFirstTr").each(function(){
			////同时，设置隐含域，用于记录子商品ID,设置为0
			var hiddenTag = $(this).find(".hiddenSubProductClass");
			hiddenTag.attr("value","0");
			////清空条码框的内容
			var barcodeText = $(this).find("input[barcode_text='true']");
			barcodeText.removeAttr("disabled");
			barcodeText.attr("enabled","enabled");
			barcodeText.attr("value","");
			//barcodeText.attr("onclick","javascript:clickForBarcode(this);");
			///设置默认的条码方式为自带
			var barcodeSelect = $(this).find("select[barcode_select='true']");
			barcodeSelect.removeAttr("disabled");
			barcodeSelect.attr("enabled","enabled");
			barcodeSelect.val(1);    ////默认为系统自带

			////内部名称设置为空
			$(this).find(".originalValue").each(function(){
				var name = $(this).attr("name");
				if(name.indexOf("oriname")==0){
					$(this).val("");
				}
			});


		});
	}

	///将所有的动态框的名称保存下来，作为隐含域dynamicNames的value来提交
	this.collectInputs = 	function(){
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
			row.find(".hiddenSubProductClass").each(function(){
				var name = $(this).attr("name",name);
				rowNames.push(name);
			});
			row.find(".onlyInEdit").each(function(){
				var name = $(this).attr("name",name);
				rowNames.push(name);
			});
			dynamicNames.push(rowNames.join(","));
		});
		var namesText = dynamicNames.join(":");
		//alert(namesText);
		$("#dynamicNames").attr("value",namesText);
	}

	this.getRowSubProductId = function(tag){
		var sub = $(tag).parent().parent();
		var subIdTag = sub.find(".hiddenSubProductClass");
		return subIdTag.val();
	}
}
