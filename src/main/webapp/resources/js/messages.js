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

function append(message, userId){
//	console.log(message);
	if(userId){
		$('.messages#'+userId).append('<br />'+message);
	}
	else{
		$('.messages.active').append('<br />'+message);
	}
	var messagesDiv = $('.active');
	$('.messages.active').scrollTop(messagesDiv.prop("scrollHeight"));
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
		data: {userName:$('#userName').val()}
	}).done(function(result) {
		appendSystem("Susbribed - "+result);
		readMessages();
		getConnectedUsers();
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
//		console.log(result);
		//TODO bug null message printed
		if(result){
			append('<i>['+result.timeStamp+']: '+result.fromUser.userName+'</i>: '+result.message,result.fromUser.userId);
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
		var d = new Date();
		append('<i>['+d.toLocaleTimeString()+']: '+fromUser+'</i>: '+messageTo);
		$("#messageTo").val('');
	});
};

function choseUser(userButton){
	$('#sendToUser').val($(userButton).text());
	$('.user-button').removeClass('selected');
	$(userButton).addClass('selected');
	$('.messages').removeClass('active');
	$('.system-messages').removeClass('active');
	var id = $(userButton).attr('id');
	$('#'+id).addClass('active');
}

function getConnectedUsers(){
	$.ajax({
		method : "GET",
		url : "connected-users",
	}).done(function(result){
		$('#users').html('Users:');
		$('.user-messages').remove();
		$('.system-messages').addClass('active');
		for(var i in result){
			var userName = result[i].userName;
			var userId = result[i].userId;
			$('#users').append('<br /><button class=\"user-button\" id=\"'+userId+'\" onclick=\"choseUser(this)\">'+userName+'</button>');
			$('#container').prepend('<div class="messages user-messages" id=\"'+userId+'\">Messages:</div>');
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