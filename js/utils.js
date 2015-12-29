/**
 * Some algrithms
 */
/**
 * 构造唯一id索引的矩阵
 */
function printTwoDimArray(array){
	var text = "";
	for(var i=0;i<array.length;i++){
		text+= (array[i].join(","));
		text += "\r\n";
	}
	alert(text);
}

function TreeNode(value){
	this.value = value;
	this.parentNode=null;   ///父节点
	this.childNodes = new Array();  ///孩子节点
}
TreeNode.prototype.addChild = function(node) {
	  node.parentNode = this;
	  this.childNodes[this.childNodes.length] = node;
}
////获取所有的路径
function getAllPath(leafs){
	var matrix = new Array();
	for(var i=0;i<leafs.length;i++){
		var subs = new Array();
		var leaf = leafs[i];
		while(leaf.parentNode!=null && leaf.value!=-1){
			subs.push(leaf.value);
			leaf = leaf.parentNode;
		}
		subs.reverse();
		matrix.push(subs);
	}
	return matrix;
}

////获取所有的叶子节点
function getAllLeafs(tree){
	var leafs = new Array();
	getLeafs(leafs,tree);
	return leafs;
}
function getLeafs(array,tree){
	if(tree.childNodes.length==0){
		array.push(tree);
		return;
	}
	else{
		for(var i=0;i<tree.childNodes.length;i++){
			getLeafs(array,tree.childNodes[i]);
		}
	}
}

TreeNode.prototype.addChild = function(node) {
	  node.parentNode = this;
	  this.childNodes[this.childNodes.length] = node;
}

function build(array){
	var tree = new TreeNode(-1);
	buildTree(array,0,tree);
	return tree;
}
function buildTree(array,index,parent){
	if(index>=array.length) return;
	var childNodes = createChildNodes(array[index]);
	for(var i=0;i<childNodes.length;i++){
		parent.addChild(childNodes[i]);
		buildTree(array,index+1,parent.childNodes[i]);
	}
}
function createChildNodes(count){
	var arr = new Array();
	for(var i=0;i<count;i++){
		arr.push(new TreeNode(i));
	}
	return arr;
}
/////这是一个不区分大小写key的map的简单实现，查找复杂度为1N  (N为map的大小)
function JsMap(){
	this.keys = new Array();
	this.values = new Array();
	this.size = 0;

	this.put = function (key,value){
		if(this.exists(key)) return;

		this.keys.push(key);
		this.values.push(value);
		this.size ++;
	}
	this.remove = function(key){
		var index = this.index(key);
		if(index==-1) return;

		this.values.splice(index,1);
		this.keys.splice(index,1);
		this.size--;
	}

	this.get = function(key){
		var index = this.index(key);
		if(index==-1) return null;
		return this.values[index];
	}
	this.exists = function(key){
		if(this.index(key)==-1) return false;
		return true;
	}

	this.index = function(key){
		if(this.size==0) return -1;
		for(var i=0;i<this.size;i++){
			if(this.keys[i]==key) return i;
		}
		return -1;
	}
}

/**
 * 页面中，支持input中添加属性：valid=xxx来对页面的输入框进行验证
 * xxx可以支持：
 *  nn            (not null)
 *  num			(must be number)
 *
 * 同时支持 使用 | 对多个要求进行组合
 */
/*
 * value: 需要验证的值
 * msg: 不符合要求的提示信息
 * rules: 需要满足的规则
 * return: 如果满足，返回true,否则 alert错误信息，并返回false
 */
function validate(value,msg,rules){
	if(rules=='') return true;
	var temp = rules.split("|");
	var result = true;
	for(var i=0;i<temp.length;i++){
		if(temp[i]=='nn'){
			if(value==null || value==''){
				result = false;
			}
		}
		if(temp[i]=='num'){
			if(isNaN(value)){
				result = false;
			}
		}
	}
	if(result == false) alert(msg);
	return result;
}


