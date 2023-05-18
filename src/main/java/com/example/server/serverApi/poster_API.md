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
