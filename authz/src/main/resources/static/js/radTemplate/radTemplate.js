var zwtable;
var pageConstant = {
	columns : [
		{ title : '模板类型', data : 'templateType'},
		{ title : '模板名称', data : 'name'},
		{ title : '后缀', data : 'suffix' },
		{ title : '备注', data : 'remark' },
		{ title : '操作', data : null,
			render : function(data, type, row, meta) {
				var html = "";
				 html+="<button type=\'button\' class=\'btn btn-primary btn-xs\' title=\'编辑\' onclick=\"goUpdatePage(\'" + row.pkid + "\')\">编辑</button> \n"
				 html+="<button type=\'button\' class=\'btn btn-primary btn-xs\' title=\'删除\' onclick=\"del(\'" + row.pkid + "\')\">删除</button> \n";
				return html;
			}
		}
	]
};
$(function () {
	$('#searchBtn').unbind("click").click(function(e) {
		e.preventDefault();
		zwtable.reload(ZW.Object.serialize($("#baseForm")));
	});

	// 弹出新加页面，先解绑click事件，否则会执行多次
	$('#goAddPage').unbind("click").click(function(e) {
		//通知浏览器不要执行与事件关联的默认动作		
		e.preventDefault();
		comp.dialog({
			id: 'remoteModal_add',
			closeButton: true,
			btnClose: '关闭',
			title: '新增模板',
			style: 'width:1000px',
			url: ctx + '/rad/radTemplate/goAddPage'
		}, function() {
			if(ZW.Validate.form("addForm")){
				// 是否已经存在
				$.get(ctx + '/rad/radTemplate/isExistRadTemplateByName', {datasourceName:$("#name").val()}, function(data){
					if (data == true) {
						var name = $("#name");
						name.tips({side:1, msg : "该模板已经存在，请换个模板名称!", bg:'#FF2D2D', time:2});
						name.focus();
					} else {
						ZW.Ajax.doRequest("addForm",ctx + '/rad/radTemplate/add',null,function(data){
							ZW.Model.info(data.message,"确认",function(){
								zwtable.reload(ZW.Object.serialize($("#baseForm")));
								$("#remoteModal_add").modal('hide');
							});
						});
					}
				});
			}
		});
		$("#modal-dialog").draggable();//为模态对话框添加拖拽
	});
});

function initTable(){
	zwtable = new ZWTable({
		name : '#baseTable',
		url : ctx +'/rad/radTemplate/listByPage',
		checked : false,
		sort : true,
		showlineNo: true,
		columns : pageConstant.columns
	});
}

//弹出编辑页面
function goUpdatePage(pkid){
	comp.dialog({
		id: 'remoteModal_update',
		closeButton: true,
		btnClose: '关闭',
		title: '编辑数据源',
		style: 'width:1000px',
		url: ctx + '/rad/radTemplate/goUpdatePage' + "?pkid=" + pkid
	}, function() {
		if(ZW.Validate.form("updateForm")){
			ZW.Ajax.doRequest("updateForm",ctx +'/rad/radTemplate/update',null,function(data){
				ZW.Model.info(data.message,"确认",function(){
					zwtable.reload(ZW.Object.serialize($("#baseForm")));
					$("#remoteModal_update").modal('hide');
				});
			});
		}
	});
	$("#modal-dialog").draggable();//为模态对话框添加拖拽
}

function del(pkid){
	ZW.Model.confirm("确认删除吗？",function(){
		ZW.Ajax.doRequest(null,ctx +'/rad/radTemplate/del',{pkid:pkid},function(data){
			ZW.Model.info(data.message,null,function(){
				zwtable.reload(ZW.Object.serialize($("#baseForm")));
			});
		});
	});
}
