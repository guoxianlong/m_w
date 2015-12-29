var _preview_url_prefix = "/adult-admin/ptree/"

function verfy(event) {
	if (!$("#show").is(":hidden")) {
		return;
	}
	var idObj = $("#id");
	var id = idObj.val();
	sp.location = _preview_url_prefix+"preview2.jsp?id=" + id;
	$("#show").css("left", event.x - 220);
	$("#show").show();
}
function verfyWap20(event) {
	if (!$("#show2").is(":hidden")) {
		return;
	}
	var idObj = $("#id");
	var id = idObj.val();
	sp2.location = _preview_url_prefix+"preview2.jsp?id=" + id;
	$("#show2").css("left", event.x - 220);
	$("#show2").show();
}
function out() {
	$("#show").hide();
}
function out2() {
	$("#show2").hide();
}