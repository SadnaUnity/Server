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
