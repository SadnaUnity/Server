# Table Descriptions

## `users` table
This table stores information about users of the application.

| Column Name | Data Type | Key | Description |
| --- | --- | --- | --- |
| user_id | INT | Primary Key | The unique identifier for each user. |
| username | VARCHAR(50) | | The username for each user. |
| password | VARCHAR(50) | | The hashed password for each user. |

## `rooms` table
This table stores information about the different chat rooms in the application.

| Column Name | Data Type | Key | Description |
| --- | --- | --- | --- |
| room_id | INT | Primary Key | The unique identifier for each room. |
| manager_id | INT | Foreign Key to `users` table | The ID of the user who manages the room. |

## `posters` table
This table stores information about the posters in each room.

| Column Name | Data Type | Key | Description |
| --- | --- | --- | --- |
| poster_id | INT | Primary Key | The unique identifier for each poster. |
| user_id | INT | Foreign Key to `users` table | The ID of the user who posted the image. |
| image | BLOB | | The image data for the poster. |
| room_id | INT | Foreign Key to `rooms` table | The ID of the room in which the poster was posted. |
