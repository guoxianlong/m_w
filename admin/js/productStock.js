var ps_spts_count = 10
var ps_spts = new Array(ps_spts_count)
for (i = 0; i < ps_spts_count; i ++) {
  ps_spts[i]=new Array()
}
ps_spts[0][0] = new Option("      ", "-1");
ps_spts[1][0] = new Option("北库", "0");
ps_spts[1][1] = new Option("芳村", "1");
ps_spts[1][2] = new Option("广速", "2");
ps_spts[1][3] = new Option("增城", "3");
ps_spts[1][4] = new Option("无锡", "4");

ps_spts[2][0] = new Option("北库", "0");
ps_spts[2][1] = new Option("芳村", "1");
ps_spts[2][2] = new Option("增城", "3");
ps_spts[2][3] = new Option("无锡", "4");

ps_spts[3][0] = new Option("北库", "0");
ps_spts[3][1] = new Option("芳村", "1");
ps_spts[3][2] = new Option("增城", "3");
ps_spts[3][3] = new Option("无锡", "4");

ps_spts[4][0] = new Option("北库", "0");
ps_spts[4][1] = new Option("芳村", "1");
ps_spts[4][2] = new Option("增城", "3");
ps_spts[4][3] = new Option("无锡", "4");

ps_spts[5][0] = new Option("北库", "0");
ps_spts[5][1] = new Option("芳村", "1");
ps_spts[5][2] = new Option("广速", "2");
ps_spts[5][3] = new Option("增城", "3");
ps_spts[5][4] = new Option("无锡", "4");

ps_spts[6][0] = new Option("北库", "0");
ps_spts[6][1] = new Option("芳村", "1");
ps_spts[6][2] = new Option("广速", "2");
ps_spts[6][3] = new Option("增城", "3");
ps_spts[6][4] = new Option("无锡", "4");

ps_spts[7][0] = new Option("北库", "0");
ps_spts[7][1] = new Option("芳村", "1");
ps_spts[7][2] = new Option("增城", "3");
ps_spts[7][3] = new Option("无锡", "4");

ps_spts[8][0] = new Option("芳村", "1");
ps_spts[8][1] = new Option("无锡", "4");

function setStockArea(sptSrc, sptObj){
	x = sptSrc.selectedIndex;
	for (var m = sptObj.options.length - 1; m > 0; m --){
		sptObj.options[m] = null;
	}
	for (i = 0; i < ps_spts[x].length; i ++){
		sptObj.options[i]=new Option(ps_spts[x][i].text, ps_spts[x][i].value);
	}
	sptObj.options[0].selected=true;
}