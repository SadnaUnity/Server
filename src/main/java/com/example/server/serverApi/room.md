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
    "message": "User 456 changed room successfully to Room 123",
    "roomId": 123,
    "roomInfo": {
      "name": "Example Room",
      "capacity": 10
    }
  }
  ```
---
## `Get A Room`
This endpoint retrieves information about a specific room based on its ID.

**URL:** `/room/{roomId}`

**Method:** `GET`

**Request Parameters:**

| Field       | Type | Required | Description                             |
| ----------- |------| -------- |-----------------------------------------|
| roomId     | int  | Yes      | The ID of the room to retrieve|

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

## Get Join Room Requests

- **URL:** `/getJoinRoomRequests`
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

