# github_monitor_springboot
project for web technologies


1. create .env file with needed secrets and sensitive information following this format:

Database Credentials

DB_URL=
DB_USERNAME=
DB_PASSWORD=

JWT Secrets

JWT_SECRET=
JWT_EXPIRATION=

GITHUB_TOKEN=

2. run docker compose up -d

3. use mvn clean install & mvn spring-boot:run to run the application

4. navigate to http://localhost:8080/swagger-ui.html

5. the admin user is seeded to: admin@default.com / default