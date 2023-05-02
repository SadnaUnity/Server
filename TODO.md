## encoding passwords

## failed create something in the middle: user id. create room...

## class/enums to add:
public enum AvatarColor
{
    PINK, BLUE, GREEN, YELLOW
}

public enum AvatarAccessory
{
    HEART_GLASSES, SANTA_HAT, NORMAL_GLASSES, COOK_HAT, EMPTY
}

public class PlayerData
{
    public string name 
    public int id 
    public AvatarColor color
    public AvatarAccessory accessory
    //will include more staff like friends list, rooms list etc.    
}
    
## readme endpoint: 
| rsc | method | query params + types | body | response |
| ----|------- | -------------------- | ---- | -------- |
| /register | POST | string username, string password, AvatarColor color, AvatarAccessory accessory | none | if username not valid return code not 200OK, else return user_id and code 200OK |
| /login | GET | string username, string password | none | if username and/or password not correct return code not 200OK, else return PlayerData|


