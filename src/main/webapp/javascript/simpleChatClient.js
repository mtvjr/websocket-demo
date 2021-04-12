$(function () {
    "use strict";

    let socket = $.atmosphere; // The connection manager
    let subSocket; // Will contain details about our subscription

    // The request object details how we connect to the client
    let request = {
        url: 'http://localhost:8080/simplechat',
        contentType : "application/json",
        trackMessageLength : true,
        transport : 'websocket',
    };

    request.onOpen = function(response) {
        console.log("Connected!");
    };

    request.onTransportFailure = function(errorMsg, request) {
        console.log("Disconnected: " + errorMsg);
    };

    request.onMessage = function (response) {
        const content = response.responseBody;
        try {
            let object = jQuery.parseJSON(content);
            console.log("Got message: " + object.message);
        } catch (e) {
            console.log('Got text: ' + content);
        }
    };

    subSocket = socket.subscribe(request);
    input.keydown(function(e) {
        if (e.keyCode === 13) {
            let content = $(this).val();

            let msg = {
                message: "Hello",
                author: "The World"
            };

            subSocket.push(jQuery.stringifyJSON(msg));
            $(this).val('');
        }
    });
});