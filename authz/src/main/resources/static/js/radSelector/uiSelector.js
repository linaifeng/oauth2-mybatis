$(function () {
	initMultiselectData(ctx +'/rad/radSelector/listUiDd');
	var mult = $('#keepRenderingSort').multiselect({
		keepRenderingSort : true
	});
	$("#searchNameBtn").bind("click",function(){
		if(ZW.Validate.form("baseUiForm")){
			$("#keepRenderingSort").empty();
			initMultiselectData(ctx +'/rad/radSelector/listUiDdByLikeName?name=' + $('#uiName').val());
			mult.init2(mult, {
				keepRenderingSort : true
			});
		}
	}); 
});

/**
 * 初始化UI选择框数据
 * @returns
 */
function initMultiselectData(url){
	ZW.Ajax.doRequestIgnoreError("baseUiForm",url,null,function(data){
		if (data.length > 0) {
			var uiDdNameLen = 0;
			var tableNameLen = 0;
			for (var i = 0; i < data.length; i++) {
				if (uiDdNameLen < data[i].uiDdName.length ) {
					uiDdNameLen = data[i].uiDdName.length;
				}
				if (tableNameLen < data[i].tableName.length ) {
					tableNameLen = data[i].tableName.length;
				}
			}// stringPad(i+'', 3, '0', 'L')  + data[i].uiDdName
			for (var i = 0; i < data.length; i++) {
				$("#keepRenderingSort").append("<option value='" + data[i].uiDdName + "'>" + stringPad(data[i].uiDdName, uiDdNameLen, '&#8194;', 'R') + "&nbsp;" + stringPad(data[i].tableName, tableNameLen, '&#8194;', 'R') + "&nbsp;" + data[i].inputTitleCh + "</option>");
			}
		}
	},false);
}
// 参数：
// obj :对象
// len :长度
// Char:补位的字符
// way :补位的方向，L为左补 R为右补
function stringPad(obj, len, Char, way) {
	var val = obj;
	for (var i = 0; i < len - obj.length; i++) {
		if (way == "R" || way == "r") {
			val += Char;
		} else {
			val = Char + val;
		}
	}
	return val;
}