package io.github.aachar0.inventoryapi.web;

import io.swagger.v3.oas.annotations.Hidden;                      // keeps this method out of the Swagger page
import org.springframework.web.bind.annotation.GetMapping;       // links a method to GET requests
import org.springframework.web.bind.annotation.RestController;   // marks a class that answers web requests

import java.time.Year;                                            // gives the current year for the copyright line

/**
 * Answers requests to the bare address ("/") with a small landing page: what the API is, a button to the
 * Swagger documentation, and the footer (copyright, "Created by", Portfolio, Source code).
 *
 * The security rules already allow "/" for everyone (see SecurityConfig), so no other change is needed.
 */
@RestController
public class HomeController {

    // STEP: replace the two texts below with your real links (keep the quote marks).
    // Tip: press Ctrl+F and search for  PASTE-  to find both of them.
    private static final String PORTFOLIO_URL = "https://aachar-0.github.io/";
    private static final String SOURCE_CODE_URL = "https://github.com/AAchar-0/inventory-api";

    /** The page. {{YEAR}}, {{PORTFOLIO_URL}} and {{SOURCE_URL}} are filled in by home() below. */
    private static final String PAGE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Inventory API</title>
              <style>
                :root { --bg:#f6f8fa; --ink:#12202f; --muted:#51606f; --line:#d5dde5; --accent:#0b7a75; --accent-ink:#ffffff; }
                @media (prefers-color-scheme: dark) {
                  :root { --bg:#101a24; --ink:#e8eef3; --muted:#a6b4c2; --line:#2a3a4a; --accent:#4fc3bc; --accent-ink:#08201f; }
                }
                body { margin:0; min-height:100vh; display:flex; flex-direction:column; background:var(--bg); color:var(--ink);
                       font:16px/1.55 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif; }
                main { flex:1; width:min(720px,100% - 2.5rem); margin:0 auto; padding-block:3rem 1rem; }
                h1 { margin:0 0 .5rem; font-size:clamp(1.7rem,4vw,2.3rem); }
                p { color:var(--muted); }
                .btn { display:inline-block; margin-top:.5rem; padding:.7rem 1.4rem; border-radius:8px; font-weight:600;
                       background:var(--accent); color:var(--accent-ink); text-decoration:none; }
                .btn:focus-visible, a:focus-visible { outline:3px solid #1a5fd0; outline-offset:2px; }
                footer { width:min(720px,100% - 2.5rem); margin:2rem auto 0; padding-block:1.5rem; border-top:1px solid var(--line);
                         color:var(--muted); font-size:.95rem; }
                footer p { margin:0; }
                footer a { color:var(--accent); font-weight:600; }
              </style>
            </head>
            <body>
              <main>
                <h1>Inventory API</h1>
                <p>A Java and Spring Boot REST API for products, categories and stock movements, with JWT login,
                   two roles, a row lock that prevents overselling, and automated tests.</p>
                <p>Demo project: the products are sample data and nothing here is a real shop or a real purchase.</p>
                <a class="btn" href="/swagger-ui.html">Open the API documentation</a>
              </main>
              <footer>
                <p>&copy; {{YEAR}} &middot; Created by Anade Acharo.
                   <a href="{{PORTFOLIO_URL}}" rel="noopener">Portfolio</a>.
                   <a href="{{SOURCE_URL}}" rel="noopener">Source code</a></p>
              </footer>
            </body>
            </html>
            """;

    @Hidden                                                       // do not list this helper in the API documentation
    @GetMapping(value = "/", produces = "text/html;charset=UTF-8")   // runs when someone opens the bare address
    public String home() {
        return PAGE
                .replace("{{YEAR}}", String.valueOf(Year.now().getValue()))
                .replace("{{PORTFOLIO_URL}}", PORTFOLIO_URL)
                .replace("{{SOURCE_URL}}", SOURCE_CODE_URL);
    }
}
