package io.github.aachar0.inventoryapi.web;

import io.swagger.v3.oas.annotations.Hidden;                      // keeps these methods out of the Swagger page
import org.springframework.web.bind.annotation.GetMapping;       // links a method to GET requests
import org.springframework.web.bind.annotation.RestController;   // marks a class that answers web requests

import java.time.Year;                                            // gives the current year for the copyright line

/**
 * Two small pages, both with the footer (copyright, "Created by", Portfolio, Source code):
 *
 *   /      landing page: what the API is, plus a button to the documentation
 *   /docs  the Swagger documentation with the footer at the BOTTOM
 *          (the plain /swagger-ui.html page keeps working too, but it has no footer)
 *
 * Both addresses must be allowed in SecurityConfig (see the steps given with this file).
 */
@RestController
public class HomeController {

    private static final String PORTFOLIO_URL = "https://aachar-0.github.io/";
    private static final String SOURCE_CODE_URL = "https://github.com/AAchar-0/inventory-api";

    /** Styles shared by both pages. */
    private static final String STYLE = """
            <style>
              :root { --bg:#f6f8fa; --ink:#12202f; --muted:#51606f; --line:#d5dde5; --accent:#0b7a75; --accent-ink:#ffffff; }
              @media (prefers-color-scheme: dark) {
                :root { --bg:#101a24; --ink:#e8eef3; --muted:#a6b4c2; --line:#2a3a4a; --accent:#4fc3bc; --accent-ink:#08201f; }
              }
              body { margin:0; min-height:100vh; display:flex; flex-direction:column; background:var(--bg); color:var(--ink);
                     font:16px/1.55 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif; }
              a:focus-visible { outline:3px solid #1a5fd0; outline-offset:2px; }
              footer { width:min(1100px,100% - 2.5rem); margin:2rem auto 0; padding-block:1.5rem; border-top:1px solid var(--line);
                       color:var(--muted); font-size:.95rem; }
              footer p { margin:0; }
              footer a { color:var(--accent); font-weight:600; }
            </style>
            """;

    /** The footer. {{YEAR}}, {{PORTFOLIO_URL}} and {{SOURCE_URL}} are filled in by fill() below. */
    private static final String FOOTER = """
            <footer>
              <p>&copy; {{YEAR}} &middot; Created by Anade Acharo.
                 <a href="{{PORTFOLIO_URL}}" rel="noopener">Portfolio</a>.
                 <a href="{{SOURCE_URL}}" rel="noopener">Source code</a></p>
            </footer>
            """;

    /** Landing page at "/". */
    private static final String LANDING = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Inventory API</title>
              {{STYLE}}
              <style>
                main { flex:1; width:min(720px,100% - 2.5rem); margin:0 auto; padding-block:3rem 1rem; }
                h1 { margin:0 0 .5rem; font-size:clamp(1.7rem,4vw,2.3rem); }
                p { color:var(--muted); }
                .btn { display:inline-block; margin-top:.5rem; padding:.7rem 1.4rem; border-radius:8px; font-weight:600;
                       background:var(--accent); color:var(--accent-ink); text-decoration:none; }
              </style>
            </head>
            <body>
              <main>
                <h1>Inventory API</h1>
                <p>A Java and Spring Boot REST API for products, categories and stock movements, with JWT login,
                   two roles, a row lock that prevents overselling, and automated tests.</p>
                <p>Demo project: the products are sample data and nothing here is a real shop or a real purchase.</p>
                <a class="btn" href="/docs">Open the API documentation</a>
              </main>
              {{FOOTER}}
            </body>
            </html>
            """;

    /** Documentation page at "/docs": the same Swagger UI that the API already serves, with the footer under it. */
    private static final String DOCS = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Inventory API documentation</title>
              <link rel="stylesheet" href="/swagger-ui/swagger-ui.css">
              {{STYLE}}
              <style>
                #swagger-ui { flex:1; background:#ffffff; }   /* Swagger UI is designed for a white background */
               @media (prefers-color-scheme: dark) {
                  #swagger-ui { background:#101a24; }   /* same colour as the page */
                  #swagger-ui .swagger-ui { filter: invert(88%) hue-rotate(180deg); }   /* turns the light Swagger UI dark */
                  #swagger-ui .swagger-ui .microlight { filter: invert(100%) hue-rotate(180deg); }   /* puts code boxes back */
                }
              </style>
            </head>
            <body>
              <div id="swagger-ui"></div>
              {{FOOTER}}
              <script src="/swagger-ui/swagger-ui-bundle.js"></script>
              <script>
                window.onload = function () {
                  SwaggerUIBundle({ url: "/v3/api-docs", dom_id: "#swagger-ui", deepLinking: true });
                };
              </script>
            </body>
            </html>
            """;

    @Hidden                                                       // do not list this helper in the API documentation
    @GetMapping(value = "/", produces = "text/html;charset=UTF-8")
    public String home() {
        return fill(LANDING);
    }

    @Hidden
    @GetMapping(value = "/docs", produces = "text/html;charset=UTF-8")
    public String docs() {
        return fill(DOCS);
    }

    /** Inserts the shared parts and the changing values into a page. Order matters: footer first, then its values. */
    private static String fill(String page) {
        return page
                .replace("{{STYLE}}", STYLE)
                .replace("{{FOOTER}}", FOOTER)
                .replace("{{YEAR}}", String.valueOf(Year.now().getValue()))
                .replace("{{PORTFOLIO_URL}}", PORTFOLIO_URL)
                .replace("{{SOURCE_URL}}", SOURCE_CODE_URL);
    }
}
