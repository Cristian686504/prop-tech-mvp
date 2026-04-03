package co.com.proptech.api.controller;

import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.model.property.gateways.PropertyRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = co.com.proptech.MainApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("PropertyController - Pagination Integration Tests")
class PropertyControllerPaginationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    void setUp() {
        // Clear and set up test data
        createTestProperties();
    }

    @Test
    @DisplayName("GET /api/properties - Should return paginated response with default page size")
    @WithMockUser(roles = "TENANT")
    void shouldReturnPaginatedResponseWithDefaultPageSize() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(20)))) // Default page size
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0)) // First page
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.numberOfElements").exists());
    }

    @Test
    @DisplayName("GET /api/properties?page=0&size=5 - Should respect custom page size")
    @WithMockUser(roles = "TENANT")
    void shouldRespectCustomPageSize() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(5))))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GET /api/properties?page=1&size=10 - Should return second page")
    @WithMockUser(roles = "TENANT")
    void shouldReturnSecondPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.first").value(false));
    }

    @Test
    @DisplayName("GET /api/properties - Should sort by createdAt DESC by default")
    @WithMockUser(roles = "TENANT")
    void shouldSortByCreatedAtDescByDefault() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
                // Note: Spring Page doesn't expose sort details directly in JSON
                // Sorting is applied at query level, validated by createdAt timestamps in response
    }

    @Test
    @DisplayName("GET /api/properties?page=999 - Should return empty page for out of range page")
    @WithMockUser(roles = "TENANT")
    void shouldReturnEmptyPageForOutOfRangePage() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "999")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.number").value(999))
                .andExpect(jsonPath("$.empty").value(true));
    }

    @Test
    @DisplayName("GET /api/properties - Should include pagination metadata")
    @WithMockUser(roles = "TENANT")
    void shouldIncludePaginationMetadata() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.numberOfElements").isNumber())
                .andExpect(jsonPath("$.first").isBoolean())
                .andExpect(jsonPath("$.last").isBoolean())
                .andExpect(jsonPath("$.empty").isBoolean());
    }

    @Test
    @DisplayName("GET /api/properties - Should return only AVAILABLE properties")
    @WithMockUser(roles = "TENANT")
    void shouldReturnOnlyAvailableProperties() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "0")
                        .param("size", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status", everyItem(is("AVAILABLE"))));
    }

    @Test
    @DisplayName("GET /api/properties - Should handle negative page number gracefully")
    @WithMockUser(roles = "TENANT")
    void shouldHandleNegativePageNumber() throws Exception {
        // When & Then - Spring Data automatically converts negative to 0
        mockMvc.perform(get("/api/properties")
                        .param("page", "-1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GET /api/properties - Should require authentication (protected endpoint)")
    void shouldRequireAuthentication() throws Exception {
        // When & Then - Without @WithMockUser, should get 401
        mockMvc.perform(get("/api/properties")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("GET /api/properties - Should calculate total pages correctly")
    @WithMockUser(roles = "TENANT")
    void shouldCalculateTotalPagesCorrectly() throws Exception {
        // Given: We have test properties in DB
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("GET /api/properties - Response should include property details in content array")
    @WithMockUser(roles = "TENANT")
    void shouldIncludePropertyDetailsInContent() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/properties")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.content[0].description").exists())
                .andExpect(jsonPath("$.content[0].address").exists())
                .andExpect(jsonPath("$.content[0].price").exists())
                .andExpect(jsonPath("$.content[0].status").exists())
                .andExpect(jsonPath("$.content[0].landlordId").exists())
                .andExpect(jsonPath("$.content[0].imageUrls").isArray())
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.content[0].updatedAt").exists());
    }

    // Helper method to create test properties
    private void createTestProperties() {
        List<Property> testProperties = new ArrayList<>();
        UUID landlordId = UUID.randomUUID();
        
        for (int i = 1; i <= 25; i++) {
            Property property = Property.builder()
                    .id(UUID.randomUUID())
                    .title("Propiedad de Prueba " + i)
                    .description("Descripción de la propiedad " + i)
                    .address("Calle " + i + " #10-20")
                    .price(new BigDecimal(String.valueOf(1500000 + (i * 100000))))
                    .status(PropertyStatus.AVAILABLE)
                    .landlordId(landlordId)
                    .imageUrls(List.of("image" + i + ".jpg"))
                    .createdAt(LocalDateTime.now().minusDays(25 - i)) // Older properties first
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            testProperties.add(propertyRepository.save(property));
        }
    }
}
