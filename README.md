# Schema-Validator
 Create a REST API to validate a JSON against a schema. 


 Schema Validator is a Spring Boot application that allows users to validate JSON objects against predefined JSON schemas. Each user can store and manage their own schemas and validate JSON data against these    schemas.
### Technologies Used
* Java
* Spring Boot
* Spring Security
* Jackson (for JSON processing)
* Maven
* BCrypt (for password encoding)
* Prerequisites
* Java 17 or higher
* Maven 3.6.0 or higher

## Compile and Run 
_Note: this project can be run using an idee (ex: Intellij IDE) or by console line_

clone this repository [Schema-Validator](https://github.com/cosmina2902/Schema-Validator.git)

#### VIA INTELLIJ IDEA OR ANOTHER IDE
1. Import the project in IDEA `FILE -> OPEN-> path/to/project`
2. Select `SchemaValidatorApplication` and **RUN**
3. OPEN **POSTMAN** OR **Talend AP Tester** and implement the next steps which are same for both choice to compile

#### VIA INTELLIJ IDEA OR ANOTHER IDE
1. Prepare the right workspace: 
 * Java (ex : jdk-18.0.2.1) to be present and `JAVA HOME` to be configure in **environment variables**
 * Maven install and `MAVEN_HOME` to be configure in **environment variables** ex: `C:\Program Files\apache-maven-3.9.8\`
 * Adding bin Maven file in PATH in **environment variables**
2. Run this commands in root dir(same with pom.xml):
 **compile project**
 ```sh
 mvn clean package
 ```
 **move to target dir**
 ```sh
 cd target
 ```
 **run application**
 ```sh
 java -jar schema-validator-0.0.1-SNAPSHOT.jar
 ```
3. OPEN **POSTMAN** OR **Talend AP Tester** and implement the next steps which are same for both choice to compile

##### !IMPORTANT In root file we will find two helper files: 

* `json-Jobschema.json` which reprezent a schema for a Json
* `json-prototipe-exemple.json` which is json relative for the schema above
* `src\main\resources\config.json` which reprezent "database" for this project

This application allows you to create new user with admin rights 

   * admin user : *admin*
   * admin password: *adminpass*

But if you only want to test in the `src\main\resources\config.json`, I already added 2 users and two schemas for testing.

_Note password are encrypted in config.json file_

### Test in POSTMAN:
1. Complete Authorization with Basic Auth:
 - Username: user1
 - Password: password1
2. In a `POST` Request add: 
 ```sh
http://localhost:8080/api/json-schema/jobSchema
 ```
Where `jobSchema` reprezent name of the schema present in `config.json file`

In `Body` for `jobSchema` put the content from `json-prototipe-exemple.json` from root dir 

4. Send the request and wait for result 
![image](https://github.com/user-attachments/assets/b67dea3d-c57a-4d09-aed6-476935f7458e)


But if you will change for exemple `"Knowledge of Web technologies": true` in `"Knowledge of Web technologies": "true"` you will recieve a error message 
`Please fix your JSON! 
$.candidate_requirements.Knowledge of Web technologies: string found, boolean expected`

Because against schema candidate_requirements.Knowledge of Web technologies expected for a Boolean value. 

5. Play with json and change different type of value to see if you will recieve the right error message.
#### Test in POSTMAN to add data in json config file:

1. Add a user with **admin rights**: 
   * In a `POST` Request add: 
  ```sh
 http://localhost:8080/api/users
  ```
   * In `BODY` : 
ex: {
        "username": "user3",
        "password": "password3",
        "schemas": []
    }
2. Add a schema to a user(connect in Authorization with your user rights):
 * In a `POST` Request add: 
  ```sh
http://localhost:8080/api/users/schemas?schemaName=jobSchema
  ```
_Note: jobSchema reprezent the name of the schema you want to save in database_
   * In `BODY`: 
ex: content from `json-Jobschema.json`
_I used [generator schema](https://transform.tools/json-to-json-schema) to generete schemas for this application_
3. Tested like above(see: `Test in POSTMAN-> point 2`)



