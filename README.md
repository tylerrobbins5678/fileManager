# fileManager
web application file manager implemented in spring boot
uses authentication from "springewbapp" microservice to authorize all requests

# Usage
## file operations
#### post /{defaultPath}/path/to 
- can post to any path starting with /{defaultPath}
 headers: username, password
 body: form-data: file
 
#### put /{defaultPath}/path/to/file
- can update file 
- will not change file name
- headers: username, password
- body: form-data: file

#### get /{defaultPath}/path/to/file.txt 
- returns file as application/JSON
- headers: username, password
 
#### delete /{defaultPath}/path/to/file.txt
- headers: username, password

## directory searching
 - {defaultPath}/?path=/path/to
 - searching a directory will show all sub directories and files
 - headers: username, password

## permissions
 - must have admin privelage on file to complete these operations
 - enter default in email field to controll access for accounts not listed in permissions

#### get /permissions/{defaultPath}/path/to/file**
 - retrieves all permissions on file
 - headers: username, password
 
#### post /permissions/{defaultPath}/path/to/file**
 - post all permissions on file, can use list of emails
 - returns success and errors with reason for each email
 - headers: username, password
 - body: { email : { cadCreate : bool, canUpdate : bool, canDelete: bool, canAdmin:bool}}
 
#### put /permissions/{defaultPath}/path/to/file
 - updates all permissions on file, can use list of emails
 - returns success and errors with reason for each email
 - headers: username, password
 - body: { email : { cadCreate : bool, canUpdate : bool, canDelete: bool, canAdmin:bool}}
 
#### delete /permissions/{defaultPath}/path/to/file
 - deletes email from permissions for file
 - returns success and errors with reason for each email
 - headers: username, password
 - body: [email1, email2]
 

