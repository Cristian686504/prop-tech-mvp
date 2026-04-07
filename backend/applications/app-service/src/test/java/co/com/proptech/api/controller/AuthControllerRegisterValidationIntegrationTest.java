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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = co.com.proptech.MainApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("AuthController - Register Field Validation Integration Tests (TC-004 to TC-017)")
class AuthControllerRegisterValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest validRequest(String email) {
        RegisterRequest request = new RegisterRequest();
        request.setName("Juan Perez");
        request.setEmail(email);
        request.setPassword("Pass1234!");
        request.setPhone("3101234567");
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("1234567890");
        request.setRole(UserRole.LANDLORD);
        return request;
    }

    @Test
    @DisplayName("TC-004 - POST /api/auth/register - Should return 400 when name contains only whitespace")
    void tc004_shouldReturn400WhenNameIsBlank() throws Exception {
        RegisterRequest request = validRequest("tc004@email.com");
        request.setName("   ");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-005 - POST /api/auth/register - Should return 201 when name has exactly 1 character")
    void tc005_shouldReturn201WhenNameHasOneChar() throws Exception {
        RegisterRequest request = validRequest("tc005@email.com");
        request.setName("J");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("TC-006 - POST /api/auth/register - Should return 201 when name has exactly 255 characters")
    void tc006_shouldReturn201WhenNameHas255Chars() throws Exception {
        RegisterRequest request = validRequest("tc006@email.com");
        request.setName("A".repeat(255));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("TC-007 - POST /api/auth/register - Should return 400 when name exceeds 255 characters")
    void tc007_shouldReturn400WhenNameExceeds255Chars() throws Exception {
        RegisterRequest request = validRequest("tc007@email.com");
        request.setName("A".repeat(256));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-008 - POST /api/auth/register - Should return 400 when email is empty")
    void tc008_shouldReturn400WhenEmailIsEmpty() throws Exception {
        RegisterRequest request = validRequest("tc008@email.com");
        request.setEmail("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-009 - POST /api/auth/register - Should return 400 when email has no @ symbol")
    void tc009_shouldReturn400WhenEmailHasNoAtSymbol() throws Exception {
        RegisterRequest request = validRequest("valid@email.com");
        request.setEmail("juanemail.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-010 - POST /api/auth/register - Should return 400 when email has no domain")
    void tc010_shouldReturn400WhenEmailHasNoDomain() throws Exception {
        RegisterRequest request = validRequest("valid@email.com");
        request.setEmail("juan@");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-011 - POST /api/auth/register - Should return 400 when email has no domain extension")
    void tc011_shouldReturn400WhenEmailHasNoDomainExtension() throws Exception {
        RegisterRequest request = validRequest("valid@email.com");
        request.setEmail("juan@email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-013 - POST /api/auth/register - Should return 400 when phone is empty string")
    void tc013_shouldReturn400WhenPhoneIsEmpty() throws Exception {
        RegisterRequest request = validRequest("tc013@email.com");
        request.setPhone("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-014 - POST /api/auth/register - Should return 400 when phone contains letters")
    void tc014_shouldReturn400WhenPhoneContainsLetters() throws Exception {
        RegisterRequest request = validRequest("tc014@email.com");
        request.setPhone("31012345ab");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-015 - POST /api/auth/register - Should return 400 when phone has only 9 digits")
    void tc015_shouldReturn400WhenPhoneHas9Digits() throws Exception {
        RegisterRequest request = validRequest("tc015@email.com");
        request.setPhone("310123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-016 - POST /api/auth/register - Should return 201 when phone has exactly 10 digits")
    void tc016_shouldReturn201WhenPhoneHas10Digits() throws Exception {
        RegisterRequest request = validRequest("tc016@email.com");
        request.setPhone("3101234567");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("TC-017 - POST /api/auth/register - Should return 400 when phone has 11 digits")
    void tc017_shouldReturn400WhenPhoneHas11Digits() throws Exception {
        RegisterRequest request = validRequest("tc017@email.com");
        request.setPhone("31012345678");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
