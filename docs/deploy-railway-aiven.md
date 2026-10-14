# Deploy Web-Nhac to Railway with Aiven MySQL

## 1. Prepare the database

Create an Aiven for MySQL service and copy its host, port, database name, user, and password from **Overview → Connection information**. Hibernate creates the entity tables when the app starts (`hibernate.hbm2ddl.auto=update`).

After the first successful app start, run `src/main/resources/db/mysql-test-all.sql` with a MySQL client connected to the Aiven database to insert the demo catalog and sample records. The script expects Hibernate to have created the schema. It can be rerun for the fixed `sample-*` records; it also resets catalog rows with IDs `1` through `27` to their demo values. Demo login credentials are documented in that SQL file; change or remove them before using the site publicly.

`src/main/resources/db/test-all.sql` remains the SQL Server version. Do not run it against Aiven MySQL.

## 2. Push the project to GitHub

Commit and push the project, including the root `Dockerfile`, to a GitHub repository that Railway can access.

## 3. Create the Railway service

In Railway, create a project from the GitHub repository. Railway detects the root `Dockerfile`, builds the WAR, and runs it in Tomcat 10.1 with Java 21. Add these service variables before the app starts:

```text
DB_URL=jdbc:mysql://<AIVEN_HOST>:<AIVEN_PORT>/<AIVEN_DATABASE>?sslMode=REQUIRED
DB_USER=<AIVEN_USER>
DB_PASSWORD=<AIVEN_PASSWORD>
```

In **Settings → Networking**, generate a public domain. The Docker command configures Tomcat to use Railway's injected `PORT` value.

## 4. Open the site

Wait for the deployment to report success, then open the generated Railway domain. Check the deployment logs if the service fails to start; a typo in a database variable is a common first-deploy cause.

Railway's Free plan has a small monthly usage credit, so this setup is suitable for a demo and may not keep the app running continuously at no cost.
