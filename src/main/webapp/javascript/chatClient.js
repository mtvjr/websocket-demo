$(function () {
    "use strict";

    let header = $('#header');
    let content = $('#content');
    let input = $('#input');
    let status = $('#status');
    let warning = $('#warning')
    let warningMsg = $('#warning-msg')
    let username = null;
    let loggedIn = false;
    let attemptedLogin = false;
    let socket = $.atmosphere;
    let subSocket;
    let transport = 'websocket';
    let handlers = [];
    let senderUUID = UUID.generate();

    function handleMessage(message) {
        let handled = false;
        handlers.forEach(handler => {
            if (handler.messageType === message.messageType) {
                handled = true;
                handler.callback(message);
            }
        })
        if (!handled) {
            console.log("Unable to find handler for " + message.messageType);
        }
    }

    function addMessageHandler(messageType, callback) {
        handlers.push({
            messageType: messageType,
            callback: callback
        });
    }

    /**
     * Creates a new message object
     * MessageBase attributes are filled automatically
     * @returns MessageBase object
     * @param messageAttributes Custom attributes for your message
     */
    function createMessage(messageAttributes) {
        // Fill in the message base attributes automatically
        return {
            uuid: UUID.generate(),
            sender: senderUUID,
            time: Date.now(),
            ...messageAttributes
        }
    }

    /**
     * Display a warning to the user
     * @param warningMessage - A warning to display
     */
    function warn(warningMessage) {
        console.log(warningMessage);
        warningMsg.text(warningMessage);
        warning.removeAttr('hidden');
    }

    // We are now ready to cut the request
    let request = { url: document.location.toString() + 'chat',
        contentType : "application/json",
        trackMessageLength : true,
        shared : true,
        pollingInterval: 1000,
        transport : transport ,
        uuid: senderUUID,
        fallbackTransport: 'long-polling'};

    request.onOpen = function(response) {
        content.html($('>p<', { text: 'Atmosphere connected using ' + response.transport }));
        input.removeAttr('disabled').focus();
        status.text('Choose name:');
        transport = response.transport;

        if (response.transport === "local") {
            subSocket.pushLocal("Name?");
        }
    };

    request.onTransportFailure = function(errorMsg, request) {
        jQuery.atmosphere.info(errorMsg);
        if (window.EventSource) {
            request.fallbackTransport = "sse";
            transport = "see";
        }
        header.html($('<h3>', { text: 'Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport }));
    };

    request.onMessage = function (response) {
        const json = response.responseBody;
        let message = null
        try {
            message = jQuery.parseJSON(json);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', json.data);
            return;
        }

        handleMessage(message);
    };

    request.onReopen = function(response) {
        context.html($('<p>', {text: 'Atmosphere reconnected using transport ' + response.transport}));
        if (attemptedLogin && !loggedIn) {
            input.removeAttr('disabled').focus();
        }
    }

    subSocket = socket.subscribe(request);
    input.keydown(function(e) {
        if (e.keyCode === 13) {
            let content = $(this).val();

            let msg;
            if (!loggedIn) {
                // Attempt to login
                username = content;
                msg = createMessage({
                    username: username,
                });
                attemptedLogin = true;
                // Prevent sending additional messages until we are logged in.
                input.attr('disabled', 'disabled');
            } else {
                // We are logged in, send a real message
                msg = createMessage({
                    message: content,
                    author: username
                });
            }
            subSocket.push(jQuery.stringifyJSON(msg));
            $(this).val('');
        }
    });

    function addMessage(uuid, username, message, color, datetime) {
        content.append(`<p id="${uuid}"><span style="color: ${color}">${username}</span> @ 
            ${(datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours())}:
            ${(datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())}
            : ${message}</p>`);
    }

    addMessageHandler("TextMessage", msg => {
        if (!loggedIn) return; // We need to be logged in before displaying messages

        let me = msg.author === username;
        let date = typeof(msg.time) == 'string' ? parseInt(msg.time) : msg.time;
        addMessage(msg.uuid, msg.author, msg.message, me ? 'blue' : 'black', new Date(date));
    });

    addMessageHandler("TextMessageResponse", msg => {
        if (msg.code !== "OK") {
            warn("Unable to send message: " + msg.info);
        }
    });

    addMessageHandler("JoinRoomRequestResponse", msg => {
        // No matter what, clear attempted login
        attemptedLogin = false;
        input.removeAttr('disabled').focus();

        if (msg.code !== "OK") {
            // Login was not successful
            warn("Error connecting: " + username + " is already taken.");
            return;
        }

        status.text(username + ': ').css('color', 'blue');
        loggedIn = true;
    });
});

function hideWarning() {
    $('#warning').attr('hidden', 'hidden');
}