# Table Descriptions

## `users` table
This table stores information about users of the application.

| Column Name | Data Type | Key | Description |
| --- | --- | --- | --- |
| user_id | INT | Primary Key | The unique identifier for each user. |
| username | VARCHAR(50) | | The username for each user. |
| password | VARCHAR(50) | | The hashed password for each user. |

## `avatars` table

The `avatars` table stores information about the avatars created by users.

| Column Name | Data Type   | Constraints                        | Description                           |
|-------------|-------------|------------------------------------|---------------------------------------|
| avatar_id   | INT         | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for the avatar      |
| user_id     | INT         | FOREIGN KEY (references users.user_id), NOT NULL | Unique identifier for the user who created the avatar |
| avatar_name | VARCHAR(50) | NOT NULL                           | The name of the avatar                 |
| accessories | VARCHAR(255) | NULL                               | A comma-separated list of accessories attached to the avatar |
| color       | VARCHAR(50) | NULL                               | The color of the avatar                |


## `rooms` table
This table stores information about the different chat rooms in the application.

| Column Name | Data Type | Key | Description |
| --- | --- | --- | --- |
| room_id | INT | Primary Key | The unique identifier for each room. |
| manager_id | INT | Foreign Key to `users` table | The ID of the user who manages the room. |
| max_capacity | INT |  | Max number of users in room in the same time. |

## `posters` table
This table stores information about the posters in each room.

| Column Name | Data Type | Key | Description |
| --- | --- | --- | --- |
| poster_id | INT | Primary Key | The unique identifier for each poster. |
| user_id | INT | Foreign Key to `users` table | The ID of the user who posted the image. |
| image | BLOB | | The image data for the poster. |
| room_id | INT | Foreign Key to `rooms` table | The ID of the room in which the poster was posted. |
| poster_name | VARCHAR(50) | | The room name (uniqe) |

