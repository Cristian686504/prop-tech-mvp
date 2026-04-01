package co.com.proptech.api.controller;

import co.com.proptech.api.dto.RegisterRequest;
import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = co.com.proptech.MainApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("AuthController - Register Endpoint Integration Tests")
class AuthControllerRegisterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/register - Should register user successfully with valid data")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Perez");
        request.setEmail("juan.perez.test@example.com");
        request.setPassword("Password123!");
        request.setPhone("+573001234567");
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("1234567890");
        request.setRole(UserRole.LANDLORD);

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez.test@example.com"))
                .andExpect(jsonPath("$.phone").value("+573001234567"))
                .andExpect(jsonPath("$.documentType").value("CC"))
                .andExpect(jsonPath("$.documentId").value("1234567890"))
                .andExpect(jsonPath("$.role").value("LANDLORD"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(cookie().exists("jwt"))
                .andExpect(cookie().httpOnly("jwt", true))
                .andExpect(cookie().maxAge("jwt", 86400)) // 24 hours
                .andReturn();

        // Verify JWT cookie is present
        String jwtCookie = result.getResponse().getCookie("jwt").getValue();
        assertThat(jwtCookie, notNullValue());
        assertThat(jwtCookie, not(emptyString()));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 when email already exists")
    void shouldReturn400WhenEmailAlreadyExists() throws Exception {
        // Given - First registration
        RegisterRequest firstRequest = new RegisterRequest();
        firstRequest.setName("First User");
        firstRequest.setEmail("duplicate@example.com");
        firstRequest.setPassword("Password123!");
        firstRequest.setPhone("+573001111111");
        firstRequest.setDocumentType(DocumentType.CC);
        firstRequest.setDocumentId("1111111111");
        firstRequest.setRole(UserRole.LANDLORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        // When - Second registration with same email
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setName("Second User");
        duplicateRequest.setEmail("duplicate@example.com"); // Same email
        duplicateRequest.setPassword("DifferentPass456!");
        duplicateRequest.setPhone("+573002222222");
        duplicateRequest.setDocumentType(DocumentType.CE);
        duplicateRequest.setDocumentId("2222222222");
        duplicateRequest.setRole(UserRole.TENANT);

        // Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("");  // Blank name
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setPhone("+573001234567");
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("1234567890");
        request.setRole(UserRole.LANDLORD);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 when email is invalid")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Perez");
        request.setEmail("invalid-email");  // Invalid email format
        request.setPassword("Password123!");
        request.setPhone("+573001234567");
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("1234567890");
        request.setRole(UserRole.LANDLORD);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 when password is blank")
    void shouldReturn400WhenPasswordIsBlank() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Perez");
        request.setEmail("test@example.com");
        request.setPassword("");  // Blank password
        request.setPhone("+573001234567");
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("1234567890");
        request.setRole(UserRole.LANDLORD);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should register TENANT user successfully")
    void shouldRegisterTenantUserSuccessfully() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Maria Garcia");
        request.setEmail("maria.garcia@example.com");
        request.setPassword("SecurePass456!");
        request.setPhone("+573009876543");
        request.setDocumentType(DocumentType.CE);
        request.setDocumentId("CE9876543");
        request.setRole(UserRole.TENANT);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Garcia"))
                .andExpect(jsonPath("$.email").value("maria.garcia@example.com"))
                .andExpect(jsonPath("$.role").value("TENANT"))
                .andExpect(jsonPath("$.documentType").value("CE"))
                .andExpect(cookie().exists("jwt"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should accept different document types")
    void shouldAcceptDifferentDocumentTypes() throws Exception {
        // Test CC
        RegisterRequest ccRequest = new RegisterRequest();
        ccRequest.setName("User CC");
        ccRequest.setEmail("user.cc@example.com");
        ccRequest.setPassword("Password123!");
        ccRequest.setPhone("+573001111111");
        ccRequest.setDocumentType(DocumentType.CC);
        ccRequest.setDocumentId("1111111111");
        ccRequest.setRole(UserRole.LANDLORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ccRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentType").value("CC"));

        // Test NIT
        RegisterRequest nitRequest = new RegisterRequest();
        nitRequest.setName("Company NIT");
        nitRequest.setEmail("company@example.com");
        nitRequest.setPassword("Password123!");
        nitRequest.setPhone("+573002222222");
        nitRequest.setDocumentType(DocumentType.NIT);
        nitRequest.setDocumentId("900123456-7");
        nitRequest.setRole(UserRole.LANDLORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentType").value("NIT"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should accept registration when phone is null (optional field)")
    void shouldAcceptRegistrationWhenPhoneIsNull() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Perez");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setPhone(null);  // Phone is optional
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("1234567890");
        request.setRole(UserRole.LANDLORD);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phone").isEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 when documentType is missing")
    void shouldReturn400WhenDocumentTypeIsMissing() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Perez");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setPhone("+573001234567");
        request.setDocumentType(null);  // Missing document type
        request.setDocumentId("1234567890");
        request.setRole(UserRole.LANDLORD);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 when role is missing")
    void shouldReturn400WhenRoleIsMissing() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Perez");
        request.setEmail("test@example.com");
        request.setPassword("Password123!");
        request.setPhone("+573001234567");
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("1234567890");
        request.setRole(null);  // Missing role

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
