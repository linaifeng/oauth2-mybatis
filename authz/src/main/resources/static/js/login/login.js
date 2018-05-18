$(function() {
	// 取得验证码
	getVerifyCode();
	// 监听docuemnt的onkeydown事件看是不是按了回车键
	$(document).keydown(function(event) {
		event = event ? event : window.event;
		if (event.keyCode === 13) {
			$("#loginBtn").trigger("click");
		}
	});
	// 登录
	$("#loginBtn").click(function() {
		if ("" == $("#accountNameId").val()) {
			$("#accountNameId").tips({
				side : 3,
				msg : "用户名不能为空！",
				bg : '#FF2D2D',
				time : 2
			});
			$("#accountNameId").focus();
		} else if ("" == $("#passwordId").val()) {
			$("#passwordId").tips({
				side : 3,
				msg : "密码不能为空！",
				bg : '#FF2D2D',
				time : 2
			});
			$("#passwordId").focus();
		} else if ("" == $("#verifyCodeId").val()) {
			$("#verifyCodeId").tips({
				side : 3,
				msg : "验证码不能为空！",
				bg : '#FF2D2D',
				time : 2
			});
			$("#verifyCodeId").focus();
		} else {
			$(".add-login-main").removeAttr("style");
			var loginname = $("#accountNameId").val();
			var password = $("#passwordId").val();
			var rememberMe = $("#rememberMe").val();
			var verifyCode = $("#verifyCodeId").val();
			var company = $("#company").val();
			var code = loginname + ",&," + $.md5(password) + ",&," + rememberMe + ",&," + verifyCode + ",&," + company;
			$.ajax({
				type : 'POST',
				url : ctx + '/systemLogin',
				data : {
					KEYDATA : code,
					tm : new Date().getTime()
				},
				dataType : 'json',
				success : function(data, textStatus) {
					var result = data.result;
					if ("success" != result) { // 如果登录不成功，则再次刷新验证码
						clearLoginForm();// 清除信息
						loginAlert(result);
					} else {
						window.location.href = ctx + "/mainPage";
					}
				}
			});
		}
	});

});
// 刷新验证码
function getVerifyCode() {
	$("#vimg").attr("src", ctx + "/verifyCode/slogin?random=" + Math.random());
}
function clearLoginForm(){	
	$("#verifyCodeId").val("");
	$("#passwordId").val("");
	getVerifyCode();
}
function loginAlert(msg) {
	if("codeerror" == msg){
		$("#verifyCodeId").tips({side : 3,msg : "验证码不正确！",bg : '#FF2D2D',time : 2});
		$("#verifyCodeId").focus();
	}else if("nullup" == msg){
		$("#accountNameId").tips({side : 3,msg : "用户名或密码不能为空！",bg : '#FF2D2D',time : 2});
		$("#accountNameId").focus();
	}else if("nullcode" == msg){
		$("#verifyCodeId").tips({side : 3,msg : "验证码不能为空！",bg : '#FF2D2D',time : 2});
		$("#verifyCodeId").focus();
	}else if("usererror" == msg){
		$("#accountNameId").tips({side : 3,msg : "用户名或密码有误！",bg : '#FF2D2D',time : 2});
		$("#accountNameId").focus();
	}else if("attemptserror" == msg){
		ZW.Model.error("错误次数过多！","登录失败");
	}else if("error" == msg){
		ZW.Model.error("输入有误！","登录失败");
	}else if("inactive" == msg){
		ZW.Model.error("未激活！","登录失败");
	}
}