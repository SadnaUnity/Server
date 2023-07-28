## Room

This endpoint creates new room.

**URL:** `/room`

**Method:** `POST`

**Request Body:**

| Field       | Type | Required | Description                                                                    |
| ----------- | ---- | -------- |--------------------------------------------------------------------------------|
| managerId     | int  | Yes      | The ID of the admin.                                                           |
| roomName       | string | Yes      | The name of the room.                                                          |
| privacy       | boolean | No      | Indicates whether the room is private (TRUE) or public (FALSE). Default: False |
| description       | string | No      | room description                                                               |
| background       | string | No      | room background. values: BACKGROUND_1,BACKGROUND_2,BACKGROUND_3,BACKGROUND_4   |

**Example Request:**  

POST /room?roomName=maiRoom
body:
```json
{
  "privacy" : true,
  "managerId" : 1,
  "description" : "hi...",
  "background" : "BACKGROUND_3"
}
```
**Example Response:**  
```json
{
  "message": "Completed successfully",
  "roomId": 1,
  "room": {
    "privacy": false,
    "managerId": 1,
    "roomId": 1,
    "posters": [
      {
        "posterName": "htrtrfghfgh",
        "fileUrl": "https://storage.googleapis.com/download/storage/v1/b/posters-sadna/o/c3c31ecce?generation=16836277116&alt=media",
        "roomId": 1,
        "userId": 1,
        "posterId": 13,
        "position": {
          "x": 0.0,
          "y": 0.0
        }
      },
      {
        "posterName": "postername",
        "fileUrl": "https://storage.googleapis.com/download/storage/v1/b/posters-sadna/o/68b503b336?generation=16835631&alt=media",
        "roomId": 1,
        "userId": 1,
        "posterId": 14,
        "position": {
          "x": 0.0,
          "y": 0.0
        }
      }
    ],
    "roomName": "mai"
  }
}
```
---

## Endpoint: Update Room Image

## Description:
This endpoint allows users to update the image of a room identified by the given `roomId`. The request must include a valid image file as a `multipart/form-data` in the `file` parameter. The image is uploaded to Google Cloud Storage (GCS), and its URL is associated with the room by updating the 'url' column in the database.

## Request:
- **URL:** `/roomImage/{roomId}`
- **Method:** POST
- **Headers:**
  - Content-Type: `multipart/form-data`

- **Path Parameters:**
  - `roomId` (Integer): The unique identifier of the room for which the image will be updated.
- **Request Parameters:**
  - `userId` (Integer): The ID of the user making the request.
- **Request Body:**
  - `file` (MultipartFile): The image file to be uploaded.

## Response:
- **HTTP Status:**
  - 200 OK - The room image was updated successfully.
  - 400 Bad Request - The request was invalid (e.g., empty image file or file too big).
  - 500 Internal Server Error - An unexpected error occurred during the image upload or database update process.
- **Response Body (JSON):**
  - `roomId` (Integer): The ID of the room for which the image was updated.
  - `message` (String): A message describing the result of the operation.
  - `roomDetails` (Object): Details of the updated room.

## Request Example:
POST /roomImage/123 HTTP/1.1

## Successful Response Example:
 ```json
{
  "roomId": 123,
  "message": "Room image updated successfully.",
  "roomDetails": {
    "roomId": 123,
    "roomName": "Example Room",
    "url": "https://storage.googleapis.com/example-bucket/room_images/room_image.jpg",
    "otherDetails": "Other room details..."
  }
}
```

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
      "username": "name",
      "roomId": 3,
      "requestStatus": "PENDING"
    },
    {
      "userId": 1,
      "username": "name",
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
    "username": "name",
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
          "username": "name",
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
        "username": "name",
        "roomId": 3,
        "requestStatus": "DECLINED"
    },
    {
        "userId": 8,
        "username": "name",
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
      "username": "name",
      "roomId": 3,
      "requestStatus": "DECLINED"
    }
  ],
  "notHandledJoinRoomRequests": [
    {
      "userId": 8,
      "username": "name",
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
    "username": "name",
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
      "username": "name",
      "roomId": 3,
      "requestStatus": "DECLINED"
    }
  ],
  "notHandledJoinRoomRequests": []
}
 ```
---
