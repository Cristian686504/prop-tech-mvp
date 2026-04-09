package co.com.proptech.config.security;

import co.com.proptech.model.user.gateways.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Unit Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should continue filter chain without auth when request has no cookies")
    void shouldContinueChainWhenNoCookies() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should continue filter chain without auth when 'jwt' cookie is absent")
    void shouldContinueChainWhenJwtCookieAbsent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("session", "abc123"), new Cookie("lang", "es"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should set SecurityContext authentication when JWT cookie is present and valid")
    void shouldSetAuthenticationForValidJwt() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = "valid.jwt.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", token));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractEmail(token)).thenReturn("user@email.com");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userId, request.getAttribute("userId"));
    }

    @Test
    @DisplayName("Should continue filter chain without auth when JWT token is invalid")
    void shouldContinueChainWhenJwtInvalid() throws Exception {
        String token = "invalid.jwt.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", token));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken(token)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should continue filter chain when JWT cookie sits among other cookies")
    void shouldFindJwtCookieAmongMultipleCookies() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = "real.jwt.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("tracking", "xyz"),
                new Cookie("jwt", token),
                new Cookie("lang", "es")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractEmail(token)).thenReturn("user@email.com");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(userId, request.getAttribute("userId"));
    }
}
