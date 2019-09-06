var count = 0;
var cookies=["ruanko.com","kocla.com"];
var loginUrl = ["login.ruanko.com","login.kocla.com"];

function redirectUrl(url){
	count++;
	if(count >= cookies.length-1){
		window.location = url;
	}
}

function syncCookie(url){
	for(var i = 0;i<cookies.length;i++){

		if(document.domain != loginUrl[i]){
			// 写软酷域的cookie
			var iframeRuanko = document.createElement("iframe");
			iframeRuanko.src = "http://"+loginUrl[i]+"/writeCookie.jsp?domain="+cookies[i]+"&key=RUANKO_TOKEN&value=" + encodeURIComponent(getCookie('RUANKO_TOKEN'));
			iframeRuanko.style.display = "none";
			if (iframeRuanko.attachEvent){    
			    iframeRuanko.attachEvent("onload", function(){   
			        redirectUrl(url);
			    });
			} else {    
			    iframeRuanko.onload = function(){        
			        redirectUrl(url);  
			    };
			}
			document.body.appendChild(iframeRuanko);
		}
	}	
}



function getCookie(objName){//获取指定名称的cookie的值
	var arrStr = document.cookie.split("; ");
	for(var i = 0;i < arrStr.length;i ++){
		var temp = arrStr[i].split("=");
		if(temp[0] == objName) return unescape(temp[1]);
	}
}