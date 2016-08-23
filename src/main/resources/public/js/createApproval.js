$(document).ready(function()
{
     var amount;
     $("#submit").click(function () {
        console.log("submit clicked")
         var queryString = window.location.search.substring(1);
         amount = $("#amount").val();
         console.log("amount: " + amount);
         event = decodeURIComponent(getQueryVariable("flockEvent"));
         console.log("event: " + event);
         var parsedEvent = JSON.parse(event);
         console.log("userId: " + parsedEvent.userId);
         console.log("chat: " + parsedEvent.chat);
         requestorId = parsedEvent.userId;
         approverId = parsedEvent.chat;
         generateApprovalRequest(amount);
         flock.close();
     });

     function generateApprovalRequest(amount) {
          payload =
          {
              "amount": amount,
              "requestorId": requestorId,
              "approverId": approverId
          };
          sendAjaxRequest(baseUrl + "create", "post", payload, function (response) {
              console.log("successful response: " + JSON.stringify(response));
          });
     }

     function getQueryVariable(variable) {
         var query = window.location.search.substring(1);
         var vars = query.split("&");
         for (var i = 0; i < vars.length; i++) {
             var pair = vars[i].split("=");
             if (pair[0] == variable) {
                 return pair[1];
             }
         }
         return null;
     }
});


