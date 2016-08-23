$(document).ready(function()
{
     var amount;
     $("#submit").click(function () {
         amount = $("#amount").val();
         requestorId = "u:123";
         approverId = "u:456";
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


