# Reference Sheet 05: Calling the API from PowerShell

Easiest options: **Swagger** (http://localhost:8080/swagger-ui.html) or the **REST Client** extension with `requests.http`.
PowerShell version for terminal lovers (`Invoke-RestMethod` parses JSON for you):

```powershell
$base = "http://localhost:8080"

# 1. Log in and keep the token
$login = Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -ContentType "application/json" `
  -Body (@{ username = "admin"; password = "YOUR-ADMIN-PASSWORD" } | ConvertTo-Json)
$headers = @{ Authorization = "Bearer $($login.token)" }

# 2. List categories
Invoke-RestMethod -Uri "$base/api/categories" -Headers $headers

# 3. Create a product
$body = @{ sku="USB-C-1M"; name="USB-C cable"; price=6.50; quantity=3; reorderLevel=5; categoryId=1 } | ConvertTo-Json
$product = Invoke-RestMethod -Method Post -Uri "$base/api/products" -Headers $headers -ContentType "application/json" -Body $body

# 4. Receive stock
$move = @{ type="IN"; quantity=50; note="Delivery" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "$base/api/products/$($product.id)/movements" -Headers $headers -ContentType "application/json" -Body $move

# 5. Search
Invoke-RestMethod -Uri "$base/api/products?q=usb&sort=price&dir=desc&size=5" -Headers $headers
```

An error response throws in PowerShell; to see the JSON body of the error:

```powershell
try { Invoke-RestMethod -Uri "$base/api/products/999" -Headers $headers }
catch { $_.ErrorDetails.Message }
```

## Reading a token (for learning)

A JWT is `header.payload.signature`. Paste a token at https://jwt.io to read the payload (never paste a real production token into websites). You will see `sub` (username), `roles`, `iat` (issued at) and `exp` (expiry), all as Unix timestamps.

## Typical error JSON

```json
{ "type": "about:blank", "title": "Conflict", "status": 409,
  "detail": "Not enough stock for USB-C-1M: available 3, requested 10" }
```

Validation failure (400) adds a field list:

```json
{ "title": "Invalid request", "status": 400, "detail": "Validation failed",
  "errors": { "sku": "must not be blank", "price": "must be greater than or equal to 0.00" } }
```
