var zwtable;
var pageConstant = {
	columns : [
		{ title : '项目名称', data : 'datasourceName' },
		{ title : '数据库名', data : 'dbName' },
		{ title : 'IP', data : 'ip' },
		{ title : '端口', data : 'port' },
		{ title : '驱动', data : 'driverClass' },
		{ title : '账号', data : 'dbUserName' },
		/*{ title : '密码', data : 'dbPassword' },*/
		{ title : '操作', data : null,
			render : function(data, type, row, meta) {
				var html = "<td>";
				html+="<button type=\'button\' class=\'btn btn-primary btn-xs\' title=\'编辑\' onclick=\"goUpdatePage(\'" + row.pkid + "\')\">编辑</button> \n"
				html+="<button type=\'button\' class=\'btn btn-primary btn-xs\' title=\'删除\' onclick=\"del(\'" + row.pkid + "\')\">删除</button> \n";
				html+="<button type=\'button\' class=\'btn btn-primary btn-xs\' title=\'连接测试\' onclick=\"connect(\'" + row.pkid + "\')\">连接测试</button> \n";
				html+="</td>";
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
			title: '新增数据源',
			style: 'width:600px',
			url: ctx + '/rad/radDatasource/goAddPage'
		}, function() {
			if(ZW.Validate.form("addForm")){
				// 是否已经存在
				$.get(ctx + '/rad/radDatasource/isExistRadDatasourceByName', {datasourceName:$("#datasourceName").val()}, function(data){
					if (data == true) {
						var name = $("#datasourceName");
						name.tips({side:1, msg : "该项目已经存在，请换个项目名称!", bg:'#FF2D2D', time:2});
						name.focus();
					} else {
						ZW.Ajax.doRequest("addForm",ctx + '/rad/radDatasource/add',null,function(data){
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
		destroy : true,
		name : '#baseTable',
		url : ctx +'/rad/radDatasource/listByPage',
		checked : false,
		sort : false,
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
		style: 'width:600px',
		url: ctx + '/rad/radDatasource/goUpdatePage' + "?pkid=" + pkid
	}, function() {
		if(ZW.Validate.form("updateForm")){
			ZW.Ajax.doRequest("updateForm",ctx +'/rad/radDatasource/update',null,function(data){
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
		ZW.Ajax.doRequest(null,ctx +'/rad/radDatasource/del',{pkid:pkid},function(data){
			ZW.Model.info(data.message,null,function(){
				zwtable.reload(ZW.Object.serialize($("#baseForm")));
			});
		});
	});
}

// 数据库连接测试
function connect(pkid) {
	comp.dialog({
		id: 'remoteModal_connect',
		closeButton: true,
		btnClose: '关闭',
		btnOk: '测试',
		title: '数据库连接测试',
		style: 'width:600px',
		url: ctx + '/rad/radDatasource/goConnectTest' + "?pkid=" + pkid
	}, function() {
		ZW.Ajax.doRequest("connectForm", ctx +'/rad/radDatasource/connectTest',null,function(data){
			ZW.Model.info(data.message);
		});
	});
	$("#modal-dialog").draggable();//为模态对话框添加拖拽
}