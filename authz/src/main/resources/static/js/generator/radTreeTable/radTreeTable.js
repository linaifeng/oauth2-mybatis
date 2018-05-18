$(function () {
	// 初始化各个参照按钮和数据源选择js
	initPop("tableName");
});

/**
 * 代码生成
 */
function makeSinglePopCode() {
	ZW.Ajax.doRequest("baseForm",ctx +'/rad/radTreeTable/makeCode',null,function(data){
		$('#sourceLink').empty();
		$('#sourceLink').append("<a href=\"javascript:openView(\'" + data.data + "\')\">代码下载</a>");
	});
}