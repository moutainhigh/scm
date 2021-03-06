$(function(){
	
	$("#userName").focus(); //为用户名设置焦点
	
	//背景自适应屏幕
	if($(window).height()<=736){
	  	$(".bg-top").css("display","none");
	}else{
	  	$(".bg-top").css("height",$(window).height()-736);
	}
	if($(window).width()>1260){
	    var width=$(window).width()-1261;
		var wid=(width/2);
		$(".bg-add").css("display","block");
		$(".bg-top").css("display","block");
		$(".bg-add").css("width",wid);
	}else{
	  	$(".bg-add").css("display","none");
	}
	
	//回车事件
	document.onkeydown = function(e){ 
	    var ev = document.all ? window.event : e;
	    if(ev.keyCode==13) {
	    	login(); //登录
	    }
	}
	
	
});

//验证
function check(){
	if($("#userName").val()==""){
		Public.alert(3,"请输入用户名！");
		return false;
	}else if($("#password").val()==""){
		Public.alert(3,"请输入密码！");
		return false;
	}
	return true;
}

//登录
function login(){
	//验证
	if(!check()){
		return false;
	}
	
	$.ajax({
        cache: true,
        type: "POST",
        url:"user/login",
        data:$('#frm').serialize(),
        async: false,
        error: function(request) {
        	Public.alert(2,"登录失败，服务器出现异常！");
        },
        success: function(data) {
        	if(data.flag == true){
        		window.location.href = "html/home.html";
        	}else{
        		Public.alert(2, data.message);
        	}
        }
    });
	
}


function openInit(){
    // 打开模态窗口
    Public.openModal("initLicenseModel");
    $.ajax({
        cache: true,
        type: "GET",
        url:"license/date",
        async: false,
        error: function(request) {
            Public.alert(2,"提取license失败");
        },
        success: function(data) {
            if(data.flag == true){
                var date = data.data;
                var html = "系统授权至："+date;
                $("#licenseDate").html(html);
                $("#licenseDate").css({color:"green"})
            }else{
                var html = data.message;
                $("#licenseDate").html(html);
                $("#licenseDate").css({color:"red"})
            }
        }
    });
}

function initLicense(){
    var license = $("#license").val().replace(/(^\s*)|(\s*$)/g, "");
    if(license == ""){
        Public.alert(2,"请输入license码");
        return;
    }
    var data = '{"license":"'+license+'"}';
    $.ajax({
        cache: true,
        dataType: "json",
        type: "POST",
        data: data,
        url:"license/init",
        contentType:"application/json;charset=utf-8",
        async: false,
        error: function(request) {
            Public.alert(2,"license认证失败");
        },
        success: function(data) {
            if(data.flag == true){
                var date = data.data;
                var html = "系统授权至："+date;
                $("#licenseDate").html(html);
                $("#licenseDate").css({color:"green"})
                Public.alert(1,html);
                Public.closeModal("initLicenseModel");
            }else{
                Public.alert(2,data.message);
            }
        }
    });

}