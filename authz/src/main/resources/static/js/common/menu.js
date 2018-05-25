/**
 * 各个模块html生成
 */
function makeContent(url, hrefId){
	$.get(ctx + url, function(data){
		$("#content").html(data);
	});
	// 高亮选中menu
	$('a[id^="hrefMenu"').parent().removeClass("active");
	$('#' + hrefId).parent().addClass("active");
}