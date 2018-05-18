/*
 * ZW工具组件
 */
var ZW = ZW || {};
$(function() {

	//class是isValidCheckbox的选择框Yes或No，value设为1或0
	$(".isValidCheckbox [sh-isValid]").bind('click', function(){
		$(".isValidCheckbox [hi-isValid]").val((this.checked)?"1":"0");
	});

	//改写dialog是title可以使用html
	$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
		_title: function(title) {
			var $title = this.options.title || '&nbsp;';
			if(("title_html" in this.options)&&(this.options.title_html == true))title.html($title);
			else title.text($title);
		}
	}));

});
	ZW={
		Object:{
		notNull:function(obj) {//判断某对象不为空..返回true 否则 false
			if (obj === null)return false;else if (obj === undefined)return false;else if (obj === "undefined")return false;else if (obj === "")return false;else if (obj === "[]")return false;else if (obj === "{}")return false;else return true;},
		notEmpty:function(obj) {//判断某对象不为空..返回obj 否则 ""
					if (obj === null)return "";else if (obj === undefined)return "";else if (obj === "undefined")return "";else if(obj === "")return "";else if (obj === "[]")return "";else if (obj === "{}")return "";else return obj;},
		serialize:function(form){
			var o = {};
			$.each(form.serializeArray(),function (index){
				if (o[this['name']]){
					o[this['name']] = o[this['name']] + "," + this['value'];
				}else{
					o[this['name']] = this['value'];
				}
			});

			return o;
		},
		//组合变量传递keys,values,types形式// 转换JSON为字符串
		comVar:function(variables){var keys = "", values = "", types = "",vars={};if (variables) {$.each(variables, function() {if (keys != "") {keys += ",";values += ",";types += ",";}keys += this.key;values += this.value;types += this.type;});}vars={keys:keys,values:values,types:types};return vars;},
			repack : function(tableid) {
				var tmpid = tableid || "";
				if (tmpid === "") {
					return;
				}
				var tableids = [];
				if (tableid instanceof Array) {
					// 数组时，逐个处理
					tableids = tableid;
				} else {
					if ($("#" + tableid).is("form")) {
						$("#" + tableid + " table").each(function() {
							// form下所有table逐个处理
							tableids.push($(this).attr("id"));
						});
					} else {
						tableids[0] = tableid;
					}
				}
				for (var i = 0; i < tableids.length; i++) {
					// 逐个Table进行压缩
					var id = tableids[i];
					$("#" + id + " tbody tr").each(function(index) {
						// 逐行处理各个控件
						var tr = $(this);
						// input或select改名
						tr.find("input, select").each(function() {
							// 获取name属性，如果没有则不处理
							var that = $(this);
							var name = that.attr("name") || "";
							if (name !== "") {
								// 修改名称下标
								name = name.replace(/\[\d\]/, "[" + index + "]");
								that.attr("name", name);
							}
						});
					});
				}
			},
			isString :function(str) {
				return (typeof str == 'string') && str.constructor == String;
			},
			isArray : function(obj) {
				return (typeof obj == 'object') && obj.constructor == Array;
			},
			isNumber : function(obj) {
				return (typeof obj == 'number') && obj.constructor == Number;
			},
			isDate : function(obj) {
				return (typeof obj == 'object') && obj.constructor == Date;
			},
			isFunction : function(obj) {
				return (typeof obj == 'function') && obj.constructor == Function;
			},
			isObject : function(obj) {
				return (typeof obj == 'object') && obj.constructor == Object;
			}
		},
		Dict:{//ids 对应html中id值(自己命名，多个用逗号分隔)。keys 对应key值(数据库中的key，多个逗号分隔)。type(可选)1.请选择，2.自定义数组。默认不填.dfstr (可选)自定义数组
				setSelect:function(ids,keys,type,dfstr){
					// CSRF攻击使用的TOKEN，配合header的csrfMetaTags标签
					var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
					var csrfHeader = $("meta[name='_csrf_header']").attr("content");
					var csrfToken = $("meta[name='_csrf']").attr("content");
					var headers = {};
					headers[csrfHeader] = csrfToken;
					$.ajax({type:'POST',headers: headers,url:ctx+'/sys/sysDict/getDictByKey',data:{ids:ids,keys:keys},dataType:'json',success:function(data,textStatus){
						if(data.res==1){
							var map=data.obj;var idss= ids.split(",");var opts="",dictName="";
			        		if(type==1){
			        			for(var i=0;i<idss.length;i++){
			        				dictName=map[idss[i]].dictName;
			        				opts="<option value=''>请选择</option>";
			        				$.each(map[idss[i]].items,function(n,v) {
			        					opts+="<option value='"+v.dictCode+"'>"+v.dictName+"</option>";
			        				});
			        				$("#"+idss[i]+" select").append(opts);
			        				$("#"+idss[i]).trigger("liszt:updated");
			        			};
			        		}
			        		else if(type==2){
			        			var dfstrs= dfstr.split(",");
			        			for(var i=0;i<idss.length;i++){
			        				dictName=map[idss[i]].dictName;
			        				$("#"+idss[i]+" label").html(dictName);
			        				opts="<option value=''>"+dfstrs[i]+"</option>";
			        				$.each(map[idss[i]].items,function(n,v) {
			        					opts+="<option value='"+v.dictCode+"'>"+v.dictName+"</option>";	});
			        					$("#"+idss[i]+" select").append(opts);
			        				};
			        			}
			        		else{

			        			for(var i=0;i<idss.length;i++){var name=map[idss[i]].name;$("#"+idss[i]+" label").html(name);opts="";$.each(map[idss[i]].items,function(n,v) {opts+="<option value='"+v.dictName+"'>"+v.dictName+"</option>";});$("#"+idss[i]+" select").append(opts);}}}
			        	//适应手机

			        	if("ontouchend" in document) {
			        		$(".chosen-select").removeClass("chosen-select");}
			        	//下拉框样式
			        	else{
			        		$(".chosen-select").chosen();  $(".chosen-select-deselect").chosen({allow_single_deselect:true});}}});
			   }
		},
		Page:{//跳转分页
			  jump:function(formId,num,JpFun){$("#"+formId+" .pageNum").val(num);eval(JpFun+"()");},
			  //设置分页单个显示数量
			  setSize:function(formId,size,JpFun){$("#"+formId+" .pageNum").val(1);$("#"+formId+" .pageSize").val(size);eval(JpFun+"()");},
			  setSizeNew:function(formId,size,JpFun,pageNumNew){$("#"+formId+" .pageNum").val(pageNumNew);$("#"+formId+" .pageSize").val(size);eval(JpFun+"()");},
			  /*自定义跳转分页*/
			  jumpCustom:function(formId,pageId,leng,JpFun){
				  var choseJPage=$("#"+pageId+" .choseJPage").val();
				  if(typeof(choseJPage) == "undefined")
					  	return;
				  else if(choseJPage==0)
					  choseJPage=1;
				  else if(choseJPage>leng)
					  choseJPage=leng;
				  $("#"+formId+" .pageNum").val(choseJPage);
				  eval(JpFun+"()");
			  },
			  /*设置分页方法,formId 分页参数Form的Id,pageId 分页位置Id,pagesize 分页显示数量,pagenum 页码,totalCount 数据总数,fun 获得数据方法名*/
			setPage: function (formId, pageId, pagesize, pagenum, totalCount, fun) {
				if (totalCount > 0) {
					var pageul = $("#" + pageId + " ul"), html = "";
					pageul.empty();
					var leng = parseInt((totalCount - 1) / pagesize) + 1; // 总页数
					if (pagenum - 1 >= 1) {
						html += "<li class='prev'><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;,1,&apos;" + fun + "&apos;)' href='#'>首页</a></li>";
						html += "<li class='prev'><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + (pagenum - 1) + ",&apos;" + fun + "&apos;)' href='#'>上一页</a></li>";
					}
					else {
						html += "<li class='prev disabled'><a href='##'>首页</a></li>";
						html += "<li class='prev disabled'><a href='##'>上一页</a></li>";
					}
					
					
					/*var all = leng > 6 ? 6 : leng; //总显示个数,正常为all+1条,现在设2，显示为3条
					var start = 1;
					//all/2取整后的页数减去当前页数，判断是否为大于0
					var before = pagenum - parseInt(all / 2);
					if (before > 1) start = before;
					var end = start + all;
					if (end > leng) {
						end = leng;
						start = leng > all ? (leng - all) : 1;
					}
					//现在设2,和显示对应
					if (pagenum > 2 && leng > 3) {
						html += "<li class='' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + (pagenum - 2) + ",&apos;" + fun + "&apos;)' >..</a></li>";
					}
					for (var ii = start; ii <= end; ii++) {
						var page = (parseInt(ii));
						if (pagenum == page) {
							html += "<li class='active' ><a href='#'>" + page + "</a></li>";
						}
						else {
							html += "<li><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + ii + ",&apos;" + fun + "&apos;)' href='#'>" + page + "</a></li>";
						}
					}
					if (pagenum <= (leng - 2) && leng > 3) {
						html += "<li class='' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + (pagenum + 2) + ",&apos;" + fun + "&apos;)' >..</a></li>";
					}*/
					
					//当总页数 <= 7 全部显示
					if(leng <= 7){
						for (var i = 1; i <= leng; i++) {
							if (pagenum == i) {
								html += "<li class='active' ><a href='#'>" + i + "</a></li>";
							}
							else {
								html += "<li><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + i + ",&apos;" + fun + "&apos;)' href='#'>" + i + "</a></li>";
							}
						}
					}else{//当总页数 > 7
						// 考虑当前页是多少页，当前页 <5  显示为 1-5 + ... + 尾页
						// 考虑当前页是多少页，当前页 = 5  显示为 1 + ... + 4 + 5 + 6 + ... + 尾页
						var start = 0;
						var end = 0;
						if(pagenum < 5){
							start = 1;
							end = 5;
							for (var i = 1; i <= 5; i++) {
								if (pagenum == i) {
									html += "<li class='active' ><a href='#'>" + i + "</a></li>";
								} else {
									html += "<li><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + i + ",&apos;" + fun + "&apos;)' href='#'>" + i + "</a></li>";
								}
							}
							//html += "<li class='disabled' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + (pagenum + 1) + ",&apos;" + fun + "&apos;)' >...</a></li>";
							html += "<li class='disabled' ><a href='#'>...</a></li>";
							html += "<li class='' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + leng + ",&apos;" + fun + "&apos;)' >" + leng + "</a></li>";
						}else{
							html += "<li class='' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + 1 + ",&apos;" + fun + "&apos;)' >1</a></li>";
							//html += "<li class='disabled' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + (pagenum + 1) + ",&apos;" + fun + "&apos;)' >...</a></li>";
							html += "<li class='disabled' ><a href='#'>...</a></li>";
							start = pagenum - 1;
							if(pagenum + 3 < leng){
								end = start + 3;
								for (var i = start; i < end; i++) {
									if (pagenum == i) {
										html += "<li class='active' ><a href='#'>" + i + "</a></li>";
									}
									else {
										html += "<li><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + i + ",&apos;" + fun + "&apos;)' href='#'>" + i + "</a></li>";
									}
								}
								//html += "<li class='disabled' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + (pagenum) + ",&apos;" + fun + "&apos;)' >...</a></li>";
								html += "<li class='disabled' ><a href='#'>...</a></li>";
								html += "<li class='' ><a href='#' onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + leng + ",&apos;" + fun + "&apos;)' >" + leng + "</a></li>";
							}else{
								start = pagenum;
								if(start + 5 >= leng){
									start = leng - 5 + 1;
								}
								end = leng ;
								for (var i = start; i <= end; i++) {
									if (pagenum == i) {
										html += "<li class='active' ><a href='#'>" + i + "</a></li>";
									}
									else {
										html += "<li><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + i + ",&apos;" + fun + "&apos;)' href='#'>" + i + "</a></li>";
									}
								}
							}
						}
					}
					
					if (pagenum + 1 <= leng) {
						html += "<li class='next'><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + (pagenum + 1) + ",&apos;" + fun + "&apos;)' href='#'>下一页</a></li>";
						html += "<li class='next'><a onclick='ZW.Page.jump(&apos;" + formId + "&apos;," + leng + ",&apos;" + fun + "&apos;)' href='#'>末页</a></li>";
					}
					else {
						html += "<li class='next disabled'><a href='##'>下一页</a></li>";
						html += "<li class='next disabled'><a href='##'>末页</i></a></li>";
					}
					html+="<li class='disabled disablednone'><a style='margin-right: 2px ! important;' href='##'>每页</a></li>";
					html+="<li class='disabled disablednone'><select class='pageing-form-select' onchange='selectChangeFunc(&apos;" + formId + "&apos;, &apos;"+ fun + "&apos;,&apos;" + pagenum + "&apos;,&apos;" + pagesize + "&apos;)' style='width:75px;float:left;' title='显示条数'>"+"<option value='5'  "+((pagesize==5)?"selected='selected'":"")+" >5</option>" +"<option value='10' "+((pagesize==10)?"selected='selected'":"")+" >10</option>" +"<option value='15' "+((pagesize==15)?"selected='selected'":"")+" >15</option>"+"</select></li>";
					html+="<li class='disabled disablednone'><a style='margin-left: -7px ! important;' href='##'>项</a></li>";
					//html += "<li class='next'><input onkeyup='this.value=this.value.replace(/\D/g,&apos;&apos;)' type='number' min='1' max='" + leng + "'  placeholder='页码' class='choseJPage' style='margin-right: 10px;width: 70px;height: 34px;padding-left: 5px; border: 1px solid #ccc;' ></li>";
					// html += "<li ><a style='float: right;' class='btn btn-mini' onclick='ZW.Page.jumpCustom(&apos;" + formId + "&apos;,&apos;" + pageId + "&apos;," + leng + ",&apos;" + fun + "&apos;);' href='##'>跳转</a></li>";
					//html+="<li class='disabled'><a href='##'>共" + leng + "页<font color='red'>"+ totalCount +"</font>条</a></li>";
					html += "<li class='disabled disablednone'><a style='margin-left: 3px ! important;letter-spacing:2px ! important;' href='##'>共" + totalCount + "项</a></li>";
					pageul.append(html);
				}
			  },
			  /*简化版,设置分页方法,formId 分页参数Form的Id,pageId 分页位置Id,pagesize 分页显示数量,pagenum 页码,totalCount 数据总数,fun 获得数据方法名*/
			  setSimPage:function(formId,pageId,pagesize,pagenum,totalCount,fun){
			  	if(totalCount>0){
			  		var pageul = $("#"+pageId+" ul"),html="";
			  		pageul.empty();
			  		var leng = parseInt((totalCount - 1)/pagesize)+1;
			  		if(pagenum - 1 >= 1){html+="<li class='prev'><a onclick='ZW.Page.jump(&apos;"+formId+"&apos;,1,&apos;"+fun+"&apos;)' href='#'>首</a></li>";}
			  		else{html+="<li class='prev disabled'><a href='##'>首</a></li>";}
			  		var all = leng>2?2:leng;//总显示个数,正常为all+1条,现在设2，显示为3条
			  		var start = 1;
			  		//all/2取整后的页数减去当前页数，判断是否为大于0
			  		var before = pagenum - parseInt(all/2);
			  		if(before > 1)start = before;
			  		var end = start + all;
			  		if(end > leng){end = leng;start = leng > all ? (leng - all) : 1;}
			  	    //现在设2,和显示对应
			  		if(pagenum>2&&leng>3){html+="<li class='' ><a href='#' onclick='ZW.Page.jump(&apos;"+formId+"&apos;,"+(pagenum-2)+",&apos;"+fun+"&apos;)' >..</a></li>";}
			  		for(var ii = start ; ii <= end; ii++){
			  			var page = (parseInt(ii));
			  			if(pagenum==page){html+="<li class='active' ><a href='#'>"+page+"</a></li>";}
			  			else{html+="<li><a onclick='ZW.Page.jump(&apos;"+formId+"&apos;,"+ii+",&apos;"+fun+"&apos;)' href='#'>"+page+"</a></li>";}
			  		}
			  		if(pagenum<=(leng-2)&&leng>3){html+="<li class='' ><a href='#' onclick='ZW.Page.jump(&apos;"+formId+"&apos;,"+(pagenum+2)+",&apos;"+fun+"&apos;)' >..</a></li>";}
			  		if(pagenum + 1 <= leng){html+="<li class='next'><a onclick='ZW.Page.jump(&apos;"+formId+"&apos;,"+leng+",&apos;"+fun+"&apos;)' href='#'>尾</a></li>";}
			  		else{html+="<li class='next disabled'><a href='##'>尾</i></a></li>";}
			  		html+="<li class='disabled'><a href='##'>共"+leng+"页</a></li>";
			  		pageul.append(html);
			  	}
			  },
			  setSelectAll:function(){
				//复选框
				$('table th input:checkbox').on('click', function(){
					var that = this;
					$(this).closest('table').find('tr > td:first-child input:checkbox').each(function(){
						this.checked = that.checked;
						$(this).closest('tr').toggleClass('selected');
					});
				});
			  }
		},
		Tags:{//设置按钮用的方法,id 这行的id,pBtn 按钮组
			cleanForm:function(formId){$("#"+formId+" input[type$='text']").val("");$("#"+formId+" textarea").val("");},
			setFunction:function(id,pBtn){
				var h="";
				if(pBtn!=null&&pBtn.length>0){
					h+="<td align='center'>";
					//<i title="**" onClick="functionName('pkid','resourceUrl','targetResourcePkid');" class="***">**</i>
					for(var i=0;i<pBtn.length;i++){h+="<button type='button' class='btn btn-primary btn-xs' title='"+ZW.Object.notEmpty(pBtn[i].resourceName)+"' onclick='"+ZW.Object.notEmpty(pBtn[i].resourceFunType)+"(&apos;"+id+"&apos;,&apos;"+ZW.Object.notEmpty(pBtn[i].resourceUrl)+"&apos;,&apos;"+ZW.Object.notEmpty(pBtn[i].targetResourcePkid)+"&apos;)' class='btn btn-primary btn-xs"+ZW.Object.notEmpty(pBtn[i].icon)+"'>"+ZW.Object.notEmpty(pBtn[i].resourceName)+"</button>";}
					h+="</td>";
				}else{h+="<td></td>";}
				return h;
			},
			/*class是isValidCheckbox的选择框Yes或No，value设为1或0
			 *formId form的Id
			 */
			isValid:function(formId,val){$("#"+formId+" .isValidCheckbox [hi-isValid]").val(val);if(val==1){$("#"+formId+" .isValidCheckbox [sh-isValid]").prop("checked",true);}else{$("#"+formId+" .isValidCheckbox [sh-isValid]").prop("checked",false);}}
		},
		Model:{//loading框
			loading:function(){
				$('#baseTable').loader('show','<i class="icon-refresh fa-spin">数据加载中请稍后...</i>');
			},
			//关闭loading框
			loadingClose:function(){
				$('#baseTable').loader('hide');
			},
			//Str 询问语句可以是html格式,fn  方法
			confirm:function(Str,fn){
				bootbox.dialog({
	                message: ZW.Object.notNull(Str)?Str:"确认吗？",
	                title: "询问",
	                onEscape:true,
	                buttons: {
	                     OK: {
	                        label: "确认",
	                        className: "btn-primary",
	                        callback: function () {if(typeof(fn) == 'function'){fn.call(this);}}
	                    },
	                    Cancel: {
	                        label: "取消",
	                        className: "btn-default",
	                        callback: function () {}
	                    }
	                }
	            });
			},
			info:function(Str,title,fn,closeButtonShowFlag){
				if(ZW.Object.notNull(Str)){
					bootbox.dialog({
		                message: ZW.Object.notNull(Str)?Str:"确认吗？",
		                title: ZW.Object.notNull(title)?title:"询问",
		                onEscape:true,
		                closeButton:(closeButtonShowFlag)?true:false,
		                buttons: {
		                    OK: {
		                        label: "确认",
		                        className: "btn-primary",
		                        callback: function () {
		                        	if(typeof(fn) == 'function'){
						        		fn.call(this);
						        	}
		                        }
		                    }
		                }
		            });
				}
			},
			//Str 语句可以是html格式（如：<span>保存成功</span>）,fn 方法
			error:function(Str,title){
					var arr_html = '';
					var arr = Str.split(",");
					arr_html += "<ul class='custom-bootbox'>";
					$.each(arr, function(i,val){
						arr_html += '<li>' + val + '</li>'
					});
					arr_html += "</ul>";
					bootbox.dialog({
						message: arr_html,
	                	title: ZW.Object.notNull(title)?title:"警告信息",
            			buttons: {
		                    OK: {}
            			}
					});
			},
			//divId,fn 方法
			check:function(id,title,fn){
				$("#"+id).removeClass('hide').dialog({resizable:false,modal:true,title:"<div class='widget-header'><h4 class='smaller'>"+(ZW.Object.notNull(title)?title:"查看")+"</h4></div>",title_html:true,buttons:[{html:"<i class='icon-remove bigger-110'></i>&nbsp;取消","class":"btn btn-xs",click:function(){$(this).dialog("close");if(typeof(fn) == 'function'){fn.call(this);}}}]});
			},
			//审核
			audit:function(id,title,savefn,rejectfn,cancelfn,width){
				$("#"+id).modal('show');
				$(".modal-dialog").css("width", width + "px");
				$(".modal-title").html(title);
				$('#auditPass').unbind('click').on('click', function(e) {
					e.preventDefault();
			    // $('#auditPass').on('click', function(){
			    	if(typeof(savefn) == 'function'){
			    		savefn.call(this);
			    	}
				});
			    $('#auditReject').unbind('click').on('click', function(e) {
			    	e.preventDefault();
			    	if(typeof(rejectfn) == 'function'){
			    		rejectfn.call(this);
			    	}
				});
			    $('#auditCancel').unbind('click').on('click', function(e) {
			    	e.preventDefault();
					if(typeof(cancelfn) == 'function'){
						cancelfn.call(this);
					}
			    });
			},
			//调整
			adjust:function(id,title,rejectReason,width,reCommitfn,cancelfn){
				$("#"+id).modal('show');
				$(".modal-dialog").css("width", width + "px");
				$(".modal-title").html(ZW.Object.notNull(title)?title:"调整");
				$("#rejectReason").val(rejectReason);
				$('#auditReCommit').unbind('click').on('click', function(e) {
					e.preventDefault();
			    	if(typeof(reCommitfn) == 'function'){
			    		reCommitfn.call(this);
			    	}
				});
				$('#auditCancel').unbind('click').on('click', function(e) {
					e.preventDefault();
			    	if(typeof(cancelfn) == 'function'){
			    		cancelfn.call(this);
			    	}
				});
			}
		},
		Validate:{//判断是否是英文数字,是返回true，不是返回false
				  isEnNum:function(str){if(/^[0-9a-zA-Z]+$/.test(str))return true;return false;},
				  //判断是否是数字,是返回true，不是返回false(支持小数点，逗号)
				  isNum:function(str){if(/^-?\d+\.?\d*$/.test(str))return true;return false;},
				  //判断是否是英文,是返回true，不是返回false
				  isEn:function(str){if(/^[A-Za-z]+$/.test(str))return true;return false;},
				  //判断是否是电子邮箱,是返回true，不是返回false
				  isEmail:function(email){if(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/.test(email))return true;return false;},
				  //判断是否是日期,是返回true，不是返回false
				  isDate:function(date){if(date.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/))return true;return false;},
				  //判断是否是日期时间,是返回true，不是返回false
				  isDatetime:function(datetime){if(datetime.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/))return true;return false;},
				  //判断是否为合法http(s)
				  isUrl:function(str){if(/http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/.test(str))return true;return false;},
				  //判断数值是否在范围内(不包含临界):min 最少值,max 最大值,是返回true，不是返回false
				  numrange:function(v,min,max){v=parseInt(v);min=parseInt(min);max=parseInt(max);if((min<v)&&(v<max))return true;return false;},
				  //判断数值是否在范围内(包含临界),min 最少值,max 最大值,是返回true，不是返回false
				  numrangeth:function(v,min,max){v=parseInt(v);min=parseInt(min);max=parseInt(max);if((min<=v)&&(v<=max))return true;return false;},
				  //判断是否合法的固定电话：
				  isFixedPhone:function(phoneNum){if(phoneNum.match(/^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/))return true;return false;},
				  //判断是否合法的固定电话：
				  isMobilePhone:function(phoneNum){if(phoneNum.match(/^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/))return true;return false;},
				  //最大长度的验证(最好使用maxLength属性即可，不用js验证了）。
				  maxLength:function(val,minLen,maxLen){min=parseInt(min);max=parseInt(max);if(val.match(/^\S{,20}$/)){return true;}return false;},
				  //判断最大值 最小值 是否在范围内(不包含临界):min 最少值,max 最少值,是返回true，不是返回false
				  minNumber:function(v,min){v=parseInt(v);min=parseInt(min);if((min<=v))return true;return false;},
				  maxNumber:function(v,max){v=parseInt(v);max=parseInt(max);if((max>=v))return true;return false;},
				  lessthanNumber:function(v,min){v=parseInt(v);min=parseInt(min);if((min<v))return true;return false;},
				  greaterThan:function(v,max){v=parseInt(v);max=parseInt(max);if((max>v))return true;return false;},
				  //指定小数点前几位和后几位以及最大19位(包含小数点)
				  decimals:function(v,min,max){v;min = parseInt(min);max = parseInt(max);var maxNum = "19";var tmp = v.split(".");var num = v.length;if(v.indexOf(".")!=-1){if((num <= maxNum) && (tmp[0].length <= max) && (tmp[1].length <= min)){return true;}}else{if((num <= max) && (num <= maxNum)){return true;}}return false;},
				  //表单验证fromId,使用方法 在表单必须使用zwValidate属性
				  form:function(fromId,side) {
					var res=true;
					side=ZW.Object.notNull(side)?side:1;
				  	$('#'+fromId+" input[zwValidate], #" + fromId + " select[zwValidate]").each(function(){
				  		if(res==false)return;var that=$(this);
				  		var zwValidate = $(this).attr("zwValidate").split(",");$.each(zwValidate,function(n,v){
				  			if(res==false)return;
				  			if(v=='required'){if(!ZW.Object.notNull($.trim(that.val()))){that.tips({side:side,msg : "必要字段！",bg:'#FF2D2D',time:1});that.focus();res=false;}}
				  			else if(v=='email'){if(ZW.Object.notNull(that.val())){if(!ZW.Validate.isEmail(that.val())){that.tips({side:side,msg : "电子邮箱不正确！",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='date'){if(ZW.Object.notNull($.trim(that.val()))){if(!ZW.Validate.isDate(that.val())){that.tips({side:side,msg : "日期格式不正确！",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='datetime'){if(ZW.Object.notNull(that.val())){if(!ZW.Validate.isDatetime(that.val())){that.tips({side:side,msg : "日期时间格式不正确！",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='numrange'){if(ZW.Object.notNull(that.val())){var min=that.attr("min");var max=that.attr("max");if(!ZW.Validate.numrange(that.val(),min,max)){that.tips({side:side,msg : "数字范围："+min+"~"+max,bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='numrangeth'){if(ZW.Object.notNull(that.val())){var min=that.attr("min");var max=that.attr("max");if(!ZW.Validate.numrangeth(that.val(),min,max)){that.tips({side:side,msg :"数字范围："+min+"~"+max,bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='en'){if(ZW.Object.notNull(that.val())){if(!ZW.Validate.isEn(that.val())){that.tips({side:side,msg:"只能输入英文",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='ennum'){if(ZW.Object.notNull(that.val())){if(!ZW.Validate.isEnNum(that.val())){that.tips({side:side,msg:"只能输入英文或数字",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='isNum'){if(ZW.Object.notNull(that.val())){if(!ZW.Validate.isNum(ZW.Comma.getValue(that.val()))){that.tips({side:side,msg:"只能输入数字",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='isFixedPhone'){if(ZW.Object.notNull(that.val())){if(!ZW.Validate.isFixedPhone(that.val())){that.tips({side:side,msg:"只能输入固定电话号码",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='isMobilePhone'){if(ZW.Object.notNull(that.val())){if(!ZW.Validate.isMobilePhone(that.val())){that.tips({side:side,msg:"只能输入英文或数字",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='maxLength'){if(ZW.Object.notNull(that.val())){var min=that.attr("minLen");var max=that.attr("maxLen");if(!ZW.Validate.maxLength(that.val(),min,max)){that.tips({side:side,msg:"只能输入英文或数字",bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='minNumber'){if(ZW.Object.notNull(that.val())){var min=that.attr("min");if(!ZW.Validate.minNumber(that.val(),min)){that.tips({side:side,msg : "最小值为："+min,bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='maxNumber'){if(ZW.Object.notNull(that.val())){var max=that.attr("max");if(!ZW.Validate.maxNumber(that.val(),max)){that.tips({side:side,msg : "最大值为："+max,bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='lessthanNumber'){if(ZW.Object.notNull(that.val())){var min=that.attr("min");if(!ZW.Validate.lessthanNumber(that.val(),min)){that.tips({side:side,msg : "最小值为："+min,bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='greaterThan'){if(ZW.Object.notNull(that.val())){var max=that.attr("max");if(!ZW.Validate.greaterThan(that.val(),max)){that.tips({side:side,msg : "最大值为："+max,bg :'#FF2D2D',time:1});that.focus();res=false;}}}
				  			else if(v=='decimals'){if(!ZW.Object.notNull(that.val())){that.tips({side:side,msg : "必要字段！",bg:'#FF2D2D',time:1});that.focus();res=false;}else{var min=that.attr("data-minDecimals");var max=that.attr("data-maxDecimals");if(!ZW.Validate.decimals(ZW.Comma.getValue(that.val()),min,max)){that.tips({side:side,msg : "小数点前最多为"+max+"位，小数点后最多为"+min+"位",bg :'#FF2D2D',time:4});that.focus();res=false;}}}
				  			//extend
				          });
				  	});
				  	return res;
				  }
		},
		Date:{//时间格式化(默认),time 时间
			  Default:function(time){return ZW.Object.notNull(time)?(new Date(time).Format("yyyy-MM-dd")):" ";},
			  //时间格式化,time 时间,fmt 格式
			  Format:function(time,fmt){return ZW.Object.notNull(time)?(new Date(time).Format(fmt)):"";}
		},
		Numberic: {
			// 数值格式化，默认2位
			Default:function(num){return ZW.Numberic.Format(num, 2);},
			// 数值格式化，可限定位数
			Format:function(num, digit) {
					var tpNum = '0.00';
					if(undefined != num) {
						tpNum = num;
					}
					tpNum = new Number(tpNum);
					if(isNaN(tpNum)) {
					    return '0.00';
					}
					tpNum = tpNum.toFixed(digit) + '';
					var re = /^(-?\d+)(\d{3})(\.?\d*)/;
					while(re.test(tpNum)){
						tpNum = tpNum.replace(re, "$1,$2$3")
					}
					return tpNum;
				}
		},
		Money:{
			//金额数值格式化，默认8位
			Default:function(money) {return ZW.Money.Format(money, 8);},
			//金额数值格式化，可限定位数
			Format:function formatMoney(money, digit) {
					return "¥" + ZW.Numberic.Format(money, digit);
				}
		},
		Comma:{
			// 初始化的时候自动添加逗号(3位)
			addComma:function(objs){
				objs.each(function() {
					var that = $(this);
					var v = null;
					if (that.is("span")) {
						v = that.text() || "";
						if (v !== "") {
							that.text(ZW.Comma.Format(v));
						}
					} else {
						v = that.val() || "";
						if (v !== "") {
							that.val(ZW.Comma.Format(v));
						}
					}
				});
			},
			// 请求的时候删除逗号
			delComma:function(objs){
				// 由于后台check出错时，无法将控制权交还给前台，因此在后台使用@NumberFormat来进行去逗号操作
//				objs.each(function() {
//					var v = $(this).val() || "";
//					if (v !== "") {
//						v = v.replace(/,/g, "");
//						$(this).val(v);
//					}
//				});
			},
			// 请求的时候删除逗号
			getValue:function(obj){
				var retVal = "";
				if (obj) {
					if (ZW.Object.isString(obj)) {
						return obj.replace(/,/g, "");
					} else {
						if (obj.val()) {
							return obj.val().replace(/,/g, "");
						}
					}
				}
				return retVal;
			},
			// 获得焦点去除逗号，失去焦点加逗号。
			Default:function(str){
				$(str).unbind('focus').focus(function (){
					var v = $(this).val().replace(/,/g, "");
					$(this).val(v);
				});
				$(str).unbind('blur').blur(function (){
					var v = $(this).val();
					// 防止输入多个逗号，无论输入几个逗号全部清空一次重新校验
					v = $(this).val().replace(/,/g, "");
					$(this).val(ZW.Comma.Format(v));
				});
			},
			// 自动添加逗号正则(3位)
			Format:function(nStr){
			    nStr += '';
			    x = nStr.split('.');
			    x1 = x[0];
			    // console.log(x.length)
			    x2 = x.length > 1 ? '.' + x[1] : '';
			    var rgx = /(\d+)(\d{3})/;
			    while (rgx.test(x1)) {
			            x1 = x1.replace(rgx, '$1' + ',' + '$2');
			    }
			    return x1 + x2;
			}
		},
		Url:{//获取url中的参数,name 参数名,当不存在返回空字符串
			 getParam:function(name) {
			    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
			    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
			    if (r != null) return unescape(r[2]); return ""; //返回参数值
			}
		},
		Ajax:{//异步请求, form表单ID,url请求路径,param参数对象,如：{a:'test',b:2},fn回调函数
			doRequest:function(form,url,param,fn,async){
				ZW.Object.repack(form); //
				var params = form || param || {};
				if (typeof form == 'string'){
					params = $.extend(param || {},ZW.Object.serialize($("#" + form)),{menu:ZW.Url.getParam("menu")});
				}
				//这个debug语句不要删除。会经常用到。
				//console.log("========in doRequest,params is:"+url);
				//console.log(params);
				if (!ZW.Object.notNull(async)) {
					async = true;
				}
				$.ajax(
					{type:'POST',url:url,data:params,dataType:'json',async:async,
						success:function(data, textStatus) {
							if(data.res==1){
								if (typeof(fn)=='function'){
									fn.call(this, data);
								}
								
							} else if (data.res==2) {
								// check error
								var prefix = (form) ? "#" + form + " " : "";
								var that = $(prefix + "[name^='" + data.checkErrorName + "']");

								ZW.Component.scroller.moveTo(that);

								that.tips({side:1, msg : data.message, bg :'#FF2D2D', time:5});
							} else {
								ZW.Model.loadingClose();
								if(ZW.Object.notNull(data.message)){
									ZW.Model.error(data.message);
								}
							}
						},
						error: function (XMLHttpRequest, textStatus, errorThrown) {
							//setDisabled('#_modalBtnOkremoteModal_add', false);
							if (XMLHttpRequest.status == "500") {
								window.location.href = encodeURI(ctx +"/error?exception=" + XMLHttpRequest.responseText);
							}
							//自定义错误号512，表示ajax请求时服务器session已经超时。
							if (XMLHttpRequest.status == "512" || XMLHttpRequest.status == "511") {
								ZW.Model.info("session超时", "确认", function () {
									window.location.href = ctx +"/loginIndex";
								});
							}
							if (XMLHttpRequest.status == "400") {
								ZW.Model.error("请求包含语法错误或者是不能正确执行。");
							}
							if (XMLHttpRequest.status == "403") {
								ZW.Model.error("该请求被禁止访问，请确认Token并确认是否请求超时。");
							}
						},
						beforeSend:function(xhr){
							$.blockUI({ message: '<div class="loading-message"><img src="' + ctx + '/images/loading-spinner-grey.gif"></img><span>&nbsp;&nbsp;加载中...</span></div>'});
							// CSRF攻击使用的TOKEN，配合header的csrfMetaTags标签
							var token = $("meta[name='_csrf']").attr("content");  
							var header = $("meta[name='_csrf_header']").attr("content");
							if (ZW.Object.notNull(header)) {xhr.setRequestHeader(header, token); }
							if (ZW.Object.notNull(comp)) {comp.block({target : ".modal-content"});}
						},
						complete:function(){
							// 取session中的二次提交token，并刷新页面的token
							// $.ajax({type:'POST', url:ctx + '/sys/sysSession/getSession' , async:false, dataType:'json',
								// success:function(data, textStatus) {
									// $("input[name='sys_token']").val(data.data);
								// },
								// error: function (XMLHttpRequest, textStatus, errorThrown) {
								// }
							// });
							if (ZW.Object.notNull(comp)) {comp.unblock(".modal-content");}
							$.unblockUI();
						}
					});
			},
			doRequestIgnoreError:function(form,url,param,fn,async){
		    	var params = form || param || {};
		    	if (typeof form == 'string'){
		    		params = $.extend(param || {},ZW.Object.serialize($("#" + form)),{menu:ZW.Url.getParam("menu")});
		    	}
		    	$.ajax({type:'POST',url:url,data:params,dataType:'json',async:async,
		    		success:function(data, textStatus) {
		    		if (typeof(fn)=='function'){
		    			fn.call(this, data);}
		    		},
		    		error:function(){return;},
		    		beforeSend:function(xhr){
		    			// CSRF攻击使用的TOKEN，配合header的csrfMetaTags标签
						var token = $("meta[name='_csrf']").attr("content");  
						var header = $("meta[name='_csrf_header']").attr("content");
						if (ZW.Object.notNull(header)) {xhr.setRequestHeader(header, token); }
		    		},
		    		complete:function(){
		    		}
		    	});
		    }
		},
		File:{
			//obj:对象传this, aFmats:允许格式,用"|"分隔
			fileType:function(obj,aFmats){
				if(ZW.Object.notNull(aFmats)){var fileType=obj.value.substr(obj.value.lastIndexOf(".")+1).toLowerCase();//获得文件后缀名
					var aFmat=aFmats.split("|");for (f in aFmat){if(aFmat[f]==fileType){return;}}$(obj).tips({side:3,msg:'请上传'+aFmats+'格式的文件',bg:'#FF2D2D',time:3});$(obj).val('');
				}
			}
		},
		Component:{
			getUploader:function(urlStr,fn){
				// CSRF攻击使用的TOKEN，配合header的csrfMetaTags标签
				var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
				var csrfHeader = $("meta[name='_csrf_header']").attr("content");
				var csrfToken = $("meta[name='_csrf']").attr("content");
				var headers = {};
				headers[csrfHeader] = csrfToken;
				return new plupload.Uploader({
					runtimes : 'html5,flash,silverlight,html4',
					browse_button: 'browse', // this can be an id of a DOM element or the DOM element itself
					container: document.getElementById('container'), // ... or DOM Element itself
					url: urlStr,
					multipart_params: { 'token': $('#token').val()},
					headers : headers,
					filters : {
				        max_file_size : '10mb',
				        mime_types: [
				            {title : "Image files", extensions : "jpg,gif,png"},
				            {title : "Office files", extensions : "xls,xlsx,doc,docx,txt,pdf"},
				            {title : "Zip files", extensions : "zip"},
				            {title : "workflow files", extensions : "bpmn"}
				        ]
				    },
				 	// Flash settings
				    flash_swf_url : ctx+'/static/plupload/Moxie.swf',

				    // Silverlight settings
				    silverlight_xap_url : ctx+'/static/plupload/js/Moxie.xap',
				    init: {
				        PostInit: function() {
				            document.getElementById('filelist').innerHTML = '已经上传的文件列表如下';
				            document.getElementById('start-upload').onclick = function() {
				                uploader.start();
				                return false;
				            };
				        },
				        FilesAdded: function(up, files) {
				            plupload.each(files, function(file) {
				                document.getElementById('filelist').innerHTML += '<div id="' + file.id + '">' + file.name + ' (' + plupload.formatSize(file.size) + ') <b></b></div>';
				            });
				        },
				        UploadProgress: function(up, file) {
				            document.getElementById(file.id).getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
				        },
				        FileUploaded: function(up, file, responseObject) {
				        	var result = JSON.parse(responseObject.response);
				        	document.getElementById('console').innerHTML = "发布结果：" + result.message;
				        	if (typeof(fn)=='function'){
				    			fn.call(this);
				    		}
				        },
				        UploadComplete: function(up,files){
				        	//document.getElementById('console').innerHTML += "<br/>上传结果：共" + files.length + "个文件上传完成。";
				        },
				        Error: function(up, err) {
				            document.getElementById('console').innerHTML = "<br/>Error #" + err.code + ": " + err.message;
				        }
				    }
				})},
			getUploaderById:function(urlStr,containerId,browserId,starterId,filelistId,consoleId,fn){
				// CSRF攻击使用的TOKEN，配合header的csrfMetaTags标签
				var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
				var csrfHeader = $("meta[name='_csrf_header']").attr("content");
				var csrfToken = $("meta[name='_csrf']").attr("content");
				var headers = {};
				headers[csrfHeader] = csrfToken;
				return new plupload.Uploader({
				runtimes : 'html5,flash,silverlight,html4',
				browse_button: browserId, // this can be an id of a DOM element or the DOM element itself
				container: document.getElementById(containerId), // ... or DOM Element itself
				url: urlStr,
				multipart_params: { 'token': $('#token').val()},
				headers : headers,
				filters : {
			        max_file_size : '10mb',
			        mime_types: [
			            {title : "Image files", extensions : "jpg,gif,png"},
			            {title : "Office files", extensions : "xls,xlsx,doc,docx,txt,pdf"},
			            {title : "Zip files", extensions : "zip"},
			            {title : "workflow files", extensions : "bpmn"}
			        ]
			    },
			 	// Flash settings
			    flash_swf_url : ctx+'/static/plupload/Moxie.swf',

			    // Silverlight settings
			    silverlight_xap_url : ctx+'/static/plupload/js/Moxie.xap',
			    init: {
			        PostInit: function() {
			            document.getElementById(filelistId).innerHTML = '已经上传的文件列表如下';
			            document.getElementById(starterId).onclick = function() {
			                uploader.start();
			                return false;
			            };
			        },
			        FilesAdded: function(up, files) {
			            plupload.each(files, function(file) {
			                document.getElementById(filelistId).innerHTML += '<div id="' + file.id + '">' + file.name + ' (' + plupload.formatSize(file.size) + ') <b></b></div>';
			            });
			        },
			        UploadProgress: function(up, file) {
			            document.getElementById(file.id).getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
			        },
			        FileUploaded: function(up, file, responseObject) {
			        	var result = JSON.parse(responseObject.response);
			        	document.getElementById(consoleId).innerHTML = "发布结果：" + result.message;
			        	if (typeof(fn)=='function'){
			    			fn.call(this,result);
			    		}
			        },
			        UploadComplete: function(up,files){
			        	//document.getElementById(consoleId).innerHTML += "<br/>上传结果：共" + files.length + "个文件上传完成。";
			        },
			        Error: function(up, err) {
			            document.getElementById(consoleId).innerHTML = "<br/>Error #" + err.code + ": " + err.message;
			        }
			    }
			})},
			setCascadeOptions:function(url,resName,type,dfstr,root){
				$.ajax({
					type:'POST',url:ctx+url,data:{resName:resName,type:type},dataType:'json',success:function(data,textStatus){
						var map=data.obj;
						var opts="";
        				opts="<option value=''>"+dfstr+"</option>";
        				$.each(map[resName],function(n,v) {
        					if(root == "true"){
        						opts+="<option value='"+v.code+"' data-id='"+v.code+"' data-parentid='"+v.parentId+"'>"+v.name+"</option>";
        					} else {
        						opts+="<option class='hide' value='"+v.code+"' data-id='"+v.code+"' data-parentid='"+v.parentId+"'>"+v.name+"</option>";
        					}
        				});
        				$("#"+resName).append(opts);
	        		}
		        })
			},
			scroller : {
				moveTo : function (controler) {
					var that = controler;

					var dh = $(document).height();
					var wh = $(window).height();
					var tb = that.offset().top + that.outerHeight() + 10;
					var scrolltop = tb - wh;

					$("body").animate({
						scrollTop: scrolltop
					}, 1000);
				}
			},
			fuzzySearch : function(control, fn) {// 模糊检索控件
				control.typeahead({
					source: function (query, process) {
						// 防止页面中的input控件中的textcontent影响用户体验，在每次画面重取数据时，清空
						if (this.$element[0]) this.$element[0].textContent = "";
						// 每次重取数据时清空关联提交key的value，避免影响提交数据
			    		var name = this.$element[0].dataset.refer || '';
			    		if (name) {
			            	$("#" + name).val("");
			    		}
			    		// 获取指定的提交url，如果url没有指定，则视为不与服务器交互
			    		var url = this.$element[0].dataset.url || '';
			    		if (!url) return;
						ZW.Ajax.doRequestIgnoreError(null, ctx + url, {query:query}, function(data) {
			                var resultList = data.obj.list.map(function (item) {
			                    var aItem = { id: item.id, name: item.name };
			                    return JSON.stringify(aItem);
			                });
			                return process(resultList);
						});
					},
					matcher: function (obj) {
						if (obj === undefined) return -1;
						var item = JSON.parse(obj);
						return ~item.name.toLowerCase().indexOf(this.query.toLowerCase());
					},
					sorter: function (items) {
						var beginswith = [], caseSensitive = [], caseInsensitive = [], item;
						while (aItem = items.shift()) {
							var item = JSON.parse(aItem);
							if (!item.name.toLowerCase().indexOf(this.query.toLowerCase()))
								beginswith.push(JSON.stringify(item));
							else if (~item.name.indexOf(this.query)) caseSensitive.push(JSON.stringify(item));
							else caseInsensitive.push(JSON.stringify(item));
						}

						return beginswith.concat(caseSensitive, caseInsensitive);
					},
					highlighter: function (obj) {
			    		var item = JSON.parse(obj);
			        	var query = this.query.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g, '\\$&')
			        	return item.name.replace(new RegExp('(' + query + ')', 'ig'), function ($1, match) {
			        		return '<strong>' + match + '</strong>';
			        	});
					},
					updater: function (obj) {
			    		var item = JSON.parse(obj);
			    		// 每次选择后，更新关联的key
			    		var name = this.$element[0].dataset.refer || '';
			    		if (name) {
			            	$("#" + name).val(item.id);
			    		}
			    		if (fn != undefined && typeof(fn)=='function'){
			    			fn.call(this);
			    		}
			        	return item.name;
					}
				});
			},
			showBtns :function (containerid) {
				var url = ctx +"/sys/sysResource/getBannedBtns";
				var pageid = $("#currPageId");
				if (containerid) {
					pageid = $("#" + containerid + " #currPageId");
				} else {
					pageid = $("#currPageId");
				}
				if (pageid) {
					ZW.Ajax.doRequestIgnoreError(null, ctx + url, {pageid:pageid.val()}, function(data) {
						if (data.obj) {
							$.each(data.obj, function(index, item) {
								// 根据id删除按钮
								var obj = null;
								if (containerid) {
									obj = $("#" + containerid + " #" + item);
								} else {
									obj = $("#" + item);
								}
								if (obj) {
									// 目前按钮类型有两种，一是普通的button类型，另一种是select类型
									var parent = obj.parent();
									if (parent && parent.is("div")) {
										// 父对象是div时，判断其class是否存在【search-btn】，存在则删除父对象，否则移除自身
										var clz = parent.attr("class");
										if (clz.indexOf("search-btn") >= 0) {
											parent.remove();
										} else {
											obj.remove();
										}
									}
								}
							});
						}
					});
				}
			},
			disableEnterKey : function (id) {
				var that = $("#" + id);
				if (that) {
					that.keydown(function(e){
						 keycode = e.which || e.keyCode;
						 if (keycode==13) {
							 if (e.target.tagName == "TEXTAREA") {return true;}
							 return false;
						 }
					});
				}
			}
		}
	};

/*
 * 对Date的扩展，将 Date 转化为指定格式的String
 *月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
 *年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 *例子：
 *(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 *(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
 */
Date.prototype.Format = function (fmt) { //author: meizz
 var o = {
     "M+": this.getMonth() + 1, //月份
     "d+": this.getDate(), //日
     "h+": this.getHours(), //小时
     "m+": this.getMinutes(), //分
     "s+": this.getSeconds(), //秒
     "q+": Math.floor((this.getMonth() + 3) / 3), //季度
     "S": this.getMilliseconds() //毫秒
 };
 if (/(y+)/.test(fmt))fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
 for( var k in o){
	if (new RegExp("(" + k + ")").test(fmt))fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]): (("00" + o[k]).substr(("" + o[k]).length)));
}
 return fmt;
};

// 单选按钮自动选中(第一个参数为DOM的id值，第二个参数为数据库取得的字段值)
function radioStatus(radioId,rStatus){
	$("#" + radioId + " label input[type='radio']").each(function(){
	    var id= $(this).data('status');
		if(rStatus == id){
			$(this).prop("checked",true);
			return false;
		}
	});
}

function selectChangeFunc(formId, fun, pagenum, pagesize){
	var selValue = parseInt($(".pageing-form-select").val());
	var newPagenum =  pagenum * pagesize % selValue == 0 ? Math.floor(pagenum * pagesize / selValue) : Math.ceil(pagenum * pagesize / selValue) ;
	ZW.Page.setSizeNew(formId , selValue, fun , newPagenum);
}


/* 二次提交 
 * id : 需要变灰的按钮ID
 * shouldDisabled : 
 */
function setDisabled(id, disabledFlag) {
	if (ZW.Object.notNull($(id))) {
		if (disabledFlag === true) {
			$(id).attr("disabled","disabled");
		} else {
			$(id).removeAttr("disabled");
		}
	}
}

