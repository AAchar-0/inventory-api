# Reference Sheet 06: Git and GitHub (HTTPS, VS Code, Windows)

Your GitHub account uses HTTPS (Hypertext Transfer Protocol Secure), so no SSH keys are needed: Git Credential Manager opens a browser login the first time you push.

## What is pushed and what is not

| Push to GitHub | Never push (already listed in `.gitignore`) |
|---|---|
| `src/`, `pom.xml`, `README.md`, `docs/` | `target/` (build output) |
| `Dockerfile`, `docker-compose.yml`, `.github/` | `.env` and any real passwords, keys, tokens |
| `.env.example` (placeholders only) | IDE folders (`.idea/`, most of `.vscode/`) |
| `requests.http` (with the placeholder password) | Logs, `*.jar`, `application-local.yml` |

## One-time: create the repository

1. Go to https://github.com/new
2. Repository name: `inventory-api`. Description: "Spring Boot inventory REST API with JWT, Flyway, tests".
3. Choose **Public** (it is a portfolio piece). Do **not** tick "Add a README", ".gitignore" or a licence (we already have files).
4. Click **Create repository** and keep the page open.

## First push (PowerShell, in the project folder)

```powershell
cd C:\dev\inventory-api
git init -b main
git status                       # review the list: you should NOT see target/ or .env
git add .
git status                       # everything staged should be source files
```

**Secret check before the first commit** (do this every time you add new config files):

```powershell
git diff --cached --name-only | Select-String -Pattern "\.env$|\.pem$|\.key$|password"
git grep --cached -n -i -E '(password|secret|token)\s*[:=]\s*\S{12,}'
```
The first command must print nothing. The second will list lines in `application.yml` (they only read environment variables, e.g. `${JWT_SECRET:}`), the test file and `requests.http` (fake or placeholder values). Anything that looks like a **real** credential must be removed before you commit.

```powershell
git commit -m "feat: inventory REST API with JWT auth, Flyway schema and tests"
git remote add origin https://github.com/AAchar-0/inventory-api.git
git push -u origin main          # a browser window asks you to sign in to GitHub
```

Then open the repository on GitHub, check the **Actions** tab (the CI workflow runs `mvn verify`), and confirm `.env` is not in the file list.

## Everyday workflow

```powershell
git status                       # what changed?
git add -p                       # review and stage chunk by chunk (safer than "git add .")
git commit -m "fix: reject negative reorder level"
git push
git log --oneline -10            # recent history
git diff                         # unstaged changes
```

Commit message style (Conventional Commits): `feat:` new feature, `fix:` bug fix, `docs:` documentation, `test:` tests, `refactor:`, `chore:`.

## Branches (shows professional habits)

```powershell
git switch -c feature/product-images     # create and move to a branch
git push -u origin feature/product-images
# open a Pull Request on GitHub, let CI pass, then "Merge"
git switch main; git pull
```

## If you committed a secret by mistake

1. **Rotate it immediately**: change the password / generate a new key. Treat the old one as public.
2. Tell me, and we remove it from history (deleting the file in a new commit is NOT enough).

## Make the repository look professional

* Pin the repository on your GitHub profile.
* Add topics: `java`, `spring-boot`, `rest-api`, `jwt`, `postgresql`, `flyway`, `docker`.
* Add the README link to your LinkedIn "Featured" section and your portfolio website's projects list.
* A green CI badge in the README: `![CI](https://github.com/AAchar-0/inventory-api/actions/workflows/ci.yml/badge.svg)` (paste under the README title once the first CI run has passed).
