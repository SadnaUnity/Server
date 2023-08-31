# Chat HTTP Endpoints

## `PUT /echo`

### Description

This endpoint allows users to add a new chat message to a chat room.

### Request

- Method: PUT
- Path: `/echo`

**Request Parameters**

- `userId` (Integer, required): The ID of the user sending the message.
- `roomId` (Integer, required): The ID of the chat room where the message should be sent.

**Request Body**

The request body should contain a JSON object representing the chat message.

- `content` (String, required): The content of the chat message.
- `sender` (String, required): The username of the message sender.
- `timestamp` (Long, required): The timestamp of the message in milliseconds.

### Response

**Success**

- Status Code: 200 OK
- Response Body: JSON object containing details about the message.

**Error**

- Status Code: 400 Bad Request
- Response Body: JSON object with an error message if the user is not a member of the room or if the request is malformed.

**example response**
```json
{
    "userId": 6,
    "message": "message sent successfully.",
    "chatMessage": {
        "content": "hi",
        "sender": "shaked",
        "timestamp": 13298756484562
    }
}
```

## `GET /chat`

### Description

This endpoint allows users to retrieve chat messages sent after a given timestamp in a chat room.

### Request

- Method: GET
- Path: `/chat`

**Request Parameters**

- `userId` (Integer, required): The ID of the user requesting the messages.
- `roomId` (Integer, required): The ID of the chat room from which to retrieve messages.
- `timestamp` (Long, required): The timestamp to filter messages. Only messages sent after this timestamp will be returned.

### Response

**Success**

- Status Code: 200 OK
- Response Body: JSON object containing a list of chat messages sent after the provided timestamp.

**Error**

- Status Code: 400 Bad Request
- Response Body: JSON object with an error message if the user is not a member of the room or if the request is malformed.

**example response**
```json
{
  "message": "messages received successfully.",
  "messageList": [
    {
      "content": "hello",
      "sender": "shaked",
      "timestamp": 11125867945
    },
    {
      "content": "Hi!",
      "sender": "amit",
      "timestamp": 1654825215564
    }
  ]
}
```
