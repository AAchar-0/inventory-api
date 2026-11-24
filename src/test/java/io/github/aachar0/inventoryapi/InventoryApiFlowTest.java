package io.github.aachar0.inventoryapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * An INTEGRATION test: the real Spring application starts (in-memory H2 database, Flyway migrations, security filters)
 * and we call it through MockMvc, which simulates HTTP without opening a network port.
 * It walks the whole story: login -> create data -> move stock -> permission checks -> validation.
 */
@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-test-secret-test-secret-1234",
        "app.seed.admin-password=Admin#Test-12345"
})
class InventoryApiFlowTest {

    private static final String ADMIN_PASSWORD = "Admin#Test-12345";

    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        // Built by hand so the security filter chain is included in every request.
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void fullInventoryFlow() throws Exception {
        String suffix = Long.toString(System.nanoTime());

        // 1. No token -> 401 Unauthorized
        mvc.perform(get("/api/products")).andExpect(status().isUnauthorized());

        // 2. Wrong password -> 401 ; right password -> token
        mvc.perform(json(post("/api/auth/login"), "{\"username\":\"admin\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
        String admin = login("admin", ADMIN_PASSWORD);

        // 3. Admin creates a category and a product with low stock (3 in stock, reorder level 5)
        String categoryBody = mvc.perform(auth(json(post("/api/categories"), "{\"name\":\"Cat-" + suffix + "\"}"), admin))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long categoryId = ((Number) JsonPath.read(categoryBody, "$.id")).longValue();

        String sku = "SKU-" + suffix;
        String productJson = "{\"sku\":\"" + sku + "\",\"name\":\"Test cable\",\"price\":6.50,"
                + "\"quantity\":3,\"reorderLevel\":5,\"categoryId\":" + categoryId + "}";
        String productBody = mvc.perform(auth(json(post("/api/products"), productJson), admin))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lowStock").value(true))
                .andReturn().getResponse().getContentAsString();
        long productId = ((Number) JsonPath.read(productBody, "$.id")).longValue();

        // 4. Duplicate SKU -> 409 Conflict
        mvc.perform(auth(json(post("/api/products"), productJson), admin)).andExpect(status().isConflict());

        // 5. The low-stock report contains it
        mvc.perform(auth(get("/api/products/low-stock"), admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sku", hasItem(sku)));

        // 6. Selling more than we have -> 409 ; receiving stock -> 201 and the new level is reported
        String movements = "/api/products/" + productId + "/movements";
        mvc.perform(auth(json(post(movements), "{\"type\":\"OUT\",\"quantity\":10}"), admin))
                .andExpect(status().isConflict());
        mvc.perform(auth(json(post(movements), "{\"type\":\"IN\",\"quantity\":20,\"note\":\"Delivery\"}"), admin))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultingQuantity").value(23))
                .andExpect(jsonPath("$.createdBy").value("admin"));

        // 7. History lists the movement
        mvc.perform(auth(get(movements), admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        // 8. A STAFF user can read and move stock, but cannot create products (403 Forbidden)
        String staffName = "staff" + suffix;
        String staffBody = mvc.perform(json(post("/api/auth/register"),
                        "{\"username\":\"" + staffName + "\",\"email\":\"" + staffName + "@example.com\","
                                + "\"password\":\"a-long-staff-password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("STAFF"))
                .andReturn().getResponse().getContentAsString();
        String staff = JsonPath.read(staffBody, "$.token");

        mvc.perform(auth(get("/api/products"), staff)).andExpect(status().isOk());
        mvc.perform(auth(json(post(movements), "{\"type\":\"OUT\",\"quantity\":1}"), staff)).andExpect(status().isCreated());
        mvc.perform(auth(json(post("/api/products"), productJson), staff)).andExpect(status().isForbidden());

        // 9. Validation: blank SKU and negative price -> 400 with a list of field errors
        mvc.perform(auth(json(post("/api/products"),
                        "{\"sku\":\"\",\"name\":\"x\",\"price\":-1,\"quantity\":0,\"reorderLevel\":0,\"categoryId\":" + categoryId + "}"),
                        admin))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.price").exists());

        // 10. Sorting by a field that is not allowed -> 400 (never reaches the database)
        mvc.perform(auth(get("/api/products?sort=passwordHash"), admin)).andExpect(status().isBadRequest());
    }

    // ---- small helpers so the test above reads like a story ----

    private String login(String username, String password) throws Exception {
        MvcResult result = mvc.perform(json(post("/api/auth/login"),
                        "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder builder, String body) {
        return builder.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    private static MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder builder, String token) {
        return builder.header("Authorization", "Bearer " + token);
    }
}
