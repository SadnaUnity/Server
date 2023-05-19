## Room

This endpoint creates new room.

**URL:** `/room`

**Method:** `POST`

**Request Body:**

| Field       | Type | Required | Description                        |
| ----------- | ---- | -------- | ---------------------------------- |
| managerId     | int  | Yes      | The ID of the admin.        |
| roomName       | string | Yes      | The name of the room.   |
| maxCapacity       | int | No      | Max users in the room in the same time.  Default: 10 |
| privacy       | boolean | No      | Indicates whether the room is private (TRUE) or public (FALSE). Default: False    |

**Example Request:**  

POST /room?roomName=maiRoom
body:
```json
{
  "maxCapacity":50,
  "privacy" : true,
  "managerId":1
}
```
**Example Response:**  
```json
{
  "message": "Room: 'maiRoom' created successfully",
  "roomId": 11,
  "room": {
    "privacy": false,
    "maxCapacity": 50,
    "managerId": 1,
    "roomId": 11,
    "roomName": "mai's_room"
  }
}
```
---

## `/getIntoRoom`
This endpoint is used to move a user into a specific room.
**URL:** `/getIntoRoom`

**Method:** `POST`

**Request Parameters:**

| Field       | Type | Required | Description                              |
| ----------- |------| -------- |------------------------------------------|
| roomId     | int  | Yes      | An integer parameter representing the ID of the room the user wants to join |
| userId       | int  | Yes      |An integer parameter representing the ID of the user.                |

**Example Request:**

POST /getIntoRoom?roomId=1&userId=1

**Example Response(HTTP 200 OK):**

 ```json
{
  "posterName": "a",
  "fileUrl": "https://storage.googleapis.com/download/storage/v1/b/posters-sadna/o/05b9e369-3401-4c79-be5c-e16d6d4c211c?generation=1683625290680396&alt=media",
  "roomId": 1,
  "userId": 1,
  "posterId": 7,
  "position": {
    "x": 0.0,
    "y": 0.0
  }
}
  ```

---

## `/getOutFromRoom`

This endpoint is used to move a user out of the current room.

**URL:** `/getOutFromRoom`

**Method:** `POST`

**Request Parameters:**

| Field       | Type | Required | Description                              |
| ----------- |------| -------- |------------------------------------------|
| userId       | int  | Yes      |An integer parameter representing the ID of the user.                |

**Example Request:**

POST /getOutFromRoom?userId=1

**Example Response(HTTP 200 OK):**
 ```json
  {
  "message": "Message data",
  "roomStatuses": [
    {
      "privacy": false,
      "managerId": 1,
      "roomId": 2,
      "roomName": "shaked",
      "description": null,
      "roomMemberStatus": "NOT_A_MEMBER",
      "requestStatus": null
    },
    {
      "privacy": false,
      "managerId": 1,
      "roomId": 3,
      "roomName": "inbal",
      "description": null,
      "roomMemberStatus": "NOT_A_MEMBER",
      "requestStatus": "PENDING"
    }
  ]
}
  ```
---

## `Get A Room`
This endpoint retrieves information about a specific room based on its ID.

**URL:** `/room/{roomId}`

**Method:** `GET`

**Request Parameters:**

| Field  | Type | Required | Description                                         |
|--------|------| -------- |-----------------------------------------------------|
| roomId | int  | Yes      | The ID of the room to retrieve                      |
| userId | int  | Yes      | The ID of the user that wants to retrieve room data |


**Example Request:**

GET /room/123

**Example Response(HTTP 200 OK):**
 ```json
{
  "message": "Completed successfully",
  "roomId": 123,
  "room": {
    "privacy": true,
    "managerId": 456,
    "maxCapacity": 10,
    "roomId": 123,
    "roomName": "Example Room",
    "posters": [
       {
            "posterName": "firstPoster,firstPoster",
            "fileUrl": "https://storage.googleapis.com/download/storage/v1/b/posters-sadna/o/9646e461-2447-4022-9614-d5823b1d65b5?generation=1683624436112203&alt=media",
            "roomId": 1,
            "userId": 1,
            "posterId": 6,
            "position": {
                "x": 0.0,
                "y": 0.0
            }
        },
        {
            "posterName": "a,a",
            "fileUrl": "https://storage.googleapis.com/download/storage/v1/b/posters-sadna/o/05b9e369-3401-4c79-be5c-e16d6d4c211c?generation=1683625290680396&alt=media",
            "roomId": 1,
            "userId": 1,
            "posterId": 7,
            "position": {
                "x": 0.0,
                "y": 0.0
            }
        }
    ]
  }
}

  ```
---

## `POST /deleteRoom/{roomId}`

Deletes a room.

### Parameters

- `roomId` (required): The ID of the room to be deleted.
- `managerId` (required): The ID of the manager performing the deletion.

### Response

#### Success Response

- Status Code: `200 OK`
- Body: An instance of `RoomResponse` indicating the success of the deletion operation.

#### Error Responses

- Status Code: `404 Not Found`
    - Body: An instance of `RoomResponse` with an appropriate error message if the room ID does not exist or the user is not a manager of the room.

- Status Code: `500 Internal Server Error`
    - Body: An instance of `RoomResponse` with an appropriate error message if there was an unexpected error during the deletion operation.

### Example

#### Request

POST /deleteRoom/123?managerId=456


#### Response

HTTP/1.1 200 OK  
Content-Type: application/json
```json

{
    "message": "Room: '4' deleted successfully",
    "roomId": 4,
    "room": null
}
 ```
---

## Get Hall

- **URL:** `/hall`
- **Method:** `GET`
- **Description:** Retrieves the status of all rooms in the hall.

### Parameters

| Name    | Type     | Required | Description                         |
|---------|----------|----------|-------------------------------------|
| userId  | Integer  | Yes      | The ID of the user for whom the room status is being fetched. |

### Response

- **Status Code:** `200 OK` on success, `500 Internal Server Error` on failure.
- **Body:** JSON object with the following properties:

```json
{
  "message": "Message data",
  "roomStatuses": [
    {
      "privacy": false,
      "managerId": 1,
      "roomId": 2,
      "roomName": "shaked",
      "description": null,
      "roomMemberStatus": "NOT_A_MEMBER",
      "requestStatus": null
    },
    {
      "privacy": false,
      "managerId": 1,
      "roomId": 3,
      "roomName": "inbal",
      "description": null,
      "roomMemberStatus": "NOT_A_MEMBER",
      "requestStatus": "PENDING"
    }
  ]
}

  ```
---

## Get Waiting Join Room Requests

- **URL:** `/waitingJoinRoomRequests`
- **Method:** `GET`
- **Description:** Retrieves all join room requests for a specific manager.

### Parameters

| Name       | Type    | Required | Description                                     |
|------------|---------|----------|-------------------------------------------------|
| managerId  | Integer | Yes      | The ID of the manager whose requests are fetched.|

### Response

- **Status Code:** `200 OK` on success.
- **Body:** JSON object with the following properties:

```json
{
  "message": "Message data",
  "joinRoomRequests": [
    {
      "userId": 2,
      "roomId": 3,
      "requestStatus": "PENDING"
    },
    {
      "userId": 1,
      "roomId": 3,
      "requestStatus": "PENDING"
    }
  ]
}

  ```
---

## Ask Join Room

- **URL:** `/joinRoom/{roomId}`
- **Method:** `POST`
- **Description:** Sends a join room request for a specific room.

### Parameters

| Name    | Type    | Required | Description                                     |
|---------|---------|----------|-------------------------------------------------|
| roomId  | Integer | Yes      | The ID of the room for which the request is sent.|
| userId  | Integer | Yes      | The ID of the user sending the join room request.|

### Response

- **Status Code:** `200 OK` on success, `404 Not Found` if room or user does not exist.
- **Body:** JSON object with the following properties:

```json
{
  "message": "Your join request has been sent successfully",
  "joinRoomRequests": {
    "userId": 1,
    "roomId": 3,
    "requestStatus": "PENDING"
  }
}

  ```
---

## `GET /completedRequests`

Retrieves the completed join room requests for a specific user.

### Parameters

- `userId` (required): The ID of the user for whom to retrieve completed join room requests.

### Response

### Success Response

- Status Code: `200 OK`
- Body: An instance of `AllJoinReqResponse` containing the completed join room requests and a message.

### Error Response

- Status Code: Relevant error status code (e.g., `400 Bad Request`, `404 Not Found`, etc.)
- Body: Error response body with detailed information about the error.

### Example

### Request

GET /completedRequests?userId=123


### Response
```json

{
    "message": "Message data",
    "joinRoomRequests": [
        {
        "userId": 7,
        "roomId": 3,
        "requestStatus": "DECLINED"
      }
    ]
}
 ```
---

## `POST /handlePendingJoinRequests`

Handles pending join room requests.

### Request Parameters
- `managerId` (required): The ID of the manager responsible for handling the requests.

### Request Body

- `joinRoomRequestsToHandle` (required): A list of join room requests to handle. Each request should be represented by a `JoinRoomRequest` object.

### Response

### Success Response

- Status Code: `200 OK`
- Body: An instance of `AllJoinReqResponse` containing the successfully handled join room requests, a message, and a list of requests that could not be handled.

### Error Response

- Status Code: `500 Internal Server Error`
- Body: An instance of `AllJoinReqResponse` with an appropriate error message if there was an internal server error.

### Example

### Request

POST /handlePendingJoinRequests/managerId=1  
Request Body:  
```json

[
    {
        "userId": 7,
        "roomId": 3,
        "requestStatus": "DECLINED"
    },
    {
        "userId": 8,
        "roomId": 3,
        "requestStatus": "APPROVED"
    }
]
 ```

### Response
HTTP/1.1 200 OK  
Content-Type: application/json  
```json
 {
  "message": "Request handled",
  "joinRoomRequests": [
    {
      "userId": 7,
      "roomId": 3,
      "requestStatus": "DECLINED"
    }
  ],
  "notHandledJoinRoomRequests": [
    {
      "userId": 8,
      "roomId": 3,
      "requestStatus": "APPROVED"
    }
  ]
}
 ```
---






## `POST /approveRequest`

User approve that he saw all completed requests.

### Parameters

- `approvedRequests` (required): A list of `JoinRoomRequest` objects representing the approved requests.
- `userId` (required): The ID of the user approving the requests.

### Response

### Success Response

- Status Code: `200 OK`
- Body: An instance of `AllJoinReqResponse` indicating the success of the approval operation.

### Error Responses

- Status Code: `500 Internal Server Error`
  - Body: An instance of `AllJoinReqResponse` with an appropriate error message if there was an unexpected error during the approval operation.

### Example

### Request

POST /approveRequest?userId=123  
Content-Type: application/json  
```json

[
  {
    "userId": 6,
    "roomId": 3,
    "requestStatus": "DECLINED"
  }
]
 ```

### Response
```json

{
  "message": "Request handled",
  "joinRoomRequests": [
    {
      "userId": 6,
      "roomId": 3,
      "requestStatus": "DECLINED"
    }
  ],
  "notHandledJoinRoomRequests": []
}
 ```
---