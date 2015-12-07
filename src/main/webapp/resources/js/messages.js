$(document).ready(function(){
	$("#messageTo").keyup(function(event){
	    if(event.keyCode == 13){
	        sendMessage();
	    }
	});
	
	$.ajaxSetup({
		cache: false,
		statusCode: {
		    403: function() {
		    	appendSystem("No connection to servers");
		    },
			408: function() {
		    	appendSystem("Disconnected due to innactivity");
		    }
		}
	});
});

function append(message){
	$('.messages .active').append('<br />'+message);
	
	var objDiv = document.getElementById("messages");
	objDiv.scrollTop = objDiv.scrollHeight;
}

/*<![CDATA[**/
function appendSystem(message){
	var sysMessage = "<span class=\'message-system\'>"+message+"</span>";
	append(sysMessage);
}
/*]]>*/
function subscribe(){
	$.ajax({
		method : "POST",
		url : "subscribe",
		data: {userName:$('#userName').val()
		}
	}).done(function(result) {
		appendSystem("Susbribed - "+result);
		readMessages();
	});
}
function readMessages() {
	$.ajax({
		method : "POST",
		url : "read-messages",
		data: {
			user:$('#userName').val()
		}
	}).done(function(result) {
		if(result){
			append('<i>'+result.fromUser.userName+'</i>: '+result.message);
		}
		readMessages();
	});
};

function sendMessage(){
	$.ajax({
		method : "POST",
		url : "post-message",
		data: {
			toUser:$('#sendToUser').val(),
			message:$('#messageTo').val()
		}
	}).done(function(){
		var fromUser = $('#userName').val();
		var messageTo = $("#messageTo").val();
		
		append('<i>'+fromUser+'</i>: '+messageTo);
		$("#messageTo").val('');
	});
};

function choseUser(userButton){
	$('#sendToUser').val($(userButton).text());
	$('.user-button').removeClass('selected');
	$(userButton).addClass('selected');
}

function getConnectedUsers(){
	$.ajax({
		method : "GET",
		url : "connected-users",
	}).done(function(result){
		$('#users').html('Users:');
		for(var i in result){
			$('#users').append('<br /><button class=\"user-button\" id=\"user'+i+'\" onclick=\"choseUser(this)\">'+result[i].userName+'</button>');
			$('#users').append('<div class="messages">Messages:</div>');
		}
	});
}

function broadcast(){
	$.ajax({
		method : "POST",
		url : "broadcast-message",
		data: {
			message:$('#messageTo').val()
		}
	});
};