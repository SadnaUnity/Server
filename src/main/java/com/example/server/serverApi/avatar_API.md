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
