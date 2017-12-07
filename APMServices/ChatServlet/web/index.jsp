<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Chat</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  </head>
  <body>
    <div id="content"></div>
    <label>Username: </label>
    <input type="text" id="username" onkeydown="getInput()" />
    <label>Message: </label>
    <input type="text" id="msg" onkeydown="getInput()" />

    <script type="text/javascript">
      function addData(data) {
        $("#content").empty();
        var chatContent = "";
        for (var i = 0; i < data.length; i++) {
          chatContent += "<p>" + data[i][0] + ": " + data[i][1] + "</p>"
        }
        $("#content").append(chatContent);
      }

      function sendMsg(username, msg) {
        var url =  "/chat/chatapi?username="+ username + "&messages=" + msg
        $.post(url, function(data) {
          addData(data);
        });
      }

      function recieveAndAddMsgs() {
        $.get("/chat/chatapi", function(data) {
          addData(data);
        })
      }

      function getInput() {
        if(event.key === 'Enter') {
          var uname = $("#username")[0].value
          var msg = $("#msg")[0].value
          if(!(uname && msg)) {
            alert("Please fill both input labels");
            return;
          } else {
            sendMsg(uname, msg);
          }
        }
      }

      $(document).ready(function() {
        setInterval(function() {
            recieveAndAddMsgs();
        }, 5000);
      });
    </script>
  </body>
</html>
