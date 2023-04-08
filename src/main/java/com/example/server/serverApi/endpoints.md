# API Endpoints Documentation

## Login

This endpoint allows a user to login and obtain a token.

**URL:** `/login`

**Method:** `POST`

**Request Body:**

| Field    | Type   | Required | Description           |
| -------- | ------ | -------- | --------------------- |
| username | string | Yes      | The user's username.   |
| password | string | Yes      | The user's password.   |

**Response:**

| Field   | Type   | Description                  |
| ------- | ------ | ---------------------------- |
| message | string | A success message.            |
| user_id   | int | The user's id (uniqe) |

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

**Response:**

| Field   | Type | Description                  |
| ------- | ---- | ---------------------------- |
| message | string | Response message .            |
| user_id | int  | The user's ID.               |

---

## Poster

This endpoint allows a user to post a new poster in a room.

**URL:** `/poster`

**Method:** `POST`

**Request Body:**

| Field       | Type | Required | Description                        |
| ----------- | ---- | -------- | ---------------------------------- |
| userId     | int  | Yes      | The ID of the user posting.        |
| roomId     | int  | Yes      | The ID of the room to post in.     |
| posterName | string | Yes   | The name of the poster.            |
| image       | file | Yes      | The image of the poster to post.   |

**Response:**

| Field     | Type   | Description                        |
| --------- | ------ | ---------------------------------- |
| message   | string | Response message.                  |
| userId   | int    | The ID of the user who posted.      |
| posterId | int    | The ID of the posted poster.        |
| roomId   | int    | The ID of the room where posted.    |

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

**Response:**

| Field     | Type   | Description                        |
| --------- | ------ | ---------------------------------- |
| message   | string | Response message.                  |
| roomId   | int    | The ID of the room where the poster is posted. |
| userId | string | The URL of the poster image.        |
