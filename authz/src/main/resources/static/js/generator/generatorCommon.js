function initPop(selectInputName, selectTableInputName2) {
	// 数据源改变，取得对应数据源table列表
	$('#dataSource').change(function(){ 
		var p1=$(this).children('option:selected').val(); //这就是selected的值 
		if (ZW.Object.notNull(p1)) {
			showTables(p1, selectInputName);
			if (ZW.Object.notNull(selectTableInputName2)) {
				showTables(p1, selectTableInputName2);
			}
		} else {
			$('#' + selectInputName).empty();
			if (ZW.Object.notNull(selectTableInputName2)) {
				$('#' + selectTableInputName2).empty();
			}
		}
	});

	//弹出Ptn选择页面，先解绑click事件，否则会执行多次
	$('#ptnsBtn').unbind("click").click(function(e) {
		//通知浏览器不要执行与事件关联的默认动作
		e.preventDefault();
		comp.dialog({
			id: 'remoteModal_ptns',
			closeButton: true,
			btnClose: '关闭',
			title: '选择Ptn',
			style: 'width:600px',
			url: ctx + '/rad/radSelector/goPtnSelectorPage'
		}, function() {
			var chkValues = "";
			$("input[name='ids']:checked").each(function(index){ 
				chkValues = chkValues + this.value + ",";
			});
			if (ZW.Object.notEmpty(chkValues)) {
				chkValues = chkValues.substring(0, chkValues.length - 1);
			}
			$("#ptns").val(chkValues);
			$("#remoteModal_ptns").modal('hide');
		});
		$("#modal-dialog").draggable();//为模态对话框添加拖拽
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#keyFieldsBtn').unbind("click").click(function(e) {
		uiPop('remoteModal_key', 'keyFields', selectInputName);
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#listFieldsBtn').unbind("click").click(function(e) {
		uiPop('remoteModal_list', 'listFields', selectInputName);
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#entPrintFieldsBtn').unbind("click").click(function(e) {
		uiPop('remoteModal_ent', 'entPrintFields', selectInputName);
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#updPrintFieldsBtn').unbind("click").click(function(e) {
		uiPop('remoteModal_upd', 'updPrintFields', selectInputName);
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#entDBFieldsBtn').unbind("click").click(function(e) {
		uiPop('remoteModal_entdb', 'entDBFields', selectInputName);
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#updDBFieldsBtn').unbind("click").click(function(e) {
		uiPop('remoteModal_upddb', 'updDBFields', selectInputName);
	});

	// 以下 一对多 UI选择页面
	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#listFieldsBtn2').unbind("click").click(function(e) {
		uiPop('remoteModal_list2', 'listFields2', selectTableInputName2);
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#entPrintFieldsBtn2').unbind("click").click(function(e) {
		uiPop('remoteModal_ent2', 'entPrintFields2', selectTableInputName2);
	});

	//弹出Ui选择页面，先解绑click事件，否则会执行多次
	$('#updPrintFieldsBtn2').unbind("click").click(function(e) {
		uiPop('remoteModal_upd2', 'updPrintFields2', selectTableInputName2);
	});
}

/**
 * 下拉数据源后，table下拉赋值
 * @param pkid
 */
function showTables(datasource, selectInputName) {
	ZW.Ajax.doRequestIgnoreError("baseForm",ctx +'/rad/radSelector/listTable',{datasource : datasource},function(data){
		var list = data.data;
		var results = list;
		var html="";
		$('#' + selectInputName).empty();
		if (results != null && results.length > 0) {
			for(var i = 0;i<results.length;i++){
				var item=results[i];
				html+="<option value='" + item.tableName + "'>" + item.tableName + "</option>";
			}
			$('#' + selectInputName).append(html);
		}
	});
}

function uiPop (compId, parentId, selectInputName) {
	comp.dialog({
		id: compId,
		closeButton: true,
		btnClose: '关闭',
		title: '选择UI元数据',
		style: 'width:1100px;height:1000px',
		type: 'POST',
		url: ctx + '/rad/radSelector/goUiSelectorPage?datasource=' + $("#dataSource").val() + '&tableName=' + $("#" + selectInputName).val()
	}, function() {
		var chkValues = '';
		$("#keepRenderingSort_to option").each(function(index){ 
			chkValues = chkValues + this.value + ",";
		});
		if (ZW.Object.notEmpty(chkValues)) {
			chkValues = chkValues.substring(0, chkValues.length - 1);
		}
		$("#" + parentId).val(chkValues);
		$("#" + compId).modal('hide');
	});
	$("#modal-dialog").draggable();//为模态对话框添加拖拽
}

function openView(url){
	window.open(url)
}