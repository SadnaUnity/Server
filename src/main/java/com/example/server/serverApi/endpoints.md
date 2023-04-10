# API Endpoints Documentation

## Login

The request should include a JSON object with the following keys:

**URL:** `/login`

**Method:** `POST`

**Request Body:**

| Field    | Type   | Required | Description           |
| -------- | ------ | -------- | --------------------- |
| username | string | Yes      | The user's username.   |
| password | string | Yes      | The user's password.   |

**Example Request:**. 

{
  "username": "exampleuser",
  "password": "examplepassword"
}

**Response:**. 

The response will include a JSON object with the following keys:

| Field   | Type   | Description                  |
| ------- | ------ | ---------------------------- |
| message | string | A success message.            |
| user_id   | int | The user's id (uniqe) |

**Example Response:**. 

{
  "message": "Login successful.",
  "user_id": 123
}

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

**Example Request:**. 

{
  "username": "newuser",
  "password": "newpassword"
}

**Response:**

The response will include a JSON object with the following keys:

| Field   | Type | Description                  |
| ------- | ---- | ---------------------------- |
| message | string | Response message .            |
| user_id | int  | The user's ID.               |

**Example Response:**. 

{
  "message": "Registration successful.",
  "user_id": 456
}


---

## Poster

This endpoint allows a user to post a new poster in a room.

**URL:** `/poster`

**Method:** `POST`

**Request Body:**

The request should include a JSON object with the following keys:

| Field       | Type | Required | Description                        |
| ----------- | ---- | -------- | ---------------------------------- |
| userId     | int  | Yes      | The ID of the user posting.        |
| roomId     | int  | Yes      | The ID of the room to post in.     |
| posterName | string | Yes   | The name of the poster.            |
| image       | file | Yes      | The image of the poster to post.   |

**Example Request:**

{
  "userId": 123,
  "roomId": 456,
  "posterName": "exampleposter",
  "image": "https://example.com/exampleimage.jpg"
}

**Response:**

The response will include a JSON object with the following keys:

| Field     | Type   | Description                        |
| --------- | ------ | ---------------------------------- |
| message   | string | Response message.                  |
| userId   | int    | The ID of the user who posted.      |
| posterId | int    | The ID of the posted poster.        |
| roomId   | int    | The ID of the room where posted.    |

**Example Response:**  
{
  "message": "Poster successfully posted.",
  "user_id": 123,
  "poster_id": 789,
  "room_id": 456
}

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

{
  "managerId": 123,
  "roomName": "exampleroom",
}


**Response:**

| Field     | Type   | Description                        |
| --------- | ------ | ---------------------------------- |
| message   | string | Response message.                  |
| roomId   | int    | The ID of the room where the poster is posted. |
| userId | string | The URL of the poster image.        |
| maxCapacity       | int | Max users in the room in the same time.  Default: 10 |
| privacy       | boolean | Indicates whether the room is private (TRUE) or public (FALSE). Default: False    |. 

**Example Response:**  

{
  "message": "Room created successfully.",
  "user_id": 123,
  "room_id": 456
}

