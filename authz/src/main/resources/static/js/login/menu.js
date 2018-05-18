$(function() {
	getMainMenu();
});

/**
 * 主菜单取得
 */
function getMainMenu() {
	$.ajax({
		type : 'POST',
		url : ctx + '/listMainMenu',
		dataType : 'json',
		success : function(data, textStatus) {
			var tree = new treeMenu(data.result, '00');
			$("#mainMenu").append(tree.getLevel1Dom());
		}
	});
}

/**
 * 各个模块html生成
 */
function makeContent(url, hrefId){
	$.get(ctx + url, function(data){
		$("#content").html(data);
	});
	// 高亮选中menu
	$('a[id^="hrefMenu"').parent().removeClass("active");
	$('#' + hrefId).parent().addClass("active");
	// 导航设定
	
}

/**
 * 页面右侧的导航栏
 * @param pkid
 */
//function breadcrumb(pkid) {
//	var rootPkid = "";	// 根节点的pkid
//	for (var i = 0; i < tree.tree.length; i++ ) {
//		if (tree.tree[i].parentPkid == tree.root) {
//			// 找到根节点
//			rootPkid = tree.tree[i].pkid;
//			break;
//		}
//	}
//	// 便利tree生成右侧的导航
//	for (var i = 0; i < tree.tree.length; i++ ) {
//		if (tree.tree[i].pkid == pkid && rootPkid == tree.tree[i].parentPkid) {
//			alert(pkid)
//			// 点击的是一级菜单
//			var html = '<li>' + tree.tree[i].menuName + '</li>';
//			$('#breadcrumb').html(html).text();
//			break;
//		}
//	}
//	$('#levelTwo').remove();
//	for (var i = 0; i < tree.tree.length; i++ ) {
//		if (tree.tree[i].pkid == pkid && rootPkid != tree.tree[i].parentPkid) {
//			var html = '<li id="levelTwo">' + tree.tree[i].menuName + '</i>';
//			$('#breadcrumb').append(html);
//			break;
//		}
//	}
//}

/**
 * 构造菜单主函数
 */
function treeMenu(data, root) {
	// 树数据
	this.tree = data || [];
	// 根节点
	this.root = root;
};

treeMenu.prototype = {
	getLevel1Dom : function () {
		var html = '';
		// 取得根id, 根只有一个
		var level1_id = '';
		for (var i = 0; i < this.tree.length; i++ ) {
			if (this.tree[i].parentPkid == this.root) {
				level1_id = this.tree[i].pkid;
				break;
			}
		}
		// 构造菜单
		for (var i = 0; i < this.tree.length; i++ ) {
			// 第一级菜单
			if (this.tree[i].parentPkid == level1_id) {
				// 如果没有菜单图标，设置一个默认图标
				var icon = this.tree[i].iconUrl;
				if (!ZW.Object.notNull(icon)) {
					icon = 'fa-circle-o'
				}
				html += '<ul class="sidebar-menu"> \n';
				html += '<li class="treeview">';
				html += '	<a href="#"><i class="fa ' + icon + '\"> </i> <span>' + this.tree[i].menuName + '</span>';
				html += '	<span class="pull-right-container">';
				html += '		<i class="fa fa-angle-left pull-right"></i>';
				html += '	</span>';
				html += '	</a>';
				// 递归调用取得二级以下各级菜单
				html += this.getLevelNDom(this.tree[i].pkid);
				html += '</li>\n';
				html += '</ul>';
			}
		}
		return html;
	},
	getLevelNDom : function (parentId) {
		var html = '';
		html += '<ul class="treeview-menu">\n';
		for (var i = 0; i < this.tree.length; i++ ) {
			if (this.tree[i].parentPkid == parentId) {
				if (this.hasNext(this.tree[i].pkid)) {
					// 如果还有下级，设定下拉样式
					html += '<li>\n';
					html += '	<a href="#"><i class="fa fa-circle-o"></i> ' + this.tree[i].menuName + '\n';
					html += '	<span class="pull-right-container">\n';
					html += '		<i class="fa fa-angle-left pull-right"></i>\n';
					html += '	</span>\n';
					html += '	</a>\n';
					html += this.getLevelNDom(this.tree[i].pkid);
					html += '</li>\n';
				} else {
					// 没有下级，不设定样式
					html += '<li><a id="hrefMenu' + i + '" href="#" onClick="makeContent(\''+ this.tree[i].menuUrl + '\',\'hrefMenu' + i + '\');"><i class="fa fa-circle-o"></i> ' + this.tree[i].menuName + ' </a></li>\n';
				}
			}
		}
		html+='</ul>\n';
		return html;
	},
	hasNext : function (parentId) {
		// 判断树是否还有下级节点
		for (var i = 0; i < this.tree.length; i++ ) {
			if (this.tree[i].parentPkid == parentId) {
				return true;
			}
		}
		return false;
	}
}