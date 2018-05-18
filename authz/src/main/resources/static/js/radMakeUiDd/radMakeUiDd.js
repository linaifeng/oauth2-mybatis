var zwtable;
var pageConstant = {
	columns : [
		{ title : '表名', data : 'tableName' },
		{ title : '件数', data : 'tableRows' },
		{ title : '备注', data : 'tableComment' }
	]
};

$(function () {
	// 全选控制
	$('#allCheckbox').on('click', function() {
		var isChecked = $(this).prop("checked");
		$("input[name='ids']").prop("checked", isChecked);
	});

	$('#searchBtn').unbind("click").click(function(e) {
		e.preventDefault();
		var data = {
			pkid : $('#dataSource').val()
		}
		zwtable.reload(data);
	});

	// UI元数据生成
	$('#makeUiDd').unbind("click").click(function(e) {
		//通知浏览器不要执行与事件关联的默认动作
		e.preventDefault();
		var _checkArr = zwtable.getChecked();
		var chks = [];
		if (_checkArr != null && _checkArr.length > 0) {
			for (var i = 0; i < _checkArr.length; i++) {
				if (ZW.Object.notNull(_checkArr[i])) {
					chks.push(_checkArr[i].tableName);
				}
			}
		}
		if(chks.length==0) {
			ZW.Model.info("您没有选择任何内容!"); 
		}else{
			ZW.Model.confirm("确认要生成UI元数据吗?",function(){
				ZW.Model.loading();
				$("#makeUiDd").attr({"disabled":"disabled"});
				ZW.Ajax.doRequest(null,ctx +'/rad/radMakeUiDd/makeUi',{dataSourcePkid : $("#dataSource").val(), chks:chks.toString()},function(data){
					ZW.Model.info(data.message,null,function(){
						ZW.Model.loadingClose();
						$("#makeUiDd").removeAttr("disabled");
						$('#progressbar').modal('hide');
					});
				});
			});		
		}
	});
});

function initTable(){
	zwtable = new ZWTable({
		destroy : true,
		name : '#baseTable',
		url : ctx +'/rad/radMakeUiDd/listTable',
		checked : true,
		sort : false,
		paging: false,
		language: {"sInfoEmpty":"","sInfoFiltered":""},
		columns : pageConstant.columns
	});
}