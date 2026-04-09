package co.com.proptech.model.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PageResponse Unit Tests")
class PageResponseTest {

    @Test
    @DisplayName("Should compute totalPages correctly for exact multiples")
    void shouldComputeTotalPagesForExactMultiple() {
        PageResponse<String> page = new PageResponse<>(List.of("a", "b"), 0, 2, 6L);
        assertEquals(3, page.getTotalPages());
    }

    @Test
    @DisplayName("Should compute totalPages correctly for non-exact multiples (ceiling)")
    void shouldRoundUpTotalPages() {
        PageResponse<String> page = new PageResponse<>(List.of("a"), 0, 3, 7L);
        assertEquals(3, page.getTotalPages()); // ceil(7/3) = 3
    }

    @Test
    @DisplayName("Should return zero total pages when totalElements is 0")
    void shouldReturnZeroTotalPagesWhenEmpty() {
        PageResponse<String> page = new PageResponse<>(List.of(), 0, 20, 0L);
        assertEquals(0, page.getTotalPages());
    }

    @Test
    @DisplayName("hasNext - should return true when not on last page")
    void hasNextTrueWhenNotLastPage() {
        PageResponse<String> page = new PageResponse<>(List.of("a"), 0, 1, 3L); // pages: 0,1,2
        assertTrue(page.hasNext());
    }

    @Test
    @DisplayName("hasNext - should return false when on last page")
    void hasNextFalseWhenLastPage() {
        PageResponse<String> page = new PageResponse<>(List.of("c"), 2, 1, 3L); // last page
        assertFalse(page.hasNext());
    }

    @Test
    @DisplayName("hasPrevious - should return true when page > 0")
    void hasPreviousTrueWhenNotFirstPage() {
        PageResponse<String> page = new PageResponse<>(List.of("b"), 1, 1, 3L);
        assertTrue(page.hasPrevious());
    }

    @Test
    @DisplayName("hasPrevious - should return false when on first page")
    void hasPreviousFalseWhenFirstPage() {
        PageResponse<String> page = new PageResponse<>(List.of("a"), 0, 1, 3L);
        assertFalse(page.hasPrevious());
    }

    @Test
    @DisplayName("isFirst - should return true when page is 0")
    void isFirstTrueWhenPageIsZero() {
        PageResponse<String> page = new PageResponse<>(List.of("a"), 0, 10, 30L);
        assertTrue(page.isFirst());
    }

    @Test
    @DisplayName("isFirst - should return false when page > 0")
    void isFirstFalseWhenNotFirstPage() {
        PageResponse<String> page = new PageResponse<>(List.of("b"), 1, 10, 30L);
        assertFalse(page.isFirst());
    }

    @Test
    @DisplayName("isLast - should return true when on last page")
    void isLastTrueOnLastPage() {
        PageResponse<String> page = new PageResponse<>(List.of("c"), 2, 1, 3L);
        assertTrue(page.isLast());
    }

    @Test
    @DisplayName("isLast - should return false when not on last page")
    void isLastFalseWhenNotOnLastPage() {
        PageResponse<String> page = new PageResponse<>(List.of("a"), 0, 1, 3L);
        assertFalse(page.isLast());
    }

    @Test
    @DisplayName("Should expose all getter values correctly")
    void shouldExposeGetters() {
        List<String> content = List.of("x", "y");
        PageResponse<String> page = new PageResponse<>(content, 1, 5, 12L);
        assertSame(content, page.getContent());
        assertEquals(1, page.getPage());
        assertEquals(5, page.getSize());
        assertEquals(12L, page.getTotalElements());
    }
}
