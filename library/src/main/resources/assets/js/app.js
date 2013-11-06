  /*
  $(":button").click(function() {
  	var isbn = this.id;
  	alert('About to report lost on ISBN ' + isbn);
  });
  */
  /*

  $(":button").click(function(){
  var isbn = this.id;	
     $.ajax({
     url: 'library/v1/books/'+isbn+'/',
     type: 'PUT',
     data: "status=lost",
     contentType: "application/json",
     success: function(data) {
     alert('Load was performed.');
    }
  })
});*/

  $(":button").click(function() {
   var isbn = this.id;

   $.ajax({
     url: '/library/v1/books/' + isbn + '/?status=lost',
     dataType: "json",
     contentType: "application/json",
     type: 'PUT',
     success: function(data) {
       //alert('Reported lost on ISBN ' + isbn);

       window.location.reload();


     },
     error: function(xhr, status) {
       //alert("Sorry, there was a problem!");
     }
   });

        // document.getElementById(isbn).disabled = true;
      });
  
  $(document).ready(function() {
  //alert("Inside websocket"); 
  var url="ws://54.215.210.214:61623";
  var login = "admin";
  var password = "password";
  var destination = "/topic/10418.book.*";
  var client = Stomp.client(url);
  client.debug = function(str) {
            $("#debug").append(str + "\n");
          };
  client.connect(login,password,
    function(frame){
      /*alert("Inside connect");*/
      client.debug("connected to Stomp");
      client.subscribe(destination,
        function(message){
          //alert(message.body);
          $.ajax({
          url: '/library/v1/books/update',
          //dataType: "json",
          //contentType: "application/json",
          data: message.body,
          type: 'POST',
          contentType: 'application/json',
          success: function(data) {
          //alert('Reported new book ');

          window.location.reload();


     },
     error: function(xhr, status) {
       //alert("Sorry, there was a problem!");
     }
   });


        });
        

        });

    }); 

/*$(document).ready(function() {
var connection=new WebSocket("ws://54.215.211.164:8001/library/v1/books",'json');
connection.onopen = function () {
  //connection.send('Hello, Server!!'); //send a message to server once connection is opened.
};
connection.onerror = function (error) {
  console.log('Error Logged: ' + error); //log errors
};
connection.onmessage = function (e) {
  console.log('Received From Server: ' + e.data); //log the received message
};

});*/




























