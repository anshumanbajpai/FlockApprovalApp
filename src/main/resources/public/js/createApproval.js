$(document).ready(function()
{
     var amount;
     $("#submit").click(function () {
         amount = $("#amount").val();
         generateApprovalRequest(amount);
     });

     function generateApprovalRequest(amount) {
          payload = {"amount": + amount};
          sendAjaxRequest(baseUrl + "create", "post", payload, function (response) {
              console.log("successful response: " + JSON.stringify(response));
          });
     }
});


