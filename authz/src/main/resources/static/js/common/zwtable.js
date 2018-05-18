
/**
 * Query-table 封装
 * @Author:李鑫
 * 
 * @param:
 * name 表格定位
 * url 后台请求url
 * checked 是否开启选中
 * columns 列定义
 * sort 是否开启排序
 * order: [[ 1, 'asc' ]]，开启排序后，指定排序列和升降序
 * showDetails: true 开启行详细页面，默认关闭
 * detailFun: 详细页面函数
 * 可以使用并不限于以上的DataTable属性设置
 * 
 * @function：
 * getTable 获取表格对象
 * getChecked 获取选中的数据
 * 
 * @Api:
 * http://datatables.club/reference/api/
 * 
 */
var ZWTable = (function(baseParam) {
	this.$table = $(baseParam.name);
	return function() {
		var columnReady = function() {
			baseParam.bAutoWidth = false;
			var _columns = [];
			if(baseParam.checked) {
				if(baseParam.columns) {
					_columns.push({
						class: 'text-center',
						data: null,
						orderable: false,
						sortable: false,
						render: function (data, type, row, meta) {
							return '<label> <input type="checkbox" name="ids" value=\'' + row.pkid + '\'/> <span class="lbl"></span></label>';
						}
					});
				}
			}

			for(var i=0; i<baseParam.columns.length; i++) {
				if(baseParam.columns[i].tip) {
					baseParam.columns[i].render = function (data, type, row, meta) {
						if(data.length > 15) {
							return data.substring(0, 6) + "...";
						} else {
							return data;
						}
					}
					baseParam.columns[i].fnCreatedCell = function(nTd, sData, oData, IRow, ICol) {
						$(nTd).attr("title", sData);
					}
					
				}
			}

			if(baseParam.showDetails) {
				_columns.push({
					class: 'details-control',
					orderable: false,
					data: null,
					defaultContent: ''
				});
			}
			_columns = _columns.concat(baseParam.columns);
			baseParam.columns = _columns;
		};

		var tableReady = function() {
			if(baseParam.data && typeof baseParam.data == "object") {
				baseParam.fnServerParams = function (aoData) {
					$.extend(aoData, baseParam.data);
				}
			}
			$(baseParam.name).dataTable($.extend(true, {}, ZWTable.getBaseOption(baseParam.url), baseParam));
		}

		var listenerReady = function() {
			$(baseParam.name).on('click', 'td.details-control', function () {
				var tr = $(this).closest('tr');
				var row = $(this).parents("table").DataTable().row( tr );
				if ( row.child.isShown() ) {
					row.child.hide();
					tr.removeClass('shown');
				}
				else {
					row.child( baseParam.detailFun(row.data()) ).show();
					tr.addClass('shown');
				}
			});
		}
		columnReady();
		tableReady();
		listenerReady();
	}();
});

ZWTable.prototype = {
	$table: this.$table,
	getTable: function() {
		return this.$table.DataTable();
	},
	getChecked: function() {
		var _data = [];
		var _table = this.getTable();
		this.$table.find('input[type="checkbox"][name!="allCheckbox"]:checked').each(function(i, o) {
			_data.push(_table.row($(o).parents("tr")).data());
		});
		return _data;
	},
	reload: function(data) {
		var _table = this.getTable();
		if(data) {
			_table.settings()[0].ajax.data = data;
		}
		_table.ajax.reload();
	}
};

ZWTable.getBaseOption = (function(url) {
	var baseOption = function(url) {
		return {
			serverSide: true, //如果是服务器方式，必须要设置为true
			language: {
				"sProcessing": "数据加载中请稍候...",
				"sLengthMenu": "每页 _MENU_ 项",
				"sZeroRecords": "没有匹配结果",
				//"sInfo": "当前显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项。",
				"sInfo": "共 _TOTAL_ 项",
				"sInfoEmpty": "当前显示第 0 至 0 项，共 0 项",
				"sInfoFiltered": "(由 _MAX_ 项结果过滤)",
				"sInfoPostFix": "",
				"sSearch": "搜索:",
				"sUrl": "",
				"sEmptyTable": "没有相关数据",
				"sLoadingRecords": "载入中...",
				"sInfoThousands": ",",
				"oPaginate": {
					"sFirst": "首页",
					"sPrevious": "上一页",
					"sNext": "下一页",
					"sLast": "末页",
					"sJump": "跳转"
			},
				"oAria": {
					"sSortAscending": ": 以升序排列此列",
					"sSortDescending": ": 以降序排列此列"
				}
			}, //语言
			pageLength: 10, //分页数
			pageButton: 'bootstrap',
			sDom: "<'top'>rt<'bottom'ilp><'clear'>",
			bLengthChange: true,
			aLengthMenu:[5, 10, 15, 50],
			bInfo : true,
			sPaginationType: "full_numbers",
		 	ajax: function(data, callback, settings) {
				$.ajax({
					type: "POST",
					url: url,
					cache : false,  //禁用缓存
					data: $.extend(baseQueryCondition(data), settings.ajax.data),	//传入已封装的参数
					dataType: "json",
					success: function(result) {
						callback(result);
					},
					error: function(XMLHttpRequest, textStatus, errorThrown) {
						//$.dialog.alert("查询失败");
						//$wrapper.spinModal(false);
					}
				});
			}
		};
	};
	
	var baseQueryCondition = function(data) {
		var param = $.extend({}, data);
		if (data.order&&data.order.length&&data.order[0]) {
			var _column = 0;
			var _data = data.columns[data.order[0].column].data;
			if(_data && !/^[0-9]*$/gi.test(_data)) {
				param.orderBy = _data.replace(/([A-Z])/g,"_$1").toLowerCase();
				param.orderDir = data.order[0].dir;
				param.orderByClause = param.orderBy + " " + param.orderDir;
			}
		}
		param.draw = data.draw;
		param.pageNum = data.start / data.length + 1;
		param.pageSize = data.length;
		return param;
	};
	
	return baseOption(url);
});