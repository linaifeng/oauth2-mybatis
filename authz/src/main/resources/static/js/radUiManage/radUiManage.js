var zwtable;
var pageConstant = {
	columns : [
		{ title : '数据源名称', data : 'datasourceName' },
		{ title : '所属表名', data : 'tableName' },
		{ title : '页面元数据名称', data : 'uiDdName' },
		{ title : '查询方式', data : 'searchTypeName' },
		{ title : '数据库字段', data : 'dbField' },
		{ title : '字段类型', data : 'dataTypeName' },
		{ title : '页面表示类型', data : 'inputTypeName' },
		{ title : '校验 ', data : 'inputValidateName' },
		{ title : '页面显示名', data : 'inputTitleCh' },
		{ title : '操作', data : null,
			render : function(data, type, row, meta) {
				var html = "<td>";
				html+="<button type=\'button\' class=\'btn btn-primary btn-xs\' title=\'编辑\' onclick=\"goUpdatePage(\'" + row.pkid + "\')\">编辑</button> \n"
				html+="<button type=\'button\' class=\'btn btn-primary btn-xs\' title=\'删除\' onclick=\"del(\'" + row.pkid + "\')\">删除</button> \n";
				html+="</td>";
				return html;
			}
		}
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
		$('input[name="allCheckbox"]').prop("checked", false);
		zwtable.reload(ZW.Object.serialize($("#baseForm")));
	});

	//弹出新加页面，先解绑click事件，否则会执行多次
	$('#goAddPageUiManage').unbind("click").click(function(e) {
		//通知浏览器不要执行与事件关联的默认动作		
		e.preventDefault();
		comp.dialog({
			id: 'remoteModal_add',
			closeButton: true,
			btnClose: '关闭',
			title: '新建元数据管理',
			style: 'width:1000px',
			url: ctx + '/rad/radUiManage/goAddPage'
		}, function() {
			e.preventDefault();
			if(ZW.Validate.form("addForm")){
				var that =$(this);
				ZW.Ajax.doRequest("addForm",ctx + '/rad/radUiManage/add',null,function(data){
					ZW.Model.info(data.message,"确认",function(){
						zwtable.reload(ZW.Object.serialize($("#baseForm")));
						$("#remoteModal_add").modal('hide');
					});
				});
			}
		});
		$("#modal-dialog").draggable();//为模态对话框添加拖拽
	});

	// 批量删除
	$('#goBatchDelBtn').unbind("click").click(function(e) {
		var chks =[];
		$('#baseTable input[name="ids"]:checked').each(function(){
			chks.push($(this).val());	
		});
		if(chks.length==0) {
			ZW.Model.info("您没有选择任何内容!"); 
		}else{
			ZW.Model.confirm("确认要删除所选内容吗?",function(){	
				ZW.Ajax.doRequest(null,ctx +'/rad/radUiManage/batchDelete',{chks:chks.toString()},function(data){
					ZW.Model.info(data.message,null,function(){
						zwtable.reload(ZW.Object.serialize($("#baseForm")));
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
		url : ctx +'/rad/radUiManage/listByPage',
		checked : true,
		columns : pageConstant.columns
	});
}

//单个删除
function del(pkid){
	ZW.Model.confirm("确认删除吗？",function(){
		ZW.Ajax.doRequest(null,ctx +'/rad/radUiManage/del',{pkid:pkid},function(data){
			ZW.Model.info(data.message,null,function(){
				zwtable.reload(ZW.Object.serialize($("#baseForm")));
			});
		});
	});
}

//跳转编辑页面
function goUpdatePage(pkid){
	var params = {pkid:pkid};
	comp.dialog({
		id: 'remoteModal_update',
		closeButton: true,
		btnClose: '关闭',
		title: '修改元数据管理',
		style: 'width:1000px',
		url: ctx + '/rad/radUiManage/goUpdatePage' + "?" + $.param(params)
	}, function() {
		if(ZW.Validate.form("updateForm")){
			var that =$(this);
			ZW.Ajax.doRequest("updateForm",ctx +'/rad/radUiManage/update',null,function(data){
				ZW.Model.info(data.message,"确认",function(){
					zwtable.reload(ZW.Object.serialize($("#baseForm")));
					$("#remoteModal_update").modal('hide');
				});
			});
		}
	});
	$("#modal-dialog").draggable();//为模态对话框添加拖拽
}