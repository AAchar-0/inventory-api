# Reference Sheet 07: Troubleshooting

Golden rule: read the **first** error line and the **"Caused by:"** lines at the bottom of a Java stack trace. Copy them to me exactly.

## Terminal problems

| Symptom | Cause | Fix |
|---|---|---|
| `'Remove-Item' is not recognized as an internal or external command` | You are in **Command Prompt**, not PowerShell (prompt has no `PS`) | Type `powershell` and press Enter, or open a new "PowerShell" terminal in VS Code |
| `mvn : The term 'mvn' is not recognized` | Maven not on PATH, or terminal opened before PATH changed | Check `C:\tools\apache-maven-3.10.2\bin` is in your user Path; close **all** VS Code windows, end it in Task Manager, reopen |
| `JAVA_HOME is not defined correctly` | Maven cannot find Java | Set user variable `JAVA_HOME` to `C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot` |
| `release version 21 not supported` | Maven is running an older Java | `mvn -version` must show Java 21 |
| Path with an apostrophe (`A'Acharo`) breaks a tool | Tools often mishandle `'` | Keep projects in `C:\dev` and Maven's cache in `C:\dev\.m2` (already set) |

## Maven build problems

| Symptom | Fix |
|---|---|
| `Could not find artifact ...spring-boot-starter-...` | Typos in `pom.xml`, or no internet. Send me the artifact name |
| `Could not resolve dependencies` after a network drop | Delete the broken folder under `C:\dev\.m2\repository` named in the error and rebuild |
| Tests fail but app starts | Run one test: `mvn -Dtest=StockServiceTest test` |
| Want a clean slate | `mvn clean verify` |

## Starting the app

| Symptom | Fix |
|---|---|
| `Port 8080 was already in use` | Another program uses it. Find it: `netstat -ano \| findstr :8080`, then stop that process, or run with `$env:SERVER_PORT = "8081"` |
| `app.jwt.secret (JWT_SECRET) must be at least 32 characters long` | Your `$env:JWT_SECRET` is too short |
| `Could not resolve placeholder 'DB_PASSWORD'` | You started with `postgres` profile without setting `DB_PASSWORD` |
| `FlywayException: Validate failed ... checksum mismatch` | You edited a migration that already ran. For H2 just restart (memory resets). For PostgreSQL create a new `V3__...sql` instead of editing |
| `Connection refused` to PostgreSQL | The database is not running. See `docs/08-docker-and-postgres.md` |

## Calling the API

| Status | Meaning here | Fix |
|---|---|---|
| 401 | Missing/expired/invalid token, or wrong password | Log in again; header must be `Authorization: Bearer <token>` |
| 403 | Logged in but not ADMIN | Use the admin token |
| 404 | Unknown id or wrong URL | Check `/api/...` path and the id |
| 409 | Duplicate SKU / not enough stock / category in use | Read the `detail` field |
| 400 | Validation failed | Read `errors` in the response |
| Token worked, then stopped after restart | `JWT_SECRET` not set, so a random secret was generated | Set `JWT_SECRET` to keep tokens valid across restarts |

## VS Code

* **"Java: Ready" never appears**: Command Palette (`Ctrl+Shift+P`) -> "Java: Clean Java Language Server Workspace" -> Restart.
* **Red squiggles but `mvn verify` passes**: the editor is stale. Same fix as above.
* **"Install New JDK" tab pops up**: close it. We use Java 21, which you already have. Do not install JDK 25 for this project.
* **Wrong Java version in the editor**: Command Palette -> "Java: Configure Java Runtime" and select JDK 21 for this project.

## Reporting a problem to me

Send: (1) the exact command you ran, (2) the first 15 lines of the error and any "Caused by:" lines, (3) the output of `mvn -version`. Please paste text rather than screenshots when you can.
