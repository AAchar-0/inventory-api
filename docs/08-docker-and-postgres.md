# Reference Sheet 08: PostgreSQL and Docker (do this later)

You do **not** need any of this to run or test the API. Use it when you want to show the project running on a real PostgreSQL database.

## Fixing Docker Desktop on your PC

Docker Desktop showed "Virtual Machine Platform is not enabled". Fix:

1. Right-click the Start button -> **Terminal (Admin)** (a PowerShell window with administrator rights).
2. Run exactly:
   ```powershell
   Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform
   ```
   (If it also complains about WSL, the Windows Subsystem for Linux: run `wsl --install`.)
3. **Restart Windows** (restarting only Docker is not enough).
4. Open Docker Desktop and wait for "Engine running".
5. Test: `docker run --rm hello-world`.

If it says virtualization is disabled in the BIOS (the PC's startup settings), tell me your PC brand/model and I will give the exact key and menu.

## Option A: everything in Docker (API + PostgreSQL)

```powershell
cd C:\dev\inventory-api
Copy-Item .env.example .env          # then open .env and replace EVERY placeholder with a real value
docker compose up --build            # first build downloads a lot; later runs are fast
```
* API: http://localhost:8080/swagger-ui.html
* Stop: `Ctrl+C`, then `docker compose down` (add `-v` to also delete the database volume).
* `.env` is git-ignored: it holds your real passwords.

## Option B: PostgreSQL in Docker, API from VS Code

```powershell
docker run --name inventory-db -e POSTGRES_DB=inventory -e POSTGRES_USER=inventory `
  -e POSTGRES_PASSWORD=choose-a-password -p 127.0.0.1:5432:5432 -d postgres:17

$env:SPRING_PROFILES_ACTIVE = "postgres"
$env:DB_PASSWORD = "choose-a-password"
$env:APP_ADMIN_PASSWORD = "choose-a-strong-admin-password"
$env:JWT_SECRET = "at-least-32-random-characters-go-here!!"
mvn spring-boot:run
```
Look inside the database:
```powershell
docker exec -it inventory-db psql -U inventory -d inventory -c "\dt"
docker exec -it inventory-db psql -U inventory -d inventory -c "select * from flyway_schema_history;"
```

## Option C: PostgreSQL installed directly on Windows (no Docker)

1. Download the installer from https://www.postgresql.org/download/windows/ and install (remember the `postgres` password).
2. Open **SQL Shell (psql)** from the Start menu and run:
   ```sql
   CREATE USER inventory WITH PASSWORD 'choose-a-password';
   CREATE DATABASE inventory OWNER inventory;
   ```
3. Start the API with the same four environment variables as Option B.

## Useful Docker commands

| Command | Meaning |
|---|---|
| `docker ps` | running containers |
| `docker logs inventory-db` | container output |
| `docker stop inventory-db` / `docker start inventory-db` | pause / resume |
| `docker rm -f inventory-db` | delete the container |
| `docker images` | downloaded images |
| `docker compose up --build` / `down` | start / stop the stack |

## Why Docker matters for a job

It proves your project runs the same on any machine: `docker compose up` is the whole installation guide. Mention it in the README (already done) and on your CV.
