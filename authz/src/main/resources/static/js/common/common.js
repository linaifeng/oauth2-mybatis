(function() {
	var init = function() {
		toastr.options = {
			userDefineButton: true,
			closeButton: true,
			debug: false,
			newestOnTop: false,
			progressBar: true,
			positionClass: 'toast-top-full-width',
			preventDuplicates: true,
			onclick: null,
			showDuration: 300,
			hideDuration: 1000,
			timeOut: 3000,
			extendedTimeOut: 0,
			showEasing: 'swing',
			hideEasing: 'linear',
			showMethod: 'fadeIn',
			hideMethod: 'fadeOut',
			tapToDismiss: false
		}
		$.ajaxSetup({
			contentType : "application/x-www-form-urlencoded;charset=utf-8",
			complete : function(xhr, status) {
				// 通过XMLHttpRequest取得响应头，sessionstatus
				var sessionstatus = xhr.getResponseHeader("sessionstatus");
				if (sessionstatus == "timeout") {
					// 如果超时就处理 ，指定要跳转的页面
					ZW.Model.info("session超时", "确认", function () {
						window.location.href = ctx +"/loginIndex";
					});
				}
			},
			error: function (xhr, status, e) {
				toastr.error('服务器错误');
				//自定义错误号512，表示ajax请求时服务器session已经超时。
				if (xhr.status == "512" || xhr.status == "511") {
					ZW.Model.info("session超时", "确认", function () {
						window.location.href = ctx +"/loginIndex";
					});
				}
				//App.unblockUI();
			},
			beforeSend: function (xhr) {
				// CSRF攻击使用的TOKEN，配合header的csrfMetaTags标签
				var token = $("meta[name='_csrf']").attr("content");  
				var header = $("meta[name='_csrf_header']").attr("content");
				if (ZW.Object.notNull(header)) {xhr.setRequestHeader(header, token); }
			}
		});
		if($.fn.dataTable) {
			$.extend($.fn.dataTable.defaults, {
				processing: true,
			    searching: false,
			    ordering:  false,
			    info: false,
			    pageButton: 'bootstrap',
			    lengthChange: false,
			    language: {
			    	processing: '加载中...',
			    	emptyTable: '未找到相关记录...',
			    	paginate: {
		                previous: '上一页',
		                next:     '下一页'
		            }
			    }
			});
		}
		$.extend({    
			formatNumber:function(src, pos) {    
		        var num = parseFloat(src).toFixed(pos);    
		        num = num.toString().replace(/\$|\,/g,'');    
		        if(isNaN(num)) return src;    
		        sign = (num == (num = Math.abs(num)));    
		        num = Math.floor(num*100+0.50000000001);    
		        cents = num%100;    
		        num = Math.floor(num/100).toString();    
		        if(cents<10) cents = "0" + cents;    
		        for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)    
		        num = num.substring(0,num.length-(4*i+3))+','+num.substring(num.length-(4*i+3));    
		        return (((sign)?'':'-') + num + '.' + cents);    
		    },
		    formatDate: function(src, fmt) {
		    	var d = new Date(src);
		    	if(isNaN(d)) return src;
		    	var o = {
		    			"M+": d.getMonth() + 1,
				        "d+": d.getDate(),
				        "h+": d.getHours(),
				        "m+": d.getMinutes(),
				        "s+": d.getSeconds(),
				        "q+": Math.floor((d.getMonth() + 3) / 3),
				        "S": d.getMilliseconds()
		    	};
				if (/(y+)/.test(fmt)) {
					fmt = fmt.replace(RegExp.$1, (d.getFullYear() + "").substr(4 - RegExp.$1.length));
				}
				for (var k in o) {
					if (new RegExp("(" + k + ")").test(fmt)) {
						fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
					}
				}
				return fmt;
		    }
		});
		/* 这个实现离大连框架中的表结构差的太远，无法直接使用，尽作为原理参考。
		 * $.get(ctx + '/security/resource/ajax/menus', null, function(data) {
			var currPageId;
			var _pagePath = $('#_page_path').val() == '' ? window.location.pathname : $('#_page_path').val();
			$.each(data, function(i, e) {
				if(e.pid == 0) {
					var menuStr = '<li class="treeview '
						+ (e.path == _pagePath ? 'active' : '')
						+ '"><a href="' + (e.type==1 ? e.path : '#')
						+ '"><i class="fa fa-' + (e.iconStyle ? e.iconStyle : 'folder') + '"></i> '
						+ '<span>' + e.name + '</span>';
					if(e.type == 0) {
						menuStr += '<span class="pull-right-container"><i class="fa fa-angle-left pull-right"></i></span></a>';
						menuStr += '<ul id="_menu_' + e.id + '" class="treeview-menu"></ul>';
					}else {
						menuStr += '</a>';
					}
					menuStr += '</li>';
					$('#_menu').append($(menuStr));
				}else {
					var menuStr = '<li class="menu-node" '+ (e.path == _pagePath ? 'class="active"' : '')
						+ '><a href="' + (e.type==1 ? e.path : '#')
						+ '"><i class="fa fa-circle-o"></i> '+ '<span>' + e.name + '</span>';
					if(e.type == 0) {
						menuStr += '<span class="pull-right-container"><i class="fa fa-angle-left pull-right"></i></span></a>';
						menuStr += '<ul id="_menu_' + e.id + '" class="treeview-menu"></ul>';
					}else {
						menuStr += '</a>';
					}
					menuStr += '</li>';
					$('#_menu_' + e.pid).append($(menuStr));
				}
			});
			var currTarget = $('a[href="' + _pagePath + '"]');
			currTarget.parents('.treeview').addClass('active');
			currTarget.parents('.menu-node').addClass('active');
		}, 'json');*/
	}
	init();
})()

var _toPage = function(pageNum) {
	var _f = $('#queryForm')[0] ? $('#queryForm')[0] : $('form')[0];
	if(_f == undefined) alert('页面不存在form！'); 
	else $(_f).append('<input type="hidden" id="pageNum" name="pageNum" value="' + pageNum + '" />').submit();
}

var comp = {}
comp.modaloptions = {
		id			: '_modal',
		url 		: '',
		title		: '默认标题',
		body		: '默认信息',
		head		: true,
		foot		: true,
		closeable	: true,
		userDefineButton	: false,
		closeButton	: false,
		btnOk		: '确定',
		btnUserDefine	: '暂存',
		btnClose	: '取消',
		big			: false,
		style		: '',
		backdrop	: 'static',
		keyboard	: true,
		show		: true,
		type		: 'get',
		data		: ''
};
comp.modalstr = function(opt) {
	var start = '<div class="modal fade" id="' + opt.id + '" tabindex="-1" role="dialog" aria-labelledby="bsmodaltitle" aria-hidden="true" style="position:fixed;">';
	start += '<div id="modal-dialog" class="modal-dialog' ;
	if(opt.big) start += 'modal-lg';
	start +='" style="' + opt.style + '"><div class="modal-content">';

	var end = '</div></div></div>';
	
	var head = '';
	if(opt.head){
		head += '<div class="modal-header">';
		if(opt.closeable){
			head += '<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>';
		}
		head += '<h3 class="modal-title">' + opt.title + '</h3></div>';
	}
	
	var body = '<div class="modal-body"><p><h4>' + opt.body +'</h4></p></div>';
	
	var foot = '';
	if(opt.foot){
		foot += '<div class="modal-footer"><button type="button" id="_modalBtnOk' + opt.id + '" class="btn btn-primary btn-sm">'+opt.btnOk+'</button>';
		
		if(opt.userDefineButton){
			foot += '<button type="button" id="userDefineButton" class="btn btn-primary btn-sm">' + opt.btnUserDefine + '</button>';
		}
		
		if(opt.closeButton){
			foot += '<button type="button" data-dismiss="modal" class="btn btn-default btn-sm">' + opt.btnClose + '</button>';
		}
		foot += '</div>';
	}
	
	return start + head + body + foot + end;
};
comp.dialog = function(options, func){
	var opt = $.extend({}, comp.modaloptions, options);
	var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
	var csrfHeader = $("meta[name='_csrf_header']").attr("content");
	var csrfToken = $("meta[name='_csrf']").attr("content");
	var headers = {};
	headers[csrfHeader] = csrfToken;
	$('body').append(comp.modalstr(opt));
	if(opt.url) {
		$.ajax({
			type:opt.type,
			data:opt.data,
			url:opt.url,
			dataType:'html',
			headers: headers,
			success: function(html) {
				$('#' + opt.id + ' div.modal-body').empty().append(html);
			}
		});
	}
	var $modal = $('#' + opt.id); 
	console.log($modal)
	$modal.modal(opt);
	
	$('#_modalBtnOk' + opt.id).on('click', function() {
		var flag = true;
		if(func) flag = func();
		if(flag) $modal.modal('hide');
	});
	
	
	if(opt.btnCancelFunc) {
		$('#_modalBtnOk' + opt.id).on('click', function() {
			var flag = true;
			if(opt.btnOkFunc) flag = opt.btnOkFunc;
			if(flag) $modal.modal('hide');
		});
	}
	
	$modal.on('hidden.bs.modal', function() {
		$modal.remove();
	});
	
	$modal.modal('show');
};

comp.block = function(options) {
	options = $.extend(true, {}, options);
    var html = '<div class="loading-message ' + (options.boxed ? 'loading-message-boxed' : '') + '"><img src="' + ctx + '/images/loading-spinner-grey.gif" align=""><span>&nbsp;&nbsp;' + (options.message ? options.message : '加载中...') + '</span></div>';
    if (options.target) { // element blocking
        var el = $(options.target);
        if (el.height() <= ($(window).height())) {
            options.cenrerY = true;
        }
        el.block({
            message: html,
            baseZ: options.zIndex ? options.zIndex : 1000,
            centerY: options.cenrerY !== undefined ? options.cenrerY : false,
            css: {
                top: '10%',
                border: '0',
                padding: '0',
                backgroundColor: 'none'
            },
            overlayCSS: {
                backgroundColor: options.overlayColor ? options.overlayColor : '#555',
                opacity: options.boxed ? 0.05 : 0.1,
                cursor: 'wait'
            }
        });
    } else { // page blocking
        $.blockUI({
            message: html,
            baseZ: options.zIndex ? options.zIndex : 1000,
            css: {
                border: '0',
                padding: '0',
                backgroundColor: 'none'
            },
            overlayCSS: {
                backgroundColor: options.overlayColor ? options.overlayColor : '#555',
                opacity: options.boxed ? 0.05 : 0.1,
                cursor: 'wait'
            }
        });
    }
};

comp.unblock = function(target) {
    if (target) {
        $(target).unblock({
            onUnblock: function() {
                $(target).css('position', '');
                $(target).css('zoom', '');
            }
        });
    } else {
        $.unblockUI();
    }
}

comp.resetForm = function(target, validator) {
	$(':input', target).each(function(i, e) {
		if(e.type == 'text') e.value = '';
	});
	if($.fn.iCheck) target.find("[type='radio']").iCheck('uncheck');
	if(validator) validator.resetForm();
};