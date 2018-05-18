$(function () {
	// 初始化各个参照按钮和数据源选择js
	initPop("tableName", "tableName2");
});

/**
 * 代码生成
 */
function makeSinglePopCode() {
	ZW.Ajax.doRequest("baseForm",ctx +'/rad/radOneToMany/makeCode',null,function(data){
		$('#sourceLink').empty();
		$('#sourceLink').append("<a href=\"javascript:openView(\'" + data.data + "\')\">代码下载</a>");
	});
}