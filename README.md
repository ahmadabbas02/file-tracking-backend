
# EMU File Tracker API

The main purpose of this project was to make it simple for users (Students,Advisor, Secretary, Chairman, Admin) to follow up with student documents and application forms all in one place. This is done by digitalizing the process and reducing paperwork needed. Students will be able to upload documents and fill in forms directly to the system and then the responsible people will be able to approve/deny it all done in one step. Other actors will be able to see students’ documents uploaded to the system.




## Features

- Role based authentication and authorization using JWT.
- Students: 
    - Can fill forms like contact forms and petitions, a PDF will be generated and uploaded to the blob storage.
- Advisors:
    - Can view documents related to students only assigned to them.
    - Documents access is based on category of document which can be modified in specific endpoint by the admin.
- Secretary:
    - Full access to all categories and is responsible for scanning and uploading documents to the system.
    - Can approve medical reports uploaded by students.
    - Can create sub categories for documents if needed.
- Chairman:
    - Full access to students and their documents.
    - Can approve petition reports uploaded by students.
- System Administrator:
    - Can import students in batch to the database using a csv of specific format.
    - Can add users to the system.
    - Can disable student accounts from the system.





## Environment Variables

To run this project, you will need to add the following environment variables to your system environment or using IntelliJ custom run configuration for development

`DB_URL` - Connection URL to the postgres database.

`DB_USERNAME` - Database username.

`DB_PASSWORD` - Database password.

`JWT_SECRET` - Key to be used for signing generated JWTs and verifying authenticity.

`JWT_EXPIRATION_DAYS` - Number of days until generated JWTs expires.

`MAIL_HOST` - Email SMTP host.

`MAIL_PORT` - Email SMTP port.

`MAIL_USERNAME` - Email SMTP username.

`MAIL_PASSWORD` - Email SMTP password.

`AZURE_CONTAINER` - Azure storage container name which contains the blob storage.

`AZURE_CONNECTION` - Azure blob storage connection url.

`FRONTEND_ACC_ACTIVATION_URL` - URL to the activate account in the frontend, this is used as hyperlink in the emails sent to activate accounts.

`CORS_ALLOWED_ORIGINS` - Allowed CORS origins.

`CORS_ALLOWED_METHODS` - Allowed HTTP methods for CORS.

`CORS_ALLOWED_HEADERS` - Allowed HTTP headers for CORS.

`CORS_EXPOSED_HEADERS` - Exposed HTTP headers for CORS.
## Documentation

After building and running the project, the swagger API docs will be available.

The default endpoint is: http://localhost:8080/swagger-ui/index.html


## Tech Stack

**Libraries:** Spring Boot, Spring Web, Spring Data JPA, Lombok, opencsv,Flyway for database migrations. The full detailed packages can be found in `pom.xml`.

**Storage:** PostgreSQL database and Azure Blob Storage.


## Diagrams

![Schema Diagram](https://i.imgur.com/KUpjcNy.png)
