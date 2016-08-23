$(document).ready(function()
{
     var amount;
     $("#submit").click(function () {
         amount = $("#amount").val();
         requestorId = "u:007effelfl6l6ajf";
         approverId = "u:3y373y1tty31368w";
         generateApprovalRequest(amount);
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
});


