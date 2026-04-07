package co.com.proptech.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthController")
class HealthControllerTest {

    @InjectMocks
    private HealthController controller;

    @Test
    @DisplayName("GET /api/health should return 200 OK with body 'OK'")
    void shouldReturnOk() {
        ResponseEntity<String> response = controller.health();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("OK");
    }
}
