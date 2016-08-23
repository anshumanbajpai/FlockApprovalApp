function sendAjaxRequest(url, methodType, objectTobeSent, successCallback) {
    $.ajax({
        url: url,
        type: methodType,
        data: JSON.stringify(objectTobeSent),
        success: function (response) {
            if (successCallback != null) {
                successCallback(response);
            }
        },
        error: function(response, status, error) {
            console.log('XHR failure: ' + status);
            console.log(error);
        }
    });
}