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
| user_id     | int  | Yes      | The ID of the user posting.        |
| room_id     | int  | Yes      | The ID of the room to post in.     |
| poster_name | string | Yes   | The name of the poster.            |
| image       | file | Yes      | The image of the poster to post.   |

**Response:**

| Field     | Type   | Description                        |
| --------- | ------ | ---------------------------------- |
| message   | string | Response message.                  |
| user_id   | int    | The ID of the user who posted.      |
| poster_id | int    | The ID of the posted poster.        |
| room_id   | int    | The ID of the room where posted.    |

---

## Room

This endpoint creates new room.

**URL:** `/room/`

**Method:** `POST`

**Request Body:**

| Field       | Type | Required | Description                        |
| ----------- | ---- | -------- | ---------------------------------- |
| manager_id     | int  | Yes      | The ID of the admin.        |
| room_name       | string | Yes      | The name of the room.   |

**Response:**

| Field     | Type   | Description                        |
| --------- | ------ | ---------------------------------- |
| message   | string | Response message.                  |
| room_id   | int    | The ID of the room where the poster is posted. |
| user_id | string | The URL of the poster image.        |
