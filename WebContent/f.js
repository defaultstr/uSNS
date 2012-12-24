
var xmlHttpRequest;
//XmlHttpRequest对象  
function createXmlHttpRequest()
{  
	if(window.ActiveXObject)	//如果是IE浏览器  
		return new ActiveXObject("Microsoft.XMLHTTP");  
	else if(window.XMLHttpRequest)	//非IE浏览器  
		return new XMLHttpRequest();  
}

function onload() {
	var user = location.search.slice(1);
	if (user) {
		document.getElementById("txt_username").value = user;
		document.getElementById("txt_password").value = "";
		document.getElementById("btn_connect_weibo").disabled = false;
		document.getElementById("btn_connect_renren").disabled = false;
		document.getElementById("btn_login").disabled = false;
		document.getElementById("btn_refresh").disabled = false;
		document.getElementById("btn_prevPage").disabled = false;
		document.getElementById("btn_nextPage").disabled = false;
		document.getElementById("btn_refresh").disabled = false;
		refreshData();
	}
}

function handleIpResponseLogin()
{  
//	alert("readyState:" + xmlHttpRequest.readyState + "  status:" + xmlHttpRequest.status);
	if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200)
	{
		var result = xmlHttpRequest.responseText;
		var json = eval('('+result+')');
		if ( json.result == 0 )
		{
			alert("登录成功");
			var btn = document.getElementById("btn_connect_weibo");
			btn.disabled = false;
			btn = document.getElementById("btn_connect_renren");
			btn.disabled = false;
		}
		else
			alert("登录失败");
	}  
}

function login()
{
	var username = document.getElementById("txt_username").value;
	var password = document.getElementById("txt_password").value;
	var url = "http://127.0.0.1/uSNS/UserLogin?username=" + username + "&password=" + password;
	//1.创建XMLHttpRequest组建  
	xmlHttpRequest = createXmlHttpRequest();  
	//2.设置回调函数  
	xmlHttpRequest.onreadystatechange = handleIpResponseLogin; 
	//3.初始化XMLHttpRequest组建  
	xmlHttpRequest.open("GET", url, true);  
	//4.发送请求  
	xmlHttpRequest.send(null);   
}

function handleIpResponseTag()
{
	if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200)
	{
		var result = xmlHttpRequest.responseText;
		var json = eval('('+result+')');
		
		if ( json.result == 0 )
		{
			alert("标注成功");
			refreshData();
		}
	} 	
}

function like()
{
	var url = "http://127.0.0.1/uSNS/Tag?_id=" + this.id.slice(9) + "&tag=good";
	//1.创建XMLHttpRequest组建  
	xmlHttpRequest = createXmlHttpRequest();
	//2.设置回调函数  
	xmlHttpRequest.onreadystatechange = handleIpResponseTag; 
	//3.初始化XMLHttpRequest组建  
	xmlHttpRequest.open("GET", url, true);  
	//4.发送请求  
	xmlHttpRequest.send(null);   
}

function spam()
{
	var url = "http://127.0.0.1/uSNS/Tag?_id=" + this.id.slice(9) + "&tag=spam";
	//1.创建XMLHttpRequest组建  
	xmlHttpRequest = createXmlHttpRequest();
	//2.设置回调函数  
	xmlHttpRequest.onreadystatechange = handleIpResponseTag; 
	//3.初始化XMLHttpRequest组建  
	xmlHttpRequest.open("GET", url, true);  
	//4.发送请求  
	xmlHttpRequest.send(null);   
}

function addrow(rowText,id)
{	
	var mytable = document.getElementById("tb_main");
	var mytr=mytable.insertRow(-1);
	var mytd=document.createElement("td");
	mytd.id="mytd-"+id;
	mytd.innerHTML=rowText;
	mytr.appendChild(mytd);
	/*
	//insert button row
	var btnrow = mytable.insertRow(-1);
	var btntd = document.createElement("td");
	
	var btn_like = document.createElement("input");
	btn_like.setAttribute("type","button");
	btn_like.onclick = like;
	btn_like.id = "btn_like-" + id;
	btn_like.value = "喜欢哦亲~";
	btntd.appendChild(btn_like);

	var btn_spam = document.createElement("input");
	btn_spam.setAttribute("type","button");
	btn_spam.onclick = spam;
	btn_spam.id = "btn_spam-" + id;
	btn_spam.value = "渣渣哦亲~";
	btntd.appendChild(btn_spam);
	
	btnrow.appendChild(btntd);
	*/
	var btn_like = document.getElementById("btn_like-"+id);
	btn_like.onclick = like;
	btn_like.id = "btn_like-" + id;
	btn_like.value = "喜欢哦亲~";

	var btn_spam = document.getElementById("btn_spam-"+id);
	btn_spam.onclick = spam;
	btn_spam.id = "btn_spam-" + id;
	btn_spam.value = "渣渣哦亲~";
	
	mytr.appendChild(mytd);
}

var startAt = 0;
var limit = 20;

function refreshData()
{
	var url = "http://127.0.0.1/uSNS/TweetsList?startAt="+startAt+"&limit="+limit;
	//1.创建XMLHttpRequest组建  
	xmlHttpRequest = createXmlHttpRequest();  
	//2.设置回调函数  
	xmlHttpRequest.onreadystatechange = handleIpResponseShow; 
	//3.初始化XMLHttpRequest组建  
	xmlHttpRequest.open("GET", url, true);  
	//4.发送请求  
	xmlHttpRequest.send(null);  
}

function handleIpResponseShow()
{
	if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200)
	{
		var mytable = document.getElementById("tb_main");
		while( mytable.rows.length > 0 )
			mytable.deleteRow(-1);

		var result = xmlHttpRequest.responseText;
		var json = eval('('+result+')');
		
//		alert(json.list.length);
		for ( var i = 0; i < json.list.length; i++ )
		{
			var user = json.list[i].user;
			var text = json.list[i].text;
			var time = (new Date(json.list[i].time)).toLocaleString();
			
		//	addrow("<div class='class-user'>"+user+"</div><div class='class-text'>"+text+
		//		"</div><div class='class-time'>"+time+"</div>",json.list[i]._id);
				
			addrow("<div class='class-user'>"+user+
				"</div><div class='class-text'>"+text+
				"</div><div class='class-time'>"+time+
				"</div><div class='class-btn'><input type='button' id='btn_like-"+json.list[i]._id+
				"'><input type='button' id='btn_spam-"+json.list[i]._id+"'></div>",json.list[i]._id);	
		}
	}  
}

function pageUp()
{
	startAt -= limit;
	if ( startAt < 0 )
		startAt = 0;
	refreshData();
}

function pageDown()
{
	startAt += limit;
	//TODO  超过就去死吧= =
	refreshData();
}

function connectWeiBo()
{
	document.getElementById("btn_login").disabled = false;
	document.getElementById("btn_connect_weibo").disabled = true;
	document.getElementById("btn_prevPage").disabled = false;
	document.getElementById("btn_nextPage").disabled = false;
	document.getElementById("btn_refresh").disabled = false;
	window.location = "http://127.0.0.1/uSNS/connectToWeibo";
}

function connectRenren()
{
	document.getElementById("btn_login").disabled = false;
	document.getElementById("btn_connect_renren").disabled = true;
	document.getElementById("btn_prevPage").disabled = false;
	document.getElementById("btn_nextPage").disabled = false;
	document.getElementById("btn_refresh").disabled = false;
	window.location = "http://127.0.0.1/uSNS/connectToRenren";
}

