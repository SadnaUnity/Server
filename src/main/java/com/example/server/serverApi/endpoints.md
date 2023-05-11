# API Endpoints Documentation

## Login

**URL:** `/login`

**Method:** `POST`

**Request Body:**

| Field    | Type   | Required | Description           |
| -------- | ------ | -------- | --------------------- |
| username | string | Yes      | The user's username.   |
| password | string | Yes      | The user's password.   |

**Example Request:**. 

POST /login?username=johndoe&password=secretpassword 

**Example Response:**.  
<code>HTTP/1.1 200 OK  
Content-Type: application/json</code>
```json
{
  "avatar_id": 1,
  "message": "Login successfully",
  "avatar": {
    "accessory": "EMPTY",
    "color": "YELLOW",
    "name": "avatar_1",
    "avatarId": 1
  },
  "username": "dana",
  "userId": 1
}
```

---

## Register

This endpoint allows a user to register and create an account.

**URL:** `/register`

**Method:** `POST`

**Request Body:**

| Field    | Type   | Required | Description           |
| -------- | ------ | -------- | --------------------- |
| username | string | Yes      | The user's username.   |
| password | string | Yes      | The user's password.   |

**Example Request:**  
POST /register?username=johndoe&password=secretpassword

**Response:**

The response will include a JSON object with the following keys:

| Field   | Type | Description                  |
| ------- | ---- | ---------------------------- |
| message | string | Response message .            |
| user_id | int  | The user's ID.               |

**Example Response:**.
<code>HTTP/1.1 200 OK  
Content-Type: application/json</code>
```json
{
"avatar_id": 9,
"message": "User: 'amira' created successfully",
"avatar": null,
"username": "amira",
"userId": 9
}
```

---

## Return Avatar Data

This endpoint returns the data for a specified avatar.

**URL:** `/avatar/{avatarId}`

**Method:** `GET`

**URL Parameters:**

| Parameter | Type   | Required | Description                              |
| --------- | ------ | -------- | ---------------------------------------- |
| avatarId  | int    | Yes      | The ID of the avatar to retrieve data for. |

Example Request:  
GET /avatar/{avatarId}

**Response:**

The response will include a JSON object with the following keys:

| Field      | Type   | Description                     |
| ---------- | ------ | ------------------------------- |
| message    | string | Response message.               |
| avatar_id  | int    | The ID of the retrieved avatar. |
| avatar     | object | The data of the retrieved avatar. |

**Example Response:**  
<code>HTTP/1.1 200 OK  
Content-Type: application/json</code>
```json
{
  "avatarId": 7,
  "message": "Valid Avatar",
  "avatar": {
    "accessory": "EMPTY",
    "color": "RED",
    "name": "maiush",
    "avatarId": 7
  },
  "userId": 7
}
```

If the specified avatar ID does not exist, the response will have a 404 Not Found status code:  

<code>HTTP/1.1 404 OK  
Content-Type: application/json</code>  
```json
{
"avatarId": 70,
"message": "ERROR! Avatar for user id: '70' isn't exist",
"avatar": null,
"userId": 70
}
```

---

## Edit Avatar Properties

This endpoint allows a user to edit the properties of their avatar.

**URL:** `/avatar/{avatarId}`

**Method:** `PUT`

**Request Body:**

A JSON object with the following keys:

| Field     | Type   | Required | Description                       |
| --------- | ------ | -------- | --------------------------------- |
| name      | string | No       | The new name for the user's avatar. |
| color     | string | No       | The new color for the user's avatar. |
| accessory | string | No       | The new accessory for the user's avatar. |

**Path Variables:**

| Field    | Type   | Required | Description                        |
| -------- | ------ | -------- | ---------------------------------- |
| avatarId | int    | Yes      | The ID of the avatar to be edited.  |

**Example Request:**.
PUT /avatar/5
```json
{
  "name": "My New Avatar Name",
  "color": "blue",
  "accessory": "hat"
} 
```
Response:

The response will include a JSON object with the following keys:

| Field    | Type   | Description                     |
| -------- |--------| ------------------------------- |
| message | string | Response message.|
| avatar_id	 | int    | The ID of the edited avatar.|
| avatar | object | The updated avatar object.|

Example Response:  
<code>HTTP/1.1 200 OK  
Content-Type: application/json</code>

```json
{
  "message": "Avatar properties updated successfully",
  "avatar_id": 5,
  "avatar": {
    "avatarId": 5,
    "name": "My New Avatar Name",
    "color": "blue",
    "accessory": "hat"
  }
}
```

---
## Create a New Poster

This endpoint allows a user to create a new poster in a specified room.

**URL:** `/poster`

**Method:** `POST`

**Request Parameters:**

The request should include a JSON object with the following keys:

| Field       | Type | Required | Description                        |
| ----------- | ---- | -------- | ---------------------------------- |
| userId     | int  | Yes      | The ID of the user posting.        |
| roomId     | int  | Yes      | The ID of the room to post in.     |
| posterName | string | Yes   | The name of the poster.            |

Request Body:

The request must include a file uploaded using multipart/form-data with the following keys:  

| Field | Type | Required | Description                        |
|-------|------| -------- | ---------------------------------- |
| file  | File | Yes      | The image file of the new poster (maximum 10 MB).|

**Example Request:**
POST /poster?posterName=poster4&userId=1&roomId=1

{
  "userId": 123,
  "roomId": 456,
  "posterName": "exampleposter",
  "image": "https://example.com/exampleimage.jpg"
}

**Response:**

The response will include a JSON object with the following keys:

| Field    | Type   | Description                        |
|----------| ------ | ---------------------------------- |
| message  | string | Response message.                  |
| posterId | int    | The ID of the newly created poster.|
| poster   | object    | The newly created poster object.|

**Example Response:**  
<code>HTTP/1.1 200 OK  
Content-Type: application/json</code>
```json
{
  "message": "Poster: 'poster4' created successfully",
  "posterId": 1,
  "poster": {
    "fileUrl": "https://storage.googleapis.com/download/storage/v1/b/posters-sadna/o/posterId",
    "roomId": 1,
    "userId": 1,
    "posterId": 5
  }
}
```
---
## Delete Poster

This endpoint allows a user to delete a specific poster from the database.

URL: /deletePoster/{posterId}

Method: POST

Path Variables:

| Field       | Type | Required | Description                       |
| ----------- | ---- | -------- | --------------------------------- |
| posterId     | int  | Yes      |The ID of the poster to be deleted.|

**Example Request:**  
POST /deletePoster/{poster_id}

| Field       | Type    | Description                        |
| ----------- |-------- | ---------------------------------- |
| message     | string  |A message indicating the status of the request.|
| posterId       | int     | The ID of the poster that was deleted.|
| poster       | object  | The poster object that was deleted. This will be null since the poster was deleted and no longer exists in DB.|

Example Response:

<code>HTTP/1.1 200 OK  
Content-Type: application/json</code>

```json
{
  "message": "Poster: '1' deleted successfully",
  "posterId": 1,
  "poster": null
}
```
---
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
    "message": "User 456 changed room successfully to Room 123",
    "roomId": 123,
    "roomInfo": {
      "name": "Example Room",
      "capacity": 10
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
## Get All Rooms

Retrieves information about all rooms.

**URL:** `/rooms`

**Method:** `GET`

**Request Parameters:** The endpoint does not require any request parameters.

**Example Request:**

GET /rooms

**Example Response(HTTP 200 OK):**
 ```json
{
  "message": "Message data",
  "roomsData": {
    "1": "mai's room",
    "2": "shaked's room",
    "3": "inbal's room"
  }
}
  ```
---
## Get Positions

Retrieves the positions of avatars in a room for a specific user.

**URL:** `/getPositions`

**Method:** `GET`

**Request Parameters:**

| Field       | Type | Required | Description                              |
| ----------- |------| -------- |------------------------------------------|
| userId       | int  | Yes      |An integer parameter representing the ID of the user.                |


**Example Request:**

GET /getPositions?userId={userId}

**Example Response(HTTP 200 OK):**
 ```json
{
  "message": "Avatars data",
  "avatarPositions": [
    {
      "avatar": {
        "accessory": "EMPTY",
        "color": "YELLOW",
        "avatarId": 1
      },
      "position": {
        "x": 2.0,
        "y": 3.0
      }
    },
    {
      "avatar": {
        "accessory": "EMPTY",
        "color": "RED",
        "avatarId": 3
      },
      "position": {
        "x": 2.0,
        "y": 3.0
      }
    }
  ]
}
  ```

---
