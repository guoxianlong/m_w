var sps_count = 10
var sps = new Array(sps_count)
for (i = 0; i < sps_count; i ++) {
  sps[i]=new Array()
}
//合格库
sps[0][0] = new Option("全部", "-1");
sps[0][1] = new Option("北库", "0");
sps[0][2] = new Option("芳村", "1");
sps[0][3] = new Option("广速", "2");
sps[0][4] = new Option("增城", "3");
sps[0][5] = new Option("无锡", "4");
//待检库
sps[1][0] = new Option("全部", "-1");
sps[1][1] = new Option("北库", "0");
sps[1][2] = new Option("芳村", "1");
sps[1][3] = new Option("增城", "3");
sps[1][4] = new Option("无锡", "4");
//维修库
sps[2][0] = new Option("全部", "-1");
sps[2][1] = new Option("北库", "0");
sps[2][2] = new Option("芳村", "1");
sps[2][3] = new Option("增城", "3");
//返厂库
sps[3][0] = new Option("全部", "-1");
sps[3][1] = new Option("北库", "0");
sps[3][2] = new Option("芳村", "1");
sps[3][3] = new Option("增城", "3");
sps[3][4] = new Option("无锡", "4");
//退货库
sps[4][0] = new Option("全部", "-1");
sps[4][1] = new Option("北库", "0");
sps[4][2] = new Option("芳村", "1");
sps[4][3] = new Option("广速", "2");
sps[4][4] = new Option("增城", "3");
sps[4][5] = new Option("无锡", "4");
//残次品库
sps[5][0] = new Option("全部", "-1");
sps[5][1] = new Option("北库", "0");
sps[5][2] = new Option("芳村", "1");
sps[5][3] = new Option("广速", "2");
sps[5][4] = new Option("增城", "3");
sps[5][5] = new Option("无锡", "4");
//样品库
sps[6][0] = new Option("全部", "-1");
sps[6][1] = new Option("北库", "0");
sps[6][2] = new Option("芳村", "1");
sps[6][3] = new Option("增城", "3");
sps[6][4] = new Option("无锡", "4");
//售后库
sps[7][0] = new Option("全部", "-1");
sps[7][1] = new Option("芳村", "1");

function setStockArea(spSrc, spObj){
	x = spSrc.selectedIndex;
	for (var m = spObj.options.length; m > 0; m --){
		spObj.options[m] = null;
	}
	for (i = 0; i < sps[x].length; i ++){
		spObj.options[i]=new Option(sps[x][i].text, sps[x][i].value);
		//alert(sps[x][i].text+'   '+sps[x][i].value);
	}
	spObj.options[0].selected=true;
}