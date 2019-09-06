var userName=false;
var userEmail=false;
var userPassword=false;
var userCheckPassword=false;
var userCode=false;
var userNotNullCheck = false;
var phoneCode = false;

var userPhone=false;
var phoneAuthCode=false;
var phonePassword=false;
var phoneCheckPassword=false;
var phoneNotNullCheck=false;

var usedsend=false;
$(function() {
	//用户名失去焦点的时候检测用户格式和查询用户是否已经存在
	// $('#user_name').focus(function(){
	// 	$("#user_name").next().remove();
	// 	$("#user_name").after("<span id='user_name_error' style='color:#aaa;margin-left: 15px;'>以字母开头,长度为4-30（可用字母、数字、下划线）</span>");
	// });
	// $('#user_email').focus(function(){
	// 	$("#user_email").next().remove();
	// 	$("#user_email").after("<span id='user_email_error' style='color:#aaa;margin-left: 15px;'>请输入邮箱地址</span>");
	// });
	// $('#user_password').focus(function(){
	// 	$("#user_password").next().remove();
	// 	$("#user_password").after("<span id='user_password_error' style='color:#aaa;margin-left: 15px;'>密码长度为6-20</span>");
	// });
	// $('#user_check_password').focus(function(){
	// 	$("#user_check_password").next().remove();
	// 	$("#user_check_password").after("<span id='user_check_password_error' style='color:#aaa;margin-left: 15px;'>请再次输入密码</span>");
	// });

	$('#phone_password').focus(function(){
		$("#phone_password").next().remove();
		$("#phone_password").after("<span id='phone_password_error' style='color:#aaa;margin-left: 15px;'>密码长度为6-20</span>");
	});
	$('#phone_check_password').focus(function(){
		$("#phone_check_password").next().remove();
		$("#phone_check_password").after("<span id='phone_check_password_error' style='color:#aaa;margin-left: 15px;'>请再次输入密码</span>");
	});




	$('#user_name').blur(function(){
		$("#user_name_error").remove();
		var user_name_length = getBytesCount($("#user_name").val());
		if(user_name_length > 30 || user_name_length < 4){
			showMessage("user_name","warn","用户名长度不符合");
			userName = false;
		}else if(/^[a-zA-Z][a-zA-Z0-9_]{1,39}$/.test($("#user_name").val())){
			var url = "/new/registers/checkAccountNo.reg";
			var param = {accountNo: $("#user_name").val()};
			$.getJSON(url, param, function(data){
				if(data[0].isExists == 1){
					showMessage("user_name","warn","用户已经存在");
					userName = false;
				} else {
					showMessage("user_name","right","用户名可用");
					userName = true;
				}
			});
		} else {
			showMessage("user_name","warn","用户名格式不正确");
		}
	});

	//邮箱检测，查看是否已经使用
	$('#user_email').blur(function(){
		if( /^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$/g.test($("#user_email").val())){
			var url = "/new/registers/checkEmailAndPhone.reg";
			var param = {account: $("#user_email").val()};
			$.getJSON(url, param, function(data){
				if(data[0].isExists == 1){
					showMessage("user_email","warn","邮箱已经被使用");
					userEmail = false;
				} else {
					userEmail = true;
					showMessage("user_email","right","邮箱可用");
				}
			});

		} else {
			showMessage("user_email","warn","邮箱格式不正确");
			userEmail = false;
		}
	});

	$('#user_password').blur(function(){
		var user_pass_length = getBytesCount($("#user_password").val());
		var reg=/[\u4E00-\u9FA5]/i;
		if(reg.test($("#user_password").val())){
			showMessage("user_password","warn","密码不能输入中文");
			userPassword = false;
		}else if(user_pass_length < 6 || user_pass_length > 20){
			showMessage("user_password","warn","密码长度不符合");
			userPassword = false;
		}else {
			showMessage("user_password","right","输入正确");
			userPassword = true;
		}
	});

	$('#user_check_password').blur(function(){
		if($("#user_check_password").val() == "" ){
			showMessage("user_check_password","warn","确认密码为空");
			userCheckPassword = false;
		}else if( $("#user_password").val() != $("#user_check_password").val()){
			showMessage("user_check_password","warn","2个密码不匹配");
			userCheckPassword = false;
		} else {
			showMessage("user_check_password","right","输入正确");
			userCheckPassword = true;
		}
	});

	$('#user_code').blur(function(){
		var url = "/new/registers/checkAuthCode.reg";
		var param = {codeKey : codeKey , code : $("#user_code").val()};
		$.getJSON(url, param, function(data){
			if(data[0].isMatch == 1){
				showCodeMessage("user_code","right","验证码匹配");
				userCode = true;
			} else {
				showCodeMessage("user_code","warn","输入验证码不一致");
				userCode = false;
			}
		});
	});

	//手机注册页面的验证码验证
	$('#phone_code').blur(function(){
		if($("#phone_code").val() == ""){
			showMessage("reg_phone_img","warn","请输入验证码");
			return;
		}
		var url = "/new/registers/checkAuthCode.reg";
		var param = {codeKey : codeKey , code : $("#phone_code").val()};
		$.getJSON(url, param, function(data){
			if(data[0].isMatch == 1){
				showMessage("reg_phone_img","right","验证码匹配");
				phoneCode = true;
			} else {
				showMessage("reg_phone_img","warn","输入验证码不一致");
				phoneCode = false;
			}
		});
	});

	$('#user_phone').focus(function(){
		usedsend=false;
	});

	$('#user_phone').blur(function(){
		if(/^1\d{10}$/.test($("#user_phone").val())){
			var url = "/new/registers/checkEmailAndPhone.reg";
			var param = {account: $("#user_phone").val()};
			$.getJSON(url, param, function(data){
				if(data[0].isExists == 1){
					showMessage("user_phone","warn","手机号已经存在");
					userPhone = false;
					$("#get_confirm_num").attr("href","javascript:void(0);");
					usedsend=false;
				} else {
					showMessage("user_phone","right","手机号可用");
					userPhone = true;
					usedsend=true;
					$("#get_confirm_num").attr("href","javascript:sendAuthCode();");
				}
			});
		} else {
			userPhone = false;
			showMessage("user_phone","warn","手机格式不正确");
			$("#get_confirm_num").attr("href","javascript:void(0);");
			usedsend=false;
		}
	});

	$("#phone_authcode").blur(function(){
		var url = "/new/registers/checkAuthCode.reg";
		var param = {codeKey : $("#user_phone").val() , code : $("#phone_authcode").val()};
		$.getJSON(url, param, function(data){
			if(data[0].isMatch == 1){
				phoneAuthCode = true;
				showCodeMessage("phone_authcode","right","手机验证码匹配");
			} else {
				phoneAuthCode = false;
				showCodeMessage("phone_authcode","warn","输入手机验证码不一致");
			}
		});
	});

	$("#phone_password").blur(function(){
		var user_pass_length = getBytesCount($("#phone_password").val());
		var reg=/[\u4E00-\u9FA5]/i;
		if(reg.test($("#phone_password").val())){
			showMessage("phone_password","warn","密码不能输入中文");
			phone_password = false;
		}else if(user_pass_length < 6 || user_pass_length > 20){
			phonePassword = false;
			showMessage("phone_password","warn","密码长度不符合");
		}else {
			phonePassword = true;
			showMessage("phone_password","right","输入正确");
		}
	});

	$("#phone_check_password").blur(function(){
		if($("#phone_check_password").val() == "" ){
			showMessage("phone_check_password","warn","确认密码为空");
			userCheckPassword = false;
		}else if( $("#phone_password").val() != $("#phone_check_password").val()){
			phoneCheckPassword = false;
			showMessage("phone_check_password","warn","2个密码不匹配");
		} else {
			phoneCheckPassword = true;
			showMessage("phone_check_password","right","输入正确");
		}
	});
	//个人协议选择
	$("#userRegisterAccept").click(function(){
		if($("#userRegisterAccept").attr("checked")){
			$("#userRegisterAccept").removeAttr("checked");
		}else{
			$("#userRegisterAccept").attr("checked","checked");
		}
	});
	$("#userPhoneAccept").click(function(){
		if($("#userPhoneAccept").attr("checked")){
			$("#userPhoneAccept").removeAttr("checked");
		}else{
			$("#userPhoneAccept").attr("checked","checked");
		}
	});

});

/**
 * 通用显示提示信息 先找出同级后同胞节点，然后删除，在重新添加
 * @param key id的名字
 */
function showMessage(key,className,desc){
	$("#"+key).next().remove();
	$("#"+key).after("<span class="+className+"><img src=\"/img/account/"+className+".png\">"+desc+"</span>");
}

// ../../images/

function removeWarnMessage(className){
	$("."+className).remove();
}

function showCodeMessage(key,className,desc){
	$("#"+key).next().next().remove();
	$("#"+key).next().after("<span class="+className+"><img src=\"/img/"+className+".png\">"+desc+"</span>");
}


// 获取字节数
function getBytesCount(str){
	if(str == null){
		return 0;
	} else {
		return (str.length + str.replace(/[\u0000-\u00ff]/g, "").length);
	}
}

function userRegisterNotCheck(){
	//用户名非空检查
	if($.trim($('#user_name').val()).length == 0){
		showMessage("user_name","warn","用户名不能为空");
		return;
	}
	//邮箱非空检查
	if($.trim($('#user_email').val()).length == 0){
		showMessage("user_email","warn","邮箱不能为空");
		return;
	}
	//密码非空检察
	if(getBytesCount($("#user_password").val()) == 0){
		showMessage("user_password","warn","不能为空");
		return;
	}
	//缺人密码非空检察
	if(getBytesCount($("#user_check_password").val()) == 0){
		showMessage("user_check_password","warn","不能为空");
		return;
	}
	//验证码非空检察
	if($.trim($('#user_code').val()).length == 0){
		showCodeMessage("user_code","warn","验证码为空");
		return;
	}
	if($("#userRegisterAccept").attr("checked")=="checked"){
	}else{
		window.alert("请选择个人会员服务协议");
		return;
	}
	userNotNullCheck = true;
}

function phoneRegisterNotCheck(){
	//用户名非空检查
	if($.trim($('#user_phone').val()).length == 0){
		showMessage("user_phone","warn","手机号不能为空");
		return;
	}
	//验证码非空检察
	if($.trim($('#phone_authcode').val()).length == 0){
		showCodeMessage("phone_authcode","warn","验证码为空");
		return;
	}
	//密码非空检察
	if(getBytesCount($("#phone_password").val()) == 0){
		showMessage("phone_password","warn","不能为空");
		return;
	}
	//缺人密码非空检察
	if(getBytesCount($("#phone_check_password").val()) == 0){
		showMessage("phone_check_password","warn","不能为空");
		return;
	}
	if($("#userPhoneAccept").attr("checked")=="checked"){
	}else{
		window.alert("请选择个人会员服务协议");
		return;
	}
	phoneNotNullCheck = true;
}

//用户名提交注册
function register(codeKey,fromsystem,redirectUrl){
	var href="javascript: register('"+codeKey+"','"+fromsystem+"','"+redirectUrl+"');";
	var user_name_length = getBytesCount($("#user_name").val());
	if(user_name_length > 30 || user_name_length < 4){
		showMessage("user_name","warn","用户名长度不符合");
		return;
	}else if(/^[a-zA-Z][a-zA-Z0-9-.@_]{1,39}$/.test($("#user_name").val())){
		var url = "/new/registers/checkAccountNo.reg";
		var param = {accountNo: $("#user_name").val()};
		$.getJSON(url, param, function(data){
			if(data[0].isExists == 1){
				showMessage("user_name","warn","用户已经存在");
				return;
			}
		});
	} else {
		showMessage("user_name","warn","用户名格式不正确");
		return;
	}
	var url = "/common/new/registers/user_register";
	param = {user_name: $("#user_name").val(),
		user_email: $("#user_email").val(),
		user_password: $("#user_password").val(),
		user_code: $("#user_code").val(),
		codeKey: codeKey,
		from_system: fromsystem};
	userRegisterNotCheck();

	if(userNotNullCheck && userName && userEmail && userPassword && userCheckPassword && userCode){
		$("#userRegister").attr("href","javascript:void(0);");
		$.post(url, param,function(data, textStatus){
			var regResult = data[0].register_status;
			if(regResult == "reg_success"){
				$("#userRegister").attr("href",href);
				window.location = "/new/registers/"+regName+"/success.reg?userName=" + data[0].userName+ "&email=" + data[0].email;
			}else if(regResult == "code_error"){
				$("#userRegister").attr("href",href);
				showCodeMessage("user_code","warn","输入验证码不一致");
			}else if(regResult == "reg_error"){
				$("#userRegister").attr("href",href);
				alert("错误");
			}
		},"json");
	}
}

//发送验证码
function sendAuthCode(){
	if($("#user_phone").val() == ""){
		showMessage("user_phone","warn","请输入手机号");
		return;
	}

	if($("#phone_code").val() == ""){
		showMessage("reg_phone_img","warn","请输入验证码");
		return;
	}

	if(!phoneCode){
		if($("#phone_code").val() == ""){
			showMessage("reg_phone_img","warn","请输入验证码");
			phoneCode = false;
			return;
		}
		var url = "/new/registers/checkAuthCode.reg";
		var param = {codeKey : codeKey , code : $("#phone_code").val()};
		$.getJSON(url, param, function(data){
			if(data[0].isMatch == 1){
				showMessage("reg_phone_img","right","验证码匹配");
				phoneCode = true;
			} else {
				showMessage("reg_phone_img","warn","输入验证码不一致");
				phoneCode = false;
				return;
			}
		});
	}
	if(!phoneCode){
		return;
	}

	if(/^1\d{10}$/.test($("#user_phone").val())){
	} else {
		userPhone = false;
		showMessage("user_phone","warn","手机格式不正确");
		return;
	}

	if(usedsend){
		removeWarnMessage("warn");

		var url = "/common/new/registers/sendAuthCode";
		param ={ user_phone: $("#user_phone").val() };
		$.post(url, param,function(data, textStatus){
			setTime();
		},"json");
	}

}

//倒数计时
var i = 30;
var intervalid;

function setTime(){
	i=60;
	intervalid = setInterval("fun()", 1000);
}

function fun() {
	if (i == 0) {
		document.getElementById("get_confirm_num").innerHTML = "免费获取";
		$("#get_confirm_num").attr("href","javascript:sendAuthCode();");
		$("#get_confirm_num").css("cursor", "pointer");
		$("#get_confirm_num").css("background", "#2BB118");
		clearInterval(intervalid);
	}else{

		document.getElementById("get_confirm_num").innerHTML = "重新获取(" + i + ")";
		$("#get_confirm_num").css("cursor", "default");
		$("#get_confirm_num").css("background", "#8F968E");
		$("#get_confirm_num").removeAttr("href");
		i--;
	}
}

//手机提交注册
function registerphone(fromsystem,redirectUrl){
	var href="javascript:registerphone('"+fromsystem+"','"+redirectUrl+"');";
	if(/^1\d{10}$/.test($("#user_phone").val())){
		var url = "/new/registers/checkEmailAndPhone.reg";
		var param = {account: $("#user_phone").val()};
		$.getJSON(url, param, function(data){
			if(data[0].isExists == 1){
				showMessage("user_phone","warn","手机号已经存在");
				return;
			} else {
				showMessage("user_phone","right","用户名可用");
				return;
			}
		});
	} else {
		showMessage("user_phone","warn","手机格式不正确");
		return;
	}
	phoneRegisterNotCheck();
	var url = "/common/new/registers/phone_register";
	if(phoneNotNullCheck && userPhone && phoneAuthCode && phonePassword && phoneCheckPassword){
		$("#phoneRegister").attr("href","javascript:void(0);");
		param = {user_phone: $("#user_phone").val(),
			phone_authcode: $("#phone_authcode").val(),
			phone_password: $("#phone_password").val(),
			from_system: fromsystem};
		$.post(url, param,function(data, textStatus){
			var regResult = data[0].register_status;
			if(regResult == "reg_success"){
				$("#phoneRegister").attr("href",href);
				window.location = "/new/registers/"+regName+"/phone_success.reg?phone="+$("#user_phone").val();
			}else if(regResult == "code_error"){
				showCodeMessage("phone_authcode","warn","输入验证码错误");
				$("#phoneRegister").attr("href",href);
			} else if(regResult == "reg_error"){
				$("#phoneRegister").attr("href",href);
				alert("错误");
			}
		},"json");
	}
}

// 重置CODE
function reloadImage(){
	var imgUrl = "/validateImage.jsp?checkKey="+codeKey+"&date=" + new Date().getTime();
	$("#reg_code_img").attr("src", imgUrl);
	$("#reg_phone_img").attr("src", imgUrl);
}