# Server Endpoints
This document outlines the endpoints available on the server.

## _Login Endpoint_
The login endpoint is used to authenticate a user and retrieve a session token.

### Request:
<code>POST /login  
Content-Type: application/json  
{ "username": "string", "password": "string" }</code>  

### Parameters
_username_ (string, required): The username of the user to authenticate.  
_password_ (string, required): The password of the user to authenticate.

### Response:
If the authentication is successful, the endpoint returns an HTTP 200 OK status code:  
<code>HTTP/1.1 200 OK  
Content-Type: application/json</code>   

If the authentication fails (for example, if the username or password is incorrect), the endpoint returns an HTTP 401 Unauthorized status code and an error message in the response body:  
<code>HTTP/1.1 401 Unauthorized
Content-Type: application/json 
{ "error": "string" } </code>

## _Register Endpoint_
The register endpoint is used to create a new user account.

### Request:
<code>POST /register  
Content-Type: application/json  
{ "username": "string", "password": "string" }</code>  

### Parameters:  
_username_ (string, required): The desired username for the new account.  
_password_ (string, required): The desired password for the new account.  

### Response: 

If the registration fails, the endpoint returns an HTTP 400 Bad Request status code and an error message in the response body that indicates the specific problem with the registration request:   
<code>HTTP/1.1 400 Bad Request  
Content-Type: application/json  
{ "error": { "code": "string", "message": "string" } }</code>

The error object contains two properties:

_code_ (string): A code that indicates the specific error that occurred. Possible values are:  
**USERNAME_TAKEN**: Indicates that the desired username is already taken.  
**INVALID_USERNAME**: Indicates that the desired username is invalid (for example, it contains invalid characters or is too long).   
**INVALID_PASSWORD**: Indicates that the desired password is invalid (for example, it is too short or contains invalid characters).  
**UNKNOWN_ERROR**: Indicates that an unknown error occurred (for example, a database error or a server error).    

_message_ (string): A human-readable message that provides more information about the specific error that occurred.  

<code>HTTP/1.1 400 Bad Request   
Content-Type: application/json  
{ "error": { "code": "USERNAME_TAKEN", "message": "The username 'alice' is already taken" } } </code>  

<code>HTTP/1.1 400 Bad Request  
Content-Type: application/json  
{ "error": { "code": "INVALID_EMAIL", "message": "The email address 'foo' is not a valid email address" } }</code>  

