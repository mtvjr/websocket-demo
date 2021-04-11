# Websocket Demo

This is a chat application showcasing how to use websockets.

It is built with the [Atmosphere](https://github.com/Atmosphere/atmosphere) Java/Javascript library
and runs on [jetty](http://www.eclipse.org/jetty/).

## How to run the demo

The demo is built an run through maven, using the following command:

> ./mvnw package jetty:run

or, if you are on windows:

> ./mvnw.cmd package jetty:run

This will build, test, and launch the demo on your local machine, opening up a website at [localhost:8080](http://localhost:8080).

## How to use the demo

When you open the website, you will be presented a text input. Use it, then press `enter` to join the chat room.
If you use the username 'Moderator', you will log in with moderator permissions.

After you are logged in, you will see your username to the left of the input bar. From then on,
the input can be used to send messages.

## Expansion Task

Moderators need to have the ability to delete messages from the chatroom. When a user is logged in
as a moderator, `x` buttons appear next to messages.

Update the application so that when the button is clicked,
it uses the websocket to delete the message.

### Rules:
1. Message should not be deleted until commanded from the server.
3. The server should filter out bad requests:
  - Requests from non-moderators
  - Requests relating to invalid messages.
4. If the server filters a request, a response must be sent to the commanding user.
5. If a not "OK" response is received, a warning should be displayed to the user.
6. A deleted message should be deleted from all connected clients.

### Client Tips:
1. Use removeTextMessage(uuid) to delete a given message from the UI.
2. Use addMessageHandler(MessageType, callback) to handle your new messages.
3. When clicked, the `x` buttons will call `sendDelete(uuid)` in chatClient.js
