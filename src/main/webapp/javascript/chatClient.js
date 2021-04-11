sendDelete = null;

$(function () {
    "use strict";

    let header = $('#header');
    let content = $('#content');
    let input = $('#input');
    let status = $('#status');
    let warning = $('#warning');
    let warningMsg = $('#warning-msg');
    let username = null;
    let loggedIn = false;
    let attemptedLogin = false;
    let moderator = false;
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
                status.text('Joining Chatroom');
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

    function addTextMessage(uuid, username, message, color, datetime) {
        let hours = datetime.getHours().toString().padStart( 2, '0');
        let minutes = datetime.getMinutes().toString().padStart(2, '0');
        let timestamp = hours + ':' + minutes;
        let deleteBtn = `
            <div class="col-1">
                <button type="button" class="close" onclick="sendDelete('${uuid}')">&times;</button>
            </div> `;
        let row = `
            <div class="row" id="${uuid}">
                <div class="col">
                    <p><span style="color: ${color}">${username}</span> @ ${timestamp}: ${message}</p>
                </div>
                ${moderator ? deleteBtn : ""}
            </div>`;
        content.append(row);
    }

    function removeTextMessage(uuid) {
        $(`#${uuid}`).remove();
    }

    addMessageHandler("TextMessage", msg => {
        if (!loggedIn) return; // We need to be logged in before displaying messages

        let me = msg.author === username;
        let date = typeof(msg.time) == 'string' ? parseInt(msg.time) : msg.time;
        addTextMessage(msg.uuid, msg.author, msg.message, me ? 'blue' : 'black', new Date(date));
    });

    addMessageHandler("TextMessageResponse", msg => {
        if (msg.code !== "OK") {
            warn("Unable to send message: " + msg.info);
        }
    });

    addMessageHandler("JoinRoomResponse", msg => {
        // No matter what, clear attempted login
        attemptedLogin = false;
        input.removeAttr('disabled').focus();

        if (msg.code !== "OK") {
            // Login was not successful
            warn("Error connecting: " + username + " is already taken.");
            return;
        }

        moderator = msg.isModerator;
        let color = 'blue'
        if (moderator) {
            color = 'green';
        }
        status.text(username + ':').css('color', color);
        loggedIn = true;
    });

    sendDelete = function (uuid) {
        /**
         * Task: Add the ability for moderators to delete messages
         * Rules:
         * 1. Message should not be deleted until commanded from the server.
         * 3. The server should filter out bad requests:
         *    3a. Requests from non-moderators
         *    3b. Requests relating to invalid messages.
         * 4. If the server filters a request, a response must be sent to the commanding user.
         * 5. If a not "OK" response is received, a warning should be displayed to the user.
         * 6. A deleted message should be deleted from all connected clients.
         *
         * Client Tips:
         * 1. Use removeTextMessage(uuid) to delete a given message from the UI.
         * 2. Use addMessageHandler(MessageType, callback) to handle your new messages.
         */
        warn("Implement this!");
    }
});

function hideWarning() {
    $('#warning').attr('hidden', 'hidden');
}