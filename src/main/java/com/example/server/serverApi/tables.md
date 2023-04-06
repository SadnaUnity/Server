# Table Schema

## `users` table

The `users` table contains information about registered users of the system. It has the following columns:

- `user_id`: A unique identifier for the user.
- `username`: The user's chosen username, which must be unique.
- `password`: The user's encrypted password.

The `user_id` column is the primary key of the table, which means that each user has a unique identifier that can be used to identify them in other parts of the system.  
The `username` column is also unique, which ensures that each user has a unique username.

## `posters` table

The `posters` table contains information about posters that have been uploaded to the system. It has the following columns:

- `poster_id`: A unique identifier for the poster.
- `poster_name`: The name of the poster, which may or may not be unique.
- `user_id`: The unique identifier of the user who uploaded the poster.
- `room_id`: The unique identifier of the room where the poster is displayed.

The `poster_id` column is the primary key of the table, which means that each poster has a unique identifier that can be used to identify it in other parts of the system.  
The `user_id` column is a foreign key that references the `user_id` column in the `users` table, which means that each poster is associated with the user who uploaded it.  
The `room_id` column is also a foreign key that references the `room_id` column in a `rooms` table, which means that each poster is associated with the room where it is displayed.
