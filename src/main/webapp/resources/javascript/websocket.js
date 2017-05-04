/**
 * 
 */

var webSocket;

$(document).ready(function(){
	
	webSocket = new WebSocket("ws://localhost:8080/imageStream");
	
	webSocket.onmessage = function(event){
		$("#receive").prepend("<p>"+event.data+"</p>");
	};
});