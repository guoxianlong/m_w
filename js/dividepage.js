function gotoPage(formname, formname2){
	formname.currentPage.value=formname2.pages.value - 1;
	formname.submit();
}
function firstPage(formname){
	formname.currentPage.value=0;
	formname.submit();
}
function previousPage(formname){
	formname.currentPage.value--;
	formname.submit();
}
function nextPage(formname){
	formname.currentPage.value++;
	formname.submit();
}
function lastPage(formname){
	formname.currentPage.value=formname.totalPage.value;
	formname.submit();
}
function buttonControl(formname){
	var currentPage = parseInt(formname.currentPage.value);
	var totalPage = parseInt(formname.totalPage.value);
	if(currentPage > 0){
		document.all.first_button.disabled=false;
		document.all.previous_button.disabled=false;
	}else{
		document.all.first_button.disabled=true;
		document.all.previous_button.disabled=true;
	}
	if(currentPage < totalPage){
		document.all.next_button.disabled=false;
		document.all.last_button.disabled=false;
	}else{
		document.all.next_button.disabled=true;
		document.all.last_button.disabled=true;
	}
}