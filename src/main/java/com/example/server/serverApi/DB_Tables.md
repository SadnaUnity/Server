# Table: users

This table stores information about users in the `sadna_db` database.

| Column Name | Data Type     | Constraints      |
|-------------|---------------|------------------|
| user_id     | int           | Primary Key      |
| username    | varchar(50)   | Nullable         |
| password    | varchar(50)   | Nullable         |

# Table: posters

This table stores information about posters in the `sadna_db` database.

| Column Name | Data Type     | Constraints                                         |
|-------------|---------------|-----------------------------------------------------|
| poster_id   | int           | Primary Key                                         |
| user_id     | int           | Not Null                                            |
| room_id     | int           | Not Null                                            |
| poster_name | varchar(50)   | Not Null                                            |
| url         | varchar(255)  | Nullable                                            |
| position_x  | float         | Nullable                                            |
| position_y  | float         | Nullable                                            |

Foreign Key Constraints:
- `user_id` references `users(user_id)`
- `room_id` references `rooms(room_id)` with `ON DELETE CASCADE`

Indexes:
- Index on `room_id`
- Index on `user_id`

# Table: avatars

This table stores information about avatars in the `sadna_db` database.

| Column Name | Data Type     | Constraints                                        |
|-------------|---------------|----------------------------------------------------|
| accessory   | enum          | ('HEART_GLASSES', 'SANTA_HAT', 'NORMAL_GLASSES', 'COOK_HAT', 'EMPTY') Nullable |
| color       | enum          | ('PINK', 'BLUE', 'GREEN', 'YELLOW') Nullable      |
| avatar_id   | int           | Primary Key                                        |

Foreign Key Constraint:
- `avatar_id` references `users(user_id)`

# Table: rooms

This table stores information about rooms in the `sadna_db` database.

| Column Name  | Data Type    | Constraints                            |
|--------------|--------------|----------------------------------------|
| room_id      | int          | Primary Key                            |
| manager_id   | int          | Not Null                               |
| room_name    | varchar(50)  | Not Null                               |
| max_capacity | int          | Default: 50, Nullable                  |
| privacy      | tinyint(1)   | Nullable                               |
| description    | varchar(250) | Not Null                               |

Foreign Key Constraint:
- `manager_id` references `users(user_id)`
