package co.com.proptech.api.controller;

import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = co.com.proptech.MainApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("PropertyController - Price Validation Tests")
class PropertyControllerPriceValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UUID landlordId;

    @BeforeEach
    void setUp() {
        // Create a test landlord
        User landlord = User.builder()
                .id(UUID.randomUUID())
                .email("test-landlord@example.com")
                .passwordHash("$2a$10$encoded-password-hash")
                .name("Test Landlord")
                .role(UserRole.LANDLORD)
                .build();
        
        User savedLandlord = userRepository.save(landlord);
        landlordId = savedLandlord.getId();
    }

    @Test
    @DisplayName("POST /api/properties - Should return 400 when price is non-numeric string")
    @WithMockUser(roles = "LANDLORD")
    void shouldReturn400WhenPriceIsNonNumericString() throws Exception {
        // Given - JSON with non-numeric price
        String invalidPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description",
                    "address": "Test Address 123",
                    "price": "not-a-number",
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then - Should return 400, not 500
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest()) // Key assertion: 400 not 500
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", containsString("Invalid")));
    }

    @Test
    @DisplayName("POST /api/properties - Should return 400 when price is empty string")
    @WithMockUser(roles = "LANDLORD")
    void shouldReturn400WhenPriceIsEmptyString() throws Exception {
        // Given
        String invalidPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description",
                    "address": "Test Address 123",
                    "price": "",
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/properties - Should return 400 when price has invalid format (e.g., '12.34.56')")
    @WithMockUser(roles = "LANDLORD")
    void shouldReturn400WhenPriceHasInvalidFormat() throws Exception {
        // Given
        String invalidPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description",
                    "address": "Test Address 123",
                    "price": "12.34.56",
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/properties - Should return 400 when price is null (after @NotNull validation)")
    @WithMockUser(roles = "LANDLORD")
    void shouldReturn400WhenPriceIsNull() throws Exception {
        // Given
        String invalidPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description",
                    "address": "Test Address 123",
                    "price": null,
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.price", containsString("required")));
    }

    @Test
    @DisplayName("POST /api/properties - Should return 400 when price is zero (violates @DecimalMin)")
    @WithMockUser(roles = "LANDLORD")
    void shouldReturn400WhenPriceIsZero() throws Exception {
        // Given
        String invalidPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description",
                    "address": "Test Address 123",
                    "price": 0,
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.price", containsString("greater than zero")));
    }

    @Test
    @DisplayName("POST /api/properties - Should return 400 when price is negative")
    @WithMockUser(roles = "LANDLORD")
    void shouldReturn400WhenPriceIsNegative() throws Exception {
        // Given
        String invalidPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description",
                    "address": "Test Address 123",
                    "price": -100,
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.price", containsString("greater than zero")));
    }

    @Test
    @DisplayName("POST /api/properties - Should return 400 when JSON is malformed")
    @WithMockUser(roles = "LANDLORD")
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        // Given - Missing closing brace
        String malformedJson = """
                {
                    "title": "Test Property",
                    "description": "Test Description",
                    "address": "Test Address 123",
                    "price": 2500000
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/properties - Should accept valid numeric price")
    @WithMockUser(username = "test-landlord", roles = "LANDLORD")
    void shouldAcceptValidNumericPrice() throws Exception {
        // Given
        String validPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description with more than 20 characters to pass validation",
                    "address": "Test Address 123",
                    "price": 2500000,
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                        .requestAttr("userId", landlordId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(2500000));
    }

    @Test
    @DisplayName("POST /api/properties - Should accept decimal price")
    @WithMockUser(username = "test-landlord", roles = "LANDLORD")
    void shouldAcceptDecimalPrice() throws Exception {
        // Given
        String validPayload = """
                {
                    "title": "Test Property",
                    "description": "Test Description with more than 20 characters to pass validation",
                    "address": "Test Address 123",
                    "price": 2500000.50,
                    "imageUrls": ["http://example.com/image.jpg"]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload)
                        .requestAttr("userId", landlordId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(2500000.50));
    }
}
